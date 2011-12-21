package org.slc.sli.ingestion.landingzone.validation;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.validation.ValidationReport;

/**
 * Validates file's checksum using MD5 algorithm.
 *
 * @author okrook
 *
 */
public class ChecksumValidator extends IngestionFileValidator {

    private Logger log = LoggerFactory.getLogger(ChecksumValidator.class);

    @Override
    public boolean isValid(FileEntryDescriptor item, ValidationReport callback) {
        IngestionFileEntry fe = item.getFileItem();

        if (StringUtils.isBlank(fe.getChecksum())) {
            fail(callback, getFailureMessage("SL_ERR_MSG10", fe.getFileName()));

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

            if (log.isDebugEnabled()) {
                String[] args = { fe.getFileName(), actualMd5Hex, fe.getChecksum() };
                log.debug("File [{}] checksum ({}) does not match control file checksum ({}).", args);
            }

            fail(callback, getFailureMessage("SL_ERR_MSG2", fe.getFileName()));

            return false;
        }

        return true;
    }

}
