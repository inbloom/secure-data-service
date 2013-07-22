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
package org.slc.sli.api.security.context;

import junit.framework.Assert;
import org.elasticsearch.common.inject.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Stubber;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.validator.*;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @author: tke
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ContextValidatorTest {

    @Autowired
    @InjectMocks
    ContextValidator contextValidator;

    @Autowired
    SLIPrincipal principal;

    SecurityContext context = Mockito.mock(SecurityContext.class);
    Authentication auth = Mockito.mock(Authentication.class);

    @Before
    public void setup() {

        Mockito.when(context.getAuthentication()).thenReturn(auth);
        Mockito.when(auth.getPrincipal()).thenReturn(principal);
        SecurityContextHolder.setContext(context);

    }

    @Test
    public void testFindContextualValidator() {
        //only teacher context, inTransitive
        Set<Object> expectedValidators = new HashSet<Object>(Arrays.asList(TeacherToStudentValidator.class));
        testStaffValidators(true, false, false, EntityNames.STUDENT, expectedValidators);

        //Only staff context, inTransitive
        expectedValidators = new HashSet<Object>(Arrays.asList(StaffToStudentValidator.class));
        testStaffValidators(false, true, false, EntityNames.STUDENT, expectedValidators);

        //both contexts, intransitive
        expectedValidators = new HashSet<Object>(Arrays.asList(TeacherToStudentValidator.class, StaffToStudentValidator.class));
        testStaffValidators(true, true, false, EntityNames.STUDENT, expectedValidators);

        //both contexts, transitive
        expectedValidators = new HashSet<Object>(Arrays.asList(TransitiveTeacherToTeacherSchoolAssociationValidator.class, StaffToTeacherSchoolAssociationValidator.class));
        testStaffValidators(true, true, true, EntityNames.TEACHER_SCHOOL_ASSOCIATION, expectedValidators);

        //only staff context, transitive
        expectedValidators = new HashSet<Object>(Arrays.asList(StaffToTeacherSchoolAssociationValidator.class));
        testStaffValidators(false, true, true, EntityNames.TEACHER_SCHOOL_ASSOCIATION, expectedValidators);

        //only teacher context, transitive
        expectedValidators = new HashSet<Object>(Arrays.asList(TransitiveTeacherToTeacherSchoolAssociationValidator.class));
        testStaffValidators(true, false, true, EntityNames.TEACHER_SCHOOL_ASSOCIATION, expectedValidators);

        //no contexts
        expectedValidators = new HashSet<Object>();
        testStaffValidators(false, false, false, EntityNames.STUDENT, expectedValidators);
    }


    private void testStaffValidators(boolean isTeacher, boolean isStaff, boolean isTransitive, String entityType, Set<Object> expectedValidators) {
        principal.setTeacher(isTeacher);
        principal.setStaff(isStaff);
        principal.setTransitive(isTransitive);
        MongoEntity entity = new MongoEntity(EntityNames.TEACHER, new HashMap<String, Object>());
        principal.setEntity(entity);

        List<IContextValidator> validators = contextValidator.findContextualValidator(entityType, principal.isTransitive());
        Assert.assertEquals(expectedValidators.size(), validators.size());

        for(IContextValidator validator : validators) {
            Assert.assertTrue(expectedValidators.contains(validator.getClass()));
        }
    }

}
