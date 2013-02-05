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

import java.io.IOException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.output.MongoRecordWriter;

import org.apache.hadoop.mapreduce.TaskAttemptContext;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * M/R result aggregation records to mongo. The MongoAggWriter takes an EmittableKey used to
 * locate the record to write to, and a BSONObject to write.
 *
 */
public class MongoAggWriter extends MongoRecordWriter<EmittableKey, BSONWritable> {

    private final DBCollection output;

    public MongoAggWriter(DBCollection c, TaskAttemptContext ctx) {
        super(c, ctx);
        this.output = c;
    }

    @Override
    public void write(EmittableKey key, BSONWritable value) throws IOException {
        DBObject k = new BasicDBObject();
        k.putAll(key.toBSON());

        DBObject v = new BasicDBObject();
        v.put("$set", value);

        output.findAndModify(k, v);
    }
}
