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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit Tests
 *
 * @author pghosh
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class StudentSectionAssociationEndDateFilterTest {
    @InjectMocks
    @Spy
    StudentSectionAssociationEndDateFilter nodeFilter = new StudentSectionAssociationEndDateFilter(); //class under test

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
    public void testSetParameters() {
        nodeFilter.setParameters();
        assertEquals("Should match", EntityNames.STUDENT_SECTION_ASSOCIATION, nodeFilter.getEntityName());
        assertEquals("Should match", ParameterConstants.STUDENT_ID, nodeFilter.getReferenceId());
        assertEquals("Should match", gracePeriodVal, nodeFilter.getGracePeriod());
        assertEquals("Should match", "endDate", nodeFilter.getFilterDateParam());
    }
}

