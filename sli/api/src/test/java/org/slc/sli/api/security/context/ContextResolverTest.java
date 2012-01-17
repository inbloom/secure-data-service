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
public class ContextResolverTest {

    @Autowired
    private ContextResolver contextResolver;
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
        contextResolver.clearContexts();
    }

    @Test
    public void testAddContextResolver() throws Exception {

        try {
            contextResolver.addContextResolver(resolverIn);
        } catch (EntityExistsException e) {
            assertTrue("add on empty contextResolver should not throw exception", false);
        }

        try {
            contextResolver.addContextResolver(resolverIn);
            Assert.fail("should throw EntityExistsException resolver already exists");
        } catch (EntityExistsException e) {
            Assert.assertNotNull(contextResolver);
        }

    }

    @Test
    public void testGetContextResolver() {

        try {
            contextResolver.addContextResolver(resolverIn);
        } catch (EntityExistsException e) {
            assertTrue("add on empty contextResolver should not throw exception", false);
        }

        EntityContextResolver resolverOut = contextResolver.getContextResolver(source, target);
        Assert.assertNotNull(resolverOut);
        Assert.assertEquals(resolverIn, resolverOut);

        EntityContextResolver defaultResolver = contextResolver.getContextResolver(nonExistingField, nonExistingField);
        Assert.assertNotNull(defaultResolver);
        Assert.assertTrue(defaultResolver instanceof DefaultEntityContextResolver);

    }

}
