package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import ch.qos.logback.classic.Logger;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

public class ValidationController {

	private ZipValidation zipValidation;

    private ControlFileValidation ctlValidation;

    private LocalFileSystemLandingZone lz;

    private List<SimpleValidatorSpring> validators;

    ValidationController() {
        lz = new LocalFileSystemLandingZone();
    }


    void doValidation(String dirName) throws IOException {
        File directory = new File(dirName);
        BatchJob job=null;

        ZipFilter zFilter = new ZipFilter();
        String[] zipFiles=directory.list(zFilter);
        if (zipFiles.length > 0) {
            File ctlFile = zipFileValidation(zipFiles[0]);
            job=ctlFileValidation(ctlFile.getName());
        }

        CtlFilter ctlFilter = new CtlFilter();
        String[] ctlFiles=directory.list(ctlFilter);

        if (ctlFiles.length > 0) {
            job=ctlFileValidation(ctlFiles[0]);
        }

        for(IngestionFileEntry ife : job.getFiles()) {
            for(SimpleValidatorSpring validator : validators) {
                validator.isValid(ife, job.getFaultsReport());
            }
        }
        if (job != null ) {
           Logger jobLogger = BatchJobLogger.createLoggerForJob(job, lz);
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

        File zipFileValidation(String fileName) throws IOException {
            BatchJob job = BatchJob.createDefault(fileName);
            File zFile = new File(fileName);
            return zipValidation.validate(zFile, job);
        }

        BatchJob ctlFileValidation(String fileName) throws IOException {
            File ctlFile = new File(fileName);
            ControlFile cfile = ControlFile.parse(ctlFile);
            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);
            BatchJob job = BatchJob.createDefault(fileName);
            ctlValidation.validate(cfd, job);
            return job;
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

    class ZipFilter implements FilenameFilter
    {
        public boolean accept(File d,String n)
        {
            return n.endsWith(".zip");
        }
    }

    public class CtlFilter implements FilenameFilter {
        public boolean accept(File d,String n) {
            return n.endsWith(".ctl");
        }
    }


}
