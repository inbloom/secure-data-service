package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.zip.ZipFile;

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

        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);
        } catch (IOException ex) {
         // error reading zip file
            fail(callback, getFailureMessage("SL_ERR_MSG4", zipFile.getName()));
            return false;
        }

        Enumeration<? extends ZipEntry> entries = zf.getEntries();

        boolean isValid = false;
        while (entries.hasMoreElements()) {

            ZipEntry ze = entries.nextElement();

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
        return isValid;
    }

    private static boolean isDirectory(ZipEntry zipEntry) {

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
