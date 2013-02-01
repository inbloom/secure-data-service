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

package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import org.junit.Test;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;

/**
 * Test for ControlFile
 */
public class ControlFileTest {

    @Test
    public void testParseFile() throws IOException, SubmissionLevelException {

        String sep = System.getProperty("line.separator");
        String content = "@hello=world" + sep + " " + sep
                + "edfi-xml,StudentEnrollment,data.xml,756a5e96e330082424b83902908b070a" + sep;

        ControlFile controlFile = getControlFileFor(content);

        controlFile.parse(null);

        List<IngestionFileEntry> items = controlFile.getFileEntries();

        assertEquals(items.size(), 1);

        assertEquals(items.get(0).getFileFormat(), FileFormat.EDFI_XML);
        assertEquals(items.get(0).getFileType(), FileType.XML_STUDENT_ENROLLMENT);
        assertEquals(items.get(0).getFileName(), "data.xml");
        assertEquals(items.get(0).getChecksum(), "756a5e96e330082424b83902908b070a");

        String[] configPropNames = new String[1];
        Enumeration<?> e = controlFile.configProperties.propertyNames();
        int i = 0;
        while (e.hasMoreElements()) {
            configPropNames[i++] = e.nextElement().toString();
        }
        String[] expectedNames = { "hello", };
        assertArrayEquals(expectedNames, configPropNames);
    }

    @Test(expected = SubmissionLevelException.class)
    public void testInvalidRecordParseFile() throws IOException, SubmissionLevelException {

        String content = "edfi-xml,StudentEnrollment,data.xml756a5e96e330082424b83902908b070a";

        ControlFile controlFile = getControlFileFor(content);

        controlFile.parse(new DummyMessageReport());
    }

    @Test
    public void testEmptyParseFile() throws IOException, SubmissionLevelException {

        final String content = "";

        ControlFile controlFile = getControlFileFor(content);

        controlFile.parse(null);

        List<IngestionFileEntry> items = controlFile.getFileEntries();

        assertEquals(items.size(), 0);
    }


    private ControlFile getControlFileFor(final String content) {
        ControlFile controlFile = new ControlFile("", "") {
            private static final long serialVersionUID = 1L;

            @Override
            public InputStream getFileStream() {
                return new ByteArrayInputStream(content.getBytes());
            }
        };

        return controlFile;
    }

}
