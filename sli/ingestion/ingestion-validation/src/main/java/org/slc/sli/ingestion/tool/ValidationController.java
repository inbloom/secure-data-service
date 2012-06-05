package org.slc.sli.ingestion.tool;

import java.io.File;
import java.io.IOException;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.handler.ZipFileHandler;
import org.slc.sli.ingestion.landingzone.BatchJobAssembler;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.validation.ComplexValidator;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.LoggingErrorReport;
import org.slf4j.Logger;

/**
 * Validation Controller reads zip file or ctl file in a give directory and applies set of
 * pre-defined validators.
 * 
 * @author mpatel
 */
public class ValidationController {
    
    private ZipFileHandler zipFileHandler;
    
    private BatchJobAssembler batchJobAssembler;
    
    // private List<? extends Validator<IngestionFileEntry>> validators;
    private ComplexValidator<IngestionFileEntry> complexValidator;
    
    private static Logger logger = LoggerUtil.getLogger();
    
    private static ErrorReport errorReport = new LoggingErrorReport(logger);
    
    /*
     * retrieve zip file or control file from the input directory and invoke
     * relevant validator
     */
    public void doValidation(File path) {
        if (path.isFile() && path.getName().endsWith(".ctl")) {
            processControlFile(path);
        } else if (path.isFile() && path.getName().endsWith(".zip")) {
            processZip(path);
        } else {
            logger.error("Invalid input: No clt/zip file found");
        }
    }
    
    public void processValidators(Job job) {
        boolean isValid = false;
        for (IngestionFileEntry ife : job.getFiles()) {
            logger.info("Processing file: {} ...", ife.getFileName());
            isValid = complexValidator.isValid(ife, errorReport);
            if (!isValid) {
                logger.info("Processing of file: {} resulted in errors.", ife.getFileName());
                continue;
            }
            logger.info("Processing of file: {} completed.", ife.getFileName());
        }
    }
    
    public void processZip(File zipFile) {
        
        logger.info("Processing a zip file [{}] ...", zipFile.getAbsolutePath());
        
        File ctlFile = zipFileHandler.handle(zipFile, errorReport);
        
        if (!errorReport.hasErrors()) {
            
            processControlFile(ctlFile);
        }
        
        logger.info("Zip file [{}] processing is complete.", zipFile.getAbsolutePath());
    }
    
    public void processControlFile(File ctlFile) {
        Job job = null;
        
        logger.info("Processing a control file [{}] ...", ctlFile.getAbsolutePath());
        
        try {
            LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
            lz.setDirectory(ctlFile.getAbsoluteFile().getParentFile());
            ControlFile cfile = ControlFile.parse(ctlFile);
            
            ControlFileDescriptor cfd = new ControlFileDescriptor(cfile, lz);
            
            job = batchJobAssembler.assembleJob(cfd);
            
            if (job != null) {
                for (Fault fault : job.getFaultsReport().getFaults()) {
                    if (fault.isError()) {
                        logger.error(fault.getMessage());
                    } else {
                        logger.warn(fault.getMessage());
                    }
                }
                processValidators(job);
            }
        } catch (IOException e) {
            logger.error("Cannot parse control file", ValidationController.class);
        } catch (SubmissionLevelException exception) {
            logger.error(exception.getMessage());
        } finally {
            logger.info("Control file [{}] processing is complete.", ctlFile.getAbsolutePath());
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
    
    public ComplexValidator<IngestionFileEntry> getComplexValidator() {
        return complexValidator;
    }
    
    public void setComplexValidator(ComplexValidator<IngestionFileEntry> complexValidator) {
        this.complexValidator = complexValidator;
    }
    
}
