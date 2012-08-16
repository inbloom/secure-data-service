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

package org.slc.sli.aggregation.mapreduce.map;

import java.io.IOException;

import com.mongodb.hadoop.util.MongoConfigUtil;

import org.apache.hadoop.conf.Configuration;

import org.slc.sli.aggregation.mapreduce.map.key.EmittableKey;

/**
 * CmdLineQueryMapper - Mapper that takes the map query on the command line as a standard
 * mongo query string.
 */
public class CmdLineQueryMapper extends IDMapper {

    @Override
    public void setup(Context context) throws InterruptedException, IOException {
        super.setup(context);

        Configuration conf = context.getConfiguration();

        String[] propertyNames =
            conf.getStrings(ConfigurableMapper.mapper_entry.COMMAND_LINE_QUERY_PROPERTIES
                .toString());

        if (propertyNames == null) {
            throw new IllegalArgumentException(
                "Invalid command line mapper specified. No command line properties were found.");
        }

        String query = "{";
        for (String s : propertyNames) {
            String prop = conf.get(s);
            if (prop == null) {
                throw new IllegalArgumentException(
                    "Invalid mapper specified. The configured command line property ('%s') was not given.");
            }
            query = query + String.format("\"%s\":\"%s\"", s, prop);
        }
        query = query + "}";

        MongoConfigUtil.setQuery(conf, query);
    }

    public CmdLineQueryMapper(Class<? extends EmittableKey> keyType, String[] keyFields)
        throws InstantiationException, IllegalAccessException {
        super(keyType, keyFields);
    }
}
