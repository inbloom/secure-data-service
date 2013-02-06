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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
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
import org.slc.sli.ingestion.util.XsdSelector;

/**
 * EdFiParserProcessor implements camel splitter pattern.
 *
 * @author okrook
 *
 */
public class EdFiParserProcessor extends IngestionProcessor<FileEntryWorkNote> implements RecordVisitor {
    private static final Logger LOG = LoggerFactory.getLogger(EdFiParserProcessor.class);
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
    protected void process(Exchange exchange, FileEntryWorkNote workNote, NewBatchJob job, ReportStats rs) {
        prepareState(exchange, workNote);

        cleanUpState();
    }


    /**
     * Prepare the state for the job.
     *
     * @param exchange
     *            Exchange
     * @throws InvalidPayloadException
     */
    private void prepareState(Exchange exchange, FileEntryWorkNote workNote) {
        ParserState newState = new ParserState();
        newState.setOriginalExchange(exchange);
        newState.setWork(workNote);

        state.set(newState);
    }

    /**
     * Clean the internal state for the job.
     */
    private void cleanUpState() {
        state.set(null);
    }

    @Override
    public String getStageDescription() {
        return BATCH_JOB_STAGE_DESC;
    }

    @Override
    public BatchJobStageType getStage() {
        return BatchJobStageType.EDFI_PROCESSOR;
    }

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
    public void visit(RecordMeta recordMeta, Map<String, Object> record) {
        state.get().addToBatch(recordMeta, record);
    }

    public void sendDataBatch() {
        ParserState s = state.get();

        if (s.getDataBatch().size() > 0) {
            producer.sendBodyAndHeaders(s.getDataBatch(), s.getOriginalExchange().getIn().getHeaders());
        }
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
        private FileEntryWorkNote work;
        private List<NeutralRecord> dataBatch = new ArrayList<NeutralRecord>();

        public Exchange getOriginalExchange() {
            return originalExchange;
        }

        public void setOriginalExchange(Exchange originalExchange) {
            this.originalExchange = originalExchange;
        }

        public FileEntryWorkNote getWork() {
            return work;
        }

        public void setWork(FileEntryWorkNote work) {
            this.work = work;
        }

        public List<NeutralRecord> getDataBatch() {
            return dataBatch;
        }

        public void addToBatch(RecordMeta recordMeta, Map<String, Object> record) {
            NeutralRecord neutralRecord = new NeutralRecord();

            neutralRecord.setBatchJobId(work.getBatchJobId());
            neutralRecord.setSourceFile(work.getFileEntry().getResourceId());

//            neutralRecord.setVisitBeforeLineNumber(visitBeforeLineNumber);
//            neutralRecord.setVisitBeforeColumnNumber(visitBeforeColumnNumber);
//            neutralRecord.setVisitAfterLineNumber(visitAfterLineNumber);
//            neutralRecord.setVisitAfterColumnNumber(visitAfterColumnNumber);

            neutralRecord.setAttributes(record);
        }
    }

    public void audit(SecurityEvent event) {
        // Do nothing
    }
}
