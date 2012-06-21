package org.slc.sli.ingestion.landingzone.validation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 * Zip file validator.
 *
 * @author okrook
 *
 */
public class ZipFileValidator extends SimpleValidatorSpring<File> {

    @Override
    public boolean isValid(File zipFile, ErrorReport callback) {

        FileInputStream fis = null;
        ZipArchiveInputStream zis = null;

        boolean isValid = false;

        try {
            fis = new FileInputStream(zipFile);
            zis = new ZipArchiveInputStream(new BufferedInputStream(fis));

            ArchiveEntry ze;

            while ((ze = zis.getNextEntry()) != null) {

                if (isDirectory(ze)) {
                    fail(callback, getFailureMessage("SL_ERR_MSG15", zipFile.getName()));
                    return false;
                }

                if (ze.getName().endsWith(".ctl")) {
                    isValid = true;
                }
            }

            // no manifest (.ctl file) found in the zip file
            if (!isValid) {
                fail(callback, getFailureMessage("SL_ERR_MSG5", zipFile.getName()));
            }
        } catch (IOException ex) {
            // error reading zip file
            fail(callback, getFailureMessage("SL_ERR_MSG4", zipFile.getName()));
            return false;
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }
        return isValid;
    }

    private static boolean isDirectory(ArchiveEntry zipEntry) {

        if (zipEntry.isDirectory()) {
            return true;
        }


        //UN: This check is to ensure that any zipping utility which does not pack a directory entry
        //    is verified by checking for a filename with '/'. Example: Windows Zipping Tool.
        if (zipEntry.getName().contains("/")) {
            return true;
        }

        return false;
    }
}
