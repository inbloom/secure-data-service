package org.slc.sli.api.security.context;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.List;

/**
 * Tests for DenyAllContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DenyAllContextResolverTest {

    @Autowired
    DenyAllContextResolver resolver;

    @Test
    public void testFindAccessible() throws Exception {
        Entity principalEntity = Mockito.mock(Entity.class);
        List<String> accessible = resolver.findAccessible(principalEntity);
        Assert.assertTrue(accessible.isEmpty());
    }

    @Test
    public void testGetSourceType() throws Exception {
        Assert.assertNull(resolver.getSourceType());
    }

    @Test
    public void testGetTargetType() throws Exception {
        Assert.assertNull(resolver.getTargetType());
    }
}
