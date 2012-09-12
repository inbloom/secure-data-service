package org.slc.sli.ingestion.transformation.normalization.did;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * DidEntityConfigFactory unit tests.
 *
 * @author ecole
 *
 */
public class DidEntityConfigFactoryTest {

    @Test
    public void testGetDidEntityConfig() {
        DidEntityConfigFactory factory = new DidEntityConfigFactory();
        factory.setResourceLoader(new DefaultResourceLoader());
        factory.setSearchPath("classpath:didEntityConfigs/");
        factory.setup();
        DidEntityConfig ec = factory.getDidEntityConfiguration("dummyTestAssociation");

        Assert.assertNotNull(ec);
        Assert.assertNotNull(ec.getReferenceSources());
        Assert.assertEquals(2, ec.getReferenceSources().size());
        Assert.assertEquals("dummy", ec.getReferenceSources().get(0).getEntityType());
        Assert.assertEquals("dummyId", ec.getReferenceSources().get(0).getDidFieldPath());
        Assert.assertEquals("dummyReference", ec.getReferenceSources().get(0).getSourceRefPath());

        DidEntityConfig ec2 = factory.getDidEntityConfiguration("dummyTestAssociation");

        Assert.assertSame(ec, ec2);
    }

    @Test
    public void testNoEntityConfig() {
        DidEntityConfigFactory factory = new DidEntityConfigFactory();
        factory.setResourceLoader(new DefaultResourceLoader());
        factory.setSearchPath("classpath:didEntityConfigs/");

        DidEntityConfig ec = factory.getDidEntityConfiguration("dummyTestAssociation2");

        Assert.assertNull(ec);
    }
}
