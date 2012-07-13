package org.slc.sli.validation;

import java.util.List;

/**
 * Exception for Natural Key Validations
 *
 * @author srupasinghe
 */
public class NaturalKeyValidationException extends RuntimeException {

    final String entityType;
    final List<String> naturalKeys;

    public NaturalKeyValidationException(String entityType, List<String> naturalKeys) {
        this.entityType = entityType;
        this.naturalKeys = naturalKeys;
    }

    public String getEntityType() {
        return entityType;
    }

    public List<String> getNaturalKeys() {
        return this.naturalKeys;
    }
}
