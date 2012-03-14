package org.slc.sli.ingestion.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.util.IdNormalizer;
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
public class NentralRecordEntityPersistHandler extends AbstractIngestionHandler<NeutralRecordEntity, Entity> {

    // private static final Logger LOG = LoggerFactory.getLogger(EntityPersistHandler.class);

    private static final String METADATA_BLOCK = "metaData";

    // Hard-code region ID here for now, until it is set for real!
    private static final String REGION_ID = "https://devapp1.slidev.org:443/sp";

    private Repository<Entity> entityRepository;

    private MessageSource messageSource;

    @Override
    Entity doHandling(NeutralRecordEntity entity, ErrorReport errorReport) {

        // Okay, so for now, we're hard-coding the region into the meta data!
        entity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);

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
        if (entity.getEntityId() != null) {

            if (!entityRepository.update(entity.getType(), entity)) {
                // TODO: exception should be replace with some logic.
                throw new RuntimeException("Record was not updated properly.");
            }

            return entity;
        } else {
            return entityRepository.create(entity.getType(), entity.getBody(), entity.getMetaData(), entity.getType());
        }
    }

    private void reportErrors(List<ValidationError> errors, NeutralRecordEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {
            String message = getFailureMessage("DAL_" + err.getType().name(), entity.getType(),
                    entity.getRecordNumberInFile(), err.getFieldName(), err.getFieldValue(),
                    Arrays.toString(err.getExpectedTypes()));
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
        String assembledMessage = "Entity (" + entity.getType() + ") reports failure: " + errorMessage;
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
                    errorReport.error("Invalid localParentId key [" + externalIdEntry.getKey() + "]", this);
                    break;
                }
            } else {
                collection = externalIdEntry.getKey().toLowerCase();
                fieldName = collection + "Id";
            }
            String idNamespace = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();

            String internalId = "";

            // Allows a reference to be configured as a String or a Map of search criteria, used to
            // make the transition to search criteria smoother
            if (Map.class.isInstance(externalIdEntry.getValue())) {

                Map<?, ?> externalSearchCriteria = (Map<?, ?>) externalIdEntry.getValue();
                internalId = IdNormalizer.resolveInternalId(entityRepository, collection, idNamespace,
                        externalSearchCriteria, errorReport);

            } else {

                String externalId = externalIdEntry.getValue().toString();
                internalId = IdNormalizer.resolveInternalId(entityRepository, collection, idNamespace, externalId,
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

        Iterable<Entity> match = entityRepository.findByPaths(entity.getType(), matchFilter);
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
        String regionId = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();

        Map<String, String> filter = new HashMap<String, String>();
        filter.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), regionId);

        if (entity.isAssociation()) {
            // Lookup each associated entity in the data store.
            for (Map.Entry<String, Object> externalReference : entity.getLocalParentIds().entrySet()) {
                String referencedCollection = externalReference.getKey().toLowerCase();
                String referencedId = referencedCollection + "Id";

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
        return messageSource.getMessage(code, args, "#?" + code + "?#", null);
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
     * @param entity : the entity to be looked up.
     * @param keyFields : the list of the fields with which to generate the filter
     * @param errorReport: error reporting
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
            errorReport.error("Invalid key fields", this);
        }

        return filter;
    }

}
