package org.slc.sli.api.security.context.resolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;

/**
 * Unit Tests
 *
 * @author srupasinghe
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentGracePeriodNodeFilterTest {
    @InjectMocks
    @Spy
    StudentGracePeriodNodeFilter nodeFilter = new StudentGracePeriodNodeFilter(); //class under test

    @Mock
    private AssociativeContextHelper mockHelper;

    @Mock
    private Repository<Entity> mockRepo;

    @Value("${sli.security.gracePeriod}")
    private String gracePeriodVal;

    @Before
    public void setup() {
    }

    @Test
    public void testsetParameters() {
        nodeFilter.setParameters();
        assertEquals("Should match", EntityNames.STUDENT_SCHOOL_ASSOCIATION, nodeFilter.getEntityName());
        assertEquals("Should match", ParameterConstants.STUDENT_ID, nodeFilter.getReferenceId());
        assertEquals("Should match", gracePeriodVal, nodeFilter.getGracePeriod());
        assertEquals("Should match", "exitWithdrawDate", nodeFilter.getEndDateParamName());
    }

}
