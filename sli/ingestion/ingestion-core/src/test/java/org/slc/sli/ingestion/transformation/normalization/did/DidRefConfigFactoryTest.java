package org.slc.sli.ingestion.transformation.normalization.did;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * DidRefConfigFactory unit tests.
 *
 * @author ecole
 *
 */
public class DidRefConfigFactoryTest {

    @Test
    public void testGetDidRefConfig() {
        DidRefConfigFactory factory = new DidRefConfigFactory();
        factory.setResourceLoader(new DefaultResourceLoader());
        factory.setSearchPath("classpath:didRefConfigs/");
        DidRefConfig ec = factory.getDidRefConfiguration("dummy");

        Assert.assertNotNull(ec);
        Assert.assertNotNull(ec.getKeyFields());
        Assert.assertEquals(1, ec.getKeyFields().size());
        Assert.assertEquals("dummy", ec.getEntityType());
        Assert.assertEquals("dummyUniqueStateId", ec.getKeyFields().get(0).getKeyFieldName());
        Assert.assertEquals("dummyIdentity.dummyUniqueStateId", ec.getKeyFields().get(0).getValueSource());

        DidRefConfig ec2 = factory.getDidRefConfiguration("dummy");

        Assert.assertSame(ec, ec2);
    }

    @Test
    public void testNoEntityConfig() {
        DidRefConfigFactory factory = new DidRefConfigFactory();
        factory.setResourceLoader(new DefaultResourceLoader());
        factory.setSearchPath("classpath:didRefConfigs/");

        DidRefConfig ec = factory.getDidRefConfiguration("dummy2");

        Assert.assertNull(ec);
    }
}
