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

import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.view.impl.StudentAssessmentOptionalFieldStrategy;
import org.slc.sli.api.resources.v1.view.impl.StudentAttendanceOptionalFieldStrategy;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Unit tests 
 * @author srupasinghe
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OptionalFieldStrategyFactoryTest {
    
    @Autowired
    OptionalFieldStrategyFactory factory;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void testGetViewGenerator() {
        assertTrue("Should be of type studentassessment", factory.getOptionalFieldStrategy(ParameterConstants.OPTIONAL_FIELD_ASSESSMENTS)
                instanceof StudentAssessmentOptionalFieldStrategy);
        assertTrue("Should be of type studentattendance", factory.getOptionalFieldStrategy(ParameterConstants.OPTIONAL_FIELD_ATTENDANCES)
                instanceof StudentAttendanceOptionalFieldStrategy);
    }
}
