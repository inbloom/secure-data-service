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

package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 *
 * @param <T>
 */
@Component
public class PagingRepositoryDelegate<T> implements Repository<T> {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<T> repo;

    // The size of queries we page around.
    @Value("${sli.api.security.context.paging}")
    private int count;

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#createWithRetries(java.lang.String, java.lang.String,
     * java.util.Map, java.util.Map, java.lang.String, int)
     */
    @Override
    public T createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        return repo.createWithRetries(type, id, body, metaData, collectionName, noOfRetries);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#updateMulti(org.slc.sli.domain.NeutralQuery,
     * java.util.Map, java.lang.String)
     */
    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
        return repo.updateMulti(query, update, entityReferenced);
    }

    private Set<String> getQueriedIds(NeutralQuery query) {
        Set<String> startingIds = new HashSet<String>();
        for (NeutralQuery orQuery : query.getOrQueries()) {
            for (NeutralCriteria criteria : orQuery.getCriteria()) {
                if (criteria.getKey().equals(ParameterConstants.ID)) {
                    startingIds.addAll((List) criteria.getValue());
                }
            }
        }
        return startingIds;
    }

    private NeutralQuery adjustIdsInQuery(List<String> ids, NeutralQuery query) {
        for (NeutralQuery orQuery : query.getOrQueries()) {
            for (NeutralCriteria criteria : orQuery.getCriteria()) {
                if (criteria.getKey().equals(ParameterConstants.ID)) {
                    criteria.setValue(ids);
                }
            }
        }
        return query;
    }

    public List<List<String>> extractBrokenListOfIds(List<String> queriedIds) {
        List<List<String>> brokenList = new ArrayList<List<String>>();
        int blocks = queriedIds.size() / count;
        if (queriedIds.size() % count > 0) {
            blocks++;
        }
        for (int i = 0; i < blocks; ++i) {
            List<String> part = new ArrayList<String>();
            int offSet = queriedIds.size() - i * count;
            if (offSet > count) {
                offSet = count;
            }
            part.addAll(queriedIds.subList(i * count, i * count + offSet));
            brokenList.add(part);
        }
        return brokenList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#create(java.lang.String, java.util.Map)
     */
    @Override
    public T create(String type, Map<String, Object> body) {
        return repo.create(type, body);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#create(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public T create(String type, Map<String, Object> body, String collectionName) {
        return repo.create(type, body, collectionName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#create(java.lang.String, java.util.Map, java.util.Map,
     * java.lang.String)
     */
    @Override
    public T create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        return repo.create(type, body, metaData, collectionName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#insert(java.util.List, java.lang.String)
     */
    @Override
    public List<T> insert(List<T> records, String collectionName) {
        return repo.insert(records, collectionName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#findById(java.lang.String, java.lang.String)
     */
    @Override
    public T findById(String collectionName, String id) {
        return repo.findById(collectionName, id);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#exists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean exists(String collectionName, String id) {
        return repo.exists(collectionName, id);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#findOne(java.lang.String, org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public T findOne(String collectionName, NeutralQuery neutralQuery) {
        return repo.findOne(collectionName, neutralQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#findAll(java.lang.String, org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public Iterable<T> findAll(String collectionName, NeutralQuery neutralQuery) {
        List<String> queriedIds = new ArrayList<String>(getQueriedIds(neutralQuery));
        // This is an insecure query, let it through.
        if (queriedIds.size() == 0) {
            return repo.findAll(collectionName, neutralQuery);
        }
        List<List<String>> brokenList = extractBrokenListOfIds(queriedIds);
        List<T> results = new ArrayList<T>();
        for (List<String> idSet : brokenList) {
            Iterable<T> entities = repo.findAll(collectionName, adjustIdsInQuery(idSet, neutralQuery));
            for (T e : entities) {
                // find and return an instance
                results.add(e);
            }
        }
        return results;
    }

    /*
     * (non-Javadoc)
     * @see org.slc.sli.domain.Repository#findAllIds(java.lang.String,
     * org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        return repo.findAllIds(collectionName, neutralQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#count(java.lang.String, org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return repo.count(collectionName, neutralQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#update(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean update(String collection, T object) {
        return repo.update(collection, object);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#doUpdate(java.lang.String,
     * org.slc.sli.domain.NeutralQuery, org.springframework.data.mongodb.core.query.Update)
     */
    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        return repo.doUpdate(collection, query, update);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#delete(java.lang.String, java.lang.String)
     */
    @Override
    public boolean delete(String collectionName, String id) {
        return repo.delete(collectionName, id);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#deleteAll(java.lang.String)
     */
    @Override
    public void deleteAll(String collectionName, NeutralQuery query) {
        repo.deleteAll(collectionName, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#getCollection(java.lang.String)
     */
    @Override
    public DBCollection getCollection(String collectionName) {
        return repo.getCollection(collectionName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#getCollections(boolean)
     */
    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        return repo.getCollections(includeSystemCollections);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#findByQuery(java.lang.String,
     * org.springframework.data.mongodb.core.query.Query, int, int)
     */
    @Override
    @Deprecated
    public Iterable<T> findByQuery(String collectionName, Query query, int skip, int max) {
        return repo.findByQuery(collectionName, query, skip, max);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#collectionExists(java.lang.String)
     */
    @Override
    public boolean collectionExists(String collection) {
        return repo.collectionExists(collection);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#setWriteConcern(java.lang.String)
     */
    @Override
    public void setWriteConcern(String writeConcern) {
        repo.setWriteConcern(writeConcern);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#setReferenceCheck(java.lang.String)
     */
    @Override
    public void setReferenceCheck(String referenceCheck) {
        repo.setReferenceCheck(referenceCheck);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#count(java.lang.String,
     * org.springframework.data.mongodb.core.query.Query)
     */
    @Override
    public long count(String collectionName, Query query) {
        return repo.count(collectionName, query);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#updateWithRetries(java.lang.String, java.lang.Object, int)
     */
    @Override
    public boolean updateWithRetries(String collection, T object, int noOfRetries) {
        return repo.updateWithRetries(collection, object, noOfRetries);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.slc.sli.domain.Repository#patch(java.lang.String, java.lang.String,
     * java.lang.String, java.util.Map)
     */
    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        return repo.patch(type, collectionName, id, newValues);
    }

    @Override
    public T findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}
