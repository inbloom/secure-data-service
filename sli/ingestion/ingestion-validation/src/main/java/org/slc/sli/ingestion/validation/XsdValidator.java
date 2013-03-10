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

package org.slc.sli.ingestion.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.parser.impl.EdfiRecordValidator;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.util.XsdSelector;
import org.springframework.core.io.Resource;

/**
 * Validates the xml file against an xsd. Returns false if there is any error else it will always
 * return true. The error messages would be reported by the error handler.
 *
 * @author ablum
 *
 */
public class XsdValidator implements Validator<IngestionFileEntry> {
    private static final Logger LOG = LoggerFactory.getLogger(XsdValidator.class);
    
    private static final String STAGE_NAME = "XSD Validation";
    
    private XsdSelector xsdSelector;
    
    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report,
            ReportStats reportStats, Source source) {

        InputStream input = null;
        try {
            input = entry.getFileStream();
            Resource xsdSchema = xsdSelector.provideXsdResource(entry);

            EdfiRecordValidator.validate(input, xsdSchema, report, reportStats, source);
            
            return !reportStats.hasErrors();
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + entry.getFileName(), e);
        } catch (IOException e) {
            LOG.error("Problem reading file: " + entry.getFileName(), e);
        } catch (SAXException e) {
            LOG.error("SAXException");
        } catch (Exception e) {
            LOG.error("Error processing file " + entry.getFileName(), e);
        } finally {
            IOUtils.closeQuietly(input);
        }

        return false;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

    public XsdSelector getXsdSelector() {
        return xsdSelector;
    }

    public void setXsdSelector(XsdSelector xsdSelector) {
        this.xsdSelector = xsdSelector;
    }

}
