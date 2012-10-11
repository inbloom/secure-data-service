package org.slc.sli.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception for Natural Key Validations
 *
 * @author srupasinghe
 */
public class NaturalKeyValidationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    final String entityType;
    final List<String> naturalKeys;

    public NaturalKeyValidationException(Exception ex) {
        super(ex);
        this.naturalKeys = null;
        this.entityType = null;
    }

    public NaturalKeyValidationException(String msg) {
        super(msg);
        this.naturalKeys = null;
        this.entityType = null;
    }

    public NaturalKeyValidationException(String entityType, List<String> naturalKeys) {
        super(entityType + " is missing the following required key fields: " + naturalKeys);
        this.entityType = entityType;
        this.naturalKeys = naturalKeys;
    }

    public NaturalKeyValidationException(NoNaturalKeysDefinedException e,
            String entityType, ArrayList<String> naturalKeys) {
        super(e);
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
