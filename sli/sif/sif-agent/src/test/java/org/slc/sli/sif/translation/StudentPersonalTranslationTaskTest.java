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

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.student.LEAInfo;
import openadk.library.student.StudentPersonal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slc.sli.sif.domain.slientity.LEAEntity;
import org.slc.sli.sif.domain.slientity.StudentEntity;

/**
 * StudentPersonal to StudentEntity unit tests
 *
 * @author slee
 *
 */
public class StudentPersonalTranslationTaskTest
{
    @InjectMocks
    private final StudentPersonalTranslationTask translator = new StudentPersonalTranslationTask();

    @Before
    public void beforeTests() {
        try {
            ADK.initialize();
        } catch (ADKException e) {
            e.printStackTrace();
        }
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNotNull() throws SifTranslationException {
        List<StudentEntity> result = translator.translate(new StudentPersonal());
        Assert.assertNotNull("Result was null", result);
        Assert.assertEquals(1, result.size());
    }

}
