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
package org.slc.sli.api.security.roles;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.slc.sli.api.test.WebContextTestExecutionListener;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;



/**
 * @author: tke
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RightAccessValidatorTest {

    @Autowired
    SecurityContextInjector securityContextInjector;

    @Autowired
    private RightAccessValidator service;

    private static final String STUDENT_ID = "testStudentId";

    private static final String BAD_EDORG = "RandomEdorgId";
    private static final String BAD_STUDENT = "badStudentId";

    @Autowired
    Repository mockRepo;

    @Before
    public void setup() {


    }

    @Test
    public void testCheckFieldAccessAdmin() {
        // inject administrator security context for unit testing

        securityContextInjector.setAdminContextWithElevatedRights();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        NeutralQuery query1 = new NeutralQuery(query);


        service.checkFieldAccess(query, false, null, EntityNames.STUDENT);
        assertTrue("Should match", query1.equals(query));
    }

    @Test (expected = QueryParseException.class)
    public void testCheckFieldAccessEducator() {
        // inject administrator security context for unit testing
        securityContextInjector.setEducatorContext();

        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("economicDisadvantaged", "=", "true"));

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(STUDENT_ID, SecurityContextInjector.ED_ORG_ID));

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());
        mockRepo.createWithRetries(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>(), new HashMap<String, Object>(), EntityNames.STUDENT, 1);
        service.checkFieldAccess(query, false, student, EntityNames.STUDENT);
    }

    @Test
    public void testCheckFullAccessAdmin() {
        securityContextInjector.setAdminContextWithElevatedRights();

        service.checkAccess(true, false, null, EntityNames.STUDENT);
    }

    @Test
    public void testCheckAccessAdmin() {
        securityContextInjector.setLeaAdminContext();
        service.checkAccess(true, false, null, EntityNames.ADMIN_DELEGATION);
    }

    @Test (expected = AccessDeniedException.class)
    public void testCheckAccessStaffReadAccessDenied() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, BAD_EDORG, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(BAD_STUDENT, BAD_EDORG));

        Map<String, Object> eb = new HashMap<String, Object>();
        eb.put("studentUniqueStateId", "1234");
        Entity student = createEntity(EntityNames.STUDENT, BAD_STUDENT, eb);

        service.checkAccess(false, false, student, EntityNames.STUDENT);
    }

    @Test (expected = AccessDeniedException.class)
    public void testCheckAccessStaffWriteAccessDenied() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, BAD_EDORG, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(BAD_STUDENT, BAD_EDORG));

        Map<String, Object> eb = new HashMap<String, Object>();
        eb.put("studentUniqueStateId", "1234");
        Entity student = createEntity(EntityNames.STUDENT, BAD_STUDENT, eb);

        service.checkAccess(true, false, student, EntityNames.STUDENT);
    }

    @Test
    public void testCheckAccessStaffWrite() {
        securityContextInjector.setStaffContext();

        mockRepo.createWithRetries(EntityNames.EDUCATION_ORGANIZATION, SecurityContextInjector.ED_ORG_ID, new HashMap<String, Object>(),
                new HashMap<String, Object>(), EntityNames.EDUCATION_ORGANIZATION, 1);

        mockRepo.create(EntityNames.STUDENT_SCHOOL_ASSOCIATION, createStudentSchoolAssociation(STUDENT_ID, SecurityContextInjector.ED_ORG_ID));

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID, new HashMap<String, Object>());

        service.checkAccess(false, false, student, EntityNames.STUDENT);
    }

    private Entity createEntity(String type, String id, Map<String, Object> body) {
        return new MongoEntity(type, id, body, new HashMap<String,Object>());
    }

    private Map<String, String> createStudentSchoolAssociation(String studentId, String schoolId) {
        Map<String, String> entity = new HashMap<String, String>();
        entity.put(ParameterConstants.STUDENT_ID, studentId);
        entity.put(ParameterConstants.SCHOOL_ID, schoolId);
        return  entity;
    }

    private Map<String, String> createEdorg(String edorgId) {
        Map<String, String> entity = new HashMap<String, String>();
        entity.put(ParameterConstants.STATE_ORGANIZATION_ID, edorgId);
        return  entity;
    }
}
