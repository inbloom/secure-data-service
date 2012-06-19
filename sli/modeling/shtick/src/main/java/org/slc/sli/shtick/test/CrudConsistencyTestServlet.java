package org.slc.sli.shtick.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.shtick.StandardLevel3ClientManual;
import org.slc.sli.shtick.pojo.Name;
import org.slc.sli.shtick.pojo.SexType;
import org.slc.sli.shtick.pojo.Student;
import org.slc.sli.shtick.pojomanual.NameManual;
import org.slc.sli.shtick.pojomanual.StudentManual;

/**
 * Servlet for testing API crud consistency
 * using shtick (?) // TODO: update javadoc
 *
 * WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP
 *
 * @author chung
 */
@SuppressWarnings("serial")
public class CrudConsistencyTestServlet extends HttpServlet {

    StandardLevel3ClientManual client;

    private final int rrogersAccessibleStudentsCount = 82;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        client = new StandardLevel3ClientManual(TestConstants.API_URL);

        String testResult = "";
        String testType = req.getParameter("testType");
        if (testType == null) {
            testResult = TestResultConstants.PARAMETER_TEST_TYPE_ERROR;
        } else if (testType.equals("create")) {
            testResult = testCreate();
        } else if (testType.equals("read")) {
            testResult = testRead();
        } else if (testType.equals("update")) {
            testResult = testUpdate();
        } else if (testType.equals("delete")) {
            testResult = testDelete();
        } else if (testType.equals("assoc-crud")) {
            testResult = testAssociationCrud();
        } else {
            testResult = String.format(TestResultConstants.UNKNOWN_TEST_TYPE_ERROR, testType);
        }

        req.setAttribute("testResult", testResult);
        req.getRequestDispatcher("WEB-INF/result.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String testCreate() {
        // RestEntity student = new RestEntity(ResourceNames.STUDENTS, createTestStudentBody());
        // try {
        // Response response = client.create(student);
        // List<RestEntity> collection = new ArrayList<RestEntity>();
        // if (response.getStatus() != 201) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 201, response.getStatus());
        // }
        // String location = response.getHeaders().getHeaderValues("Location").get(0);
        // String id = location.substring(location.lastIndexOf("/") + 1);
        // response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
        // if (response.getStatus() != 200) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 200, response.getStatus());
        // }
        // if (collection != null && collection.size() == 1) {
        // @SuppressWarnings("unchecked")
        // String firstName = ((Map<String, String>)
        // collection.get(0).getData().get("name")).get("firstName");
        // @SuppressWarnings("unchecked")
        // String lastSurname = ((Map<String, String>)
        // collection.get(0).getData().get("name")).get("lastSurname");
        // if (!(firstName.equals("Monique") && lastSurname.equals("Johnson"))) {
        // return "Failed - Response contains incorrect values";
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        // }

        return TestResultConstants.PASSED;
    }

    private String testRead() {
        List<String> idList = new ArrayList<String>();
        idList.add(TestConstants.MARVIN_MILLER_ID);
        try {
            List<StudentManual> students = client.getStudentsById(TestConstants.RICK_ROGERS_TOKEN, idList,
                    Collections.<String, Object> emptyMap());
            if (students.size() != 1) {
                return String.format(TestResultConstants.ERROR_GENERIC,
                        String.format("received %s student record(s)", students.size()));
            } else {
                StudentManual student = students.get(0);
                String errorMsg = "";
                NameManual name = student.getName();
                if (!name.getFirstName().getValue().equals("Marvin")) {
                    errorMsg = errorMsg + String.format(TestResultConstants.ERROR_GENERIC,
                            "First name does not match.\n");
                }
                if (!name.getLastSurname().getValue().equals("Miller")) {
                    errorMsg = errorMsg + String.format(TestResultConstants.ERROR_GENERIC,
                            "Last name does not match.\n");
                }
                SexType sexType = student.getSex();
                if (!sexType.getName().equals("Male")) {
                    errorMsg = errorMsg + String.format(TestResultConstants.ERROR_GENERIC,
                            "Sex does not match.\n");
                }
                // we can check more fields here if we wish
                if (!errorMsg.isEmpty()) {
                    return errorMsg;
                }
            }

            students = client.getStudents(TestConstants.RICK_ROGERS_TOKEN, Collections.<String, Object> emptyMap());
            if (students.size() != rrogersAccessibleStudentsCount) {
                return String.format(TestResultConstants.ERROR_GENERIC,
                        String.format("received %s student record(s)", students.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        }

        return TestResultConstants.PASSED;
    }

    private String testUpdate() {
        // RestEntity student = new RestEntity(ResourceNames.STUDENTS, createTestStudentBody());
        // try {
        // Response response = client.create(student);
        // List<Entity> collection = new ArrayList<Entity>();
        // String location = response.getHeaders().getHeaderValues("Location").get(0);
        // String id = location.substring(location.lastIndexOf("/") + 1);
        // client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
        // if (collection != null && collection.size() == 1) {
        // student = collection.get(0);
        // ((List<Map<String, String>>)
        // student.getData().get("address")).get(0).put("streetNumberName",
        // "2817 Oakridge Farm Lane");
        // response = client.update(student);
        // if (response.getStatus() != 204) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 204, response.getStatus());
        // }
        // }
        // response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
        // if (response.getStatus() != 200) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 200, response.getStatus());
        // }
        // if (collection != null && collection.size() == 1) {
        // student = collection.get(0);
        // String address = ((List<Map<String, String>>)
        // student.getData().get("address")).get(0).get(
        // "streetNumberName");
        // if (!address.equals("2817 Oakridge Farm Lane")) {
        // return "Failed - Response contains incorrect values";
        // }
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        // }

        return TestResultConstants.PASSED;
    }

    private String testDelete() {
        // RestEntity student = new RestEntity(ResourceNames.STUDENTS, createTestStudentBody());
        // try {
        // Response response = client.create(student);
        // List<Entity> collection = new ArrayList<Entity>();
        // String location = response.getHeaders().getHeaderValues("Location").get(0);
        // String id = location.substring(location.lastIndexOf("/") + 1);
        // client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
        // if (collection != null && collection.size() == 1) {
        // student = collection.get(0);
        // response = client.delete(student);
        // if (response.getStatus() != 204) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 204, response.getStatus());
        // }
        // }
        // response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
        // if (!(response.getStatus() == 404)) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 404, response.getStatus());
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        // }

        return TestResultConstants.PASSED;
    }

    private String testAssociationCrud() {
        // RestEntity student = new RestEntity(ResourceNames.STUDENTS, createTestStudentBody());
        // RestEntity school = new RestEntity(ResourceNames.SCHOOLS, createTestSchoolBody());
        // try {
        // Response response = client.create(student);
        // String location = response.getHeaders().getHeaderValues("Location").get(0);
        // String studentId = location.substring(location.lastIndexOf("/") + 1);
        //
        // response = client.create(school);
        // location = response.getHeaders().getHeaderValues("Location").get(0);
        // String schoolId = location.substring(location.lastIndexOf("/") + 1);
        //
        // // Create
        // Entity studentSchoolAssoc = new GenericEntity(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS,
        // createTestStudentSchoolAssocBody(studentId, schoolId));
        // response = client.create(studentSchoolAssoc);
        // if (response.getStatus() != 201) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 201, response.getStatus());
        // }
        //
        // // Read
        // location = response.getHeaders().getHeaderValues("Location").get(0);
        // String id = location.substring(location.lastIndexOf("/") + 1);
        // List<Entity> collection = new ArrayList<Entity>();
        // client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id,
        // BasicQuery.EMPTY_QUERY);
        // if (response.getStatus() != 200) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 200, response.getStatus());
        // }
        // if (collection != null && collection.size() == 1) {
        // studentSchoolAssoc = collection.get(0);
        // @SuppressWarnings("unchecked")
        // String entryDate = ((Map<String, String>)
        // studentSchoolAssoc.getData().get("entryDate")).toString();
        // if (!entryDate.equals("2011-09-01")) {
        // return "Failed - Response contains incorrect values";
        // }
        // }
        //
        // // Update
        // studentSchoolAssoc.getData().put("entryDate", "2011-10-01");
        // response = client.update(studentSchoolAssoc);
        // if (response.getStatus() != 204) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 204, response.getStatus());
        // }
        // client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id,
        // BasicQuery.EMPTY_QUERY);
        // if (collection != null && collection.size() == 1) {
        // studentSchoolAssoc = collection.get(0);
        // @SuppressWarnings("unchecked")
        // String entryDate = ((Map<String, String>)
        // studentSchoolAssoc.getData().get("entryDate")).toString();
        // if (!entryDate.equals("2011-10-01")) {
        // return "Failed - Response contains incorrect values";
        // }
        // }
        //
        // // Delete
        // response = client.delete(studentSchoolAssoc);
        // if (response.getStatus() != 204) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 204, response.getStatus());
        // }
        // response = client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id,
        // BasicQuery.EMPTY_QUERY);
        // if (response.getStatus() != 404) {
        // return String.format(TestResultConstants.STATUS_CODE_ERROR, 404, response.getStatus());
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        // }

        return TestResultConstants.PASSED;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> createTestStudentBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, String> name = new HashMap<String, String>();
        name.put("firstName", "Monique");
        name.put("lastSurname", "Johnson");
        name.put("middleName", "L");
        body.put("name", name);
        body.put("sex", "Female");
        Map<String, String> birthDate = new HashMap<String, String>();
        birthDate.put("birthDate", "1995-01-01");
        body.put("birthData", birthDate);
        body.put("studentUniqueStateId", "123456");
        body.put("economicDisadvantaged", false);
        List<Map<String, String>> addresses = new ArrayList<Map<String, String>>();
        Map<String, String> address = new HashMap<String, String>();
        address.put("addressType", "Physical");
        address.put("streetNumberName", "817 Oakridge Farm Lane");
        address.put("apartmentRoomSuiteNumber", "200");
        address.put("city", "NY");
        address.put("stateAbbreviation", "NY");
        address.put("postalCode", "12345");
        address.put("nameOfCounty", "Wake");
        addresses.add(address);
        body.put("address", addresses);

        return body;
    }

    @SuppressWarnings("unused")
    private Student createTestPojoStudent() {
        // FIXME:
        throw new UnsupportedOperationException("TODO");
        // Student student = new Student("11111111-1111-1111-1111-111111111111", new Name("Monique",
        // "Johnson"), "Female",
        // false, "123456");
        // return student;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> createTestSchoolBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        List<String> schoolCategories = new ArrayList<String>();
        schoolCategories.add("High School");
        body.put("nameOfInstitution", "Test Elementary School");
        body.put("schoolCategories", schoolCategories);

        return body;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> createTestStudentSchoolAssocBody(String studentId, String schoolId) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", studentId);
        body.put("schoolId", schoolId);
        body.put("entryDate", "2011-09-01");

        return body;
    }
}