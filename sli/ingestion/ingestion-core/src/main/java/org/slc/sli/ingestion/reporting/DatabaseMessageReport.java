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

package org.slc.sli.ingestion.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.BatchJobUtils2;

/**
 * Message report that persists errors and warnings to a database.
 *
 * @author dduran
 *
 */
@Component
public class DatabaseMessageReport extends AbstractMessageReport {

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void error(ReportStats reportStats, MessageCode code, Object... args) {

        if (reportStats != null) {
            reportStats.incError();
        }

        error(reportStats.getSource(), code, args);
    }

    private void error(Source source, MessageCode code, Object... args) {

        String message = getMessage(code, args);

        String recordIdentifier = null;
        Error error = Error.createIngestionError(source.getBatchJobId(), source.getResourceId(), source.getStageName(),
                BatchJobUtils2.getHostName(), BatchJobUtils2.getHostAddress(), recordIdentifier,
                FaultType.TYPE_ERROR.getName(), FaultType.TYPE_ERROR.getName(), message);

        batchJobDAO.saveError(error);
    }

    @Override
    public void warning(ReportStats reportStats, MessageCode code, Object... args) {

        if (reportStats != null) {
            reportStats.incWarning();
        }

        warning(reportStats.getSource(), code, args);
    }

    private void warning(Source source, MessageCode code, Object... args) {

        String message = getMessage(code, args);

        String recordIdentifier = null;
        Error warning = Error.createIngestionError(source.getBatchJobId(), source.getResourceId(), source.getStageName(),
                BatchJobUtils2.getHostName(), BatchJobUtils2.getHostAddress(), recordIdentifier,
                FaultType.TYPE_WARNING.getName(), FaultType.TYPE_WARNING.getName(), message);

        batchJobDAO.saveError(warning);
    }

}
