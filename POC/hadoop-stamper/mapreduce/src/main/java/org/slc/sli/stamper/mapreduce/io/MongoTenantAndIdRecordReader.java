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

package org.slc.sli.stamper.mapreduce.io;

import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.input.MongoRecordReader;
import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.bson.BSONObject;

import org.slc.sli.stamper.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.stamper.util.BSONUtilities;

/**
 * MongoTenantAndIdRecordReader
 *
 */
public class MongoTenantAndIdRecordReader extends RecordReader<TenantAndIdEmittableKey, BSONWritable> {

    protected MongoRecordReader privateReader = null;
    protected String keyField;

    public MongoTenantAndIdRecordReader(MongoInputSplit split) {
        privateReader = new MongoRecordReader(split);
        this.keyField = split.getKeyField();
    }

    @Override
    public void close() {
        privateReader.close();
    }

    @Override
    public TenantAndIdEmittableKey getCurrentKey() {
        BSONObject obj = privateReader.getCurrentValue();

        String id = BSONUtilities.getValue(obj, keyField);
        String tenantId = BSONUtilities.getValue(obj, "metaData.tenantId");

        TenantAndIdEmittableKey rval = new TenantAndIdEmittableKey("metaData.tenantId", keyField);
        rval.setId(new Text(id));
        rval.setTenantId(new Text(tenantId));
        return rval;
    }

    @Override
    public BSONWritable getCurrentValue() {
        return new BSONWritable(privateReader.getCurrentValue());
    }

    @Override
    public float getProgress() {
        return privateReader.getProgress();
    }

    @Override
    public void initialize(InputSplit split, org.apache.hadoop.mapreduce.TaskAttemptContext context) {
        privateReader.initialize(split, context);
    }

    @Override
    public boolean nextKeyValue() {
        return privateReader.nextKeyValue();
    }
}
