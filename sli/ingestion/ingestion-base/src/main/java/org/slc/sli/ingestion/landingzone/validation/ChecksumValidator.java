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
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.ControlFileSource;
import org.slc.sli.ingestion.validation.Validator;

/**
 * Validates file's checksum using MD5 algorithm.
 *
 * @author okrook
 *
 */
public class ChecksumValidator implements Validator<IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(ChecksumValidator.class);
    private static final String STAGE_NAME = "File Checksum Validation";

    @Override
    public boolean isValid(IngestionFileEntry fe, AbstractMessageReport report, ReportStats reportStats,
            Source source) {

        if (StringUtils.isBlank(fe.getChecksum())) {
            report.error(reportStats, new ControlFileSource(source.getResourceId(), fe), BaseMessageCode.BASE_0007, fe.getFileName());

            return false;
        }

        String actualMd5Hex;

        InputStream is = null;

        try {
            is = fe.getFileStream();

            actualMd5Hex = DigestUtils.md5Hex(is);
        } catch (IOException e) {
            actualMd5Hex = null;
        } finally {
            IOUtils.closeQuietly(is);
        }

        if (!checksumsMatch(actualMd5Hex, fe.getChecksum())) {

            String[] args = { fe.getFileName(), actualMd5Hex, fe.getChecksum() };
            LOG.debug("File [{}] checksum ({}) does not match control file checksum ({}).", args);

            report.error(reportStats, new ControlFileSource(source.getResourceId(), fe), BaseMessageCode.BASE_0006, fe.getFileName());

            return false;
        }

        return true;
    }

    protected boolean checksumsMatch(String actualMd5Hex, String recordedMd5Hex) {
        return !StringUtils.isBlank(actualMd5Hex) && actualMd5Hex.equalsIgnoreCase(recordedMd5Hex);
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }

}
