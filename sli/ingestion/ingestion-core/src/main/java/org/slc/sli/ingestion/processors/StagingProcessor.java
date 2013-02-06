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

import com.mongodb.MongoException;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Persists records into a datastore
 * @author ablum
 *
 */
@Component
public class StagingProcessor extends IngestionProcessor<NeutralRecordWorkNote>{
    private static final Logger LOG = LoggerFactory.getLogger(StagingProcessor.class);

    @Autowired
    private ResourceWriter<NeutralRecord> nrMongoStagingWriter;
    
    @Override
    protected void process(Exchange exchange, ProcessorArgs<NeutralRecordWorkNote> args) {

            writeRecords(args.workNote.getNeutralRecords(), args.reportStats);

    }

    private void writeRecords(List<NeutralRecord> neutralRecords, ReportStats rs) {
        for (NeutralRecord record : neutralRecords) {
            try {
                nrMongoStagingWriter.insertResource(record);
            } catch (DataAccessResourceFailureException darfe) {
                LOG.error("Exception processing record " + record.getRecordId() + " of type: " + record.getRecordId(), darfe);
                rs.incError();
            } catch (InvalidDataAccessApiUsageException ex) {
                LOG.error("Exception processing record " + record.getRecordId() + " of type: " + record.getRecordId(), ex);
                rs.incError();
            } catch (InvalidDataAccessResourceUsageException ex) {
                LOG.error("Exception processing record " + record.getRecordId() + " of type: " + record.getRecordId(), ex);
                rs.incError();
            } catch (MongoException me) {
                LOG.error("Exception processing record " + record.getRecordId() + " of type: " + record.getRecordId(), me);
                rs.incError();
            } catch (UncategorizedMongoDbException ex) {
                LOG.error("Exception processing record " + record.getRecordId() + " of type: " + record.getRecordId(), ex);
                rs.incError();
            }
        }
    }
    
    public void setNrMongoStagingWriter(ResourceWriter<NeutralRecord> nrMongoStagingWriter) {
        this.nrMongoStagingWriter = nrMongoStagingWriter;
    }

    @Override
    protected BatchJobStageType getStage() {
        return BatchJobStageType.STAGING_PROCESSOR;
    }

    @Override
    protected String getStageDescription() {
        return "Persists records to a temporary staging datastore";
    }

}
