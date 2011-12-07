package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.IOException;

public interface LandingZone {

    /**
     * Checks for a file in the landing zone with this name, and if found,
     * returns a java.io.File for it. Returns null if no file with the name
     * can be found.
     *
     * @return File object
     */
    public File getFile(String fileName);

    /**
     * @return md5Hex string for the given File object
     * @throws IOException
     */
    public String getMd5Hex(File file) throws IOException;

}
