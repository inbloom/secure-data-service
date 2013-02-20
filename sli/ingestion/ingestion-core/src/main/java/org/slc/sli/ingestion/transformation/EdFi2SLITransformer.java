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

package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.BatchJobStage;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.handler.Handler;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;
import org.slc.sli.ingestion.transformation.normalization.ComplexKeyField;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.RefDef;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.validation.NaturalKeyValidationException;
import org.slc.sli.validation.NoNaturalKeysDefinedException;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.INaturalKeyExtractor;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * EdFi to SLI data transformation
 *
 * @author okrook
 *
 */
public abstract class EdFi2SLITransformer implements Handler<NeutralRecord, List<SimpleEntity>>, BatchJobStage {

    private static final Logger LOG = LoggerFactory.getLogger(EdFi2SLITransformer.class);

    protected static final String METADATA_BLOCK = "metaData";

    protected static final String ID = "_id";

    private DeterministicIdResolver dIdResolver;

    private EntityConfigFactory entityConfigurations;

    private Repository<Entity> entityRepository;

    private String batchJobId;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private INaturalKeyExtractor naturalKeyExtractor;

    @Autowired
    private DeterministicUUIDGeneratorStrategy deterministicUUIDGeneratorStrategy;

    @Override
    public List<SimpleEntity> handle(NeutralRecord item, AbstractMessageReport report, ReportStats reportStats) {

        resolveReferences(item, report, reportStats);

        if (reportStats.hasErrors()) {
            LOG.info("Issue was detected in EdFi2SLITransformer.resolveReferences()");
            return Collections.emptyList();
        }

        List<SimpleEntity> transformed = transform(item, report, reportStats);

        if (reportStats.hasErrors()) {
            LOG.info("Issue was detected in EdFi2SLITransformer.transform()");
            return Collections.emptyList();
        }

        if (transformed != null && !transformed.isEmpty()) {

            for (SimpleEntity entity : transformed) {

                if (entity.getMetaData() == null) {
                    entity.setMetaData(new HashMap<String, Object>());
                }

                try {
                    matchEntity(entity, report, reportStats);
                } catch (DataAccessResourceFailureException darfe) {
                    LOG.error("Exception in matchEntity", darfe);
                    report.error(reportStats, new ElementSourceImpl(item), CoreMessageCode.CORE_0046, darfe);
                }

                if (reportStats.hasErrors()) {
                    return Collections.emptyList();
                }
            }
        } else {
            LOG.error("EdFi2SLI Transform has resulted in either a null or empty list of transformed SimpleEntities.");
            report.error(reportStats, new ElementSourceImpl(item), CoreMessageCode.CORE_0047);
        }

        return transformed;
    }

    protected void resolveReferences(NeutralRecord item, AbstractMessageReport report, ReportStats reportStats) {
        NeutralRecordEntity entity = new NeutralRecordEntity(item);
        dIdResolver.resolveInternalIds(entity, item.getSourceId(), report, reportStats);
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
    protected void matchEntity(SimpleEntity entity, AbstractMessageReport report, ReportStats reportStats) {
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        Query query = createEntityLookupQuery(entity, entityConfig, report, reportStats);

        if (reportStats.hasErrors()) {
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
    protected Query createEntityLookupQuery(SimpleEntity entity, EntityConfig entityConfig,
            AbstractMessageReport report, ReportStats reportStats) {
        Query query;

        NaturalKeyDescriptor naturalKeyDescriptor;
        try {
            naturalKeyDescriptor = naturalKeyExtractor.getNaturalKeyDescriptor(entity);
        } catch (NaturalKeyValidationException e1) {
            StringBuilder message = new StringBuilder("");

            for (String fieldName : e1.getNaturalKeys()) {
                message.append("\n" + "       Field      " + fieldName);
            }
            report.error(reportStats, new ElementSourceImpl(entity), CoreMessageCode.CORE_0010, entity.getType(),
                    Long.toString(entity.getRecordNumber()), message.toString());
            return null;
        } catch (NoNaturalKeysDefinedException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

        if (naturalKeyDescriptor.isNaturalKeysNotNeeded()) {
            // Okay for embedded entities
            LOG.error("Unable to find natural key fields" + "       Entity     " + entity.getType() + "\n"
                    + "       Instance   " + entity.getRecordNumber());

            query = createEntityLookupQueryFromKeyFields(entity, entityConfig, report, reportStats);
        } else {
            query = new Query();
            String entityId = deterministicUUIDGeneratorStrategy.generateId(naturalKeyDescriptor);
            query.addCriteria(Criteria.where(ID).is(entityId));
        }

        return query;
    }

    protected Query createEntityLookupQueryFromKeyFields(SimpleEntity entity, EntityConfig entityConfig,
            AbstractMessageReport report, ReportStats reportStats) {
        Query query = new Query();

        StringBuilder errorMessage = new StringBuilder("");
        if (entityConfig.getKeyFields() == null || entityConfig.getKeyFields().size() == 0) {
            report.error(reportStats, new ElementSourceImpl(entity), CoreMessageCode.CORE_0011);
        } else {
            errorMessage.append("       Entity      " + entity.getType() + "\n" + "       Key Fields  "
                    + entityConfig.getKeyFields() + "\n");
            if (entityConfig.getReferences() != null && entityConfig.getReferences().size() > 0) {
                errorMessage.append("     The following collections are referenced by the key fields:" + "\n");
                for (RefDef refDef : entityConfig.getReferences()) {
                    String collectionName = "";
                    NeutralSchema schema = schemaRepository.getSchema(refDef.getRef().getEntityType());
                    if (schema != null) {
                        AppInfo appInfo = schema.getAppInfo();
                        if (appInfo != null) {
                            collectionName = appInfo.getCollectionType();
                        }
                    }

                    errorMessage.append("       collection = " + collectionName + "\n");
                }
            }
        }

        try {
            for (String field : entityConfig.getKeyFields()) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                if (fieldValue instanceof List) {
                    @SuppressWarnings("rawtypes")
                    List fieldValues = ((List) fieldValue);
                    int size = fieldValues.size();
                    // make sure we have exactly the number of desired values
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
                    // this will be insufficient if fieldValue can contain duplicates
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
            report.error(reportStats, new ElementSourceImpl(entity), CoreMessageCode.CORE_0012, errorMessage.toString());
        }

        return query;
    }

    protected abstract List<SimpleEntity> transform(NeutralRecord item, AbstractMessageReport report,
            ReportStats reportStats);

    public void setDIdResolver(DeterministicIdResolver dIdResolver) {
        this.dIdResolver = dIdResolver;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
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
