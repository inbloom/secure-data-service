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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.TypeProvider;
import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.parser.impl.EdfiRecordParserImpl;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.XsdSelector;

/**
 * EdFiParserProcessor implements camel splitter pattern.
 *
 * @author okrook
 *
 */
public class EdFiParserProcessor extends AbstractIngestionHandler<IngestionFileEntry, Void> implements Processor,
        RecordVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(EdFiParserProcessor.class);
    private static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.EDFI_PROCESSOR;
    private static final String BATCH_JOB_STAGE_DESC = "Reads records from the interchanges and persists to the staging database";
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

    // Processor configuration
    private BatchJobDAO batchJobDAO;
    private AbstractMessageReport messageReport;
    private ProducerTemplate producer;
    private TypeProvider typeProvider;
    private XsdSelector xsdSelector;
    private int batchSize;

    // Internal state of the processor
    private ThreadLocal<ParserState> state = new ThreadLocal<ParserState>();

    @Override
    public void process(Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        NewBatchJob job = null;

        try {
            prepareState(exchange);

            FileEntryWorkNote work = exchange.getIn().getMandatoryBody(FileEntryWorkNote.class);

            job = getJob(work);

            Metrics metrics = Metrics.newInstance(work.getFileEntry().getFileName());
            stage.addMetrics(metrics);

            ReportStats rs = new SimpleReportStats();

            handle(work.getFileEntry(), messageReport, rs);

            exchange.getIn().setHeader("hasErrors", rs.hasErrors());
        } catch (InvalidPayloadException e) {
            exchange.getIn().setHeader("hasErrors", true);
            LOG.error("Cannot retrieve a work note to process.");
        } finally {
            cleanUpState();

            if (job != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, job);
                batchJobDAO.saveBatchJob(job);
            }
        }
    }

    /**
     * Prepare the state for the job.
     *
     * @param exchange
     *            Exchange
     */
    private void prepareState(Exchange exchange) {
        ParserState newState = new ParserState();
        newState.setOriginalExchange(exchange);
        state.set(newState);
    }

    /**
     * Clean the internal state for the job.
     */
    private void cleanUpState() {
        state.set(null);
    }

    private NewBatchJob getJob(WorkNote work) {
        NewBatchJob job = batchJobDAO.findBatchJobById(work.getBatchJobId());

        String tenantId = job.getTenantId();
        TenantContext.setTenantId(tenantId);
        TenantContext.setJobId(job.getId());
        TenantContext.setBatchProperties(job.getBatchProperties());

        return job;
    }

    @Override
    public String getStageName() {
        return BATCH_JOB_STAGE.getName();
    }

    @Override
    protected Void doHandling(IngestionFileEntry item, AbstractMessageReport report, ReportStats reportStats,
            FileProcessStatus fileProcessStatus) {

        Source source = new FileSource(item.getResourceId());
        InputStream input = null;
        try {
            input = item.getFileStream();
            XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(input);

            EdfiRecordParserImpl.parse(reader, xsdSelector.provideXsdResource(item), typeProvider, this);
        } catch (IOException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0016);
        } catch (XMLStreamException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0017);
        } catch (XmlParseException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0017);
        } finally {
            IOUtils.closeQuietly(input);
        }

        return null;
    }

    @Override
    protected List<Void> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // This processor does not support this.
        return null;
    }

    @Override
    public void visit(RecordMeta type, Map<String, Object> record) {
        //
    }

    public void sendDataBatch() {
        ParserState s = state.get();

        if (s.getDataBatch().size() > 0) {
            producer.sendBodyAndHeaders(s.getDataBatch(), s.getOriginalExchange().getIn().getHeaders());
        }
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

    public ProducerTemplate getProducer() {
        return producer;
    }

    public void setProducer(ProducerTemplate producer) {
        this.producer = producer;
    }

    public TypeProvider getTypeProvider() {
        return typeProvider;
    }

    public void setTypeProvider(TypeProvider typeProvider) {
        this.typeProvider = typeProvider;
    }

    public XsdSelector getXsdSelector() {
        return xsdSelector;
    }

    public void setXsdSelector(XsdSelector xsdSelector) {
        this.xsdSelector = xsdSelector;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * State for the parser processor
     *
     * @author okrook
     *
     */
    static class ParserState {
        private Exchange originalExchange;
        private List<Object> dataBatch;

        public Exchange getOriginalExchange() {
            return originalExchange;
        }

        public void setOriginalExchange(Exchange originalExchange) {
            this.originalExchange = originalExchange;
        }

        public List<Object> getDataBatch() {
            return dataBatch;
        }

        public void setDataBatch(List<Object> dataBatch) {
            this.dataBatch = dataBatch;
        }
    }

    public void audit(SecurityEvent event) {
        // Do nothing
    }
}
