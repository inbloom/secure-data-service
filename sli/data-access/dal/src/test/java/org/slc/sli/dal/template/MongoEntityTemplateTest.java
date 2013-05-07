/*
 *
 *  Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.slc.sli.dal.template;

import com.mongodb.*;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slc.sli.domain.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * MongoEntityTemplate test.
 * @author ablum
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoEntityTemplateTest {

    @Autowired
    private MongoEntityTemplate template;

    @Test
    public void testReadPreference() {
        template.setReadPreference(ReadPreference.secondary());

        Query query = new Query();
        template.findEach(query, Entity.class, "student");

        Assert.assertEquals(ReadPreference.secondary(), template.getCollection("student").getReadPreference());

    }
}
