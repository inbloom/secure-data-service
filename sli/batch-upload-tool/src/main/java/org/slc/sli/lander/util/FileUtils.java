package org.slc.sli.lander.util;

import java.io.File;

public class FileUtils {

    public static File getLandableDataFile(File localDataDir) {
        // TODO get this working
        return localDataDir;
    }

    public static File getLandableDataFile(String localDataDir) {
        return FileUtils.getLandableDataFile(new File(localDataDir));
    }
}
