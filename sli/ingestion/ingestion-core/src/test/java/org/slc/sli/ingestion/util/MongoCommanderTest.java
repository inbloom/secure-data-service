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
package org.slc.sli.ingestion.util;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoCommanderTest {

    @Autowired
    private MongoTemplate testMongoTemplate;

    @Resource
    private Set<String> testIndexes;

    private Set<String> shardCollections = new TreeSet<String>();

    public MongoTemplate getTestMongo() {
        return testMongoTemplate;
    }

    public void setTestMongo(MongoTemplate testMongo) {
        this.testMongoTemplate = testMongo;
    }

    public Set<String> getTestIndexes() {
        return testIndexes;
    }

    public void setTestIndexes(Set<String> testIndexes) {
        this.testIndexes = testIndexes;
    }

    @Before
    public void setup(){
        /*
        testIndexes.add("assessment,false,creationTime");
        testIndexes.add("assessmentFamily,false,creationTime");
        testIndexes.add("assessmentItem,false,creationTime");
        */

        shardCollections.add("assessment");
        shardCollections.add("assessmentFamily");
        shardCollections.add("assessmentItem");


    }


    public void testEnsureIndexes() {
        MongoCommander.ensureIndexes(testIndexes, "commanderTest2", testMongoTemplate);
    }

    @Test
    public void testPreSplit() {
        MongoCommander.preSplit(shardCollections, "commanderTest3", testMongoTemplate);
    }

}
