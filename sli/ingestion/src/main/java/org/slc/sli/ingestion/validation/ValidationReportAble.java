package org.slc.sli.ingestion.validation;

/**
 * Type's validation is reportable.
 *
 * @author dduran
 *
 */
public interface ValidationReportAble {

    /**
     * Provides the ValidationReport for this type.
     *
     * @return ValidationReport or null if not yet validated.
     */
    ValidationReport getValidationReport();

}
