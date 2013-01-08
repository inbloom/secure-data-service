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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.JobSource;

/**
 * @author ablum
 *
 */
public class ZipFileHandler extends AbstractIngestionHandler<FileResource, File> {
    private static final Logger LOG = LoggerFactory.getLogger(ZipFileHandler.class);

    private static final String STAGE_NAME = "Zip File Extraction";

    @Value("${sli.ingestion.file.timeout:600000}")
    private Long zipfileCompletionTimeout;

    @Value("${sli.ingestion.file.retryinterval:30000}")
    private Long zipfileCompletionPollInterval;

    File doHandling(FileResource zipFile, AbstractMessageReport report, ReportStats reportStats) {
        return doHandling(zipFile, report, reportStats, null);
    }

    @Override
    protected File doHandling(FileResource zipFile, AbstractMessageReport report, ReportStats reportStats,
            FileProcessStatus fileProcessStatus) {

        boolean done = false;
        long clockTimeout = System.currentTimeMillis() + zipfileCompletionTimeout;
        Source source = new JobSource(reportStats.getBatchJobId(), zipFile.getResourceId(), getStageName());

        while (!done) {

            try {
                File dir = ZipFileUtil.extract(zipFile);
                LOG.info("Extracted zip file to {}", dir.getAbsolutePath());
                done = true;

                // Find manifest (ctl file)
                File ctlFile = ZipFileUtil.findCtlFile(dir);
                if (ctlFile == null) {
                    report.error(reportStats, source, BaseMessageCode.BASE_0012);
                }
                return ctlFile;

            } catch (UnsupportedZipFeatureException ex) {
                // Unsupported compression method
                report.error(reportStats, source, BaseMessageCode.BASE_0011);
                done = true;

            } catch (FileNotFoundException ex) {
                // DE1618 Gluster may have lost track of the file, or it has been deleted from under
                // us so give up
                LOG.info(
                        zipFile.getAbsolutePath()
                                + " cannot be found. If the file was not processed by another ingestion service, please resubmit.",
                        ex);
                report.error(reportStats, source, BaseMessageCode.BASE_0008);
                done = true;

            } catch (IOException ex) {

                if (System.currentTimeMillis() >= clockTimeout) {
                    report.error(reportStats, source, BaseMessageCode.BASE_0008);
                    done = true;
                } else {
                    try {
                        LOG.info("Waiting for " + zipFile.getAbsolutePath() + "to move in handler.");
                        Thread.sleep(zipfileCompletionPollInterval);
                    } catch (InterruptedException e) {
                        // Restore the interrupted status
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected List<File> doHandling(List<FileResource> items, AbstractMessageReport report, ReportStats reportStats,
            FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
