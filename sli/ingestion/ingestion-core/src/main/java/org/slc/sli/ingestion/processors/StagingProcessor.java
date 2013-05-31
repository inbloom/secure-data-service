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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mongodb.MongoException;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.Resource;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.reporting.ReportStats;

/**
 * Persists records into a datastore
 *
 * @author ablum
 *
 */
public class StagingProcessor extends IngestionProcessor<NeutralRecordWorkNote, Resource> {
    private static final Logger LOG = LoggerFactory.getLogger(StagingProcessor.class);

    private ResourceWriter<NeutralRecord> nrMongoStagingWriter;

    @Override
    protected void process(Exchange exchange, ProcessorArgs<NeutralRecordWorkNote> args) {
        writeRecords(args.workNote.getNeutralRecords(), args.reportStats);
    }

    private void writeRecords(List<NeutralRecord> neutralRecords, ReportStats rs) {
        Map<String, List<NeutralRecord>> recordsByCollections = splitRecords(neutralRecords);
        for (Map.Entry<String, List<NeutralRecord>> entry : recordsByCollections.entrySet()) {
            try {
                nrMongoStagingWriter.insertResources(entry.getValue(), entry.getKey());
            } catch (DataAccessResourceFailureException darfe) {
                LOG.error("Exception occured while batch processing records of type: " + entry.getKey(), darfe);
                rs.incError();
            } catch (InvalidDataAccessApiUsageException ex) {
                LOG.error("Exception occured while batch processing records of type: " + entry.getKey(), ex);
                rs.incError();
            } catch (InvalidDataAccessResourceUsageException ex) {
                LOG.error("Exception occured while batch processing records of type: " + entry.getKey(), ex);
                rs.incError();
            } catch (MongoException me) {
                LOG.error("Exception occured while batch processing records of type: " + entry.getKey(), me);
                rs.incError();
            } catch (UncategorizedMongoDbException ex) {
                LOG.error("Exception occured while batch processing records of type: " + entry.getKey(), ex);
                rs.incError();
            }
        }
    }

    private Map<String, List<NeutralRecord>> splitRecords(List<NeutralRecord> neutralRecords) {
        Map<String, List<NeutralRecord>> recordsByCollection = new HashMap<String, List<NeutralRecord>>();

        for (NeutralRecord record : neutralRecords) {
            String collection = record.getRecordType();
            List<NeutralRecord> records = recordsByCollection.get(collection);
            if (records == null) {
                records = new ArrayList<NeutralRecord>();
            }
            records.add(record);
            recordsByCollection.put(collection, records);
        }
        return recordsByCollection;
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
