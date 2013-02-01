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


package org.slc.sli.ingestion.transformation.normalization;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

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
        factory.setResourceLoader(new DefaultResourceLoader());
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
        factory.setResourceLoader(new DefaultResourceLoader());
        factory.setSearchPath("classpath:smooksEdFi2SLI/");

        EntityConfig ec = factory.getEntityConfiguration("dummy2");

        Assert.assertNull(ec);
    }
}
