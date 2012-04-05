package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOG = LoggerFactory.getLogger(ValidationController.class);

    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    void doValidation(File path) {
        if (!path.exists()) {
            //TODO: Report an issue
            return;
        }

        BatchJob job = null;
        if (path.isDirectory()) {
            job = processDirectory(path);
        } else if (path.isFile() && path.getName().endsWith(".zip")) {
            job = processZip(path);
        }

        if (job == null) {
            LOG.error("Invalid input: No clt/zip file found");
            return;
        }

        if (job.getFaultsReport().hasErrors()) {
            for (Fault fault : job.getFaultsReport().getFaults()) {
                if (fault.isError()) {
                    LOG.error(fault.getMessage());
                } else {
                    LOG.warn(fault.getMessage());
                }
            }
        } else {
            processXMLValidators(job);
        }
    }

    private void processXMLValidators(BatchJob job) {
        ErrorReport errorReport = new LoggingErrorReport(LOG);

        for (IngestionFileEntry ife : job.getFiles()) {
            for (Validator<IngestionFileEntry> validator : validators) {
                validator.isValid(ife, errorReport);
            }
        }
    }

    private BatchJob processZip(File zipFile) {
        ErrorReport errorReport = new LoggingErrorReport(LOG);

        LOG.info("Processing a zip file [{}] ...", zipFile.getAbsolutePath());

        File ctlFile = zipFileHandler.handle(zipFile, errorReport);

        BatchJob job = null;

        if (!errorReport.hasErrors()) {

            job = processControlFile(ctlFile);
        }

        LOG.info("Zip file [{}] processing is complete.", zipFile.getAbsolutePath());

        return job;
    }

    private BatchJob processDirectory(File directory) {
        LOG.info("Processing a folder [{}] ...", directory.getAbsolutePath());

        CtlFilter ctlFilter = new CtlFilter();
        File[] ctlFiles = directory.listFiles(ctlFilter);

        BatchJob job = null;

        if (ctlFiles.length > 0) {

            job = processControlFile(ctlFiles[0]);
        }

        LOG.info("Folder [{}] processing is complete.", directory.getAbsolutePath());

        return job;
    }

    private BatchJob processControlFile(File ctlFile) {
        LOG.info("Processing a conotrol file [{}] ...", ctlFile.getAbsolutePath());

        try {
            LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
            lz.setDirectory(ctlFile.getParentFile());
            ControlFile cfile = ControlFile.parse(ctlFile);

            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);

            return batchJobAssembler.assembleJob(cfd);

        } catch (IOException e) {
            LOG.error("Cannot parse control file", ValidationController.class);
        } finally {
            LOG.info("Control file [{}] processing is complete.", ctlFile.getAbsolutePath());
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
