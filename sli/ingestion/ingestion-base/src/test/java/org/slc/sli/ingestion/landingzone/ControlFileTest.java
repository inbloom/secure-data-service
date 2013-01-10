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

package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.landingzone.validation.SubmissionLevelException;

/**
 * Test for ControlFile
 */
public class ControlFileTest {

    @Test
    public void testParseFile() throws IOException, SubmissionLevelException {

        String sep = System.getProperty("line.separator");
        String content = "@hello=world" + sep + " " + sep
                + "edfi-xml,StudentEnrollment,data.xml,756a5e96e330082424b83902908b070a" + sep;

        File tmpFile = File.createTempFile("test", ".ctl");
        FileUtils.writeStringToFile(tmpFile, content);

        ControlFile controlFile = ControlFile.parse(tmpFile, null);
        tmpFile.delete();

        ArrayList<IngestionFileEntry> items = (ArrayList<IngestionFileEntry>) controlFile.getFileEntries();

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

}
