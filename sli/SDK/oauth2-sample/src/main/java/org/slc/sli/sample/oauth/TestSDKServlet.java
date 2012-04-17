package org.slc.sli.sample.oauth;

import java.io.IOException;
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
import org.slc.sli.api.client.EntityCollection;
import org.slc.sli.api.client.EntityType;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Servlet that test against SDK.
 */
public class TestSDKServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicClient client = (BasicClient) req.getSession().getAttribute("client");
        String testType = (String) req.getParameter("test");
        String testResult = "failed";
        if (testType.equals("read")) {
            testResult = testRead(client);
        } else if (testType.equals("create")) {
            testResult = testCreate(client);
        } else if (testType.equals("update")) {
            testResult = testUpdate(client);
        } else if (testType.equals("delete")) {
            testResult = testDelete(client);
        } else if (testType.equals("query")) {
            testResult = testQuery(client);
        } else {
            testResult = "unsupported test!";
        }
        resp.getWriter().print(testResult);
    }
    
    private String testRead(BasicClient client) {
        String testResult = "succeed";
        return testResult;
    }
    
    @SuppressWarnings("unchecked")
    private String testCreate(BasicClient client) {
        String testResult = "failed";
        String id = "";
        Map<String, Object> studentBody = createStudentBody();
        Entity student = new GenericEntity(EntityType.STUDENTS, studentBody);
        EntityCollection collection = new EntityCollection();
        try {
            Response response = client.create(student);
            if (response.getStatus() != 201) {
                testResult = "failed";
                return testResult;
            }
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            id = location.substring(location.lastIndexOf("/") + 1);
            client.read(collection, EntityType.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {

                String firstName = ((Map<String, String>) (collection.get(0).getData().get("name"))).get("firstName");
                String lastSurname = ((Map<String, String>) (collection.get(0).getData().get("name")))
                        .get("lastSurname");
                if (firstName.equals("Monique") && lastSurname.equals("Johnson")) {
                    testResult = "succeed";
                } else {
                    testResult = "failed";
                }
            }
        } catch (Exception e) {
            testResult = "failed";
        }
        return testResult;
    }
    
    @SuppressWarnings("unchecked")
    private String testUpdate(BasicClient client) {
        String testResult = "failed";
        String id = "";
        Map<String, Object> studentBody = createStudentBody();
        Entity student = new GenericEntity(EntityType.STUDENTS, studentBody);
        EntityCollection collection = new EntityCollection();
        try {
            Response response = client.create(student);
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            id = location.substring(location.lastIndexOf("/") + 1);
            client.read(collection, EntityType.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                ((List<Map<String, String>>) (student.getData().get("address"))).get(0).put("streetNumberName",
                        "2817 Oakridge Farm Lane");
                response = client.update(student);
                if (response.getStatus() != 204) {
                    testResult = "failed";
                    return testResult;
                }
            }
            client.read(collection, EntityType.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                String address = ((List<Map<String, String>>) (student.getData().get("address"))).get(0).get(
                        "streetNumberName");
                if (address.equals("2817 Oakridge Farm Lane")) {
                    testResult = "succeed";
                } else {
                    testResult = "failed";
                }
            }
        } catch (Exception e) {
            testResult = "failed";
        }
        return testResult;
    }
    
    private String testDelete(BasicClient client) {
        String testResult = "failed";
        String id = "";
        Map<String, Object> studentBody = createStudentBody();
        Entity student = new GenericEntity(EntityType.STUDENTS, studentBody);
        EntityCollection collection = new EntityCollection();
        try {
            Response response = client.create(student);
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            id = location.substring(location.lastIndexOf("/") + 1);
            client.read(collection, EntityType.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection != null && collection.size() == 1) {
                student = collection.get(0);
                response = client.delete(student);
                if (response.getStatus() != 204) {
                    testResult = "failed";
                    return testResult;
                }
            }
            response = client.read(collection, EntityType.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (response.getStatus() == 404) {
                testResult = "succeed";
            } else {
                testResult = "failed";
                return testResult;
            }
            
        } catch (Exception e) {
            testResult = "failed";
        }
        return testResult;
    }
    
    @SuppressWarnings("unchecked")
    private String testQuery(BasicClient client) {
        EntityCollection collection = new EntityCollection();
        String testResult = "";
        try {
            client.read(collection, EntityType.TEACHERS,
                    BasicQuery.Builder.create().filterEqual("sex", "Male").sortBy("name.firstName").sortDescending()
                            .build());
            if (collection != null && collection.size() > 0) {
                String firstName = ((Map<String, String>) (collection.get(0).getData().get("name"))).get("firstName");
                if (firstName.equals("Stephen")) {
                    testResult = "succeed";
                } else {
                    testResult = "failed";
                    return testResult;
                }
            }
        } catch (Exception e) {
            testResult = "failed";
        }

        return testResult;
        
    }
    
    private Map<String, Object> createStudentBody() {
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
}
