package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.mongodb.DBObject;

import org.slc.sli.domain.EntityQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityRepository;
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
    }
    
    protected Map<String, Map<String, Entity>> getRepo() {
        return repo;
    }
    
    protected void setRepo(Map<String, Map<String, Entity>> repo) {
        this.repo = repo;
    }
    
    @Override
    public Entity find(String collectionName, Map<String, String> queryParameters) {
        return find(collectionName, queryParameters.get("_id"));
    }
    
    @Override
    public Iterable<Entity> findAll(String collectionName, Map<String, String> queryParameters) {
        return findByFields(collectionName, queryParameters, 0, 10);
    }

    @Override
    public Iterable<Entity> findAll(String collectionName, EntityQuery query) {
        Map<String, String> fields = query.getFields();
        
        return findByFields(collectionName, fields);
    }

    @Override
    public Entity find(String entityType, String id) {
        return repo.get(entityType).get(id);
    }
    
    @Override
    public Iterable<Entity> findAll(String entityType, int skip, int max) {
        List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
        return all.subList(skip, (Math.min(skip + max, all.size())));
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
    
    @Override
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields, int skip, int max) {
        List<Entity> toReturn = new ArrayList<Entity>();
        
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                if (matchesFields(entity, fields)) {
                    toReturn.add(entity);
                }
            }
        }
        return toReturn.subList(skip, (Math.min(skip + max, toReturn.size())));
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
    public Iterable<Entity> findByFields(String entityType, Map<String, String> fields) {
        List<Entity> toReturn = new ArrayList<Entity>();
        
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                if (matchesFields(entity, fields)) {
                    toReturn.add(entity);
                }
            }
        }
        return toReturn;
    }

    @Override
    public long count(String collectionName, Query query) {
        return ((List<?>) findByQuery(collectionName, query, 0, Integer.MAX_VALUE)).size();
    }
    
    private Iterable<Entity> findByFields(String entityType, Query query, Map<String, Integer> sortKeyOrderMap,
            int skip, int max) {
        List<Entity> toReturn = new ArrayList<Entity>();
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                try {
                    if (matchQueries(entity, query)) {
                        toReturn.add(entity);
                    }
                } catch (Exception e) {
                    LOG.debug("error processing query!");
                }
            }
        }
        if (sortKeyOrderMap != null && sortKeyOrderMap.size() > 0) {
            EntityComparator comparator = new EntityComparator(sortKeyOrderMap);
            sortEntities(toReturn, comparator);
        }
        return toReturn.subList(skip, (Math.min(skip + max, toReturn.size())));
    }
    
    private boolean matchQueries(Entity entity, Query query) throws Exception {
        if (query != null) {
            DBObject queryObject = query.getQueryObject();
            for (String rawKey : queryObject.keySet()) {
                if (!matches(entity, queryObject, rawKey)) {
                    return false;
                }
            }
        }
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
    
    @Override
    public Iterable<Entity> findByQuery(String entityType, Query query, int skip, int max) {
        Map<String, Integer> sortKeyOrderMap = getSortKeyOrderMap(query);
        return findByFields(entityType, query, sortKeyOrderMap, skip, max);
    }
    
    private String generateId() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths, int skip, int max) {
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
    
    private Map<String, Integer> getSortKeyOrderMap(Query query) {
        Map<String, Integer> sortKeyOrderMap = new LinkedHashMap<String, Integer>();
        if (query != null) {
            DBObject sortObject = query.getSortObject();
            if (sortObject != null) {
                sortKeyOrderMap.putAll(sortObject.toMap());
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
    public Iterable<String> findIdsByQuery(String collectionName, Query query, int skip, int max) {
        ArrayList<String> ids = new ArrayList<String>();
        for (Entity e : this.findByQuery(collectionName, query, skip, max)) {
            ids.add(e.getEntityId());
        }
        return ids;
    }

    @Override
    public Entity findOne(String collectionName, Query query) {
        throw new UnsupportedOperationException("Not implemented here yet, implement me! (We're agile. And toasted.");
    }
    
}
