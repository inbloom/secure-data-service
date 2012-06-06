package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.bson.BasicBSONObject;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.EntityNames;
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
    @Override
    public boolean collectionExists(String collection) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createCollection(String collection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ensureIndex(IndexDefinition index, String collection) {
        // TODO Auto-generated method stub

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
        repo.put("studentSectionGradebookEntry", new LinkedHashMap<String, Entity>());
        repo.put("learningObjective", new LinkedHashMap<String, Entity>());
        repo.put("studentDisciplineIncidentAssociation", new LinkedHashMap<String, Entity>());
        repo.put("studentParentAssociation", new LinkedHashMap<String, Entity>());
        repo.put("studentTranscriptAssociation", new LinkedHashMap<String, Entity>());
        repo.put("teacherSectionAssociation", new LinkedHashMap<String, Entity>());
        repo.put("studentProgramAssociation", new LinkedHashMap<String, Entity>());
        repo.put("staffProgramAssociation", new LinkedHashMap<String, Entity>());
        repo.put("authSession", new LinkedHashMap<String, Entity>());
        repo.put("assessmentFamily", new LinkedHashMap<String, Entity>());
        repo.put("application", new LinkedHashMap<String, Entity>());
        repo.put("oauthSession", new LinkedHashMap<String, Entity>());
        repo.put("oauth_access_token", new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.ATTENDANCE, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.LEARNINGOBJECTIVE, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.COHORT, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.STAFF_COHORT_ASSOCIATION, new LinkedHashMap<String, Entity>());
        repo.put(EntityNames.STUDENT_COHORT_ASSOCIATION, new LinkedHashMap<String, Entity>());

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

            Map<String, Entity> results2 = new LinkedHashMap<String, Entity>();

            if (criteria.getOperator().equals("=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
                    if (entityValue != null) {
                        if (entityValue.equals(criteria.getValue())) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("in")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {

                    String entityValue = String.valueOf(this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed()));

                    List<String> validValues = (List<String>) criteria.getValue();
                    if (validValues.contains(entityValue)) {
                        results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                    }
                }

                results = results2;
            } else if (criteria.getOperator().equals("!=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
                    if (entityValue != null) {
                        if (!entityValue.equals(criteria.getValue())) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals(">")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) > 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("<")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed()).toString();
                    if (entityValue != null) {
                        if (entityValue.compareTo(criteria.getValue().toString()) < 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals(">=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) >= 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("<=")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    String entityValue = (String) this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
                    if (entityValue != null) {
                        if (entityValue.compareTo((String) criteria.getValue()) <= 0) {
                            results2.put(idAndEntity.getKey(), idAndEntity.getValue());
                        }
                    }
                }
                results = results2;
            } else if (criteria.getOperator().equals("=~")) {
                for (Entry<String, Entity> idAndEntity : results.entrySet()) {
                    Object entityValue = this.getValue(idAndEntity.getValue(), criteria.getKey(),
                            criteria.canBePrefixed());
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
                warn("Unsupported operator: {}", criteria.getOperator());
            }
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

    @Override
    public Entity findOne(String entityType, NeutralQuery neutralQuery) {

        if (entityType.equals("realm")) {
            final Map<String, Object> body = new HashMap<String, Object>();
            body.put("tenantId", "SLI");

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
            Iterator<Entity> iter = this.findAll(entityType, neutralQuery).iterator();
            return iter.hasNext() ? iter.next() : null;
        }
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

    @Override
    public Entity create(final String type, Map<String, Object> body, String collectionName) {
        final HashMap<String, Object> clonedBody = new HashMap<String, Object>(body);
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

    @Override
    public boolean exists(String collectionName, String id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setWriteConcern(String writeConcern) {
        // TODO Auto-generated method stub

    }

}
