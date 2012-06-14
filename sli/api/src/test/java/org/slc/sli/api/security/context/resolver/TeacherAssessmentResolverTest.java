package org.slc.sli.api.security.context.resolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Tests for TeacherAssessmentResolver
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TeacherAssessmentResolverTest {

    @InjectMocks
    TeacherAssessmentResolver resolver = new TeacherAssessmentResolver();

    @Mock
    Repository<Entity> repository;


    @Test
    public void testCanResolve() {
        assertTrue(resolver.canResolve(EntityNames.TEACHER, EntityNames.ASSESSMENT));
        assertFalse(resolver.canResolve(EntityNames.STAFF, EntityNames.ASSESSMENT));
        assertFalse(resolver.canResolve(EntityNames.TEACHER, EntityNames.COHORT));
    }

    @Test
    public void testFindAssessible() {
        Mockito.when(repository.findAllIds(EntityNames.ASSESSMENT, null)).thenReturn(Arrays.asList("id1", "id2"));
        List<String> ids = resolver.findAccessible(null);
        assertNotNull(ids);
        assertEquals(2, ids.size());
        assertEquals("id1", ids.get(0));
        assertEquals("id2", ids.get(1));
    }
}
