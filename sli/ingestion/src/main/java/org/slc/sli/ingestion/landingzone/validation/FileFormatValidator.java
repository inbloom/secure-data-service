package org.slc.sli.ingestion.landingzone.validation;

import java.util.List;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * 
 */
public class FileFormatValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, List<Fault> faults) {
        IngestionFileEntry entry = item.getFileItem();
        FileFormat format = entry.getFileFormat();
        if (format == null) {
            faults.add(Fault.createError(
                    "unknown or empty file format specified for file ["
                    + entry.getFileName() + "]"));
            return false;
        }
        return true;
    }

}
