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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.Source;
import org.slc.sli.ingestion.reporting.impl.BaseMessageCode;
import org.slc.sli.ingestion.reporting.impl.FileSource;
import org.slc.sli.ingestion.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Zip file validator.
 *
 * @author okrook
 *
 */
public class ZipFileValidator implements Validator<File> {

    private static final Logger LOG = LoggerFactory.getLogger(ZipFileValidator.class);
    private static final String STAGE_NAME = "Zip File Validation";

    @Override
    public boolean isValid(File zipFile, AbstractMessageReport report, ReportStats reportStats, Source source, Map<String, Object> parameters) {
        boolean isValid = false;

        // we know more of our source
        LOG.info("Validating zipFile: {}", zipFile.getAbsolutePath());

        ZipFile zf = null;
        try {
            zf = new ZipFile(zipFile);

            Enumeration<ZipArchiveEntry> zes = zf.getEntries();

            while (zes.hasMoreElements()) {
                ZipArchiveEntry ze = zes.nextElement();

                LOG.debug("  ZipArchiveEntry:  name: {}, size {}", ze.getName(), ze.getSize());

                if (isDirectory(ze)) {
                    report.error(reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0010, zipFile.getName());
                    return false;
                }

                if (ze.getName().endsWith(".ctl")) {
                    isValid = true;
                }
            }

            // no manifest (.ctl file) found in the zip file
            if (!isValid) {
                report.error(reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0009, zipFile.getName());
            }
        } catch (UnsupportedZipFeatureException ex) {
            report.error(ex, reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0022, zipFile.getName());

            isValid = false;
        } catch (FileNotFoundException ex) {
            report.error(ex, reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0020, zipFile.getName());

            isValid = false;
        } catch (IOException ex) {
            LOG.warn("Caught IO exception processing " + zipFile.getAbsolutePath());

            report.error(ex, reportStats, new FileSource(zipFile.getName()), BaseMessageCode.BASE_0021, zipFile.getName());

            isValid = false;
        } finally {
            ZipFile.closeQuietly(zf);
        }

        return isValid;
    }

    private static boolean isDirectory(ArchiveEntry zipEntry) {

        if (zipEntry.isDirectory()) {
            return true;
        }

        // UN: This check is to ensure that any zipping utility which does not pack a directory
        // entry
        // is verified by checking for a filename with '/'. Example: Windows Zipping Tool.
        if (zipEntry.getName().contains("/")) {
            return true;
        }

        return false;
    }

    @Override
    public String getStageName() {
        return STAGE_NAME;
    }
}
