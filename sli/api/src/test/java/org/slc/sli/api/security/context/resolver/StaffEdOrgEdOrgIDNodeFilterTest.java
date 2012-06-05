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
public class StaffEdOrgEdOrgIDNodeFilterTest {
    private static final String ED_ORG_REF = "educationOrganizationReference";

    @InjectMocks
    @Spy
    StaffEdOrgEdOrgIDNodeFilter nodeFilter = new StaffEdOrgEdOrgIDNodeFilter(); //class under test

    @Mock
    private AssociativeContextHelper mockHelper;

    @Mock
    private Repository<Entity> mockRepo;

    @Before
    public void setup() {
    }

    @Test
    public void testFilterIds() {
        List<Entity> schoolAssociations = getSchoolAssociations();
        List<Entity> edorgAssociations = getEdorgAssociations();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 3);
        when(mockHelper.getFilterDate(anyString(), any(Calendar.class))).thenReturn("2012-04-03");

        when(mockHelper.getReferenceEntities(eq(EntityNames.STAFF_ED_ORG_ASSOCIATION),
                eq(ED_ORG_REF), any(List.class))).thenReturn(edorgAssociations);

        when(mockHelper.getReferenceEntities(eq(EntityNames.TEACHER_SCHOOL_ASSOCIATION),
                eq(ParameterConstants.SCHOOL_ID), any(List.class))).thenReturn(schoolAssociations);

        List<String> ids = new ArrayList<String>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        ids.add("14");
        ids.add("15");
        ids.add("16");

        List<String> returnedIds = nodeFilter.filterIds(ids);
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 4, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("1"));
        assertTrue("Should be true", returnedIds.contains("3"));
        assertTrue("Should be true", returnedIds.contains("14"));
        assertTrue("Should be true", returnedIds.contains("15"));

    }

    private List<Entity> getSchoolAssociations() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("schoolId", "1","endDate", "2012-08-03"));
        list.add(createEntity("schoolId", "2", "endDate", "2012-01-03"));
        list.add(createEntity("schoolId", "3","someKey", "7"));

        return list;
    }
    private List<Entity> getEdorgAssociations() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("educationOrganizationReference","14","exitWithdrawDate", ""));
        list.add(createEntity("educationOrganizationReference","15", "endDate", "2012-08-03"));
        list.add(createEntity("educationOrganizationReference","16","endDate", "2012-01-03"));

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
