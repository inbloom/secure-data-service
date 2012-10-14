/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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
