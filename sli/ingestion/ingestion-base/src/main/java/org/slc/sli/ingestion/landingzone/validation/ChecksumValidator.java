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

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.FileEntryDescriptor;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.BaseMessageCode;
import org.slc.sli.ingestion.reporting.Source;

/**
 * Validates file's checksum using MD5 algorithm.
 *
 * @author okrook
 *
 */
public class ChecksumValidator extends IngestionFileValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ChecksumValidator.class);

    @Override
    public boolean isValid(FileEntryDescriptor item, AbstractMessageReport report, AbstractReportStats reportStats,
            Source source) {
        IngestionFileEntry fe = item.getFileItem();

        if (StringUtils.isBlank(fe.getChecksum())) {
            error(report, reportStats, source, BaseMessageCode.BASE_0007, fe.getFileName());

            return false;
        }

        String actualMd5Hex;

        try {
            // and the attributes match
            actualMd5Hex = item.getLandingZone().getMd5Hex(fe.getFile());
        } catch (IOException e) {
            actualMd5Hex = null;
        }

        if (!checksumsMatch(actualMd5Hex, fe.getChecksum())) {

            String[] args = { fe.getFileName(), actualMd5Hex, fe.getChecksum() };
            LOG.debug("File [{}] checksum ({}) does not match control file checksum ({}).", args);

            error(report, reportStats, source, BaseMessageCode.BASE_0006, fe.getFileName());

            return false;
        }

        return true;
    }

    protected boolean checksumsMatch(String actualMd5Hex, String recordedMd5Hex) {
        return !StringUtils.isBlank(actualMd5Hex) && actualMd5Hex.equalsIgnoreCase(recordedMd5Hex);
    }

}
