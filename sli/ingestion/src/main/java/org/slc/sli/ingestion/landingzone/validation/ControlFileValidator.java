package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.util.List;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.spring.SimpleValidator;

/**
 * Control File validator.
 *
 * @author okrook
 *
 */
public class ControlFileValidator extends SimpleValidator<ControlFileDescriptor> {

    private List<IngestionFileValidator> ingestionFileValidators;

    @Override
    public boolean isValid(ControlFileDescriptor item, ErrorReport callback) {
        ControlFile controlFile = item.getFileItem();

        List<IngestionFileEntry> entries = controlFile.getFileEntries();

        if (entries.size() < 1) {
            fail(callback, getFailureMessage("SL_ERR_MSG9"));

            return false;
        }

        for (IngestionFileEntry entry : entries) {
            File file = item.getLandingZone().getFile(entry.getFileName());

            if (file == null) {
                fail(callback, getFailureMessage("SL_ERR_MSG3", entry.getFileName()));
            } else {
                entry.setFile(file);

                if (!isValid(new FileEntryDescriptor(entry, item.getLandingZone()), callback)) {
                    // remove the file from the entry since it did not pass the validation
                    entry.setFile(null);
                }
            }
        }

        return true;
    }

    protected boolean isValid(FileEntryDescriptor item, ErrorReport callback) {
        for (IngestionFileValidator validator : ingestionFileValidators) {
            if (!validator.isValid(item, callback)) {
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
