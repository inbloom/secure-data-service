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

import java.io.IOException;
import java.util.List;

import org.slc.sli.ingestion.FileProcessStatus;
import org.slc.sli.ingestion.landingzone.FileResource;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;

/**
 * @author ablum
 *
 */
public class ZipFileHandler extends AbstractIngestionHandler<FileResource, String> {
    private static final String STAGE_NAME = "Zip File Extraction";

    String doHandling(FileResource zipFile, AbstractMessageReport report, ReportStats reportStats) {
        return doHandling(zipFile, report, reportStats, null);
    }

    @Override
    protected String doHandling(FileResource zipFile, AbstractMessageReport report, ReportStats reportStats,
            FileProcessStatus fileProcessStatus) {

        try {
            return ZipFileUtil.getControlFileName(zipFile);
        } catch (IOException e) {
            // we know more of our source
            report.error(e, reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0025, zipFile.getName());
        }

        return null;
    }

    @Override
    protected List<String> doHandling(List<FileResource> items, AbstractMessageReport report, ReportStats reportStats,
            FileProcessStatus fileProcessStatus) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
