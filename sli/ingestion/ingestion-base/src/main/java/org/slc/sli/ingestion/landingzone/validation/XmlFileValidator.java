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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.XmlFileSource;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Validator for EdFi xml ingestion files.
 *
 * @author dduran
 *
 */
public class XmlFileValidator implements Validator<IngestionFileEntry> {

    private static final String STAGE_NAME = "XML File Validation";

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry entry, AbstractMessageReport report, ReportStats reportStats,
            Source source) {
        LOG.debug("validating xml...");

        if (isEmptyOrUnreadable(entry, report, reportStats, source)) {
            return false;
        }

        return true;
    }

    private boolean isEmptyOrUnreadable(IngestionFileEntry entry, AbstractMessageReport report,
            ReportStats reportStats, Source source) {
        boolean isEmpty = false;

        InputStream is = null;
        try {
            is = entry.getFileStream();

            if (is.read() == -1) {
                report.error(reportStats, new XmlFileSource(entry), BaseMessageCode.BASE_0015, entry.getFileName());
                isEmpty = true;
            }
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + entry.getFileName(), e);
            report.error(e, reportStats, new XmlFileSource(entry), BaseMessageCode.BASE_0013, entry.getFileName());
            isEmpty = true;
        } catch (IOException e) {
            LOG.error("Problem reading file: " + entry.getFileName());
            report.error(e, reportStats, new XmlFileSource(entry), BaseMessageCode.BASE_0014, entry.getFileName());
            isEmpty = true;
        } finally {
            IOUtils.closeQuietly(is);
        }

        return isEmpty;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
