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
package org.slc.sli.dal.repository;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.QueryMapper;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;

/**
 * Provides journaling of entity updates for delta processing
 *
 * @author nbrown
 *
 */
@Component
public class DeltaJournal {

    @Value("${sli.bulk.extract.deltasEnabled:false}")
    private boolean isEnabled;

    public static final String DELTA_COLLECTION = "deltas";

    @Autowired
    @Qualifier("journalTemplate")
    private MongoTemplate template;

    public void journal(List<String> ids, String collection, boolean isDelete) {
        if (isEnabled) {
            long now = new Date().getTime();
            Update update = new Update();
            update.set("t", now);
            update.set("c", collection);
            if (isDelete) {
                update.set("d", now);
            } else {
                update.set("u", now);
            }
            for (String id : ids) {
                template.upsert(Query.query(Criteria.where("_id").is(id)), update, DELTA_COLLECTION);
            }
        }
    }

    public void journal(String id, String collection, boolean isDelete) {
        journal(Arrays.asList(id), collection, isDelete);
    }

    /*
     * must break spring data encapsulation again since Delta records aren't "entities", hence we
     * can not use mongoEntityRepository
     * 
     * Change this into a paging iterator
     */
    public Iterator<Map<String, Object>> findDeltaRecordBetween(long start, long end) {
        // Delta collection is always tenant aware
        TenantContext.setIsSystemCall(false);
        DBCollection collection = template.getDb().getCollection(DELTA_COLLECTION);
        final MongoConverter converter = template.getConverter();
        QueryMapper mapper = new QueryMapper(converter);
        MongoPersistentEntity<?> entity = converter.getMappingContext().getPersistentEntity(Map.class);
        Query query = buildDeltaQuery(start, end);
        DBObject dbQuery = mapper.getMappedObject(query.getQueryObject(), entity);

        final DBCursor cursor;

        if (query.getFieldsObject() == null) {
            cursor = collection.find(dbQuery);
        } else {
            cursor = collection.find(dbQuery, query.getFieldsObject());
        }

        return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
                return cursor.hasNext();
            }
            
            @SuppressWarnings("unchecked")
            @Override
            public Map<String, Object> next() {
                return converter.read(Map.class, cursor.next());
            }
            
            @Override
            public void remove() {
                cursor.remove();
            }
        };
    }
    
    private Query buildDeltaQuery(long lastDeltaTime, long uptoTime) {
        Criteria timeElasped = Criteria.where("t").gte(lastDeltaTime).lt(uptoTime);
        Query q = Query.query(timeElasped);
        return q;
    }
}
