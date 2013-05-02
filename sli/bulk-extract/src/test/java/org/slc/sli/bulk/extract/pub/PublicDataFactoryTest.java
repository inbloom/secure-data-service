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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ablum
 * Date: 5/1/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PublicDataFactoryTest {
    private PublicDataFactory factory = new PublicDataFactory();

    @Test
    public void testBuildPublicEdOrgExtract() {
        Assert.assertTrue(factory.buildDirectPublicDataExtract(null) != null);
        Assert.assertTrue(factory.buildDirectPublicDataExtract(null).getClass() == DirectPublicDataExtract.class);
    }

    @Test
    public void testBuildAllPublicDataExtracts() {
        Assert.assertTrue(factory.buildAllPublicDataExtracts(null) != null);
        List<PublicDataExtract> extractors = factory.buildAllPublicDataExtracts(null);
        Assert.assertEquals(extractors.size(), 1);
        Assert.assertTrue(extractors.get(0).getClass() == DirectPublicDataExtract.class);
    }

}
