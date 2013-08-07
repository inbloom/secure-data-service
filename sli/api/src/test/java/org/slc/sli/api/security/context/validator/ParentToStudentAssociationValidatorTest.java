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


import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.constants.EntityNames;

public class ParentToStudentAssociationValidatorTest {
    
    @InjectMocks
    ParentToStudentAssociationValidator underTest = new ParentToStudentAssociationValidator();
    
    @Mock
    TransitiveStudentToStudentValidator studentValidator;
    
    Set<String> ids = new HashSet<String>();
    @Before
    public void setUp() throws Exception {
        ids.add("parent_idchild_id");
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void followStudentValidator() {
        Set<String> superdocIds = new HashSet<String>(Arrays.asList("parent_id"));
        when(studentValidator.validate(EntityNames.STUDENT, superdocIds)).thenReturn(Collections.EMPTY_SET);
        Assert.assertEquals(superdocIds, underTest.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, ids));
        
        when(studentValidator.validate(EntityNames.STUDENT, superdocIds)).thenReturn(superdocIds);
        Assert.assertEquals(superdocIds, underTest.validate(EntityNames.STUDENT_COHORT_ASSOCIATION, ids));
    }
    
}
