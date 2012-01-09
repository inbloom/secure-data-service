package org.slc.sli.api.init;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

/**
 * Simple test for RoleInitializer
 */
public class RoleInitializerTest {
    
    private RoleInitializer roleInitializer;
    private EntityRepository mockRepo;
    

    @Before
    public void setUp() throws Exception {
        mockRepo = mock(EntityRepository.class);
        roleInitializer = new RoleInitializer();
        roleInitializer.setRepository(mockRepo);
    }

    @Test
    public void testAllRolesCreated() throws Exception {
        when(mockRepo.findAll("roles")).thenReturn(new ArrayList<Entity>());
        
        assertTrue(roleInitializer.buildRoles() == 5);

    }

    @Test
    public void testNoRolesCreated() throws Exception {
        List<Entity> entities = new ArrayList<Entity>();
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("name", RoleInitializer.EDUCATOR);
        entities.add(new MongoEntity("roles", body));
        body = new HashMap<String, Object>();
        body.put("name", RoleInitializer.IT_ADMINISTRATOR);
        entities.add(new MongoEntity("roles", body));
        body = new HashMap<String, Object>();
        body.put("name", RoleInitializer.LEADER);
        entities.add(new MongoEntity("roles", body));
        body = new HashMap<String, Object>();
        body.put("name", RoleInitializer.AGGREGATE_VIEWER);
        entities.add(new MongoEntity("roles", body));
        body = new HashMap<String, Object>();
        body.put("name", RoleInitializer.SLI_ADMINISTRATOR);
        entities.add(new MongoEntity("roles", body));
        when(mockRepo.findAll("roles")).thenReturn(entities);
        assertTrue(roleInitializer.buildRoles() == 0);
    }

    @Test
    public void testSomeRolesCreated() throws Exception {
        when(mockRepo.findAll("roles")).thenReturn(new Iterable<Entity>() {
            @Override
            public Iterator<Entity> iterator() {
                List<Entity> entities = new ArrayList<Entity>();
                Map<String, Object> body = new HashMap<String, Object>();
                body.put("name", RoleInitializer.EDUCATOR);
                entities.add(new MongoEntity("roles", body));
                body = new HashMap<String, Object>();
                body.put("name", RoleInitializer.IT_ADMINISTRATOR);
                entities.add(new MongoEntity("roles", body));
                return entities.iterator();
            }
        });
        assertTrue(roleInitializer.buildRoles() == 3);

    }
}
