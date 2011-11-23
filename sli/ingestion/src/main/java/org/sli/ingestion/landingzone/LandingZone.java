package org.sli.ingestion.landingzone;

import java.io.File;
import java.io.IOException;

interface LandingZone {

	/**
	 * @return File object for the given fileName
	 */
	public File getFile(String fileName);

	/**
	 * @return md5Hex string for the given File object
	 * @throws IOException 
	 */
	public String getMd5Hex(File file) throws IOException;

}