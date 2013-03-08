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
package org.slc.sli.ingestion.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.ValidatorHandler;

import org.slc.sli.ingestion.parser.XmlParseException;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ElementSource;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ElementSourceImpl;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A reader delegate that will intercept an XML Validator's calls to nextEvent() and build the
 * document into a Map of Maps data structure.
 *
 * Additionally, the class implements ErrorHandler so
 * that the parsing of a specific entity can be aware of validation errors.
 *
 * @author dduran
 *
 */
public class EdfiRecordValidator extends DefaultHandler {

    protected AbstractMessageReport messageReport;

    protected ReportStats reportStats;

    protected Source source;

    public static void validate(InputStream input, Resource schemaResource, AbstractMessageReport messageReport, ReportStats reportStats, Source source)
                    throws SAXException, IOException, XmlParseException {

        EdfiRecordValidator validator = new EdfiRecordValidator(messageReport, reportStats, source);

        validator.process(input, schemaResource);
    }

    public void process(InputStream input, Resource schemaResource)
            throws SAXException, IOException, XmlParseException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Schema schema = schemaFactory.newSchema(schemaResource.getURL());

        this.parseAndValidate(input, schema.newValidatorHandler());
    }

    public EdfiRecordValidator(AbstractMessageReport messageReport, ReportStats reportStats,
            Source source) {

        this.messageReport = messageReport;
        this.reportStats = reportStats;
        this.source = source;

    }

    protected void parseAndValidate(InputStream input, ValidatorHandler vHandler) throws XmlParseException, IOException {
        vHandler.setErrorHandler(this);

        InputSource is = new InputSource(new InputStreamReader(input, "UTF-8"));
        is.setEncoding("UTF-8");

        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(vHandler);
            parser.setErrorHandler(this);

            vHandler.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);

            parser.setFeature("http://apache.org/xml/features/validation/id-idref-checking", false);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            parser.setFeature("http://xml.org/sax/features/external-general-entities", false);
            parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            parser.parse(is);
        } catch (SAXException e) {
            throw new XmlParseException(e.getMessage(), e);
        }
    }

    @Override
    public void warning(final SAXParseException ex) throws SAXException {
        Source elementSource = new ElementSourceImpl(new ElementSource() {

            @Override
            public String getResourceId() {
                return source.getResourceId();
            }

            @Override
            public int getVisitBeforeLineNumber() {
                return ex.getLineNumber();
            }

            @Override
            public int getVisitBeforeColumnNumber() {
                return ex.getColumnNumber();
            }

            @Override
            public String getElementType() {
                return source.getResourceId();
            }
        });

        messageReport.warning(reportStats, elementSource, BaseMessageCode.BASE_0026, source.getResourceId(), ex.getMessage());
    }

    @Override
    public void error(final SAXParseException ex) throws SAXException {
        Source elementSource = new ElementSourceImpl(new ElementSource() {

            @Override
            public String getResourceId() {
                return source.getResourceId();
            }

            @Override
            public int getVisitBeforeLineNumber() {
                return ex.getLineNumber();
            }

            @Override
            public int getVisitBeforeColumnNumber() {
                return ex.getColumnNumber();
            }

            @Override
            public String getElementType() {
                return source.getResourceId();
            }
        });

        messageReport.error(reportStats, elementSource, BaseMessageCode.BASE_0027, source.getResourceId(), ex.getMessage());
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        error(exception);

    }
}
