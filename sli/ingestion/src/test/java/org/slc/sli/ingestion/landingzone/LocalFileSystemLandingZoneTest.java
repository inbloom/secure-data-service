package org.slc.sli.ingestion.landingzone;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LocalFileSystemLandingZoneTest {

	LocalFileSystemLandingZone lz;
	
	public static final String DUMMY_DIR = 
			FilenameUtils.normalize("src/test/resources/dummylz");

	public static final String DUMMY_FILE_CTL = "dummycontrolfile.txt";
	public static final String DUMMY_FILE_CTL_MD5HEX = 
			"a36379d18565cf4edd7e935255dbd3cf";
	
	public static final String DUMMY_FILE_NOT_EXISTS = "does_not_exist.txt";
	
	@Before
	public void setUp() {
		lz = new LocalFileSystemLandingZone();
		lz.setDirectory(new File(DUMMY_DIR));
	}
	
	@After
	public void tearDown() {
		lz = null;
	}
	
	@Test
	public void testGetSetDirectory() {
		lz.directory = new File(".");
		assertEquals(new File("."), lz.getDirectory());
		lz.directory = new File(DUMMY_DIR);
		assertEquals(new File(DUMMY_DIR), lz.getDirectory());
	}

	@Test
	public void testGetFile() throws IOException {
		File f = lz.getFile(DUMMY_FILE_CTL);
		assertEquals(new File(DUMMY_DIR + File.separator + DUMMY_FILE_CTL), f);
	}
	
	@Test
	public void testGetFileNotFound() throws IOException {
		File f = lz.getFile(DUMMY_FILE_NOT_EXISTS);
		assertNull(f);
	}

	@Test
	public void testGetMd5Hex() throws IOException {
		File f = lz.getFile(DUMMY_FILE_CTL);
		assertEquals(DUMMY_FILE_CTL_MD5HEX, lz.getMd5Hex(f));
	}

	@Test(expected=IOException.class)
	public void testGetMd5HexNotFound() throws IOException {
		lz.getMd5Hex(
				new File(DUMMY_DIR + File.separator + DUMMY_FILE_NOT_EXISTS));
	}

}
