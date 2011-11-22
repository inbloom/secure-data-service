package org.slc.sli.cuketests.studentSchoolAssociation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import cucumber.annotation.en.And;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;

import org.slc.sli.cuketests.util.DataLoader;
import org.slc.sli.cuketests.util.JerseyClientFactory;
import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;
import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.domain.enums.GradeLevelType;

public class StudentSchoolAssociationStepdefs {
    
    private static final String RESOURCE_URL = "http://ec2-50-19-203-5.compute-1.amazonaws.com:8080/api/rest";
//    private static final String RESOURCE_URL = "http://localhost:8081/api/rest";
    
    Client client = JerseyClientFactory.createClient();
    
    WebResource webResource = client.resource(RESOURCE_URL);
    static final DataLoader dataLoader = new DataLoader();
    
    String format;
    String uri;
    String verb;
    String student;
    String school;
    String newSchool;
    String user;
    String pass;
    
    @Given("^the school \"([^\"]*)\"$")
    public void the_school_(String school) {
        this.school = school;
    }
    
    @Given("^the student \"([^\"]*)\"$")
    public void the_student_(String student) {
        this.student = student;
    }
    
    @And("^format \"([^\"]*)\"$")
    public void format_(String format) {
        this.format = format;
    }
    
    @Given("^I am logged in using \"([^\"]*)\" \"([^\"]*)\"$")
    public void I_am_logged_in_using_(String username, String password) {
        client.addFilter(new HTTPBasicAuthFilter(username, password));
        this.user = username;
        this.pass = password;
    }
    
    @Given("^I have access to all students and schools$")
    public void I_have_access_to_all_students_and_schools() {
        // you sure do!
    }
    
    @Given("^\"([^\"]*)\" attends \"([^\"]*)\"$")
    public void _attends_(String name, String school) {
        this.student = name;
        this.school = school;
    }
    
    @Given("^the SLI_SMALL dataset is loaded$")
    public void the_SLI_SMALL_dataset_is_loaded() throws Exception {
        // TODO: This is a temporary workaround for this sprint. Use main data loading solution when
        // it is available.
        assertNotNull(user);
        assertNotNull(pass);
        dataLoader.loadData(RESOURCE_URL, user, pass);
    }
    
    @When("^I navigate to DELETE \"([^\"]*)\"$")
    public void I_navigate_to_DELETE_(String uri) {
        this.uri = uri;
        this.verb = "delete";
    }
    
    @When("^I navigate to GET \"([^\"]*)\"$")
    public void I_navigate_to_GET_(String uri) throws Exception {
        this.uri = uri;
        this.verb = "get";
    }
    
    @When("^I navigate to POST \"([^\"]*)\"$")
    public void I_navigate_to_POST_(String uri) {
        this.uri = uri;
        this.verb = "post";
    }
    
    @When("^I navigate to PUT \"([^\"]*)\"$")
    public void I_navigate_to_PUT_(String uri) {
        this.uri = uri;
        this.verb = "put";
    }
    
    @And("^change the school to \"([^\"]*)\"$")
    public void change_the_school_to(String newSchool) {
        this.newSchool = newSchool;
    }
    
    /* THEN */
    
    List<School> schools;
    List<Student> students;
    
    @Then("^I should receive a return code of (\\d+)$")
    public void I_should_receive_a_return_code_of_(int status) throws Exception {
        assertNotNull(format);
        if (school != null && student == null && verb.equals("get")) {
            int schoolId = dataLoader.lookupIdBySchoolName(school);
            this.students = findStudentsBySchool(webResource, format, schoolId);
            
        } else if (student != null && school == null && verb.equals("get")) {
            int studentId = dataLoader.lookupIdByStudentName(student);
            this.schools = findSchoolsByStudent(webResource, format, studentId);
            
        } else if (student != null && school != null && verb.equals("put")) {
            int schoolId = dataLoader.lookupIdBySchoolName(school);
            int studentId = dataLoader.lookupIdByStudentName(student);
            int newSchoolId = dataLoader.lookupIdBySchoolName(newSchool);
            List<StudentSchoolAssociation> assocs = findAssociationsByStudentAndSchool(webResource, format, studentId,
                    schoolId);
            for (StudentSchoolAssociation ssa : assocs) {
                ssa.setSchoolId(newSchoolId);
                ClientResponse response = webResource.path("students").path(studentId + "").path("schools")
                        .path(schoolId + "").path(ssa.getAssociationId() + "").accept(format).put(ClientResponse.class, ssa);
                if (response.getStatus() == 500)
                    System.err.println(response.getEntity(String.class));
                assertEquals(status, response.getStatus());
            }
            
        } else if (student != null && school != null && verb.equals("delete")) {
            int schoolId = dataLoader.lookupIdBySchoolName(school);
            int studentId = dataLoader.lookupIdByStudentName(student);
            List<StudentSchoolAssociation> assocs = findAssociationsByStudentAndSchool(webResource, format, studentId,
                    schoolId);
            for (StudentSchoolAssociation ssa : assocs) {
                ClientResponse response = webResource.path("students").path(studentId + "").path("schools")
                        .path(schoolId + "").path(ssa.getAssociationId() + "").accept(format).delete(ClientResponse.class);
                assertEquals(status, response.getStatus());
            }
            
        } else if (student != null && school != null && verb.equals("post")) {
            int schoolId = dataLoader.lookupIdBySchoolName(school);
            int studentId = dataLoader.lookupIdByStudentName(student);
            StudentSchoolAssociation ssa = new StudentSchoolAssociation();
            ssa.setStudentId(studentId);
            ssa.setSchoolId(schoolId);
            ssa.setEntryGradeLevel(GradeLevelType.INFANT_TODDLER);
            ClientResponse response = webResource.path("students").path(studentId + "").path("schools")
                    .path(schoolId + "").accept(format).post(ClientResponse.class, ssa);
            assertEquals(status, response.getStatus());
        } else {
            fail("invalid test state");
        }
    }
    
    @And("^I should find the student \"([^\"]*)\"$")
    public void I_should_find_the_school_(String expectedStudent) {
        assertNotNull(this.students);
        for (Student s : this.students) {
            String name = buildStudentName(s);
            if (name.equals(expectedStudent)) {
                assertTrue(true);
                return;
            }
        }
        fail();
    }
    
    @And("^I should find the school \"([^\"]*)\"$")
    public void I_should_find_the_student_(String expectedSchool) {
        assertNotNull(this.schools);
        for (School s : this.schools) {
            if (s.getFullName().equals(expectedSchool)) {
                assertTrue(true);
                return;
            }
        }
        fail();
    }
    
    @And("^I should not find the student \"([^\"]*)\"$")
    public void I_should_not_find_the_student_(String arg1) {
        assertNotNull(this.students);
        for (Student s : this.students) {
            if (buildStudentName(s).equals(arg1)) {
                fail();
            }
        }
        assertTrue(true);
    }
    
    @And("^I should see (\\d+) school$")
    public void I_should_see_school(int count) {
        assertNotNull(this.schools);
        assertEquals(count, this.schools.size());
    }
    
    @And("^I should see (\\d+) schools$")
    public void I_should_see_schools(int count) {
        assertNotNull(this.schools);
        assertEquals(count, this.schools.size());
    }
    
    @And("^I should see (\\d+) students$")
    public void I_should_see_students(int count) {
        assertNotNull(this.students);
        assertEquals(count, this.students.size());
    }
    
    @And("^I should see the student \"([^\"]*)\" attends \"([^\"]*)\"$")
    public void I_should_see_the_student_attends_(String student, String school) throws Exception {
        assertNotNull(format);
        Integer studentId = dataLoader.lookupIdByStudentName(student);
        Integer schoolId = dataLoader.lookupIdBySchoolName(school);
        assertNotNull(studentId);
        assertNotNull(schoolId);
        List<StudentSchoolAssociation> assocs = findAssociationsByStudentAndSchool(webResource, format, studentId,
                schoolId);
        assertEquals(1, assocs.size());
    }
    
    @And("^I should see the student \"([^\"]*)\" does not attend \"([^\"]*)\"$")
    public void I_should_see_the_student_does_not_attend_(String student, String school) throws Exception {
        assertNotNull(format);
        Integer studentId = dataLoader.lookupIdByStudentName(student);
        Integer schoolId = dataLoader.lookupIdBySchoolName(school);
        assertNotNull(studentId);
        assertNotNull(schoolId);
        List<StudentSchoolAssociation> assocs = findAssociationsByStudentAndSchool(webResource, format, studentId,
                schoolId);
        assertEquals(0, assocs.size());
    }
    
    private static String buildStudentName(Student s) {
        if (s.getMiddleName() != null && s.getMiddleName().trim().length() > 0) {
            return s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastSurname();
        } else {
            return s.getFirstName() + " " + s.getLastSurname();
        }
    }
    
    private static List<StudentSchoolAssociation> findAssociationsByStudentAndSchool(WebResource resource,
            String format, int studentId, int schoolId) throws Exception {
        List<StudentSchoolAssociation> ssas = new LinkedList<StudentSchoolAssociation>();
        ClientResponse response = resource.path("students").path("" + studentId).path("schools").path("" + schoolId)
                .accept(format).get(ClientResponse.class);
        for (String link : response.getHeaders().get("Link")) {
            Pattern pattern = Pattern.compile("<(.+)>;rel=(.+)");
            java.util.regex.Matcher matcher = pattern.matcher(link);
            if (matcher.find()) {
                String uri = matcher.group(1);
                String type = matcher.group(2);
                if (type.equals("studentSchoolAssociation")) {
                    StudentSchoolAssociation ssa = resource.uri(new URI(uri)).accept(format)
                            .get(StudentSchoolAssociation.class);
                    ssas.add(ssa);
                }
            }
        }
        return ssas;
    }
    
    private static List<School> findSchoolsByStudent(WebResource resource, String format, int studentId)
            throws Exception {
        List<School> schools = new LinkedList<School>();
        ClientResponse response = resource.path("students").path(studentId + "").path("schools").accept(format)
                .get(ClientResponse.class);
        for (String link : response.getHeaders().get("Link")) {
            Pattern pattern = Pattern.compile("<(.+)>;rel=(.+)");
            java.util.regex.Matcher matcher = pattern.matcher(link);
            if (matcher.find()) {
                String uri = matcher.group(1);
                String type = matcher.group(2);
                if (type.equals("studentSchoolAssociations")) {
                    ClientResponse response2 = resource.uri(new URI(uri)).accept(format).get(ClientResponse.class);
                    for (String link2 : response2.getHeaders().get("Link")) {
                        Matcher matcher2 = pattern.matcher(link2);
                        if (matcher2.find()) {
                            String uri2 = matcher2.group(1);
                            String type2 = matcher2.group(2);
                            
                            if (type2.equals("school")) {
                                School school = resource.uri(new URI(uri2)).accept(format).get(School.class);
                                schools.add(school);
                            }
                        }
                    }
                }
            }
        }
        return schools;
    }
    
    private static List<Student> findStudentsBySchool(WebResource resource, String format, int schoolId)
            throws Exception {
        List<Student> students = new LinkedList<Student>();
        ClientResponse response = resource.path("schools").path(schoolId + "").path("students").accept(format)
                .get(ClientResponse.class);
        for (String link : response.getHeaders().get("Link")) {
            Pattern pattern = Pattern.compile("<(.+)>;rel=(.+)");
            java.util.regex.Matcher matcher = pattern.matcher(link);
            if (matcher.find()) {
                String uri = matcher.group(1);
                String type = matcher.group(2);
                if (type.equals("studentSchoolAssociations")) {
                    ClientResponse response2 = resource.uri(new URI(uri)).accept(format).get(ClientResponse.class);
                    for (String link2 : response2.getHeaders().get("Link")) {
                        Matcher matcher2 = pattern.matcher(link2);
                        if (matcher2.find()) {
                            String uri2 = matcher2.group(1);
                            String type2 = matcher2.group(2);
                            if (type2.equals("student")) {
                                Student student = resource.uri(new URI(uri2)).accept(format).get(Student.class);
                                students.add(student);
                            }
                            
                        }
                    }
                }
            }
        }
        return students;
    }
}
