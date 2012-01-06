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

public class HDFSUtil {

    public void addFile(File file, String path) throws IOException {
        Configuration config = new Configuration();

        // Read the configurations from XML file
        // config.addResource(new Path(config.xml));

        FileSystem fileSystem = FileSystem.get(config);

        // Get the filename out of the file path
        String filename = file.getName();

        // Create the destination path including the filename.
        String destFile = path + "/" + filename;

        // Check if the file already exists
        Path dest = new Path(destFile);
        if (fileSystem.exists(dest)) {
            System.out.println("File " + dest + " already exists");
            return;
        }

        // Create a new file and write data to it.
        FSDataOutputStream out = fileSystem.create(dest);
        InputStream in = new BufferedInputStream(new FileInputStream(file));

        byte[] b = new byte[1024];
        int numBytes = 0;
        while ((numBytes = in.read(b)) > 0) {
            out.write(b, 0, numBytes);
        }

        in.close();
        out.close();
        fileSystem.close();
    }
}
