/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
@ContextConfiguration(locations = { "/spring/BatchJob-Mongo.xml" })
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
