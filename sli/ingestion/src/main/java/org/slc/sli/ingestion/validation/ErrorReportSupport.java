package org.slc.sli.ingestion.validation;

/**
 * Type's validation is reportable.
 *
 * @author dduran
 *
 */
public interface ErrorReportSupport {

    /**
     * Provides the ValidationReport for this type.
     *
     * @return ValidationReport instance.
     */
    ErrorReport getValidationReport();

}
