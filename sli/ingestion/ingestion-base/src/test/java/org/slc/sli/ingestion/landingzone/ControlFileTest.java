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
                + "csv,Student,the-students.csv,95b3b66973da25541e7939753b1abf04" + sep
                + "edfi-xml,StudentEnrollment,data.xml,756a5e96e330082424b83902908b070a" + sep;

        File tmpFile = File.createTempFile("test", ".ctl");
        FileUtils.writeStringToFile(tmpFile, content);

        ControlFile controlFile = ControlFile.parse(tmpFile);
        tmpFile.delete();

        ArrayList<IngestionFileEntry> items = (ArrayList<IngestionFileEntry>) controlFile.getFileEntries();

        assertEquals(items.size(), 2);

        assertEquals(items.get(0).getFileFormat(), FileFormat.CSV);
        assertEquals(items.get(0).getFileType(), FileType.CSV_STUDENT);
        assertEquals(items.get(0).getFileName(), "the-students.csv");
        assertEquals(items.get(0).getChecksum(), "95b3b66973da25541e7939753b1abf04");

        assertEquals(items.get(1).getFileFormat(), FileFormat.EDFI_XML);
        assertEquals(items.get(1).getFileType(), FileType.XML_STUDENT_ENROLLMENT);
        assertEquals(items.get(1).getFileName(), "data.xml");
        assertEquals(items.get(1).getChecksum(), "756a5e96e330082424b83902908b070a");

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
