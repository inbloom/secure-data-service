package org.slc.sli.api.security.pdp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;

/**
 * Tests for ContextInferranceHelper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class ContextInferranceHelperTest {

    @Autowired
    ContextInferranceHelper contextInferranceHelper;

    private Entity teacher;
    private Entity staff;

    @Before
    public void setup() throws Exception {
        teacher = mock(Entity.class);
        when(teacher.getType()).thenReturn(EntityNames.TEACHER);
        staff = mock(Entity.class);
        when(staff.getType()).thenReturn(EntityNames.STAFF);
    }

    @Test
    public void testGetInferredUriForTeacherAttendance() throws Exception {
        String attendanceEndpoint = contextInferranceHelper.getInferredUri(ResourceNames.ASSESSMENTS, teacher);
        assertNotNull(attendanceEndpoint);
    }
}
