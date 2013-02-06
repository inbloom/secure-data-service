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

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Abstract processor for Ingestion
 * T A unit of work that contains all information that needs to be processed
 * @author ablum
 *
 */
@Component
public abstract class IngestionProcessor<T extends WorkNote> implements Processor{
    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";
    private static final Logger LOG = LoggerFactory.getLogger(IngestionProcessor.class);

    @Autowired
    protected BatchJobDAO batchJobDAO;

    @Autowired
    private AbstractMessageReport databaseMessageReport;

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        try {
            
            T workNote = (T) exchange.getIn().getMandatoryBody(WorkNote.class);
            ReportStats rs = new SimpleReportStats();
            NewBatchJob job = getJob(workNote);
            Stage stage = Stage.createAndStartStage(getStage(), getStageDescription());
            
            try {
                process(exchange, workNote, job, rs);
                
                exchange.getIn().setHeader("hasErrors", rs.hasErrors());
            } catch (Exception e) {
                handleProcessingExceptions(exchange, workNote, e);
            } finally {
                if (job != null) {
                    BatchJobUtils.stopStageAndAddToJob(stage, job);
                    getBatchJobDAO().saveBatchJob(job);
                }
            }
            
        } catch (InvalidPayloadException e) {
            exchange.getIn().setHeader("hasErrors", true);
            LOG.error("Cannot retrieve a work note to process.");
        } 

    }

    protected abstract void process(Exchange exchange, T workNote, NewBatchJob newBatchJob, ReportStats rs);

    protected void handleProcessingExceptions(Exchange exchange, WorkNote workNote, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + workNote.getBatchJobId(), exception);

        if (workNote.getBatchJobId() != null) {
            ReportStats reportStats = new SimpleReportStats();
            databaseMessageReport.error(reportStats, new ProcessorSource(getStage().getName()),
                    CoreMessageCode.CORE_0021, workNote.getBatchJobId(),
                    exception.getMessage());
        }
    }
    
    private NewBatchJob getJob(WorkNote work) {
        NewBatchJob job = batchJobDAO.findBatchJobById(work.getBatchJobId());

        initTenantContext(job);

        return job;
    }

    private void initTenantContext(NewBatchJob newJob) {
        String tenantId = newJob.getTenantId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setJobId(newJob.getId());
        TenantContext.setBatchProperties(newJob.getBatchProperties());
    }

    public BatchJobDAO getBatchJobDAO() {
        return batchJobDAO;
    }
    
    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public AbstractMessageReport getDatabaseMessageReport() {
        return databaseMessageReport;
    }

    public void setDatabaseMessageReport(AbstractMessageReport databaseMessageReport) {
        this.databaseMessageReport = databaseMessageReport;
    }
    
    protected abstract BatchJobStageType getStage();
    protected abstract String getStageDescription();
}
