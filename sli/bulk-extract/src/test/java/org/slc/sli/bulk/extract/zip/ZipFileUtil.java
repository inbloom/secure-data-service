package org.slc.sli.bulk.extract.zip;

import java.io.BufferedOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter; 
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipFileUtil {

    private ZipFileUtil() {}

    static Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    static final int BUFFER = 2048;

    public static File extract(File zipFile) throws IOException {

        String filePath = zipFile.getParentFile().getAbsolutePath() + File.separator + "unzip" + File.separator
                + zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));

        // make dir to unzip files
        File targetDir = new File(filePath);

        if (!targetDir.mkdirs()) {
            throw new IOException("directory creation failed: " + filePath);
        }

        extract(zipFile, targetDir, false);

        return targetDir;
    }

    /**
     * @param sourceZipFile
     * @param targetDir
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void extract(File sourceZipFile, File targetDir, boolean mkdirs) throws IOException {
        FileInputStream fis = null;
        ZipArchiveInputStream zis = null;

        try {
            // Create input stream
            fis = new FileInputStream(sourceZipFile);
            zis = new ZipArchiveInputStream(new BufferedInputStream(fis));

            ArchiveEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory()) {
                    extract(zis, entry, targetDir, mkdirs);
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * Extracts a Zip Entry from an archive to a directory
     *
     * @param zis
     *            Archive
     * @param entry
     *            Zip Entry
     * @param targetDir
     *            Directory to extract file to
     * @throws IOException
     *             in case of error
     */
    protected static void extract(ZipArchiveInputStream zis, ArchiveEntry entry, File targetDir, boolean mkdirs) throws IOException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File targetFile = new File(targetDir.getAbsolutePath() + File.separator + entry.getName());

            if (mkdirs) {
                targetFile.getParentFile().mkdirs();
            }

            fos = new FileOutputStream(targetFile);
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

    public static File findCtlFile(File dir) {

        if (!dir.isDirectory()) {
            log.info("Non-existent control file directory: " + dir.getAbsolutePath());
            return null;
        }
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // if the file extension is .ctl return true, else false
                return name.endsWith(".ctl");
            }
        };

        File ctlFile = null;

        File[] fileList = dir.listFiles(filter);
        if (fileList.length > 0) {
            ctlFile = fileList[0];
            log.info("Found control file: " + ctlFile.getName());
        } else {
            log.info("No control file found in " + dir.getAbsolutePath());
        }

        return ctlFile;
    }
}
