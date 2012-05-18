package org.slc.sli.lander;

import java.io.File;
import java.util.Properties;

import org.slc.sli.lander.util.FileUtils;
import org.slc.sli.lander.util.PropertyUtils;
import org.slc.sli.lander.util.SftpUtils;

public class FileLander {

    /**
    * @param args
    */
    public static void main(String[] args) {
        FileLander fl = new FileLander();
        try {
            fl.intialize(args);
            fl.landData();
        } catch (Exception e) {
            System.err.println("Something happened");
            e.printStackTrace();
        }

    }

    private Properties properties = null;

    public FileLander() {

    }

    public void intialize(String[] args) throws Exception {
        properties = PropertyUtils.parseArguments(args);
        for (Object key : properties.keySet()) {
            System.out.println(key + "=" + properties.get(key));
        }
    }

    public void landData() throws Exception {
        if (properties == null) {
            return;
        }

        String localDataDir = properties.getProperty(PropertyUtils.KEY_LOCAL_DIRECTORY);
        File fileToLand = FileUtils.getLandableDataFile(localDataDir);
/*        if (DataUtils.zipIngestionData(localDataDir))
            ; //then the zip file is at localDataDir/DataUtils.ZIP_FILE_NAME
*/
        SftpUtils.landFile(fileToLand, properties);
    }
}
