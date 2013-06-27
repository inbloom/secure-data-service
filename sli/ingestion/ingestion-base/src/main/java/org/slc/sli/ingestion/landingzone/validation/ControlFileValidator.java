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
import java.util.*;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slc.sli.ingestion.landingzone.ControlFile;
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
 * Control File validator.
 *
 * @author okrook
 *
 */
public class ControlFileValidator implements Validator<ControlFile> {

    private static final Logger LOG = LoggerFactory.getLogger(ControlFileValidator.class);

    private static final String STAGE_NAME = "Control File Validation";

    public static final String ZIPFILE_ENTRIES = "zipFileEntries";

    private List<Validator<IngestionFileEntry>> ingestionFileValidators;

    public List<Validator<IngestionFileEntry>> getIngestionFileValidators() {
        return ingestionFileValidators;
    }

    public void setIngestionFileValidators(List<Validator<IngestionFileEntry>> ingestionFileValidators) {
        this.ingestionFileValidators = ingestionFileValidators;
    }

    @Override
    public boolean isValid(ControlFile controlFile, AbstractMessageReport report, ReportStats reportStats,
            Source source, Map<String, Object> parameters) {
        Set<String> zipFileEntries = null;
        Map<String, Object> localParameters = (parameters == null) ? new HashMap<String, Object>() : new HashMap<String, Object>(parameters);

        // we know more of our source
        List<IngestionFileEntry> entries = controlFile.getFileEntries();

        if (entries.size() < 1) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), controlFile), BaseMessageCode.BASE_0003);
            return false;
        }

        try {
            zipFileEntries = ZipFileUtil.getZipFileEntries(controlFile.getParentZipFileOrDirectory());
        } catch (IOException e) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), controlFile), BaseMessageCode.BASE_0020, controlFile.getParentZipFileOrDirectory());
            return false;
        }

        if (zipFileEntries != null) {
            localParameters.put(ZIPFILE_ENTRIES, zipFileEntries);
        }

        boolean isValid = true;
        for (IngestionFileEntry entry : entries) {
            if (!isValid(entry, report, reportStats, source, localParameters)) {
                // remove the file from the entry since it did not pass the validation
                entry.setValid(false);
                isValid = false;
            }
        }

        // If all the entries failed and we haven't logged an error yet
        // then this is a case of 'no valid files in control file'
        // (i.e., SL_ERR_MSG8)
        if (!isValid && !reportStats.hasErrors()) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), controlFile), BaseMessageCode.BASE_0002);
            return false;
        }

        return isValid;
    }

    protected boolean isValid(IngestionFileEntry item, AbstractMessageReport report, ReportStats reportStats,
            Source source, Map<String, Object> parameters) {
        for (Validator<IngestionFileEntry> validator : ingestionFileValidators) {
            if (!validator.isValid(item, report, reportStats, source, parameters)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
