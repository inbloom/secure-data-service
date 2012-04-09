package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.LoggingErrorReport;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Validation Controller reads zip file or ctl file in a give directory and applies set of pre-defined validators.
 * @author mpatel
 */
public class ValidationController {

    private ZipFileHandler zipFileHandler;

    private BatchJobAssembler batchJobAssembler;

    private List<? extends Validator<IngestionFileEntry>> validators;

    private static Logger logger = null;

    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    void doValidation(File path) {

        logger = LoggerUtil.getLogger();
        BatchJob job = null;
        if (path.isFile() && path.getName().endsWith(".ctl")) {
            job = processControlFile(path);
        } else if (path.isFile() && path.getName().endsWith(".zip")) {
            job = processZip(path);
        } else {
            logger.error("Invalid input: No clt/zip file found");
            return;
        }

        if (job == null) {
            logger.error("Invalid input: No clt/zip file found");
            return;
        }

        if (job.getFaultsReport().hasErrors()) {
            for (Fault fault : job.getFaultsReport().getFaults()) {
                if (fault.isError()) {
                    logger.error(fault.getMessage());
                } else {
                    logger.warn(fault.getMessage());
                }
            }
        } else {
            processXMLValidators(job);
        }
    }

    private void processXMLValidators(BatchJob job) {
        ErrorReport errorReport = new LoggingErrorReport(logger);
        boolean isValid = false;
        for (IngestionFileEntry ife : job.getFiles()) {
            logger.info("Processing file: {} ...", ife.getFileName());
            for (Validator<IngestionFileEntry> validator : validators) {
                isValid = validator.isValid(ife, errorReport);
                if (!isValid) {
                    break;
                }
            }
        }
    }

    private BatchJob processZip(File zipFile) {
        ErrorReport errorReport = new LoggingErrorReport(logger);

        logger.info("Processing a zip file [{}] ...", zipFile.getAbsolutePath());

        File ctlFile = zipFileHandler.handle(zipFile, errorReport);

        BatchJob job = null;

        if (!errorReport.hasErrors()) {

            job = processControlFile(ctlFile);
        }

        logger.info("Zip file [{}] processing is complete.", zipFile.getAbsolutePath());

        return job;
    }

    private BatchJob processControlFile(File ctlFile) {
        logger.info("Processing a conotrol file [{}] ...", ctlFile.getAbsolutePath());

        try {
            LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
            lz.setDirectory(ctlFile.getParentFile());
            ControlFile cfile = ControlFile.parse(ctlFile);

            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);

            return batchJobAssembler.assembleJob(cfd);

        } catch (IOException e) {
            logger.error("Cannot parse control file", ValidationController.class);
        } finally {
            logger.info("Control file [{}] processing is complete.", ctlFile.getAbsolutePath());
        }

        return null;
    }

    /**
     *  Filters all the ctl files in the specified directory
     * @author mpatel
     *
     */
    public class CtlFilter implements FilenameFilter {
        public boolean accept(File d, String n) {
            return n.endsWith(".ctl");
        }
    }

    public ZipFileHandler getZipFileHandler() {
        return zipFileHandler;
    }

    public void setZipFileHandler(ZipFileHandler zipFileHandler) {
        this.zipFileHandler = zipFileHandler;
    }

    public BatchJobAssembler getBatchJobAssembler() {
        return batchJobAssembler;
    }

    public void setBatchJobAssembler(BatchJobAssembler batchJobAssembler) {
        this.batchJobAssembler = batchJobAssembler;
    }

    public List<? extends Validator<IngestionFileEntry>> getValidators() {
        return validators;
    }

    public void setValidators(List<? extends Validator<IngestionFileEntry>> validators) {
        this.validators = validators;
    }

}
