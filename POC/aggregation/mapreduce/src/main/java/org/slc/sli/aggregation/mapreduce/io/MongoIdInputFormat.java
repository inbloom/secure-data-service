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

import java.util.List;

import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.io.BSONWritable;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * MongoIdInputFormat
 */
public class MongoIdInputFormat extends InputFormat<EmittableKey, BSONWritable> {

    protected MongoInputFormat privateFormat = new MongoInputFormat();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context) {
        if (!(split instanceof MongoInputSplit)) {
            throw new IllegalStateException("Creation of a new MongoIdInputFormat requires a MongoInputSplit instance.");
        }

        final MongoInputSplit mis = (MongoInputSplit) split;
        return new MongoIdRecordReader(mis);
    }

    @Override
    public List<InputSplit> getSplits(JobContext context) {
        return privateFormat.getSplits(context);
    }


    public boolean verifyConfiguration(Configuration conf) {
        return true;
    }

}
