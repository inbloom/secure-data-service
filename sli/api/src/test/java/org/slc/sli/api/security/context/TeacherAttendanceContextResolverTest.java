package org.slc.sli.api.security.context;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.service.MockRepo;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;

/**
 * Unit tests for the TeacherAttendanceContextResolver class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class TeacherAttendanceContextResolverTest {
    
    @Autowired
    TeacherAttendanceContextResolver resolver;
    
    @Autowired
    MockRepo mockRepo;
    
    @Autowired
    private EntityDefinitionStore definitionStore;
    
    @Before
    public void setUp() throws Exception {
        
        resolver.setDefinitionStore(definitionStore);
        resolver.setRepository(mockRepo);
        
    }
    
    @Test
    public void testGetSourceType() throws Exception {
        Assert.assertTrue(resolver.getSourceType().equals(EntityNames.TEACHER));
    }
    
    @Test
    public void testGetTargetType() throws Exception {
        Assert.assertTrue(resolver.getTargetType().equals(EntityNames.ATTENDANCE));
    }
    
    @Test
    public void testFindAccessible() throws Exception {
        resolver.setRepository(mockRepo);
        Entity principal = Mockito.mock(Entity.class);
        Assert.assertTrue(resolver.findAccessible(principal).isEmpty());
        
        // TODO resolve principal ID, add association objects, add entities
        // TODO findAccessible should return only ids with context
    }
    
    @Test
    public void testSetRepository() throws Exception {
        EntityRepository repository = Mockito.mock(EntityRepository.class);
        when(repository.findByQuery(anyString(), any(Query.class), anyInt(), anyInt())).thenReturn(
                new ArrayList<Entity>());
        resolver.setRepository(repository);
        
        Entity principal = Mockito.mock(Entity.class);
        resolver.setRepository(repository);
        resolver.findAccessible(principal);
        verify(repository, atLeastOnce()).findByQuery(anyString(), any(Query.class), anyInt(), anyInt());
    }
    
    @Test
    public void testSetDefinitionStore() throws Exception {
        EntityDefinitionStore definitionStore = Mockito.mock(EntityDefinitionStore.class);
        Entity principal = Mockito.mock(Entity.class);
        resolver.setDefinitionStore(definitionStore);
        try {
            resolver.findAccessible(principal);
        } catch (NullPointerException e) {
            assertTrue(true);
            // expected as we haven't created mock definitions
        }
        verify(definitionStore, atLeastOnce()).lookupByResourceName(anyString());
    }
}
