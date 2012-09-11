package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.domain.Entity;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class SubDocAccessor {
    private final Map<String, Location> locations = new HashMap<String, SubDocAccessor.Location>();

    private final MongoTemplate template;

    public SubDocAccessor(MongoTemplate template) {
        this.template = template;
        locations.put("studentAssessmentAssociation", new Location("student", "studentId", "assessments"));
        locations.put("studentSectionAssociation", new Location("student", "studentId", "sections"));
        locations.put("studentSchoolAssociation", new Location("student", "studentId", "schools"));
    }

    /**
     * THe location of the subDoc
     *
     * @author nbrown
     *
     */
    public class Location {
        private static final String ID_SEPERATOR = ";";
        private final String collection;
        private final String key;
        private final String subField;

        /**
         * Create a new location to store subdocs
         *
         * @param collection the collection the superdoc is in
         * @param key the field in the subdoc that refers to the super doc's id
         * @param subField the place to put the sub doc
         */
        public Location(String collection, String key, String subField) {
            super();
            this.collection = collection;
            this.key = key;
            this.subField = subField;
        }

        private String getKey() {
            return key;
        }

        private Query getQuery(String parentId) {
            return Query.query(Criteria.where("_id").is(parentId));
        }

        private String getParentEntityId(Map<String, Object> entity) {
            return (String) entity.get(getKey());
        }

        private String getParentEntityId(String entityId) {
            return entityId.split(ID_SEPERATOR)[0];
        }

        private Update getUpdateObject(Map<String, Map<String, Object>> newEntities) {
            Update update = new Update();
            for (Entry<String, Map<String, Object>> entity : newEntities.entrySet()) {
                update.set(getField(entity.getKey()), entity.getValue());
            }
            return update;
        }

        public boolean update(String id, Map<String, Object> entity) {
            Query query = getQuery(getParentEntityId(entity));
            Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
            updateMap.put(id, entity);
            Update updateObject = getUpdateObject(updateMap);
            return template.updateFirst(query, updateObject, collection).getLastError().ok();
        }

        public boolean bulkUpdate(String parentId, Map<String, Map<String, Object>> newEntities) {
            Query query = getQuery(parentId);
            Update update = getUpdateObject(newEntities);
            return template.updateFirst(query, update, collection).getLastError().ok();
        }

        public boolean create(Map<String, Object> entity) {
            return update(makeEntityId(entity), entity);
        }

        public boolean bulkCreate(String parentId, List<Map<String, Object>> entities) {
            Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
            for (Map<String, Object> entity : entities) {
                updateMap.put(makeEntityId(entity), entity);
            }
            return bulkUpdate(parentId, updateMap);
        }

        public boolean insert(List<Entity> entities) {
            ConcurrentMap<String, List<Map<String, Object>>> parentEntityMap = new ConcurrentHashMap<String, List<Map<String, Object>>>();
            for (Entity entity : entities) {
                String parentEntityId = getParentEntityId(entity.getBody());
                parentEntityMap.putIfAbsent(parentEntityId, new ArrayList<Map<String, Object>>());
                parentEntityMap.get(parentEntityId).add(entity.getBody());
            }
            boolean result = true;
            for (Entry<String, List<Map<String, Object>>> entry : parentEntityMap.entrySet()) {
                result &= bulkCreate(entry.getKey(), entry.getValue());
            }
            return result;
        }

        private String makeEntityId(Map<String, Object> entity) {
            // TODO this needs to be done a bit smarter, probably using whatever is in place for
            // deterministic ids
            return getParentEntityId(entity) + ID_SEPERATOR + RandomStringUtils.randomNumeric(16);
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> read(String id) {
            Map<?, ?> result = template.findOne(Query.query(Criteria.where("_id").is(getParentEntityId(id))),
                    Map.class, collection);
            return (Map<String, Object>) ((Map<String, Object>) result.get(subField)).get(id);
        }

        private String getField(String id) {
            return subField + "." + id;
        }

    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public Location subDoc(String docType) {
        return locations.get(docType);
    }
}
