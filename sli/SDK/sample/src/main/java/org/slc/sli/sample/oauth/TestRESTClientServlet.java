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

package org.slc.sli.sample.oauth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.RESTClient;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.util.Query;
import org.slc.sli.api.client.util.URLBuilder;

/**
 * Servlet that do CRUD test against Java SDK via RESTClient interface.
 *
 * This is almost a clone of TestSDKServlet
 *
 * @author ycao
 *
 */
public class TestRESTClientServlet extends HttpServlet {

    private static final long serialVersionUID = 6114075027060L;
    private static final Logger LOG = LoggerFactory.getLogger(TestRESTClientServlet.class);

    private static final String SUCCEED = "succeed";
    private static final String FAILED = "failed";

    // build the test student entity that can pass schema validation
    private static final String STUDENT_MONIQUE =
            "{                                                                  "
                    + "    \"studentUniqueStateId\":\"123456\",                           "
                    + "    \"sex\":\"Female\",                                            "
                    + "    \"economicDisadvantaged\":false,                               "
                    + "    \"address\":                                                   "
                    + "        [ {                                                        "
                    + "            \"nameOfCounty\":\"Orange\",                           "
                    + "            \"apartmentRoomSuiteNumber\":\"200\",                  "
                    + "            \"postalCode\":\"45678\",                              "
                    + "            \"streetNumberName\":\"817 Oakridge Farm Lane\",       "
                    + "            \"stateAbbreviation\":\"CA\",                          "
                    + "            \"addressType\":\"Physical\",                          "
                    + "            \"city\":\"Los Angeles\"                               "
                    + "        } ],                                                       "
                    + "    \"name\":                                                      "
                    + "        {                                                          "
                    + "            \"middleName\":\"L\",                                  "
                    + "            \"lastSurname\":\"Christie\",                          "
                    + "            \"firstName\":\"Monique\"                              "
                    + "        },                                                         "
                    + "    \"birthData\":                                                 "
                    + "        {\"birthDate\":\"1993-12-31\"}                             "
                    + "}                                                                  ";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RESTClient client = ((BasicClient) req.getSession().getAttribute("client")).getRESTClient();
        String testType = req.getParameter("test");
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
        req.setAttribute("testResult", testResult);
        req.getRequestDispatcher("WEB-INF/restsdktest.jsp").forward(req, resp);
    }

    // the read test has been done in list student, so always return succeed
    private String testRead(RESTClient client) {
        return SUCCEED;
    }

    // test the create for Java SDK
    @SuppressWarnings("unchecked")
    private String testCreate(RESTClient client) {

        String[] inDB = persistAndRead(STUDENT_MONIQUE, client, ResourceNames.STUDENTS);
        String studentBody = inDB[1];

        if (studentBody.indexOf("\"lastSurname\":\"Christie\"") > -1 && studentBody.indexOf("\"firstName\":\"Monique\"") > -1) {
            return SUCCEED;
        }

        return FAILED;
    }

    // test the update for Java SDK
    @SuppressWarnings("unchecked")
    private String testUpdate(RESTClient client) {

        String[] inDB = persistAndRead(STUDENT_MONIQUE, client, ResourceNames.STUDENTS);
        String resourceLocation = inDB[0];
        String studentBody = inDB[1];

        String updatedMonique = studentBody.replaceAll("817 Oakridge Farm Lane", "2817 New Found Lane");

        try {
            URL resourceURL = new URL(resourceLocation);

            Response response = client.putRequest(resourceURL, updatedMonique);
            response = client.getRequest(resourceURL);

            String updated = response.readEntity(String.class);
            if (updated.indexOf("2817 New Found Lane") > -1) {
                return SUCCEED;
            }

        } catch (MalformedURLException e) {
            //SUPPRESS
        } catch (URISyntaxException e) {
            //SUPPRESS
        }

        return FAILED;
    }

    // test the delete of Java SDK
    private String testDelete(RESTClient client) {

        try {
            String resourceLocation = persistInMongo(STUDENT_MONIQUE, client, ResourceNames.STUDENTS);
            if (resourceLocation != null) {
                URL resourceURL = new URL(resourceLocation);

                Response response = client.deleteRequest(resourceURL);
                if (!requestSucceed(response)) {
                    return FAILED;
                }

                response = client.getRequest(resourceURL);
                if (response.getStatus() != Status.NOT_FOUND.getStatusCode()) {
                    return FAILED;
                }

                return SUCCEED;
            }
        } catch (MalformedURLException e) {
            //SUPPRESS
        } catch (URISyntaxException e) {
            //SUPPRESS
        }

        return FAILED;
    }

    // test query and sorting of Java SDK
    @SuppressWarnings("unchecked")
    private String testQuery(RESTClient client) {

        Query query = BasicQuery.Builder.create().filterEqual("sex", "Male")
                .sortBy("name.firstName").sortDescending().build();

        URLBuilder urlBuilder = URLBuilder.create(client.getBaseURL()).entityType(ResourceNames.TEACHERS);
        urlBuilder.query(query);

        try {
            URL url = urlBuilder.build();
            Response response = client.getRequest(url);
            String result = response.readEntity(String.class);

            result = result.substring(result.indexOf("firstName\":\"") + 12);
            result = result.substring(0, result.indexOf("\""));

            if ("Mark".equals(result)) {
                return SUCCEED;
            }
        } catch (MalformedURLException e) {
            //SUPPRESS
        } catch (URISyntaxException e) {
            //SUPPRESS
        }

        return FAILED;
    }


    private String[] persistAndRead(String jsonObj, RESTClient client, String resourceType) {

        String[] result = new String[2];
        Response response = null;
        try {

            String location = persistInMongo(jsonObj, client, resourceType);
            if (location == null) {
                return null;
            }
            result[0] = location;

            response = client.getRequest(new URL(location));
            if (response == null || !requestSucceed(response)) {
                return null;
            }

        } catch (MalformedURLException e1) {
            return null;
        } catch (URISyntaxException e1) {
            return null;
        }

        result[1] = response.readEntity(String.class);

        return result;
    }


    private String persistInMongo(String jsonObj, RESTClient client, String resourceType) throws MalformedURLException, URISyntaxException {

        URL url = URLBuilder.create(client.getBaseURL()).entityType(resourceType).build();
        Response response = client.postRequest(url, jsonObj);

        if (response.getStatus() != Status.CREATED.getStatusCode()) {
            return null;
        }

        return getLocation(response);
    }


    private String getLocation(Response response) {
        return response.getHeaders().getHeaderValues("Location").get(0);
    }


    private String getId(String location) {
        return location.substring(location.lastIndexOf("/") + 1);
    }


    private boolean requestSucceed(Response response) {
        //2xx are successful status
        return response.getStatus() >= 200 && response.getStatus() < 300;
    }
}
