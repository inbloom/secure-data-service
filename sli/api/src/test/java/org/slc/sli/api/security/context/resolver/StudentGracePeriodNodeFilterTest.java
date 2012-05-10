package org.slc.sli.api.security.context.resolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

    @Before
    public void setup() {
    }

    @Test
    public void testIsResolvable() {
        assertTrue("Should be true", nodeFilter.isResolvable("2012-08-03", "2012-02-03"));
        assertTrue("Should be true", nodeFilter.isResolvable(null, "2012-02-03"));
        assertTrue("Should be true", nodeFilter.isResolvable("", "2012-02-03"));
        assertFalse("Should be false", nodeFilter.isResolvable("2012-05-12", "2012-06-03"));
        assertFalse("Should be false", nodeFilter.isResolvable("2012-05-12", ""));
        assertFalse("Should be false", nodeFilter.isResolvable("somevalue", "2012-06-03"));
    }

    @Test
    public void testFilterIds() {
        List<Entity> studentSchoolAssociations = getStudentSchoolAssociations();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 3);
        when(mockHelper.getFilterDate(anyString(), any(Calendar.class))).thenReturn("2012-04-03");

        when(mockHelper.getReferenceEntities(eq(EntityNames.STUDENT_SCHOOL_ASSOCIATION),
                eq(ParameterConstants.STUDENT_ID), any(List.class))).thenReturn(studentSchoolAssociations);

        List<String> ids = new ArrayList<String>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        ids.add("4");

        List<String> returnedIds = nodeFilter.filterIds(ids);
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 3, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("1"));
        assertTrue("Should be true", returnedIds.contains("3"));
        assertTrue("Should be true", returnedIds.contains("4"));
    }

    private List<Entity> getStudentSchoolAssociations() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("studentId", "1", "exitWithdrawDate", "2012-08-03"));
        list.add(createEntity("studentId", "2", "exitWithdrawDate", "2012-01-03"));
        list.add(createEntity("studentId", "3", "someKey", "7"));
        list.add(createEntity("studentId", "4", "exitWithdrawDate", ""));

        return list;
    }

    private Entity createEntity(String key1, String value1,
                                String key2, String value2) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(key1, value1);
        body.put(key2, value2);

        Entity mockEntity = mock(Entity.class);
        when(mockEntity.getBody()).thenReturn(body);

        return mockEntity;
    }
}
