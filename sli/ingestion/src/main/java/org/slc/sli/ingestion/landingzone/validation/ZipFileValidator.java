package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slc.sli.ingestion.validation.ValidationReport;
import org.slc.sli.ingestion.validation.spring.SimpleValidator;

/**
 * Zip file validator.
 *
 * @author okrook
 *
 */
public class ZipFileValidator extends SimpleValidator<File> {

    @Override
    public boolean isValid(File zipFile, ValidationReport callback) {

        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);
        } catch (IOException ex) {
         // error reading zip file
            fail(callback, getFailureMessage("SL_ERR_MSG4", zipFile.getName()));
            return false;
        }

        Enumeration<?> entries = zf.entries();

        while (entries.hasMoreElements()) {

            ZipEntry ze = (ZipEntry) entries.nextElement();

            // System.out.println("Read " + ze.getName() + "?");
            if (ze.getName().endsWith(".ctl"))
                return true;
        }

        // no manifest (.ctl file) found in the zip file
        fail(callback, getFailureMessage("SL_ERR_MSG5", zipFile.getName()));
        return false;
    }
}
