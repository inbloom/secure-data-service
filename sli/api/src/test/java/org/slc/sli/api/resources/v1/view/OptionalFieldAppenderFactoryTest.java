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


package org.slc.sli.api.resources.v1.view;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.v1.view.impl.StudentAssessmentOptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.impl.StudentGradebookOptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.impl.StudentTranscriptOptionalFieldAppender;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * Unit tests
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OptionalFieldAppenderFactoryTest {

    @Autowired
    OptionalFieldAppenderFactory factory;

    @Test
    public void testGetViewGenerator() {
        assertTrue("Should be of type studentassessment",
                factory.getOptionalFieldAppender(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_ASSESSMENTS)
                instanceof StudentAssessmentOptionalFieldAppender);
        assertTrue("Should be of type studentgradebook",
                factory.getOptionalFieldAppender(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_GRADEBOOK)
                instanceof StudentGradebookOptionalFieldAppender);
        assertTrue("Should be of type studenttranscript",
                factory.getOptionalFieldAppender(ResourceNames.SECTIONS + "_" + ParameterConstants.OPTIONAL_FIELD_TRANSCRIPT)
                instanceof StudentTranscriptOptionalFieldAppender);
    }
}
