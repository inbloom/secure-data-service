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
package org.slc.sli.common.util.uuid;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("deterministicUUIDGeneratorStrategy")
public class DeterministicUUIDGeneratorStrategy implements UUIDGeneratorStrategy {
    
    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    ShardType1UUIDGeneratorStrategy oldStrategy;
    
    @Override
    public String randomUUID() {
        return oldStrategy.randomUUID();
    }
    
    @Override
    public String randomUUID(Map<String, String> naturalKeys) {
        // TODO: Replace with real code
        return oldStrategy.randomUUID();
        
        // String key;
        // if (naturalKeys != null && naturalKeys != null && naturalKeys.size() > 0) {
        // key = UUID.randomUUID().toString() + "||";
        // for (Entry<String, String> entry : naturalKeys.entrySet()) {
        // key += entry.getKey() + ":" + entry.getValue() + "||";
        // }
        // } else {
        // key = UUID.randomUUID().toString();
        //
        // }
        // return key;
    }
    
}
