package org.slc.sli.util.transform;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * The purpose of this class is to transform a CSV input file into Mongo-importable form. The CSV
 * file must be of a particular format
 * and must contain several data sections in a particular order. There is no attempt to generalize
 * this code into recognizing
 * variable data - it is not an ingestor. It is simply a means of transforming test data into a
 * usable form. The expected format is:
 * 
 * ===ED-ORGS===
 * id,edOrgName,street,suite,city,state,postalCode,stateOrgId
 * 1,Greater Smallville K-12 School District,123 District Office Way,200,NY,NY,,KS-SMALLVILLE
 * ===SCHOOLS===
 * ID,Name,street,city,state,postal code,telephone,grades,categories
 * 1,Small Fry Elementary School,111 Main St.,Smallville,NC,27500,919-191-9191,K-5,Elementary_School
 * <== many rows of data
 * ===ED-ORG SCHOOLS===
 * edOrg,school
 * 1,6
 * ===TEACHERS===
 * staffUniqueStateId,firstName,lastSurname,sex,highestLevelOfEducationCompleted
 * 8888001,Waldo,Washington,Male,Masters <== many rows of data
 * ===SECTIONS===
 * uniqueSectionCode,sectionName,sequenceOfCourse
 * math-8-6-1,8th grade math,1 <== many rows of data
 * ===TEACHER SECTIONS===
 * staffUniqueStateId,uniqueSectionCode
 * 8888031,math-8-6-1 <== many rows of data
 * ===STUDENTS===
 * ID,First Name, Last Name,School,Section,Course,Teacher,Grade
 * 1,James,Smith,1,,,Washington,K <== many rows of data
 * ===ASSESSMENTS===
 * Assessment ID,title,subject,category,grade level,content standard,version,school
 * 1,Mathematics Achievement Assessment Test,Mathematics,EOG,8,LEA_STANDARD,2010-2011,6 <== many
 * rows of data
 * ===STUDENT ASSESSMENTS===
 * Student ID, Performance Level,State,Family, Year,Subject,Grade Level,Assessment ID
 * 862,3,NC,EOG,2010-2011,Math,8,1 <== many rows of data
 * ===SECTION ASSESSMENTS===
 * #Assessment ID,section
 * 1,math-8-6-1 <== many rows of data
 * 
 * 
 * @author Dwayne Williams <dwilliams@wgen.net>
 * 
 */
public class StudentAssessmentParser {
    // TODO: don't you dare check in without fixing this constant!!
    private static final String AvroPath = "/Users/dwilliams/git/SLI/sli/common/domain/src/main/resources/avroSchema/";
    
    private String dataFile = null;
    private String dataDirectory = null;
    
    public StudentAssessmentParser(String dataFile, String dataDir, String outputType) {
        this.dataFile = dataFile;
        this.dataDirectory = dataDir;
        
        if (outputType.equals("xml")) {
            Configuration.outputType = Configuration.OutputType.EdFiXml;
        } else {
            Configuration.outputType = Configuration.OutputType.Fixture;
        }
    }
    
    public void transform() {
        String edOrgFixtureFile = mungeFileName(dataFile, "_edOrg");
        String edOrgSchoolAssociationFixtureFile = mungeFileName(dataFile, "_school_edOrg");
        String edOrgEdOrgAssociationFixtureFile = mungeFileName(dataFile, "_edOrg_edOrg_association");
        String schoolFixtureFile = mungeFileName(dataFile, "_schools");
        String teacherFixtureFile = mungeFileName(dataFile, "_teachers");
        String sectionFixtureFile = mungeFileName(dataFile, "_sections");
        String teacherSectionAssociationFixtureFile = mungeFileName(dataFile, "_teacher_sections");
        String studentFixtureFile = mungeFileName(dataFile, "_students");
        String studentSectionAssociationFixtureFile = mungeFileName(dataFile, "_student_section_association");
        String studentSchoolAssociationFixtureFile = mungeFileName(dataFile, "_student_school_association");
        String assessmentFixtureFile = mungeFileName(dataFile, "_assessment");
        String studentAssessmentFixtureFile = mungeFileName(dataFile, "_student_assessment");
        String sectionAssessmentFixtureFile = mungeFileName(dataFile, "_section_assessment");
        String sectionSchoolFixtureFile = mungeFileName(dataFile, "_section_school_association");
        
        String sessionFixtureFile = mungeFileName(dataFile, "_sessions");
        String coursesFixtureFile = mungeFileName(dataFile, "_courses");
        String schoolSessionAssociationFixtureFile = mungeFileName(dataFile, "_school_session_association");
        String courseSessionAssociationFixtureFile = mungeFileName(dataFile, "_course_session_association");
        String courseSectionAssociationFixtureFile = mungeFileName(dataFile, "_course_section_association");
        
        // InterchangeSchool contains EdOrg, School, EdOrg-School association, Course
        // InterchangeStudent contains Student
        // InterchangeEnrollment contains StudentSchoolAssociation
        // InterchangeStaffAssociation contains Teacher
        
        DataManager.setEdOrgs(parseEdOrgs());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeEdOrgFixture(edOrgFixtureFile, false);
        } else {
            writeEdOrgFixture("InterchangeSchool.xml", false);
        }
        
        parseEdOrgEdOrgAssociations(); // data is stored in EdOrg objects (on the child)
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeEdOrgEdOrgAssociationFixture(edOrgEdOrgAssociationFixtureFile, false);
        } else {
            // what does EdOrg-EdOrg Association XML look like?
        }
        
        DataManager.setSessions(parseSessions());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSessionFixture(sessionFixtureFile, false);
        } else {
            // what does Session XML look like?
        }
        
        DataManager.setCourses(parseCourses());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeCourseFixture(coursesFixtureFile, false);
        } else {
            writeCourseFixture("InterchangeSchool.xml", true);
        }
        
        DataManager.setSchools(parseSchools());
        parseEdOrgSchoolAssociations();	// data is stored in School objects
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSchoolFixture(schoolFixtureFile, false);
        } else {
            writeSchoolFixture("InterchangeSchool.xml", true);
        }
        
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeEdOrgSchoolsAssociationFixture(edOrgSchoolAssociationFixtureFile, false);
        } else {
            // edorg-school associations are written as part of a school object in XML
        }
        
        parseSchoolSessionAssociations(); // data is stored in School objects
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSchoolSessionAssociationFixture(schoolSessionAssociationFixtureFile, false);
        } else {
            // what does School-Session XML look like?
        }
        
        parseCourseSessionAssociations();	// data is stored in Course objects
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeCourseSessionAssociationFixture(courseSessionAssociationFixtureFile, false);
        } else {
            // what does Course-Session XML look like?
        }
        
        DataManager.setTeachers(parseTeachers());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeTeacherFixture(teacherFixtureFile, false);
        } else {
            writeTeacherFixture("InterchangeStaffAssociation.xml", false);
        }
        
        DataManager.setAssessments(parseAssessments());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeAssessmentFixture(assessmentFixtureFile, false);
        } else {
            // what does assessment XML look like?
        }
        
        DataManager.setSections(parseSections());
        parseCourseSectionAssociations();	// data is stored in Course objects
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSectionFixture(sectionFixtureFile, false);
        } else {
            // what does section XML look like?
        }
        
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeCourseSectionAssociationFixture(courseSectionAssociationFixtureFile, false);
        } else {
            // what does course-section XML look like?
        }
        
        parseTeacherSections();	// data is stored in Teacher objects
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeTeacherSectionAssociationFixture(teacherSectionAssociationFixtureFile, false);
        } else {
            // what does teacher-section XML look like?
        }
        
        DataManager.setStudents(parseStudents());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeStudentFixture(studentFixtureFile, false);
        } else {
            writeStudentFixture("InterchangeStudent.xml", false);
        }
        
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeStudentSchoolAssociationFixture(studentSchoolAssociationFixtureFile, false);
        } else {
            writeStudentSchoolAssociationFixture("InterchangeEnrollment.xml", false);
        }
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeStudentSectionAssociationFixture(studentSectionAssociationFixtureFile, false);
        } else {
            // what does student-section XML look like?
        }
        
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSectionAssessmentAssociationFixture(sectionAssessmentFixtureFile, false);
        } else {
            // what does section-assessment XML look like?
        }
        
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeSectionSchoolAssociationFixture(sectionSchoolFixtureFile, false);
        } else {
            // what does section-school XML look like?
        }
        
        DataManager.setStudentAssessments(parseStudentAssessments());
        if (Configuration.outputType.equals(Configuration.OutputType.Fixture)) {
            writeStudentAssessmentFixture(studentAssessmentFixtureFile, false);
        } else {
            // what does student-assessment XML look like?
        }
    }
    
    private String mungeFileName(String filename, String part) {
        int pos = filename.lastIndexOf('.');
        return filename.substring(0, pos) + part + "_fixture.json";
    }
    
    private String readFile(String file) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);
            int bytesRead = 0;
            byte[] buf = new byte[4096];
            while (bytesRead >= 0) {
                baos.write(buf, 0, bytesRead);
                bytesRead = fis.read(buf);
            }
            String data = new String(baos.toByteArray());
            return data;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    private ArrayList<String> makeLines(String text) {
        ArrayList<String> answer = new ArrayList<String>();
        LineNumberReader lnr = new LineNumberReader(new StringReader(text));
        try {
            String line = lnr.readLine();
            while (line != null) {
                String noFlankingWhitespace = line.trim();
                if (noFlankingWhitespace.length() > 0) {
                    answer.add(noFlankingWhitespace);
                }
                line = lnr.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
    
    private HashMap<String, StudentAssessment> parseStudentAssessments() {
        // Student ID,Performance Level,State,Family,Year,Subject,Grade Level,Assessment ID
        // 862,3,NC,EOG,2010-2011,Math,8,1
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, StudentAssessment> answer = new HashMap<String, StudentAssessment>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===STUDENT ASSESSMENTS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String studentId = (parts.length > ++i) ? parts[i] : "";
            String performanceLevel = (parts.length > ++i) ? parts[i] : "";
            String state = (parts.length > ++i) ? parts[i] : "";
            String family = (parts.length > ++i) ? parts[i] : "";
            String year = (parts.length > ++i) ? parts[i] : "";
            String subject = (parts.length > ++i) ? parts[i] : "";
            String gradeLevel = (parts.length > ++i) ? parts[i] : "";
            String assessmentId = (parts.length > ++i) ? parts[i] : "";
            
            StudentAssessment sa = new StudentAssessment(studentId, performanceLevel, assessmentId);
            answer.put(studentId + "-" + assessmentId, sa);
        }
        return answer;
    }
    
    private HashMap<String, Assessment> parseAssessments() {
        // Assessment ID,title,subject,category,grade level,content standard,version,school,state
        // 1,Mathematics Achievement Assessment Test,Mathematics,EOG,8,LEA_STANDARD,2010-2011,6,NC
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Assessment> answer = new HashMap<String, Assessment>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===ASSESSMENTS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String assessmentId = (parts.length > ++i) ? parts[i] : "";
            String title = (parts.length > ++i) ? parts[i] : "";
            String subject = (parts.length > ++i) ? parts[i] : "";
            String category = (parts.length > ++i) ? parts[i] : "";
            String gradeLevel = (parts.length > ++i) ? parts[i] : "";
            String standard = (parts.length > ++i) ? parts[i] : "";
            String version = (parts.length > ++i) ? parts[i] : "";
            String schoolId = (parts.length > ++i) ? parts[i] : "";
            String state = (parts.length > ++i) ? parts[i] : "";
            
            AssessmentCode code = null;
            if (schoolId != null && schoolId.trim().length() > 0) {
                code = new AssessmentCode(schoolId);
            } else {
                code = new AssessmentCode("State", state);
            }
            ArrayList<AssessmentCode> codes = new ArrayList<AssessmentCode>();
            codes.add(code);
            
            AssessmentBody body = new AssessmentBody(title, codes, subject, category, gradeLevel, standard, version);
            Assessment assessment = new Assessment(body, assessmentId);
            answer.put(assessmentId, assessment);
        }
        return answer;
    }
    
    private HashMap<String, Teacher> parseTeachers() {
        // staffUniqueStateId,firstName,lastSurname,sex,highestLevelOfEducationCompleted
        // 8888001,Waldo,Washington,Male,Masters
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Teacher> answer = new HashMap<String, Teacher>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===TEACHERS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String firstName = (parts.length > ++i) ? parts[i] : "";
            String lastSurname = (parts.length > ++i) ? parts[i] : "";
            String sex = (parts.length > ++i) ? parts[i] : "";
            String highestLevelOfEducationCompleted = (parts.length > ++i) ? parts[i] : "";
            
            Teacher teacher = new Teacher(id, firstName, lastSurname, sex, highestLevelOfEducationCompleted);
            answer.put(id, teacher);
        }
        return answer;
    }
    
    private void parseTeacherSections() {
        // staffUniqueStateId,uniqueSectionCode
        // 8888031,math-8-6-1
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===TEACHER SECTIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String staffUniqueStateId = (parts.length > ++i) ? parts[i] : "";
            String uniqueSectionCode = (parts.length > ++i) ? parts[i] : "";
            
            Teacher teacher = DataManager.getTeacher(staffUniqueStateId);
            // teacher.addSectionId(uniqueSectionCode);
            
            Section section = DataManager.getSection(uniqueSectionCode);
            teacher.addSectionId(section.getUuid());
        }
    }
    
    private HashMap<String, Section> parseSections() {
        // uniqueSectionCode,school,sectionName,sequenceOfCourse,assessment
        // math-8-6-1,6,8th grade math,1,1
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Section> answer = new HashMap<String, Section>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===SECTIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String uniqueSectionCode = (parts.length > ++i) ? parts[i] : "";
            String schoolId = (parts.length > ++i) ? parts[i] : "";
            String sectionName = (parts.length > ++i) ? parts[i] : "";
            String sequenceOfCourse = (parts.length > ++i) ? parts[i] : "";
            String assessmentId = (parts.length > ++i) ? parts[i] : "";
            String sessionId = (parts.length > ++i) ? parts[i] : "";
            
            Section section = new Section(uniqueSectionCode, sectionName, sequenceOfCourse);
            Assessment a = DataManager.getAssessment(assessmentId);
            section.setAssessment(a);
            
            Session s = DataManager.getSession(sessionId);
            section.setSession(s);
            
            School school = DataManager.getSchool(schoolId);
            section.setSchool(school);
            
            answer.put(uniqueSectionCode, section);
        }
        return answer;
    }
    
    private HashMap<String, Student> parseStudents() {
        // ID,First Name, Last
        // Name,School,Section,Course,Teacher,Grade,economicDisadvantaged,schoolFoodServicesEligibility,studentCharacteristics,
        // limitedEnglishProficiency,section504Disabilities,programParticipations
        // 1,James,Smith,1,,,Washington,K,false,Full price,,NotLimited,,,
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Student> answer = new HashMap<String, Student>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===STUDENTS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String first = (parts.length > ++i) ? parts[i] : "";
            String last = (parts.length > ++i) ? parts[i] : "";
            String schoolId = (parts.length > ++i) ? parts[i] : "";
            String sectionId = (parts.length > ++i) ? parts[i] : "";
            String course = (parts.length > ++i) ? parts[i] : "";
            String teacher = (parts.length > ++i) ? parts[i] : "";
            String grade = (parts.length > ++i) ? parts[i] : "";
            String econDis = (parts.length > ++i) ? parts[i] : "";
            String sfse = (parts.length > ++i) ? parts[i] : "";
            String stuChar = (parts.length > ++i) ? parts[i] : "";
            String lep = (parts.length > ++i) ? parts[i] : "";
            String sect504Dis = (parts.length > ++i) ? parts[i] : "";
            String progPartic = (parts.length > ++i) ? parts[i] : "";
            String displacement = (parts.length > ++i) ? parts[i] : "";
            String disability = (parts.length > ++i) ? parts[i] : "";
            
            StudentName sName = new StudentName(first, null, last);
            BirthData bd = new BirthData("01-01-2000");
            
            School school = DataManager.getSchool(schoolId);
            Student student = new Student(sName, bd, id, "Male", econDis, school, grade, sfse, stuChar, lep,
                    sect504Dis, progPartic, displacement, disability);
            
            Section section = DataManager.getSection(sectionId);
            if (section != null) {
                student.addSection(section);
            }
            answer.put(id, student);
        }
        return answer;
    }
    
    private HashMap<String, School> parseSchoolsAvro() throws JsonIOException, JsonSyntaxException,
            FileNotFoundException {
        // ID,Name,street,city,state,postal code,telephone,grades,categories
        // 1,Small Fry Elementary School,111 Main
        // St.,Smallville,NC,27500,919-191-9191,K-5,Elementary_School
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, School> answer = new HashMap<String, School>();
        
        // TODO - incomplete
        JsonParser parser = new JsonParser();
        try {
            JsonElement jsonElement = parser.parse(new JsonReader(new FileReader(AvroPath + "school_body.avpr")));
            JsonObject obj = jsonElement.getAsJsonObject();
            JsonObject nameObj = obj.getAsJsonObject("name");
            JsonArray fields = obj.getAsJsonArray("fields");
            System.out.println(obj);
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===SCHOOLS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String name = (parts.length > ++i) ? parts[i] : "";
            String street = (parts.length > ++i) ? parts[i] : "";
            String city = (parts.length > ++i) ? parts[i] : "";
            String state = (parts.length > ++i) ? parts[i] : "";
            String zip = (parts.length > ++i) ? parts[i] : "";
            String telephone = (parts.length > ++i) ? parts[i] : "";
            String grades = (parts.length > ++i) ? parts[i] : "";
            String categories = (parts.length > ++i) ? parts[i] : "";
            
            School school = new School(name, id, street, city, state, zip, "Wake", telephone, grades, categories);
            answer.put(id, school);
        }
        return answer;
    }
    
    private HashMap<String, School> parseSchools() {
        // ID,Name,street,city,state,postal code,telephone,grades,categories
        // 1,Small Fry Elementary School,111 Main
        // St.,Smallville,NC,27500,919-191-9191,K-5,Elementary_School
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, School> answer = new HashMap<String, School>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===SCHOOLS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String name = (parts.length > ++i) ? parts[i] : "";
            String street = (parts.length > ++i) ? parts[i] : "";
            String city = (parts.length > ++i) ? parts[i] : "";
            String state = (parts.length > ++i) ? parts[i] : "";
            String zip = (parts.length > ++i) ? parts[i] : "";
            String telephone = (parts.length > ++i) ? parts[i] : "";
            String grades = (parts.length > ++i) ? parts[i] : "";
            String categories = (parts.length > ++i) ? parts[i] : "";
            
            School school = new School(name, id, street, city, state, zip, "Wake", telephone, grades, categories);
            answer.put(id, school);
        }
        return answer;
    }
    
    private HashMap<String, Session> parseSessions() {
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Session> answer = new HashMap<String, Session>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===SESSIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String schoolYear = (parts.length > ++i) ? parts[i] : "";
            String semester = (parts.length > ++i) ? parts[i] : "";
            String beginDate = (parts.length > ++i) ? parts[i] : "";
            String endDate = (parts.length > ++i) ? parts[i] : "";
            String days = (parts.length > ++i) ? parts[i] : "";
            
            Session session = new Session(schoolYear, semester, beginDate, endDate, Integer.parseInt(days));
            answer.put(id, session);
        }
        return answer;
    }
    
    private HashMap<String, Course> parseCourses() {
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, Course> answer = new HashMap<String, Course>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===COURSES===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String courseDefinedBy = (parts.length > ++i) ? parts[i] : "";
            String adoptionDate = (parts.length > ++i) ? parts[i] : "";
            String description = (parts.length > ++i) ? parts[i] : "";
            String characteristics = (parts.length > ++i) ? parts[i] : "";
            String required = (parts.length > ++i) ? parts[i] : "";
            String code = (parts.length > ++i) ? parts[i] : "";
            String gpaAppl = (parts.length > ++i) ? parts[i] : "";
            String idSystem = (parts.length > ++i) ? parts[i] : "";
            String minCredit = (parts.length > ++i) ? parts[i] : "";
            String maxCredit = (parts.length > ++i) ? parts[i] : "";
            String subject = (parts.length > ++i) ? parts[i] : "";
            String level = (parts.length > ++i) ? parts[i] : "";
            String title = (parts.length > ++i) ? parts[i] : "";
            String numParts = (parts.length > ++i) ? parts[i] : "";
            String orgCode = (parts.length > ++i) ? parts[i] : "";
            String careerPath = (parts.length > ++i) ? parts[i] : "";
            
            Course course = new Course(courseDefinedBy, adoptionDate, description, characteristics,
                    Boolean.parseBoolean(required), code, gpaAppl, idSystem, Integer.parseInt(minCredit),
                    Integer.parseInt(maxCredit), subject, level, title, Integer.parseInt(numParts), orgCode, careerPath);
            
            for (int k = i + 1; k < parts.length; k++) {
                String grade = parts[k];
                course.addGrade(grade);
            }
            
            answer.put(id, course);
        }
        return answer;
    }
    
    private HashMap<String, EducationalOrganization> parseEdOrgs() {
        // id,edOrgName,street,suite,city,state,postalCode,stateOrgId
        // 1,Greater Smallville K-12 School District,123 District Office
        // Way,200,NY,NY,,KS-SMALLVILLE
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        HashMap<String, EducationalOrganization> answer = new HashMap<String, EducationalOrganization>();
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===ED-ORGS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String id = (parts.length > ++i) ? parts[i] : "";
            String name = (parts.length > ++i) ? parts[i] : "";
            String street = (parts.length > ++i) ? parts[i] : "";
            String suite = (parts.length > ++i) ? parts[i] : "";
            String city = (parts.length > ++i) ? parts[i] : "";
            String state = (parts.length > ++i) ? parts[i] : "";
            String zip = (parts.length > ++i) ? parts[i] : "";
            String stateOrgId = (parts.length > ++i) ? parts[i] : "";
            String edOrgType = (parts.length > ++i) ? parts[i] : "";
            
            EducationalOrganization edOrg = new EducationalOrganization(name, street, suite, city, state, zip,
                    stateOrgId);
            edOrg.setType(edOrgType);
            answer.put(id, edOrg);
        }
        return answer;
    }
    
    private void parseEdOrgSchoolAssociations() {
        // edOrg,school
        // 1,6
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===ED-ORG SCHOOLS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String edOrgId = (parts.length > ++i) ? parts[i] : "";
            String schoolId = (parts.length > ++i) ? parts[i] : "";
            
            School school = DataManager.getSchool(schoolId);
            EducationalOrganization edOrg = DataManager.getEdOrg(edOrgId);
            school.setEdOrg(edOrg);
        }
    }
    
    private void parseCourseSessionAssociations() {
        // course,session,local course code,local course title
        // 3,1,LCCGR1,German 1 - Intro to German
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===COURSE SESSIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String courseId = (parts.length > ++i) ? parts[i] : "";
            String sessionId = (parts.length > ++i) ? parts[i] : "";
            String lcc = (parts.length > ++i) ? parts[i] : "";
            String lct = (parts.length > ++i) ? parts[i] : "";
            
            Course course = DataManager.getCourse(courseId);
            Session session = DataManager.getSession(sessionId);
            course.createSessionAssociation(session, lcc, lct);
        }
    }
    
    private void parseCourseSectionAssociations() {
        // course id,uniqueSectionCode
        // 5,math-8-6-1
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===COURSE SECTIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String courseId = (parts.length > ++i) ? parts[i] : "";
            String sectionId = (parts.length > ++i) ? parts[i] : "";
            
            Course course = DataManager.getCourse(courseId);
            Section section = DataManager.getSection(sectionId);
            course.createSectionAssociation(section);
        }
    }
    
    private void parseSchoolSessionAssociations() {
        // school,session,grading period name,begin date,end date,total days
        // 9,1,Spring 2011 at Algebra Alternative,2011-01-01,2011-06-31,90
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===SCHOOL SESSIONS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String schoolId = (parts.length > ++i) ? parts[i] : "";
            String sessionId = (parts.length > ++i) ? parts[i] : "";
            String gpName = (parts.length > ++i) ? parts[i] : "";
            String beginDate = (parts.length > ++i) ? parts[i] : "";
            String endDate = (parts.length > ++i) ? parts[i] : "";
            String days = (parts.length > ++i) ? parts[i] : "";
            
            School school = DataManager.getSchool(schoolId);
            Session session = DataManager.getSession(sessionId);
            school.createSessionAssociation(session, gpName, beginDate, endDate, Integer.parseInt(days));
        }
    }
    
    private void parseEdOrgEdOrgAssociations() {
        // parentID,childId
        // 0,1
        
        String data = readFile(dataDirectory + dataFile);
        ArrayList<String> lines = makeLines(data);
        
        boolean inParseBlock = false;
        for (String line : lines) {
            if (line.startsWith("===")) {
                inParseBlock = line.contains("===ED-ORG ED-ORGS===");
                continue;
            }
            if (!inParseBlock || line.startsWith("#")) {	// use '#' to indicate full line comments
                continue;
            }
            String[] parts = line.split(",");
            int i = -1;
            String parentId = (parts.length > ++i) ? parts[i] : "";
            String childId = (parts.length > ++i) ? parts[i] : "";
            
            EducationalOrganization parent = DataManager.getEdOrg(parentId);
            EducationalOrganization child = DataManager.getEdOrg(childId);
            child.setParentEdOrg(parent);
        }
    }
    
    private void writeAssessmentFixture(String fileName, boolean append) {
        Iterator<Assessment> iter = DataManager.getAssessments().values().iterator();
        writeFixture(fileName, iter, append);
    }
    
    private void writeStudentAssessmentFixture(String fileName, boolean append) {
        Iterator<StudentAssessment> iter = DataManager.getStudentAssessments().values().iterator();
        writeFixture(fileName, iter, append);
    }
    
    private void writeStudentFixture(String fileName, boolean append) {
        String header = "", footer = "";
        if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
            header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeStudent xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-Student.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">\n";
            footer = "</InterchangeStudent>\n";
        }
        Iterator<Student> iter = DataManager.getStudents().values().iterator();
        writeFixture(fileName, iter, append, header, footer);
    }
    
    private void writeStudentSchoolAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
                String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeStudentEnrollment xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-StudentEnrollment.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">\n";
                fos.write(header.getBytes());
            }
            
            Iterator<Student> iter = DataManager.getStudents().values().iterator();
            while (iter.hasNext()) {
                Student student = iter.next();
                String s = student.emitSchoolAssociation();
                fos.write(s.getBytes());
            }
            
            if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
                String footer = "</InterchangeStudentEnrollment>\n";
                fos.write(footer.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeStudentSectionAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Student> iter = DataManager.getStudents().values().iterator();
            while (iter.hasNext()) {
                Student student = iter.next();
                String s = student.emitSectionAssociations();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeSectionAssessmentAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Section> iter = DataManager.getSections().values().iterator();
            while (iter.hasNext()) {
                Section section = iter.next();
                String s = section.emitAssessmentAssociation();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeSectionSchoolAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Section> iter = DataManager.getSections().values().iterator();
            while (iter.hasNext()) {
                Section section = iter.next();
                String s = section.emitSchoolAssociation();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeTeacherSectionAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Teacher> iter = DataManager.getTeachers().values().iterator();
            while (iter.hasNext()) {
                Teacher teacher = iter.next();
                String s = teacher.emitSectionAssociation();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeSectionFixture(String fileName, boolean append) {
        Iterator<Section> iter = DataManager.getSections().values().iterator();
        writeFixture(fileName, iter, append);
    }
    
    private void writeFixture(String fileName, Iterator<?> iter, boolean append) {
        writeFixture(fileName, iter, append, null, null);
    }
    
    private void writeFixture(String fileName, Iterator<?> iter, boolean append, String header, String footer) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            if (header != null) {
                fos.write(header.getBytes());
            }
            while (iter.hasNext()) {
                MongoDataEmitter emitter = (MongoDataEmitter) iter.next();
                String s = emitter.emit();
                fos.write(s.getBytes());
            }
            if (footer != null) {
                fos.write(footer.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeTeacherFixture(String fileName, boolean append) {
        String header = "", footer = "";
        if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
            header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeStaffAssociation xmlns=\"http://ed-fi.org/0100\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Ed-Fi/Interchange-StaffAssociation.xsd\">\n";
            footer = "</InterchangeStaffAssociation>\n";
        }
        Iterator<Teacher> iter = DataManager.getTeachers().values().iterator();
        writeFixture(fileName, iter, append, header, footer);
    }
    
    private void writeEdOrgFixture(String fileName, boolean append) {
        String header = "", footer = "";
        if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
            header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<InterchangeEducationOrganization xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"Interchange-EducationOrganization.xsd\" xmlns=\"http://ed-fi.org/0100RFC062811\">\n";
        }
        
        Iterator<EducationalOrganization> iter = DataManager.getEdOrgs().values().iterator();
        writeFixture(fileName, iter, append, header, footer);
    }
    
    private void writeSessionFixture(String fileName, boolean append) {
        Iterator<Session> iter = DataManager.getSessions().values().iterator();
        writeFixture(fileName, iter, append);
    }
    
    private void writeCourseFixture(String fileName, boolean append) {
        Iterator<Course> iter = DataManager.getCourses().values().iterator();
        writeFixture(fileName, iter, append);
    }
    
    private void writeCourseSessionAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Course> iter = DataManager.getCourses().values().iterator();
            while (iter.hasNext()) {
                Course course = iter.next();
                String s = course.emitSessionAssociations();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeCourseSectionAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<Course> iter = DataManager.getCourses().values().iterator();
            while (iter.hasNext()) {
                Course course = iter.next();
                String s = course.emitSectionAssociations();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeSchoolSessionAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<School> iter = DataManager.getSchools().values().iterator();
            while (iter.hasNext()) {
                School school = iter.next();
                String s = school.emitSessionAssociations();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeEdOrgSchoolsAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<School> iter = DataManager.getSchools().values().iterator();
            while (iter.hasNext()) {
                School school = iter.next();
                String s = school.emitEdOrgAssociation();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeEdOrgEdOrgAssociationFixture(String fileName, boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName, append);
            
            Iterator<EducationalOrganization> iter = DataManager.getEdOrgs().values().iterator();
            while (iter.hasNext()) {
                EducationalOrganization edOrg = iter.next();
                String s = edOrg.emitParentEdOrgAssociation();
                fos.write(s.getBytes());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void writeSchoolFixture(String fileName, boolean append) {
        String header = "", footer = "";
        if (Configuration.outputType.equals(Configuration.OutputType.EdFiXml)) {
            footer = "</InterchangeEducationOrganization>\n";
        }
        Iterator<School> iter = DataManager.getSchools().values().iterator();
        writeFixture(fileName, iter, append, header, footer);
    }
    
    private static void usage() {
        String usage = "Usage:\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName()
                + " [-u | [<filename.csv> [[<directory>] -x ]]\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName() + " -u\n";
        usage += "    - print this usage message\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName() + " <filename.csv> <directory>\n";
        usage += "    - load data specified by <filename>.csv and export it as a set of Mongo-importable fixture files.\n";
        usage += "      The output files are created in the same directory.\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName() + " <filename.csv>\n";
        usage += "    - same as above, using the current working directory\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName() + " <filename.csv> -x\n";
        usage += "    - same as above in the current directory, but convert the mock data to Ed-Fi XML ingestible format.\n\n";
        usage += "java " + StudentAssessmentParser.class.getSimpleName() + " <filename.csv> <directory> -x\n";
        usage += "    - same as above, using the named directory, but convert the mock data to Ed-Fi XML ingestible format.\n\n";
        System.out.println(usage);
        System.exit(0);
    }
    
    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            usage();
        }
        
        if (args[0].equals("-u")) {
            usage();
        }
        
        String outputType = "json";
        String fileName = args[0];
        String dataDir = ".";
        String nextArg = (args.length < 2) ? "." : args[1];
        if (nextArg.equals("-x")) {
            outputType = "xml";
            nextArg = (args.length < 3) ? "." : args[2];
            dataDir = nextArg;
        } else {
            dataDir = nextArg;
            outputType = (args.length < 3) ? "json" : (args[2].equals("-x") ? "xml" : "json");
        }
        
        if (!dataDir.endsWith(File.separator)) {
            dataDir += File.separator;
        }
        StudentAssessmentParser sap = new StudentAssessmentParser(fileName, dataDir, outputType);
        sap.transform();
    }
}
