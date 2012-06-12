package org.slc.sli.shtick.test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;
import org.slc.sli.api.client.util.URLBuilder;

/**
 * Servlet for testing API crud consistency
 * using shtick (?) // TODO: update javadoc
 *
 * WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP
 *
 * @author chung
 */
public class CrudConsistencyTestServlet extends HttpServlet {

    private BasicClient client;
    private URL apiUrl;
    private URL callbackUrl;

    // wrong, don't use
    private final String clientId = "12345";
    private final String clientSecret = "41ehu3o2hr81";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: once we have the actual harness, tie the harness in
        apiUrl = URLBuilder.create("http://local.slidev.org:8080").build();
        callbackUrl = URLBuilder.create("http://local.slidev.org:8081/sample/callback").build();
        client = new BasicClient(apiUrl, clientId, clientSecret, callbackUrl);

        String testResult = "";
        String testType = req.getParameter("testType");
        if (testType == null) {
            throw new ServletException("Parameter \"testType\" not specified.");
        }
        if (testType.equals("create")) {
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
            throw new ServletException(String.format("Unknown test type: %s", testType));
        }

        req.setAttribute("testResult", testResult);
        req.getRequestDispatcher("WEB-INF/result.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private String testCreate() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestStudentBody());
        try {
            Response response = client.create(student);
            List<Entity> collection = new ArrayList<Entity>();
            if (response.getStatus() != 201) {
                return TestResultConstants.ERROR_201;
            }
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            String id = location.substring(location.lastIndexOf("/") + 1);
            response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (response.getStatus() != 200) {
                return TestResultConstants.ERROR_200;
            }
            if (collection != null && collection.size() == 1) {
                String firstName = ((Map<String, String>) collection.get(0).getData().get("name")).get("firstName");
                String lastSurname = ((Map<String, String>) collection.get(0).getData().get("name")).get("lastSurname");
                if (!(firstName.equals("Monique") && lastSurname.equals("Johnson"))) {
                    return "Failed - Response contains incorrect values";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        return TestResultConstants.PASSED;
    }

    private String testRead() {
        // already tested read in other tests
        return TestResultConstants.PASSED;
    }

    private String testUpdate() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestStudentBody());
        try {
            Response response = client.create(student);
            List<Entity> collection = new ArrayList<Entity>();
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            String id = location.substring(location.lastIndexOf("/") + 1);
            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                ((List<Map<String, String>>) student.getData().get("address")).get(0).put("streetNumberName",
                        "2817 Oakridge Farm Lane");
                response = client.update(student);
                if (response.getStatus() != 204) {
                    return TestResultConstants.ERROR_204;
                }
            }
            response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (response.getStatus() != 200) {
                return TestResultConstants.ERROR_200;
            }
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                String address = ((List<Map<String, String>>) student.getData().get("address")).get(0)
                        .get("streetNumberName");
                if (!address.equals("2817 Oakridge Farm Lane")) {
                    return "Failed - Response contains incorrect values";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        return TestResultConstants.PASSED;
    }

    private String testDelete() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestStudentBody());
        try {
            Response response = client.create(student);
            List<Entity> collection = new ArrayList<Entity>();
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            String id = location.substring(location.lastIndexOf("/") + 1);
            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                response = client.delete(student);
                if (response.getStatus() != 204) {
                    return TestResultConstants.ERROR_204;
                }
            }
            response = client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (!(response.getStatus() == 404)) {
                return TestResultConstants.ERROR_404;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        return TestResultConstants.PASSED;
    }

    private String testAssociationCrud() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestStudentBody());
        Entity school = new GenericEntity(ResourceNames.SCHOOLS, createTestSchoolBody());
        try {
            Response response = client.create(student);
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            String studentId = location.substring(location.lastIndexOf("/") + 1);

            response = client.create(school);
            location = response.getHeaders().getHeaderValues("Location").get(0);
            String schoolId = location.substring(location.lastIndexOf("/") + 1);

            // Create
            Entity studentSchoolAssoc = new GenericEntity(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS,
                    createTestStudentSchoolAssocBody(studentId, schoolId));
            response = client.create(studentSchoolAssoc);
            if (response.getStatus() != 201) {
                return TestResultConstants.ERROR_201;
            }

            // Read
            location = response.getHeaders().getHeaderValues("Location").get(0);
            String id = location.substring(location.lastIndexOf("/") + 1);
            List<Entity> collection = new ArrayList<Entity>();
            client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id, BasicQuery.EMPTY_QUERY);
            if (response.getStatus() != 200) {
                return TestResultConstants.ERROR_200;
            }
            if (collection != null && collection.size() == 1) {
                studentSchoolAssoc = collection.get(0);
                String entryDate = ((Map<String, String>) studentSchoolAssoc.getData().get("entryDate")).toString();
                if (!entryDate.equals("2011-09-01")) {
                    return "Failed - Response contains incorrect values";
                }
            }

            // Update
            studentSchoolAssoc.getData().put("entryDate", "2011-10-01");
            response = client.update(studentSchoolAssoc);
            if (response.getStatus() != 204) {
                return TestResultConstants.ERROR_204;
            }
            client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                studentSchoolAssoc = collection.get(0);
                String entryDate = ((Map<String, String>) studentSchoolAssoc.getData().get("entryDate")).toString();
                if (!entryDate.equals("2011-10-01")) {
                    return "Failed - Response contains incorrect values";
                }
            }

            // Delete
            response = client.delete(studentSchoolAssoc);
            if (response.getStatus() != 204) {
                return TestResultConstants.ERROR_204;
            }
            response = client.read(collection, ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, id, BasicQuery.EMPTY_QUERY);
            if (response.getStatus() != 404) {
                return TestResultConstants.ERROR_404;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }

        return TestResultConstants.PASSED;
    }

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

    private Map<String, Object> createTestSchoolBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        List<String> schoolCategories = new ArrayList<String>();
        schoolCategories.add("High School");
        body.put("nameOfInstitution", "Test Elementary School");
        body.put("schoolCategories", schoolCategories);

        return body;
    }

    private Map<String, Object> createTestStudentSchoolAssocBody(String studentId, String schoolId) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentId", studentId);
        body.put("schoolId", schoolId);
        body.put("entryDate", "2011-09-01");

        return body;
    }
}