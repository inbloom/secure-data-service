package org.slc.sli.api.security.context.resolver;

import junit.framework.Assert;
import org.junit.Before;
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

/**
 * Tests for DenyAllContextResolver
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class DenyAllContextResolverTest {

    @Autowired
    private DenyAllContextResolver denyAllContextResolver;

    private String fromEntity;
    private String toEntity;
    private Entity principal;

    @Before
    public void setUp() {
        fromEntity = null;
        toEntity = null;
        principal = Mockito.mock(Entity.class);
    }

    @Test
    public void testCanResolve() throws Exception {
        Assert.assertFalse(denyAllContextResolver.canResolve(fromEntity, toEntity));
    }

    @Test
    public void testFindAccessible() throws Exception {
        Assert.assertTrue(denyAllContextResolver.findAccessible(principal).isEmpty());
    }
}
