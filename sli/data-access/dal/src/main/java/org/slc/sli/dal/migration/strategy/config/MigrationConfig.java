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

package org.slc.sli.dal.migration.strategy.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slc.sli.dal.migration.config.Strategy;

/**
 * 
 * @author sashton
 * 
 */
public class MigrationConfig {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Map<String, Map<Integer, List<Map<Strategy, Map<String, Object>>>>> entities;

    public MigrationConfig() {
        entities = new HashMap<String, Map<Integer, List<Map<Strategy, Map<String, Object>>>>>();
    }

    public Map<String, Map<Integer, List<Map<Strategy, Map<String, Object>>>>> getEntities() {
        return entities;
    }


    public static MigrationConfig parse(InputStream inputStream) throws IOException {
        return MAPPER.readValue(inputStream, MigrationConfig.class);
    }

    @Override
    public String toString() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (java.io.IOException e) {
            return super.toString();
        }
    }

}
