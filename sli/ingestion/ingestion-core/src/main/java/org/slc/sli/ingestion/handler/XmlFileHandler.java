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

package org.slc.sli.ingestion.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;

/**
 * @author tshewchuk
 *
 */
public class XmlFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    public static final Logger LOG = LoggerFactory.getLogger(XmlFileHandler.class);
    private static final String STAGE_NAME = "XML File Processing";

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // We don't really do any handling here. This is just a placeholder for the XML file
        // validators.
        return fileEntry;
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // Blank instantiation of this (never-called) method.
        return null;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }
}
