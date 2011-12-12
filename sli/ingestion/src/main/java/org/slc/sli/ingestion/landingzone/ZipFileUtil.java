package org.slc.sli.ingestion.landingzone;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FilenameFilter;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;

public class ZipFileUtil {

    static Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    static final int BUFFER = 2048;

    public static File extract(File zipFile) throws IOException {

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String filePath = zipFile.getParentFile().getAbsolutePath() + "/unzip/"
                + zipFile.getName().substring(0, zipFile.getName().lastIndexOf(".")) + time.getTime();

        // make dir to unzip files
        boolean result = new File(filePath).mkdirs();
        if (!result) {
            throw new IOException("directory creation failed: " + filePath);
        }
        File path = new File(filePath);

        try {
            // Create input stream
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));

            ZipEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                int count;
                byte[] data = new byte[BUFFER];

                // Create output stream
                FileOutputStream fos = new FileOutputStream(path.getAbsolutePath() + "/" + entry.getName());
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);

                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.flush();
                bos.close();
            }

            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return path;
    }

    public static File findCtlFile(File dir) throws IOException {

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                // if the file extension is .ctl return true, else false
                return name.endsWith(".ctl");
            }
        };

        File[] fileList = dir.listFiles(filter);
        File ctlFile = null;

        if (fileList.length > 0) {
            ctlFile = fileList[0];
            log.info("Found control file: " + ctlFile.getName());
        } else {
            log.info("No control file found in " + dir.getAbsolutePath());
        }

        return ctlFile;
    }
}
