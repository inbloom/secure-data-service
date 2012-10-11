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

package org.slc.sli.api.resources.v1.admin;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Captures the the number of records and estimated total size of all rows in a collection.
 */
class CollectionMetric implements Serializable {
    protected long entityCount;
    protected double size;

    CollectionMetric(long recordCount, double size) {
        entityCount = recordCount;
        this.size = size;
    }

    public long getEntityCount() {
        return entityCount;
    }

    public double getSize() {
        return size;
    }

    public void add(CollectionMetric c) {
        entityCount += c.entityCount;
        size += c.size;
    }
}

/**
 * Captures metrics for all collections in the mongo database, organized by collection name.
 */
public class CollectionMetrics extends HashMap<String, CollectionMetric> {

    private static final long serialVersionUID = -5616329370579405926L;
    private CollectionMetric allCollections = new CollectionMetric(0, 0.0);

    public CollectionMetrics aggregate(final String collectionName, long entityCount, double size) {

        if (containsKey(collectionName)) {
            CollectionMetric c = get(collectionName);
            c.entityCount += entityCount;
            c.size += size;
        } else {
            put(collectionName, new CollectionMetric(entityCount, size));
        }

        allCollections.entityCount += entityCount;
        allCollections.size += size;

        return this;
    }

    public CollectionMetric getTotals() {
        return allCollections;
    }

}
