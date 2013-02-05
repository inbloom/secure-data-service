/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

import java.util.List;
import java.util.Map;

import com.mongodb.MongoException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;

/**
 *
 * @author slee
 *
 */
@Component
public class StagingProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.STAGING_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Persists records to the staging database";

    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";

    private static final Logger LOG = LoggerFactory.getLogger(StagingProcessor.class);

    @Autowired
    private NeutralRecordMongoAccess nrMongoStagingWriter;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void process(Exchange exchange) throws Exception {
        FileEntryWorkNote feWorkNote = exchange.getIn().getBody(FileEntryWorkNote.class);

        if (feWorkNote == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            persistNeutralRecords(feWorkNote.getQueuedRecords(), feWorkNote.getFileEntry().getFileName());
            feWorkNote.clearQueuedRecords();
        }
    }

    /**
     * Write all neutral records currently contained in the queue, and clear the queue.
     */
    private void persistNeutralRecords(Map<String, List<NeutralRecord>> queuedWrites, String fileName) {
        int total = 0;
        for (Map.Entry<String, List<NeutralRecord>> entry : queuedWrites.entrySet()) {
            if (entry.getValue().size() > 0) {
                try {
                    nrMongoStagingWriter.insertResources(entry.getValue(), entry.getKey());
                    total += entry.getValue().size();
                    LOG.info("Persisted {} records of type {} ", entry.getValue().size(), entry.getKey());
                    queuedWrites.get(entry.getKey()).clear();
                } catch (DataAccessResourceFailureException darfe) {
                    LOG.error("Exception processing record with entityPersistentHandler", darfe);
                } catch (InvalidDataAccessApiUsageException ex) {
                    LOG.error("Exception processing record with entityPersistentHandler", ex);
                } catch (InvalidDataAccessResourceUsageException ex) {
                    LOG.error("Exception processing record with entityPersistentHandler", ex);
                } catch (MongoException me) {
                    LOG.error("Exception processing record with entityPersistentHandler", me);
                } catch (UncategorizedMongoDbException ex) {
                    LOG.error("Exception processing record with entityPersistentHandler", ex);
                }
            }
        }
        LOG.info("Received and persisted {} records to staging db from file: {}.", total, fileName);
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LOG.error("No records specified in " + this.getClass().getName() + " exchange message header.");
    }

    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = (NeutralRecordMongoAccess) nrMongoStagingWriter;
    }

}

