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

package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.SmooksException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.handler.XmlFileHandler;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.CoreMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * The Smooks of the future..
 *
 * @author dduran
 *
 */
public class SmooksCallable implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksCallable.class);

    private SliSmooksFactory sliSmooksFactory;

    private final NewBatchJob job;
    private final XmlFileHandler handler;
    private final ResourceEntry re;
    private final Stage stage;
    private final AbstractMessageReport messageReport;
    private final ReportStats reportStats;

    public SmooksCallable(NewBatchJob job, XmlFileHandler handler, ResourceEntry re,
            AbstractMessageReport messageReport, ReportStats reportStats,
            Stage stage, SliSmooksFactory sliSmooksFactory) {
        this.job = job;
        this.handler = handler;
        this.re = re;
        this.stage = stage;
        this.messageReport = messageReport;
        this.reportStats = reportStats;
        this.sliSmooksFactory = sliSmooksFactory;
    }

    @Override
    public Boolean call() throws Exception {
        return runSmooksFuture();
    }

    public boolean runSmooksFuture() {
        TenantContext.setJobId(job.getId());
        TenantContext.setTenantId(job.getTenantId());
        TenantContext.setBatchProperties(job.getBatchProperties());

        LOG.info("Starting SmooksCallable for: " + re.getResourceName());
        Metrics metrics = Metrics.newInstance(re.getResourceName());
        stage.addMetrics(metrics);

        FileProcessStatus fileProcessStatus = new FileProcessStatus();

        // actually do the processing
        processFileEntry(fileProcessStatus);

        processMetrics(metrics, fileProcessStatus);

        LOG.info("Finished SmooksCallable for: " + re.getResourceName());

        TenantContext.setJobId(null);
        TenantContext.setTenantId(null);
        TenantContext.setBatchProperties(null);
        return (reportStats.hasErrors());
    }

    public void processFileEntry(FileProcessStatus fileProcessStatus) {

        if (re.getResourceType() != null) {
            FileFormat fileFormat = FileFormat.findByCode(re.getResourceFormat());
            if (fileFormat == FileFormat.EDFI_XML) {

                doHandling(fileProcessStatus);

            } else {
                throw new IllegalArgumentException("Unsupported file format: " + FileFormat.findByCode(re.getResourceFormat()));
            }
        } else {
            throw new IllegalArgumentException("FileType was not provided.");
        }
    }

    private void doHandling(FileProcessStatus fileProcessStatus) {
        try {

            handler.handle(re, messageReport, reportStats);

            if (!reportStats.hasErrors()) {
                generateNeutralRecord(fileProcessStatus);
            }

        } catch (IOException e) {
            LogUtil.error(LOG,
                    "Error generating neutral record: Could not instantiate smooks, unable to read configuration file",
                    e);
            messageReport.error(reportStats, new FileSource(re.getResourceId()), CoreMessageCode.CORE_0053);
        } catch (SAXException e) {
            LogUtil.error(LOG, "Could not instantiate smooks, problem parsing configuration file", e);
            messageReport.error(reportStats, new FileSource(re.getResourceId()), CoreMessageCode.CORE_0054);
        }
    }

    void generateNeutralRecord(FileProcessStatus fileProcessStatus) throws IOException, SAXException {

        InputStream zais = null;

        try {
            zais = re.getFileStream();

            // create instance of Smooks (with visitors already added)
            String jobId = TenantContext.getJobId();
            SliSmooks smooks = sliSmooksFactory.createInstance(re, jobId,messageReport, reportStats);

            try {
                // filter fileEntry inputStream, converting into NeutralRecord entries as we go
                smooks.filterSource(new StreamSource(zais));

                populateRecordCountsFromSmooks(smooks, fileProcessStatus, re);

            } catch (SmooksException se) {
                LogUtil.error(LOG, "smooks exception - encountered problem with " + re.getResourceName(), se);
                messageReport.error(reportStats, new FileSource(re.getResourceId()), CoreMessageCode.CORE_0055,
                        re.getResourceName());
            }
        } finally {
            IOUtils.closeQuietly(zais);
        }
    }

    private void populateRecordCountsFromSmooks(SliSmooks smooks, FileProcessStatus fileProcessStatus,
            ResourceEntry re2) {

        SmooksEdFiVisitor edFiVisitor = smooks.getSmooksEdFiVisitor();

        int recordsPersisted = edFiVisitor.getRecordsPerisisted();
        Map<String, Long> duplicateCounts = edFiVisitor.getDuplicateCounts();

        fileProcessStatus.setTotalRecordCount(recordsPersisted);
        fileProcessStatus.setDuplicateCounts(duplicateCounts);

        LOG.debug("Parsed and persisted {} records to staging db from file: {}.", recordsPersisted,
                re2.getResourceName());
    }

    private void processMetrics(Metrics metrics, FileProcessStatus fileProcessStatus) {
        metrics.setDuplicateCounts(fileProcessStatus.getDuplicateCounts());
        metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());
        metrics.setErrorCount(reportStats.getErrorCount());
    }
}
