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

package org.slc.sli.ingestion.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;

/**
 * Validates the xml file against an xsd. Returns false if there is any error else it will always
 * return true. The error messages would be reported by the error handler.
 *
 * @author ablum
 *
 */
@Scope("prototype")
@Component
public class XsdValidator implements org.slc.sli.ingestion.validation.Validator<IngestionFileEntry> {

    private Map<String, Resource> xsd;

    @Autowired
    private XsdErrorHandlerInterface errorHandler;

    private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);

    public Map<String, Resource> getXsd() {
        return xsd;
    }

    public void setXsd(Map<String, Resource> xsd) {
        this.xsd = xsd;
    }

    @Override
    public boolean isValid(IngestionFileEntry ingestionFileEntry, AbstractMessageReport report,
            ReportStats reportStats, org.slc.sli.ingestion.reporting.Source source) {
        errorHandler.setReportAndStats(report, reportStats);

        InputStream is = null;
        try {

            is = validateXmlFile(ingestionFileEntry);

            return true;

        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + ingestionFileEntry.getFileName(), e);
            report.error(reportStats, source, BaseMessageCode.BASE_0013, ingestionFileEntry.getFileName());
        } catch (IOException e) {
            LOG.error("Problem reading file: " + ingestionFileEntry.getFileName(), e);
            report.error(reportStats, source, BaseMessageCode.BASE_0014, ingestionFileEntry.getFileName());
        } catch (SAXException e) {
            LOG.error("SAXException");
        } catch (Exception e) {
            LOG.error("Error processing file " + ingestionFileEntry.getFileName(), e);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return false;
    }

    private InputStream validateXmlFile(IngestionFileEntry ingestionFileEntry) throws SAXException, IOException {

        File xmlFile = ingestionFileEntry.getFile();
        if (xmlFile == null) {
            throw new FileNotFoundException();
        }

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Resource xsdResource = xsd.get(ingestionFileEntry.getFileType().getName());
        Schema schema = schemaFactory.newSchema(xsdResource.getURL());

        String sourceXml = ingestionFileEntry.getFile().getAbsolutePath();
        InputStream is = new FileInputStream(sourceXml);

        Validator validator = schema.newValidator();
        validator.setResourceResolver(new ExternalEntityResolver());
        validator.setErrorHandler(errorHandler);

        Source sc = new StreamSource(is, xmlFile.toURI().toASCIIString());
        validator.validate(sc);

        return is;
    }

    /**
     * Throws a runtime exception which is caught by the XsdValidatior
     * if a user attempts to pass external entities in their XML.
     *
     * @author dshaw
     *
     */
    private static final class ExternalEntityResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
                String baseURI) {
            throw new RuntimeException("Attempted disallowed ingestion of External XML Entity (XXE).");
        }
    }

}
