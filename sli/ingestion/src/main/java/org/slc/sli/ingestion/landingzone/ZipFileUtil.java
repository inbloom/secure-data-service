package org.slc.sli.ingestion.landingzone;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipFileUtil {

    static Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    static final int BUFFER = 2048;

    public static File extract(File zipFile) throws IOException {

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String filePath = zipFile.getParentFile().getAbsolutePath() + File.pathSeparator + "unzip" + File.pathSeparator
                + zipFile.getName().substring(0, zipFile.getName().lastIndexOf(".")) + time.getTime();

        // make dir to unzip files
        File path = new File(filePath);

        if (!path.mkdirs()) {
            throw new IOException("directory creation failed: " + filePath);
        }

        FileInputStream fis = null;
        ZipInputStream zis = null;

        try {
            // Create input stream
            fis = new FileInputStream(zipFile);
            zis = new ZipInputStream(new BufferedInputStream(fis));

            ZipEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory()) {
                    extractTo(zis, entry, path);
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }

        return path;
    }

    /**
     * Extracts a Zip Entry from an archive to a directory
     *
     * @param zis
     *            Archive
     * @param entry
     *            Zip Entry
     * @param dir
     *            Directory to extract file to
     * @throws IOException
     *             in case of error
     */
    protected static void extractTo(ZipInputStream zis, ZipEntry entry, File dir) throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dir.getAbsolutePath() + File.pathSeparator + entry.getName());
            bos = new BufferedOutputStream(fos, BUFFER);

            int count;
            byte[] data = new byte[BUFFER];

            while ((count = zis.read(data, 0, BUFFER)) != -1) {
                bos.write(data, 0, count);
            }
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(fos);
        }

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
