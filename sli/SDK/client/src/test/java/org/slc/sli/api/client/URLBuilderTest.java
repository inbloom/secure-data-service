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


package org.slc.sli.api.client;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.client.util.URLBuilder;

/**
 * Unit test for URL builder
 *
 *
 */
public class URLBuilderTest {

    @Test
    public void testIds() throws Exception {
        URLBuilder builder = URLBuilder.create("http://localhost");
        List<String> ids = Arrays.asList("1", "2", "3", "4", "5");
        builder.addPath(PathConstants.API_SERVER_PATH).addPath(ResourceNames.STUDENTS).ids(ids);
        URL url = builder.build();
        assertEquals("the URL should be http://localhost/api/rest/v1/students/1,2,3,4,5", "http://localhost/"
                + PathConstants.API_SERVER_PATH + "/students/1,2,3,4,5", url.toString());
    }

    @Test(expected = MalformedURLException.class)
    public void testBuildException() throws Exception {
        URLBuilder builder = URLBuilder.create("");
        builder.addPath(PathConstants.API_SERVER_PATH).addPath(ResourceNames.STUDENTS);
        builder.build();
    }
    
    @Test
    public void testTypeToResourceConversion() throws Exception{
        URLBuilder builder = URLBuilder.create("http://localhost");
        builder.addPath(PathConstants.API_SERVER_PATH);
        builder.entityType("staffEducationOrganizationAssociation");
        URL url = builder.build();
        assertEquals("the url should be http://localhost/api/rest/v1/staffEducationOrgAssignmentAssociations", "http://localhost/"
                + PathConstants.API_SERVER_PATH + "/staffEducationOrgAssignmentAssociations", url.toString());
    }
}
