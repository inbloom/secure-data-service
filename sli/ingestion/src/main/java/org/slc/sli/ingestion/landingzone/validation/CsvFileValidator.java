package org.slc.sli.ingestion.landingzone.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.SimpleValidator;

/**
 * Validator for ingestion csv files
 *
 * @author dduran
 *
 */
public class CsvFileValidator extends SimpleValidator<IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvFileValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry object, ErrorReport callback) {
        LOG.info("validating...");
        return false;
    }

}
