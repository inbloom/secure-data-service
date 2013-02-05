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

package org.slc.sli.aggregation.mapreduce.io;

import static org.junit.Assert.assertTrue;

import com.mongodb.DBCollection;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * MongoAggFormatterTest
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserGroupInformation.class, MongoConfigUtil.class })
public class MongoAggFormatterTest {

    /**
     * Test method for
     * {@link org.slc.sli.aggregation.mapreduce.io.MongoAggFormatter#getRecordWriter(org.apache.hadoop.mapreduce.TaskAttemptContext)}
     * .
     *
     * @throws Exception
     */
    @Test
    public void testGetRecordWriter() throws Exception {

        DBCollection mockCollection = Mockito.mock(DBCollection.class);
        UserGroupInformation ugi = Mockito.mock(UserGroupInformation.class);

        PowerMockito.mockStatic(UserGroupInformation.class);

        Mockito.when(UserGroupInformation.getCurrentUser()).thenReturn(ugi);

        TaskAttemptContext c = new MockTaskAttemptContext();
        Configuration config = c.getConfiguration();

        PowerMockito.mockStatic(MongoConfigUtil.class);
        Mockito.when(MongoConfigUtil.getOutputCollection(config)).thenReturn(mockCollection);

        MongoAggFormatter f = new MongoAggFormatter();
        assertTrue(f.getRecordWriter(new MockTaskAttemptContext()) instanceof MongoAggWriter);
    }
}
