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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.impl.BasicClient;
import org.slc.sli.api.client.impl.BasicQuery;

/**
 * Sample domain wrapper.
 */
public class Students {

    private static final Logger LOG = LoggerFactory.getLogger(Students.class);

    @SuppressWarnings("unchecked")
    public static List<String> getNames(BasicClient client) throws IOException {
        List<Entity> collection = new ArrayList<Entity>();
        try {
            client.read(collection, ResourceNames.STUDENTS, BasicQuery.Builder.create().startIndex(0).maxResults(50)
                    .build());
        } catch (URISyntaxException e) {
            LOG.error("Exception occurred", e);
        } catch (SLIClientException e) {
            // the read was unsucessful
            LOG.error("Exception occurred", e);
        }
        ArrayList<String> toReturn = new ArrayList<String>();
        for (Entity student : collection) {
            String firstName = (String) ((Map<String, Object>) student.getData().get("name")).get("firstName");
            String lastName = (String) ((Map<String, Object>) student.getData().get("name")).get("lastSurname");
            toReturn.add(lastName + ", " + firstName);
        }
        return toReturn;
    }

    @SuppressWarnings("javadoc")
    public static int getGrade(BasicClient client, String studentName) {
        return 0;
    }

}
