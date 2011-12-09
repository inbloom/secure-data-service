package org.slc.sli.ingestion.landingzone.validation;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mortbay.log.Log;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

public class ChecksumValidator extends IngestionFileValidator {

    @Override
    public boolean isValid(FileEntryDescriptor item, List<Fault> faults) {
        IngestionFileEntry fe = item.getFileItem();

        if (StringUtils.isBlank(fe.getChecksum())) {
            faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG10", fe.getFileName())));

            return false;
        }

        String actualMd5Hex;

        try {
            // and the attributes match
            actualMd5Hex = item.getLandingZone().getMd5Hex(fe.getFile());
        } catch (IOException e) {
            actualMd5Hex = null;
        }

        if (StringUtils.isBlank(actualMd5Hex) || !actualMd5Hex.equals(fe.getChecksum())) {
            if (Log.isDebugEnabled()) {
                Log.debug(String.format("File [%s] checksum (%s) does not match control file checksum (%s).",
                        fe.getFileName(), actualMd5Hex, fe.getChecksum()));
            }

            faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG2", fe.getFileName())));

            return false;
        }

        return true;
    }

}
