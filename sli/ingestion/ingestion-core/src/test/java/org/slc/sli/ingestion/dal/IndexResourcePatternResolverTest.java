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


package org.slc.sli.ingestion.dal;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * A Junit test for IndexResourcePatternResolver
 * @author ablum
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class IndexResourcePatternResolverTest {
    @Autowired
    IndexResourcePatternResolver irpr;

    @Test
    public void testFindAllIndexes() {
        String indexRootDir = "classpath:mongoIndexes/**/*.json";
        List<MongoIndexConfig> indexes = irpr.findAllIndexes(indexRootDir);

        Assert.assertEquals(1, indexes.size());
        Assert.assertEquals("student", indexes.get(0).getCollection());

        List<Map<String, String>> fields1 = indexes.get(0).getIndexFields();
        Assert.assertEquals(6, fields1.size());
        Assert.assertEquals("body.name", fields1.get(0).get("name"));
        Assert.assertEquals("body.sex", fields1.get(1).get("name"));
        Assert.assertEquals("body.birthDate", fields1.get(2).get("name"));
    }

}
