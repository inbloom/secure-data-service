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


package org.slc.sli.api.representation;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Contents of Home resource, for use in XML return
 */
//public class Home extends MutablePair<String, HashMap<String, Object>> {
public class Home extends HashMap<String, Object> {

    private static final long serialVersionUID = -8766900333518618999L;

    public Home(String entityCollectionName, HashMap<String, Object> linksMap) {
        super();
        this.put(entityCollectionName, linksMap);
    }

    public HashMap<String, Object> getLinksMap() {
        Entry<String, Object> collectionEntry = this.entrySet().iterator().next();
        @SuppressWarnings(value = "unchecked")
        HashMap<String, Object> linksMap = (HashMap<String, Object>) collectionEntry.getValue();
        return linksMap;
    }
}
