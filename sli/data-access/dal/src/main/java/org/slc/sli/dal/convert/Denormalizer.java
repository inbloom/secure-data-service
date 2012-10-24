/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.dal.convert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.Entity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Utility for denormalizing entities
 *
 * @author srupasinghe
 */
public class Denormalizer {

    private final Map<String, Denormalization> denormalizations = new HashMap<String, Denormalization>();

    private final MongoTemplate template;

    private static final String PARENT_REFERENCE = "parentEducationAgencyReference";
    private static final String EDUCATION_ORGANIZATION = "educationOrganization";

    public Denormalizer(MongoTemplate template) {
        this.template = template;

        for (String entityType : EmbeddedDocumentRelations.getDenormalizedDocuments()) {
            String toEntity = EmbeddedDocumentRelations.getDenormalizeToEntity(entityType);
            String field = EmbeddedDocumentRelations.getDenormalizedToField(entityType);
            Map<String, String> referenceKeys = EmbeddedDocumentRelations.getReferenceKeys(entityType);
            String idKey = EmbeddedDocumentRelations.getDenormalizedIdKey(entityType);
            List<String> denormalizedBodyFields = EmbeddedDocumentRelations.getDenormalizedBodyFields(entityType);
            List<String> denormalizedMetaFields = EmbeddedDocumentRelations.getDenormalizedMetaFields(entityType);

            if (toEntity != null && referenceKeys != null) {
                denormalize(entityType).data(denormalizedBodyFields, denormalizedMetaFields).to(toEntity).as(field)
                        .using(referenceKeys).idKey(idKey).register();
            }
        }
    }

    /**
     * Builds a denormalization
     *
     * @param type
     * @return
     */
    public DenormalizationBuilder denormalize(String type) {
        return new DenormalizationBuilder(type);
    }

    private class DenormalizationBuilder {
        private String collection;
        private String field;
        private final String type;
        private Map<String, String> referenceKeys;
        private String idKey;
        private List<String> bodyFields;
        private List<String> metaFields;

        public DenormalizationBuilder(String type) {
            super();
            this.type = type;
        }

        public DenormalizationBuilder to(String collection) {
            this.collection = collection;
            return this;
        }

        public DenormalizationBuilder as(String field) {
            this.field = field;
            return this;
        }

        public DenormalizationBuilder using(Map<String, String> referenceKeys) {
            this.referenceKeys = referenceKeys;
            return this;
        }

        public DenormalizationBuilder data(List<String> bodyFields, List<String> metaFields) {
            this.bodyFields = bodyFields;
            this.metaFields = metaFields;
            return this;
        }

        public DenormalizationBuilder idKey(String idKey) {
            this.idKey = idKey;
            return this;
        }

        public void register() {
            denormalizations.put(type, new Denormalization(type, collection, field, referenceKeys, idKey, bodyFields,
                    metaFields));
        }

    }

    /**
     * Encapsulates the de-normalizing logic for each entity
     *
     */
    public class Denormalization {

        private String type;
        private String denormalizeToEntity;
        private Map<String, String> denormalizationReferenceKeys;
        private String denormalizedIdKey;
        private List<String> denormalizedBodyFields;
        private List<String> denormalizedMetaDataFields;
        private String denormalizedToField;

        public Denormalization(String type, String denormalizeToEntity, String denormalizedToField,
                Map<String, String> denormalizationReferenceKeys, String denormalizedIdKey,
                List<String> denormalizedFields) {
            this.type = type;
            this.denormalizeToEntity = denormalizeToEntity;
            this.denormalizedToField = denormalizedToField;
            this.denormalizationReferenceKeys = denormalizationReferenceKeys;
            this.denormalizedIdKey = denormalizedIdKey;
            this.denormalizedBodyFields = denormalizedFields;
        }

        public Denormalization(String type, String denormalizeToEntity, String denormalizedToField,
                Map<String, String> denormalizationReferenceKeys, String denormalizedIdKey,
                List<String> denormalizedBodyFields, List<String> denormalizedMetaDataFields) {
            this.type = type;
            this.denormalizeToEntity = denormalizeToEntity;
            this.denormalizedToField = denormalizedToField;
            this.denormalizationReferenceKeys = denormalizationReferenceKeys;
            this.denormalizedIdKey = denormalizedIdKey;
            this.denormalizedBodyFields = denormalizedBodyFields;
            this.denormalizedMetaDataFields = denormalizedMetaDataFields;
        }

        public boolean create(Entity entity) {
            DBObject parentQuery = getParentQuery(entity.getBody());

            List<Entity> entities = new ArrayList<Entity>();
            entities.add(entity);

            return doUpdate(parentQuery, entities);
        }

        private DBObject getParentQuery(Map<String, Object> body) {
            Query parentQuery = new Query();

            for (Map.Entry<String, String> entry : denormalizationReferenceKeys.entrySet()) {
                String value = (String) body.get(entry.getKey());

                if (entry.getValue().equals("_id")) {
                    parentQuery.addCriteria(new Criteria(entry.getValue()).is(value));
                } else {
                    parentQuery.addCriteria(new Criteria("body." + entry.getValue()).is(value));
                }
            }

            return parentQuery.getQueryObject();
        }

        private BasicDBObject getDbObject(Entity entity) {
            Map<String, Object> body = entity.getBody();
            Map<String, Object> meta = entity.getMetaData();
            BasicDBObject dbObj = new BasicDBObject();
            String internalId = null;

            if (denormalizedIdKey.equals("_id")) {
                internalId = entity.getEntityId();
            } else {
                internalId = (String) body.get(denormalizedIdKey);
            }

            // add the id field
            dbObj.put("_id", internalId);

            if (denormalizedBodyFields != null) {
                for (String field : denormalizedBodyFields) {
                    if (body.containsKey(field)) {
                        dbObj.put(field, body.get(field));
                    }
                }
            }

            if (denormalizedMetaDataFields != null) {
                for (String field : denormalizedMetaDataFields) {
                    if (meta.containsKey(field)) {
                        dbObj.put(field, meta.get(field));
                    }
                }
            }

            if (denormalizedIdKey.equals("schoolId")) {
                dbObj.put("edOrgs", new ArrayList<String>(fetchLineage(internalId)));
            }

            return dbObj;
        }

        /**
         * Fetches the education organization lineage for the specified education organization id.
         * Use
         * sparingly, as this will recurse up the education organization hierarchy.
         *
         * @param id
         *            Education Organization for which the lineage must be assembled.
         * @return Set of parent education organization ids.
         */
        private Set<String> fetchLineage(String id) {
            Set<String> parents = new HashSet<String>();
            Entity edOrg = template.findOne(new Query().addCriteria(Criteria.where("_id").is(id)), Entity.class,
                    EDUCATION_ORGANIZATION);
            if (edOrg != null) {
                parents.add(id);
                Map<String, Object> body = edOrg.getBody();
                if (body.containsKey(PARENT_REFERENCE)) {
                    String myParent = (String) body.get(PARENT_REFERENCE);
                    parents.addAll(fetchLineage(myParent));
                }
            }
            return parents;
        }

        private boolean doUpdate(DBObject parentQuery, List<Entity> entities) {
            boolean result = true;

            TenantContext.setIsSystemCall(false);
            result &= template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPullObject(entities), false, true).getLastError().ok();
            result &= template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPushObject(entities), false, true).getLastError().ok();

            return result;
        }

        private DBObject buildPushObject(List<Entity> entities) {
            List<DBObject> docs = new ArrayList<DBObject>();
            for (Entity entity : entities) {
                docs.add(getDbObject(entity));
            }

            Update update = new Update();
            update.pushAll(denormalizedToField, docs.toArray());

            return update.getUpdateObject();
        }

        private DBObject buildPullObject(List<Entity> entities) {
            Set<String> existingIds = new HashSet<String>();
            Query pullQuery = new Query();

            for (Entity entity : entities) {
                String internalId = null;
                if (denormalizedIdKey.equals("_id")) {
                    internalId = entity.getEntityId();
                } else {
                    internalId = (String) entity.getBody().get(denormalizedIdKey);
                }
                existingIds.add(internalId);
            }

            pullQuery.addCriteria(Criteria.where("_id").in(existingIds));

            Update update = new Update();
            update.pull(denormalizedToField, pullQuery.getQueryObject());

            return update.getUpdateObject();
        }

        public boolean insert(List<Entity> entities) {
            ConcurrentMap<DBObject, List<Entity>> parentEntityMap = new ConcurrentHashMap<DBObject, List<Entity>>();
            for (Entity entity : entities) {
                DBObject parentQuery = getParentQuery(entity.getBody());
                parentEntityMap.putIfAbsent(parentQuery, new ArrayList<Entity>());
                parentEntityMap.get(parentQuery).add(entity);
            }
            boolean result = true;
            for (Map.Entry<DBObject, List<Entity>> entry : parentEntityMap.entrySet()) {
                result &= bulkCreate(entry.getKey(), entry.getValue());
            }
            return result;
        }

        private boolean bulkCreate(DBObject parentQuery, List<Entity> entities) {
            return bulkUpdate(parentQuery, entities);
        }

        private boolean bulkUpdate(DBObject parentQuery, List<Entity> newEntities) {
            return doUpdate(parentQuery, newEntities);
        }

        public boolean delete(Entity entity, String id) {

            if (entity == null) {
                entity = findTypeEntity(id);
            }

            if (entity == null) {
                return false;
            }

            DBObject parentQuery = getParentQuery(entity.getBody());
            List<Entity> subEntities = new ArrayList<Entity>();
            subEntities.add(entity);
            TenantContext.setIsSystemCall(false);

            return template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPullObject(subEntities), false, true).getLastError().ok();
        }

        private Entity findTypeEntity(String id) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            return template.findOne(query, Entity.class, type);
        }

        public boolean doUpdate(Entity parentEntity, Update update) {
            if (parentEntity == null) return false;

            DBObject parentQuery = getParentQuery(parentEntity.getBody());
            parentQuery.put(denormalizedToField + "._id", parentEntity.getBody().get(denormalizedIdKey));

            DBObject patchUpdate = toDenormalizedObjectUpdate(update);

            boolean result = template.getCollection(denormalizeToEntity).update(parentQuery, patchUpdate, false, true)
                        .getLastError().ok();

            return result;
        }

        private DBObject toDenormalizedObjectUpdate(Update originalUpdate) {
            DBObject updateDBObject = new BasicDBObject();

            for (String key : originalUpdate.getUpdateObject().keySet()) {
                if (key.startsWith("$")) {
                    Map<String, Object> fieldAndValue = (Map<String, Object>) originalUpdate.getUpdateObject().get(key);
                    Map<String, Object> newFieldAndValue = new HashMap<String, Object>();
                    for (String field : fieldAndValue.keySet()) {
                        if (!field.startsWith("$")) {
                            newFieldAndValue.put(denormalizedToField + ".$." + field, fieldAndValue.get(field));
                        }
                    }
                    updateDBObject.put(key, newFieldAndValue);
                }
            }
            return updateDBObject;
        }

    }

    public boolean isDenormalizedDoc(String docType) {
        return denormalizations.containsKey(docType);
    }

    public Denormalization denormalization(String docType) {
        return denormalizations.get(docType);
    }
}
