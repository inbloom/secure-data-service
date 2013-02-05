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

package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bson.BasicBSONObject;
import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.domain.CalculatedData;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

/**
 * Mock implementation of the Repository<Entity> for unit testing.
 * 
 */
@Component("validationRepo")
@Primary
public class MockRepo implements Repository<Entity> {
    @Override
    public boolean collectionExists(String collection) {
        return false;
    }
    
    private Map<String, Map<String, Entity>> repo = new HashMap<String, Map<String, Entity>>();
    
    public MockRepo() {
        setup();
    }
    
    private void setup() {
        repo.put("course", new LinkedHashMap<String, Entity>());
        repo.put("student", new LinkedHashMap<String, Entity>());
        repo.put("school", new LinkedHashMap<String, Entity>());
        repo.put("roles", new LinkedHashMap<String, Entity>());
        repo.put("realm", new LinkedHashMap<String, Entity>());
        repo.put("studentSchoolAssociation", new LinkedHashMap<String, Entity>());
        repo.put("teacher", new LinkedHashMap<String, Entity>());
        repo.put("section", new LinkedHashMap<String, Entity>());
        repo.put("assessment", new LinkedHashMap<String, Entity>());
        repo.put("studentAssessment", new LinkedHashMap<String, Entity>());
        repo.put("studentSectionAssociation", new LinkedHashMap<String, Entity>());
        repo.put("teacherSchoolAssociation", new LinkedHashMap<String, Entity>());
        repo.put("staff", new LinkedHashMap<String, Entity>());
        repo.put("educationOrganization", new LinkedHashMap<String, Entity>());
        repo.put("educationOrganizationSchoolAssociation", new LinkedHashMap<String, Entity>());
        repo.put("staffEducationOrganizationAssociation", new LinkedHashMap<String, Entity>());
        repo.put("sectionAssessmentAssociation", new LinkedHashMap<String, Entity>());
        repo.put("sectionSchoolAssociation", new LinkedHashMap<String, Entity>());
        repo.put("aggregation", new LinkedHashMap<String, Entity>());
        repo.put("staffschoolassociation", new LinkedHashMap<String, Entity>());
        repo.put("aggregationDefinition", new LinkedHashMap<String, Entity>());
        repo.put("educationOrganizationAssociation", new LinkedHashMap<String, Entity>());
        repo.put("session", new LinkedHashMap<String, Entity>());
        repo.put("courseOffering", new LinkedHashMap<String, Entity>());
        repo.put("courseSectionAssociation", new LinkedHashMap<String, Entity>()); // known
                                                                                   // technical-debt.
        repo.put("bellSchedule", new LinkedHashMap<String, Entity>());
        repo.put("cohort", new LinkedHashMap<String, Entity>());
        repo.put("disciplineIncident", new LinkedHashMap<String, Entity>());
        repo.put("disciplineAction", new LinkedHashMap<String, Entity>());
        repo.put("parent", new LinkedHashMap<String, Entity>());
        repo.put("program", new LinkedHashMap<String, Entity>());
        repo.put("gradebookEntry", new LinkedHashMap<String, Entity>());
        repo.put("studentGradebookEntry", new LinkedHashMap<String, Entity>());
        repo.put("learningObjective", new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION, new LinkedHashMap<String, Entity>());
        repo.put("studentParentAssociation", new LinkedHashMap<String, Entity>());
        repo.put("courseTranscript", new LinkedHashMap<String, Entity>());
        repo.put("teacherSectionAssociation", new LinkedHashMap<String, Entity>());
        repo.put("studentProgramAssociation", new LinkedHashMap<String, Entity>());
        repo.put("staffProgramAssociation", new LinkedHashMap<String, Entity>());
        repo.put("authSession", new LinkedHashMap<String, Entity>());
        repo.put("assessmentFamily", new LinkedHashMap<String, Entity>());
        repo.put("application", new LinkedHashMap<String, Entity>());
        repo.put("applicationAuthorization", new LinkedHashMap<String, Entity>());
        repo.put("userSession", new LinkedHashMap<String, Entity>());
        repo.put("oauth_access_token", new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.ATTENDANCE, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.LEARNING_OBJECTIVE, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.COHORT, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.STAFF_COHORT_ASSOCIATION, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.GRADE, new LinkedHashMap<String, Entity>());
        repo.put("tenant", new LinkedHashMap<String, Entity>());
    }
    
    protected Map<String, Map<String, Entity>> getRepo() {
        return repo;
    }
    
    protected void setRepo(Map<String, Map<String, Entity>> repo) {
        this.repo = repo;
    }
    
    @Override
    public Entity findById(String entityType, String id) {
        return repo.get(entityType).get(id);
    }
    
    @SuppressWarnings("unchecked")
    private Object getValue(Entity entity, String key, boolean prefixable) {
        if (!"_id".equals(key) && prefixable) {
            key = "body." + key;
        }
        String[] path = key.split("\\.");
        Map<String, Object> container = null;
        if (path.length > 0) {
            if ("_id".equals(path[0])) {
                return entity.getEntityId();
            } else if ("body".equals(path[0])) {
                container = entity.getBody();
            } else if ("metaData".equals(path[0])) {
                container = entity.getMetaData();
            } else if ("type".equals(path[0])) {
                container = new HashMap<String, Object>();
                container.put("type", entity.getType());
            }
            for (int i = 1; i < path.length - 1; i++) {
                Object sub = container.get(path[i]);
                if (sub != null && sub instanceof Map) {
                    container = (Map<String, Object>) sub;
                } else {
                    return null;
                }
            }
        }
        
        if (container == null) {
            return null;
        }
        
        return container.get(path[path.length - 1]);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Entity> findAll(String entityType, NeutralQuery neutralQuery) {
        
        Map<String, Entity> results = repo.get(entityType);
        if (results == null) {
            results = new LinkedHashMap<String, Entity>();
        }
        
        for (NeutralCriteria criteria : neutralQuery.getCriteria()) {
            results = processCriteria(results, criteria);
        }
        
        if (neutralQuery.getOrQueries().size() > 0) {
            Map<String, Entity> oredResults = new LinkedHashMap<String, Entity>();
            
            for (NeutralQuery orQueries : neutralQuery.getOrQueries()) {
                Map<String, Entity> tmpResults = results;
                for (NeutralCriteria criteria : orQueries.getCriteria()) {
                    tmpResults = processCriteria(tmpResults, criteria);
                }
                oredResults.putAll(tmpResults);
            }
            results = oredResults;
        }
        
        List<Entity> entitiesFound = new ArrayList<Entity>();
        for (Entity entity : results.values()) {
            entitiesFound.add(entity);
        }
        
        if (neutralQuery.getSortBy() != null) {
            final NeutralQuery.SortOrder sortOrder = neutralQuery.getSortOrder();
            Entity[] entities = entitiesFound.toArray(new Entity[] {});
            final String[] keysToSortBy = neutralQuery.getSortBy().split(",");
            Arrays.sort(entities, new Comparator<Entity>() {
                @Override
                public int compare(Entity entity1, Entity entity2) {
                    // loop for each key in the requested sort by
                    for (String sortKey : keysToSortBy) {
                        Object value1 = MockRepo.getValue(sortKey, entity1.getBody());
                        Object value2 = MockRepo.getValue(sortKey, entity2.getBody());
                        
                        int compare = MockRepo.compareValues(value1, value2);
                        
                        if (compare != 0) {
                            if (sortOrder == NeutralQuery.SortOrder.descending) {
                                return 0 - compare;
                            } else {
                                return compare;
                            }
                        }
                    }
                    
                    return 0;
                }
            });
            
            List<Entity> newEntitiesFound = new ArrayList<Entity>();
            for (Entity entity : entities) {
                newEntitiesFound.add(entity);
            }
            entitiesFound = newEntitiesFound;
        }
        
        int offset = neutralQuery.getOffset() > 0 ? neutralQuery.getOffset() : 0;
        for (int i = 0; i < offset; i++) {
            entitiesFound.remove(0);
        }
        
        int limit = neutralQuery.getLimit() > 0 ? neutralQuery.getLimit() : Integer.MAX_VALUE;
        while (entitiesFound.size() > limit) {
            entitiesFound.remove(entitiesFound.size() - 1);
        }
        
        return entitiesFound;
    }
    
    private static int compareValues(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return 0;
        }
        
        if (value1 instanceof Integer && value2 instanceof Integer) {
            return (Integer) value1 - (Integer) value2;
        } else if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).compareTo((String) value2);
        } else if (value1 instanceof String && value2 instanceof Integer) {
            return Integer.parseInt((String) value1) - (Integer) value2;
        } else if (value1 instanceof Integer && value2 instanceof String) {
            return (Integer) value1 - Integer.parseInt((String) value2);
        }
        
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    private static Object getValue(String fullKey, Map<String, Object> map) {
        Object value = null;
        
        for (String subKey : fullKey.split("\\.")) {
            if (subKey.equals("body")) {
                continue;
            } else if (map.get(subKey) instanceof Map) {
                map = (Map<String, Object>) map.get(subKey);
            } else {
                value = map.get(subKey);
            }
        }
        
        return value;
    }
    
    private Map<String, Entity> processCriteria(Map<String, Entity> results, NeutralCriteria criteria) {
        Map<String, Entity> toReturn = new LinkedHashMap<String, Entity>();
        if (criteria.getOperator().equals("=")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed());

                if (entityValue != null) {
                    if (entityValue.equals(criteria.getValue())) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    } else if (entityValue instanceof Collection) { // also need to
                                                              // handle = for
                                                              // array
                        for (Object arrayElement : (Collection) entityValue) {
                            if (arrayElement.equals(criteria.getValue())) {
                                toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                            }
                        }
                    }
                }
            }
        } else if (criteria.getOperator().equals("in")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                Object value = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed());
                if (value instanceof Collection) {
                    value = new ArrayList((Collection) value);  //fix occasional UnsupportedOperationException when list created with Arrays.asList
                }
                Collection<String> validValues = toList(criteria.getValue());
                if (value != null) {
                    if (value.getClass() == String.class) {
                        String entityValue = String.valueOf(value);
                        if (validValues.contains(entityValue)) {
                            toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    } else { // Is an array
                        List<String> entityValues = (List<String>) value;
                        entityValues.retainAll(validValues);
                        if (!entityValues.isEmpty()) {
                            toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
            }
        } else if (criteria.getOperator().equals("exists")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                
                Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed());
                if (entityValue != null == (Boolean) criteria.getValue()) {
                    
                    toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                }
            }
            
        } else if (criteria.getOperator().equals("!=")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed());
                if (entityValue != null) {
                    if (!entityValue.equals(criteria.getValue())) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
            }
        } else if (criteria.getOperator().equals(">")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                        criteria.canBePrefixed());
                if (entityValue != null) {
                    if (entityValue.compareTo((String) criteria.getValue()) > 0) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
            }
        } else if (criteria.getOperator().equals("<")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                String entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed())
                        .toString();
                if (entityValue != null) {
                    if (entityValue.compareTo(criteria.getValue().toString()) < 0) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
            }
        } else if (criteria.getOperator().equals(">=")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                        criteria.canBePrefixed());
                if (entityValue != null) {
                    if (entityValue.compareTo((String) criteria.getValue()) >= 0) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
            }
        } else if (criteria.getOperator().equals("<=")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                        criteria.canBePrefixed());
                if (entityValue != null) {
                    if (entityValue.compareTo((String) criteria.getValue()) <= 0) {
                        toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
            }
        } else if (criteria.getOperator().equals("=~")) {
            for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(), criteria.canBePrefixed());
                if (entityValue != null) {
                    if (entityValue instanceof String && criteria.getValue() instanceof String) {
                        String entityValueString = (String) entityValue;
                        String criteriaValueString = (String) criteria.getValue();
                        
                        if (!entityValueString.equals(entityValueString.replaceAll(criteriaValueString, ""))) {
                            toReturn.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
            }
        } else {
            warn("Unsupported operator: {}", criteria.getOperator());
        }

        return toReturn;
    }
    
    @Override
    public Entity findOne(String entityType, NeutralQuery neutralQuery) {
        
        Iterator<Entity> iter = this.findAll(entityType, neutralQuery).iterator();
        return iter.hasNext() ? iter.next() : null;
    }
    
    @Override
    public boolean update(String type, Entity entity) {
        if (repo.get(type) == null) {
            repo.put(type, new LinkedHashMap<String, Entity>());
        }
        repo.get(type).put(entity.getEntityId(), entity);
        return true;
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Entity create(final String type, Map<String, Object> body, String collectionName) {
        final HashMap<String, Object> clonedBody = new HashMap<String, Object>(body);
        Map<String, List<Map<String, Object>>> denormalized = new HashMap<String, List<Map<String, Object>>>();
        if (body.containsKey("denormalization")) {
            denormalized.putAll((Map<String, List<Map<String, Object>>>) body.remove("denormalization"));
        }
        final Map<String, List<Map<String, Object>>> clonedDenormalization = new HashMap<String, List<Map<String, Object>>>(
                denormalized);
        final String id = generateId();
        
        Entity newEntity = new Entity() {
            @Override
            public String getEntityId() {
                return id;
            }
            
            @Override
            public Map<String, Object> getMetaData() {
                return new BasicBSONObject();
            }
            
            @Override
            public Map<String, Object> getBody() {
                return clonedBody;
            }
            
            @Override
            public String getType() {
                return type;
            }
            
            @Override
            public CalculatedData<String> getCalculatedValues() {
                return null;
            }
            
            @Override
            public CalculatedData<Map<String, Integer>> getAggregates() {
                return null;
            }
            
            @Override
            public Map<String, List<Entity>> getEmbeddedData() {
                return null;
            }
            
            @Override
            public Map<String, List<Map<String, Object>>> getDenormalizedData() {
                return clonedDenormalization;
            }
            
            @Override
            public String getStagedEntityId() {
                return null;
            }
        };
        
        update(collectionName, newEntity);
        return newEntity;
    }
    
    @Override
    public boolean delete(String entityType, String id) {
        return repo.get(entityType).remove(id) != null;
    }
    
    @Override
    public void deleteAll(String entityType, NeutralQuery query) {
        Map<String, Entity> repository = repo.get(entityType);
        if (repository != null) {
            repository.clear();
        }
        
    }
    
    public void deleteAll() {
        repo.clear();
        setup();
    }
    
    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return ((List<?>) findAll(collectionName, neutralQuery)).size();
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public Entity create(final String type, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName) {
        final HashMap<String, Object> clonedBody = new HashMap<String, Object>(body);
        final HashMap<String, Object> clonedMetadata = new HashMap<String, Object>(metaData);
        final String id = generateId();
        
        Entity newEntity = new Entity() {
            @Override
            public String getEntityId() {
                return id;
            }
            
            @Override
            public Map<String, Object> getMetaData() {
                return clonedMetadata;
            }
            
            @Override
            public Map<String, Object> getBody() {
                return clonedBody;
            }
            
            @Override
            public String getType() {
                return type;
            }
            
            @Override
            public CalculatedData<String> getCalculatedValues() {
                return null;
            }
            
            @Override
            public CalculatedData<Map<String, Integer>> getAggregates() {
                return null;
            }
            
            @Override
            public Map<String, List<Entity>> getEmbeddedData() {
                return null;
            }
            
            @Override
            public Map<String, List<Map<String, Object>>> getDenormalizedData() {
                return null;
            }
            
            @Override
            public String getStagedEntityId() {
                return null;
            }
        };
        
        update(collectionName, newEntity);
        return newEntity;
    }
    
    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        ArrayList<String> ids = new ArrayList<String>();
        for (Entity e : this.findAll(collectionName, neutralQuery)) {
            ids.add(e.getEntityId());
        }
        return ids;
    }
    
    @Override
    public DBCollection getCollection(String collectionName) {
        return null;
    }
    
    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        return null;
    }
    
    @Override
    public boolean exists(String collectionName, String id) {
        return false;
    }
    
    @Override
    public void setWriteConcern(String writeConcern) {
    }
    
    @Override
    public long count(String collectionName, Query query) {
        return 0;
    }
    
    @Override
    public void setReferenceCheck(String referenceCheck) {
    }
    
    @Override
    public List<DBCollection> getCollections(boolean includeSystemCollections) {
        return null;
    }
    
    protected Collection<String> toList(Object obj) {
        if (obj instanceof String) {
            List<String> list = new ArrayList();
            list.add((String) obj);
            return list;
        }
        
        return (Collection<String>) obj;
    }
    
    @Override
    public boolean doUpdate(String collection, NeutralQuery query, Update update) {
        return false;
    }
    
    @Override
    public boolean updateWithRetries(String collection, Entity object, int noOfRetries) {
        return false;
    }
    
    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        return null;
    }
    
    @Override
    public Entity createWithRetries(String type, String id, Map<String, Object> body, Map<String, Object> metaData,
            String collectionName, int noOfRetries) {
        return null;
    }
    
    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        this.findById(type, id).getBody().putAll(newValues);
        return true;
    }
    
    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String entityReferenced) {
        return null;
    }
    
    @Override
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        return null;
    }
}
