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

package org.slc.sli.dashboard.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.client.Entity;
import org.slc.sli.api.client.SLIClient;
import org.slc.sli.api.client.SLIClientException;
import org.slc.sli.api.client.SLIClientFactory;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.util.Constants;

/**
 * @author: sashton
 */
public class SDKAPIClientTest {
    
    SLIClientFactory mockSliClientFactory = mock(SLIClientFactory.class);
    
    SLIClient mockSliClient = mock(SLIClient.class);
    
    SDKAPIClient sdkapiClient = new SDKAPIClient();
    
    private final String TOKEN = "TOKEN";
    private final String SCHOOL_ID = "SCHOOL_ID";
    private final String LEA_ID = "LEA_ID";
    private final String SEA_ID = "SEA_ID";
    
    @Before
    public void setup() {
        sdkapiClient.setClientFactory(mockSliClientFactory);
        
        try {
            when(mockSliClientFactory.getClientWithSessionToken(TOKEN)).thenReturn(mockSliClient);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void shouldGetCourseSectionMappings() throws SLIClientException, IOException, URISyntaxException {
        
        List<GenericEntity> sections = new ArrayList<GenericEntity>();
        sections.add(createSection("S1", "CO1"));
        sections.add(createSection("S2", "CO2"));
        
        List<Entity> courseOfferings = new ArrayList<Entity>();
        courseOfferings.add(createCourseOffering("CO1", "C1"));
        courseOfferings.add(createCourseOffering("CO2", "C2"));
        
        when(mockSliClient.read("/schools/SCHOOL_ID/courseOfferings/?limit=0")).thenReturn(courseOfferings);
        when(mockSliClient.read("/educationOrganizations/SCHOOL_ID"))
                .thenReturn(asList(createEdOrg(SCHOOL_ID, LEA_ID)));
        when(mockSliClient.read("/educationOrganizations/LEA_ID")).thenReturn(asList(createEdOrg(LEA_ID, SEA_ID)));
        when(mockSliClient.read("/educationOrganizations/SEA_ID")).thenReturn(asList(createEdOrg(SEA_ID, null)));
        when(mockSliClient.read("/educationOrganizations/SCHOOL_ID/courses?limit=0")).thenReturn(
                asList(createCourse("C1", "C1")));
        when(mockSliClient.read("/educationOrganizations/LEA_ID/courses?limit=0")).thenReturn(
                asList(createCourse("C2", "C1")));
        
        List<GenericEntity> result = sdkapiClient.getCourseSectionMappings(sections, SCHOOL_ID, TOKEN);
        
        Assert.assertEquals(2, result.size());
    }
    
    private GenericEntity createSection(String id, String courseOfferingId) {
        GenericEntity e = new GenericEntity();
        e.put(Constants.ATTR_ID, id);
        e.put(Constants.ATTR_COURSE_OFFERING_ID, courseOfferingId);
        return e;
    }
    
    private GenericEntity createCourseOffering(String id, String courseId) {
        GenericEntity e = new GenericEntity();
        e.put(Constants.ATTR_ID, id);
        e.put(Constants.ATTR_COURSE_ID, courseId);
        return e;
    }
    
    private GenericEntity createCourse(String id, String title) {
        GenericEntity e = new GenericEntity();
        e.put(Constants.ATTR_ID, id);
        e.put(Constants.ATTR_COURSE_TITLE, title);
        return e;
    }
    
    private GenericEntity createEdOrg(String id, String parentId) {
        GenericEntity e = new GenericEntity();
        e.put(Constants.ATTR_ID, id);
        e.put(Constants.ATTR_PARENT_EDORG, parentId);
        return e;
    }
    
    private List<Entity> asList(Entity e) {
        List<Entity> list = new ArrayList<Entity>();
        list.add(e);
        return list;
    }
    
}
