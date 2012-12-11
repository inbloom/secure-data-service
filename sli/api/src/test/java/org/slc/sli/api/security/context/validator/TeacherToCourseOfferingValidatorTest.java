/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@Component
public class TeacherToCourseOfferingValidatorTest {
    
    @Autowired
    ValidatorTestHelper helper;
    
    @Autowired
    PagingRepositoryDelegate<Entity> repo;
    
    @Autowired
    TeacherToCourseOfferingValidator validator;

    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testCanValidate() {
        assertTrue(validator.canValidate(EntityNames.COURSE_OFFERING, false));
        assertFalse(validator.canValidate(EntityNames.COURSE_OFFERING, true));
        assertFalse(validator.canValidate(EntityNames.ATTENDANCE, false));
    }
    
    @Test
    public void testCanNotValidateEmptyIds() {
        assertFalse(validator.validate(EntityNames.COURSE_OFFERING, null));
    }
    
    @Test
    public void testCanNotValidateEmptyName() {
        assertFalse(validator.validate(null, new HashSet<String>()));
    }

}
