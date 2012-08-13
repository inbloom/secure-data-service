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
import java.io.IOException;
import java.util.List;

import com.mongodb.MongoException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.FileUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * @author unavani
 *
 */
@Component
public class FilePreProcessor  implements Processor, MessageSourceAware  {

    private static final Logger LOG = LoggerFactory.getLogger(FilePreProcessor.class);
    private MessageSource messageSource;
    private static final String BATCH_JOB_STAGE_NAME = "FilePreProcessor";

    @Autowired
    private BatchJobDAO batchJobDAO;

    /* (non-Javadoc)
     * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
     */
    @Override
    public void process(Exchange exchange) {

        String inputFileName = "control_file";
        File fileForControlFile = null;
        NewBatchJob newBatchJob = null;
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);

        try {
            fileForControlFile = exchange.getIn().getBody(File.class);
            inputFileName = fileForControlFile.getName();
            newBatchJob = getOrCreateNewBatchJob(batchJobId, fileForControlFile);

            moveControlFileDependencies(inputFileName, fileForControlFile, newBatchJob);

            exchange.getIn().setHeader("BatchJobId", batchJobId);
            if (inputFileName.endsWith(FileFormat.CONTROL_FILE.getExtension())) {
                exchange.getIn().setHeader("fileType", FileFormat.CONTROL_FILE.getExtension());
            } else if (inputFileName.endsWith(FileFormat.ZIP_FILE.getExtension())) {
                exchange.getIn().setHeader("fileType", FileFormat.ZIP_FILE.getExtension());
            }

        } catch (IOException ioException) {
            handleExceptions(exchange, batchJobId, ioException, inputFileName);
        } catch (SubmissionLevelException submissionLevelException) {
            handleExceptions(exchange, batchJobId, submissionLevelException, inputFileName);
        } catch (MongoException mongoException) {
            handleExceptions(exchange, batchJobId, mongoException, inputFileName);
        } catch (IllegalArgumentException illegalArgException) {
            handleExceptions(exchange, batchJobId, illegalArgException, inputFileName);
        }
    }

    private void moveControlFileDependencies(String inputFileName,
            File fileForControlFile, NewBatchJob newBatchJob)
            throws IOException, SubmissionLevelException {
        //UN: If it is a control file, parse this file and move all the other files in the .done folder
        if (inputFileName.endsWith(FileFormat.CONTROL_FILE.getExtension())) {
            File lzFile = new File(newBatchJob.getTopLevelSourceId());
            LandingZone topLevelLandingZone = new LocalFileSystemLandingZone(lzFile);
            ControlFile controlFile = ControlFile.parse(fileForControlFile, topLevelLandingZone, messageSource);
            List<IngestionFileEntry> entries = controlFile.getFileEntries();
            for (IngestionFileEntry entry : entries) {
                FileUtils.renameFile(new File(lzFile +  "\\" + entry.getFileName()), new File(lzFile +  "\\.done\\" + entry.getFileName()));
            }
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
        this.messageSource = messageSource;
    }

    private void handleExceptions(Exchange exchange, String batchJobId, Exception exception, String fileName) {
        exchange.getIn().setHeader("BatchJobId", batchJobId);
        exchange.getIn().setHeader("hasErrors", true);
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + batchJobId, exception);
        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, fileName, BATCH_JOB_STAGE_NAME, null,
                    null, null, FaultType.TYPE_ERROR.getName(), null, exception.getMessage());
            batchJobDAO.saveError(error);
            // this will require some routing changes
            WorkNote workNote = WorkNote.createSimpleWorkNote(batchJobId);
            exchange.getIn().setBody(workNote, WorkNote.class);
        }
    }

}
