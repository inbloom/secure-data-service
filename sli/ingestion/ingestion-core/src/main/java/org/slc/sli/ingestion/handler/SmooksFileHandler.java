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
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.CoreMessageCode;
import org.slc.sli.ingestion.smooks.SliSmooks;
import org.slc.sli.ingestion.smooks.SliSmooksFactory;
import org.slc.sli.ingestion.smooks.SmooksEdFiVisitor;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * smooks handler for edfi files
 *
 * @author dduran
 *
 */
@Component
public class SmooksFileHandler extends AbstractIngestionHandler<IngestionFileEntry, IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksFileHandler.class);

    @Autowired
    private SliSmooksFactory sliSmooksFactory;

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        /*
         * try {
         *
         * //generateNeutralRecord(fileEntry, errorReport, errorReport, fileProcessStatus);
         *
         * } catch (IOException e) {
         * LOG.error("IOException: Could not instantiate smooks, unable to read configuration file",
         * e);
         * errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
         * SmooksFileHandler.class);
         * } catch (SAXException e) {
         * LOG.error("SAXException: Could not instantiate smooks, problem parsing configuration file"
         * , e);
         * errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
         * SmooksFileHandler.class);
         * }
         */
        return fileEntry;
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, AbstractMessageReport errorReport,
            AbstractReportStats reportStats, FileProcessStatus fileProcessStatus) throws IOException, SAXException {

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
            errorReport.error(reportStats, CoreMessageCode.CORE_0020, ingestionFileEntry.getFile().getName());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) {
        return null;
    }

    @Override
    protected IngestionFileEntry doHandling(IngestionFileEntry item, AbstractMessageReport report,
            AbstractReportStats reportStats, FileProcessStatus fileProcessStatus) {
        try {

            generateNeutralRecord(item, report, reportStats, fileProcessStatus);

        } catch (IOException e) {
            report.error(reportStats, CoreMessageCode.CORE_0016);
        } catch (SAXException e) {
            report.error(reportStats, CoreMessageCode.CORE_0017);
        }

        return item;
    }

    @Override
    protected List<IngestionFileEntry> doHandling(List<IngestionFileEntry> items, AbstractMessageReport report,
            AbstractReportStats reportStats, FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }
}
