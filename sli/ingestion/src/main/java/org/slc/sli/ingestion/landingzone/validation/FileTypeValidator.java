package org.slc.sli.ingestion.landingzone.validation;

import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ValidationReport;

/**
 * File Type validator.
 *
 */
public class FileTypeValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, ValidationReport callback) {
        IngestionFileEntry entry = item.getFileItem();
        FileType fileType = entry.getFileType();

        if (fileType == null) {
            fail(callback, getFailureMessage("SL_ERR_MSG1", entry.getFileName(), "type"));

            return false;
        }
        return true;
    }

}
