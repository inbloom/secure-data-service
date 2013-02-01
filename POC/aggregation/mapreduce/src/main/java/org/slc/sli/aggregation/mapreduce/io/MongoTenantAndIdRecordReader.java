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

import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.io.Text;
import org.bson.BSONObject;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;
import org.slc.sli.aggregation.mapreduce.map.key.TenantAndIdEmittableKey;
import org.slc.sli.aggregation.util.BSONUtilities;

/**
 * MongoTenantAndIdRecordReader
 */
public class MongoTenantAndIdRecordReader extends MongoIdRecordReader {

    public MongoTenantAndIdRecordReader(MongoInputSplit split) {
        super(split);
    }

    @Override
    public EmittableKey getCurrentKey() {
        BSONObject obj = privateReader.getCurrentValue();

        String id = BSONUtilities.getValue(obj, keyField);
        String tenantId = BSONUtilities.getValue(obj, "metaData.tenantId");

        TenantAndIdEmittableKey rval = new TenantAndIdEmittableKey();
        rval.setId(new Text(id));
        rval.setTenantId(new Text(tenantId));
        return rval;
    }

    @Override
    public BSONWritable getCurrentValue() {
        return new BSONWritable(privateReader.getCurrentValue());
    }

}
