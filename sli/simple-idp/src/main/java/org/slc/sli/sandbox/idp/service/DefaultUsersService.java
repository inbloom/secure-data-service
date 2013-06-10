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

package org.slc.sli.sandbox.idp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Returns list of the default users
 */
@Component
public class DefaultUsersService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUsersService.class);

    @Value("${sli.simple-idp.sandbox.users}")
    private String datasetList;

    private List<Dataset> datasets;
    private Map<String, List<DefaultUser>> userLists;

    /**
     * For unit tests
     * @param datasetList
     */
    void setDatasetList(String datasetList) {
        this.datasetList = datasetList;
    }

    @PostConstruct
    public void initDatasets(){
        datasets = new ArrayList<Dataset>();
        if (datasetList != null) {
            String[] values = datasetList.split(",");
            for (int i = 0; i < values.length; i += 2) {
                String key = values[i];
                datasets.add(new Dataset(key, values[i + 1]));
            }
        }
    }

    @PostConstruct
    public void initUserLists(){
        userLists = new HashMap<String, List<DefaultUser>>();
        if (datasetList != null) {
            for (Dataset dataset : datasets) {
                String key = dataset.getKey();
                userLists.put(key, buildUserList(key));
            }
        }
    }

    /**
     * Get the list of datasets that are configured
     * @return
     */
    public List<Dataset> getAvailableDatasets() {
        return datasets;
    }

    /**
     * Returns available default users for provided dataset
     */
    public List<DefaultUser> getUsers(String dataset) {
        return userLists.get(dataset);
    }

    /**
     * Get the user info for the given userId in the given dataset
     * @param dataset
     * @param userId
     * @return
     */
    public DefaultUser getUser(String dataset, String userId) {
        for (DefaultUser user : getUsers(dataset)) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    private List<DefaultUser> buildUserList(String dataset){
        List<DefaultUser> users = new ArrayList<DefaultUser>();
        try {
            String jsonData = readFile(dataset + ".json");
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonUser = jsonArray.getJSONObject(i);
                String userId = jsonUser.getString("userId");
                String name = jsonUser.getString("name");
                String type = jsonUser.getString("type");
                String role = jsonUser.getString("role");
                String association = jsonUser.getString("association");
                DefaultUser user = new DefaultUser(userId, type, name, role, association);
                users.add(user);
            }
        } catch (IOException e) {
            LOG.error("IOException building list of default sample dataset users", e);
        } catch (JSONException e) {
            LOG.error("JSONException building list of default sample dataset users", e);
        }
        return users;
    }

    private String readFile(String file) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(file)));
            String line = null;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Provided DataSets
     *
     */
    public static class Dataset {
        private String key;
        private String displayName;

        public Dataset(String key, String displayName) {
            this.key = key;
            this.displayName = displayName;
        }

        public String getKey() {
            return key;
        }

        public String getDisplayName() {
            return displayName;
        }

    }

    /**
     * DefaultUser information.
     */
    public static class DefaultUser {
        private String userId, type, name, role, association;

        public DefaultUser(String userId, String type, String name, String role, String association) {
            this.userId = userId;
            this.type = type;
            this.name = name;
            this.role = role;
            this.association = association;
        }

        public String getName() {
            return name;
        }

        public String getUserId() {
            return userId;
        }

        public String getType() {
            return type;
        }

        public String getRole() {
            return role;
        }

        public String getAssociation() {
            return association;
        }
    }
}
