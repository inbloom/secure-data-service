package org.slc.sli.ingestion.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.validation.EntityValidationException;
import org.slc.sli.validation.ValidationError;

import org.apache.commons.lang3.text.WordUtils;

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

    private Repository<Entity> entityRepository;

    private MessageSource messageSource;

    @Override
    Entity doHandling(SimpleEntity entity, ErrorReport errorReport) {
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
}
