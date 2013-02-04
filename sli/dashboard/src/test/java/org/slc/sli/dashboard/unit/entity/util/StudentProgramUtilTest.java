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


package org.slc.sli.dashboard.unit.entity.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.StudentProgramUtil;
import org.slc.sli.dashboard.util.Constants;

/**
 * 
 * @author dwu
 */
public class StudentProgramUtilTest {

    GenericEntity student;
    
    @Before
    public void setupAll() {
        student = new GenericEntity();
        student.put(Constants.PROGRAM_ELL, Constants.SHOW_ELL_LOZENGE);
        student.put(Constants.PROGRAM_FRE, "No");
    }
    
    @Test
    public void testHasProgramParticipation() {
        Assert.assertTrue(StudentProgramUtil.hasProgramParticipation(student, Constants.PROGRAM_ELL));
        Assert.assertFalse(StudentProgramUtil.hasProgramParticipation(student, Constants.PROGRAM_FRE));
        Assert.assertFalse(StudentProgramUtil.hasProgramParticipation(student, "Fake program"));
    }

}
