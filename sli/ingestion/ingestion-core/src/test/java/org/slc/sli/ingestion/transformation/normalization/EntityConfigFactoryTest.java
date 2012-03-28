package org.slc.sli.ingestion.transformation.normalization;

import org.junit.Assert;
import org.junit.Test;

/**
 * EntityConfigFactory unit tests.
 *
 * @author okrook
 *
 */
public class EntityConfigFactoryTest {

    @Test
    public void testGetEntityConfig() {
        EntityConfigFactory factory = new EntityConfigFactory();
        factory.setSearchPath("classpath:smooksEdFi2SLI/");

        EntityConfig ec = factory.getEntityConfiguration("dummy");

        Assert.assertNotNull(ec);
        Assert.assertNotNull(ec.getKeyFields());
        Assert.assertEquals(1, ec.getKeyFields().size());
        Assert.assertEquals("pk", ec.getKeyFields().get(0));

        EntityConfig ec2 = factory.getEntityConfiguration("dummy");

        Assert.assertSame(ec, ec2);
    }

    @Test
    public void testNoEntityConfig() {
        EntityConfigFactory factory = new EntityConfigFactory();
        factory.setSearchPath("classpath:smooksEdFi2SLI/");

        EntityConfig ec = factory.getEntityConfiguration("dummy2");

        Assert.assertNull(ec);
    }
}
