package org.slc.sli.api.security.context.resolver;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.context.AssociativeContextHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SectionGracePeriodNodeFilterTest {

    @InjectMocks
    @Spy
    SectionGracePeriodNodeFilter nodeFilter = new SectionGracePeriodNodeFilter(); //class under test

    @Mock
    private AssociativeContextHelper mockHelper;

    @Mock
    private Repository<Entity> mockRepo;

    @Before
    public void setup() {
    }

    @Test
    public void testGetIds() {
        List<Entity> entities = getEntityList();

        Set<String> returnedIds = nodeFilter.getIds(entities, "key1");
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 3, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("9"));
        assertTrue("Should be true", !returnedIds.contains("6"));

        returnedIds = nodeFilter.getIds(entities, "_id");
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 4, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("1"));

        returnedIds = nodeFilter.getIds(null, "_id");
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 0, returnedIds.size());
    }

    @Test
    public void testGetEntityIds() {
        List<Entity> entities = getEntityList();
        Set<String> ids = new HashSet<String>();

        ids.add("9");
        ids.add("8");

        Set<String> returnedIds = nodeFilter.getEntityIds(entities, "key1", ids);
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 2, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("1"));
        assertTrue("Should be true", returnedIds.contains("2"));
    }

    @Test
    public void testFilterIds() {
        List<Entity> sections = getSectionsList();
        List<Entity> sessions = getSessionList();
        List<Entity> studentSecAssoc = getStudentSectionAssociation();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 3);
        when(mockHelper.getFilterDate(anyString(), any(Calendar.class))).thenReturn("2012-04-03");

        when(mockHelper.getReferenceEntities(eq(EntityNames.SECTION),
                eq("_id"), any(List.class))).thenReturn(sections);
        when(mockRepo.findAll(eq("session"), any(NeutralQuery.class))).thenReturn(sessions);

        List<Entity> returnedEntities = nodeFilter.filterEntities(studentSecAssoc,"sectionId");
        List<String> returnedIds = getReturnedIds(returnedEntities);

        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 2, returnedIds.size());
        assertTrue("Should be true", returnedIds.contains("1"));
        assertTrue("Should be true", returnedIds.contains("3"));
    }

    @Test
    public void testEmptyFilterDate()  {
        List<Entity> studentSecAssoc = getStudentSectionAssociation();
        when(mockHelper.getFilterDate(anyString(), any(Calendar.class))).thenReturn(StringUtils.EMPTY);

        List<Entity> returnedEntities = nodeFilter.filterEntities(studentSecAssoc,"sectionId");
        List<String> returnedIds = getReturnedIds(returnedEntities);
        assertNotNull("Should not be null", returnedIds);
        assertEquals("Should match", 3, returnedIds.size());
    }

    private List<Entity> getEntityList() {
        List<Entity> list = new ArrayList<Entity>();
        String key = "key1";

        list.add(createEntity("1", key, "9"));
        list.add(createEntity("2", key, "8"));
        list.add(createEntity("3", key, "7"));
        list.add(createEntity("4", "key2", "6"));

        return list;
    }

    private List<Entity> getSectionsList() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("1", "sessionId", "9"));
        list.add(createEntity("2", "sessionId", "8"));
        list.add(createEntity("3", "sessionId", "7"));

        return list;
    }

    private List<Entity> getSessionList() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("9", "endDate", "2012-03-01"));
        list.add(createEntity("7", "endDate", "2012-03-01"));

        return list;
    }

    private List<Entity> getStudentSectionAssociation() {
        List<Entity> list = new ArrayList<Entity>();

        list.add(createEntity("1", "sectionId", "1"));
        list.add(createEntity("2", "sectionId", "2"));
        list.add(createEntity("3", "sectionId", "3"));
        return list;
    }
    private Entity createEntity(String id, String key, String value) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put(key, value);

        Entity mockEntity = mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn(id);
        when(mockEntity.getBody()).thenReturn(body);

        return mockEntity;
    }
    private List<String> getReturnedIds(List<Entity> entityList){
        List<String> ids=new ArrayList<String>();
        for(Entity e:entityList){
            ids.add(e.getEntityId());
        }
        return ids;
    }

}
