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
 * @author: lchen
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class GenericToGlobalClassPeriodWriteValidatorTest {
    @Autowired
    private GenericToGlobalClassPeriodWriteValidator validator;

    @Autowired
    private ValidatorTestHelper helper;

    @Autowired
    private SecurityContextInjector injector;

    @Autowired
    private PagingRepositoryDelegate<Entity> repo;

    Entity lea = null;
    Entity schoolParentLea = null;
    Entity schoolNoParent = null;

    Entity classPeriodLea= null;
    Entity classPeriodSchoolLea = null;
    Entity classPeriodSchoolNoParent = null;

    Entity staffLea = null, staffSchoolLea = null, staffSchoolNoParent = null;
    Set<String> classPeriodIds = new HashSet<String>();

    @Before
    public void setup() {
        lea = helper.generateEdorgWithParent(null);

        schoolParentLea = helper.generateSchoolEdOrg(lea.getEntityId());
        schoolNoParent = helper.generateSchoolEdOrg(null);

        classPeriodLea = helper.generateClassPeriod(lea.getEntityId());  
        classPeriodSchoolLea = helper.generateClassPeriod(schoolParentLea.getEntityId());
        classPeriodSchoolNoParent = helper.generateClassPeriod(schoolNoParent.getEntityId());
        
        classPeriodIds.add(classPeriodLea.getEntityId());
        classPeriodIds.add(classPeriodSchoolNoParent.getEntityId());
        classPeriodIds.add(classPeriodSchoolLea.getEntityId());

        staffLea = helper.generateStaff();
        staffSchoolLea = helper.generateStaff();
        staffSchoolNoParent = helper.generateStaff();

        helper.generateStaffEdorg(staffLea.getEntityId(), lea.getEntityId(), false);
        helper.generateStaffEdorg(staffSchoolLea.getEntityId(), schoolParentLea.getEntityId(), false);
        helper.generateStaffEdorg(staffSchoolNoParent.getEntityId(), schoolNoParent.getEntityId(), false);

    }

    private void setupStaff(Entity staff, String edorgId ) {
        String user = "fake staff";
        String fullName = "Fake Staff";
        List<String> roles = Arrays.asList(SecureRoleRightAccessImpl.IT_ADMINISTRATOR);
        injector.setCustomContext(user, fullName, "MERPREALM", roles, staff, edorgId);
    }

    @Test
    public void testFilterClassPeriodDataFromLEA() {
       setupStaff(staffLea, lea.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(classPeriodLea.getEntityId(), classPeriodSchoolLea.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CLASS_PERIOD, classPeriodIds);

        Assert.assertEquals(expectedIds, actual);
    }

    @Test
    public void testFilterClassPeriodDataFromSchool() {
        setupStaff(staffSchoolLea, schoolParentLea.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(classPeriodSchoolLea.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CLASS_PERIOD, classPeriodIds);

        Assert.assertEquals(expectedIds, actual);
    }

    @Test
    public void testFilterClassPeriodDataFromSchool2() {
        setupStaff(staffSchoolNoParent, schoolNoParent.getEntityId());
        Set<String> expectedIds = new HashSet<String>(Arrays.asList(classPeriodSchoolNoParent.getEntityId()));

        Set<String> actual = validator.validate(EntityNames.CLASS_PERIOD, classPeriodIds);

        Assert.assertEquals(expectedIds, actual);
    }
}
