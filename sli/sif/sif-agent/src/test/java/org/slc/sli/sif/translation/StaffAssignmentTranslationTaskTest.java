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

package org.slc.sli.sif.translation;

import java.util.List;

import openadk.library.student.StaffAssignment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.SliEntity;
import org.slc.sli.sif.slcinterface.SifIdResolver;

/**
 * StaffAssignmentTranslationTask unit tests
 *
 * @author slee
 *
 */
public class StaffAssignmentTranslationTaskTest extends AdkTest {

    @InjectMocks
    private final StaffAssignmentTranslationTask translator = new StaffAssignmentTranslationTask();

    @Mock
    SifIdResolver mockSifIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<SliEntity> result = translator.translate(new StaffAssignment(), "");
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

}


