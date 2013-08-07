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

package org.slc.sli.dal.repository;

import java.util.*;
import java.util.Map.Entry;

import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.domain.EmbeddedDocumentRelations;
import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.common.util.tenantdb.TenantIdToDbName;
import org.slc.sli.common.util.uuid.UUIDGeneratorStrategy;
import org.slc.sli.dal.RetryMongoCommand;
import org.slc.sli.dal.convert.ContainerDocumentAccessor;
import org.slc.sli.dal.convert.Denormalizer;
import org.slc.sli.dal.convert.SubDocAccessor;
import org.slc.sli.dal.convert.SuperdocConverter;
import org.slc.sli.dal.convert.SuperdocConverterRegistry;
import org.slc.sli.dal.encrypt.EntityEncryption;
import org.slc.sli.dal.migration.config.MigrationRunner.MigrateEntity;
import org.slc.sli.dal.migration.config.MigrationRunner.MigrateEntityCollection;
import org.slc.sli.dal.versioning.SliSchemaVersionValidatorProvider;
import org.slc.sli.domain.AccessibilityCheck;
import org.slc.sli.domain.CascadeResult;
import org.slc.sli.domain.CascadeResultError;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.FullSuperDoc;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.SchemaReferencePath;
import org.slc.sli.validation.schema.SchemaReferencesMetaData;

/**
 * mongodb implementation of the entity repository interface that provides basic
 * CRUD and field query methods for entities including core entities and
 * association entities
 *
 * @author Dong Liu dliu@wgen.net
 */

public class MongoEntityRepository extends MongoRepository<Entity> implements InitializingBean,
        ValidationWithoutNaturalKeys {


    public static final String SCHOOL_EDORG_LINEAGE = "edOrgs";

    private static final int DEL_LOG_IDENT = 4;

    public static final int DELETE_BASE_DEPTH = 0;         // public for unit tests

    private static final Logger DELETION_LOG = LoggerFactory.getLogger("CascadingDeletionLog");

    @Autowired
    private EntityValidator validator;

    @Autowired(required = false)
    @Qualifier("entityEncryption")
    private EntityEncryption encrypt;

    @Autowired
    @Qualifier("deterministicUUIDGeneratorStrategy")
    private UUIDGeneratorStrategy uuidGeneratorStrategy;

    @Autowired
    private INaturalKeyExtractor naturalKeyExtractor;

    @Autowired
    private SuperdocConverterRegistry converterReg;

    @Autowired
    @Qualifier("entityKeyEncoder")
    private EntityKeyEncoder keyEncoder;

    @Autowired
    private SchemaReferencesMetaData schemaRefMetaData;

    @Value("${sli.maxCascadeDeleteDepth:200}")
    private Integer maxCascadeDeleteDepth;

    @Value("${sli.default.mongotemplate.writeConcern}")
    private String writeConcern;

    private SubDocAccessor subDocs;

    private Denormalizer denormalizer;

    private ContainerDocumentAccessor containerDocumentAccessor;

    @Autowired
    protected SliSchemaVersionValidatorProvider schemaVersionValidatorProvider;

    @Autowired(required = false)
    private DeltaJournal journal;

    @Override
    public void afterPropertiesSet() {
        setWriteConcern(writeConcern);
        subDocs = new SubDocAccessor(getTemplate(), uuidGeneratorStrategy, naturalKeyExtractor);
        denormalizer = new Denormalizer(getTemplate());
        containerDocumentAccessor = new ContainerDocumentAccessor(uuidGeneratorStrategy, naturalKeyExtractor, template);
    }

    @Override
    public void setReferenceCheck(String referenceCheck) {
        validator.setReferenceCheck(referenceCheck);

    }

    @Override
    protected String getRecordId(Entity entity) {
        return entity.getEntityId();
    }

    private List<String> getIds(Iterable<Entity> entities) {
        List<String> ids = new ArrayList<String>();
        for (Entity entity : entities) {
            ids.add(entity.getEntityId());
        }
        return ids;
    }

    @Override
    protected Class<Entity> getRecordClass() {
        return Entity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterable<Entity> findAllAcrossTenants(String collectionName, Query mongoQuery) {
        List<Entity> crossTenantResults = Collections.emptyList();

        guideIfTenantAgnostic("realm");
        List<String> distinctTenantIds = template.getCollection("realm").distinct("body.tenantId");

        String originalTenantId = TenantContext.getTenantId();
        try {
            crossTenantResults = issueQueryToTenantDbs(collectionName, mongoQuery, distinctTenantIds);
        } finally {
            TenantContext.setTenantId(originalTenantId);
        }

        return crossTenantResults;
    }

    private List<Entity> issueQueryToTenantDbs(String collectionName, Query mongoQuery, List<String> distinctTenantIds) {
        List<Entity> crossTenantResults = new ArrayList<Entity>();

        guideIfTenantAgnostic(collectionName);
        for (String tenantId : distinctTenantIds) {
            // escape nasty characters

            String dbName = TenantIdToDbName.convertTenantIdToDbName(tenantId);

            if (isValidDbName(dbName)) {
                TenantContext.setTenantId(tenantId);

                List<Entity> resultsForThisTenant = template.find(mongoQuery, getRecordClass(), collectionName);
                crossTenantResults.addAll(resultsForThisTenant);
            }
        }
        return crossTenantResults;
    }

    private boolean isValidDbName(String tenantId) {
        return tenantId != null && !"sli".equalsIgnoreCase(tenantId) && tenantId.length() > 0
                && tenantId.indexOf(' ') == -1;
    }

    public Entity createWithRetries(final String type, final Map<String, Object> body,
            final Map<String, Object> metaData, final String collectionName, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return create(type, body, metaData, collectionName);
            }
        };
        return (Entity) rc.executeOperation(noOfRetries);
    }

    @Override
    public Entity createWithRetries(final String type, final String id, final Map<String, Object> body,
            final Map<String, Object> metaData, final String collectionName, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                return internalCreate(type, id, body, metaData, collectionName);
            }
        };
        return (Entity) rc.executeOperation(noOfRetries);
    }

    @Override
    public boolean patch(String type, String collectionName, String id, Map<String, Object> newValues) {
        boolean result = false;
        Entity entity = new MongoEntity(type, id, newValues, null);
        validator.validatePresent(entity);
        validator.validateNaturalKeys(entity, false);
        keyEncoder.encodeEntityKey(entity);
        if (subDocs.isSubDoc(collectionName)) {

            // prepare to find desired record to be patched
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(idConverter.toDatabaseId(id)));
            query.addCriteria(createTenantCriteria(collectionName));

            // prepare update operation for record to be patched
            Update update = new Update();
            for (Entry<String, Object> patch : newValues.entrySet()) {
                update.set("body." + patch.getKey(), patch.getValue());
            }
            result = subDocs.subDoc(collectionName).doUpdate(query, update);
        } else if (containerDocumentAccessor.isContainerDocument(type)) {
            result = containerDocumentAccessor.update(type, id, newValues, collectionName);
        } else {
            result = super.patch(type, collectionName, id, newValues);
            if (result
                    && EntityNames.EDUCATION_ORGANIZATION.equals(collectionName)
                    && newValues.containsKey(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE)) {
                // TODO can be optimized to take an edOrg Id
                updateAllSchoolLineage();
            }
        }

        if (result && denormalizer.isDenormalizedDoc(collectionName)) {
            Entity updateEntity;
            if (subDocs.isSubDoc(collectionName)) {
                updateEntity = subDocs.subDoc(collectionName).findById(id);
            } else {
                updateEntity = super.findById(collectionName, id);
            }

            Update update = new Update();
            for (Map.Entry<String, Object> patch : newValues.entrySet()) {
                update.set(patch.getKey(), patch.getValue());
            }

            denormalizer.denormalization(collectionName).doUpdate(updateEntity, update);
        }

        if (journal != null) {
            journal.journal(id, collectionName, false);
        }

        return result;
    }

    @Override
    public Entity create(String type, Map<String, Object> body, Map<String, Object> metaData, String collectionName) {
        return internalCreate(type, null, body, metaData, collectionName);
    }

    /*
     * This method should be private, but is used via mockito in the tests, thus
     * it's public. (S. Altmueller)
     */
    Entity internalCreate(String type, String id, Map<String, Object> body, Map<String, Object> origMetaData,
            String collectionName) {
        Assert.notNull(body, "The given entity must not be null!");
        Map<String, Object> metaData = origMetaData == null ? new HashMap<String, Object>() : origMetaData;

        MongoEntity entity = new MongoEntity(type, id, body, metaData);
        keyEncoder.encodeEntityKey(entity);

        this.addTimestamps(entity);
        this.schemaVersionValidatorProvider.getSliSchemaVersionValidator().insertVersionInformation(entity);
        Entity result;

        if (subDocs.isSubDoc(collectionName)) {
            validator.validate(entity);
            validator.validateNaturalKeys(entity, true);
            subDocs.subDoc(collectionName).create(entity);

            result = entity;
        } else if (containerDocumentAccessor.isContainerDocument(entity.getType())) {
            validator.validate(entity);
            validator.validateNaturalKeys(entity, true);
            final String createdId = containerDocumentAccessor.insert(entity);
            if (!createdId.isEmpty()) {
                result = new MongoEntity(type, createdId, entity.getBody(), metaData);
            } else {
                result = entity;
            }
        } else {
            SuperdocConverter converter = converterReg.getConverter(collectionName);
            if (converter != null) {
                converter.bodyFieldToSubdoc(entity);
            }
            validator.validate(entity);
            validator.validateNaturalKeys(entity, true);

            result = super.insert(entity, collectionName);

            // Update edOrg lineage cache for schools if needed
            if (result != null && EntityNames.EDUCATION_ORGANIZATION.equals(collectionName)) {
                // TODO can be optimized to take an edOrg Id
                updateAllSchoolLineage();
            }
        }
        if (denormalizer.isDenormalizedDoc(collectionName)) {
            denormalizer.denormalization(collectionName).create(entity);
        }
        if (journal != null) {
            journal.journal(result.getEntityId(), collectionName, false);
        }

        return result;
    }

    @Override
    public List<Entity> insert(List<Entity> records, String collectionName) {
        List<Entity> results;

        // TODO this is a kludge around mongodb JIRA 6802. Remove when migration to mongo 2.4+ happens
        // For superdocs upsert rather than bulk insert since there is an intermittent mongos bug reporting errors
        // which can lead to superdoc bulk inserts failing silently
        if (EmbeddedDocumentRelations.isSubDocParentEntityType(collectionName)) {
            results = new ArrayList<Entity>();
            for (Entity entity : records) {
                if (update(collectionName, entity, false)) {
                    results.add(entity);
                }
            }
            return results;
        }

        for (Entity entity : records) {
            this.schemaVersionValidatorProvider.getSliSchemaVersionValidator().insertVersionInformation(entity);
        }

        if (subDocs.isSubDoc(collectionName)) {
            subDocs.subDoc(collectionName).insert(records);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).insert(records);
            }

            results = records;
        } else if (containerDocumentAccessor.isContainerDocument(collectionName)) {
            containerDocumentAccessor.insert(records);
            results = records;
        } else {
            List<Entity> persist = new ArrayList<Entity>();
            boolean updateLineage = EntityNames.EDUCATION_ORGANIZATION.equals(collectionName);

            for (Entity record : records) {
                Entity entity = new MongoEntity(record.getType(), null, record.getBody(), record.getMetaData());
                keyEncoder.encodeEntityKey(entity);
                persist.add(entity);
            }

            results = super.insert(persist, collectionName);

            if (updateLineage) {
                // TODO can be optimized to take a set of edOrg Ids
                updateAllSchoolLineage();
            }

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).insert(records);
            }
            if (denormalizer.isCached(collectionName)) {
                denormalizer.addToCache(results, collectionName);
            }
        }
        if (journal != null) {
            journal.journal(getIds(results), collectionName, false);
        }
        return results;
    }

    @Override
    @MigrateEntity
    public Entity findOne(String collectionName, Query query) {
        if (subDocs.isSubDoc(collectionName) || containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            List<Entity> entities = subDocs.isSubDoc(collectionName) ? subDocs.subDoc(collectionName).findAll(query)
                    : containerDocumentAccessor.findAll(collectionName, query);
            if (entities != null && entities.size() > 0) {
                return entities.get(0);
            }
            return null;
        }
        if (FullSuperDoc.FULL_ENTITIES.containsKey(collectionName)) {
            Set<String> embededFields = FullSuperDoc.FULL_ENTITIES.get(collectionName);
            addEmbededFields(query, embededFields);
        }
        Entity entity = super.findOne(collectionName, query);
        if (entity != null) {
            SuperdocConverter converter = converterReg.getConverter(entity.getType());
            if (converter != null) {
                return converter.subdocToBodyField(entity);
            }
        }
        return entity;
    }

    // TODO derive this from the SLI.xsd
    private static final Map<String, String> ENTITY_BASE_TYPE_MAP;
    static {
        ENTITY_BASE_TYPE_MAP = new HashMap<String, String>();
        ENTITY_BASE_TYPE_MAP.put("school", "educationOrganization");
        ENTITY_BASE_TYPE_MAP.put("localEducationAgency", "educationOrganization");
        ENTITY_BASE_TYPE_MAP.put("stateEducationAgency", "educationOrganization");
        ENTITY_BASE_TYPE_MAP.put("teacher", "staff");
    }

    /**
     * Get the union of references that need to be queried if a an entity of the specified type is
     * to be deleted
     *
     * @param entityType
     *            the entity type that is referenced
     * @return
     */
    List<SchemaReferencePath> getAllReferencesTo(String entityType) {
        Set<SchemaReferencePath> set = new HashSet<SchemaReferencePath>();
        String baseType = ENTITY_BASE_TYPE_MAP.get(entityType);

        // No inheriting relations from other types
        if (baseType == null) {
            return schemaRefMetaData.getReferencesTo(entityType);
        }

        // Inherits relations from other types
        set.addAll(schemaRefMetaData.getReferencesTo(entityType));
        set.addAll(schemaRefMetaData.getReferencesTo(baseType));

        return new ArrayList<SchemaReferencePath>(set);
    }

    // HACK to explicitly map from specific to base types
    // once the DAL interface data model is well defined, this should not be needed
    String getEntityRepositoryType(String entityType) {
        String repositoryType = entityType;  // default to the entityType

        String baseType = ENTITY_BASE_TYPE_MAP.get(entityType);
        if (baseType != null) {
            // check for base class differences that change the entity we reference
            repositoryType = baseType;
        }

        return repositoryType;
    }

    @Override
    public CascadeResult safeDelete(String entityType, String id, boolean cascade, boolean dryrun, boolean force,
            boolean logViolations, Integer maxObjects, AccessibilityCheck access) {

        // LOG.info("*** DELETING object '" + id + "' of type '" + entityType + "'");
        DELETION_LOG.info("Delete request for entity: " + entityType + " _id: " + id + " cascade: " + cascade
                + " dryrun: " + dryrun);
        CascadeResult result = null;
        Set<String> deletedIds = new HashSet<String>();

        // Always do a dryrun first
        result = safeDeleteHelper(entityType, id, cascade, true, force, logViolations, maxObjects, access, DELETE_BASE_DEPTH,
                deletedIds);

        if (result.getStatus() == CascadeResult.Status.SUCCESS) {
            if (maxObjects != null && result.getnObjects() > maxObjects) {
                // We have exceeded max affected objects
                String message = "Maximum affected objects exceeded when deleting custom entities for entity with id "
                        + id + " and type " + entityType;
                DELETION_LOG.error(message);

                result.setStatus(CascadeResult.Status.MAX_OBJECTS_EXCEEDED);
            } else if (!dryrun) {
                // Do the actual deletes with some confidence
                deletedIds.clear();
                result = safeDeleteHelper(entityType, id, cascade, false, force, logViolations, maxObjects, access, DELETE_BASE_DEPTH,
                        deletedIds);
                result.setDeletedIds(deletedIds);
            }
        }

        return result;
    }

    @Override
    public CascadeResult safeDelete(Entity entity, String id, boolean cascade, boolean dryrun,
                                    boolean force, boolean logViolations, Integer maxObjects, AccessibilityCheck access) {
        String entityType = entity.getType();
        if ((!containerDocumentAccessor.isContainerDocument(entityType)) || containerDocumentAccessor.isContainerSubdoc(entityType)) {
            return safeDelete(entityType, id, cascade, dryrun, force, logViolations, maxObjects, access);
        }

        // Container docs without subdocs are special cases.
        String embeddedDocType = containerDocumentAccessor.getEmbeddedDocType(entityType);
        DELETION_LOG.info("Delete request for " + embeddedDocType + " from record: " + entityType + " with id: " + entity.getEntityId());

        // Delete the subdoc embedded within the container doc entity.
        CascadeResult result = new CascadeResult();
        boolean deleted = containerDocumentAccessor.deleteContainerNonSubDocs(entity);
        if (!deleted) {
            String message = "Cannot delete " + embeddedDocType + " from " + entityType + " record with id " + id;
            DELETION_LOG.error(message);
            result.addError(1, message, CascadeResultError.ErrorType.DELETE_ERROR, entityType, id);
            return result;
        }

        result.getDeletedIds().add(id);
        return result;
    }

    /**
     * Recursive helper used to cascade deletes to referencing entities
     *
     *  The only reason this method is not broken up into smaller methods is because it would hide the recursive call
     *  which I believe would make it more difficult to maintain than its current state
     *
     * @param entityType        type of the entity to delete
     * @param id                id of the entity to delete
     * @param cascade           delete related entities if true
     * @param dryrun            only delete if true
     * @param force             true iff the operation should delete the entity whether or not it is referred to by other entities
     * @param logViolations     true iff the operation should log referential integrity violation information
     * @param maxObjects        if the number of entities that will be deleted is > maxObjects, no deletes will be done
     * @param access            callback used to determine whether we have rights to delete an entity
     * @param depth             the depth of cascading the current entity is at - used to determine result.depth
     * @param deletedIds        Used to store deleted (or would be deleted if dryrun == true) for number objects
     * @return
     */
    private CascadeResult safeDeleteHelper(String entityType, String id, boolean cascade, boolean dryrun,
            boolean force, boolean logViolations, Integer maxObjects, AccessibilityCheck access, int depth,
            Set<String> deletedIds) {
        CascadeResult result = new CascadeResult();
        String repositoryEntityType = getEntityRepositoryType(entityType);

        result.setDepth(depth);

        // Sanity check for maximum depth
        if (depth > maxCascadeDeleteDepth) {
            String message = "Maximum delete cascade depth exceeded for entity type " + entityType + " with id " + id
                    + " at depth " + depth;
            LOG.debug(message);
            result.addError(depth, message, CascadeResultError.ErrorType.MAX_DEPTH_EXCEEDED, entityType, id);
            return result;
        }

        // Delete the id only if it hasn't already been deleted - needed for correct dryrun counts
        if (deletedIds.contains(id)) {
            return result;
        }

        // Check accessibility to this entity
        // TODO this looks like it will need to take collection as an argument
        if ((access != null) && !access.accessibilityCheck(id)) {
            String message = "Access denied for entity type " + entityType + " with id " + id + " at depth " + depth;
            LOG.debug(message);
            result.addError(depth, message, CascadeResultError.ErrorType.ACCESS_DENIED, entityType, id);
            return result;
        }

        List<SchemaReferencePath> refFields = getAllReferencesTo(entityType);

        if (dryrun) {
            result.initIdTree(entityType, id);
        }

        // Process each referencing entity field that COULD reference the deleted ID
        for (SchemaReferencePath referencingFieldSchemaInfo : refFields) {
            String referenceEntityType = referencingFieldSchemaInfo.getEntityName();
            String referenceField = referencingFieldSchemaInfo.getFieldPath();
            String referenceTypeField = referenceEntityType + "." + referenceField;

            List<Map<String, Object>> idTreeChildList = null;
            if (dryrun) {
                idTreeChildList = result.addRefField(referenceTypeField);
            }

            // Form the query to access the referencing entity's field values
            NeutralQuery neutralQuery = new NeutralQuery();
            neutralQuery.addCriteria(new NeutralCriteria(referenceField + "=" + id));
            DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * depth) + "Handling all references of type "
                    + referenceEntityType + "." + referenceField + " = " + id);

            // List all entities that have the deleted entity's ID in one or more
            // referencing fields
            Iterable<Entity> referencingEntities = this.findAll(getEntityRepositoryType(referenceEntityType),
                    neutralQuery);
            for (Entity entity : referencingEntities) {
                // Note we are examining entities one level below our initial depth now
                int referencingEntityDepth = depth +1;
                String referencerId = entity.getEntityId();
                String referent = referenceEntityType + "." + referencerId;
                String referentPath = referent + "." + referenceField;
                DELETION_LOG
                        .info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Handling reference " + referentPath);

                // Check accessibility to this entity
                // TODO this looks like it will need to take collection as an argument
                if ((access != null) && !access.accessibilityCheck(referencerId)) {
                    String message = "Access denied for entity type " + referenceEntityType + " with id "
                            + referencerId + " at depth " + referencingEntityDepth;
                    LOG.debug(message);
                    result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.ACCESS_DENIED,
                            referenceEntityType, referencerId);
                    continue;  // skip to the next referencing entity
                }

                // Non-cascade and Force handling
                if (!cascade) {
                    // There is a child when there shouldn't be
                    String message = "Child reference of entity type " + referenceEntityType + " id " + referencerId
                            + " exists for entity type " + entityType + " id " + id;
                    if (!force) {
                        result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.CHILD_DATA_EXISTS,
                                referenceEntityType, referencerId);
                    } else if (logViolations) {
                        result.addWarning(referencingEntityDepth, message, CascadeResultError.ErrorType.CHILD_DATA_EXISTS,
                                referenceEntityType, referencerId);
                    }
                    continue;
                }

                // Determine if this is the last value in a list of references
                boolean isLastValueInReferenceList = false;
                Map<String, Object> body = entity.getBody();
                List<?> childRefList = null;
                if (referencingFieldSchemaInfo.isArray()) {
                    childRefList = (List<?>) body.get(referenceField);
                    if (childRefList != null) {
                        isLastValueInReferenceList = childRefList.size() == 1;
                    } else {
                        String message = "List child ref '" + referentPath + "' to entity type '" + entityType
                                + "' ID '" + id + "' located child object, but not in child body";
                        result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.DATABASE_ERROR,
                                referenceEntityType, referencerId);
                        return result;  // no reference to remove
                    }
                }

                if (referencingFieldSchemaInfo.isRequired()
                        && (!referencingFieldSchemaInfo.isArray() || isLastValueInReferenceList)) {
                    // Recursively delete the referencing entity if:
                    // 1. it is a required field and is a non-list reference
                    // 2. it is a required field and it is the last reference in a list of
                    // references

                    DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Required" + referentPath
                            + "Invoking cascading deletion of " + referent);
                    CascadeResult recursiveResult = safeDeleteHelper(referenceEntityType, referencerId, cascade,
                            dryrun, force, logViolations, maxObjects, access, referencingEntityDepth, deletedIds);
                    DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth)
                            + recursiveResult.getStatus().name() + " Cascading deletion of " + referent);

                    // Update the overall result depth if necessary
                    if (result.getDepth() < recursiveResult.getDepth()) {
                        // report the deepest depth
                        result.setDepth(recursiveResult.getDepth());
                    }

                    // Accumulate object list in dry run for reporting
                    if (dryrun) {
                        idTreeChildList.add(recursiveResult.getIdTree());
                    }

                    // Update the affected entity counts
                    result.getDeletedIds().addAll(recursiveResult.getDeletedIds());
                    result.getReferenceFieldPatchedIds().addAll(recursiveResult.getReferenceFieldPatchedIds());
                    result.getReferenceFieldRemovedIds().addAll(recursiveResult.getReferenceFieldRemovedIds());

                    // Update errors
                    result.getErrors().addAll(recursiveResult.getErrors());

                    // Update the overall status to be the latest non-SUCCESS
                    if (recursiveResult.getStatus() != CascadeResult.Status.SUCCESS) {
                        result.setStatus(recursiveResult.getStatus());
                        continue;  // go to the next referencing entity
                    }

                } else if (referencingFieldSchemaInfo.isOptional()
                        && (!referencingFieldSchemaInfo.isArray() || isLastValueInReferenceList)) {
                    // Remove the field from the entity if:
                    // 1. it is an optional field and is a non-list reference
                    // 2. it is an optional field and it is the last reference in a list of
                    // references
                    DELETION_LOG
                            .info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Removing field " + referentPath);

                    // Make sure child ref is actually in the body for deletion (shoud be, as it was
                    // used to find the child)
                    if (!entity.getBody().containsKey(referencingFieldSchemaInfo.getMappedPath())) {
                        String message = "Single-valued child ref '" + referentPath + "' to entity type '" + entityType
                                + "' ID '" + id + "' located child object, but not in child body";
                        result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.DATABASE_ERROR,
                                referenceEntityType, referencerId);
                        return result;
                    }

                    if (dryrun) {
                        result.getReferenceFieldRemovedIds().add(entity.getEntityId());  // assume
                                                                                        // successful
                                                                                        // update
                                                                                        // for
                                                                                        // dryrun
                    } else {
                        entity.getBody().remove(referencingFieldSchemaInfo.getMappedPath());
                        if (!this.update(getEntityRepositoryType(referenceEntityType), entity, true,
                                FullSuperDoc.isFullSuperdoc(entity), true)) {
                            String message = "Unable to update entity type: " + referenceEntityType + ", entity id: "
                                    + referencerId + ", field name: " + referenceField + " at depth " + referencingEntityDepth;
                            LOG.debug(message);
                            result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.UPDATE_ERROR,
                                    referenceEntityType, referencerId);
                            DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Failed removing field "
                                    + referentPath);
                            continue;  // skip to the next referencing entity
                        } else {
                            result.getReferenceFieldRemovedIds().add(entity.getEntityId());
                            DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Removed field "
                                    + referentPath);
                        }
                    }
                } else if (referencingFieldSchemaInfo.isArray() && !isLastValueInReferenceList) {
                    // Remove the matching reference from the list of references:
                    // 1. it is NOT the last reference in a list of references
                    DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Adjusting field "
                            + referentPath);

                    // Make sure child ref is actually in the body for deletion (should be, as it
                    // was used to find the child)
                    if (!childRefList.contains(id)) {
                        String message = "Array child ref '" + referentPath + "' to entity type '" + entityType
                                + "' ID '" + id + "' located child object, but not in child body";
                        result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.DATABASE_ERROR,
                                referenceEntityType, referencerId);
                        return result;
                    }

                    if (dryrun) {
                        result.getReferenceFieldPatchedIds().add(referencerId);  // assume successful
                                                                                // patch for dryrun
                    } else {
                        childRefList.remove(id);
                        Map<String, Object> patchEntityBody = new HashMap<String, Object>();
                        patchEntityBody.put(referenceField, childRefList);

                        if (!this.patch(referenceEntityType, getEntityRepositoryType(referenceEntityType),
                                referencerId, patchEntityBody)) {
                            // 1. it is NOT the last reference in a list of references
                            String message = "Database error while patching entity type: " + referenceEntityType
                                    + ", entity id: " + referencerId + ", field name: " + referenceField + " at depth "
                                    + referencingEntityDepth;
                            LOG.debug(message);
                            result.addError(referencingEntityDepth, message, CascadeResultError.ErrorType.PATCH_ERROR,
                                    referenceEntityType, referencerId);
                            DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth)
                                    + "Failed adjusting field " + referentPath);
                            continue;  // skip to the next referencing entity
                        } else {
                            result.getReferenceFieldPatchedIds().add(referencerId);
                            DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * referencingEntityDepth) + "Adjusted field "
                                    + referentPath);
                        }
                    }
                }

            } // per entity processing loop
        }

        // Base case : delete the current entity
        if (result.getStatus() == CascadeResult.Status.SUCCESS) {
            if (dryrun) {
                // Track deleted ids
                deletedIds.add(id);               // global deleted ids tracking
                result.getDeletedIds().add(id);   // local result deleted ids tracking
            } else {
                DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * depth) + "Finally deleting "
                        + repositoryEntityType + "." + id);
                if (!delete(repositoryEntityType, id, force)) {
                    String message = "Failed to delete entity with id " + id + " and type " + entityType;
                    LOG.debug(message);
                    DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * depth) + "Failed finally deleting "
                            + repositoryEntityType + "." + id);
                    result.addError(depth, message, CascadeResultError.ErrorType.DELETE_ERROR, entityType, id);
                    return result;
                } else {
                    // Track deleted ids
                    deletedIds.add(id);               // global deleted ids tracking
                    result.getDeletedIds().add(id);   // local result deleted ids tracking
                    DELETION_LOG.info(StringUtils.repeat(" ", DEL_LOG_IDENT * depth) + "Finally deleted "
                            + repositoryEntityType + "." + id);
                }

                // Delete custom entities attached to this entity
                // custom entities inherit the access of the referenced entity
                deleteAttachedCustomEntities(id, dryrun, result);
            }

        }

        return result;
    }

    private Integer deleteAttachedCustomEntities(String sourceId, boolean dryrun, CascadeResult result) {
        Integer count = 0;
        NeutralQuery query = new NeutralQuery();
        query.addCriteria(new NeutralCriteria("metaData." + CUSTOM_ENTITY_ENTITY_ID, "=", sourceId, false));
        Iterable<String> ids = this.findAllIds(CUSTOM_ENTITY_COLLECTION, query);
        for (String id : ids) {
            count++;
            if (!dryrun) {
                if (!delete(CUSTOM_ENTITY_COLLECTION, id)) {
                    String message = "Failed to delete custom entity " + id + " referencing id " + sourceId;
                    result.addError(result.getDepth(), message, CascadeResultError.ErrorType.DELETE_ERROR,
                            CUSTOM_ENTITY_COLLECTION, id);
                    LOG.error(message);
                }
            }
        }
        return count;
    }

    /* Delete the entity with the given ID and type.  Note that "collectionName"
     * is really the entity type and does not always correspond to the actual
     * MongoDB collection, in the case of entity types embedded in the collections of
     * other entity types.
     */
    @Override
    public boolean delete(String collectionName, String id) {
    	return delete(collectionName, id, false);
    }

    /*
     * Version of delete that has special handling for "force" deletes: if embedded
     * data in subdocs exists, leave it in place, "hollowing out" the containing
     * document by removing only the body/metaData, and leaving type, _id, and
     * contained documents.
     */
    private boolean delete(String collectionName, String id, boolean force) {
        boolean result;
        if (subDocs.isSubDoc(collectionName)) {
            Entity entity = subDocs.subDoc(collectionName).findById(id);

            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).delete(entity, id);
            }

            result = subDocs.subDoc(collectionName).delete(entity);
        } else if (containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            Entity entity = containerDocumentAccessor.findById(collectionName, id);
            if (entity == null) {
                LOG.warn("Could not find entity {} in collection {}", id, collectionName);
                return false;
            }
            result = containerDocumentAccessor.delete(entity);
        } else {
            if (denormalizer.isDenormalizedDoc(collectionName)) {
                denormalizer.denormalization(collectionName).delete(null, id);
            }
            if ( force ) {
            	result = deleteOrHollowOut(collectionName, id);
            }
            else {
            	result = super.delete(collectionName, id);
            }

            // update school lineage cache iff updating educationOrganization.parentEducationAgencyReference
            if (result
                    && collectionName.equals(EntityNames.EDUCATION_ORGANIZATION)) {
                // TODO can be optimized to take an edOrg Id
                updateAllSchoolLineage();
            }
        }

        if (result && journal != null) {
            journal.journal(id, collectionName, true);
        }

        return result;
    }

    /*
     * Examine document in given MongoDB collection, known not to be a subdoc.
     * If it contains keys other than
     * the standard "entity" keys (_id, type, metaData, body), AND these other keys
     * have non-empty data, then, instead of deleting the document, update it in place
     * to remove the body and metadata, leaving _id, type and the contained doc data.
     */
    private boolean deleteOrHollowOut(String collectionName, String id) {
    	boolean result = false;
    	Entity entity = findById(collectionName, id, true);
        if(entity != null) {
            if ( entity.getEmbeddedData().size() > 0 ) {
                entity.hollowOut();
                result = update(collectionName, entity, true);
            }
            else {
                result = super.delete(collectionName, id);
            }
        } else {
            LOG.warn("Did not find {} with id {}", collectionName, id);
        }
    	return result;
    }

    @Override
    protected Query getUpdateQuery(Entity entity) {
        String id = getRecordId(entity);
        return new Query(Criteria.where("_id").is(idConverter.toDatabaseId(id)));
    }

    @Override
    protected Entity getEncryptedRecord(Entity entity) {
        MongoEntity encryptedEntity = new MongoEntity(entity.getType(), entity.getEntityId(), entity.getBody(),
                entity.getMetaData(), entity.getCalculatedValues(), entity.getAggregates());
        encryptedEntity.getEmbeddedData().putAll(entity.getEmbeddedData());
        encryptedEntity.encrypt(encrypt);
        return encryptedEntity;
    }

    @Override
    protected Update getUpdateCommand(Entity entity, boolean isSuperdoc) {
        // set up update query
        Update update = new Update();

        // It is possible for the body and metaData keys to be absent in the case
        // of "orphaned" subDoc data when a document has been "hollowed out"
        Map<String, Object> entityBody = entity.getBody();
        if ( entityBody != null && entityBody.size() == 0 ) {
        	update.unset("body");
        }
        else {
        	update.set("body", entityBody);
        }
        Map<String, Object> entityMetaData = entity.getMetaData();
        if (entityMetaData != null && entityMetaData.size() == 0 ) {
        	update.unset("metaData");
        }
        else {
        	update.set("metaData", entityMetaData);
        }

        // update should also set type in case of upsert
        String entityType = entity.getType();
        if (entityType != null && !entityType.isEmpty()) {
            update.set("type", entityType);
        }
        // superdoc need to update subdoc fields outside body
        if (isSuperdoc && entity.getEmbeddedData() != null) {
            Set<String> subdocFields = FullSuperDoc.FULL_ENTITIES.get(entity.getType());
            if (subdocFields != null) {
                for (String subdocField : subdocFields) {
                    List<Entity> subdocEntities = entity.getEmbeddedData().get(subdocField);
                    if (subdocEntities != null && subdocEntities.size() > 0) {
                        List<Map<String, Object>> updateEntities = new ArrayList<Map<String, Object>>();
                        for (Entity subdocEntity : subdocEntities) {
                            Map<String, Object> updateEntity = new HashMap<String, Object>();
                            updateEntity.put("_id", subdocEntity.getEntityId());
                            updateEntity.put("body", subdocEntity.getBody());
                            updateEntity.put("type", subdocEntity.getType());
                            updateEntity.put("metaData", subdocEntity.getMetaData());
                            updateEntities.add(updateEntity);
                        }
                        update.set(subdocField, updateEntities);
                    } else {
                        update.unset(subdocField);
                    }
                }
            }
        }
        return update;
    }

    @Override
    public boolean updateWithRetries(final String collection, final Entity entity, int noOfRetries) {
        RetryMongoCommand rc = new RetryMongoCommand() {

            @Override
            public Object execute() {
                // updateWithRetries is only used by ingestion, so always set isSuperdoc to false
                return update(collection, entity, false);
            }
        };
        Object result = rc.executeOperation(noOfRetries);
        if (result != null) {
            return (Boolean) result;
        } else {
            return false;
        }

    }

    @Override
    public boolean updateWithoutValidatingNaturalKeys(String collection, Entity entity) {
        return this.update(collection, entity, FullSuperDoc.isFullSuperdoc(entity));
    }

    @Override
    public boolean update(String collection, Entity entity, boolean isSuperdoc) {
        return this.update(collection, entity, true, isSuperdoc, false);
    }

    // TODO for now API does not allow deletes of assessment.assessmentFamilyReference so we need a
    // "special" flag for it hence deleteAssessmentFamilyReference
    private boolean update(String collection, Entity entity, boolean validateNaturalKeys, boolean isSuperdoc,
            boolean deleteAssessmentFamilyReference) {
        boolean result;

        if (validateNaturalKeys) {
            validator.validateNaturalKeys(entity, true);
        }

        this.updateTimestamp(entity);

        // convert subdoc from superdoc body to outside body
        SuperdocConverter converter = converterReg.getConverter(entity.getType());
        if (converter != null && isSuperdoc) {
            SuperdocConverter.Option option = null;
            if (deleteAssessmentFamilyReference) {
                option = SuperdocConverter.Option.DELETE_ASSESSMENT_FAMILY_REFERENCE;
            }
            // It is possible for the body and metaData keys to be absent in the case
            // of "orphaned" subDoc data when a document has been "hollowed out"
            Map<String, Object> body = entity.getBody();
            if ( body != null && body.size() > 0 ) {
            	converter.bodyFieldToSubdoc(entity, option);
            }
        }
        validator.validate(entity);
        if (denormalizer.isDenormalizedDoc(collection)) {
            denormalizer.denormalization(collection).create(entity);
        }
        if (subDocs.isSubDoc(collection)) {
            result = subDocs.subDoc(collection).create(entity);
        } else if (containerDocumentAccessor.isContainerDocument(collection)) {
            result = !containerDocumentAccessor.update(entity).isEmpty();
        } else {
            result = super.update(collection, entity, null, isSuperdoc); // body);

            // update school lineage cache iff updating educationOrganization.parentEducationAgencyReference
            if (result
                    && collection.equals(EntityNames.EDUCATION_ORGANIZATION)
                    && entity.getBody().containsKey(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE)) {
                // TODO can be optimized to take an edOrg Id
                updateAllSchoolLineage();
            }

        }
        if (result && journal != null) {
            journal.journal(entity.getEntityId(), collection, false);
        }
        return result;
    }

    /**
     * Add the created and updated timestamp to the document metadata.
     */
    private void addTimestamps(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();

        Map<String, Object> metaData = entity.getMetaData();
        metaData.put(EntityMetadataKey.CREATED.getKey(), now);
        metaData.put(EntityMetadataKey.UPDATED.getKey(), now);
    }

    /**
     * Update the updated timestamp on the document metadata.
     */
    public void updateTimestamp(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();
        Map<String, Object> metaData = entity.getMetaData();
        if ( null != metaData ) {
        	metaData.put(EntityMetadataKey.UPDATED.getKey(), now);
        }
    }

    public void setValidator(EntityValidator validator) {
        this.validator = validator;
    }

    @Override
    @MigrateEntity
    public Entity findById(String collectionName, String id) {
    	return findById(collectionName, id, false);
    }

    @Override
    public Entity findById(String collectionName, String id, boolean allFields) {
       	if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).findById(id);
        } else if (containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            return containerDocumentAccessor.findById(collectionName, id);
        }
        return super.findById(collectionName, id, allFields);
    }

    @Override
    public Iterable<String> findAllIds(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            NeutralQuery subDocNeutralQuery = neutralQuery == null ? new NeutralQuery() : neutralQuery;
            subDocNeutralQuery.setIncludeFieldString("_id");
            addDefaultQueryParams(subDocNeutralQuery, collectionName);
            Query q = getQueryConverter().convert(collectionName, subDocNeutralQuery);

            List<String> ids = new LinkedList<String>();
            for (Entity e : subDocs.subDoc(collectionName).findAll(q)) {
                ids.add(e.getEntityId());
            }
            return ids;
        }
        return super.findAllIds(collectionName, neutralQuery);
    }

    @Override
    @MigrateEntityCollection
    public Iterable<Entity> findAll(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName)) {
            this.addDefaultQueryParams(neutralQuery, collectionName);
            return subDocs.subDoc(collectionName).findAll(getQueryConverter().convert(collectionName, neutralQuery));
        }
        if (containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            this.addDefaultQueryParams(neutralQuery, collectionName);
            return containerDocumentAccessor.findAll(collectionName,
                    getQueryConverter().convert(collectionName, neutralQuery));
        }
        if (FullSuperDoc.FULL_ENTITIES.containsKey(collectionName)) {
            Set<String> embededFields = FullSuperDoc.FULL_ENTITIES.get(collectionName);
            addEmbededFields(neutralQuery, embededFields);
        }
        Iterable<Entity> entities = super.findAll(collectionName, neutralQuery);
        SuperdocConverter converter = converterReg.getConverter(collectionName);
        if (converter != null) {
            return converter.subdocToBodyField(entities);
        }
        return entities;
    }

    @Override
    public boolean exists(String collectionName, String id) {
        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).exists(id);
        } else if (containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            return containerDocumentAccessor.exists(collectionName, id);
        }
        return super.exists(collectionName, id);
    }

    @Override
    public long count(String collectionName, NeutralQuery neutralQuery) {
        if (subDocs.isSubDoc(collectionName) || containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            return count(collectionName, query);
        }
        return super.count(collectionName, neutralQuery);
    }

    @Override
    public long count(String collectionName, Query query) {
        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).count(query);
        } else if (containerDocumentAccessor.isContainerSubdoc(collectionName)) {
            return containerDocumentAccessor.count(collectionName, query);
        }
        return super.count(collectionName, query);
    }

    @Override
    public boolean doUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        if (subDocs.isSubDoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            return subDocs.subDoc(collectionName).doUpdate(query, update);
        }

        boolean result = super.doUpdate(collectionName, neutralQuery, update);

        // update school lineage cache iff updating educationOrganization.parentEducationAgencyReference
        if (result
                && collectionName.equals(EntityNames.EDUCATION_ORGANIZATION)) {
            // don't know specifically what edOrgs were updated so refresh all school lineage
            return updateAllSchoolLineage();
        }

        return result;
    }

    @Override
    public void deleteAll(String collectionName, NeutralQuery neutralQuery) {
        if (journal != null) {
            journal.journal(getIds(findAll(collectionName, neutralQuery)), collectionName, true);
        }
        if (subDocs.isSubDoc(collectionName)) {
            Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
            subDocs.subDoc(collectionName).deleteAll(query);
        } else {
            super.deleteAll(collectionName, neutralQuery);

            // update school lineage cache iff updating educationOrganization.parentEducationAgencyReference
            if (collectionName.equals(EntityNames.EDUCATION_ORGANIZATION)) {
                // TODO can be optimized to take an edOrg Id
                updateAllSchoolLineage();
            }
        }
    }

    /**
     * @Deprecated "This is a deprecated method that should only be used by the ingestion ID
     *             Normalization code.
     *             It is not tenant-safe meaning clients of this method must include tenantId in the
     *             metaData block"
     */
    @Override
    @Deprecated
    @MigrateEntityCollection
    public Iterable<Entity> findByQuery(String collectionName, Query origQuery, int skip, int max) {
        Query query = origQuery == null ? new Query() : origQuery;

        query.skip(skip).limit(max);

        if (subDocs.isSubDoc(collectionName)) {
            return subDocs.subDoc(collectionName).findAll(query);
        }

        return findByQuery(collectionName, query);
    }

    @Override
    @MigrateEntity
    public Entity findAndUpdate(String collectionName, NeutralQuery neutralQuery, Update update) {
        Query query = this.getQueryConverter().convert(collectionName, neutralQuery);
        FindAndModifyOptions options = new FindAndModifyOptions();
        Entity result = template.findAndModify(query, update, options, getRecordClass(), collectionName);

        if (result != null
                && collectionName.equals(EntityNames.EDUCATION_ORGANIZATION)) {
            updateAllSchoolLineage();
        }

        return result;
    }

    private Query addEmbededFields(Query query, Set<String> embededFields) {
        if (query == null) {
            return null;
        }
        DBObject fieldObjects = query.getFieldsObject();
        if (fieldObjects != null) {
            for (String embededField : embededFields) {
                if (!fieldObjects.containsField(embededField)) {
                    fieldObjects.put(embededField, 1);
                }
            }
        }
        return query;
    }

    private NeutralQuery addEmbededFields(NeutralQuery query, Set<String> embededFields) {
        if (query == null) {
            return null;
        }
        List<String> fields = new ArrayList<String>();
        fields.addAll(embededFields);
        query.setEmbeddedFields(fields);
        return query;
    }

    @Override
    public Iterator<Entity> findEach(String collectionName, NeutralQuery neutralQuery) {
        guideIfTenantAgnostic(collectionName);
        Query query = this.getQueryConverter().convert(collectionName, neutralQuery, true);
        return template.findEach(query, Entity.class, collectionName);
    }

    @Override
    public Iterator<Entity> findEach(String collectionName, Query query) {
        guideIfTenantAgnostic(collectionName);

        return template.findEach(query, Entity.class, collectionName);
    }

    @Override
    public Entity insert(Entity record, String collectionName) {
        Entity result = null;

        if (journal != null) {
            journal.journal(record.getEntityId(), collectionName, false);
        }

        // Cache education organization lineage for schools
        if (collectionName.equals(EntityNames.SCHOOL)) {
            record.getMetaData().put(SCHOOL_EDORG_LINEAGE, new ArrayList<String>(fetchLineage(record.getEntityId(), new HashSet<String>())));
        }

        result = super.insert(record, collectionName);

        // Update edOrg lineage cache for schools if needed
        if (result != null && EntityNames.EDUCATION_ORGANIZATION.equals(collectionName)) {
            // TODO can be optimized to take an edOrg Id
            updateAllSchoolLineage();
        }

        return result;
    }

    @Override
    public WriteResult updateMulti(NeutralQuery query, Map<String, Object> update, String collectionName) {
        WriteResult result = super.updateMulti(query, update, collectionName);

        // update school lineage cache iff updating educationOrganization
        // TODO possible optimization to check updates for parent ref changes
        if (result != null
                && collectionName.equals(EntityNames.EDUCATION_ORGANIZATION)) {
            // don't know specifically what edOrgs were updated so refresh all school lineage
            updateAllSchoolLineage();
        }

        if (journal != null) {
            journal.journal(getIds(findAll(collectionName, query)), collectionName, false);
        }
        return result;
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
    private Set<String> fetchLineage(String id, Set<String> parentsSoFar) {
        Set<String> parents = new HashSet<String>(parentsSoFar);
        if (id != null) {
            Entity edOrg = template.findOne(new Query().addCriteria(Criteria.where("_id").is(id)), Entity.class,
                    EntityNames.EDUCATION_ORGANIZATION);
            if (edOrg != null) {
                parents.add(id);
                Map<String, Object> body = edOrg.getBody();
                if (body.containsKey(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE)) {
                    List<String> myParents = (List<String>) body.get(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE);
                    if (myParents != null) {
                        for (String myParent : myParents) {
                            if (!parents.contains(myParent)) {
                                parents.addAll(fetchLineage(myParent, parents));
                            }
                        }
                    }
                }
            }
        }
        return parents;
    }

    // TODO add Set of edOrgs parameter to limit school documents needing recalc
    private boolean updateAllSchoolLineage() {
        boolean result = true;
        NeutralQuery categoryIsSchool = new NeutralQuery(new NeutralCriteria("organizationCategories",
                                                                             NeutralCriteria.CRITERIA_IN,
                                                                             Arrays.asList("School")));
        Iterator<Entity> schools = this.findEach(EntityNames.EDUCATION_ORGANIZATION, categoryIsSchool);
        while (schools.hasNext()) {
            if (!updateSchoolLineage(schools.next().getEntityId())) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Updates the cached education organization hierarchy for the specified school
     *
     * @param schoolId
     * @return whether the update succeeded
     */
    private boolean updateSchoolLineage(String schoolId) {
        try {
            NeutralQuery query = new NeutralQuery(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, schoolId));

            Update update = new Update();
            update.set("metaData.edOrgs", new ArrayList<String>(fetchLineage(schoolId, new HashSet<String>())));
            super.doUpdate(EntityNames.EDUCATION_ORGANIZATION, query, update);
        } catch (RuntimeException e) {
            LOG.error("Failed to update educational organization lineage for school with Id " + schoolId
                    + ". " + e.getMessage() + ".  Related data will not be visible.  Re-ingestion required.");
            throw e;
        }
        return true;
    }

}
