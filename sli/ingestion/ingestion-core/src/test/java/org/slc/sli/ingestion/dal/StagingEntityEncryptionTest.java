package org.slc.sli.ingestion.dal;

import java.lang.reflect.Field;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;

/**
 *Unit Test for StagingEntityEncryption class.
 *
 * @author npandey
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class StagingEntityEncryptionTest {

    @Autowired
    private StagingEntityEncryption stagingEntityEncryption;

    @Test
    public void testValidBuildPIIMap() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        String entityType = "student";
        EntityConfig ec = Mockito.mock(EntityConfig.class);

        EntityConfigFactory entityConfigFactory = Mockito.mock(EntityConfigFactory.class);
        Field entityConfigFactoryField = stagingEntityEncryption.getClass().getDeclaredField("entityConfigFactory");
        entityConfigFactoryField.setAccessible(true);
        entityConfigFactoryField.set(stagingEntityEncryption, entityConfigFactory);
        Mockito.when(entityConfigFactory.getEntityConfiguration(entityType)).thenReturn(ec);

        stagingEntityEncryption.buildPiiMap(entityType);
        Mockito.verify(ec, Mockito.atLeastOnce()).getPiiFields();
    }

    @Test
    public void testInValidBuildPIIMap() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        String entityType = "InValidEntity";

        EntityConfigFactory entityConfigFactory = Mockito.mock(EntityConfigFactory.class);
        Field entityConfigFactoryField = stagingEntityEncryption.getClass().getDeclaredField("entityConfigFactory");
        entityConfigFactoryField.setAccessible(true);
        entityConfigFactoryField.set(stagingEntityEncryption, entityConfigFactory);
        Mockito.when(entityConfigFactory.getEntityConfiguration(entityType)).thenReturn(null);

        StagingEntityEncryption se = Mockito.spy(stagingEntityEncryption);
        Assert.assertNull(se.buildPiiMap(entityType));
    }

}
