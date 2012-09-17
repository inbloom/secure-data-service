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

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
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
import org.slc.sli.ingestion.validation.DummyErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
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

    private IdNormalizer idNormalizer;

    private EntityConfigFactory entityConfigurations;

    private Repository<Entity> entityRepository;

    @Value("${sli.security.gracePeriod}")
    private String gracePeriod;

    @Autowired
    private SchemaRepository schemaRepository;

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

                entity.getMetaData().put(EntityMetadataKey.TENANT_ID.getKey(), item.getSourceId());

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

        idNormalizer.resolveInternalIds(entity, item.getSourceId(), entityConfig, errorReport);

        // propagate context according to configuration
        giveContext(entity, entityConfig);
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
        if (entity.getType().equals("studentSchoolAssociation") && entity.getBody().containsKey("exitWithdrawDate")) {
            try {
                DateTime exitWithdrawDate = DateTime.parse((String) entity.getBody().get("exitWithdrawDate"));
                if (exitWithdrawDate.isBefore(DateTime.now().minusDays(Integer.valueOf(gracePeriod)))) {
                    expired = true;
                }
            } catch (Exception e) {
                LOG.warn(
                        "Error parsing exitWithdrawDate for student: {} at school: {} --> continuing as if date was absent.",
                        new Object[] { entity.getBody().get("studentId"), entity.getBody().get("schoolId") });
            }
        }
        return expired;
    }

    public void giveContext(Entity entity, EntityConfig entityConfig) {
        ComplexRefDef complexRefDef = entityConfig.getComplexReference();
        if (complexRefDef != null) {
            // what is this?
            // -> thinking we'll need this for course lookups
        }

        if (isEducationOrganization(entity.getType())) {
            @SuppressWarnings("unchecked")
            List<String> edOrgs = (List<String>) entity.getMetaData().get("edOrgs");
            if (edOrgs != null) {
                edOrgs.add(entity.getEntityId());
                entity.getMetaData().put("edOrgs", edOrgs);
            } else {
                List<String> edOrg = new ArrayList<String>();
                edOrg.add(entity.getEntityId());
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
            updateProgramContext(entity);
            updateCohortContextUsingEdOrg(entity);
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
        query.addCriteria(new NeutralCriteria("metaData.tenantId", NeutralCriteria.OPERATOR_EQUAL, TenantContext
                .getTenantId(), false));
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

        if (edOrgId != null) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", TenantContext.getTenantId(), false));
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

    private void updateCohortContextUsingEdOrg(Entity entity) {
        LOG.info("updating cohort context for {}: {}", entity.getType(), entity.getBody().get("stateOrganizationId"));

        @SuppressWarnings("unchecked")
        List<String> edOrgIds = (List<String>) entity.getMetaData().get("edOrgs");

        if (edOrgIds != null) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", TenantContext.getTenantId(), false));
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
                    LOG.info("adding id: {} to context for cohorts with ids: {}", entity.getEntityId(),
                            cohortsToUpdate);
                    updateContext("cohort", "edOrgs", entity.getEntityId(), cohortsToUpdate);
                } else {
                    LOG.info("found no cohorts to update for ed org: {}", entity.getEntityId());
                }
            }
        }
    }

    private void updateProgramContext(Entity entity) {
        LOG.info("updating program context for {}: {}", entity.getType(), entity.getBody().get("stateOrganizationId"));

        @SuppressWarnings("unchecked")
        List<String> edOrgIds = (List<String>) entity.getMetaData().get("edOrgs");

        if (edOrgIds != null && edOrgIds.size() > 0) {
            NeutralQuery query = new NeutralQuery(0);
            query.addCriteria(new NeutralCriteria("metaData.tenantId", "=", TenantContext.getTenantId(), false));
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
                    LOG.info("adding id: {} to context for programs with ids: {}", entity.getEntityId(),
                            programsToUpdate);
                    updateContext("program", "edOrgs", entity.getEntityId(), programsToUpdate);
                } else {
                    LOG.info("found no programs to update for ed org: {}", entity.getEntityId());
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

        String tenantId = entity.getMetaData().get(EntityMetadataKey.TENANT_ID.getKey()).toString();
        query.addCriteria(Criteria.where(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey()).is(tenantId));

        try {
            for (String field : entityConfig.getKeyFields()) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                if (fieldValue instanceof List) {
                    int size = ((List) fieldValue).size();
                    //make sure we have exactly the number of desired values
                    query.addCriteria(Criteria.where(field).size(size));
                    //make sure we have each individual desired value
                    for (Object val : (List) fieldValue) {
                        query.addCriteria(Criteria.where(field).is(val));
                    }
                    //this will be insufficient if fieldValue can contain duplicates
                } else {
                    query.addCriteria(Criteria.where(field).is(fieldValue));
                }
            }
            ComplexKeyField complexField = entityConfig.getComplexKeyField();
            if (complexField !=null) {
                String propertyString = complexField.getListPath() + ".[0]." + complexField.getFieldPath();
                Object fieldValue = PropertyUtils.getProperty(entity, propertyString);

                query.addCriteria(Criteria.where(complexField.getListPath() + "." +complexField.getFieldPath()).is(fieldValue));
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
