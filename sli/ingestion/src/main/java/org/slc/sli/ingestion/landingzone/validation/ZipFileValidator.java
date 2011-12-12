package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slc.sli.ingestion.Fault;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipFileValidator extends IngestionValidator<File> {

    @Override
    public boolean isValid(File zipFile, List<Fault> faults) {

        ZipFile zf = null;

        try {
            zf = new ZipFile(zipFile);
        } catch (ZipException ex) {
            // error reading zip file
            faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG4", zipFile.getName())));
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Enumeration<?> entries = zf.entries();

        while (entries.hasMoreElements()) {

            ZipEntry ze = (ZipEntry) entries.nextElement();

            // System.out.println("Read " + ze.getName() + "?");
            if (ze.getName().endsWith(".ctl"))
                return true;
        }

        // no manifest (.ctl file) found in the zip file
        faults.add(Fault.createError(getFailureMessage("SL_ERR_MSG5", zipFile.getName())));
        return false;
    }
}
