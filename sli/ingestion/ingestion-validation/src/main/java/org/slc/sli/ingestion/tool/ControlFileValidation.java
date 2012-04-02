package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.ChecksumValidator;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.landingzone.validation.FileFormatValidator;
import org.slc.sli.ingestion.landingzone.validation.FileTypeValidator;
import org.slc.sli.ingestion.landingzone.validation.IngestionFileValidator;

/**
 * ControlFileValidation is used to validate the control file
 *
 * @author tke
 *
 */
public class ControlFileValidation {

    /**
     * Validate the control file specified by control file descriptor
     *
     * @param control
     *            file descriptor
     * @param the
     *            batch job
     * @return the batch job from the assembler
     */
    BatchJob validate(ControlFileDescriptor fileDesc, BatchJob job) {
        BatchJobAssembler assembler = new BatchJobAssembler();
        return assembler.assembleJob(fileDesc);
    }

    /**
     * Validate the control file specified by the location of the file
     *
     * @param dir
     *            : specifies the landing zone directory
     * @param ctrFile
     *            : specifies the name of the control file
     * @throws IOException
     */
    boolean validate(String dir, String ctrFile) throws IOException {
        LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
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
        return cfValidator.isValid(fileDesc, errorReport);
    }
}
