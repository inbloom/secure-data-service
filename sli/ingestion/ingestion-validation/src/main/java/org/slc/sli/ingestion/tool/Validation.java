package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.ChecksumValidator;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.landingzone.validation.FileFormatValidator;
import org.slc.sli.ingestion.landingzone.validation.FileTypeValidator;
import org.slc.sli.ingestion.landingzone.validation.IngestionFileValidator;

public class Validation {

    void validateControlFile(String dir, String ctrFile) throws IOException{
        LocalFileSystemLandingZone lz  = new LocalFileSystemLandingZone();
        lz.setDirectory(new File(dir));
        ControlFile cf = ControlFile.parse(new File(dir + ctrFile));
        ControlFileDescriptor fileDesc = new ControlFileDescriptor(cf, lz);
        ControlFileValidator cfValidator = new ControlFileValidator();
        List<IngestionFileValidator> ingestionFileValidators = new ArrayList<IngestionFileValidator>();
        ingestionFileValidators.add(new FileFormatValidator());
        ingestionFileValidators.add(new FileTypeValidator());
        ingestionFileValidators.add(new ChecksumValidator());
        cfValidator.setIngestionFileValidators(ingestionFileValidators);
        FaultsReport errorReport = new FaultsReport();
        cfValidator.isValid(fileDesc, errorReport);
    }
}
