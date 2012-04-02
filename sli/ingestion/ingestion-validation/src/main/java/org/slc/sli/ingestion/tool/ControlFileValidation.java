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
     */
    void validate(ControlFileDescriptor fileDesc, BatchJob job){
        BatchJobAssembler assembler = new BatchJobAssembler();
        assembler.populateJob(fileDesc, job);
    }
}
