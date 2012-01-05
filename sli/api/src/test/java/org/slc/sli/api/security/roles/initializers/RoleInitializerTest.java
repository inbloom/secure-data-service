package org.slc.sli.api.security.roles.initializers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slc.sli.dal.repository.EntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

import java.util.*;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        
        assertTrue(roleInitializer.buildRoles() == 4);

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
        assertTrue(roleInitializer.buildRoles() == 2);

    }
}
