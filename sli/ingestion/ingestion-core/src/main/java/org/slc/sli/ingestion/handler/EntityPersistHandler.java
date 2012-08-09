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


package org.slc.sli.ingestion.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;

import org.slc.sli.common.util.datetime.DateTimeUtil;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.EntityValidator;
import org.slc.sli.validation.SchemaRepository;
import org.slc.sli.validation.ValidationError;
import org.slc.sli.validation.schema.AppInfo;
import org.slc.sli.validation.schema.NeutralSchema;

/**
 * Handles the persisting of Entity objects
 *
 * @author dduran
 *         Modified by Thomas Shewchuk (PI3 US811)
 *         - 2/1/2010 Added record DB lookup and update capabilities, and support for association
 *         entities.
 *
 */
public class EntityPersistHandler extends AbstractIngestionHandler<SimpleEntity, Entity> implements InitializingBean {

    private Repository<Entity> entityRepository;
    private EntityConfigFactory entityConfigurations;
    private MessageSource messageSource;

    @Value("${sli.ingestion.mongotemplate.writeConcern}")
    private String writeConcern;

    @Value("${sli.ingestion.referenceSchema.referenceCheckEnabled}")
    private String referenceCheckEnabled;

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private EntityValidator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        entityRepository.setWriteConcern(writeConcern);
        entityRepository.setReferenceCheck(referenceCheckEnabled);
    }

    Entity doHandling(SimpleEntity entity, ErrorReport errorReport) {
        return doHandling(entity, errorReport, null);
    }

    @Override
    protected Entity doHandling(SimpleEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        try {
            return persist(entity);
        } catch (EntityValidationException ex) {
            reportErrors(ex.getValidationErrors(), entity, errorReport);
        } catch (DuplicateKeyException ex) {
            reportWarnings(ex.getRootCause().getMessage(), entity.getType(), errorReport);
        }
        return null;
    }

    @Override
    protected List<Entity> doHandling(List<SimpleEntity> entities, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        return persist(entities, errorReport);
    }

    /**
     * Persist entity in the data store.
     *
     * @param entity
     *            Entity to be persisted
     * @return Persisted entity
     * @throws EntityValidationException
     *             Validation Exception
     */
    private Entity persist(SimpleEntity entity) throws EntityValidationException {

        String collectionName = "";
        NeutralSchema schema = schemaRepository.getSchema(entity.getType());
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                collectionName = appInfo.getCollectionType();
            }
        }

        if (entity.getEntityId() != null) {

            if (!entityRepository.update(collectionName, entity)) {
                // TODO: exception should be replace with some logic.
                throw new RuntimeException("Record was not updated properly.");
            }

            return entity;
        } else {
            return entityRepository.create(entity.getType(), entity.getBody(), entity.getMetaData(), collectionName);
        }
    }

    private List<Entity> persist(List<SimpleEntity> entities, ErrorReport errorReport) {
        List<Entity> failed = new ArrayList<Entity>();
        List<Entity> queued = new ArrayList<Entity>();
        Map<List<Object>, SimpleEntity> memory = new HashMap<List<Object>, SimpleEntity>();
        String collectionName = getCollectionName(entities.get(0));
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entities.get(0).getType());

        for (SimpleEntity entity : entities) {
            if (entity.getEntityId() != null) {
                if (!entityRepository.update(collectionName, entity)) {
                    failed.add(entity);
                }
            } else {
                preMatchEntity(memory, entityConfig, errorReport, entity);
            }
        }

        for (Map.Entry<List<Object>, SimpleEntity> entry : memory.entrySet()) {
            SimpleEntity entity = entry.getValue();
            try {
                validator.validate(entity);
                addTimestamps(entity);
                queued.add(entity);
            } catch (EntityValidationException e) {
                reportErrors(e.getValidationErrors(), entity, errorReport);
                failed.add(entity);
            }
        }

        try {
            entityRepository.insert(queued, collectionName);
        } catch (DuplicateKeyException e) {
            reportWarnings(e.getRootCause().getMessage(), collectionName, errorReport);
        }

        return failed;
    }

    private void preMatchEntity(Map<List<Object>, SimpleEntity> memory, EntityConfig entityConfig, ErrorReport errorReport, SimpleEntity entity) {
        List<String> keyFields = entityConfig.getKeyFields();
        if (keyFields.size() > 0) {
            List<Object> keyValues = new ArrayList<Object>();
            for (String field : keyFields) {
                try {
                    keyValues.add(PropertyUtils.getProperty(entity, field));
                } catch (Exception e) {
                    String errorMessage = "Issue finding key field: " + field + " for entity of type: " + entity.getType() + "\n";
                    errorReport.error(errorMessage, this);
                }
            }
            memory.put(keyValues, entity);
        }
    }

    private String getCollectionName(Entity entity) {
        String collectionName = null;
        NeutralSchema schema = schemaRepository.getSchema(entity.getType());
        if (schema != null) {
            AppInfo appInfo = schema.getAppInfo();
            if (appInfo != null) {
                collectionName = appInfo.getCollectionType();
            }
        }
        return collectionName;
    }

    private void reportErrors(List<ValidationError> errors, SimpleEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {

            String message = "ERROR: There has been a data validation error when saving an entity" + "\n"
                    + "       Error      " + err.getType().name() + "\n" + "       Entity     " + entity.getType()
                    + "\n"  + "       Instance   " + entity.getRecordNumber()
                    + "\n" + "       Field      " + err.getFieldName() + "\n" + "       Value      "
                    + err.getFieldValue() + "\n" + "       Expected   " + Arrays.toString(err.getExpectedTypes())
                    + "\n";
            errorReport.error(message, this);
        }
    }

    /**
     * Generic warning reporting function.
     *
     * @param warningMessage
     *            Warning message reported by entity.
     * @param entity
     *            Entity reporting warning.
     * @param errorReport
     *            Reference to error report to log warning message in.
     */
    private void reportWarnings(String warningMessage, String type, ErrorReport errorReport) {
        String assembledMessage = "Entity (" + type + ") reports warning: " + warningMessage;
        errorReport.warning(assembledMessage, this);
    }

    protected String getFailureMessage(String code, Object... args) {
        return MessageSourceHelper.getMessage(messageSource, code, args);
    }

    public void setEntityRepository(Repository<Entity> entityRepository) {
        this.entityRepository = entityRepository;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public EntityConfigFactory getEntityConfigurations() {
        return entityConfigurations;
    }

    public void setEntityConfigurations(EntityConfigFactory entityConfigurations) {
        this.entityConfigurations = entityConfigurations;
    }

    private void addTimestamps(Entity entity) {
        Date now = DateTimeUtil.getNowInUTC();
        entity.getMetaData().put("created", now);
        entity.getMetaData().put("updated", now);
    }
}
