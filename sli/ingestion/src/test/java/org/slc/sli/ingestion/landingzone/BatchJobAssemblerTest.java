package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;

public class BatchJobAssemblerTest {

	public static final String DUMMY_DIR = 
		FilenameUtils.normalize("src/test/resources/dummylz");

	BatchJobAssembler assembler;
	
	@Before
	public void setUp() {
		LocalFileSystemLandingZone lz = new LocalFileSystemLandingZone();
		lz.setDirectory(new File(DUMMY_DIR));
		assembler = new BatchJobAssembler(lz);
	}
	
	@After
	public void tearDown() {
		assembler = null;
	}
	
	@Test
	public void testAssembleJobValid() throws IOException {

		// set up some valid entries
		ArrayList<ControlFile.FileEntry> entries = 
				new ArrayList<ControlFile.FileEntry>();
		entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, 
				FileType.XML_STUDENT, "InterchangeStudent.xml", 
				"1257ae55b836dc57d635f0733c115179"));
		entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, 
				FileType.XML_STUDENT_ENROLLMENT, "InterchangeEnrollment.xml", 
				"ab164978e7ee64ed6ddfd9c2e0716ca6"));

		// set up some valid properties
		Properties props = new Properties();
		props.setProperty("hello", "world");
		
		ControlFile ctlFile = new ControlFile(null, entries, props);

		BatchJob job = assembler.assembleJob(ctlFile);

		assertEquals(2, job.getFiles().size());
		assertEquals(0, job.getFaults().size());
		assertFalse(job.hasErrors());
		assertEquals("world", job.getProperty("hello"));
		
	}

	@Ignore
	@Test
	public void testAssembleJobWithWarnings() {
		fail("Not yet implemented");
	}

	@Test
	public void testAssembleJobWithErrors() throws IOException {

		// set up entries
		ArrayList<ControlFile.FileEntry> entries = 
				new ArrayList<ControlFile.FileEntry>();
		
		// file name deliberately incorrect
		entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, 
				FileType.XML_STUDENT, "ZInterchangeStudent.xml", 
				"1257ae55b836dc57d635f0733c115179"));
		
		// checksum deliberately incorrect
		entries.add(new ControlFile.FileEntry(FileFormat.EDFI_XML, 
				FileType.XML_STUDENT_ENROLLMENT, "InterchangeEnrollment.xml", 
				"Zab164978e7ee64ed6ddfd9c2e0716ca6"));
		
		// set up some valid properties
		Properties props = new Properties();
		props.setProperty("hello", "world");
		
		ControlFile ctlFile = new ControlFile(null, entries, props);

		BatchJob job = assembler.assembleJob(ctlFile);

		assertEquals(0, job.getFiles().size());
		assertEquals(2, job.getFaults().size());
		assertTrue(job.hasErrors());
		assertTrue(job.getFaults().get(0).getMessage().contains("not found"));
		assertTrue(job.getFaults().get(1).getMessage().contains("checksum"));
		assertEquals("world", job.getProperty("hello"));
	
	}

}
