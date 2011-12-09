package org.slc.sli.ingestion.landingzone.validation;

import java.util.List;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;

public class FileFormatValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, List<Fault> faults) {
        return true;
    }

}
