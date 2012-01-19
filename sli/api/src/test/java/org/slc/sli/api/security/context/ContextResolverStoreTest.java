package org.slc.sli.api.security.context;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityExistsException;

import static org.junit.Assert.assertTrue;

/**
 * Tests for ContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ContextResolverStoreTest {

    @Autowired
    private ContextResolverStore contextResolverStore;
    private String source;
    private String target;
    private EntityContextResolver resolverIn;
    private String nonExistingField;

    @org.junit.Before
    public void setUp() throws Exception {
        source = "teacher";
        target = "student";
        resolverIn = Mockito.mock(EntityContextResolver.class);
        Mockito.when(resolverIn.getSourceType()).thenReturn(source);
        Mockito.when(resolverIn.getTargetType()).thenReturn(target);
        nonExistingField = "nonExistingField";
        contextResolverStore.clearContexts();
    }

    @Test
    public void testAddContextResolver() throws Exception {

        try {
            contextResolverStore.addContext(resolverIn);
        } catch (EntityExistsException e) {
            assertTrue("add on empty contextResolver should not throw exception", false);
        }

        try {
            contextResolverStore.addContext(resolverIn);
            Assert.fail("should throw EntityExistsException resolver already exists");
        } catch (EntityExistsException e) {
            Assert.assertNotNull(contextResolverStore);
        }

    }

    @Test
    public void testGetContextResolver() {

        try {
            contextResolverStore.addContext(resolverIn);
        } catch (EntityExistsException e) {
            assertTrue("add on empty contextResolver should not throw exception", false);
        }

        EntityContextResolver resolverOut = contextResolverStore.getContextResolver(source, target);
        Assert.assertNotNull(resolverOut);
        Assert.assertEquals(resolverIn, resolverOut);

        EntityContextResolver defaultResolver = contextResolverStore.getContextResolver(nonExistingField, nonExistingField);
        Assert.assertNotNull(defaultResolver);
        Assert.assertTrue(defaultResolver instanceof DefaultEntityContextResolver);

    }

}
