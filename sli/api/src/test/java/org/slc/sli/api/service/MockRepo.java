package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;


/**
 * Mock implementation of the Repository<Entity> for unit testing.
 *
 */
@Component
@Primary
public class MockRepo implements Repository<Entity> {
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
    public Entity findById(String entityType, String id) {
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
                LOG.warn("Unsupported operator: {}", criteria.getOperator());
            }
        }

        List<Entity> entitiesFound = new ArrayList<Entity>();
        for (Entity entity : results.values()) {
            entitiesFound.add(entity);
        }


        if (neutralQuery.getSortBy() != null) {
            final NeutralQuery.SortOrder sortOrder = neutralQuery.getSortOrder();
            Entity[]entities = entitiesFound.toArray(new Entity[]{});
            final String[]keysToSortBy = neutralQuery.getSortBy().split(",");
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

    private static int compareValues(Object value1, Object value2) {
        if (value1 == null || value2 == null) {
            return 0;
        }

        if (value1 instanceof Integer && value2 instanceof Integer) {
            return (Integer) value1 - (Integer) value2;
        } else if (value1 instanceof String && value2 instanceof String) {
            return ((String) value1).compareTo((String) value2);
        } else if (value1 instanceof String && value2 instanceof Integer) {
            return Integer.parseInt((String) value1) - ((Integer) value2);
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



    @Override
    public Entity findOne(String entityType, NeutralQuery neutralQuery) {

        if (entityType.equals("realm")) {
            final Map<String, Object> body = new HashMap<String, Object>();
            body.put("regionId", "SLI");

            return new Entity() {
                @Override
                public String getEntityId() {
                    return null;
                }
                @Override
                public Map<String, Object> getMetaData() {
                    return new BasicBSONObject();
                }
                @Override
                public Map<String, Object> getBody() {
                    return body;
                }
                @Override
                public String getType() {
                    return "realm";
                }
            };
        } else {
            return this.findAll(entityType, neutralQuery).iterator().next();
        }
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
    public Entity create(final String type, final Map<String, Object> body, String collectionName) {
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
                return body;
            }
            @Override
            public String getType() {
                return type;
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

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Iterable<Entity> findAllByPaths(String collectionName, Map<String, String> paths, NeutralQuery neutralQuery) {
        // Not implemented
        return null;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        // Not implemented
        return null;
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
    public CommandResult execute(DBObject command) {
        return null;
    }

    @Override
    public DBCollection getCollection(String collectionName) {
        return null;
    }

    @Override
    public Iterable<Entity> findByPaths(String collectionName, Map<String, String> paths) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<Entity> findByQuery(String collectionName, Query query, int skip, int max) {
        // TODO Auto-generated method stub
        return null;
    }

}
