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

package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.validation.Validator;

/**
 * File Presence validator.
 *
 * Performs a test of the presence and that does not contain a folder in the path.
 *
 *
 */
public class FilePresenceValidator implements Validator<IngestionFileEntry> {

    private static final String STAGE_NAME = "File Presense Validation";

    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report, ReportStats reportStats,
            Source source) {

        InputStream is = null;
        boolean valid = true;

        if (hasPathInName(entry.getFileName())) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), entry), BaseMessageCode.BASE_0004, entry.getFileName());
            valid = false;
        } else {
            try {
                is = entry.getFileStream();
            } catch (IOException e) {
                report.error(e, reportStats, new ControlFileSource(source.getResourceId(), entry), BaseMessageCode.BASE_0001, entry.getFileName());
                valid = false;
            } finally {
                IOUtils.closeQuietly(is);
            }
        }

        return valid;
    }

    private static boolean hasPathInName(String fileName) {
        return (fileName.contains(File.separator) || fileName.contains("/"));
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
