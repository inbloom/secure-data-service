package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.landingzone.validation.FileFormatValidator;
import org.slc.sli.ingestion.landingzone.validation.FileTypeValidator;

/**
 *
 * @author jsa
 *
 */
@Component
public class BatchJobAssembler {

    @Autowired
    private ControlFileValidator validator;

    /**
     * Attempt to generate a new BatchJob based on data found in the
     * controlFile.
     *
     * @param controlFile Control file descriptor
     * @return BatchJob Assembled batch job
     */
    public BatchJob assembleJob(ControlFileDescriptor fileDesc) {
        BatchJob job = BatchJob.createDefault();

        return populateJob(fileDesc, job);
    }

    /**
     * Attempt to populate a BatchJob based on data found in the
     * controlFile.
     * @param fileDesc Control file descriptor
     * @param job Batch Job to populate
     * @return populated Batch Job
     */
    public BatchJob populateJob(ControlFileDescriptor fileDesc, BatchJob job) {
        ControlFile controlFile = fileDesc.getFileItem();

        if (validator.isValid(fileDesc, job.getFaults())) {
            for (IngestionFileEntry entry : controlFile.getFileEntries()) {
                if (entry.getFile() != null) {
                    job.addFile(entry);
                }
            }
        }

        // iterate over the configProperties and copy into the job
        // TODO validate config properties are legit
        Enumeration<Object> e = controlFile.configProperties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            job.setProperty(key, controlFile.configProperties.getProperty(key));
        }

        return job;
    }

    public ControlFileValidator getValidator() {
        return validator;
    }

    public void setValidator(ControlFileValidator validator) {
        this.validator = validator;
    }
}
