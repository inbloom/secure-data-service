package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;

public class ControlFileTest {

	@Test
	public void testParseFile() throws IOException {

		String sep = System.getProperty("line.separator");
		String content = 
			"@hello=world" + sep +
			" " + sep +
			"csv,Student,the-students.csv,95b3b66973da25541e7939753b1abf04" + sep +
			"edfi-xml,StudentEnrollment,data.xml,756a5e96e330082424b83902908b070a" + sep;
		
		File tmpFile = File.createTempFile("test",".ctl");
		tmpFile.deleteOnExit();
		FileUtils.writeStringToFile(tmpFile, content);
		
		ControlFile controlFile = ControlFile.parse(tmpFile);
		
		ArrayList<ControlFile.FileEntry> items = 
				(ArrayList<ControlFile.FileEntry>) controlFile.getFileEntries();
		
		assertEquals(items.size(), 2);
		
		assertEquals(items.get(0).fileFormat, FileFormat.CSV);
		assertEquals(items.get(0).fileType, FileType.CSV_STUDENT);
		assertEquals(items.get(0).fileName, "the-students.csv");
		assertEquals(items.get(0).checksum, "95b3b66973da25541e7939753b1abf04");
		
		assertEquals(items.get(1).fileFormat, FileFormat.EDFI_XML);
		assertEquals(items.get(1).fileType, FileType.XML_STUDENT_ENROLLMENT);
		assertEquals(items.get(1).fileName, "data.xml");
		assertEquals(items.get(1).checksum, "756a5e96e330082424b83902908b070a");

		String[] configPropNames = new String[1];
		Enumeration<?> e = controlFile.configProperties.propertyNames();
		int i = 0;
		while (e.hasMoreElements()) {
			configPropNames[i++] = e.nextElement().toString();
		}
		String[] expectedNames = {"hello", };
		assertArrayEquals(expectedNames, configPropNames);
	}

}
