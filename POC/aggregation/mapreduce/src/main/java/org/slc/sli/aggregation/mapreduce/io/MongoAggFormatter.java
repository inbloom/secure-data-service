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

import com.mongodb.hadoop.MongoOutputFormat;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.RecordWriter;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * output mongo reduce results to a collection.
 *
 */
public class MongoAggFormatter extends MongoOutputFormat<EmittableKey, BSONWritable> {

    public static final String KEY_FIELD = "mongo.output.key.field";
    public static final String UPDATE_FIELD = "mongo.output.update.field";

    @Override
    public RecordWriter<EmittableKey, BSONWritable> getRecordWriter(
        org.apache.hadoop.mapreduce.TaskAttemptContext context) {
        Configuration config = context.getConfiguration();
        return new MongoAggWriter(MongoConfigUtil.getOutputCollection(config), context);
    }
}
