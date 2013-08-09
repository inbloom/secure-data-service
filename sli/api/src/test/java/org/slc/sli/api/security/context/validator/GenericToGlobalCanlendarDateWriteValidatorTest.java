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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.roles.SecureRoleRightAccessImpl;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: tke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericToGlobalCanlendarDateWriteValidatorTest {
    @Autowired
    private GenericToGlobalCalendarDateWriteValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    Entity lea = null;
    Entity schoolParenLea = null;
    Entity schoolNoParent = null;

    Entity calendarDateLea= null;
    Entity calendarDateSchoolLea = null;
    Entity calendarDateSchoolNoParent = null;

    Entity staffLea = null, staffSchoolLea = null, staffSchoolNoParent = null;
    Set<String> calendarDateIds = new HashSet<String>();

    @Before
    public void setup() {
        lea = helper.generateEdorgWithParent(null);

        schoolParenLea = helper.generateSchoolEdOrg(lea.getEntityId());
        schoolNoParent = helper.generateSchoolEdOrg(null);

        calendarDateLea = helper.generateCalendarDate(lea.getEntityId());
        calendarDateSchoolLea = helper.generateCalendarDate(schoolParenLea.getEntityId());
        calendarDateSchoolNoParent = helper.generateCalendarDate(schoolNoParent.getEntityId());
        calendarDateIds.add(calendarDateLea.getEntityId());
        calendarDateIds.add(calendarDateSchoolNoParent.getEntityId());
        calendarDateIds.add(calendarDateSchoolLea.getEntityId());

        staffLea = helper.generateStaff();
        staffSchoolLea = helper.generateStaff();
        staffSchoolNoParent = helper.generateStaff();

        helper.generateStaffEdorg(staffLea.getEntityId(), lea.getEntityId(), false);
        helper.generateStaffEdorg(staffSchoolLea.getEntityId(), schoolParenLea.getEntityId(), false);
        helper.generateStaffEdorg(staffSchoolNoParent.getEntityId(), schoolNoParent.getEntityId(), false);

    }

    private void setupStaff(Entity staff, String edorgId ) {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, edorgId);
    }

    @Test
    public void testFilterCalendarDataFromLEA() {
        setupStaff(staffLea, lea.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(calendarDateLea.getEntityId(), calendarDateSchoolLea.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CALENDAR_DATE, calendarDateIds);

        Assert.assertEquals(expectedIds, actual);
    }

    @Test
    public void testFilterCalendarDataFromSchool() {
        setupStaff(staffSchoolLea, schoolParenLea.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(calendarDateSchoolLea.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CALENDAR_DATE, calendarDateIds);

        Assert.assertEquals(expectedIds, actual);
    }

    @Test
    public void testFilterCalendarDataFromSchool2() {
        setupStaff(staffSchoolNoParent, schoolNoParent.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(calendarDateSchoolNoParent.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CALENDAR_DATE, calendarDateIds);

        Assert.assertEquals(expectedIds, actual);
    }
}
