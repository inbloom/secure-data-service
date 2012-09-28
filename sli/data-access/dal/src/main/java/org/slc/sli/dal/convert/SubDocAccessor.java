package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;

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
        Map<String, String> studentLookup = new LinkedHashMap<String, String>();
        studentLookup.put("studentId", "studentId");
        studentLookup.put("schoolYear", "schoolYear");
        locations.put("studentAssessmentAssociation", new Location("enrollment", studentLookup, "assessments",
                "studentAssessmentAssociation"));
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
        private final Map<String, String> lookup;
        private final String subField;
        private final String type;

        /**
         * Create a new location to store subdocs
         *
         * @param collection
         *            the collection the superdoc is in
         * @param key
         *            the field in the subdoc that refers to the super doc's id
         * @param subField
         *            the place to put the sub doc
         */
        public Location(String collection, Map<String, String> lookup, String subField, String type) {
            super();
            this.collection = collection;
            this.lookup = lookup;
            this.subField = subField;
            this.type = type;
        }

        private Query getQuery(String parentId) {
            return Query.query(Criteria.where("_id").is(parentId));
        }

        private String getParentEntityId(String entityId) {
            return entityId.split(ID_SEPERATOR)[0];
        }

        private String getParentEntityId(Map<String, Object> body) {
            List<String> parentIdList = new ArrayList<String>();
            for (Entry<String, String> entry : lookup.entrySet()) {
                if (entry.getKey().equals("_id")) {
                    return (String) body.get(entry.getValue());
                }
                parentIdList.add((String) body.get(entry.getValue()));
            }
            return StringUtils.join(parentIdList, ID_SEPERATOR);
        }

        private Map<String, Object> getParentQuery(Map<String, Object> body) {
            Map<String, Object> parentQuery = new HashMap<String, Object>();
            for (Entry<String, String> entry : lookup.entrySet()) {
                parentQuery.put(entry.getKey(), body.get(entry.getValue()));
            }
            return parentQuery;
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

        public boolean bulkUpdate(Map<String, Object> parentQuery, Map<String, Map<String, Object>> newEntities) {
            Query query = new Query();
            query.getQueryObject().putAll(parentQuery);
            Update update = getUpdateObject(newEntities);
            return template.updateFirst(query, update, collection).getLastError().ok();
        }

        public boolean create(Map<String, Object> entity) {
            return update(makeEntityId(entity), entity);
        }

        public boolean bulkCreate(Map<String, Object> parentQuery, List<Map<String, Object>> entities) {
            Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
            for (Map<String, Object> entity : entities) {
                updateMap.put(makeEntityId(entity), entity);
            }
            return bulkUpdate(parentQuery, updateMap);
        }

        public boolean insert(List<Entity> entities) {
            ConcurrentMap<Map<String, Object>, List<Map<String, Object>>> parentEntityMap = new ConcurrentHashMap<Map<String, Object>, List<Map<String, Object>>>();
            for (Entity entity : entities) {
                Map<String, Object> parentQuery = getParentQuery(entity.getBody());
                parentEntityMap.putIfAbsent(parentQuery, new ArrayList<Map<String, Object>>());
                parentEntityMap.get(parentQuery).add(entity.getBody());
            }
            boolean result = true;
            for (Entry<Map<String, Object>, List<Map<String, Object>>> entry : parentEntityMap.entrySet()) {
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

        public List<Entity> find(Query original) {
            DBObject originalQuery = original.getQueryObject();
            DBObject query = new BasicDBObject();
            for (Entry<String, String> entry : lookup.entrySet()) {
                String keyField = "body." + entry.getValue();
                Object keyQuery = originalQuery.get(entry.getKey());
                query.put(keyField, keyQuery);
            }
            query.put(subField, new BasicDBObject("$exists", true));
            DBCursor cursor = template.getCollection(collection).find(query, new BasicDBObject(subField, 1));
            List<Entity> results = new ArrayList<Entity>();
            while (cursor.hasNext()) {
                DBObject next = cursor.next();
                @SuppressWarnings("unchecked")
                Map<String, Map<String, Object>> subEntities = (Map<String, Map<String, Object>>) next.get(subField);
                for (Entry<String, Map<String, Object>> subEntityEntry : subEntities.entrySet()) {
                    Map<String, Object> subEntity = subEntityEntry.getValue();
                    String id = subEntityEntry.getKey();
                    results.add(new MongoEntity(type, id, subEntity, new HashMap<String, Object>()));
                }
            }
            return results;
        }

    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public Location subDoc(String docType) {
        return locations.get(docType);
    }
}
