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


/*
 * Validation Controller reads zip file or ctl file in a give directory and applies set of pre-defined validators.
 * @author mpatel
 */

public class ValidationController {

	private ZipValidation zipValidation;

    private ControlFileValidation ctlValidation;

    private LocalFileSystemLandingZone lz;

    private List<SimpleValidatorSpring> validators;

    private static final Logger LOG = LoggerFactory.getLogger(ControlFile.class);

	ValidationController() {
		lz = new LocalFileSystemLandingZone();
	}

	/* retrieve zip file or control file from the input directory and invoke relevant validator */
	void doValidation(String dirName) {
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
	       Logger jobLogger = null;
	       try {
               jobLogger = BatchJobLogger.createLoggerForJob(job, lz);
	       } catch (IOException ex) {
        	   LOG.error("error creating logger..." + ex);
           }

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

    /* validate zip file and return control file for further validation */
    File zipFileValidation(String fileName) {
        BatchJob job = BatchJob.createDefault(fileName);
        File zFile = new File(fileName);
        return zipValidation.validate(zFile, job);
    }

    /* validate control file */
    BatchJob ctlFileValidation(String fileName) {
        File ctlFile = new File(fileName);
        BatchJob job = null;
        try {
            ControlFile cfile = ControlFile.parse(ctlFile);
            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);
             job = BatchJob.createDefault(fileName);
             ctlValidation.validate(cfd, job);
        } catch(IOException ex) {
            LOG.error("exception parsing control file" + fileName + ex);
        }
        return job;
    }
}
