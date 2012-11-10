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

package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.transformation.normalization.ComplexKeyField;
import org.slc.sli.ingestion.transformation.normalization.ComplexRefDef;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
import org.slc.sli.ingestion.transformation.normalization.RefDef;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NaturalKeyExtractor;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * EdFi to SLI data transformation
 *
 * @author okrook
 *
 */
public abstract class EdFi2SLITransformer implements Handler<NeutralRecord, List<SimpleEntity>> {

    private static final Logger LOG = LoggerFactory.getLogger(EdFi2SLITransformer.class);

    protected static final String METADATA_BLOCK = "metaData";

    protected static final String ID = "_id";

    private IdNormalizer idNormalizer;

    private DeterministicIdResolver didResolver;

    private EntityConfigFactory entityConfigurations;

    private Repository<Entity> entityRepository;

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private INaturalKeyExtractor naturalKeyExtractor;

    @Autowired
    private DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy;

    @Override
    public List<SimpleEntity> handle(NeutralRecord item) {
        return handle(item, new DummyErrorReport());
    }

    @Override
    public List<SimpleEntity> handle(NeutralRecord item, ErrorReport errorReport) {

        resolveReferences(item, errorReport);

        if (errorReport.hasErrors()) {
            LOG.info("Issue was detected in EdFi2SLITransformer.resolveReferences()");
            return Collections.emptyList();
        }

        List<SimpleEntity> transformed = transform(item, errorReport);

        if (errorReport.hasErrors()) {
            LOG.info("Issue was detected in EdFi2SLITransformer.transform()");
            return Collections.emptyList();
        }

        if (transformed != null && !transformed.isEmpty()) {

            for (SimpleEntity entity : transformed) {

                if (entity.getMetaData() == null) {
                    entity.setMetaData(new HashMap<String, Object>());
                }

                if (item.getMetaData().get("edOrgs") != null) {
                    entity.getMetaData().put("edOrgs", item.getMetaData().get("edOrgs"));
                }

                try {
                    matchEntity(entity, errorReport);
                } catch (DataAccessResourceFailureException darfe) {
                    LOG.error("Exception in matchEntity", darfe);
                }

                if (errorReport.hasErrors()) {
                    return Collections.emptyList();
                }
            }
        } else {
            LOG.error("EdFi2SLI Transform has resulted in either a null or empty list of transformed SimpleEntities.");
        }

        return transformed;
    }

    protected void resolveReferences(NeutralRecord item, ErrorReport errorReport) {
        Entity entity = new NeutralRecordEntity(item);
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        ComplexRefDef ref = entityConfig.getComplexReference();
        if (ref != null) {
            String entityType = ref.getEntityType();
            String collectionName = getPersistedCollectionName(entityType);

            idNormalizer.resolveReferenceWithComplexArray(entity, item.getSourceId(), ref.getValueSource(),
                    ref.getFieldPath(), collectionName, ref.getPath(), ref.getComplexFieldNames(), errorReport);
        }

//        didResolver.resolveInternalIds(entity, item.getSourceId(), errorReport);

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);

        // propagate context according to configuration
        giveContext(entity, entityConfig, item.getSourceId());
    }

    /**
     * Checks the entity for 'body.exitWithdrawDate' and marks the association as expired
     * accordingly.
     *
     * @param entity
     *            Entity to check for expiry.
     * @return True (if association is expired) and False otherwise.
     */
    private boolean isAssociationExpired(Entity entity) {
        boolean expired = false;
        if (entity.getType().equals("studentSchoolAssociation") && entity.getBody().containsKey("ExitWithdrawDate")) {
            try {
                DateTime exitWithdrawDate = DateTime.parse((String) entity.getBody().get("ExitWithdrawDate"));
                if (exitWithdrawDate.isBefore(DateTime.now().minusDays(Integer.valueOf(gracePeriod)))) {
                    expired = true;
                }
            } catch (Exception e) {
                LOG.warn(
                        "Error parsing ExitWithdrawDate for student: {} at school: {} --> continuing as if date was absent.",
                        new Object[] { entity.getBody().get("studentId"), entity.getBody().get("schoolId") });
            }
        }
        return expired;
    }

    public void giveContext(Entity entity, EntityConfig entityConfig, String tenantId) {

        String deterministicId = null;
        if (isEducationOrganization(entity.getType())) {
            // Calculate deterministic id for educationOrganization and school
            // This is important because the edOrg id is currently used for stamping metaData
            // during ingestion. Therefore, the id needs to be known now, rather than
            // waiting till the entity is persisted in the DAL.

            // Normally, NaturalKeyDescriptors are generated based on the sli.xsd, but in this
            // case, we need to generate one ahead of time (for context stamping), so it will
            // be built by hand in this case
            Map<String, String> naturalKeys = new HashMap<String, String>();
            String stateOrganizationId = (String) entity.getBody().get("stateOrganizationId");

            if (tenantId == null ) {
                LOG.error("Failed to extract tenantId for EducationOrganization");
            }
            if (stateOrganizationId == null) {
                LOG.error("Failed to extract natural key for EducationOrganization");
            }

            naturalKeys.put("stateOrganizationId", stateOrganizationId);

            NaturalKeyDescriptor descriptor = new NaturalKeyDescriptor(naturalKeys, tenantId,
                    entity.getType(), null);
            descriptor.setEntityType("educationOrganization");

            deterministicId = deterministicUUIDGeneratorStrategy.generateId(descriptor);

            @SuppressWarnings("unchecked")
            List<String> edOrgs = (List<String>) entity.getMetaData().get("edOrgs");
            if (edOrgs != null) {
                edOrgs.add(deterministicId);
                entity.getMetaData().put("edOrgs", edOrgs);
            } else {
                List<String> edOrg = new ArrayList<String>();
                edOrg.add(deterministicId);
                entity.getMetaData().put("edOrgs", edOrg);
            }
        }

        // check all references to potentially propagate context
        if (entityConfig.getReferences() != null) {
            for (RefDef refDef : entityConfig.getReferences()) {
                List<String> givesContextList = refDef.getRef().getGivesContext();
                if (givesContextList != null) {
                    if (isAssociationExpired(entity)) {
                        continue;
                    }

                    String referencedEntityType = refDef.getRef().getEntityType();
                    String persistedCollectionName = getPersistedCollectionName(referencedEntityType);

                    List<String> referencedIds = determineIdsToQuery(entity, refDef);

                    // if the list of referenced id's has entries
                    // --> propagate specified context to each entity
                    if (referencedIds.size() > 0) {
                        for (String typeOfContext : givesContextList) {
                            Object context = entity.getMetaData().get(typeOfContext);
                            if (context != null) {
                                updateContext(persistedCollectionName, typeOfContext, context, referencedIds);
                            }
                        }
                    }
                }
            }
        }

        // de1550: update program and cohort context
        if (isChildEducationOrganization(entity.getType())) {
            updateProgramContext(entity, deterministicId);
            updateCohortContextUsingEdOrg(entity, deterministicId);
        } else if (isCohort(entity.getType())) {
            updateCohortContextUsingCohort(entity);
        }
    }

    private boolean isEducationOrganization(String type) {
        return (type.equals("stateEducationAgency")) || (type.equals("localEducationAgency"))
                || (type.equals("school"));
    }

    private boolean isChildEducationOrganization(String type) {
        return (type.equals("localEducationAgency")) || (type.equals("school"));
    }

    private boolean isCohort(String type) {
        return type.equals("cohort");
    }

    private void updateContext(String referencedEntityType, String typeOfContext, Object context,
            List<String> idsToQuery) {
        NeutralQuery query = new NeutralQuery(idsToQuery.size());
        query.addCriteria(new NeutralCriteria("_id", NeutralCriteria.OPERATOR_EQUAL, idsToQuery, false));

        // need to use $each operator to add an array with $addToSet
        Object updateValue = context;
        if (context instanceof List) {
            Map<String, Object> eachList = new HashMap<String, Object>();
            eachList.put("$each", context);
            updateValue = eachList;
        }

        Map<String, Object> metaDataFields = new HashMap<String, Object>();
        metaDataFields.put("metaData." + typeOfContext, updateValue);
        Map<String, Object> update = new HashMap<String, Object>();
        update.put("addToSet", metaDataFields);

        entityRepository.updateMulti(query, update, referencedEntityType);
    }

    private void updateCohortContextUsingCohort(Entity entity) {
        LOG.info("updating cohort context for {}: {}", entity.getType(), entity.getBody().get("cohortIdentifier"));

        String edOrgId = (String) entity.getBody().get("educationOrgId");

        if(edOrgId == null) edOrgId =   (String) entity.getBody().get("EducationOrgReference");

        if (edOrgId != null) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("metaData.edOrgs", "=", edOrgId, false));
            Iterable<Entity> edOrgs = entityRepository.findAll("educationOrganization", query);

            if (edOrgs != null) {
                @SuppressWarnings("unchecked")
                List<String> myEdOrgs = (List<String>) entity.getMetaData().get("edOrgs");
                for (Entity edOrg : edOrgs) {
                    if (!myEdOrgs.contains(edOrg.getEntityId())) {
                        myEdOrgs.add(edOrg.getEntityId());
                    }
                }
                entity.getMetaData().put("edOrgs", myEdOrgs);
            }
        }
    }

    private void updateCohortContextUsingEdOrg(Entity entity, String deterministicId) {
        LOG.info("updating cohort context for {}: {}", entity.getType(), entity.getBody().get("stateOrganizationId"));

        if (deterministicId == null) {
            LOG.error("null edOrg deterministicId being used to update cohort context");
        }

        @SuppressWarnings("unchecked")
        List<String> edOrgIds = (List<String>) entity.getMetaData().get("edOrgs");

        if (edOrgIds != null) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("educationOrgId", "=", edOrgIds));
            Iterable<Entity> cohorts = entityRepository.findAll("cohort", query);

            if (cohorts != null) {
                List<String> cohortsToUpdate = new ArrayList<String>();
                for (Entity cohort : cohorts) {
                    String cohortId = cohort.getEntityId();
                    if (!cohortsToUpdate.contains(cohortId)) {
                        cohortsToUpdate.add(cohortId);
                    }
                }

                if (cohortsToUpdate.size() > 0) {
                    LOG.info("adding id: {} to context for cohorts with ids: {}", deterministicId, cohortsToUpdate);
                    updateContext("cohort", "edOrgs", deterministicId, cohortsToUpdate);
                } else {
                    LOG.info("found no cohorts to update for ed org: {}", deterministicId);
                }
            }
        }
    }

    private void updateProgramContext(Entity entity, String deterministicId) {
        LOG.info("updating program context for {}: {}", entity.getType(), entity.getBody().get("stateOrganizationId"));

        if (deterministicId == null) {
            LOG.error("null edOrg deterministicId being used to update program context");
        }

        @SuppressWarnings("unchecked")
        List<String> edOrgIds = (List<String>) entity.getMetaData().get("edOrgs");

        if (edOrgIds != null && edOrgIds.size() > 0) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("_id", "=", edOrgIds, false));
            Iterable<Entity> edOrgs = entityRepository.findAll("educationOrganization", query);

            if (edOrgs != null) {
                List<String> programsToUpdate = new ArrayList<String>();
                for (Entity edOrg : edOrgs) {
                    @SuppressWarnings("unchecked")
                    List<String> programIds = (List<String>) edOrg.getBody().get("programReference");
                    for (String programId : programIds) {
                        if (!programsToUpdate.contains(programId)) {
                            programsToUpdate.add(programId);
                        }
                    }
                }

                if (programsToUpdate.size() > 0) {
                    LOG.info("adding id: {} to context for programs with ids: {}", deterministicId,
                            programsToUpdate);
                    updateContext("program", "edOrgs", deterministicId, programsToUpdate);
                } else {
                    LOG.info("found no programs to update for ed org: {}", deterministicId);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<String> determineIdsToQuery(Entity entity, RefDef refDef) {
        List<String> idsToQuery = new ArrayList<String>();

        String bodyPath = refDef.getFieldPath().replaceFirst("body\\.", "");
        Object normalizedIdValue = entity.getBody().get(bodyPath);

        if (normalizedIdValue instanceof String) {
            idsToQuery.add(normalizedIdValue.toString());
        } else if (normalizedIdValue instanceof List) {
            // we don't want dupes but want as List so Mongo can handle as an array
            for (String id : (List<String>) normalizedIdValue) {
                if (!idsToQuery.contains(id)) {
                    idsToQuery.add(id);
                }
            }
        }
        return idsToQuery;
    }

    private String getPersistedCollectionName(String entityType) {
        String collectionName = "";
        NeutralSchema schema = schemaRepository.getSchema(entityType);
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                collectionName = appInfo.getCollectionType();
            }
        }
        return collectionName;
    }

    /**
     * Find a matched entity in the data store. If match is found the EntityID gets updated with the
     * ID from the data store.
     *
     * @param entity
     *            Entity to match
     * @param entityConfig
     *            Configuration for the entity
     * @param errorReport
     *            Error reporting
     */
    protected void matchEntity(SimpleEntity entity, ErrorReport errorReport) {
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        Query query = createEntityLookupQuery(entity, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        String collection = "";
        NeutralSchema schema = schemaRepository.getSchema(entity.getType());
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                collection = appInfo.getCollectionType();
            }
        }

        @SuppressWarnings("deprecation")
        Iterable<Entity> match = entityRepository.findByQuery(collection, query, 0, 0);

        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());

            @SuppressWarnings("unchecked")
            List<String> edOrgs = (List<String>) entity.getMetaData().get("edOrgs");

            if (edOrgs != null && edOrgs.size() > 0) {
                @SuppressWarnings("unchecked")
                List<String> matchedEdOrgs = (List<String>) matched.getMetaData().get("edOrgs");
                if (matchedEdOrgs != null) {
                    for (String edOrg : edOrgs) {
                        if (!matchedEdOrgs.contains(edOrg)) {
                            matchedEdOrgs.add(edOrg);
                        }
                    }
                    matched.getMetaData().put("edOrgs", matchedEdOrgs);
                }
            }
            entity.getMetaData().putAll(matched.getMetaData());
        }
    }

    /**
     * Create entity lookup query from EntityConfig fields
     *
     * @param entity
     *            : the entity to be looked up.
     * @param keyFields
     *            : the list of the fields with which to generate the filter
     * @param errorReport
     *            : error reporting
     * @return Look up filter
     *
     * @author tke
     */
    protected Query createEntityLookupQuery(SimpleEntity entity, EntityConfig entityConfig, ErrorReport errorReport) {
        Query query;

        if (NaturalKeyExtractor.useDeterministicIds()) {

            NaturalKeyDescriptor naturalKeyDescriptor;
            try {
                naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(entity);
            } catch (NaturalKeyValidationException e1) {
                String message = "An entity is missing one or more required natural key fields" + "\n"
                        + "       Entity     " + entity.getType() + "\n" + "       Instance   "
                        + entity.getRecordNumber();

                for (String fieldName : e1.getNaturalKeys()) {
                    message += "\n" + "       Field      " + fieldName;
                }
                errorReport.error(message, this);
                return null;
            } catch (NoNaturalKeysDefinedException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }

            if (naturalKeyDescriptor.isNaturalKeysNotNeeded()) {
                // Okay for embedded entities
                LOG.error("Unable to find natural keys fields" + "       Entity     " + entity.getType() + "\n"
                        + "       Instance   " + entity.getRecordNumber());

                query = createEntityLookupQueryFromKeyFields(entity, entityConfig, errorReport);
            } else {
                query = new Query();
                String entityId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);
                query.addCriteria(Criteria.where(ID).is(entityId));
            }
        } else {
            query = createEntityLookupQueryFromKeyFields(entity, entityConfig, errorReport);
        }

        return query;
    }

    protected Query createEntityLookupQueryFromKeyFields(SimpleEntity entity, EntityConfig entityConfig,
            ErrorReport errorReport) {
        Query query = new Query();

        String errorMessage = "ERROR: Invalid key fields for an entity\n";
        if (entityConfig.getKeyFields() == null || entityConfig.getKeyFields().size() == 0) {
            errorReport.fatal("Cannot find a match for an entity: No key fields specified", this);
        } else {
            errorMessage += "       Entity      " + entity.getType() + "\n" + "       Key Fields  "
                    + entityConfig.getKeyFields() + "\n";
            if (entityConfig.getReferences() != null && entityConfig.getReferences().size() > 0) {
                errorMessage += "     The following collections are referenced by the key fields:" + "\n";
                for (RefDef refDef : entityConfig.getReferences()) {
                    String collectionName = "";
                    NeutralSchema schema = schemaRepository.getSchema(refDef.getRef().getEntityType());
                    if (schema != null) {
                        AppInfo appInfo = schema.getAppInfo();
                        if (appInfo != null) {
                            collectionName = appInfo.getCollectionType();
                        }
                    }

                    errorMessage += "       collection = " + collectionName + "\n";
                }
            }
        }

        try {
            for (String field : entityConfig.getKeyFields()) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                if (fieldValue instanceof List) {
                    List fieldValues = ((List) fieldValue);
                    int size = fieldValues.size();
                    //make sure we have exactly the number of desired values
                    Criteria criteria = Criteria.where(field).size(size);
                    // if there are desired values, make sure we have each individual desired value
                    if (size > 0) {
                        Criteria[] valueCriteria = new Criteria[size];
                        for (int i = 0; i < size; i++) {
                            valueCriteria[i] = Criteria.where(field).is(fieldValues.get(i));
                        }
                        criteria = criteria.andOperator(valueCriteria);
                    }
                    query.addCriteria(criteria);
                    //this will be insufficient if fieldValue can contain duplicates
                } else {
                    query.addCriteria(Criteria.where(field).is(fieldValue));
                }
            }
            ComplexKeyField complexField = entityConfig.getComplexKeyField();
            if (complexField != null) {
                String propertyString = complexField.getListPath() + ".[0]." + complexField.getFieldPath();
                Object fieldValue = PropertyUtils.getProperty(entity, propertyString);

                query.addCriteria(Criteria.where(complexField.getListPath() + "." + complexField.getFieldPath()).is(
                        fieldValue));
            }
        } catch (Exception e) {
            errorReport.error(errorMessage, this);
        }

        return query;
    }

    protected abstract List<SimpleEntity> transform(NeutralRecord item, ErrorReport errorReport);

    public IdNormalizer getIdNormalizer() {
        return idNormalizer;
    }

    public void setIdNormalizer(IdNormalizer idNormalizer) {
        this.idNormalizer = idNormalizer;
    }

    public DeterministicIdResolver getDidResolver() {
        return didResolver;
    }

    public void setDidResolver(DeterministicIdResolver didResolver) {
        this.didResolver = didResolver;
    }

    public EntityConfigFactory getEntityConfigurations() {
        return entityConfigurations;
    }

    public void setEntityConfigurations(EntityConfigFactory entityConfigurations) {
        this.entityConfigurations = entityConfigurations;
    }

    public Repository<Entity> getEntityRepository() {
        return entityRepository;
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }
}
