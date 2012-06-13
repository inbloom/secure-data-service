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
 * Servlet for field level API testing
 * using shtick (?)
 * Refer to SimpleTypes.xsd/ComplexTypes.xsd
 * for field constraints // TODO: Update javadoc
 *
 * WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP WIP
 *
 * @author chung
 */
public class FieldLevelTestServlet extends HttpServlet {
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
            testResult = TestResultConstants.PARAMETER_TEST_TYPE_ERROR;
        } else if (testType.equals("create")) {
            testResult = testCreateInvalidData();
        } else if (testType.equals("update")) {
            testResult = testUpdateInvalidData();
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

    private String testCreateInvalidData() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestInvalidStudentBody());
        try {
            Response response = client.create(student);
            if (response.getStatus() != 400) {
                return String.format(TestResultConstants.STATUS_CODE_ERROR, 400, response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        }

        return TestResultConstants.PASSED;
    }

    private String testUpdateInvalidData() {
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createTestValidStudentBody());
        try {
            Response response = client.create(student);
            String location = response.getHeaders().getHeaderValues("Location").get(0);
            String id = location.substring(location.lastIndexOf("/") + 1);
            List<Entity> collection = new ArrayList<Entity>();
            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            student = collection.get(0);
            student.getData().put("sex", "Neutral");
            response = client.update(student);
            if (response.getStatus() != 400) {
                return String.format(TestResultConstants.STATUS_CODE_ERROR, 400, response.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return String.format(TestResultConstants.EXCEPTION_GENERIC, e.toString());
        }

        return TestResultConstants.PASSED;
    }

    private Map<String, Object> createTestValidStudentBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, String> name = new HashMap<String, String>();
        name.put("firstName", "Monique");
        name.put("middleName", "L");
        body.put("name", name);
        body.put("sex", "Female");
        Map<String, String> birthDate = new HashMap<String, String>();
        birthDate.put("birthDate", "1995-02-03");
        body.put("birthData", birthDate);
        body.put("studentUniqueStateId", "123456");
        body.put("economicDisadvantaged", false);

        return body;
    }

    private Map<String, Object> createTestInvalidStudentBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        Map<String, String> name = new HashMap<String, String>();
        name.put("firstName", "Monique");
        name.put("middleName", "L");
        body.put("name", name); // invalid: missing last name
        body.put("sex", "Neutral"); // invalid sex type
        Map<String, String> birthDate = new HashMap<String, String>();
        birthDate.put("birthDate", "1995-02-30"); // invalid date
        body.put("birthData", birthDate);
        body.put("studentUniqueStateId", "123456");
        body.put("economicDisadvantaged", false);

        return body;
    }
}
