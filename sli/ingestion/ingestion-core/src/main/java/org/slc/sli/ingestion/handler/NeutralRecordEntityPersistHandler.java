package org.slc.sli.ingestion.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.util.InternalIdNormalizer;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

/**
 * Handles the persisting of Entity objects
 *
 * @author dduran
 *         Modified by Thomas Shewchuk (PI3 US811)
 *         - 2/1/2010 Added record DB lookup and update capabilities, and support for association
 *         entities.
 *
 */
public class NeutralRecordEntityPersistHandler extends AbstractIngestionHandler<NeutralRecordEntity, Entity> implements
        InitializingBean {

    private static final String METADATA_BLOCK = "metaData";

    private Repository<Entity> entityRepository;

    private MessageSource messageSource;

    @Autowired
    private InternalIdNormalizer internalIdNormalizer;

    @Value("${sli.ingestion.staging.mongotemplate.writeConcern}")
    private String writeConcern;

    @Override
    public void afterPropertiesSet() throws Exception {
        entityRepository.setWriteConcern(writeConcern);
    }

    Entity doHandling(NeutralRecordEntity entity, ErrorReport errorReport) {
        return doHandling(entity, errorReport, null);
    }

    @Override
    protected Entity doHandling(NeutralRecordEntity entity, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        matchEntity(entity, errorReport);

        if (errorReport.hasErrors()) {
            return null;
        }

        try {
            return persist(entity);
        } catch (EntityValidationException ex) {
            reportErrors(ex.getValidationErrors(), entity, errorReport);
        } catch (DuplicateKeyException ex) {
            reportErrors(ex.getRootCause().getMessage(), entity, errorReport);
        }
        return null;
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
    private Entity persist(Entity entity) throws EntityValidationException {

        String collectionName = entity.getType();
        if ((String) entity.getBody().get("collectionName") != null) {
            collectionName = (String) entity.getBody().get("collectionName");
            entity.getBody().remove("collectionName");
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

    private void reportErrors(List<ValidationError> errors, NeutralRecordEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {

            String message = "ERROR: There has been a data validation error when saving an entity" + "\n"
                    + "       Error      " + err.getType().name() + "\n" + "       Entity     " + entity.getType()
                    + "\n" + "       Instance   " + entity.getRecordNumberInFile() + "\n" + "       Field      "
                    + err.getFieldName() + "\n" + "       Value      " + err.getFieldValue() + "\n"
                    + "       Expected   " + Arrays.toString(err.getExpectedTypes()) + "\n";
            errorReport.error(message, this);
        }
    }

    /**
     * Generic error reporting function.
     *
     * @param errorMessage
     *            Error message reported by entity.
     * @param entity
     *            Entity reporting error.
     * @param errorReport
     *            Reference to error report to log error message in.
     */
    private void reportErrors(String errorMessage, NeutralRecordEntity entity, ErrorReport errorReport) {
        String assembledMessage = MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_ERR_MSG1",
                entity.getType(), errorMessage);
        errorReport.error(assembledMessage, this);
    }

    /**
     * Resolve references defined by external IDs.
     *
     * @param entity
     *            Entity which has references that need to be resolved
     * @param errorReport
     *            Error reporting
     */
    @SuppressWarnings("unchecked")
    public void resolveInternalIds(NeutralRecordEntity entity, ErrorReport errorReport) {
        for (Map.Entry<String, Object> externalIdEntry : entity.getLocalParentIds().entrySet()) {

            // TODO change all smooks mappings to use "collection#fieldName" naming convention
            // get the collection name and from the key name in one of two naming conventions
            String collection;
            String fieldName;

            if (externalIdEntry.getKey().contains("#")) {
                try {
                    String[] keys = externalIdEntry.getKey().split("#");
                    collection = keys[0];
                    fieldName = keys[1];
                } catch (Exception e) {
                    errorReport.error(
                            MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_ERR_MSG2",
                                    externalIdEntry.getKey()), this);
                    break;
                }
            } else {
                collection = WordUtils.uncapitalize(externalIdEntry.getKey());
                fieldName = collection + "Id";
            }
            String tenantId = entity.getMetaData().get(EntityMetadataKey.TENANT_ID.getKey()).toString();

            Object internalId = "";

            // Allows a reference to be configured as a String, a Map of search criteria, or a list
            // of such Maps,
            // used to make the transition to search criteria smoother
            if (Map.class.isInstance(externalIdEntry.getValue())) {

                Map<?, ?> externalSearchCriteria = (Map<?, ?>) externalIdEntry.getValue();
                internalId = internalIdNormalizer.resolveInternalId(entityRepository, collection, tenantId,
                        externalSearchCriteria, errorReport);

            } else if (List.class.isInstance(externalIdEntry.getValue())) {
                List<?> referenceList = (List<?>) externalIdEntry.getValue();
                internalId = new ArrayList<String>();
                for (Object reference : referenceList) {
                    if (Map.class.isInstance(reference)) {
                        Map<?, ?> externalSearchCriteria = (Map<?, ?>) reference;
                        String id = internalIdNormalizer.resolveInternalId(entityRepository, collection, tenantId,
                                externalSearchCriteria, errorReport);
                        ((List<String>) internalId).add(id);
                    } else {
                        String externalId = reference.toString();
                        internalId = internalIdNormalizer.resolveInternalId(entityRepository, collection, tenantId, externalId,
                                errorReport);
                    }
                }
            } else {

                String externalId = externalIdEntry.getValue().toString();
                internalId = internalIdNormalizer.resolveInternalId(entityRepository, collection, tenantId, externalId,
                        errorReport);
            }

            if (errorReport.hasErrors()) {
                // Stop processing.
                break;
            }
            entity.setAttributeField(fieldName, internalId);
        }
    }

    /**
     * Find a matched entity in the data store. If match is found the EntityID gets updated with the
     * ID from the data store.
     *
     * @param entity
     *            Entity to match
     * @param errorReport
     *            Error reporting
     */
    public void matchEntity(NeutralRecordEntity entity, ErrorReport errorReport) {
        resolveInternalIds(entity, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Map<String, String> matchFilter = createEntityLookupFilter(entity, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        // only true if no local id was supplied (and association is false)
        if (matchFilter.isEmpty()) {
            return;
        }

        String collectionName = entity.getType();

        if (collectionName.equals("teacher")) {
            collectionName = "staff";
        } else if (collectionName.equals("school")) {
            collectionName = "educationOrganization";
        }

        Iterable<Entity> match = entityRepository.findAllByPaths(collectionName, matchFilter, new NeutralQuery());
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
            Map<String, Object> metadataMap = matched.getMetaData();
            for (Map.Entry<String, Object> metadataEntry : metadataMap.entrySet()) {
                entity.setMetaDataField(metadataEntry.getKey(), metadataEntry.getValue());
            }
        }
    }

    /**
     * Create look-up filter to find a matched entity in the data store.
     *
     * @param entity
     *            Entity to match in the data store
     * @param errorReport
     *            Error Reporting
     * @return Look-up filter
     */
    public Map<String, String> createEntityLookupFilter(NeutralRecordEntity entity, ErrorReport errorReport) {
        String tenantId = entity.getMetaData().get(EntityMetadataKey.TENANT_ID.getKey()).toString();

        Map<String, String> filter = new HashMap<String, String>();
        filter.put(METADATA_BLOCK + "." + EntityMetadataKey.TENANT_ID.getKey(), tenantId);

        if (entity.isAssociation()) {
            // Lookup each associated entity in the data store.
            for (Map.Entry<String, Object> externalReference : entity.getLocalParentIds().entrySet()) {
                String referencedCollection = WordUtils.uncapitalize(externalReference.getKey());
                String referencedId = referencedCollection + "Id";

                if (referencedCollection.contains("#")) {
                    referencedId = referencedCollection.substring(referencedCollection.indexOf("#") + 1);
                }

                filter.put("body." + referencedId, entity.getBody().get(referencedId).toString());
            }
        } else {
            if (entity.getLocalId() != null) {
                entity.setMetaDataField(EntityMetadataKey.EXTERNAL_ID.getKey(), entity.getLocalId());
                filter.put(METADATA_BLOCK + "." + EntityMetadataKey.EXTERNAL_ID.getKey(), entity.getLocalId()
                        .toString());
            } else {
                filter.clear();
            }
        }

        return filter;
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

    /**
     * Create entity lookup filter by fields
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
    public Map<String, String> createEntityLookupFilter(Entity entity, List<String> keyFields, ErrorReport errorReport) {
        Map<String, String> filter = new HashMap<String, String>();

        try {
            for (String field : keyFields) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                filter.put(field, (String) fieldValue);
            }
        } catch (Exception e) {
            errorReport.error(MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_ERR_MSG3"), this);
        }

        return filter;
    }

}
