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

package org.slc.sli.ingestion.transformation.normalization;

import java.util.List;

import com.mongodb.BasicDBList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 *
 * Helper component to add context to an entity
 *
 */
@Component
public class ContextTaker {

    @Autowired
    @Qualifier(value = "mongoEntityRepository")
    private Repository<Entity> entityRepository;

    public void addContext(Entity entity, List<String> takesContext, String collection, Query filter, List<String> ids) {
        // if takes context is set, once records are queried for, peel off metaData.[Takes] and
        // store on current record
        // cannot check in cache --> need whole records for metaData propagation
        // update cache with query results
        @SuppressWarnings("deprecation")
        Iterable<Entity> foundRecords = entityRepository.findByQuery(collection, filter, 0, 0);

        if (foundRecords != null && foundRecords.iterator().hasNext()) {
            // for each string in takesContext array
            // -> metaData.get(takesField) --> get context in metaData
            // -> add context to local record
            // -> add entity id to ids array (normal part of id normalization)
            for (String takesField : takesContext) {
                for (Entity record : foundRecords) {
                    if (record.getMetaData().containsKey(takesField)) {
                        BasicDBList addToContext = (BasicDBList) record.getMetaData().get(takesField);

                        if (entity.getMetaData().containsKey(takesField)) {
                            BasicDBList original = (BasicDBList) entity.getMetaData().get(takesField);
                            for (int i = 0; i < addToContext.size(); i++) {
                                String context = (String) addToContext.get(i);
                                if (!original.contains(context)) {
                                    original.add(context);
                                }
                            }
                            entity.getMetaData().put(takesField, original);
                        } else {
                            entity.getMetaData().put(takesField, addToContext);
                        }
                    }
                    ids.add(record.getEntityId());
                }
            }
        }
    }
}
