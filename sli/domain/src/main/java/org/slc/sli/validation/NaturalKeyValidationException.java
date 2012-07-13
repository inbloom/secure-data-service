package org.slc.sli.validation;

/**
 * Exception for Natural Key Validations
 *
 * @author srupasinghe
 */
public class NaturalKeyValidationException extends RuntimeException {

    final String entityType;

    public NaturalKeyValidationException(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }
}
