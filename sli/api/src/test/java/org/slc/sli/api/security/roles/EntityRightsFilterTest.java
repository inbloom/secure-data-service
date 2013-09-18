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
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.service.Treatment;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
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
    private static final String STAFF_ID = "testStaffId";

    private static final String TELEPONE_TYPE = "telephoneNumberType";
    private static final String TELEPONE_VALUE = "teleponeNumber";
    private static final String EMAIL_TYPE = "emailAddressType";
    private static final String EMAIL_VALUE = "emailAddress";
    private static final String TELEPONE_FEILD = "telephone";
    private static final String EMAIL_FIELD = "electronicMail";

    private static final String HOME_TYPE = "Home";
    private static final String WORK_TYPE = "Work";

    private static final String HOME_PHONE = "1234H";
    private static final String WORK_PHONE = "1234W";
    private static final String HOME_EMAIL = "home@test.com";
    private static final String WORK_EMAIL = "work@test.com";

    @Autowired
    @InjectMocks
    EntityRightsFilter entityRightsFilter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        RightAccessValidator mockRightAccessValidator = Mockito.mock(RightAccessValidator.class);

        Set<Right> neededRightID = new HashSet<Right>();
        neededRightID.add(Right.READ_GENERAL);
        Set<Right> neededRightAss = new HashSet<Right>();
        neededRightAss.add(Right.READ_RESTRICTED);
        Mockito.when(mockRightAccessValidator.getNeededRights(Matchers.eq(ParameterConstants.STUDENT_UNIQUE_STATE_ID), Matchers.eq(EntityNames.STUDENT))).thenReturn(neededRightID);
        Mockito.when(mockRightAccessValidator.getNeededRights(Matchers.eq(ParameterConstants.STUDENT_SCHOOL_ASSOCIATION_ID), Matchers.eq(EntityNames.STUDENT))).thenReturn(neededRightAss);

        Mockito.doCallRealMethod().when(mockRightAccessValidator).intersection(Matchers.anyCollection(), Matchers.anySetOf(Right.class));
        entityRightsFilter.setRightAccessValidator(mockRightAccessValidator);
    }

    @Test
    public void testFilterFields() {
       Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        auths.add(Right.READ_GENERAL);

        Entity student = createStudentEntity(EntityNames.STUDENT, STUDENT_ID);
        EntityBody sb = new EntityBody(student.getBody());
        entityRightsFilter.filterFields(sb, auths, "", EntityNames.STUDENT);
        Assert.assertEquals(1, sb.size());
        Assert.assertEquals(STUDENT_UNIQUE_STATE_ID, sb.get(ParameterConstants.STUDENT_UNIQUE_STATE_ID));
    }

    @Test
    public void testComplexFilterReadGeneral() {
        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        auths.add(Right.READ_GENERAL);

        Entity staff = createStaffEntity(EntityNames.STAFF, STAFF_ID);
        EntityBody sb = new EntityBody(staff.getBody());
        entityRightsFilter.complexFilter(sb, auths, EntityNames.STAFF);
        List<Map<String, Object>> telepone = (List<Map<String, Object>>) sb.get(TELEPONE_FEILD);
        List<Map<String, Object>> emails = (List<Map<String, Object>>) sb.get(EMAIL_FIELD);

        Assert.assertEquals(1, telepone.size());
        Assert.assertEquals(1, emails.size());
        Assert.assertEquals(WORK_PHONE, telepone.get(0).get(TELEPONE_VALUE));
        Assert.assertEquals(WORK_EMAIL, emails.get(0).get(EMAIL_VALUE));
    }

    @Test
    public void testComplexFilterReadRestrict() {
        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        auths.add(Right.READ_RESTRICTED);

        Entity staff = createStaffEntity(EntityNames.STAFF, STAFF_ID);
        EntityBody sb = new EntityBody(staff.getBody());
        entityRightsFilter.complexFilter(sb, auths, EntityNames.STAFF);
        List<Map<String, Object>> telepone = (List<Map<String, Object>>) sb.get(TELEPONE_FEILD);
        List<Map<String, Object>> emails = (List<Map<String, Object>>) sb.get(EMAIL_FIELD);

        Assert.assertEquals(2, telepone.size());
        Assert.assertEquals(2, emails.size());
    }

    @Test
    public void testMakeEntityBody() {
        Treatment treatment1 = Mockito.mock(Treatment.class);
        Treatment treatment2 = Mockito.mock(Treatment.class);

        List<Treatment> treatments = new ArrayList<Treatment>();
        treatments.add(treatment1);
        treatments.add(treatment2);

        Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
        auths.add(Right.READ_GENERAL);

        Entity student = createStudentEntity(EntityNames.STUDENT, STUDENT_ID);
        Entity staff = createStaffEntity(EntityNames.STAFF, STAFF_ID);
        student.getEmbeddedData().put("studentSchoolAssociation", Arrays.asList(staff));

        EntityBody sb = new EntityBody(student.getBody());
        EntityBody staffBody = new EntityBody(staff.getBody());
        Mockito.when(treatment1.toExposed(Matchers.any(EntityBody.class), Matchers.any(EntityDefinition.class), Matchers.any(Entity.class))).thenReturn(sb, staffBody);
        Mockito.when(treatment2.toExposed(Matchers.any(EntityBody.class), Matchers.any(EntityDefinition.class), Matchers.any(Entity.class))).thenReturn(sb, staffBody);

        EntityDefinition definition = Mockito.mock(EntityDefinition.class);
        Mockito.when(definition.getType()).thenReturn("student");

        EntityBody res = entityRightsFilter.makeEntityBody(student, treatments, definition, false, auths, SecurityUtil.UserContext.STAFF_CONTEXT);
        Assert.assertNotNull(res);
        List<EntityBody> ssa = (List<EntityBody>) res.get("studentSchoolAssociation");
        Assert.assertNotNull(ssa);
        Assert.assertEquals(1, ssa.size());
        Assert.assertEquals(STAFF_ID, ssa.get(0).get(ParameterConstants.STAFF_UNIQUE_STATE_ID));
    }

    private Entity createStaffEntity(String type, String id) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STAFF_UNIQUE_STATE_ID, STAFF_ID);
        List<Map<String, Object>> telephones = new ArrayList<Map<String, Object>>();
        telephones.add(buildMap(TELEPONE_TYPE, HOME_TYPE, TELEPONE_VALUE, HOME_PHONE));
        telephones.add(buildMap(TELEPONE_TYPE, WORK_TYPE, TELEPONE_VALUE, WORK_PHONE));

        body.put(TELEPONE_FEILD, telephones);
        List<Map<String, Object>> emails = new ArrayList<Map<String, Object>>();
        emails.add(buildMap(EMAIL_TYPE, HOME_TYPE, EMAIL_VALUE, HOME_EMAIL));
        emails.add(buildMap(EMAIL_TYPE, WORK_TYPE, EMAIL_VALUE, WORK_EMAIL));
        body.put(EMAIL_FIELD, emails);

        return new MongoEntity(type, id, body, new HashMap<String,Object>());
    }

    private Map<String, Object> buildMap(String type, String typeValue, String valueKey, String value) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put(type, typeValue);
        res.put(valueKey, value);
        return res;
    }

    private Entity createStudentEntity(String type, String id) {
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
