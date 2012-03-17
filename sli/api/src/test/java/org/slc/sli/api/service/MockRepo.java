package org.slc.sli.api.service;

import com.mongodb.DBObject;
import org.slc.sli.api.config.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.MongoEntity;

/**
 * Mock implementation of the EntityRepository for unit testing.
 * 
 */
@Component
@Primary
public class MockRepo implements EntityRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MockRepo.class);
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
        repo.put("studentAssessmentAssociation", new LinkedHashMap<String, Entity>());
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
        repo.put("schoolSessionAssociation", new LinkedHashMap<String, Entity>());
        repo.put("sessionCourseAssociation", new LinkedHashMap<String, Entity>());
        repo.put("courseSectionAssociation", new LinkedHashMap<String, Entity>()); // known
                                                                                   // technical-debt.
        repo.put("authSession", new LinkedHashMap<String, Entity>());
        repo.put("assessmentFamily", new LinkedHashMap<String, Entity>());
        repo.put("application", new LinkedHashMap<String, Entity>());
        repo.put("oauthSession", new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.ATTENDANCE, new LinkedHashMap<String, Entity>());
    }
    
    protected Map<String, Map<String, Entity>> getRepo() {
        return repo;
    }
    
    protected void setRepo(Map<String, Map<String, Entity>> repo) {
        this.repo = repo;
    }

    @Override
    public Entity find(String entityType, String id) {
        return repo.get(entityType).get(id);
    }
    
    private Object getValue(Entity entity, String key) {
        return (key.equals("_id")) ? entity.getEntityId() : entity.getBody().get(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterable<Entity> findAll(String entityType, NeutralQuery neutralQuery) {
        
        Map<String, Entity> results = repo.get(entityType);
        if (results == null) {
            results = new LinkedHashMap<String, Entity>();
        }
        
        for (NeutralCriteria criteria : neutralQuery.getCriteria()) {
            
            Map<String, Entity> results2 = new LinkedHashMap<String, Entity>();
            
            //
            if (criteria.getOperator().equals("=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue.equals(criteria.getValue())) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("in")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    
                    String entityValue = String.valueOf(this.getValue(idAndEntity.getValue(), criteria.getKey()));
                    
                    List<String> validValues = (List<String>) criteria.getValue();
                    if (validValues.contains(entityValue)) {
                        results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }
                
                results = results2;
            } else if (criteria.getOperator().equals("!=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (!entityValue.equals(criteria.getValue())) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals(">")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) > 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("<")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) < 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals(">=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) >= 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("<=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) <= 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("=~")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey());
                    if (entityValue != null) {
                        if (entityValue instanceof String && criteria.getValue() instanceof String) {
                            String entityValueString = (String) entityValue;
                            String criteriaValueString = (String) criteria.getValue();
                            
                            if (!entityValueString.equals(entityValueString.replaceAll(criteriaValueString, ""))) {
                                results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                            }
                        }
                    }
                }
                results = results2;
            } else {
                LOG.warn("Unsupported operator: " + criteria.getOperator());
            }
        }
        
        List<Entity> entitiesFound = new ArrayList<Entity>();
        for (Entity entity : results.values()) {
            entitiesFound.add(entity);
        }
        
        final String sortBy = neutralQuery.getSortBy();
        if (sortBy != null) {
            final NeutralQuery.SortOrder sortOrder = neutralQuery.getSortOrder();
            Entity[]entities = (Entity[]) entitiesFound.toArray(new Entity[]{});
            Arrays.sort(entities, new Comparator<Entity>() {
                public int compare(Entity entity1, Entity entity2) {
                    int compare = 0;
                    
                    Object entity1SortByObject = entity1.getBody().get(sortBy);
                    Object entity2SortByObject = entity2.getBody().get(sortBy);
                    
                    if (entity1SortByObject != null && entity2SortByObject != null) {
                        
                        if (entity1SortByObject instanceof String && entity2SortByObject instanceof String) {
                            String entity1Value = (String) entity1SortByObject;
                            String entity2Value = (String) entity2SortByObject;
                            
                            compare = entity1Value.compareTo(entity2Value);
                        } else if (entity1SortByObject instanceof String && entity2SortByObject instanceof Integer) {
                            compare = Integer.parseInt((String) entity1SortByObject) - ((Integer) entity2SortByObject);
                        } else if (entity1SortByObject instanceof Integer && entity2SortByObject instanceof String) {
                            compare = ((Integer) entity1SortByObject - Integer.parseInt((String) entity2SortByObject));
                        } else if (entity1SortByObject instanceof Integer && entity2SortByObject instanceof Integer) {
                            compare = (Integer) entity1SortByObject - (Integer) entity2SortByObject;
                        }
                    }
                    
                    
                    if (sortOrder == NeutralQuery.SortOrder.descending) {
                        return 0 - compare;
                    } else {
                        return compare;
                    }
                }
            });

            List<Entity> newEntitiesFound = new ArrayList<Entity>();
            for (Entity entity : entities) {
                newEntitiesFound.add(entity);
            }
            entitiesFound = newEntitiesFound;
        }
        
        int offset = (neutralQuery.getOffset() > 0) ? neutralQuery.getOffset() : 0;
        for (int i = 0; i < offset; i++) {
            entitiesFound.remove(0);
        }
        
        int limit = (neutralQuery.getLimit() > 0) ? neutralQuery.getLimit() : Integer.MAX_VALUE;
        while (entitiesFound.size() > limit) {
            entitiesFound.remove(entitiesFound.size() - 1);
        }
        
        return entitiesFound;
    }
    
    
    public Entity find(String entityType, NeutralQuery neutralQuery) {
        
        return this.findAll(entityType, neutralQuery).iterator().next();
    }
    
    @Override
    public boolean update(String type, Entity entity) {
        repo.get(type).put(entity.getEntityId(), entity);
        return true;
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body) {
        return create(type, body, type);
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body, String collectionName) {
        String id = generateId();
        Entity newEntity = new MongoEntity(type, id, body, null);
        update(collectionName, newEntity);
        return newEntity;
    }
    
    @Override
    public boolean delete(String entityType, String id) {
        return repo.get(entityType).remove(id) != null;
    }
    
    private boolean matchesFields(Entity entity, Map<String, String> fields) {
        for (Map.Entry<String, String> field : fields.entrySet()) {
            Object value;
            if (field.getKey().equals("_id")) {
                value = entity.getEntityId();
            } else {
                value = entity.getBody().get(field.getKey());
            }
            
            if (value == null || !field.getValue().contains(value.toString())) {
                return false;
            }
        }
        return true;
        
    }
    
    @Override
    public void deleteAll(String entityType) {
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
    public Iterable<Entity> findAll(String entityType) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        return all;
    }
    
    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        return ((List<?>) findAll(collectionName, neutralQuery)).size();
    }
    
    private Iterable<Entity> findByFields(String entityType, NeutralQuery neutralQuery) {
        List<Entity> toReturn = new ArrayList<Entity>();
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                try {
                    if (matchQueries(entity, neutralQuery)) {
                        toReturn.add(entity);
                    }
                } catch (Exception e) {
                    LOG.debug("error processing query!");
                }
            }
        }
        Map<String, Integer> sortKeyOrderMap = this.getSortKeyOrderMap(neutralQuery);
        
        if (sortKeyOrderMap != null && sortKeyOrderMap.size() > 0) {
            EntityComparator comparator = new EntityComparator(sortKeyOrderMap);
            sortEntities(toReturn, comparator);
        }
        
        int skip = neutralQuery.getOffset();
        int max = neutralQuery.getLimit();
        
        return toReturn.subList(skip, (Math.min(skip + max, toReturn.size())));
    }
    
    private boolean matchQueries(Entity entity, NeutralQuery neutralQuery) throws Exception {
        return true;
    }
    
    private boolean matches(Entity entity, DBObject queryObject, String rawKey) throws Exception {
        boolean isId = rawKey.equals("_id");
        String key = isId ? "_id" : rawKey.substring("body.".length());
        if (!(isId || entity.getBody().containsKey(key))) {
            return false;
        }
        Object bigValue = queryObject.get(rawKey);
        String entityId = entity.getEntityId();
        Object entityValue = isId ? entityId : entity.getBody().get(key);
        if (bigValue instanceof DBObject) {
            @SuppressWarnings("unchecked")
            Entry<String, ?> opValue = (Entry<String, ?>) ((DBObject) bigValue).toMap().entrySet().iterator().next();
            String operator = opValue.getKey();
            Object rawValue = opValue.getValue();
            String value = rawValue.toString();
            if (operator.equals("$gte")) {
                return compareToValue(entityValue, value) >= 0;
            } else if (operator.equals("$lte")) {
                return compareToValue(entityValue, value) <= 0;
            } else if (operator.equals("$ne")) {
                return compareToValue(entityValue, value) != 0;
            } else if (operator.equals("$lt")) {
                return compareToValue(entityValue, value) < 0;
            } else if (operator.equals("$gt")) {
                return compareToValue(entityValue, value) > 0;
            } else if (operator.equals("$in")) {
                boolean match = false;
                Object[] values = (Object[]) rawValue;
                if (values != null) {
                    for (Object v : values) {
                        if (v.toString().equals(entityValue.toString())) {
                            return true;
                        }
                    }
                }
                if (!match) {
                    return false;
                }
            } else if (operator.equals("$regex")) {
                return ((String) entityValue).matches(value);
            } else {
                throw new RuntimeException("Unhandled query operator: " + operator);
            }
        }
        return compareToValue(entityValue, bigValue.toString()) == 0;
    }
    
    private int compareToValue(Object entityValue, String value) throws Exception {
        int compare = 0;
        if (entityValue instanceof String) {
            compare = ((String) entityValue).compareTo(value);
        } else if (entityValue instanceof Integer) {
            compare = (Integer) entityValue - Integer.parseInt(value);
        }
        return compare;
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        // Not implemented
        return null;
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        // Not implemented
        return null;
    }
    
    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        // Not implemented
        return null;
    }
    
    private Map<String, Integer> getSortKeyOrderMap(NeutralQuery neutralQuery) {
        Map<String, Integer> sortKeyOrderMap = new LinkedHashMap<String, Integer>();
        if (neutralQuery != null) {
            String sortBy = neutralQuery.getSortBy();
            int count = 1;
            for (String field : sortBy.split(",")) {
                sortKeyOrderMap.put(field, count);
                count++;
            }
        }
        return sortKeyOrderMap;
    }
    
    private class EntityComparator implements Comparator<Entity> {
        Map<String, Integer> sortKeyOrderMap;
        
        EntityComparator(Map<String, Integer> sortKeyOrderMap) {
            this.sortKeyOrderMap = sortKeyOrderMap;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public int compare(Entity entity1, Entity entity2) {
            for (String sortKey : sortKeyOrderMap.keySet()) {
                Integer order = sortKeyOrderMap.get(sortKey);
                String[] keys = sortKey.split("\\.");
                Map<String, Object> map1 = entity1.getBody();
                Map<String, Object> map2 = entity2.getBody();
                Object value1 = null;
                Object value2 = null;
                for (String key : keys) {
                    if (key.equals("body")) {
                        continue;
                    } else if (map1.get(key) instanceof Map && map2.get(key) instanceof Map) {
                        map1 = (Map<String, Object>) map1.get(key);
                        map2 = (Map<String, Object>) map2.get(key);
                    } else {
                        value1 = map1.get(key);
                        value2 = map2.get(key);
                        break;
                    }
                }
                if (value1 instanceof Integer && value2 instanceof Integer && value1.equals(value2)) {
                    continue;
                } else if (value1 instanceof String && value2 instanceof String && value1.equals(value2)) {
                    continue;
                } else if (value1 instanceof Integer && value2 instanceof Integer) {
                    return order * ((Integer) value1 - (Integer) value2);
                } else if (value1 instanceof String && value2 instanceof String) {
                    return order * (((String) value1).compareTo((String) value2));
                }
            }
            return 0;
        }
    }
    
    private void sortEntities(List<Entity> entities, EntityComparator comparator) {
        Collections.sort(entities, comparator);
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
    public Entity findOne(String collectionName, NeutralQuery neutralQuery) {
        Entity response = null;
        
        if (collectionName.equals("realm")) {
            Map<String, Object> body = new HashMap<String, Object>();
            body.put("regionId", "SLI");
            response = new MongoEntity("realm", body);
            
            return response;
        } else {
            return find(collectionName, neutralQuery);
        }
    }
    
}
