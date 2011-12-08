package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

public class LocalFileSystemLandingZone implements LandingZone {

	protected File directory;

	/**
	 * @return the directory
	 */
	public File getDirectory() {
		return directory;
	}
	
	/**
	 * @param directory the directory to set
	 */
	public void setDirectory(File directory) {
		this.directory = directory;
	}
	
	/**
	 * @return File object for the given fileName
	 */
	public File getFile(String fileName) {
		File f = FileUtils.getFile(this.directory, fileName);
		if (f.exists()) {
			return f;
		} else {
			return null;
		}
	}

	/**
	 * @return md5Hex string for the given File object
	 */
	public String getMd5Hex(File file) throws IOException {
		FileInputStream s = FileUtils.openInputStream(file);
		try {
			return DigestUtils.md5Hex(s);
		} finally {
			IOUtils.closeQuietly(s);
		}
	}

}