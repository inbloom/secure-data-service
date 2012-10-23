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

package org.slc.sli.sandbox.idp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    
    
    void setDatasetList(String datasetList) {
        this.datasetList = datasetList;
    }

    /**
     * Holds role information.
     * 
     */
    public static class DefaultUser {
        String userId, name, role;
        
        public DefaultUser(String userId, String name, String role) {
            this.userId = userId;
            this.name = name;
            this.role = role;
        }
        
        public String getName() {
            return name;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public String getRole() {
            return role;
        }
        
    }
    
    public List<Dataset> getAvailableDatasets() {
        List<Dataset> result = new ArrayList<Dataset>();
        if (datasetList != null) {
            String[] values = datasetList.split(",");
            for (int i = 0; i < values.length; i += 2) {
                result.add(new Dataset(values[i], values[i + 1]));
            }
        }
        return result;
    }
    
    /**
     * Returns available default users for provided dataset
     * 
     * @throws IOException
     * @throws JSONException
     */
    public List<DefaultUser> getUsers(String dataset) {
        List<DefaultUser> users = new ArrayList<DefaultUser>();
        try {
            String jsonData = readFile(dataset + ".json");
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonUser = jsonArray.getJSONObject(i);
                String userId = jsonUser.getString("userId");
                String name = jsonUser.getString("name");
                String role = jsonUser.getString("role");
                DefaultUser user = new DefaultUser(userId, name, role);
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream(file)));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        
        return stringBuilder.toString();
    }
    
    public static class Dataset {
        String key;
        String displayName;
        
        public Dataset(String key, String displayName){
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

    public DefaultUser getUser(String dataset, String userId) {
        for(DefaultUser user : getUsers(dataset)){
            if(user.getUserId().equals(userId)){
                return user;
            }
        }
        return null;
    }
}
