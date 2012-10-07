package org.slc.sli.validation;

/**
 * Exception for Natural Key Lookup
 *
 * @author sashton
 */
public class NoNaturalKeysDefinedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    final String entityType;

    public NoNaturalKeysDefinedException(String entityType) {
        super(entityType + " should have natural keys defined, but none were found");
        this.entityType = entityType;
    }

    public String getEntityType() {
        return entityType;
    }

}
