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

import javax.xml.stream.Location;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.TypeProvider;
import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.parser.impl.EdfiRecordParserImpl2;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.reporting.impl.JobSource;
import org.slc.sli.ingestion.util.XsdSelector;

/**
 * EdFiParserProcessor implements camel splitter pattern.
 *
 * @author okrook
 *
 */
public class EdFiParserProcessor extends IngestionProcessor<FileEntryWorkNote, IngestionFileEntry> implements
        RecordVisitor {
    private static final String BATCH_JOB_STAGE_DESC = "Reads records from the interchanges";

    // Processor configuration
    private ProducerTemplate producer;
    private TypeProvider typeProvider;
    private XsdSelector xsdSelector;
    private int batchSize;

    // Internal state of the processor
    protected ThreadLocal<ParserState> state = new ThreadLocal<ParserState>();
    protected ThreadLocal<Integer> ignoredRecordcount = new ThreadLocal<Integer>();

    @Override
    protected void process(Exchange exchange, ProcessorArgs<FileEntryWorkNote> args) {
        prepareState(exchange, args.workNote);

        Source source = new FileSource(args.workNote.getFileEntry().getResourceId());
        Metrics metrics = Metrics.newInstance(args.workNote.getFileEntry().getResourceId());

        InputStream input = null;
        boolean validData = true;
        try {
            input = args.workNote.getFileEntry().getFileStream();
            Resource xsdSchema = xsdSelector.provideXsdResource(args.workNote.getFileEntry());

            parse(input, xsdSchema);
            metrics.setValidationErrorCount(ignoredRecordcount.get());

        } catch (IOException e) {
            getMessageReport().error(args.reportStats, source, CoreMessageCode.CORE_0061);
        } catch (SAXException e) {
            getMessageReport().error(args.reportStats, source, CoreMessageCode.CORE_0062);
        } catch (XmlParseException e) {
            getMessageReport().error(args.reportStats, source, CoreMessageCode.CORE_0063);
            validData = false;
        } finally {
            IOUtils.closeQuietly(input);

            if (validData) {
                sendDataBatch();
            }

            cleanUpState();

            // Get job from db to capture delta and staging processor information
            args.job = refreshjob(args.job.getId());
            args.stage.addMetrics(metrics);
        }
    }

    protected void parse(InputStream input, Resource xsdSchema) throws SAXException, IOException, XmlParseException {
        EdfiRecordParserImpl2.parse(input, xsdSchema, typeProvider, this);
    }

    /**
     * Prepare the state for the job.
     *
     * @param exchange
     *            Exchange
     * @throws InvalidPayloadException
     */
    protected void prepareState(Exchange exchange, FileEntryWorkNote workNote) {
        ParserState newState = new ParserState();
        newState.setOriginalExchange(exchange);
        newState.setWork(workNote);

        state.set(newState);

        ignoredRecordcount.set(0);
    }

    /**
     * Clean the internal state for the job.
     */
    private void cleanUpState() {
        state.set(null);
    }

    private NewBatchJob refreshjob(String id) {
        return batchJobDAO.findBatchJobById(id);
    }

    @Override
    public void visit(RecordMeta recordMeta, Map<String, Object> record) {
        state.get().addToBatch(recordMeta, record);

        if (state.get().getDataBatch().size() >= batchSize) {
            sendDataBatch();
        }
    }

    @Override
    public void ignored() {
        ignoredRecordcount.set(ignoredRecordcount.get() + 1);
    }

    public void sendDataBatch() {
        ParserState s = state.get();

        if (s.getDataBatch().size() > 0) {
            NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(s.getDataBatch(), s.getWork().getBatchJobId(),
                    false);

            producer.sendBodyAndHeaders(workNote, s.getOriginalExchange().getIn().getHeaders());

            s.resetDataBatch();
        }
    }

    @Override
    public AbstractMessageReport getMessageReport() {
        return messageReport;
    }

    @Override
    public void setMessageReport(AbstractMessageReport messageReport) {
        this.messageReport = messageReport;
    }

    @Override
    public String getStageDescription() {
        return BATCH_JOB_STAGE_DESC;
    }

    @Override
    public BatchJobStageType getStage() {
        return BatchJobStageType.EDFI_PARSER_PROCESSOR;
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

        public void resetDataBatch() {
            dataBatch = new ArrayList<NeutralRecord>();
        }

        public void addToBatch(RecordMeta recordMeta, Map<String, Object> record) {
            NeutralRecord neutralRecord = new NeutralRecord();

            neutralRecord.setRecordType(StringUtils.uncapitalize(recordMeta.getName()));
            neutralRecord.setBatchJobId(work.getBatchJobId());
            neutralRecord.setSourceFile(work.getFileEntry().getResourceId());

            Location startLoc = recordMeta.getSourceStartLocation();
            Location endLoc = recordMeta.getSourceStartLocation();

            neutralRecord.setVisitBeforeLineNumber(startLoc.getLineNumber());
            neutralRecord.setVisitBeforeColumnNumber(startLoc.getColumnNumber());
            neutralRecord.setVisitAfterLineNumber(endLoc.getLineNumber());
            neutralRecord.setVisitAfterColumnNumber(endLoc.getColumnNumber());

            neutralRecord.setAttributes(record);
            dataBatch.add(neutralRecord);
        }
    }

    public void audit(SecurityEvent event) {
        // Do nothing
    }

    @Override
    protected IngestionFileEntry getItemToValidate(ProcessorArgs<FileEntryWorkNote> args) {
        return args.workNote.getFileEntry();
    }

    @Override
    protected Source getSource(ProcessorArgs<FileEntryWorkNote> args) {
        return new JobSource(args.workNote.getFileEntry().getResourceId());
    }

}
