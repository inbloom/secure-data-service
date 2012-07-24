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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.TenantContext;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.handler.NeutralRecordEntityDeleteHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;

/**
 * Performs selective delete of data in mongodb based on Ed-Fi XML file.
 *
 * @author dshaw
 *
 */
@Component
public class DeleteProcessor implements Processor, MessageSourceAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.DELETE_PROCESSOR;
    private static Logger logger = LoggerFactory.getLogger(DeleteProcessor.class);

    private static final String METADATA_BLOCK = "metaData";

    private static final String TENANT_ID = "tenantId";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private List<String> excludeCollections;

    private MessageSource messageSource;

    private NeutralRecordEntityDeleteHandler deleteHandler;

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<String> getExcludeCollections() {
        return excludeCollections;
    }

    public void setExcludeCollections(List<String> excludeCollections) {
        this.excludeCollections = excludeCollections;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = getBatchJobId(exchange);
        if (batchJobId != null) {

            NewBatchJob newJob = null;
            try {
                newJob = batchJobDAO.findBatchJobById(batchJobId);

                // TODO Process Ed-Fi, until then, hard-code a student to 
                // delete
                String resourceId = "";
                
                NeutralRecord neutralRecord = new NeutralRecord();
                Map<String,Object> attributes = new HashMap<String,Object>();
                attributes.put("studentUniqueStateId", "530425896");
                neutralRecord.setRecordType("student");
                neutralRecord.setAttributes(attributes);
                neutralRecord.setLocalId("530425896");
                NeutralRecordEntity nrEntity = new NeutralRecordEntity(neutralRecord);
                nrEntity.setMetaDataField("tenantId", "Midgar");
                nrEntity.setMetaDataField("externalId", "530425896");

                // TODO Set up this ErrorReport properly
                ErrorReport errorReportForNrEntity = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                        resourceId, batchJobDAO);

                try {
                    deleteHandler.handle(nrEntity, errorReportForNrEntity);
                } catch (DataAccessResourceFailureException darfe) {
                    logger.error("Exception processing record with deleteHandler", darfe);
                }


            } catch (Exception exception) {
                handleProcessingExceptions(exchange, batchJobId, exception);
            } finally {
                if (newJob != null) {
                    BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                    batchJobDAO.saveBatchJob(newJob);
                }
            }

        } else {
            missingBatchJobIdError(exchange);
        }
    }

    public NeutralRecordEntityDeleteHandler getDeleteHandler() {
		return deleteHandler;
	}

	public void setDeleteHandler(NeutralRecordEntityDeleteHandler deleteHandler) {
		this.deleteHandler = deleteHandler;
	}

	private void handleProcessingExceptions(Exchange exchange, String batchJobId, Exception exception) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        exchange.setProperty("delete.complete", "Errors encountered during delete process");
        logger.error("Error processing batch job " + batchJobId, exception);

        if (batchJobId != null) {
            Error error = Error.createIngestionError(batchJobId, null, BatchJobStageType.DELETE_PROCESSOR.getName(),
                    null, null, null, FaultType.TYPE_ERROR.getName(), null, exception.toString());
            batchJobDAO.saveError(error);
        }
    }

    private String getBatchJobId(Exchange exchange) {
        String batchJobId = null;

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);
        if (workNote != null) {
            batchJobId = workNote.getBatchJobId();
        }
        return batchJobId;
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        logger.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }
}
