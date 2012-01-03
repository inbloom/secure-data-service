package org.slc.sli.ingestion.landingzone.validation;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * File format validator.
 */
public class FileFormatValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, ErrorReport callback) {
        IngestionFileEntry entry = item.getFileItem();
        FileFormat format = entry.getFileFormat();
        if (format == null) {
            fail(callback, getFailureMessage("SL_ERR_MSG1", entry.getFileName(), "format"));

            return false;
        }
        return true;
    }

}
