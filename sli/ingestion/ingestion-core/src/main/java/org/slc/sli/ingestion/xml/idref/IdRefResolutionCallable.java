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

package org.slc.sli.ingestion.xml.idref;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.SimpleReportStats;
import org.slc.sli.ingestion.reporting.SimpleSource;
import org.slc.sli.ingestion.reporting.Source;

/**
 * Id Reference Resolution of the future...
 *
 * @author shalka
 */
@Scope("prototype")
@Component
public class IdRefResolutionCallable implements Callable<Boolean> {

    private static final Logger LOG = LoggerFactory.getLogger(IdRefResolutionCallable.class);

    private final IdRefResolutionHandler resolver;
    private final IngestionFileEntry entry;
    private final Job job;

    private AbstractMessageReport report;

    private ReportStats reportStats;

    /**
     * Default constructor for the id reference resolution callable.
     *
     * @param fileEntry
     *            ingestion file entry.
     * @param job
     *            batch job.
     * @param handler
     *            IdRefResolutionHandler to resolve references in ingestion file entries.
     */
    public IdRefResolutionCallable(IdRefResolutionHandler resolver, IngestionFileEntry fileEntry, Job job,
            AbstractMessageReport report) {
        this.resolver = resolver;
        this.entry = fileEntry;
        this.job = job;
        this.report = report;

        Source source = new SimpleSource(job.getId(), fileEntry.getFileName(),
                BatchJobStageType.XML_FILE_PROCESSOR.getName());
        this.reportStats = new SimpleReportStats(source);
    }

    /**
     * Entry point of IdRefResolutionCallable.
     */
    @Override
    public Boolean call() throws Exception {
        TenantContext.setJobId(job.getId());

        LOG.info("Starting IdRefResolutionCallable for: " + entry.getFileName());

        resolver.handle(entry, report, reportStats);

        LOG.info("Finished IdRefResolutionCallable for: " + entry.getFileName());

        return reportStats.hasErrors();
    }
}
