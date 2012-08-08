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

package org.slc.sli.ingestion.processors;

import java.io.File;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

/**
 * @author unavani
 *
 */
@Component
public class FilePreProcessor  implements Processor, MessageSourceAware  {

    private static final Logger LOG = LoggerFactory.getLogger(FilePreProcessor.class);
    private MessageSource messageSource;

    @Autowired
    private BatchJobDAO batchJobDAO;

    /* (non-Javadoc)
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO Auto-generated method stub

        String inputFileName = "control_file";
        File fileForControlFile = null;
        NewBatchJob newBatchJob = null;
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);

        try {
            fileForControlFile = exchange.getIn().getBody(File.class);
            inputFileName = fileForControlFile.getName();
            newBatchJob = getOrCreateNewBatchJob(batchJobId, fileForControlFile);
            LOG.debug("FilePreProcessor: " + inputFileName);

            //UN: If it is a control file, parse this file and move all the other files in the .done folder
            if(inputFileName.endsWith(FileFormat.CONTROL_FILE.getExtension())) {
                File lzFile = new File(newBatchJob.getTopLevelSourceId());
                LandingZone topLevelLandingZone = new LocalFileSystemLandingZone(lzFile);
                ControlFile controlFile = ControlFile.parse(fileForControlFile, topLevelLandingZone, messageSource);
                List<IngestionFileEntry> entries = controlFile.getFileEntries();
                for (IngestionFileEntry entry : entries) {
                    FileUtils.renameFile(new File(lzFile +  "\\" + entry.getFileName()), new File(lzFile +  "\\.done\\" + entry.getFileName()));
                }
            }

        } catch (Exception exception) {

        }

    }

    private NewBatchJob getOrCreateNewBatchJob(String batchJobId, File cf) {
        NewBatchJob job = null;
        if (batchJobId != null) {
            job = batchJobDAO.findBatchJobById(batchJobId);
        } else {
            job = createNewBatchJob(cf);
        }

        TenantContext.setJobId(job.getId());

        return job;
    }

    private NewBatchJob createNewBatchJob(File controlFile) {
        NewBatchJob newJob = NewBatchJob.createJobForFile(controlFile.getName());
        newJob.setSourceId(controlFile.getParentFile().getAbsolutePath() + File.separator);
        newJob.setStatus(BatchJobStatusType.RUNNING.getName());
        LOG.info("Created job [{}]", newJob.getId());
        return newJob;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.MessageSourceAware#setMessageSource(org.springframework.context.MessageSource)
     */
    @Override
    public void setMessageSource(MessageSource messageSource) {
        // TODO Auto-generated method stub

    }

}
