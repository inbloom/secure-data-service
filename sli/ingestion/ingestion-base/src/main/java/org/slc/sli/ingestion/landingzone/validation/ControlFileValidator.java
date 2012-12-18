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

package org.slc.sli.ingestion.landingzone.validation;

import java.io.File;
import java.util.List;

import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.landingzone.ControlFileDescriptor;
import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.BaseMessageCode;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 * Control File validator.
 *
 * @author okrook
 *
 */
public class ControlFileValidator extends SimpleValidatorSpring<ControlFileDescriptor> {

    private List<IngestionFileValidator> ingestionFileValidators;

    @Override
    public boolean isValid(ControlFileDescriptor item, ErrorReport callback) {
        ControlFile controlFile = item.getFileItem();

        List<IngestionFileEntry> entries = controlFile.getFileEntries();

        if (entries.size() < 1) {
            fail(callback, getFailureMessage("BASE_0003"));
            return false;
        }

        boolean isValid = true;
        for (IngestionFileEntry entry : entries) {

            if (hasPathInName(entry.getFileName())) {
                fail(callback, getFailureMessage("BASE_0004", entry.getFileName()));
                isValid = false;
            } else {

                File file = item.getLandingZone().getFile(entry.getFileName());
                if (file == null) {
                    fail(callback, getFailureMessage("BASE_0001", entry.getFileName()));
                    isValid = false;
                } else {
                    entry.setFile(file);

                    if (!isValid(new FileEntryDescriptor(entry, item.getLandingZone()), callback)) {
                        // remove the file from the entry since it did not pass the validation
                        entry.setFile(null);
                        isValid = false;
                    }
                }
            }
        }
        // If all the entries failed and we haven't logged an error yet
        // then this is a case of 'no valid files in control file'
        // (i.e., SL_ERR_MSG8)
        if (!isValid && !callback.hasErrors()) {
            fail(callback, getFailureMessage("BASE_0002"));
            return false;
        }

        return isValid;
    }

    protected boolean isValid(FileEntryDescriptor item, ErrorReport callback) {
        for (IngestionFileValidator validator : ingestionFileValidators) {
            if (!validator.isValid(item, callback)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasPathInName(String fileName) {
        return (fileName.contains(File.separator) || fileName.contains("/"));
    }

    public List<IngestionFileValidator> getIngestionFileValidators() {
        return ingestionFileValidators;
    }

    public void setIngestionFileValidators(List<IngestionFileValidator> ingestionFileValidators) {
        this.ingestionFileValidators = ingestionFileValidators;
    }

    @Override
    public boolean isValid(ControlFileDescriptor item, AbstractMessageReport report, AbstractReportStats reportStats) {
        ControlFile controlFile = item.getFileItem();

        List<IngestionFileEntry> entries = controlFile.getFileEntries();

        if (entries.size() < 1) {

            error(report, reportStats, BaseMessageCode.BASE_0003);

            return false;
        }

        boolean isValid = true;
        for (IngestionFileEntry entry : entries) {

            if (hasPathInName(entry.getFileName())) {
                error(report, reportStats, BaseMessageCode.BASE_0004, entry.getFileName());
                isValid = false;
            } else {

                File file = item.getLandingZone().getFile(entry.getFileName());
                if (file == null) {
                    error(report, reportStats, BaseMessageCode.BASE_0001, entry.getFileName());
                    isValid = false;
                } else {
                    entry.setFile(file);

                    if (!isValid(new FileEntryDescriptor(entry, item.getLandingZone()), report, reportStats)) {
                        // remove the file from the entry since it did not pass the validation
                        entry.setFile(null);
                        isValid = false;
                    }
                }
            }
        }
        // If all the entries failed and we haven't logged an error yet
        // then this is a case of 'no valid files in control file'
        // (i.e., SL_ERR_MSG8)
        if (!isValid && !reportStats.hasErrors()) {
            error(report, reportStats, BaseMessageCode.BASE_0002);
            return false;
        }

        return isValid;
    }

    protected boolean isValid(FileEntryDescriptor item, AbstractMessageReport report, AbstractReportStats reportStats) {
        for (IngestionFileValidator validator : ingestionFileValidators) {
            if (!validator.isValid(item, report, reportStats)) {
                return false;
            }
        }

        return true;
    }

}
