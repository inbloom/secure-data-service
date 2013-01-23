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

package org.slc.sli.ingestion.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.smooks.SliSmooks;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
@Component
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, FileProcessStatus> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Override
    protected FileProcessStatus doHandling(IngestionFileEntry item, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        Source source = new FileSource(item.getResourceId());

        try {
            generateNeutralRecord(item, report, reportStats, source, fileProcessStatus);
        } catch (IOException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0016);
        } catch (SAXException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0017);
        }

        return fileProcessStatus;
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, AbstractMessageReport errorReport,
            ReportStats reportStats, Source source, FileProcessStatus fileProcessStatus) throws IOException,
            SAXException {

        // Create instance of Smooks (with visitors already added).
        SliSmooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, errorReport, reportStats);

        InputStream inputStream = null;

        try {
            inputStream = ingestionFileEntry.getFileStream();

            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));
            populateRecordCountsFromSmooks(smooks, fileProcessStatus, ingestionFileEntry);
        } catch (SmooksException se) {
            errorReport.error(reportStats, source, CoreMessageCode.CORE_0020, ingestionFileEntry.getFileName());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void populateRecordCountsFromSmooks(SliSmooks smooks, FileProcessStatus fileProcessStatus,
            IngestionFileEntry ingestionFileEntry) {

        SmooksEdFiVisitor edFiVisitor = smooks.getSmooksEdFiVisitor();

        int recordsPersisted = edFiVisitor.getRecordsPerisisted();
        Map<String, Long> duplicateCounts = edFiVisitor.getDuplicateCounts();

        fileProcessStatus.setTotalRecordCount(recordsPersisted);
        fileProcessStatus.setDuplicateCounts(duplicateCounts);

        LOG.debug("Parsed and persisted {} records to staging db from file: {}.", recordsPersisted,
                ingestionFileEntry.getFileName());
    }

    @Override
    protected List<FileProcessStatus> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // Blank instantiation of this (never-called) method.
        return null;
    }

    @Override
    public String getStageName() {
        return BatchJobStageType.EDFI_PROCESSOR.getName();
    }
}
