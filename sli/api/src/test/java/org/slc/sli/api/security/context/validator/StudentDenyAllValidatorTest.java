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

import java.util.Collections;
import java.util.HashSet;

import junit.framework.TestCase;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentDenyAllValidatorTest extends TestCase {

    @Autowired
    SecurityContextInjector injector;

    @Autowired
    ValidatorTestHelper helper;

    @Autowired
    StudentDenyAllValidator validator;

    @Override
    @Before
    public void setUp() throws Exception {
        Entity student1 = helper.generateStudent();
        injector.setStudentContext(student1);
    }

    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_ACTION, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_ACTION, false));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, true));
        assertTrue(validator.canValidate(EntityNames.DISCIPLINE_INCIDENT, false));
        assertTrue(validator.canValidate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, true));
        assertTrue(validator.canValidate(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, false));

        assertFalse(validator.canValidate(EntityNames.STUDENT, true));
        assertFalse(validator.canValidate(EntityNames.STUDENT_GRADEBOOK_ENTRY, false));

        assertTrue(validator.canValidate(EntityNames.PROGRAM, true));
        assertFalse(validator.canValidate(EntityNames.PROGRAM, false));
    }

    @Test
    public void testValidate() {
        Assert.assertEquals(Collections.emptySet(), validator.validate(null, null));
        Assert.assertEquals(Collections.emptySet(), validator.validate(new String(), new HashSet<String>()));
    }
}
