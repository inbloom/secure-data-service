package org.slc.sli.ingestion.hdfs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Hadoop Distributed File System (HDFS) utility class
 *
 * @author ccheng
 *
 */
public class HDFSUtil {

    static final int BUFFER = 2048;

    public static void addFile(File srcFile, String destPath) throws IOException {
        Configuration config = new Configuration();

        // Read the configurations from XML file
        // config.addResource(new Path(config.xml));

        FileSystem fileSystem = FileSystem.get(config);

        // Create directory if not exists
        if (!fileSystem.exists(new Path(destPath)))
            fileSystem.mkdirs(new Path(destPath));

        // Create the destination path including the filename.
        String destFile = destPath + "/" + srcFile.getName();

        // Check if the file already exists
        Path dest = new Path(destFile);
        if (fileSystem.exists(dest)) {
            System.out.println("File " + dest + " already exists");
            return;
        }

        // Create a new file and write data to it.
        FSDataOutputStream out = fileSystem.create(dest);
        InputStream in = new BufferedInputStream(new FileInputStream(srcFile));

        byte[] b = new byte[BUFFER];
        int numBytes = 0;
        while ((numBytes = in.read(b)) > 0) {
            out.write(b, 0, numBytes);
        }

        in.close();
        out.close();
        fileSystem.close();
    }
}
