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
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.parser.RecordMeta;
import org.slc.sli.ingestion.parser.RecordVisitor;
import org.slc.sli.ingestion.parser.TypeProvider;
import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.parser.impl.EdfiRecordParserImpl2;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import test.camel.support.ZipFileEntry;

/**
 * EdFiParserProcessor implements camel splitter pattern.
 *
 * @author okrook
 *
 */
public class EdFiParserProcessor implements Processor, RecordVisitor {
    // Processor configuration
    private ProducerTemplate producer;
    private TypeProvider typeProvider;
    private XsdSelector xsdSelector;
    private int batchSize;

    // Internal state of the processor
    protected ThreadLocal<ParserState> state = new ThreadLocal<ParserState>();

    @Override
    public void process(Exchange exchange) throws Exception {
        prepareState(exchange);

        InputStream input = null;
        try {
            ZipFileEntry fe = exchange.getIn().getMandatoryBody(ZipFileEntry.class);
            input = exchange.getIn().getMandatoryBody(InputStream.class);

            Resource xsdSchema = xsdSelector.provideXsdResource(fe.getFileEntry());

            parse(input, xsdSchema);
        } finally {
            IOUtils.closeQuietly(input);

            sendDataBatch();

            cleanUpState();
        }
    }

    public void parse(InputStream input, Resource xsdSchema) throws IOException, XmlParseException, SAXException{
        EdfiRecordParserImpl2.parse(input, xsdSchema, typeProvider, this);
    }

    /**
     * Prepare the state for the job.
     *
     * @param exchange
     *            Exchange
     * @throws InvalidPayloadException
     */
    protected void prepareState(Exchange exchange) {
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

    @Override
    public void visit(RecordMeta recordMeta, Map<String, Object> record) {
        state.get().addToBatch(recordMeta, record);

        if (state.get().getDataBatch().size() >= batchSize) {
            sendDataBatch();
        }
    }

    public void sendDataBatch() {
        ParserState s = state.get();

        if (s.getDataBatch().size() > 0) {
            producer.sendBodyAndHeaders(s.getDataBatch(), s.getOriginalExchange().getIn().getHeaders());

            s.resetDataBatch();
        }
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
        private List<NeutralRecord> dataBatch = new ArrayList<NeutralRecord>();

        public Exchange getOriginalExchange() {
            return originalExchange;
        }

        public void setOriginalExchange(Exchange originalExchange) {
            this.originalExchange = originalExchange;
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

}
