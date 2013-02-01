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


package org.slc.sli.ingestion.queue;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;

/**
 *
 * Builds items that can be placed onto the queue for worker processing.
 *
 * @author smelody
 *
 */
@Component
public class ItemBuilder {

    @Autowired
    @Qualifier("shardType1UUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    public Map<String, Object> buildNewItem() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("_id", uuidGeneratorStrategy.generateId());
        map.put(ItemKeys.STATE, ItemValues.UNCLAIMED);

        return map;
    }
}
