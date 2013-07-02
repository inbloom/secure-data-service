/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.domain.AccessibilityCheck;
import org.slc.sli.domain.CascadeResult;
import org.slc.sli.domain.Entity;


/**
 * Utility for denormalizing entities
 *
 * @author srupasinghe
 */
public class Denormalizer {

    private final Map<String, Denormalization> denormalizations = new HashMap<String, Denormalization>();
    private final Map<String, Map<String,Entity>> denormalizationHelperCache = new HashMap<String, Map<String, Entity>>();

    private final MongoTemplate template;

    private static final String PARENT_REFERENCE = "parentEducationAgencyReference";
    private static final String EDUCATION_ORGANIZATION = "educationOrganization";

    public Denormalizer(MongoTemplate template) {
        this.template = template;

        for (String entityType : EmbeddedDocumentRelations.getDenormalizedDocuments()) {
            String toEntity = EmbeddedDocumentRelations.getDenormalizeToEntity(entityType);
            String field = EmbeddedDocumentRelations.getDenormalizedToField(entityType);
            Map<String, String> referenceKeys = EmbeddedDocumentRelations.getReferenceKeys(entityType);
            Map<String,String> cachedReferenceKey = EmbeddedDocumentRelations.getCachedRefKeys(entityType);
            String idKey = EmbeddedDocumentRelations.getDenormalizedIdKey(entityType);
            List<String> denormalizedBodyFields = EmbeddedDocumentRelations.getDenormalizedBodyFields(entityType);
            List<String> denormalizedMetaFields = EmbeddedDocumentRelations.getDenormalizedMetaFields(entityType);
            String [] denormalizedEntityKeys =  EmbeddedDocumentRelations.getDenormalizedEntityKeys(entityType);

            if (toEntity != null && referenceKeys != null) {
                denormalize(entityType).data(denormalizedBodyFields, denormalizedMetaFields).to(toEntity).as(field)
                        .using(referenceKeys).withCache(cachedReferenceKey).idKey(idKey).withDenormalizedEntityKeys(denormalizedEntityKeys).register();
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
        private Map<String,String> cachedEntityRefKey;
        private String [] denormalizedEntityKeys;

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

        public DenormalizationBuilder withCache(Map<String,String> cachedReferenceKey) {
            this.cachedEntityRefKey = cachedReferenceKey;
            return this;
        }

        public DenormalizationBuilder withDenormalizedEntityKeys(String [] denormalizedEntityKeys) {
            this.denormalizedEntityKeys = Arrays.copyOf(denormalizedEntityKeys, denormalizedEntityKeys.length);
            return this;
        }

        public void register() {
            denormalizations.put(type, new Denormalization(type, collection, field, referenceKeys, idKey, bodyFields,
                    metaFields, cachedEntityRefKey, denormalizedEntityKeys));
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
        private Map<String,String> cachedEntityRefKey;
        private Map<String,Entity> referencedEntityMap;
        private String [] denormalizedEntityKeys;

        public Denormalization(String type, String denormalizeToEntity, String denormalizedToField,
                Map<String, String> denormalizationReferenceKeys, String denormalizedIdKey,
                List<String> denormalizedFields, Map<String,String> cachedEntityRefKey, String [] denormalizedEntityKeys) {
            this.type = type;
            this.denormalizeToEntity = denormalizeToEntity;
            this.denormalizedToField = denormalizedToField;
            this.denormalizationReferenceKeys = denormalizationReferenceKeys;
            this.denormalizedIdKey = denormalizedIdKey;
            this.denormalizedBodyFields = denormalizedFields;
            this.cachedEntityRefKey = cachedEntityRefKey;
            this.referencedEntityMap = null;
            this.denormalizedEntityKeys = Arrays.copyOf(denormalizedEntityKeys, denormalizedEntityKeys.length);
        }

        public Denormalization(String type, String denormalizeToEntity, String denormalizedToField,
                Map<String, String> denormalizationReferenceKeys, String denormalizedIdKey,
                List<String> denormalizedBodyFields, List<String> denormalizedMetaDataFields
                , Map<String,String> cachedEntityRefKey, String [] denormalizedEntityKeys) {
            this.type = type;
            this.denormalizeToEntity = denormalizeToEntity;
            this.denormalizedToField = denormalizedToField;
            this.denormalizationReferenceKeys = denormalizationReferenceKeys;
            this.denormalizedIdKey = denormalizedIdKey;
            this.denormalizedBodyFields = denormalizedBodyFields;
            this.denormalizedMetaDataFields = denormalizedMetaDataFields;
            this.cachedEntityRefKey = cachedEntityRefKey;
            this.referencedEntityMap = null;
            this.denormalizedEntityKeys = Arrays.copyOf(denormalizedEntityKeys, denormalizedEntityKeys.length);
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
                String queryKey = entry.getValue();

                if((value == null ) || value.isEmpty() ) {
                    Entity entity = null;
                    String refEntityId = (String) body.get(entry.getValue());

                    if (cachedEntityRefKey != null) {
                        if ((denormalizationHelperCache != null)
                                && (!denormalizationHelperCache.isEmpty())) {
                            entity = denormalizationHelperCache.get(entry.getKey()).get(refEntityId);
                        } else {
                            Query refEntityQuery = new Query();
                            refEntityQuery.addCriteria(new Criteria("_id").is(refEntityId));
                            entity = template.findOne(refEntityQuery,Entity.class,entry.getKey());
                        }
                    }

                    if(entity == null) {
                        continue;
                    }
                    if (referencedEntityMap == null) {
                        referencedEntityMap = new HashMap<String, Entity>();
                    }
                    referencedEntityMap.put(refEntityId,entity);
                    for (Map.Entry<String,String> refEntry : cachedEntityRefKey.entrySet()) {
                        String refKey = refEntry.getKey();
                        if (refKey.equals("_id")) {
                            value = entity.getEntityId();
                        }   else {
                            value = (String)entity.getBody().get(refKey);
                        }
                        queryKey = refEntry.getValue();
                        addToParentQuery(parentQuery,queryKey,value);
                    }
                } else {
                    addToParentQuery(parentQuery,queryKey, value);
                }
            }

            return parentQuery.getQueryObject();
        }

        private void addToParentQuery(final Query parentQuery, String queryKey, String value) {
            if (queryKey.equals("_id")) {
                parentQuery.addCriteria(new Criteria(queryKey).is(value));
            } else {
                parentQuery.addCriteria(new Criteria("body." + queryKey).is(value));
            }
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

            Map<String, Object> refEntityBody = null;
            Map<String, Object> refEntityMeta = null;

            if ((referencedEntityMap != null) && (referencedEntityMap.containsKey(internalId))) {
                Entity refEntity = referencedEntityMap.get(internalId);
                refEntityBody = refEntity.getBody();
                refEntityMeta = refEntity.getMetaData();
            }

            if (denormalizedBodyFields != null) {
                for (String field : denormalizedBodyFields) {
                    if (body.containsKey(field)) {
                        dbObj.put(field, body.get(field));
                    } else if ((refEntityBody != null) && (refEntityBody.containsKey(field))) {
                        dbObj.put(field,refEntityBody.get(field));
                    }
                }
            }

            if (denormalizedMetaDataFields != null) {
                for (String field : denormalizedMetaDataFields) {
                    if (meta.containsKey(field)) {
                        dbObj.put(field, meta.get(field));
                    } else if ((refEntityMeta != null) && (refEntityMeta.containsKey(field))) {
                        dbObj.put(field,refEntityMeta.get(field));
                    }
                }
            }

            return dbObj;
        }

        private boolean doUpdate(DBObject parentQuery, List<Entity> entities) {
            boolean result = true;

            TenantContext.setIsSystemCall(false);
            result &= template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPullObject(entities), false, true, WriteConcern.SAFE).getLastError().ok();
            result &= template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPushObject(entities), true, true, WriteConcern.SAFE).getLastError().ok();
            referencedEntityMap = null;

            return result;
        }

        private DBObject buildPushObject(List<Entity> entities) {
            List<DBObject> docs = new ArrayList<DBObject>();
            for (Entity entity : entities) {
                docs.add(getDbObject(entity));
            }

            Update update = new Update();
            update.set("type", denormalizeToEntity).pushAll(denormalizedToField, docs.toArray());

            return update.getUpdateObject();
        }

        private DBObject buildPullObject(List<Entity> entities) {
            Query pullQuery = new Query();

            List <Criteria> orList = new ArrayList<Criteria>();
            for (Entity entity : entities) {
                String internalId = null;
                if (denormalizedIdKey.equals("_id")) {
                    internalId = entity.getEntityId();
                } else {
                    internalId = (String) entity.getBody().get(denormalizedIdKey);
                }
                //delete studentSectionAssociation DND where (sectionId(_id) = "x1" and beginDate(denormalizedKey) = "y1") OR (sectionId(_id) = "x2" and beginDate(denormalizedKey) = "y2") ...
                //delete studentSchoolAssociation  DND where (schoolld(_id)  = "x1" and entryDate(denormalizedKey) = "y1") OR (schoolld(_id)  = "x2" and entryDate(denormalizedKey) = "y2") ...
                //DND -> denormalizedDoc
                List<Criteria> andList = new ArrayList<Criteria>();
                andList.add(Criteria.where("_id").is(internalId));
                for(String denormalizedKey:denormalizedEntityKeys) {
                    String denormalizedValue = (String) entity.getBody().get(denormalizedKey);
                    andList.add( Criteria.where(denormalizedKey).is(denormalizedValue));
                }
                    Criteria and = new Criteria().andOperator(andList.toArray(new Criteria[0]));
                    orList.add(and);
            }
            Criteria or = new Criteria().orOperator(orList.toArray(new Criteria[0]));
            pullQuery.addCriteria(or);

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

        public CascadeResult safeDelete(Entity providedEntity, String id, Boolean cascade, Boolean dryrun, Integer maxObjects, AccessibilityCheck access) {
        	CascadeResult result = new CascadeResult();
        	return result;
        }

        public boolean delete(Entity providedEntity, String id) {
            Entity entity = providedEntity == null ? findTypeEntity(id) : providedEntity;

            if (entity == null) {
                return false;
            }

            DBObject parentQuery = getParentQuery(entity.getBody());
            List<Entity> subEntities = new ArrayList<Entity>();
            subEntities.add(entity);
            TenantContext.setIsSystemCall(false);

            return template.getCollection(denormalizeToEntity)
                    .update(parentQuery, buildPullObject(subEntities), true, true, WriteConcern.SAFE).getLastError().ok();
        }

        private Entity findTypeEntity(String id) {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));

            return template.findOne(query, Entity.class, type);
        }

        public boolean doUpdate(Entity parentEntity, Update update) {
            if (parentEntity == null) {
                return false;
            }

            DBObject parentQuery = getParentQuery(parentEntity.getBody());
            parentQuery.put(denormalizedToField + "._id", parentEntity.getBody().get(denormalizedIdKey));

            DBObject patchUpdate = toDenormalizedObjectUpdate(update);

            boolean result = template.getCollection(denormalizeToEntity).update(parentQuery, patchUpdate, false, true, WriteConcern.SAFE)
                        .getLastError().ok();

            return result;
        }

        private DBObject toDenormalizedObjectUpdate(Update originalUpdate) {
            DBObject updateDBObject = new BasicDBObject();

            for (String key : originalUpdate.getUpdateObject().keySet()) {
                if (key.startsWith("$")) {
                    Map<String, Object> fieldAndValue = (Map<String, Object>) originalUpdate.getUpdateObject().get(key);
                    Map<String, Object> newFieldAndValue = new HashMap<String, Object>();

                    for (Entry<String, Object> entry : fieldAndValue.entrySet()) {
                        String field = entry.getKey();
                        if (!field.startsWith("$")) {
                            newFieldAndValue.put(denormalizedToField + ".$." + field, entry.getValue());
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
    public boolean isCached(String docType) {
        return EmbeddedDocumentRelations.isCached(docType);
    }
    public void addToCache(List<Entity> entityList, String collectionName) {
        Map<String,Entity> entityCache = new HashMap<String, Entity>();
        for (Entity entity: entityList) {
            entityCache.put(entity.getEntityId(),entity);
        }
        denormalizationHelperCache.put(collectionName,entityCache);
    }
}
