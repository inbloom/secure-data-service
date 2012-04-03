package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validation Controller reads zip file or ctl file in a give directory and applies set of pre-defined validators.
 * @author mpatel
 */
public class ValidationController {

    private ZipValidation zipValidation;

    private ControlFileValidation ctlValidation;

    private LocalFileSystemLandingZone lz;

    private List<SimpleValidatorSpring> validators;

    private static final Logger LOG = LoggerFactory.getLogger(ValidationController.class);

    ValidationController() {

    }

    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    void doValidation(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists())
            return;
        BatchJob job = null;
        Logger jobLogger = null;
        lz.setDirectory(directory);
        ZipFilter zFilter = new ZipFilter();
        String[] zipFiles = directory.list(zFilter);
        CtlFilter ctlFilter = new CtlFilter();
        String[] ctlFiles = directory.list(ctlFilter);

        if ((zipFiles.length + ctlFiles.length) > 1) {
            LOG.error("Error: There must only be 1 .zip and/or .ctl file in the landing zone");
            return;
        }
        if (zipFiles.length > 0) {
            job = BatchJob.createDefault(zipFiles[0]);
            try {
                jobLogger = BatchJobLogger.createLoggerForJob(job, lz);
            } catch (Exception ex) {
                LOG.error("Error creating logger for zip validator...");
            }

            File ctlFile = zipFileValidation(dirPath, zipFiles[0], job);
            if (ctlFile != null) {
                File newDir = new File(ctlFile.getParent());
                /*
                 lz.setDirectory(newDir);

                try {
                    LocalFileSystemLandingZone zlz = new LocalFileSystemLandingZone();
                    zlz.setDirectory(directory);
                    jobLogger = BatchJobLogger.createLoggerForJob(job, zlz);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                */
                lz.setDirectory(newDir);
                job = ctlFileValidation(ctlFile.getParent(), ctlFile.getName(),
                        job);
            }
        } else {
            if (ctlFiles.length > 0) {
                try {
                    job = BatchJob.createDefault(ctlFiles[0]);
                    jobLogger = BatchJobLogger.createLoggerForJob(job, lz);
                    ctlFileValidation(dirPath, ctlFiles[0], job);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

         for (IngestionFileEntry ife : job.getFiles()) {
         for (SimpleValidatorSpring validator : validators) {
         validator.isValid(ife, job.getFaultsReport()); } }

        if (job != null) {
            FaultsReport fr = job.getFaultsReport();

            for (Fault fault : fr.getFaults()) {
                if (fault.isError()) {
                    jobLogger.error(fault.getMessage());
                } else {
                    jobLogger.warn(fault.getMessage());
                }
            }
        }
    }

    public void setZipValidation(ZipValidation zv) {
        zipValidation = zv;
    }

    public ZipValidation getZipValidation() {
        return zipValidation;
    }

    public void setCtlValidation(ControlFileValidation cv) {
        ctlValidation = cv;
    }

    public ControlFileValidation getCtlValidation() {
        return ctlValidation;
    }

    public List<SimpleValidatorSpring> getValidators() {
        return validators;
    }

    public void setValidators(List<SimpleValidatorSpring> validators) {
        this.validators = validators;
    }

    public LocalFileSystemLandingZone getLz() {
        return lz;
    }

    public void setLz(LocalFileSystemLandingZone lz) {
        this.lz = lz;
    }

    /**
     *  Filters all the zip files in the specified directory
     * @author mpatel
     *
     */
    class ZipFilter implements FilenameFilter {
        public boolean accept(File d, String n) {
            return n.endsWith(".zip");
        }
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

    /* validate zip file and return control file for further validation */
    private File zipFileValidation(String dirName, String fileName, BatchJob job) {
        File zFile = new File(dirName + "/" + fileName);
        return zipValidation.validate(zFile, job);
    }

    /* validate control file */
    private BatchJob ctlFileValidation(String dirName, String fileName, BatchJob job) {
        File ctlFile = new File(dirName + "/" + fileName);
        try {
            ControlFile cfile = ControlFile.parse(ctlFile);
            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);
            ctlValidation.validate(cfd, job);
        } catch (IOException ex) {
            LOG.error("exception parsing control file" + fileName + ex);
        }
        return job;
    }
}
