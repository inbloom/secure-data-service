/**
 * 
 */
package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/**
 * @author rlatta
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class PathFindingContextResolverTest {
    
    @Autowired
    private PathFindingContextResolver resolver;
    
    private Repository<Entity> mockRepo;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        mockRepo = Mockito.mock(Repository.class);
        resolver.setRepository(mockRepo);
    }

    @Test
    public void testCanResolve() throws Exception {
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        assertTrue(resolver.canResolve(EntityNames.STUDENT, EntityNames.TEACHER));
        assertFalse(resolver.canResolve(EntityNames.AGGREGATION, EntityNames.TEACHER));
    }
    
    @Test
    public void testFindTeacherToSections() throws Exception {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");
        List<String> finalList = Arrays.asList(new String[] { "2", "3", "4" });
        
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.SECTION));
        
        when(mockRepo.findAllIds(eq(EntityNames.SECTION), any(NeutralQuery.class))).thenReturn(finalList);
        List<String> returned = resolver.findAccessible(mockEntity);
        assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }

    }
    
    @Test
    public void testFindTeacherToStudent() throws Exception {
        Entity mockEntity = Mockito.mock(Entity.class);
        when(mockEntity.getEntityId()).thenReturn("1");
        List<String> finalList = Arrays.asList(new String[] { "5", "6", "7" });
        
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.STUDENT));
        
        when(mockRepo.findAllIds(eq(EntityNames.SECTION), any(NeutralQuery.class))).thenReturn(
                Arrays.asList(new String[] { "2", "3", "4" }));
        when(mockRepo.findAllIds(eq(EntityNames.STUDENT), any(NeutralQuery.class))).thenReturn(finalList);
        List<String> returned = resolver.findAccessible(mockEntity);
        assertTrue(returned.size() == finalList.size());
        for (String id : finalList) {
            assertTrue(returned.contains(id));
        }
        
    }

}
