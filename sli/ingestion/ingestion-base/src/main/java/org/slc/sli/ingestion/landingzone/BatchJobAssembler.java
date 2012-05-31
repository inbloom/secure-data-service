package org.slc.sli.ingestion.landingzone;

import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Job;
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
     *            Control file descriptor
     * @return BatchJob Assembled batch job
     */
    public Job assembleJob(ControlFileDescriptor fileDesc) {
        Job job = BatchJob.createDefault();

        return populateJob(fileDesc, job);
    }

    /**
     * Attempt to generate a new BatchJob based on data found in the
     * controlFile.
     *
     * @param controlFile
     *            Control file descriptor
     * @param filename
     *            string representation of incoming file
     * @return BatchJob Assembled batch job
     */
    public Job assembleJob(ControlFileDescriptor fileDesc, String filename) {
        // TODO DatabaseBatchJob job = DatabaseBatchJob.createBatchJob(filename);
        Job job = BatchJob.createDefault(filename);

        return populateJob(fileDesc, job);
    }

    /**
     * Attempt to populate a BatchJob based on data found in the
     * controlFile.
     *
     * @param fileDesc
     *            Control file descriptor
     * @param job
     *            Batch Job to populate
     * @return populated Batch Job
     */
    public Job populateJob(ControlFileDescriptor fileDesc, Job job) {
        ControlFile controlFile = fileDesc.getFileItem();

        // iterate over the configProperties and copy into the job
        // TODO validate config properties are legit
        Enumeration<Object> e = controlFile.configProperties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            job.setProperty(key, controlFile.configProperties.getProperty(key));
        }

        if (job.getProperty(AttributeType.PURGE.getName()) == null) {
            if (validator.isValid(fileDesc, ((BatchJob) job).getFaultsReport())) {
                for (IngestionFileEntry entry : controlFile.getFileEntries()) {
                    if (entry.getFile() != null) {
                        ((BatchJob) job).addFile(entry);
                    }
                }
            }
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
