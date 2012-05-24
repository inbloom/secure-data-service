package org.slc.sli.api.security.context.resolver;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for AnythingToSchoolResolver
 * @author jstokes
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AnythingToSchoolResolverTest {

    @Autowired
    @InjectMocks
    AnythingToSchoolResolver anythingToSchoolResolver = new AnythingToSchoolResolver();

    @Mock
    private EntityDefinitionStore store;

    @Mock
    private Repository<Entity> repository;

    @Before
    public void setup() {
        anythingToSchoolResolver.toEntity = EntityNames.SCHOOL;
    }

    @Test
    public void testCanResolve() {
        assertTrue(anythingToSchoolResolver.canResolve("anything", EntityNames.SCHOOL));
        assertFalse(anythingToSchoolResolver.canResolve("anything", "anythingElse"));
    }

    @Test
    public void testFindAccessible() {
        EntityDefinition def = mock(EntityDefinition.class);
        when(def.getStoredCollectionName()).thenReturn(EntityNames.SCHOOL);
        when(store.lookupByEntityType(EntityNames.SCHOOL)).thenReturn(def);
        when(repository.findAllIds(EntityNames.SCHOOL, null)).thenReturn(createIdIterable());

        List<String> expected = new ArrayList<String>();
        expected.add("test-1234-id");
        expected.add("test-2345-id");
        expected.add("test-3456-id");

        assertEquals(expected, anythingToSchoolResolver.findAccessible(any(Entity.class)));
    }

    private Iterable<String> createIdIterable() {
        List<String> ret = new ArrayList<String>();
        ret.add("test-1234-id");
        ret.add("test-2345-id");
        ret.add("test-3456-id");
        return ret;
    }

}
