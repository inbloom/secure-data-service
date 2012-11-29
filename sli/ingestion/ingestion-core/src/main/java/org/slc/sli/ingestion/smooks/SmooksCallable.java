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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.delivery.ContentHandlerConfigMapTable;
import org.milyn.delivery.VisitorConfigMap;
import org.milyn.delivery.sax.SAXVisitAfter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slc.sli.common.util.tenantdb.TenantContext;
import org.xml.sax.SAXException;

import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * The Smooks of the future..
 *
 * @author dduran
 *
 */
public class SmooksCallable implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(SmooksCallable.class);

    private SliSmooksFactory sliSmooksFactory;

    private final NewBatchJob newBatchJob;
    private final IngestionFileEntry fe;
    private final Stage stage;
    private final BatchJobDAO batchJobDAO;

    public SmooksCallable(NewBatchJob newBatchJob, IngestionFileEntry fe, Stage stage, BatchJobDAO batchJobDAO,
            SliSmooksFactory sliSmooksFactory) {
        this.newBatchJob = newBatchJob;
        this.fe = fe;
        this.stage = stage;
        this.batchJobDAO = batchJobDAO;
        this.sliSmooksFactory = sliSmooksFactory;
    }

    @Override
    public Boolean call() throws Exception {
        return runSmooksFuture();
    }

    public boolean runSmooksFuture() {
        TenantContext.setJobId(newBatchJob.getId());
        TenantContext.setTenantId(newBatchJob.getTenantId());

        LOG.info("Starting SmooksCallable for: " + fe.getFileName());
        Metrics metrics = Metrics.newInstance(fe.getFileName());
        stage.addMetrics(metrics);

        FileProcessStatus fileProcessStatus = new FileProcessStatus();
        ErrorReport errorReport = fe.getErrorReport();

        // actually do the processing
        processFileEntry(fe, errorReport, fileProcessStatus);

        metrics.setDuplicateCounts(fileProcessStatus.getDuplicateCounts());

        int errorCount = processMetrics(metrics, fileProcessStatus);

        LOG.info("Finished SmooksCallable for: " + fe.getFileName());

        TenantContext.setJobId(null);
        TenantContext.setTenantId(null);
        return (errorCount > 0);
    }

    public void processFileEntry(IngestionFileEntry fe, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {

        if (fe.getFileType() != null) {
            FileFormat fileFormat = fe.getFileType().getFileFormat();
            if (fileFormat == FileFormat.EDFI_XML) {

                doHandling(fe, errorReport, fileProcessStatus);

            } else {
                throw new IllegalArgumentException("Unsupported file format: " + fe.getFileType().getFileFormat());
            }
        } else {
            throw new IllegalArgumentException("FileType was not provided.");
        }
    }

    private void doHandling(IngestionFileEntry fileEntry, ErrorReport errorReport, FileProcessStatus fileProcessStatus) {
        try {

            generateNeutralRecord(fileEntry, errorReport, fileProcessStatus);

        } catch (IOException e) {
            LogUtil.error(LOG,
                    "Error generating neutral record: Could not instantiate smooks, unable to read configuration file",
                    e);
            errorReport.fatal("Could not instantiate smooks, unable to read configuration file.",
                    SmooksCallable.class);
        } catch (SAXException e) {
            LogUtil.error(LOG, "Could not instantiate smooks, problem parsing configuration file", e);
            errorReport.fatal("Could not instantiate smooks, problem parsing configuration file.",
                    SmooksCallable.class);
        }
    }

    void generateNeutralRecord(IngestionFileEntry ingestionFileEntry, ErrorReport errorReport,
            FileProcessStatus fileProcessStatus) throws IOException, SAXException {

        // create instance of Smooks (with visitors already added)
        Smooks smooks = sliSmooksFactory.createInstance(ingestionFileEntry, errorReport);

        InputStream inputStream = new BufferedInputStream(new FileInputStream(ingestionFileEntry.getFile()));
        try {
            // filter fileEntry inputStream, converting into NeutralRecord entries as we go
            smooks.filterSource(new StreamSource(inputStream));

            populateRecordCountsFromSmooks(smooks, fileProcessStatus, ingestionFileEntry);

        } catch (SmooksException se) {
            LogUtil.error(LOG, "smooks exception - encountered problem with " + ingestionFileEntry.getFile().getName(),
                    se);
            errorReport.error("SmooksException encountered while filtering input.", SmooksCallable.class);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void populateRecordCountsFromSmooks(Smooks smooks, FileProcessStatus fileProcessStatus,
            IngestionFileEntry ingestionFileEntry) {
        try {
            Field f = smooks.getClass().getDeclaredField("visitorConfigMap");
            f.setAccessible(true);
            VisitorConfigMap map = (VisitorConfigMap) f.get(smooks);
            ContentHandlerConfigMapTable<SAXVisitAfter> visitAfters = map.getSaxVisitAfters();
            SmooksEdFiVisitor visitAfter = (SmooksEdFiVisitor) visitAfters.getAllMappings().get(0).getContentHandler();

            int recordsPersisted = visitAfter.getRecordsPerisisted();
            Map<String, Long> duplicateCounts = visitAfter.getDuplicateCounts();

            fileProcessStatus.setTotalRecordCount(recordsPersisted);
            fileProcessStatus.setDuplicateCounts(duplicateCounts);

            LOG.debug("Parsed and persisted {} records to staging db from file: {}.", recordsPersisted,
                    ingestionFileEntry.getFileName());

        } catch (Exception e) {
            LOG.error("Error accessing visitor list in smooks", e);
        }
    }

    private int processMetrics(Metrics metrics, FileProcessStatus fileProcessStatus) {
        metrics.setRecordCount(fileProcessStatus.getTotalRecordCount());

        int errorCount = aggregateAndLogProcessingErrors(newBatchJob.getId(), fe);
        metrics.setErrorCount(errorCount);
        return errorCount;
    }

    private int aggregateAndLogProcessingErrors(String batchJobId, IngestionFileEntry fe) {
        int errorCount = 0;
        for (Fault fault : fe.getFaultsReport().getFaults()) {
            if (fault.isError()) {
                errorCount++;
            }
            String faultMessage = fault.getMessage();
            String faultLevel = fault.isError() ? FaultType.TYPE_ERROR.getName()
                    : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";

            Error error = Error.createIngestionError(batchJobId, fe.getFileName(), stage.getStageName(), null, null,
                    null, faultLevel, faultLevel, faultMessage);
            batchJobDAO.saveError(error);
        }
        return errorCount;
    }
}
