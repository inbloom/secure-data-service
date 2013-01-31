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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.sample.oauth.model.Students;
import org.slc.sli.sample.oauth.model.Teachers;

/**
 * Sample servlet that returns student data.
 */
public class StudentServlet extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 818013212675276117L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicClient client = (BasicClient) req.getSession().getAttribute("client");
        List<String> names = Students.getNames(client);
        Map<String, String> tenantIdMap = Teachers.getTenantIdMap(client);
        Map<String, String> firstOne = new HashMap<String, String>();
        List<String> roles = Teachers.getRoles(client);
        List<String> accessRights = Teachers.getAccessRights(client);
        if (tenantIdMap != null && tenantIdMap.size() >= 1) {
            String firstKey = tenantIdMap.keySet().iterator().next();
            firstOne.put(firstKey, tenantIdMap.get(firstKey));
        }
        Map<String, Integer> grades = new HashMap<String, Integer>();
        for (String name : names) {
            grades.put(name, Students.getGrade(client, name));
        }
        req.setAttribute("tenantMap", firstOne);
        req.setAttribute("grades", grades);
        req.setAttribute("roles", roles);
        req.setAttribute("accessRights", accessRights);
        req.getRequestDispatcher("WEB-INF/students.jsp").forward(req, resp);
    }

}
