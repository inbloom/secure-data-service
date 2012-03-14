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
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.normalization.EntityConfig;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.IdNormalizer;
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
public class EntityPersistHandler extends AbstractIngestionHandler<SimpleEntity, Entity> {

    // private static final Logger LOG = LoggerFactory.getLogger(EntityPersistHandler.class);

    private static final String METADATA_BLOCK = "metaData";

    // Hard-code region ID here for now, until it is set for real!
    private static final String REGION_ID = "SLI";

    private Repository<Entity> entityRepository;

    private MessageSource messageSource;

    private EntityConfigFactory entityConfigurations;

    private IdNormalizer idNormalizer;

    @Override
    Entity doHandling(SimpleEntity entity, ErrorReport errorReport) {
        entity.getMetaData().put(EntityMetadataKey.ID_NAMESPACE.getKey(), REGION_ID);

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
    private Entity persist(SimpleEntity entity) throws EntityValidationException {
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

    private void reportErrors(List<ValidationError> errors, SimpleEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {
            String message = getFailureMessage("DAL_" + err.getType().name(), entity.getType(),
                    entity.getRecordNumber(), err.getFieldName(), err.getFieldValue(),
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
    private void reportErrors(String errorMessage, SimpleEntity entity, ErrorReport errorReport) {
        String assembledMessage = "Entity (" + entity.getType() + ") reports failure: " + errorMessage;
        errorReport.error(assembledMessage, this);
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
    public void matchEntity(SimpleEntity entity, ErrorReport errorReport) {
        EntityConfig entityConfig = entityConfigurations.getEntityConfiguration(entity.getType());

        String regionId = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();

        idNormalizer.resolveInternalIds(entity, regionId, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Map<String, String> matchFilter = createEntityLookupFilter(entity, entityConfig, errorReport);

        if (errorReport.hasErrors()) {
            return;
        }

        Iterable<Entity> match = entityRepository.findByPaths(entity.getType(), matchFilter);
        if (match != null && match.iterator().hasNext()) {
            // Entity exists in data store.
            Entity matched = match.iterator().next();
            entity.setEntityId(matched.getEntityId());
            entity.getMetaData().putAll(matched.getMetaData());
        }
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
    public Map<String, String> createEntityLookupFilter(SimpleEntity entity, EntityConfig entityConfig, ErrorReport errorReport) {
        Map<String, String> filter = new HashMap<String, String>();

        if (entityConfig.getKeyFields() == null || entityConfig.getKeyFields().size() == 0) {
            errorReport.fatal("Cannot find a match for an entity: No key fields specified", this);
        }

        String regionId = entity.getMetaData().get(EntityMetadataKey.ID_NAMESPACE.getKey()).toString();
        filter.put(METADATA_BLOCK + "." + EntityMetadataKey.ID_NAMESPACE.getKey(), regionId);

        try {
            for (String field : entityConfig.getKeyFields()) {
                Object fieldValue = PropertyUtils.getProperty(entity, field);
                filter.put(field, (String) fieldValue);
            }
        } catch (Exception e) {
            errorReport.error("Invalid key fields", this);
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

    public EntityConfigFactory getEntityConfigurations() {
        return entityConfigurations;
    }

    public void setEntityConfigurations(EntityConfigFactory entityConfigurations) {
        this.entityConfigurations = entityConfigurations;
    }

    public IdNormalizer getIdNormalizer() {
        return idNormalizer;
    }

    public void setIdNormalizer(IdNormalizer idNormalizer) {
        this.idNormalizer = idNormalizer;
    }

}
