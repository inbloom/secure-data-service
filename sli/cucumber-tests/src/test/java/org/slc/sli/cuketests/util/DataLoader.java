package org.slc.sli.cuketests.util;

import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.GradeLevelType;
import org.slc.sli.domain.enums.SexType;

public class DataLoader {
    
    private boolean loaded = false;
    private Map<String, Integer> schoolStudentIdToStudentId;
    private Map<String, Integer> stateIdToId;
    private Map<String, Integer> nameToStudentId;
    private Map<String, Integer> nameToSchoolId;
    
    public int lookupIdByStudentSchoolId(String studentSchoolId) {
        return this.schoolStudentIdToStudentId.get(studentSchoolId);
    }
    
    public int lookupIdByStateOrgId(String stateOrganizationId) {
        return this.stateIdToId.get(stateOrganizationId);
    }
    
    public int lookupIdBySchoolName(String schoolName) {
        return this.nameToSchoolId.get(schoolName);
    }
    
    public int lookupIdByStudentName(String studentName) {
        return this.nameToStudentId.get(studentName);
    }
    
    public synchronized void loadData(String apiUri, String user, String pass) throws Exception {
        if (loaded) {
            return;
        }
        Client client = JerseyClientFactory.createClient();
        client.addFilter(new HTTPBasicAuthFilter(user, pass));
        WebResource webResource = client.resource(apiUri);
        
        List<School> sampleSchools = readSchoolData("/data/school-sample-data.csv");
        persistSchools(sampleSchools, webResource);
        
        List<Student> sampleStudents = readStudentData("/data/student-sample-data.csv");
        persistStudents(sampleStudents, webResource);
        
        List<StudentSchoolAssociation> sampleAssocs = readStudentSchoolAssociationData("/data/student-school-assoc-sample-data.csv");
        persistStudentSchoolAssociations(sampleAssocs, webResource);
        System.err.println("loaded " + this.stateIdToId.size() + " schools");
        System.err.println("loaded " + this.schoolStudentIdToStudentId.size() + " students");
        loaded = true;
    }
    
    private static List<Student> readStudentData(String file) throws Exception {
        List<Student> students = new LinkedList<Student>();
        CSVReader studentDataReader = new CSVReader(new InputStreamReader(DataLoader.class.getResourceAsStream(file)));
        studentDataReader.readNext();
        String[] row;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while ((row = studentDataReader.readNext()) != null) {
            Student student = new Student();
            student.setStudentSchoolId(row[1]);
            student.setFirstName(row[7]);
            student.setMiddleName(row[8]);
            student.setLastSurname(row[9]);
            student.setSex(SexType.fromValue(row[18]));
            student.setBirthDate(sdf.parse(row[19]));
            students.add(student);
        }
        return students;
    }
    
    private static List<School> readSchoolData(String file) throws Exception {
        List<School> schools = new LinkedList<School>();
        CSVReader studentDataReader = new CSVReader(new InputStreamReader(DataLoader.class.getResourceAsStream(file)));
        studentDataReader.readNext();
        String[] row;
        while ((row = studentDataReader.readNext()) != null) {
            School school = new School();
            school.setStateOrganizationId(row[0]);
            school.setFullName(row[3]);
            school.setShortName(row[4]);
            schools.add(school);
        }
        return schools;
    }
    
    private List<StudentSchoolAssociation> readStudentSchoolAssociationData(String file) throws Exception {
        List<StudentSchoolAssociation> associations = new LinkedList<StudentSchoolAssociation>();
        CSVReader studentDataReader = new CSVReader(new InputStreamReader(DataLoader.class.getResourceAsStream(file)));
        studentDataReader.readNext();
        String[] row;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        while ((row = studentDataReader.readNext()) != null) {
            StudentSchoolAssociation ssa = new StudentSchoolAssociation();
            int studentId = this.lookupIdByStudentSchoolId(row[2]);
            int schoolId = this.lookupIdByStateOrgId(row[25]);
            ssa.setStudentId(studentId);
            ssa.setSchoolId(schoolId);
            Calendar entryCal = new GregorianCalendar();
            entryCal.setTime(sdf.parse(row[28]));
            ssa.setEntryDate(entryCal);
            ssa.setEntryGradeLevel(GradeLevelType.fromValue(row[29]));
            
            associations.add(ssa);
        }
        return associations;
    }
    
    private void persistStudentSchoolAssociations(List<StudentSchoolAssociation> ssas, WebResource resource)
            throws Exception {
        for (StudentSchoolAssociation ssa : ssas) {
            resource.path("schools").path("" + ssa.getSchoolId()).path("students").path("" + ssa.getStudentId())
                    .post(ssa);
        }
    }
    
    private void persistSchools(List<School> sampleSchools, WebResource resource) throws Exception {
        // delete sample data if it already exists. (it may have been modified by previous tests)
        String response = resource.path("schools").get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, School.class);
        List<School> schoolsFromApi = mapper.readValue(response, type);
        Set<String> stateIds = new HashSet<String>();
        for (School s : sampleSchools) {
            stateIds.add(s.getStateOrganizationId());
        }
        for (School s : schoolsFromApi) {
            if (stateIds.contains(s.getStateOrganizationId())) {
                resource.path("schools").path("" + s.getSchoolId()).accept("application/json").delete();
            }
        }
        
        this.stateIdToId = new HashMap<String, Integer>();
        this.nameToSchoolId = new HashMap<String, Integer>();
        for (School s : sampleSchools) {
            ClientResponse post = resource.path("schools").accept("application/json").post(ClientResponse.class, s);
            String location = post.getHeaders().get("Location").get(0);
            String idString = location.substring(location.lastIndexOf('/') + 1);
            int id = Integer.parseInt(idString);
            this.stateIdToId.put(s.getStateOrganizationId(), id);
            this.nameToSchoolId.put(s.getFullName(), id);
        }
    }
    
    private void persistStudents(List<Student> sampleStudents, WebResource resource) throws Exception {
        // delete sample data if it already exists. (it may have been modified by previous tests)
        String response = resource.path("students").get(String.class);
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, Student.class);
        List<Student> studentsFromApi = mapper.readValue(response, type);
        Set<String> studentSchoolIds = new HashSet<String>();
        for (Student s : sampleStudents) {
            studentSchoolIds.add(s.getStudentSchoolId());
        }
        for (Student s : studentsFromApi) {
            if (studentSchoolIds.contains(s.getStudentSchoolId())) {
                resource.path("students").path("" + s.getStudentId()).accept("application/json").delete();
            }
        }
        // add sample data through API
        this.schoolStudentIdToStudentId = new HashMap<String, Integer>();
        this.nameToStudentId = new HashMap<String, Integer>();
        for (Student s : sampleStudents) {
            ClientResponse post = resource.path("students").accept("application/json").post(ClientResponse.class, s);
            String location = post.getHeaders().get("Location").get(0);
            String idString = location.substring(location.lastIndexOf('/') + 1);
            int id = Integer.parseInt(idString);
            this.schoolStudentIdToStudentId.put(s.getStudentSchoolId(), id);
            String name;
            if (s.getMiddleName() != null && s.getMiddleName().trim().length() > 0) {
                name = s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastSurname();
            } else {
                name = s.getFirstName() + " " + s.getLastSurname();
            }
            this.nameToStudentId.put(name, id);
        }
    }
    
}
