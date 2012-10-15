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
package org.slc.sli.api.util;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * @author tke
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class MongoCommanderTest {

    @Test
    public void test() {
        MongoCommander.exec("api_test", "test_indexes.js", " ");

        try {
            Mongo mongo = new Mongo();
            DB db = mongo.getDB("api_test");
            DBCollection  assessment = db.getCollection("assessment");
            DBCollection attendance = db.getCollection("attendance");

            List<DBObject> assessmentIndexes = assessment.getIndexInfo();
            List<DBObject> attendanceIndexes = attendance.getIndexInfo();

            Map<String, Integer> temp = (Map<String, Integer>)assessmentIndexes.get(0).get("key");
            Assert.assertEquals(1,(int)temp.get("_id"));
            Map<String, Double> attIndex = (Map<String, Double>)attendanceIndexes.get(1).get("key");
            //Not quite sure why it is double in Mongo
            Assert.assertEquals(-1.0, (double)attIndex.get("metaData.edOrgs"));
            db.dropDatabase();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MongoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
