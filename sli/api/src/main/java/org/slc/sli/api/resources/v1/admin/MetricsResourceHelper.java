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

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Helper class for calculating system usage metrics.
 *
 */
public class MetricsResourceHelper {

    /**
     * Collect metrics for a collection, qualified by the field with name 'qualifier'
     * and value 'id'.
     *
     * @param qualifier
     * @param id
     * @param coll
     * @return
     */
    public static CollectionMetric gatherCollectionMetrics(final String qualifier, final String id, DBCollection coll) {
        CollectionMetric metrics = new CollectionMetric(0, 0.0);
        CommandResult stats = coll.getStats();


        long count = 0;
        if (qualifier == null || id == null) {
            count = coll.count();
        } else {
            coll.count(QueryBuilder.start(qualifier).is(id).get());
        }
        if (count > 0) {
            double size = (stats.getDouble("avgObjSize") + (stats.getDouble("totalIndexSize") / count)) * count;
            metrics.entityCount = count;
            metrics.size = size;
        }
        return metrics;
    }

    /**
     * Collect metrics for all collections, qualified by the field with name 'qualifier'
     * and value 'id'.
     *
     * @param repo
     * @param fieldKey
     * @param fieldValue
     * @return
     */
    public static CollectionMetrics getAllCollectionMetrics(Repository<Entity> repo, final String fieldKey,
            final String fieldValue) {

        CollectionMetrics metrics = new CollectionMetrics();

        for (DBCollection coll : repo.getCollections(false)) {

            CollectionMetric collMetrics = gatherCollectionMetrics(fieldKey, fieldValue, coll);

            if (collMetrics.entityCount > 0) {
                metrics.aggregate(coll.getName(), collMetrics.entityCount, collMetrics.size);
            }
        }

        return metrics;
    }
}