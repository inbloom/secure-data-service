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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.*;
import org.slc.sli.domain.enums.Right;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.*;

import static org.junit.Assert.assertTrue;


/**
 * @author: tke
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class EntityRightsFilterTest {

    @Autowired
    SecurityContextInjector securityContextInjector;

    @Autowired
    private RightAccessValidator service;

    private static final String STUDENT_ID = "testStudentId";

    private static final String BAD_EDORG = "RandomEdorgId";
    private static final String BAD_STUDENT = "badStudentId";

    private static final String STUDENT_UNIQUE_STATE_ID = "1234";

    @Autowired
    @InjectMocks
    EntityRightsFilter entityRightsFilter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        RightAccessValidator mockRightAccessValidator = Mockito.mock(RightAccessValidator.class);

        Set<Right> neededRightID = new HashSet<Right>();
        neededRightID.add(Right.READ_GENERAL);
        Mockito.when(mockRightAccessValidator.getNeededRights(Matchers.eq(STUDENT_UNIQUE_STATE_ID), Matchers.eq(EntityNames.STUDENT))).thenReturn(neededRightID);
        Mockito.when(mockRightAccessValidator.getNeededRights(Matchers.eq(ParameterConstants.STUDENT_SCHOOL_ASSOCIATION_ID), Matchers.eq(EntityNames.STUDENT))).thenReturn(new HashSet<Right>());

        Mockito.doCallRealMethod().when(mockRightAccessValidator).intersection(Matchers.anyCollection(), Matchers.anySetOf(Right.class));
    }

    @Test
    public void testFilterFields() {
       Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        auths.add(Right.READ_GENERAL);

        Entity student = createEntity(EntityNames.STUDENT, STUDENT_ID);
        EntityBody sb = new EntityBody(student.getBody());
        entityRightsFilter.filterFields(sb, auths, "", EntityNames.STUDENT);
        Assert.assertEquals(sb.size(), 2);
    }

    private Entity createEntity(String type, String id) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STUDENT_UNIQUE_STATE_ID, STUDENT_UNIQUE_STATE_ID);
        body.put(ParameterConstants.STUDENT_SCHOOL_ASSOCIATION_ID, BAD_EDORG);
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
