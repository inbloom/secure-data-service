package org.slc.sli.api.security.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for FullContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class FullContextResolverTest {

    @Autowired
    FullContextResolver resolver;

    private static final String SOURCE = "source";
    private static final String TARGET = "source";
    private static final String FOO = "foo";


    @Before
    public void setUp() throws Exception {

        resolver.setSource(SOURCE);
        resolver.setTarget(TARGET);


        EntityDefinition definition = Mockito.mock(EntityDefinition.class);
        resolver.setDefinition(definition);

        Repository<Entity> repository = Mockito.mock(Repository.class);
        when(repository.findAll(any(String.class), anyInt(), anyInt()))
                .thenReturn(new ArrayList<Entity>());
        resolver.setRepository(repository);

    }

    @Test
    public void testGetSourceType() throws Exception {
        Assert.assertEquals(resolver.getSourceType(), SOURCE);
    }

    @Test
    public void testGetTargetType() throws Exception {
        Assert.assertEquals(resolver.getTargetType(), TARGET);
    }

    @Test
    public void testFindAccessible() throws Exception {
        EntityDefinition definition = Mockito.mock(EntityDefinition.class);
        when(definition.getStoredCollectionName()).thenReturn(TARGET);
        resolver.setDefinition(definition);

        Entity entity = Mockito.mock(Entity.class);
        when(entity.getEntityId()).thenReturn(FOO);
        Repository<Entity> repository = Mockito.mock(Repository.class);
        when(repository.findAll(any(String.class), anyInt(), anyInt()))
                .thenReturn(Arrays.<Entity>asList(entity));
        resolver.setRepository(repository);

        Entity principal = Mockito.mock(Entity.class);
        List<String> accessible = resolver.findAccessible(principal);

        verify(definition).getStoredCollectionName();
        verify(repository).findAll(eq(TARGET), anyInt(), anyInt());
        Assert.assertTrue(accessible.size() == 1);
        Assert.assertTrue(accessible.get(0).equals(FOO));
    }

    @Test
    public void testSetSource() throws Exception {
        Assert.assertEquals(resolver.getSourceType(), SOURCE);
        resolver.setSource(FOO);
        Assert.assertEquals(resolver.getSourceType(), FOO);
    }

    @Test
    public void testSetTarget() throws Exception {
        Assert.assertEquals(resolver.getTargetType(), TARGET);
        resolver.setTarget(FOO);
        Assert.assertEquals(resolver.getTargetType(), FOO);
    }

    @Test
    public void testSetRepository() throws Exception {
        Repository<Entity> repository = Mockito.mock(Repository.class);
        when(repository.findAll(any(String.class), anyInt(), anyInt()))
                .thenReturn(new ArrayList<Entity>());
        resolver.setRepository(repository);

        Entity principal = Mockito.mock(Entity.class);
        resolver.setRepository(repository);
        resolver.findAccessible(principal);
        verify(repository).findAll(any(String.class), anyInt(), anyInt());
    }

    @Test
    public void testSetDefinition() throws Exception {
        EntityDefinition definition = Mockito.mock(EntityDefinition.class);
        Entity principal = Mockito.mock(Entity.class);
        resolver.setDefinition(definition);
        resolver.findAccessible(principal);
        verify(definition).getStoredCollectionName();
    }
}
