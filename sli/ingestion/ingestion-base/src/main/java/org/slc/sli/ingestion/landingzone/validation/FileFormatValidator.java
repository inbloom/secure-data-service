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

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.validation.Validator;

/**
 * File format validator.
 */
public class FileFormatValidator implements Validator<IngestionFileEntry> {

    private static final String STAGE_NAME = "File Format Validation";

    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report, ReportStats reportStats,
            Source source) {
        FileFormat format = entry.getFileFormat();
        if (format == null) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), entry), BaseMessageCode.BASE_0005, entry.getFileName());

            return false;
        }
        return true;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
