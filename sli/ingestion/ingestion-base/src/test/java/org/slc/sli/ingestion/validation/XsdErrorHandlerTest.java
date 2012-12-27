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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.AbstractReportStats;
import org.slc.sli.ingestion.reporting.DummyMessageReport;
import org.slc.sli.ingestion.reporting.SimpleReportStats;

/**
 *
 * @author Thomas Shewchuk
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class XsdErrorHandlerTest {

    @Autowired
    private XsdErrorHandler xsdErrorHandler;

    private final AbstractReportStats reportStats = new SimpleReportStats();

    private final AbstractMessageReport report = new DummyMessageReport();

    private final SAXParseException mockedSAXParseException = Mockito.mock(SAXParseException.class);

    @Before
    public void setup() {
        xsdErrorHandler.setReportAndStats(report, reportStats);
    }

    @Test
    public void testWarning() {
        // Test receiving a SAX warning.
        when(mockedSAXParseException.getMessage()).thenReturn("SAXParseException warning");
        xsdErrorHandler.warning(mockedSAXParseException);
        assertTrue(reportStats.hasWarnings());
    }

    @Test
    public void testError() {
        // Test receiving a SAX error.
        when(mockedSAXParseException.getMessage()).thenReturn("SAXParseException error");
        xsdErrorHandler.error(mockedSAXParseException);
        assertTrue(reportStats.hasWarnings());
    }

    @Test
    public void testFatalError() throws SAXException {
        // Test receiving a SAX fatal error.
        when(mockedSAXParseException.getMessage()).thenReturn("SAXParseException fatal error");
        try {
            xsdErrorHandler.fatalError(mockedSAXParseException);
        } catch (SAXException e) {
            // This is expected.
            assertNotNull(e);
        }
        assertTrue(reportStats.hasWarnings());
    }

}
