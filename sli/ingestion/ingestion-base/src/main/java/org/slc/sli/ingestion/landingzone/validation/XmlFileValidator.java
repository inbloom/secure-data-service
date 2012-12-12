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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.spring.SimpleValidatorSpring;

/**
 * Validator for EdFi xml ingestion files.
 *
 * @author dduran
 *
 */
public class XmlFileValidator extends SimpleValidatorSpring<IngestionFileEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(XmlFileValidator.class);

    @Override
    public boolean isValid(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        LOG.debug("validating xml...");

        if (isEmptyOrUnreadable(fileEntry, errorReport)) {
            return false;
        }

        return true;
    }

    private boolean isEmptyOrUnreadable(IngestionFileEntry fileEntry, ErrorReport errorReport) {
        boolean isEmpty = false;
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fileEntry.getFile()));
            if (br.read() == -1) {
                errorReport.fatal(getFailureMessage("SL_ERR_MSG13", fileEntry.getFileName()), XmlFileValidator.class);
                isEmpty = true;
            }
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + fileEntry.getFileName(), e);
            errorReport.error(getFailureMessage("SL_ERR_MSG11", fileEntry.getFileName()), XmlFileValidator.class);
            isEmpty = true;
        } catch (IOException e) {
            LOG.error("Problem reading file: " + fileEntry.getFileName());
            errorReport.error(getFailureMessage("SL_ERR_MSG12", fileEntry.getFileName()), XmlFileValidator.class);
            isEmpty = true;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                LogUtil.error(LOG, "Error closing buffered reader", e);
            }
        }

        return isEmpty;
    }

    @Override
    public boolean isValid(IngestionFileEntry object, AbstractMessageReport report, ReportStats reportStats) {
        // TODO Auto-generated method stub
        return false;
    }

}
