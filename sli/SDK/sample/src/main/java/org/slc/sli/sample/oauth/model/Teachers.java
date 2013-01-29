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


package org.slc.sli.sample.oauth.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;
import org.slc.sli.api.client.impl.GenericEntity;

/**
 * domain wrapper for teacher and staff with authorized roles and access right information
 *
 * @author dliu
 *
 */
public class Teachers {

    private static final Logger LOG = LoggerFactory.getLogger(Teachers.class);

    @SuppressWarnings("unchecked")
    public static Map<String, String> getTenantIdMap(BasicClient client) throws IOException {
        Entity home = new GenericEntity(PathConstants.HOME, new HashMap<String, Object>());
        try {
            client.getHomeResource(home);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        Map<String, URL> linkMap = home.getLinkMap();
        URL myURL = null;

        if (linkMap != null) {
            myURL = linkMap.get("self");
        }

        if (myURL == null) {
            return null;
        }

        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.getResource(collection, myURL, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        }
        Map<String, String> toReturn = new HashMap<String, String>();
        if (collection != null && collection.size() >= 1) {
            String firstName = (String) ((Map<String, Object>) collection.get(0).getData().get("name"))
                    .get("firstName");
            String lastName = (String) ((Map<String, Object>) collection.get(0).getData().get("name"))
                    .get("lastSurname");
            String tenantId = (String) collection.get(0).getData().get("id");
            toReturn.put(firstName + " " + lastName, tenantId);
        }
        return toReturn;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRoles(BasicClient client) throws IOException {
        List<String> roles = new ArrayList<String>();
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, PathConstants.SECURITY_SESSION_DEBUG, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        } catch (SLIClientException e) {
            // the read was unsucessful
            LOG.error("Exception occurred", e);
        }


        if (collection != null && collection.size() >= 1) {
            Map<String, Object> auth = (Map<String, Object>) collection.get(0).getData().get("authentication");
            Map<String, Object> principal = (Map<String, Object>) auth.get("principal");
            roles = (List<String>) principal.get("roles");
        }
        return roles;
    }

    @SuppressWarnings("unchecked")
    public static List<String> getAccessRights(BasicClient client) throws IOException {
        List<String> accessRights = new ArrayList<String>();
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, PathConstants.SECURITY_SESSION_DEBUG, BasicQuery.EMPTY_QUERY);
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        } catch (SLIClientException e) {
            // the read was unsucessful
            LOG.error("Exception occurred", e);
        }


        if (collection != null && collection.size() >= 1) {
            Map<String, Object> auth = (Map<String, Object>) collection.get(0).getData().get("authentication");
            accessRights = (List<String>) auth.get("authorities");
        }
        return accessRights;
    }
}
