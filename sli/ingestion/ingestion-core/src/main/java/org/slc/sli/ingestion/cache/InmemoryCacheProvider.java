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


package org.slc.sli.ingestion.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * HashMap cache for dev testing.
 *
 */
public class InmemoryCacheProvider implements CacheProvider {

    private Map<String, Object> cache = new ConcurrentHashMap<String, Object>();

    @Override
    public void add(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public void flush() {
       cache.clear();
    }

}
