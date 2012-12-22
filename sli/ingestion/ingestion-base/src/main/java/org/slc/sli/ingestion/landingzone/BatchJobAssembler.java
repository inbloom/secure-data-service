/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.landingzone;

import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.validation.ControlFileValidator;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.DummyMessageReport;
import org.slc.sli.ingestion.reporting.JobSource;
import org.slc.sli.ingestion.reporting.SimpleReportStats;

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

        // Iterate over the configProperties and copy into the job
        Enumeration<Object> e = controlFile.configProperties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            job.setProperty(key, controlFile.configProperties.getProperty(key));
        }

        AbstractMessageReport report = new DummyMessageReport();
        AbstractReportStats reportStats = new SimpleReportStats(new JobSource(job.getId(),
                controlFile.getFileName(), "BatchJobAssembler"));

        if (job.getProperty(AttributeType.PURGE.getName()) == null) {
            if (validator.isValid(fileDesc, report, reportStats)) {
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
