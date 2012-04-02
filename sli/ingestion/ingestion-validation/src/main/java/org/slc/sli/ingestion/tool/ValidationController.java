package org.slc.sli.ingestion.tool;

import java.io.*;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobLogger;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;

import ch.qos.logback.classic.Logger;

public class ValidationController {

	private ZipValidation zipValidation;

	private ControlFileValidation ctlValidation;

	private LocalFileSystemLandingZone lz;

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

	void setZipValidation(ZipValidation zv) {
		zipValidation = zv;
	}

	ZipValidation getZipValidation() {
		return zipValidation;
	}

	void setCtlValidation(ControlFileValidation cv) {
		ctlValidation = cv;
	}

	ControlFileValidation getCtlValidation() {
		return ctlValidation;
	}

	class ZipFilter implements FilenameFilter
	{
	    public boolean accept(File d,String n)
	    {
	        return n.endsWith(".zip");
	    }
	}

	public class CtlFilter implements FilenameFilter {
	    public boolean accept(File d,String n)
	    {
	        return n.endsWith(".ctl");
	    }
	}


}


