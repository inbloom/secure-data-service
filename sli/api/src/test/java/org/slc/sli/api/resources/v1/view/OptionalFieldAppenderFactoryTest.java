package org.slc.sli.api.resources.v1.view;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.resources.v1.view.impl.StudentAssessmentOptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.impl.StudentGradebookOptionalFieldAppender;
import org.slc.sli.api.resources.v1.view.impl.StudentTranscriptOptionalFieldAppender;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;

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

    @Before
    public void setup() {
    }

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
