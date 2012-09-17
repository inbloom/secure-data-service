package org.slc.sli.api.security.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

@Component
public class PagingRepositoryDelegate<T> implements Repository<T> {

    @Autowired
    @Qualifier("validationRepo")
    private Repository<T> repo;
    
    // The size of queries we page around.
    @Value("${sli.api.security.context.paging}")
    private int COUNT;

    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#createWithRetries(java.lang.String, java.lang.String,
     * java.util.Map, java.util.Map, java.lang.String, int)
     */
    @Override
    public T createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
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
        int blocks = queriedIds.size() / COUNT;
        if (queriedIds.size() % COUNT > 0) {
            blocks++;
        }
        for (int i = 0; i < blocks; ++i) {
            List<String> part = new ArrayList<String>();
            int offSet = queriedIds.size() - i * COUNT;
            if (offSet > COUNT) {
                offSet = COUNT;
            }
            part.addAll(queriedIds.subList(i * COUNT, i * COUNT + offSet));
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
        // TODO Auto-generated method stub
        return repo.create(type, body);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#create(java.lang.String, java.util.Map, java.lang.String)
     */
    @Override
    public T create(String type, Map<String, Object> body, String collectionName) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return repo.create(type, body, metaData, collectionName);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#insert(java.util.List, java.lang.String)
     */
    @Override
    public List<T> insert(List<T> records, String collectionName) {
        // TODO Auto-generated method stub
        return repo.insert(records, collectionName);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#findById(java.lang.String, java.lang.String)
     */
    @Override
    public T findById(String collectionName, String id) {
        // TODO Auto-generated method stub
        return repo.findById(collectionName, id);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#exists(java.lang.String, java.lang.String)
     */
    @Override
    public boolean exists(String collectionName, String id) {
        // TODO Auto-generated method stub
        return repo.exists(collectionName, id);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#findOne(java.lang.String, org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public T findOne(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return repo.findOne(collectionName, neutralQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#findAll(java.lang.String)
     */
    @Override
    public Iterable<T> findAll(String collectionName) {
        // TODO Auto-generated method stub
        return repo.findAll(collectionName);
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
     * 
     * @see org.slc.sli.domain.Repository#findAllByPaths(java.lang.String, java.util.Map,
     * org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public Iterable<T> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return repo.findAllByPaths(collectionName, paths, neutralQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#findAllIds(java.lang.String,
     * org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return repo.findAllIds(collectionName, neutralQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#count(java.lang.String, org.slc.sli.domain.NeutralQuery)
     */
    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        // TODO Auto-generated method stub
        return repo.count(collectionName, neutralQuery);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#update(java.lang.String, java.lang.Object)
     */
    @Override
    public boolean update(String collection, T object) {
        // TODO Auto-generated method stub
        return repo.update(collection, object);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#doUpdate(java.lang.String, java.lang.String,
     * org.springframework.data.mongodb.core.query.Update)
     */
    @Override
    public boolean doUpdate(String collection, String id, Update update) {
        // TODO Auto-generated method stub
        return repo.doUpdate(collection, id, update);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#doUpdate(java.lang.String,
     * org.slc.sli.domain.NeutralQuery, org.springframework.data.mongodb.core.query.Update)
     */
    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        // TODO Auto-generated method stub
        return repo.doUpdate(collection, query, update);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#delete(java.lang.String, java.lang.String)
     */
    @Override
    public boolean delete(String collectionName, String id) {
        // TODO Auto-generated method stub
        return repo.delete(collectionName, id);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#deleteAll(java.lang.String)
     */
    @Override
    public void deleteAll(String collectionName) {
        // TODO Auto-generated method stub
        repo.deleteAll(collectionName);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#execute(com.mongodb.DBObject)
     */
    @Override
    public CommandResult execute(DBObject command) {
        // TODO Auto-generated method stub
        return repo.execute(command);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#getCollection(java.lang.String)
     */
    @Override
    public DBCollection getCollection(String collectionName) {
        // TODO Auto-generated method stub
        return repo.getCollection(collectionName);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#getCollections(boolean)
     */
    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        // TODO Auto-generated method stub
        return repo.getCollections(includeSystemCollections);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#findByPaths(java.lang.String, java.util.Map)
     */
    @Override
    @Deprecated
    public Iterable<T> findByPaths(String collectionName, Map<String, String> paths) {
        // TODO Auto-generated method stub
        return repo.findByPaths(collectionName, paths);
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
        // TODO Auto-generated method stub
        return repo.findByQuery(collectionName, query, skip, max);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#collectionExists(java.lang.String)
     */
    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return repo.collectionExists(collection);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#createCollection(java.lang.String)
     */
    @Override
    public void createCollection(String collection) {
        // TODO Auto-generated method stub
        repo.createCollection(collection);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#ensureIndex(org.springframework.data.mongodb.core.index.
     * IndexDefinition, java.lang.String)
     */
    @Override
    public void ensureIndex(IndexDefinition index, String collection) {
        // TODO Auto-generated method stub
        repo.ensureIndex(index, collection);
        
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#setWriteConcern(java.lang.String)
     */
    @Override
    public void setWriteConcern(String writeConcern) {
        // TODO Auto-generated method stub
        repo.setWriteConcern(writeConcern);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#setReferenceCheck(java.lang.String)
     */
    @Override
    public void setReferenceCheck(String referenceCheck) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return repo.count(collectionName, query);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#createWithRetries(java.lang.String, java.util.Map,
     * java.util.Map, java.lang.String, int)
     */
    @Override
    public T createWithRetries(String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        // TODO Auto-generated method stub
        return repo.createWithRetries(type, body, metaData, collectionName, noOfRetries);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.domain.Repository#updateWithRetries(java.lang.String, java.lang.Object, int)
     */
    @Override
    public boolean updateWithRetries(String collection, T object, int noOfRetries) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return repo.patch(type, collectionName, id, newValues);
    }
    
}
