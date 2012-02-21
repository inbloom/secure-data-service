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
    private static String[] reservedQueryKeys = { "start-index", "max-results", "query" };
    
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
        return null;
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
        Entity newEntity = new MongoEntity(type, generateId(), body, null);
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
            Object value = entity.getBody().get(field.getKey());
            if (value == null || !value.toString().equals(field.getValue())) {
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

    private Iterable<Entity> findByFields(String entityType, String queryString, Map<String, Integer> sortKeyOrderMap,
            int skip, int max) {
        List<Entity> toReturn = new ArrayList<Entity>();
        Map<String[], String> queryMap = stringToQuery(queryString);
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                try {
                    if (matchQueries(entity, queryMap)) {
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
    
    private Map<String[], String> stringToQuery(String queryString) {
        Map<String[], String> queryMap = new HashMap<String[], String>();
        if (queryString != null) {
            String[] queryStrings = queryString.split("&");
            for (String query : queryStrings) {
                if (!isReservedQueryKey(query) && !query.trim().isEmpty()) {
                    if (query.contains(">=")) {
                        String[] keyAndValue = getKeyAndValue(query, ">=");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, ">=");
                        }
                    } else if (query.contains("<=")) {
                        String[] keyAndValue = getKeyAndValue(query, "<=");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, "<=");
                        }
                    } else if (query.contains("!=")) {
                        String[] keyAndValue = getKeyAndValue(query, "!=");
                        if (keyAndValue != null)
                            queryMap.put(keyAndValue, "!=");
                    } else if (query.contains("=")) {
                        String[] keyAndValue = getKeyAndValue(query, "=");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, "=");
                        }
                        
                    } else if (query.contains("<")) {
                        String[] keyAndValue = getKeyAndValue(query, "<");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, "<");
                        }
                        
                    } else if (query.contains(">")) {
                        String[] keyAndValue = getKeyAndValue(query, ">");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, ">");
                        }
                    } else if (query.contains("$in")) {
                        String[] keyAndValue = getKeyAndValue(query, "$in");
                        if (keyAndValue != null) {
                            queryMap.put(keyAndValue, "$in");
                        }
                    } else {
                        throw new RuntimeException("Unhandled query: " + query);
                    }
                }
            }
        }
        return queryMap;
    }
    
    private boolean isReservedQueryKey(String queryString) {
        boolean found = false;
        for (String key : reservedQueryKeys) {
            if (queryString.indexOf(key) >= 0) {
                found = true;
            }
        }
        return found;
    }
    
    private String[] getKeyAndValue(String queryString, String operator) {
        if (operator.equals("$in")) {
            operator = "\\$in";
        }
        String[] keyAndValue = queryString.split(operator, 2);
        if (keyAndValue.length != 2) {
            return null;
        } else {
            return keyAndValue;
        }
    }
    
    private boolean matchQueries(Entity entity, Map<String[], String> queryMap) throws Exception {
        for (Entry<String[], String> keyAndValueEntries : queryMap.entrySet()) {
            String[] keyAndValue = keyAndValueEntries.getKey();
            String key = keyAndValue[0];
            String value = keyAndValue[1];
            String operator = keyAndValueEntries.getValue();
            if (operator.equals(">=")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) >= 0))) {
                    return false;
                }
            } else if (operator.equals("<=")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) <= 0))) {
                    return false;
                }
            } else if (operator.equals("!=")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) != 0))) {
                    return false;
                }
            } else if (operator.equals("=")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) == 0))) {
                    return false;
                }
            } else if (operator.equals("<")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) < 0))) {
                    return false;
                }
            } else if (operator.equals(">")) {
                if ((!entity.getBody().containsKey(key)) || (!(compareToValue(entity.getBody().get(key), value) > 0))) {
                    return false;
                }
            } else if (operator.equals("$in")) {
                boolean match = false;
                if ("_id".equals(key)) {
                    String[] ids = value.split(":");
                    if (ids != null) {
                        for (String id : ids) {
                            if (id.equals(entity.getEntityId())) {
                                match = true;
                                break;
                            }
                        }
                    }
                } else {
                    if (!entity.getBody().containsKey(key)) {
                        match = false;
                    } else {
                        String entityValue = (String) entity.getBody().get(key);
                        String[] values = value.split(":");
                        if (values != null) {
                            for (String v : values) {
                                if (v.equals(entityValue)) {
                                    match = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!match) {
                    return false;
                }
            } else {
                throw new RuntimeException("Unhandled query operator: " + operator);
            }
        }
        return true;
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
    
    private boolean matchQuery(String entityType, String id, String queryString) {
        boolean match = false;
        List<Entity> toReturn = new ArrayList<Entity>();
        Map<String[], String> queryMap = stringToQuery(queryString);
        if (repo.containsKey(entityType)) {
            List<Entity> all = new ArrayList<Entity>(repo.get(entityType).values());
            for (Entity entity : all) {
                try {
                    if (matchQueries(entity, queryMap)) {
                        toReturn.add(entity);
                    }
                } catch (Exception e) {
                    System.out.println("error processing query!");
                }
            }
        }
        for (Entity entity : toReturn) {
            if (entity.getEntityId().equals(id)) {
                match = true;
            }
        }
        return match;
    }
    
    @Override
    public Iterable<Entity> findByQuery(String entityType, Query query, int skip, int max) {
        String queryString = queryToString(query);
        Map<String, Integer> sortKeyOrderMap = getSortKeyOrderMap(query);
        return findByFields(entityType, queryString, sortKeyOrderMap, skip, max);
    }
    
    @Override
    public boolean matchQuery(String entityType, String id, Query query) {
        String queryString = queryToString(query);
        return matchQuery(entityType, id, queryString);
    }
    
    private String queryToString(Query query) {
        String queryString = "";
        if (query != null) {
            DBObject queryObject = query.getQueryObject();
            for (String key : queryObject.keySet()) {
                if (queryObject.get(key) instanceof String) {
                    if (queryString.equals("")) {
                        queryString = key.replaceFirst("body.", "") + "=" + (String) queryObject.get(key);
                    } else {
                        queryString = queryString + "&" + key.replaceFirst("body.", "") + "="
                                + (String) queryObject.get(key);
                    }
                } else if (queryObject.get(key) instanceof Integer) {
                    if (queryString.equals("")) {
                        queryString = key.replaceFirst("body.", "") + "=" + ((Integer) queryObject.get(key)).toString();
                    } else {
                        queryString = queryString + "&" + key.replaceFirst("body.", "") + "="
                                + ((Integer) queryObject.get(key)).toString();
                    }
                } else if (queryObject.get(key) instanceof DBObject) {
                    queryString = addQueryToString(queryString, (DBObject) queryObject.get(key), key);
                }
            }
            
        }
        return queryString;
    }
    
    private String addQueryToString(String queryString, DBObject dbObject, String key) {
        if (dbObject.containsField("$gt")) {
            if (queryString.equals("")) {
                queryString = key.replaceFirst("body.", "") + ">" + dbObject.get("$gt");
            } else {
                queryString = queryString + "&" + key.replaceFirst("body.", "") + ">" + dbObject.get("$gt");
            }
            
        } else if (dbObject.containsField("$lt")) {
            
            if (queryString.equals("")) {
                queryString = key.replaceFirst("body.", "") + "<" + dbObject.get("$lt");
            } else {
                queryString = queryString + "&" + key.replaceFirst("body.", "") + "<" + dbObject.get("$lt");
            }
            
        } else if (dbObject.containsField("$gte")) {
            if (queryString.equals("")) {
                queryString = key.replaceFirst("body.", "") + ">=" + dbObject.get("$gte");
            } else {
                queryString = queryString + "&" + key.replaceFirst("body.", "") + ">=" + dbObject.get("$gte");
            }
            
        } else if (dbObject.containsField("$lte")) {
            if (queryString.equals("")) {
                queryString = key.replaceFirst("body.", "") + "<=" + dbObject.get("$lte");
            } else {
                queryString = queryString + "&" + key.replaceFirst("body.", "") + "<=" + dbObject.get("$lte");
            }
            
        } else if (dbObject.containsField("$ne")) {
            if (queryString.equals("")) {
                queryString = key.replaceFirst("body.", "") + "!=" + dbObject.get("$ne");
            } else {
                queryString = queryString + "&" + key.replaceFirst("body.", "") + "!=" + dbObject.get("$ne");
            }
        } else if (dbObject.containsField("$in")) {
            if (!queryString.equals("")) {
                queryString = queryString + "&";
            }
            StringBuilder sb = new StringBuilder();
            if (!(dbObject.get("$in") instanceof Object[])) {
                throw new RuntimeException("Unknown $in type: " + dbObject.get("$in"));
            }
            Object[] set = (Object[]) dbObject.get("$in");
            for (int i = 0; i < set.length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(set[i]);
            }
            queryString = queryString + key.replaceFirst("body.", "") + "$in" + sb.toString();
        } else {
            throw new RuntimeException("Unhandled query type: " + key + " " + dbObject);
        }
        return queryString;
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
                        map1 = (Map) map1.get(key);
                        map2 = (Map) map2.get(key);
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
    
}
