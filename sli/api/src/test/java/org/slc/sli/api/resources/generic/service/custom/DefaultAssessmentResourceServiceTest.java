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

package org.slc.sli.api.resources.generic.service.custom;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.resources.generic.representation.Resource;
import org.slc.sli.api.resources.generic.service.DefaultResourceService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit Tests
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DefaultAssessmentResourceServiceTest {

    @Autowired
    private DefaultAssessmentResourceService defaultAssessmentResourceService; //class under test

    @Autowired
    @Qualifier("defaultResourceService")
    private DefaultResourceService resourceService;

    @Autowired
    private SecurityContextInjector injector;

    private java.net.URI requestURI;

    private static final String URI = "http://some.net/api/rest/v1/assessments";
    private Resource assessments;
    private Resource learningStandards;

    @Before
    public void setup() throws Exception {
        // inject administrator security context for unit testing
        injector.setAdminContextWithElevatedRights();

        assessments = new Resource("v1", "assessments");
        learningStandards = new Resource("v1", "learningStandards");
    }

    @Test
    public void testGetLearningStandards() throws URISyntaxException {
        String learningStandardId = resourceService.postEntity(learningStandards, createLearningStandardEntity());
        String assessmentId = resourceService.postEntity(assessments, createAssessmentEntity(learningStandardId));

        requestURI = new java.net.URI(URI + "/" + assessmentId + "/learningStandards");

        List<EntityBody> entityBodyList = defaultAssessmentResourceService.getLearningStandards(assessments, assessmentId, learningStandards, requestURI).getEntityBodyList();
        assertNotNull("Should return an entity", entityBodyList);
        assertEquals("Should match", 1, entityBodyList.size());
        assertEquals("Should match", "learningStandard", entityBodyList.get(0).get("entityType"));
    }

    private EntityBody createAssessmentEntity(String learningStandardId) {
        EntityBody assessmentItem = new EntityBody();
        assessmentItem.put("learningStandards", Arrays.asList(learningStandardId));

        EntityBody entity = new EntityBody();
        entity.put("assessmentTitle", "Test");
        entity.put("assessmentItem", Arrays.asList(assessmentItem));
        return entity;
    }

    private EntityBody createLearningStandardEntity() {
        EntityBody entity = new EntityBody();
        entity.put("somefield", "somevalue");
        return entity;
    }


}
