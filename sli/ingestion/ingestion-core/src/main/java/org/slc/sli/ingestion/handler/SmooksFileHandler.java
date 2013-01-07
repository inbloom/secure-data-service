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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.JobSource;
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
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    private static final String STAGE_NAME = "Smooks File Parsing";

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry item, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        Source source = new JobSource(reportStats.getBatchJobId(), item.getResourceId(), getStageName());
        try {

            generateNeutralRecord(item, report, reportStats, source, fileProcessStatus);

        } catch (IOException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0016);
        } catch (SAXException e) {
            report.error(reportStats, source, CoreMessageCode.CORE_0017);
        }

        return item;
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, AbstractMessageReport errorReport,
            ReportStats reportStats, Source source, FileProcessStatus fileProcessStatus) throws IOException,
            SAXException {

        // create instance of Smooks (with visitors already added)
        SliSmooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, errorReport, reportStats);

        InputStream inputStream = new BufferedInputStream(new FileInputStream(ingestionFileEntry.getFile()));
        try {
            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));
            SmooksEdFiVisitor edFiVisitor = smooks.getSmooksEdFiVisitor();

            int recordsPersisted = edFiVisitor.getRecordsPerisisted();
            fileProcessStatus.setTotalRecordCount(recordsPersisted);

            LOG.info("Parsed and persisted {} records to staging db from file: {}.", recordsPersisted,
                    ingestionFileEntry.getFileName());
        } catch (SmooksException se) {
            errorReport.error(reportStats, source, CoreMessageCode.CORE_0020, ingestionFileEntry.getFile().getName());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            ReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }
}
