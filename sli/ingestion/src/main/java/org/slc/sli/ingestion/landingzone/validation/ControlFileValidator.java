package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.util.List;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

public class ControlFileValidator extends IngestionValidator<ControlFileDescriptor> {

    private List<IngestionFileValidator> ingestionFileValidators;

    @Override
    public boolean isValid(ControlFileDescriptor item, List<Fault> faults) {
        ControlFile controlFile = item.getFileItem();

        List<IngestionFileEntry> entries = controlFile.getFileEntries();

        if (entries.size() < 1) {
            faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG9")));

            return false;
        }

        for (IngestionFileEntry entry : entries) {
            File file = item.getLandingZone().getFile(entry.getFileName());

            if (file == null) {
                faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG8", entry.getFileName())));
            } else {
                entry.setFile(file);

                if (!isValid(new FileEntryDescriptor(entry, item.getLandingZone()), faults)) {
                    // remove the file from the entry since it did not pass the validation
                    entry.setFile(null);
                }
            }
        }

        return true;
    }

    protected boolean isValid(FileEntryDescriptor item, List<Fault> faults) {
        for (IngestionFileValidator validator : ingestionFileValidators) {
            if (!validator.isValid(item, faults)) {
                return false;
            }
        }

        return true;
    }

    public List<IngestionFileValidator> getIngestionFileValidators() {
        return ingestionFileValidators;
    }

    public void setIngestionFileValidators(List<IngestionFileValidator> ingestionFileValidators) {
        this.ingestionFileValidators = ingestionFileValidators;
    }

}
