package org.slc.sli.ingestion.validation;

/**
 * Type's errors are reportable.
 *
 * @author dduran
 *
 */
public interface ErrorReportSupport {

    /**
     * Provides the ErrorReport for this type.
     *
     * @return ErrorReport instance.
     */
    ErrorReport getErrorReport();

}
