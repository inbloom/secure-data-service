package org.slc.sli.ingestion.landingzone.validation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.SimpleValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validator for EdFi xml ingestion files.
 *
 * @author dduran
 *
 */
public class XmlFileValidator extends SimpleValidator<IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        LOG.debug("validating csv...");

        if (isEmptyOrUnreadable(fileEntry, errorReport))
            return false;

        return true;
    }

    private boolean isEmptyOrUnreadable(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        boolean isEmpty = false;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEntry.getFile()));
            if (br.read() == -1) {
                errorReport.fatal("Input file is empty: " + fileEntry.getFileName(), CsvFileValidator.class);
                isEmpty = true;
            }

        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + fileEntry.getFileName());
            errorReport.error("File not found: " + fileEntry.getFileName(), CsvFileValidator.class);
            isEmpty = true;
        } catch (IOException e) {
            LOG.error("Problem reading file: " + fileEntry.getFileName());
            errorReport.error("Problem reading file: " + fileEntry.getFileName(), CsvFileValidator.class);
            isEmpty = true;
        }
        return isEmpty;
    }

}
