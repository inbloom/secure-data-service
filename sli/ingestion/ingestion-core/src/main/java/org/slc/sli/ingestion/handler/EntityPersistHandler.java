package org.slc.sli.ingestion.handler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.transformation.SimpleEntity;
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
public class EntityPersistHandler extends AbstractIngestionHandler<SimpleEntity, Entity> implements InitializingBean {
    
    private Repository<Entity> entityRepository;
    
    private MessageSource messageSource;
    
    @Value("${sli.ingestion.mongotemplate.writeConcern}")
    private String writeConcern;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        entityRepository.setWriteConcern(writeConcern);
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
        
        String oldCollectionName = entity.getType();
        String newCollectionName = oldCollectionName;
        if ((String) entity.getBody().get("collectionName") != null) {
            newCollectionName = (String) entity.getBody().get("collectionName");
            entity.getBody().remove("collectionName");
            
            if (oldCollectionName.equals("stateEducationAgency") || oldCollectionName.equals("localEducationAgency")) {
                entity.setType(newCollectionName);
            }
        }
        
        if (entity.getEntityId() != null) {
            
            if (!entityRepository.update(newCollectionName, entity)) {
                // TODO: exception should be replace with some logic.
                throw new RuntimeException("Record was not updated properly.");
            }
            
            return entity;
        } else {
            return entityRepository.create(entity.getType(), entity.getBody(), entity.getMetaData(), newCollectionName);
        }
    }
    
    private void reportErrors(List<ValidationError> errors, SimpleEntity entity, ErrorReport errorReport) {
        for (ValidationError err : errors) {
            
            String message = "ERROR: There has been a data validation error when saving an entity" + "\n"
                    + "       Error      " + err.getType().name() + "\n" + "       Entity     " + entity.getType()
                    + "\n" + "       Field      " + err.getFieldName() + "\n" + "       Value      "
                    + err.getFieldValue() + "\n" + "       Expected   " + Arrays.toString(err.getExpectedTypes())
                    + "\n";
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
}
