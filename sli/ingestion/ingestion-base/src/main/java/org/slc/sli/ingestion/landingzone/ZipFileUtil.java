package org.slc.sli.ingestion.landingzone;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Zip File utility class.
 *
 */
public class ZipFileUtil {

    static Logger log = LoggerFactory.getLogger(ZipFileUtil.class);

    static final int BUFFER = 2048;

    public static File extract(File zipFile) throws IOException {

        Date date = new Date();
        Timestamp time = new Timestamp(date.getTime());

        String filePath = zipFile.getParentFile().getAbsolutePath() + File.separator + "unzip" + File.separator
                + zipFile.getName().substring(0, zipFile.getName().lastIndexOf(".")) + time.getTime();

        // make dir to unzip files
        File path = new File(filePath);

        if (!path.mkdirs()) {
            throw new IOException("directory creation failed: " + filePath);
        }

        ZipFile zip = new ZipFile(zipFile);

        try {
            Enumeration<ZipArchiveEntry> entries = zip.getEntries();

            // Extract files
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();

                if (!entry.isDirectory()) {
                    extractTo(zip, entry, path);
                }
            }
        } finally {
            ZipFile.closeQuietly(zip);
        }

        return path;
    }

    /**
     * Extracts a Zip Entry from an archive to a directory
     *
     * @param zip
     *            Archive
     * @param entry
     *            Zip Entry
     * @param dir
     *            Directory to extract file to
     * @throws IOException
     *             in case of error
     */
    protected static void extractTo(ZipFile zip, ZipArchiveEntry entry, File dir) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(dir.getAbsolutePath() + File.separator + entry.getName(), "rw");
            raf.setLength(entry.getSize());

            raf.getChannel().transferFrom(Channels.newChannel(zip.getInputStream(entry)), 0, entry.getSize());
        } finally {
            IOUtils.closeQuietly(raf);
        }

    }

    public static File findCtlFile(File dir) throws IOException {

        FilenameFilter filter = new FilenameFilter() {
            @Override
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
