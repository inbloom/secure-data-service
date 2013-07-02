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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.ZipFileUtil;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File Presence validator.
 *
 * Performs a test of the presence and that does not contain a folder in the path.
 *
 *
 */
public class FilePresenceValidator implements Validator<IngestionFileEntry> {

    private static final String STAGE_NAME = "File Presence Validation";

    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report, ReportStats reportStats,
            Source source, Map<String, Object> parameters) {

        boolean valid = true;

        if (hasPathInName(entry.getFileName())) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), entry), BaseMessageCode.BASE_0004, entry.getFileName());
            valid = false;
        } else if (!isInZipFile(entry.getFileName(), parameters)) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), entry), BaseMessageCode.BASE_0001, entry.getFileName());
            valid = false;
        }

        return valid;
    }

    private static boolean hasPathInName(String fileName) {
        return (fileName.contains(File.separator) || fileName.contains("/"));
    }

    public boolean isInZipFile(String fileName, Map<String, Object> parameters) {
        Set<String> zipFileEntries = null;

        if (parameters != null) {
            zipFileEntries = (Set<String>) parameters.get(ControlFileValidator.ZIPFILE_ENTRIES);
        }

        return ZipFileUtil.isInZipFileEntries(fileName, zipFileEntries);
    }
    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
