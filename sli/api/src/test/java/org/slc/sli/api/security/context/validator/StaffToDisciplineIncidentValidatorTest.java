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

package org.slc.sli.api.security.context.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;

/**
 * Tests the staff to discipline incident validator.
 *
 *
 * @author kmyers
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StaffToDisciplineIncidentValidatorTest {
    @Autowired
    private StaffToDisciplineIncidentValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private StaffToSubStudentEntityValidator mockStudentValidator;

    @Autowired
    private GenericToEdOrgValidator mockSchoolValidator;

    @Autowired PagingRepositoryDelegate<Entity> repo;

    Set<String> diIds;

    @Before
    public void setUp() throws Exception {
        // Set up the principal
        String user = "fake Staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);

        Entity entity = Mockito.mock(Entity.class);
        Mockito.when(entity.getType()).thenReturn("staff");
        Mockito.when(entity.getEntityId()).thenReturn(helper.STAFF_ID);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, entity, helper.ED_ORG_ID);

        diIds = new HashSet<String>();
        mockStudentValidator = Mockito.mock(StaffToSubStudentEntityValidator.class);
        mockSchoolValidator = Mockito.mock(GenericToEdOrgValidator.class);
        validator.setSchoolValidator(mockSchoolValidator);
        validator.setSubStudentValidator(mockStudentValidator);
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll(EntityNames.EDUCATION_ORGANIZATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STAFF_ED_ORG_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new NeutralQuery());
        repo.deleteAll(EntityNames.DISCIPLINE_INCIDENT, new NeutralQuery());
        mockStudentValidator = null;
        mockSchoolValidator = null;

    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, false));
        assertFalse(validator.canValidate(EntityNames.SECTION, true));
        assertFalse(validator.canValidate(EntityNames.SECTION, false));
    }

    @Test
    public void testCanValidateDisciplineIncidentFromSchool() {

        Entity school = helper.generateEdorgWithParent(null);
        Mockito.when(
                mockSchoolValidator.validate(EntityNames.SCHOOL,
                        new HashSet<String>(Arrays.asList(school.getEntityId())))).thenReturn(true);
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity di = helper.generateDisciplineIncident(school.getEntityId());
        diIds.add(di.getEntityId());
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
    }

    @Test
    public void testCanValidateDisciplineIncidentFromStudent() {
        Mockito.when(
                mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION),
                        Mockito.any(Set.class))).thenReturn(true);
        Entity school = helper.generateEdorgWithParent(null);
        Entity di = helper.generateDisciplineIncident(school.getEntityId());
        diIds.add(di.getEntityId());
        helper.generateStudentDisciplineIncidentAssociation("Berp", di.getEntityId());
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
    }

    @Test
    public void testCanValidateDisciplineIncidentFromState() {
        Entity sea = helper.generateEdorgWithParent(null);
        Entity lea = helper.generateEdorgWithParent(sea.getEntityId());

        helper.generateStaffEdorg(helper.STAFF_ID, lea.getEntityId(), false);
        for (int i = 0; i < 10; ++i) {
            Entity school = helper.generateEdorgWithParent(lea.getEntityId());
            Entity di = helper.generateDisciplineIncident(school.getEntityId());
            diIds.add(di.getEntityId());
        }
        Mockito.when(mockSchoolValidator.validate(Mockito.eq(EntityNames.SCHOOL), Mockito.any(Set.class))).thenReturn(
                true);
        assertTrue(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
        // Now put one above and fail
        Mockito.when(mockSchoolValidator.validate(Mockito.eq(EntityNames.SCHOOL), Mockito.any(Set.class))).thenReturn(
                false);
        diIds.add(helper.generateDisciplineIncident(sea.getEntityId()).getEntityId());
        assertFalse(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
    }

    @Test
    public void testCanNotValidateDisciplineIncidentFromOtherSchool() {
        Entity school = helper.generateEdorgWithParent(null);
        Entity school2 = helper.generateEdorgWithParent(null);
        Mockito.when(
                mockSchoolValidator.validate(EntityNames.SCHOOL,
                        new HashSet<String>(Arrays.asList(school2.getEntityId())))).thenReturn(false);
        helper.generateStaffEdorg(helper.STAFF_ID, school.getEntityId(), false);
        Entity di = helper.generateDisciplineIncident(school2.getEntityId());
        diIds.add(di.getEntityId());
        assertFalse(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
    }

    @Test
    public void testCanNotValidateDisciplineIncidentFromInvalidAssoc() {
        Mockito.when(
                mockStudentValidator.validate(Mockito.eq(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION),
                        Mockito.any(Set.class))).thenReturn(false);
        Entity school = helper.generateEdorgWithParent(null);
        Entity di = helper.generateDisciplineIncident(school.getEntityId());
        diIds.add(di.getEntityId());
        helper.generateStudentDisciplineIncidentAssociation("Berp", di.getEntityId());
        assertFalse(validator.validate(EntityNames.DISCIPLINE_INCIDENT, diIds));
    }
}
