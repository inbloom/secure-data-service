package org.slc.sli.api.security;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.schema.SchemaDataProvider;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.enums.Right;

/**
 *
 * @author dkornishev
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
public class SchemaDataProviderTest {

    private static final String RESTRICTED_FIELD = "economicDisadvantaged";

    private static final String ENTITY_TYPE      = "student";

    @Resource
    private SchemaDataProvider  provider;

    @Test
    public void testGeneral() {
        Assert.assertEquals(Right.READ_GENERAL, provider.getRequiredReadLevel(ENTITY_TYPE, "studentUniqueStateId"));
        Assert.assertEquals(Right.WRITE_GENERAL, provider.getRequiredWriteLevel(ENTITY_TYPE, "studentUniqueStateId"));
    }

    @Test
    public void testRestricted() {
        Assert.assertEquals(Right.READ_RESTRICTED, provider.getRequiredReadLevel(ENTITY_TYPE, RESTRICTED_FIELD));
        Assert.assertEquals(Right.WRITE_RESTRICTED, provider.getRequiredWriteLevel(ENTITY_TYPE, RESTRICTED_FIELD));
    }

    @Test
    public void testDeepTraversal() {
        Assert.assertEquals(Right.READ_GENERAL, provider.getRequiredReadLevel(ENTITY_TYPE, "name.firstName"));
    }

    @Test
    public void testSphere() {
        Assert.assertEquals("CDM", this.provider.getDataSphere(ENTITY_TYPE));
        Assert.assertEquals("Admin", this.provider.getDataSphere("realm"));
    }
}
