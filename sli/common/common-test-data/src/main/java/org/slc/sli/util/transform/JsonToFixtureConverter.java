/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.util.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * 
 * @author dwilliams
 * 
 */
public class JsonToFixtureConverter {
    private static final Logger LOG = LoggerFactory.getLogger(JsonToFixtureConverter.class);

    private String dataDirectory = null;
    private FilenameFilter edOrgFilter = null;
    private FilenameFilter schoolEdOrgAssocFilter = null;
    private FilenameFilter schoolFilter = null;
    private FilenameFilter edOrgAssocFilter = null;
    private FilenameFilter assessmentFilter = null;
    private FilenameFilter studentAssessmentFilter = null;
    private FilenameFilter studentFilter = null;
    private FilenameFilter studentProgAssocFilter = null;
    
    public JsonToFixtureConverter(String dir, String outputType) {
        dataDirectory = dir;
        
        if (outputType.equals("xml")) {
            Configuration.setOutputType(Configuration.OutputType.EdFiXml);
        } else {
            Configuration.setOutputType(Configuration.OutputType.Fixture);
        }
        
        assessmentFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "assessment_meta_data.json".equals(name);
            }
        };
        studentAssessmentFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "assessment.json".equals(name);
            }
        };
        edOrgAssocFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "educational_organization_association.json".equals(name);
            }
        };
        edOrgFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "educational_organization.json".equals(name);
            }
        };
        schoolEdOrgAssocFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "school_educational_organization_association.json".equals(name);
            }
        };
        schoolFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "school.json".equals(name);
            }
        };
        studentProgAssocFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "student_program_association.json".equals(name);
            }
        };
        studentFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "student.json".equals(name);
            }
        };
    }
    
    public void convert() {
        // recursively load and parse all the JSON documents, store the entities in memory
        File dir = new File(dataDirectory);
        if (!dir.isDirectory()) {
            LOG.error("ERROR: '" + dataDirectory + "' is not a directory");
            return;
        }
        
        loadAndStore(dir);
        writeFixtureFiles();
    }
    
    private void writeFixtureFiles() {
        writeEdOrgs();
        writeEdOrgEdOrgAssociations();
        writeCourses();
        writeSchools();
        writeEdOrgSchoolAssociations();
        writeSessions();
        writeSchoolSessionAssociations();
        writeCourseSessionAssociations();
        writeTeachers();
        writeAssessments();
        writeSections();
        writeTeacherSectionAssociations();
        writeCourseSectionAssociations();
        writeStudents();
        writeStudentAssessments();
    }
    
    private void writeFixture(String fileName, Iterator<?> iter) {
        writeFixture(fileName, iter, false, null, null);
    }
    
    private void writeFixture(String fileName, Iterator<?> iter, boolean append, String header, String footer) {
        OutputStreamWriter osw = open(fileName, append);
        
        try {
            if (header != null) {
                osw.write(header);
            }
            while (iter.hasNext()) {
                MongoDataEmitter emitter = (MongoDataEmitter) iter.next();
                String s = emitter.emit();
                osw.write(s);
            }
            if (footer != null) {
                osw.write(footer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close(osw);
    }
    
    private void writeEdOrgs() {
        HashMap<String, EducationalOrganization> map = DataManager.getEdOrgs();
        if (map != null) {
            Iterator<EducationalOrganization> iter = map.values().iterator();
            if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
                writeFixture("edOrg_fixture.json", iter);
            } else {
                String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">\n";
                writeFixture("InterchangeSchool.xml", iter, false, header, "");
            }
        }
    }
    
    private void writeSchools() {
        HashMap<String, School> map = DataManager.getSchools();
        if (map != null) {
            Iterator<School> iter = map.values().iterator();
            if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
                writeFixture("school_fixture.json", iter);
            } else {
                String footer = "</InterchangeEducationOrganization>\n";
                writeFixture("InterchangeSchool.xml", iter, true, null, footer);
            }
        }
    }
    
    private void writeSessions() {
        HashMap<String, Session> map = DataManager.getSessions();
        if (map != null) {
            Iterator<Session> iter = map.values().iterator();
            writeFixture("session_fixture.json", iter);
        }
    }
    
    private void writeCourses() {
        HashMap<String, Course> map = DataManager.getCourses();
        if (map != null) {
            Iterator<Course> iter = map.values().iterator();
            if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
                writeFixture("course_fixture.json", iter);
            } else {
                writeFixture("InterchangeSchool.xml", iter, true, null, null);
            }
        }
    }
    
    private void writeTeachers() {
        HashMap<String, Teacher> map = DataManager.getTeachers();
        if (map != null) {
            Iterator<Teacher> iter = map.values().iterator();
            if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
                writeFixture("teacher_fixture.json", iter);
            } else {
                String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">\n";
                String footer = "</InterchangeStaffAssociation>\n";
                writeFixture("InterchangeStaffAssociation.xml", iter, false, header, footer);
            }
        }
    }
    
    private void writeAssessments() {
        HashMap<String, Assessment> map = DataManager.getAssessments();
        if (map != null) {
            Iterator<Assessment> iter = map.values().iterator();
            writeFixture("assessment_fixture.json", iter);
        }
    }
    
    private void writeSections() {
        HashMap<String, Section> map = DataManager.getSections();
        if (map != null) {
            Iterator<Section> iter = map.values().iterator();
            writeFixture("section_fixture.json", iter);
        }
    }
    
    private void writeStudents() {
        HashMap<String, Student> map = DataManager.getStudents();
        if (map != null) {
            Iterator<Student> iter = map.values().iterator();
            if (Configuration.getOutputType().equals(Configuration.OutputType.Fixture)) {
                writeFixture("student_fixture.json", iter);
            } else {
                String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeStudent xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-Student.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">\n";
                String footer = "</InterchangeStudent>\n";
                writeFixture("InterchangeStudent.xml", iter, false, header, footer);
            }
        }
    }
    
    private void writeStudentAssessments() {
        HashMap<String, StudentAssessment> map = DataManager.getStudentAssessments();
        if (map != null) {
            Iterator<StudentAssessment> iter = map.values().iterator();
            writeFixture("student_assessment_fixture.json", iter);
        }
    }
    
    private void writeCourseSectionAssociations() {
        OutputStreamWriter osw = open("course_section_association_fixture.json", false);
        
        HashMap<String, Course> map = DataManager.getCourses();
        if (map != null) {
            Iterator<Course> iter = map.values().iterator();
            while (iter.hasNext()) {
                Course course = iter.next();
                String s = course.emitSectionAssociations();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private void writeTeacherSectionAssociations() {
        OutputStreamWriter osw = open("teacher_section_association_fixture.json", false);
        
        HashMap<String, Teacher> map = DataManager.getTeachers();
        if (map != null) {
            Iterator<Teacher> iter = map.values().iterator();
            while (iter.hasNext()) {
                Teacher teacher = iter.next();
                String s = teacher.emitSectionAssociation();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private void writeCourseSessionAssociations() {
        OutputStreamWriter osw = open("course_session_association_fixture.json", false);
        
        HashMap<String, Course> map = DataManager.getCourses();
        if (map != null) {
            Iterator<Course> iter = map.values().iterator();
            while (iter.hasNext()) {
                Course course = iter.next();
                String s = course.emitSessionAssociations();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private void writeSchoolSessionAssociations() {
        OutputStreamWriter osw = open("school_session_association_fixture.json", false);
        
        HashMap<String, School> map = DataManager.getSchools();
        if (map != null) {
            Iterator<School> iter = map.values().iterator();
            while (iter.hasNext()) {
                School school = iter.next();
                String s = school.emitSessionAssociations();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private void writeEdOrgSchoolAssociations() {
        OutputStreamWriter osw = open("edOrg_school_association_fixture.json", false);
        
        HashMap<String, School> map = DataManager.getSchools();
        if (map != null) {
            Iterator<School> iter = map.values().iterator();
            while (iter.hasNext()) {
                School school = iter.next();
                String s = school.emitEdOrgAssociation();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private void writeEdOrgEdOrgAssociations() {
        OutputStreamWriter osw = open("edOrg_edOrg_association_fixture.json", false);
        
        HashMap<String, EducationalOrganization> map = DataManager.getEdOrgs();
        if (map != null) {
            Iterator<EducationalOrganization> iter = map.values().iterator();
            while (iter.hasNext()) {
                EducationalOrganization edOrg = iter.next();
                String s = edOrg.emitParentEdOrgAssociation();
                try {
                    osw.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close(osw);
    }
    
    private OutputStreamWriter open(String filename, boolean append) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = new FileOutputStream(dataDirectory + filename, append);
            osw = new OutputStreamWriter(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return osw;
    }
    
    private void close(OutputStreamWriter osw) {
        if (osw != null) {
            try {
                osw.flush();
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadAndStore(File file) {
        if (file.isDirectory()) {
            // we have to load the data in the right order
            
            // EdOrgs
            File[] children = file.listFiles(edOrgFilter);
            for (File child : children) {
                loadAndStoreEdOrgs(loadJson(child)); // should be only one
            }
            
            // EdOrg-EdOrg associations
            children = file.listFiles(edOrgAssocFilter);
            for (File child : children) {
                loadAndStoreEdOrgEdOrgAssociations(loadJson(child)); // should be only one
            }
            
            // assessments
            children = file.listFiles(assessmentFilter);
            for (File child : children) {
                loadAndStoreAssessments(loadJson(child)); // should be only one
            }
            
            // students - we'll have to reach back and insert school/section/teacher data later
            children = file.listFiles(studentFilter);
            for (File child : children) {
                loadAndStoreStudents(loadJson(child)); // should be only one
            }
            
            // student assessments
            children = file.listFiles(studentAssessmentFilter);
            for (File child : children) {
                loadAndStoreStudentAssessments(loadJson(child)); // should be only one
            }
            
            // student program participants
            children = file.listFiles(studentProgAssocFilter);
            for (File child : children) {
                loadAndStoreProgramParticipants(loadJson(child)); // should be only one
            }
            
            // schools
            children = file.listFiles(schoolFilter);
            for (File child : children) {
                loadAndStoreSchools(loadJson(child)); // should be only one
            }
            
            // school-EdOrg associations
            children = file.listFiles(schoolEdOrgAssocFilter);
            for (File child : children) {
                loadAndStoreSchoolEdOrgAssociations(loadJson(child)); // should be only one
            }
            
            children = file.listFiles(); // look for subdirectories
            for (File child : children) {
                if (child.isDirectory()) {
                    loadAndStore(child);
                }
            }
        }
    }
    
    private void loadAndStoreEdOrgs(JsonElement root) {
        if (root.isJsonArray()) {
            HashMap<String, EducationalOrganization> edOrgs = new HashMap<String, EducationalOrganization>();
            JsonArray edOrgsJson = root.getAsJsonArray();
            for (int i = 0; i < edOrgsJson.size(); i++) {
                JsonObject edOrgJson = edOrgsJson.get(i).getAsJsonObject();
                String id = edOrgJson.get("id").getAsString();
                
                String type = "";
                JsonArray types = edOrgJson.get("organizationCategories").getAsJsonArray();
                if (types != null && types.size() > 0) {
                    type = types.get(0).getAsString(); // one is enough
                }
                
                String street = "", suite = "", city = "", state = "", postalCode = "";
                JsonArray addresses = edOrgJson.get("address").getAsJsonArray();
                if (addresses != null && addresses.size() > 0) {
                    JsonObject address = addresses.get(0).getAsJsonObject();
                    street = getString(address, "streetNumberName", "");
                    suite = getString(address, "apartmentRoomSuiteNumber", "");
                    city = getString(address, "city", "");
                    state = getString(address, "stateAbbreviation", "");
                    postalCode = getString(address, "postalCode", "");
                }
                String stateOrgId = getString(edOrgJson, "stateOrganizationId", "");
                String edOrgName = getString(edOrgJson, "nameOfInstitution", "");
                
                EducationalOrganization edOrg = new EducationalOrganization(edOrgName, street, suite, city, state,
                        postalCode, stateOrgId);
                edOrg.setType(type);
                edOrgs.put(id, edOrg);
            }
            DataManager.addEdOrgs(edOrgs);
        }
    }
    
    private void loadAndStoreEdOrgEdOrgAssociations(JsonElement root) {
        if (root.isJsonArray()) {
            JsonArray edOrgAssociations = root.getAsJsonArray();
            for (int i = 0; i < edOrgAssociations.size(); i++) {
                JsonObject edOrgAssocJson = edOrgAssociations.get(i).getAsJsonObject();
                String parentId = edOrgAssocJson.get("educationOrganizationParentId").getAsString();
                String childId = edOrgAssocJson.get("educationOrganizationChildId").getAsString();
                
                EducationalOrganization parent = DataManager.getEdOrg(parentId);
                EducationalOrganization child = DataManager.getEdOrg(childId);
                child.setParentEdOrg(parent);
            }
        }
    }
    
    private void loadAndStoreSchoolEdOrgAssociations(JsonElement root) {
        if (root.isJsonArray()) {
            JsonArray edOrgAssociations = root.getAsJsonArray();
            for (int i = 0; i < edOrgAssociations.size(); i++) {
                JsonObject edOrgAssocJson = edOrgAssociations.get(i).getAsJsonObject();
                String schoolId = edOrgAssocJson.get("schoolId").getAsString();
                String educationOrganizationId = edOrgAssocJson.get("educationOrganizationId").getAsString();
                
                EducationalOrganization edOrg = DataManager.getEdOrg(educationOrganizationId);
                School school = DataManager.getSchool(schoolId);
                school.setEdOrg(edOrg);
            }
        }
    }
    
    private void loadAndStoreSchools(JsonElement root) {
        if (root.isJsonArray()) {
            HashMap<String, School> schools = new HashMap<String, School>();
            HashMap<String, Course> courses = new HashMap<String, Course>();
            HashMap<String, Section> sections = new HashMap<String, Section>();
            
            Session fakeSession = new Session("Year_2011_2012", "Spring_Semester", "20122-01-01", "2011-06-31", 90);
            
            JsonArray schoolsJson = root.getAsJsonArray();
            for (int i = 0; schoolsJson != null && i < schoolsJson.size(); i++) {
                JsonObject schoolJson = schoolsJson.get(i).getAsJsonObject();
                String schoolId = getString(schoolJson, "id", getString(schoolJson, "schoolId", ""));
                String name = getString(schoolJson, "nameOfInstitution", schoolId);
                
                if (schoolId.length() == 0) {
                    schoolId = name; // hack to handle inconsistent dashboard mock data
                }
                
                School school = new School(name, schoolId, "", "", "", "", "", "", "K-12", "Elementary_School");
                school.createSessionAssociation(fakeSession, "Spring 2012", "2012-01-01", "2012-06-31", 90);
                schools.put(schoolId, school);
                
                JsonArray coursesJson = schoolJson.get("courses").getAsJsonArray();
                for (int k = 0; coursesJson != null && k < coursesJson.size(); k++) {
                    JsonObject courseJson = coursesJson.get(k).getAsJsonObject();
                    String courseName = getString(courseJson, "course", null);
                    
                    String courseDefinedBy = "DoE";
                    String adoptionDate = "2000-01-01";
                    String description = courseName;
                    String characteristics = "AP";
                    boolean required = false;
                    String code = courseName;
                    String gpaAppl = "Normal";
                    String idSystem = "BigSystem";
                    int minCredit = 1;
                    int maxCredit = 1;
                    String subject = courseName;
                    String level = "Basic";
                    int parts = 1;
                    String orgCode = code;
                    String careerPath = "Carnival Barker";
                    
                    Course course = new Course(courseDefinedBy, adoptionDate, description, characteristics, required,
                            code, gpaAppl, idSystem, minCredit, maxCredit);
                    course.setAdditionalParametersThatExceedFascistCheckstyleLimitOnConstructorArgs(subject, level,
                            courseName, parts, orgCode, careerPath);
                    course.createSessionAssociation(fakeSession, code, courseName);
                    courses.put(courseName, course);
                    
                    JsonElement sectionsJsonEl = courseJson.get("sections");
                    if (sectionsJsonEl != null) {
                        JsonArray sectionsJson = sectionsJsonEl.getAsJsonArray();
                        for (int m = 0; sectionsJson != null && m < sectionsJson.size(); m++) {
                            JsonObject sectionJson = sectionsJson.get(m).getAsJsonObject();
                            String sectionName = getString(sectionJson, "sectionName", null);
                            String sectionId = sectionName;
                            String sectionSequence = String.valueOf(m);
                            Section section = new Section(sectionId, sectionName, sectionSequence);
                            sections.put(sectionId, section);
                            course.createSectionAssociation(section);
                            
                            JsonElement studentIdsEl = sectionJson.get("studentUIDs");
                            if (studentIdsEl != null) {
                                JsonArray studentIds = studentIdsEl.getAsJsonArray();
                                for (int n = 0; studentIds != null && n < studentIds.size(); n++) {
                                    String studentId = studentIds.get(n).getAsString();
                                    Student student = DataManager.getStudent(studentId);
                                    
                                    student.addSection(section);
                                    student.setSchool(school);
                                }
                            }
                        }
                    }
                }
                
            }
            DataManager.addSchools(schools);
            DataManager.addCourses(courses);
            DataManager.addSections(sections);
        }
    }
    
    private void loadAndStoreAssessments(JsonElement root) {
        /*
         * if(root.isJsonArray()){
         * HashMap<String, Assessment> assessments = new HashMap<String, Assessment>();
         * JsonArray assessmentsJson = root.getAsJsonArray();
         * for(int i = 0; i < assessmentsJson.size(); i++){
         * JsonObject assessmentJson = assessmentsJson.get(i).getAsJsonObject();
         * String name = getString(assessmentJson, "name", "");
         * String period = assessmentJson.get("period").getAsString();
         * 
         * AssessmentCode code = new AssessmentCode("State", "ALL");
         * ArrayList<AssessmentCode> codes = new ArrayList<AssessmentCode>();
         * codes.add(code);
         * 
         * String subject = "Other", category = "Achievement test", gradeLevel = "Ungraded",
         * standard = "LEA_STANDARD", version = "1";
         * 
         * AssessmentBody body = new AssessmentBody(name, codes, subject, category, gradeLevel,
         * standard, version);
         * Assessment assessment = new Assessment(body, name);
         * 
         * JsonArray periods = assessmentJson.get("periods").getAsJsonArray();
         * for(int k = 0; periods != null && k < periods.size(); k++){
         * JsonObject per = periods.get(k).getAsJsonObject();
         * String periodName = per.get("name").getAsString();
         * String periodStart = per.get("windowStart").getAsString();
         * String periodEnd = per.get("windowEnd").getAsString();
         * assessment.addAbstractPeriod(periodName, periodStart, periodEnd);
         * }
         * assessment.setPeriod(period);
         * assessments.put(name, assessment);
         * 
         * JsonArray children = assessmentJson.get("children").getAsJsonArray();
         * for(int k = 0; children != null && k < children.size(); k++){
         * JsonObject child = children.get(k).getAsJsonObject();
         * String childName = child.get("name").getAsString();
         * Assessment childAssessment = assessment.copyWithName(childName);
         * assessments.put(childName, childAssessment);
         * }
         * }
         * DataManager.addAssessments(assessments);
         * }
         */
    }
    
    private void loadAndStoreStudentAssessments(JsonElement root) {
        /*
         * if (root.isJsonArray()) {
         * HashMap<String, StudentAssessment> assessments = new HashMap<String,
         * StudentAssessment>();
         * 
         * // TODO - parse
         * }
         */
    }
    
    private String getString(JsonObject obj, String attr, String defValue) {
        JsonElement el = obj.get(attr);
        if (el == null) {
            return defValue;
        }
        return el.getAsString();
        
    }
    
    private void loadAndStoreStudents(JsonElement root) {
        if (root.isJsonArray()) {
            HashMap<String, Student> students = new HashMap<String, Student>();
            JsonArray studentsJson = root.getAsJsonArray();
            for (int i = 0; i < studentsJson.size(); i++) {
                JsonObject studentJson = studentsJson.get(i).getAsJsonObject();
                
                String uid = getString(
                        studentJson,
                        "uid",
                        getString(
                                studentJson,
                                "studentUniqueStateId",
                                getString(studentJson, "stateId", getString(studentJson, "id", Base64.nextUuid("aaba")))));
                String suffix = getString(studentJson, "nameSuffix", "");
                String stateId = getString(studentJson, "stateId", uid);
                String sex = getString(studentJson, "sex", "Male");
                String birthDate = getString(studentJson, "birthDate", "");
                String firstName = getString(studentJson, "firstName", "");
                String econDis = getString(studentJson, "economicDisadvantaged", "false");
                String nameVerification = getString(studentJson, "nameVerification", "");
                String lep = getString(studentJson, "limitedEnglishProficiency", "NotLimited");
                String sfse = getString(studentJson, "schoolFoodServiceEligibility", "Full price");
                boolean hispanicLatinoEthnicity = Boolean.parseBoolean(getString(studentJson,
                        "hispanicLatinoEthnicity", "false"));
                String middleName = getString(studentJson, "middleName", "");
                String racialCategory = getString(studentJson, "racialCategory", "");
                String lastName = getString(studentJson, "lastName", "");
                
                // more inconsistent data - names may be attributes or sub-objects
                JsonElement nameJson = studentJson.get("name");
                if (nameJson != null) {
                    JsonObject name = nameJson.getAsJsonObject();
                    firstName = getString(name, "firstName", "");
                    middleName = getString(name, "middleName", "");
                    lastName = getString(name, "lastSurname", "");
                }
                JsonElement birthDataJson = studentJson.get("birthData");
                if (birthDataJson != null) {
                    JsonObject birthData = birthDataJson.getAsJsonObject();
                    birthDate = getString(birthData, "birthDate", "");
                }
                
                StudentName sName = new StudentName(firstName, middleName, lastName);
                sName.setSuffix(suffix);
                sName.setNameVerification(nameVerification);
                BirthData bd = new BirthData(birthDate);
                
                Student student = new Student(sName, bd, stateId, sex, econDis, null, "", sfse, "", lep, "", "", "", "");
                if (isInUuidForm(uid)) {
                    student.setUuid(uid);
                }
                student.setHispanicLatinoEthnicity(hispanicLatinoEthnicity);
                student.setRacialCategory(racialCategory);
                
                students.put(uid, student);
            }
            DataManager.addStudents(students);
        }
    }
    
    private boolean isInUuidForm(String test) {
        // quick and dirty test
        if (test == null) {
            return false;
        }
        // should be like 67ce204b-9999-4a11-aaac-000000000008
        if (test.length() != 36) {
            return false;
        }
        return (test.charAt(8) == '-' && test.charAt(13) == '-' && test.charAt(18) == '-' && test.charAt(23) == '-');
    }
    
    private void loadAndStoreProgramParticipants(JsonElement root) {
        if (root.isJsonArray()) {
            JsonArray ppsJson = root.getAsJsonArray();
            for (int i = 0; i < ppsJson.size(); i++) {
                JsonObject ppJson = ppsJson.get(i).getAsJsonObject();
                String studentId = ppJson.get("studentId").getAsString();
                JsonArray programs = ppJson.get("programs").getAsJsonArray();
                
                Student student = DataManager.getStudent(studentId);
                for (int k = 0; student != null && k < programs.size(); k++) {
                    String prog = programs.get(k).getAsString();
                    student.addProgram(prog);
                }
            }
        }
    }
    
    
    
    /*
     * private void convertStudent(OutputStreamWriter writer) throws IOException
     * {
     * JsonElement root = loadJson();
     * if(root.isJsonArray()){
     * JsonArray students = root.getAsJsonArray();
     * for(int i = 0; i < students.size(); i++){
     * JsonObject studentJson = (JsonObject)students.get(i);
     * String stateId = studentJson.get("studentUniqueStateId").getAsString();
     * JsonObject name = studentJson.get("name").getAsJsonObject();
     * boolean econDis = studentJson.get("economicDisadvantaged").getAsBoolean();
     * JsonObject birthData = studentJson.get("birthData").getAsJsonObject();
     * String sex = studentJson.get("sex").getAsString();
     * String lep = studentJson.get("limitedEnglishProficiency").getAsString();
     * String sfse = studentJson.get("schoolFoodServiceEligibility").getAsString();
     * String id = studentJson.get("id").getAsString();
     * 
     * String firstName = name.get("firstName").getAsString();
     * String middleName = name.get("middleName").getAsString();
     * String lastName = name.get("lastSurname").getAsString();
     * 
     * String birthDate = birthData.get("birthDate").getAsString();
     * 
     * //ID,First Name, Last
     * Name,School,Section,Course,Teacher,Grade,economicDisadvantaged,schoolFoodServicesEligibility
     * ,studentCharacteristics,
     * //limitedEnglishProficiency,section504Disabilities,programParticipations
     * StringBuffer line = new StringBuffer();
     * line.append(id).append(",")
     * .append(firstName).append(",")
     * .append(lastName).append(",")
     * .append(school).append(",")
     * .append(section).append(",")
     * .append(course).append(",")
     * .append(teacher).append(",")
     * .append(grade).append(",")
     * .append(economicDisadvantaged).append(",")
     * .append(schoolFoodServicesEligibility).append(",")
     * .append(studentCharacteristics).append(",")
     * .append(limitedEnglishProficiency).append(",")
     * .append(section504Disabilities).append(",")
     * append(programParticipations)
     * ;
     * 
     * writer.write(line.toString());
     * }
     * }
     * }
     */
    
    private JsonElement loadJson(File file) {
        JsonParser parser = new JsonParser();
        JsonElement root = null;
        try {
            root = parser.parse(new JsonReader(new FileReader(file)));
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return root;
    }
    
    private static void usage() {
        String usage = "Usage:\n\n";
        usage += "java " + JsonToFixtureConverter.class.getSimpleName() + " [-u | [<directory> [-x | -j]]]\n\n";
        usage += "java " + JsonToFixtureConverter.class.getSimpleName() + " -u\n";
        usage += "    - print this usage message\n\n";
        usage += "java " + JsonToFixtureConverter.class.getSimpleName() + " <directory>\n";
        usage += "    - recursively walk over the named directory, looking for specifically named .json files.\n";
        usage += "      The file names have been taken from the dashboard mock data process. Convert the mock\n";
        usage += "      data to fixture file (JSON) format.\n\n";
        usage += "java " + JsonToFixtureConverter.class.getSimpleName() + " <directory> -j\n";
        usage += "    - same as above\n\n";
        usage += "java " + JsonToFixtureConverter.class.getSimpleName() + " <directory> -x\n";
        usage += "    - same as above, but convert the mock data to Ed-Fi XML ingestible format.\n\n";
        LOG.info(usage);
        System.exit(0);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            usage();
        }
        
        if (args[0].equals("-u")) {
            usage();
        }
        
        String dataDir = (args.length < 1) ? "." : args[0];
        if (!dataDir.endsWith(File.separator)) {
            dataDir += File.separator;
        }
        
        String outputType = "json";
        String nextArg = (args.length < 2) ? "" : args[1];
        if (nextArg.equals("-x")) {
            outputType = "xml";
        }
        
        if (!dataDir.endsWith(File.separator)) {
            dataDir += File.separator;
        }
        
        JsonToFixtureConverter converter = new JsonToFixtureConverter(dataDir, outputType);
        converter.convert();
    }
}
