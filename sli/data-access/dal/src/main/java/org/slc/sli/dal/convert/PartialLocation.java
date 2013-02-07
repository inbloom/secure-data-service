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

package org.slc.sli.dal.convert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.domain.Entity;

/**
 * Subdoc location for entities that are only parts of larger entities and are not accessible
 * themselves
 *
 * @author nbrown
 *
 */
public class PartialLocation extends SubDocLocation {

    public PartialLocation(SubDocAccessor subDocAccessor, String collection, Map<String, String> lookup,
            String subField, String key) {
        super(subDocAccessor, collection, lookup, subField, key);
    }

    @Override
    public boolean doUpdate(Query query, Update update) {
        // has to be patched as part of the larger entity
        throw new UnsupportedOperationException();
    }

    @Override
    public Entity findById(String id) {
        // partial entities do not have ids
        return null;
    }

    @Override
    public List<Entity> findAll(Query originalQuery) {
        // partial entities cannot be queried on their own
        return Collections.emptyList();
    }

    @Override
    public boolean exists(String id) {
        // partial entities cannot be queried on their own
        throw new UnsupportedOperationException();
    }

    @Override
    public long count(Query query) {
        // partial entities cannot be queried on their own
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(Query query) {
        // partial entities cannot be queried on their own
        throw new UnsupportedOperationException();
    }

    @Override
    protected DBObject buildPullObject(List<Entity> subEntities) {
        Criteria[] crits = new Criteria[subEntities.size()];
        int i = 0;
        for (Entity e : subEntities) {
            Map<String, Object> body = e.getBody();
            if (body != null) {
                String field = getKey();
                crits[i++] = new Criteria(field).is(body.get(field));
            }
        }
        Query query = new Query(new Criteria().orOperator(crits));
        Update update = new Update();
        update.pull(this.getSubField(), query.getQueryObject());
        return update.getUpdateObject();
    }

    @Override
    protected DBObject subDocToDBObject(Entity entity) {
        Map<String, Object> map = entity.getBody();
        for (String key : this.getLookup().keySet()) {
            map.remove(key);
        }
        return new BasicDBObject(map);
    }

}
