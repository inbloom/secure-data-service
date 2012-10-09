package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.NaturalKeyExtractor;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class SubDocAccessor {

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private DeterministicUUIDGeneratorStrategy didGenerator;

    @Autowired
    private NaturalKeyExtractor naturalKeyExtractor;

    private MongoTemplate template;

    private final Map<String, Location> locations = new HashMap<String, SubDocAccessor.Location>();

    public SubDocAccessor() {
        // this will store student assessment associations under the student documents in the
        // assessments field
//        store("studentAssessmentAssociation").within("student").as("assessments").mapping("studentId", "_id")
//                .register();

        store("studentSectionAssociation").within("section").as("studentAssociations").mapping("sectionId", "_id").register();
    }

    /**
     * Start a location for a given sub doc type
     *
     * @param type
     * @return
     */
    public LocationBuilder store(String type) {
        return new LocationBuilder(type);
    }

    private class LocationBuilder {
        private Map<String, String> lookup = new HashMap<String, String>();
        private String collection;
        private String subField;
        private final String type;

        public LocationBuilder(String type) {
            super();
            this.type = type;
        }

        /**
         * Store the subdoc within the given super doc collection
         *
         * @param collection
         *            the collection the subdoc gets stored in
         * @return
         */
        public LocationBuilder within(String collection) {
            this.collection = collection;
            return this;
        }

        /**
         * The field the subdocs show up in
         *
         * @param subField
         *            The field the subdocs show up in
         * @return
         */
        public LocationBuilder as(String subField) {
            this.subField = subField;
            return this;
        }

        /**
         * Map a field in the sub doc to the super doc. This will be used when resolving parenthood
         *
         * @param subDocField
         * @param superDocField
         * @return
         */
        public LocationBuilder mapping(String subDocField, String superDocField) {
            lookup.put(superDocField, subDocField);
            return this;
        }

        /**
         * Register it as a sub resource location
         */
        public void register() {
            locations.put(type, new Location(collection, lookup, subField, type));
        }

    }

    /**
     * The location of the subDoc
     *
     * @author nbrown
     */
    public class Location {

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

        private String getParentEntityId(String entityId) {
            return entityId.substring(0,43);
        }

        private String getParentEntityId(Map<String, Object> body) {
            List<String> parentIdList = new ArrayList<String>();
            for (Entry<String, String> entry : lookup.entrySet()) {
                if (entry.getKey().equals("_id")) {
                    return (String) body.get(entry.getValue());
                }
                parentIdList.add(body.get(entry.getValue()).toString());
            }
            return StringUtils.join(parentIdList, "");
        }

        private DBObject getParentQuery(Map<String, Object> body) {
            Query parentQuery = new Query();
            for (Entry<String, String> entry : lookup.entrySet()) {
                parentQuery.addCriteria(new Criteria(entry.getKey()).is(body.get(entry.getValue())));
            }
            return parentQuery.getQueryObject();
        }

        private Update getUpdateObject(Map<String, Map<String, Object>> newEntities) {
            Update update = new Update();
            for (Entry<String, Map<String, Object>> entity : newEntities.entrySet()) {
                update.set(getField(entity.getKey()), entity.getValue());
            }
            return update;
        }

        public boolean update(String id, Map<String, Object> entity) {
            DBObject query = getParentQuery(entity);
            Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
            updateMap.put(id, entity);
            Update updateObject = getUpdateObject(updateMap);
            return doUpdate(query, updateObject);
        }

        public boolean doUpdate(Query query, Update update) {
            DBObject dbQuery = this.buildSubDocQuery(query);
            return this.doUpdate(dbQuery, update);
        }

        private boolean doUpdate(DBObject query, Update updateObject) {
            return template.getCollection(collection).update(query, updateObject.getUpdateObject(), true, false)
                    .getLastError().ok();
        }

        public boolean bulkUpdate(DBObject parentQuery, Map<String, Map<String, Object>> newEntities) {
            Update update = getUpdateObject(newEntities);
            return doUpdate(parentQuery, update);
        }

        public boolean create(Entity entity) {
            return update(makeEntityId(entity), entity.getBody());
        }

        public boolean bulkCreate(DBObject parentQuery, List<Entity> entities) {
            Map<String, Map<String, Object>> updateMap = new HashMap<String, Map<String, Object>>();
            for (Entity entity : entities) {
                updateMap.put(makeEntityId(entity), entity.getBody());
            }
            return bulkUpdate(parentQuery, updateMap);
        }

        public boolean insert(List<Entity> entities) {
            ConcurrentMap<DBObject, List<Entity>> parentEntityMap = new ConcurrentHashMap<DBObject, List<Entity>>();
            for (Entity entity : entities) {
                DBObject parentQuery = getParentQuery(entity.getBody());
                parentEntityMap.putIfAbsent(parentQuery, new ArrayList<Entity>());
                parentEntityMap.get(parentQuery).add(entity);
            }
            boolean result = true;
            for (Entry<DBObject, List<Entity>> entry : parentEntityMap.entrySet()) {
                result &= bulkCreate(entry.getKey(), entry.getValue());
            }
            return result;
        }

        private String makeEntityId(Entity entity) {
            String id = didGenerator.generateId(naturalKeyExtractor.getNaturalKeyDescriptor(entity));
            return getParentEntityId(entity.getBody()) + id;
        }

        public Map<String, Object> read(String id) {
            return read(id, null);
        }

        @SuppressWarnings("unchecked")
        public Map<String, Object> read(String id, Criteria additionalCriteria) {
            Query query = Query.query(Criteria.where("_id").is(getParentEntityId(id)));
            query.fields().include(getField(id));
            if (additionalCriteria != null) {
                query.addCriteria(additionalCriteria);
            }
            Map<?, ?> result = template.findOne(query, Map.class, collection);
            if (result == null) {
                return null;
            }
            return (Map<String, Object>) ((Map<String, Object>) result.get(subField)).get(id);
        }

        private String getField(String id) {
            return subField + "." + id;
        }

        /**
         * If this query contains an id, we can search for that easy, so pull the id out if it
         * exists
         *
         * @param query
         * @return
         */
        private String getId(Query query) {
            Object idQuery = query.getQueryObject().get("_id");
            if (idQuery instanceof String) {
                return (String) idQuery;
            } else if (idQuery instanceof DBObject) {
                DBObject dbQuery = (DBObject) idQuery;
                Object inQuery = dbQuery.get("$in");
                if (inQuery instanceof List<?> && ((List<?>) inQuery).size() == 1) {
                    return ((List<?>) inQuery).get(0).toString();
                } else if (inQuery instanceof Object[] && ((Object[]) inQuery).length == 1) {
                    return ((Object[]) inQuery)[0].toString();
                }
            }
            return null;

        }

        public List<Entity> find(Query original) {
            List<Entity> results = new ArrayList<Entity>();
            String extractedId = getId(original);
            if (extractedId != null) {
                results.add(new MongoEntity(type, read(extractedId)));
            } else {
                DBObject query = buildSubDocQuery(original);
                DBCursor cursor = template.getCollection(collection).find(query, new BasicDBObject(subField, 1));
                while (cursor.hasNext()) {
                    DBObject next = cursor.next();
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Object>> subEntities = (Map<String, Map<String, Object>>) next
                            .get(subField);
                    for (Entry<String, Map<String, Object>> subEntityEntry : subEntities.entrySet()) {
                        Map<String, Object> subEntity = subEntityEntry.getValue();
                        String id = subEntityEntry.getKey();
                        results.add(new MongoEntity(type, id, subEntity, new HashMap<String, Object>()));
                    }
                }
            }
            return results;
        }

        private DBObject buildSubDocQuery(Query original) {
            DBObject originalQuery = original.getQueryObject();
            DBObject query = new BasicDBObject();
            for (Entry<String, String> entry : lookup.entrySet()) {
                String keyField = entry.getKey();
                Object keyQuery = originalQuery.get("body." + entry.getValue());
                if (keyQuery != null) {
                    query.put(keyField, keyQuery);
                }
            }
            Object idQuery = originalQuery.get("_id");
            if (idQuery instanceof String) {
                // we can filter out some more based on the parent id
                query.put("_id", getParentEntityId((String) idQuery));
            }
            // metadata query params can go in as is
            for (String key : originalQuery.keySet()) {
                if (key.startsWith("metaData")) {
                    query.put(key, originalQuery.get(key));
                }
            }

            query.put(subField, new BasicDBObject("$exists", true));
            return query;
        }

        /*
         * Returns a query to find the subdoc with the given id.
         * Note: Firgure out how to merge it with buildSubDocQuery method
         */
        private DBObject getExactSubDocQuery(String id) {
            String targetDoc = "body." + lookup.get("_id") + "." + id;
            DBObject query = new BasicDBObject();
            query.put("_id", getParentEntityId(id));
            query.put(targetDoc, new BasicDBObject("$exists", true));
            return query;
        }

        public boolean exists(String id) {
            DBObject query = this.getExactSubDocQuery(id);
            return template.getCollection(collection).count(query) > 0;
        }

        // Note: This is suboptimal and too memory intensive. Should be implemented
        // by iterating over the cursor without instantiating instances of MongoEntity as
        // done in the find(..) method, which should also be refactored.
        public long count(Query query) {
            return this.find(query).size();
        }

        public boolean delete(String id) {
            String targetDoc = "body." + lookup.get("_id") + "." + id;
            DBObject query = this.getExactSubDocQuery(id);
            Update update = new Update();
            update.unset(targetDoc);
            return this.doUpdate(query, update);
        }

        public void deleteAll(Query query) {
            for(Entity e :  find(query)) {
                delete(e.getEntityId());
            }
        }
    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public Location subDoc(String docType) {
        return locations.get(docType);
    }

    public void setTemplate(MongoTemplate template) {
        this.template = template;
    }

    public void setDidGenerator(DeterministicUUIDGeneratorStrategy didGenerator) {
        this.didGenerator = didGenerator;
    }

    public void setNaturalKeyExtractor(NaturalKeyExtractor naturalKeyExtractor) {
        this.naturalKeyExtractor = naturalKeyExtractor;
    }

}
