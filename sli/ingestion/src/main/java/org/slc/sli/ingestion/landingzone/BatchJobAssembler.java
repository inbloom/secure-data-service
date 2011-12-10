package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;

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
     * @param controlFile
     * @return BatchJob
     * @throws IOException
     */
    public BatchJob assembleJob(ControlFileDescriptor fileDesc) throws IOException {

        BatchJob job = BatchJob.createDefault();

        ControlFile controlFile = fileDesc.getFileItem();

        if (validator.isValid(fileDesc, job.getFaults())) {
            for (IngestionFileEntry entry : controlFile.getFileEntries()) {
                File f = entry.getFile();
                if (f != null) {
                    job.addFile(f);
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
