/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * Servlet that do CRUD test against Java SDK.
 *
 * @author dliu
 *
 */
public class TestSDKServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 3258845941340138511L;
    private static final Logger LOG = LoggerFactory.getLogger(TestSDKServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicClient client = (BasicClient) req.getSession().getAttribute("client");
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
        req.getRequestDispatcher("WEB-INF/sdktest.jsp").forward(req, resp);
    }

    // the read test has been done in list student, so always return succeed
    private String testRead(BasicClient client) {
        String testResult = "succeed";
        return testResult;
    }

    // test the create for Java SDK
    @SuppressWarnings("unchecked")
    private String testCreate(BasicClient client) {
        String testResult = "failed";
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createStudentBody());
        List<Entity> collection = new ArrayList<Entity>();
        try {
            String id = client.create(student);
            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection.size() == 1) {

                String firstName = ((Map<String, String>) collection.get(0).getData().get("name")).get("firstName");
                String lastSurname = ((Map<String, String>) collection.get(0).getData().get("name")).get("lastSurname");
                if (firstName.equals("Monique") && lastSurname.equals("Johnson")) {
                    testResult = "succeed";
                } else {
                    LOG.error("Wrong response:" + firstName + " " + lastSurname);
                    testResult = "failed";
                }
            }

        } catch (SLIClientException e) {
            LOG.error("Exception:" + e.getMessage());
        } catch (Exception e) {
            LOG.error("Exception:" + e.getMessage());
            testResult = "failed";
        }

        return testResult;
    }

    // test the update for Java SDK
    @SuppressWarnings("unchecked")
    private String testUpdate(BasicClient client) {
        String testResult = "failed";
        String id = "";
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createStudentBody());

        List<Entity> collection = new ArrayList<Entity>();
        try {
            id = client.create(student);

            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection.size() == 1) {
                Entity foundStudent = collection.get(0);
                Map<String, Object> studentData = foundStudent.getData();

                ((List<Map<String, String>>) studentData.get("address")).get(0).put("streetNumberName",
                        "2817 Oakridge Farm Lane");
                client.update(foundStudent);
            }
            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection.size() == 1) {
                student = collection.get(0);
                String address = ((List<Map<String, String>>) student.getData().get("address")).get(0).get(
                        "streetNumberName");
                if (address.equals("2817 Oakridge Farm Lane")) {
                    testResult = "succeed";
                } else {
                    testResult = "failed";
                }
            }
        } catch (SLIClientException e) {
            // either the update or read call failed
            LOG.error("SLIClientException:" + e.getMessage());
            testResult = "failed";
        } catch (Exception e) {
            LOG.error("RESPONSE:" + e.getMessage());
            testResult = "failed";
        }
        return testResult;
    }

    // test the delete of Java SDK
    private String testDelete(BasicClient client) {
        String testResult = "failed";
        String id = "";
        Entity student = new GenericEntity(ResourceNames.STUDENTS, createStudentBody());

        List<Entity> collection = new ArrayList<Entity>();
        try {
            id = client.create(student);

            client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
            if (collection.size() == 1) {
                student = collection.get(0);
                client.delete(ResourceNames.STUDENTS, id);
            }
            // make sure the read fails
            try {
                client.read(collection, ResourceNames.STUDENTS, id, BasicQuery.EMPTY_QUERY);
                testResult = "failed";
            } catch (SLIClientException e) {
                // unable to read therefore the delete succeded.
                testResult = "succeed";
            }
        } catch (SLIClientException e) {
            // either the create or delete failed
            LOG.error("Response:" + e.getMessage());
            testResult = "failed";
        } catch (Exception e) {
            LOG.error("RESPONSE:" + e.getMessage());
            testResult = "failed";
        }
        return testResult;
    }

    // test query and sorting of Java SDK
    @SuppressWarnings("unchecked")
    private String testQuery(BasicClient client) {
        List<Entity> collection = new ArrayList<Entity>();
        String testResult = "";
        try {
            client.read(collection, ResourceNames.STAFF,
                    BasicQuery.Builder.create().filterEqual("sex", "Male")
                                               .sortBy("name.firstName")
                                               .sortDescending()
                                               .build());
            if (collection.size() > 0) {
                String firstName = ((Map<String, String>) collection.get(0).getData().get("name")).get("firstName");
                if (firstName.equals("Rick")) {
                    testResult = "succeed";
                } else {
                    testResult = "failed";
                    return testResult;
                }
            }
        } catch (SLIClientException e) {
            LOG.error("RESPONSE:" + e.getMessage());
        } catch (Exception e) {
            LOG.error("RESPONSE:" + e.getMessage());
            testResult = "failed";
        }

        return testResult;
    }

    // build the test student entity that can pass schema validation
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
        body.put("studentUniqueStateId", UUID.randomUUID().toString().substring(0, 8));
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
