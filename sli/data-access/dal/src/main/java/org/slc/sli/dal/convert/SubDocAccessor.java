package org.slc.sli.dal.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.domain.EmbedDocumentRelations;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.validation.schema.INaturalKeyExtractor;

/**
 * Utility for accessing subdocuments that have been collapsed into a super-doc
 *
 * @author nbrown
 *
 */
public class SubDocAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(SubDocAccessor.class);

    private final Map<String, Location> locations = new HashMap<String, SubDocAccessor.Location>();

    private final MongoTemplate template;

    private final UUIDGeneratorStrategy didGenerator;

    private final INaturalKeyExtractor naturalKeyExtractor;

    public SubDocAccessor(MongoTemplate template, UUIDGeneratorStrategy didGenerator,
            INaturalKeyExtractor naturalKeyExtractor) {
        this.template = template;
        this.didGenerator = didGenerator;
        this.naturalKeyExtractor = naturalKeyExtractor;
        // // this will store student assessment associations under the student documents in the
        // // assessments field
        // store("studentAssessmentAssociation").within("student").as("assessments").mapping("studentId",
        // "_id")
        // .register();
        for (String entityType : EmbedDocumentRelations.getSubDocuments()) {
            String parent = EmbedDocumentRelations.getParentEntityType(entityType);
            String parentKey = EmbedDocumentRelations.getParentFieldReference(entityType);
            if(parent != null && parentKey != null) {
                store(entityType).within(parent).as(entityType).mapping(parentKey, "_id").register();
            }
        }
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
     * THe location of the subDoc
     *
     * @author nbrown
     *
     */
    public class Location {

        private static final String ID_SEPERATOR = "×"; // it should be noted that is not an 'x', so
                                                        // be careful
                                                        // should probably change it to something
                                                        // less nefarious, but it does need to be
                                                        // something the api won't consider
                                                        // meaningful
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
            return entityId.split(ID_SEPERATOR)[0];
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

        private boolean doUpdate(DBObject query, Update updateObject) {
            return template.getCollection(collection).update(query, updateObject.getUpdateObject(), true, false)
                    .getLastError().ok();
        }

        private boolean doUpdate(DBObject parentQuery, List<Entity> subEntities) {
            boolean result = true;
            result &= template.getCollection(collection)
                    .update(parentQuery, buildPullObject(subEntities), false, false).getLastError().ok();
            result &= template.getCollection(collection)
                    .update(parentQuery, buildPushObject(subEntities), false, false).getLastError().ok();
            return result;
        }

        private DBObject buildPullObject(List<Entity> subEntities) {
            List<String> existingIds = new ArrayList<String>();
            for (Entity entity : subEntities) {
                if (entity.getEntityId() != null && !entity.getEntityId().isEmpty()) {
                    existingIds.add(entity.getEntityId());
                }
            }
            Set<String> set = new HashSet<String>();
            set.addAll(existingIds);
            set.addAll(getSubDocDids(subEntities));
            Query pullQuery = new Query(Criteria.where("_id").in(set.toArray()));
            Update update = new Update();
            update.pull(subField, pullQuery.getQueryObject());
            return update.getUpdateObject();
        }

        private DBObject buildPushObject(List<Entity> subEntities) {
            List<DBObject> subDocs = new ArrayList<DBObject>();
            for (Entity entity : subEntities) {
                subDocs.add(subDocToDBObject(entity));
            }
            Update update = new Update();
            update.pushAll(subField, subDocs.toArray());
            return update.getUpdateObject();
        }

        private List<String> getSubDocDids(List<Entity> subEntities) {
            List<String> subDocDids = new ArrayList<String>();
            for (Entity entity : subEntities) {
                subDocDids.add((String) subDocToDBObject(entity).get("_id"));
            }
            return subDocDids;
        }

        private DBObject subDocToDBObject(Entity entity) {
            DBObject dbObject = ((MongoEntity) entity).toDBObject(didGenerator, naturalKeyExtractor);

            // uncomment out if embeddedDid need to cat parent did
            // String originalDid = (String) dbObject.get("_id");
            // String embededDid = getParentEntityId(entity.getBody()) + ID_SEPERATOR + originalDid;
            // dbObject.put("_id", embededDid);
            return dbObject;
        }

        private Entity convertDBObjectToSubDoc(DBObject dbObject) {
            return MongoEntity.fromDBObject(dbObject);
        }

        public boolean bulkUpdate(DBObject parentQuery, Map<String, Map<String, Object>> newEntities) {
            Update update = getUpdateObject(newEntities);
            return doUpdate(parentQuery, update);
        }

        public boolean create(Entity entity) {
            // return update(makeEntityId(entity), entity.getBody());
            DBObject parentQuery = getParentQuery(entity.getBody());
            List<Entity> subEntities = new ArrayList<Entity>();
            subEntities.add(entity);
            return doUpdate(parentQuery, subEntities);
        }

        public boolean delete(String id) {
            Entity entity = findById(id);
            if (entity == null) {
                return false;
            }
            DBObject parentQuery = getParentQuery(entity.getBody());
            List<Entity> subEntities = new ArrayList<Entity>();
            subEntities.add(entity);

            return template.getCollection(collection).update(parentQuery, buildPullObject(subEntities), false, false)
                    .getLastError().ok();
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
            // TODO this needs to be done a bit smarter, probably using whatever is in place for
            // deterministic ids
            return getParentEntityId(entity.getBody()) + ID_SEPERATOR + RandomStringUtils.randomNumeric(16);
        }

        public Entity findById(String id) {
            LOG.info("the subDoc id is: {}", id);
            // uncomment out if embededDid need to cat parent id
            // Query parentQuery = new Query(Criteria.where("_id").is(getParentEntityId(id)));
            Query subDocQuery = new Query(Criteria.where(subField + "." + "_id").is(id));
            // comment out if embededDid need to cat parent id
            Query parentQuery = subDocQuery;

            List<Entity> entities = findSubDocs(parentQuery.getQueryObject(), subDocQuery.getQueryObject(),
                    new Query().getQueryObject());
            if (entities != null && entities.size() == 1) {
                return entities.get(0);
            }
            return null;

        }

        public List<Entity> findAll(Query originalQuery) {
            DBObject queryDBObject = toSubDocQuery(originalQuery);
            List<Entity> entities = findSubDocs(queryDBObject, queryDBObject, getLimitQuery(originalQuery));
            return entities;
        }

        // convert original query match criteria to match embeded subDocs
        @SuppressWarnings("unchecked")
        private DBObject toSubDocQuery(Query originalQuery) {

            DBObject originalQueryDBObject = originalQuery.getQueryObject();
            DBObject queryDBObject = appendSubField(originalQueryDBObject);
            for (String key : originalQueryDBObject.keySet()) {
                if (key.equals("$or") || key.equals("$and")) {
                    List<DBObject> originalOrQueryDBObjects = (List<DBObject>) originalQueryDBObject.get(key);
                    List<DBObject> orQueryDBObjects = new ArrayList<DBObject>();
                    for (DBObject originalOrQueryDBObject : originalOrQueryDBObjects) {
                        DBObject orQueryDBObject = appendSubField(originalOrQueryDBObject);
                        orQueryDBObjects.add(orQueryDBObject);
                    }
                    queryDBObject.put(key, orQueryDBObjects);
                }
            }
            return queryDBObject;
        }

        // retrieve limit/offset/sort info from the original query and make them applicable to
        // subDocs
        private DBObject getLimitQuery(Query originalQuery) {
            DBObject limitQueryDBObject = new Query().getQueryObject();
            DBObject originalSortDBObject = originalQuery.getSortObject();
            if (originalSortDBObject != null && originalSortDBObject.keySet().size() > 0) {
                limitQueryDBObject.put("$sort", appendSubField(originalSortDBObject));
            }
            if (originalQuery.getSkip() > 0) {
                limitQueryDBObject.put("$skip", originalQuery.getSkip());
            }
            if (originalQuery.getLimit() > 0) {
                limitQueryDBObject.put("$limit", originalQuery.getLimit());
            }
            return limitQueryDBObject;
        }

        // append subField to original query key, so it can query in subDocs
        private DBObject appendSubField(DBObject originalDBObject) {
            DBObject newDBObject = new Query().getQueryObject();
            for (String key : originalDBObject.keySet()) {
                if (!key.startsWith("$")) {
                    String newKey = subField + "." + key;
                    newDBObject.put(newKey, originalDBObject.get(key));
                }
            }
            return newDBObject;
        }

        @SuppressWarnings("unchecked")
        private List<Entity> findSubDocs(DBObject parentQuery, DBObject subDocQuery, DBObject limitQuery) {
            String queryCommand = "{aggregate : \"" + collection + "\", pipeline:[{$match : " + parentQuery.toString()
                    + "},{$project : {\"" + subField + "\":1,\"_id\":0 } },{$unwind: \"$" + subField + "\"},{$match:"
                    + subDocQuery.toString() + "}"
                    + ((limitQuery == null || limitQuery.keySet().size() == 0) ? "" : ("," + limitQuery.toString()))
                    + "]}";
            LOG.info("the aggregate query command is: {}", queryCommand);
            CommandResult result = template.executeCommand(queryCommand);
            List<DBObject> subDocs = (List<DBObject>) result.get("result");
            List<Entity> entities = new ArrayList<Entity>();
            for (DBObject dbObject : subDocs) {
                entities.add(convertDBObjectToSubDoc(((DBObject) dbObject.get(subField))));
            }

            return entities;
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

    }

    public boolean isSubDoc(String docType) {
        return locations.containsKey(docType);
    }

    public Location subDoc(String docType) {
        return locations.get(docType);
    }
}
