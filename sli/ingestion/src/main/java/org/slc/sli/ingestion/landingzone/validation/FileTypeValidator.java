package org.slc.sli.ingestion.landingzone.validation;

import java.util.List;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * 
 *
 */
public class FileTypeValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, List<Fault> faults) {
        IngestionFileEntry entry = item.getFileItem();
        FileType fileType = entry.getFileType();
        if (fileType == null) {
            faults.add(Fault.createError(
                    "unknown or empty file type specified for file ["
                    + entry.getFileName() + "]"));
            return false;
        }
        return true;
    }
    
}
