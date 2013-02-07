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

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.ProcessorSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Abstract processor for Ingestion
 *
 * @param <T> A unit of work that contains all information that needs to be processed
 *
 * @param <S> Resource to validate
 */
public abstract class IngestionProcessor<T extends WorkNote, S> implements Processor {
    private static final String INGESTION_MESSAGE_TYPE = "IngestionMessageType";
    private static final Logger LOG = LoggerFactory.getLogger(IngestionProcessor.class);

    protected BatchJobDAO batchJobDAO;

    private AbstractMessageReport messageReport;

    List<? extends Validator<S>> preValidators;

    List<? extends Validator<S>> postValidators;

    @Override
    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        try {

            T workNote = (T) exchange.getIn().getMandatoryBody(WorkNote.class);
            ReportStats rs = new SimpleReportStats();
            NewBatchJob job = getJob(workNote);
            Stage stage = Stage.createAndStartStage(getStage(), getStageDescription());

            try {

                ProcessorArgs<T> args = new ProcessorArgs<T>();
                args.workNote = workNote;
                args.job = job;
                args.stage = stage;
                args.reportStats = rs;

                pre(itemToValidate(args), messageReport, args.reportStats, getSource(args));
                if (!args.reportStats.hasErrors()) {
                    process(exchange, args);
                    post(itemToValidate(args), messageReport, args.reportStats, getSource(args));
                }

            } catch (Exception e) {
                handleProcessingExceptions(exchange, workNote, e);
            } finally {
                if (job != null) {
                    BatchJobUtils.stopStageAndAddToJob(stage, job);
                    getBatchJobDAO().saveBatchJob(job);
                }
            }

        } catch (InvalidPayloadException e) {
            exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
            LOG.error("Cannot retrieve a work note to process.");
        }

    }

    protected abstract void process(Exchange exchange, ProcessorArgs<T> args);

    void pre(S item, AbstractMessageReport report, ReportStats reportStats, Source source) {
        if (preValidators != null) {
            for (Validator<S> validator : preValidators) {
                validator.isValid(item, report, reportStats, source);
            }
        }
    }

    void post(S item, AbstractMessageReport report, ReportStats reportStats, Source source) {
        if (postValidators != null) {
            for (Validator<S> validator : postValidators) {
                validator.isValid(item, report, reportStats, source);
            }
        }
    }

    protected void handleProcessingExceptions(Exchange exchange, WorkNote workNote, Exception exception) {
        exchange.getIn().setHeader(INGESTION_MESSAGE_TYPE, MessageType.ERROR.name());
        LogUtil.error(LOG, "Error processing batch job " + workNote.getBatchJobId(), exception);

        if (workNote.getBatchJobId() != null) {
            ReportStats reportStats = new SimpleReportStats();
            messageReport.error(reportStats, new ProcessorSource(getStage().getName()),
                    CoreMessageCode.CORE_0062, workNote.getBatchJobId(),
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

    public AbstractMessageReport getMessageReport() {
        return messageReport;
    }

    public void setMessageReport(AbstractMessageReport messageReport) {
        this.messageReport = messageReport;
    }

    protected abstract BatchJobStageType getStage();
    protected abstract String getStageDescription();

    protected abstract S itemToValidate(ProcessorArgs<T> args);
    protected abstract Source getSource(ProcessorArgs<T> args);

    /**
     * Common arguments for Ingestion processors.
     *
     * @param <T> Type of the WorkNote.
     */
    static class ProcessorArgs<T> {
        protected Stage stage;
        protected NewBatchJob job;
        protected T workNote;
        protected ReportStats reportStats;
    };
}
