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

package org.slc.sli.bulk.extract.pub;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * The the PublicDataFactory
 * @author ablum
 */
public class PublicDataFactoryTest {
    private PublicDataFactory factory = new PublicDataFactory();

    @Test
    public void testBuildPublicEdOrgExtract() {
        Assert.assertTrue(factory.buildDirectPublicDataExtract(null) != null);
        Assert.assertTrue(factory.buildDirectPublicDataExtract(null).getClass() == DirectPublicDataExtractor.class);
        PublicDataExtractor dpdExtr = factory.buildDirectPublicDataExtract(null);
    }

    @Test
    public void buildUnfilteredPublicDataExtractor() {
        Assert.assertTrue(factory.buildUnfilteredPublicDataExtractor(null) != null);
        Assert.assertTrue(factory.buildUnfilteredPublicDataExtractor(null).getClass() == UnfilteredPublicDataExtractor.class);
        PublicDataExtractor dpdExtr = factory.buildUnfilteredPublicDataExtractor(null);
    }

    @Test
    public void buildIndependentPublicDataExtractor() {
        Assert.assertTrue(factory.buildIndependentPublicDataExtractor(null) != null);
        Assert.assertTrue(factory.buildIndependentPublicDataExtractor(null).getClass() == DirectAndIndependentPublicDataExtractor.class);
        PublicDataExtractor dpdExtr = factory.buildUnfilteredPublicDataExtractor(null);
    }

    @Test
    public void testBuildAllPublicDataExtracts() {
        Assert.assertTrue(factory.buildAllPublicDataExtracts(null) != null);
        List<PublicDataExtractor> extractors = factory.buildAllPublicDataExtracts(null);
        Assert.assertEquals(extractors.size(), 3);
        Assert.assertTrue(extractors.get(0).getClass() == DirectPublicDataExtractor.class);
        Assert.assertTrue(extractors.get(1).getClass() == UnfilteredPublicDataExtractor.class);
        Assert.assertTrue(extractors.get(2).getClass() == DirectAndIndependentPublicDataExtractor.class);

    }

}
