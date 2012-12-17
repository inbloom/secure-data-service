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

package org.slc.sli.ingestion.validation;

import java.io.File;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.BaseMessageCode;
import org.slc.sli.ingestion.reporting.ReportStats;

/**
 *
 * @author npandey
 *
 */
public class XsdErrorHandler implements XsdErrorHandlerInterface {

    private AbstractMessageReport report;

    private ReportStats reportStats;

    /**
     * Report a SAX parsing warning.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void warning(SAXParseException ex) {

        // TODO: remove after migrating to new calls
        reportWarning(ex);
    }

    /**
     * Report a SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     */
    @Override
    public void error(SAXParseException ex) {

        reportWarning(ex);
    }

    /**
     * Report a fatal SAX parsing error.
     *
     * @param ex
     *            Parser exception thrown by SAX
     * @throws SAXParseException
     *             Parser exception thrown by SAX
     */
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {

        // TODO: remove after migrating to new calls
        reportWarning(ex);

        throw ex;
    }

    /**
     * Incorporate the SAX error message into an ingestion error message.
     *
     * @return Error message returned by Ingestion
     */
    private void reportWarning(SAXParseException ex) {
        if (report != null) {

            // Create an ingestion error message incorporating the SAXParseException information.
            String fullParsefilePathname = (ex.getSystemId() == null) ? "" : ex.getSystemId();
            File parseFile = new File(fullParsefilePathname);

            report.warning(reportStats, BaseMessageCode.XSD_VALIDATION_ERROR, parseFile.getName(),
                    String.valueOf(ex.getLineNumber()), String.valueOf(ex.getColumnNumber()), ex.getMessage());
        }
    }

    @Override
    public void setReportAndStats(AbstractMessageReport report, ReportStats reportStats) {
        this.report = report;
        this.reportStats = reportStats;
    }
}
