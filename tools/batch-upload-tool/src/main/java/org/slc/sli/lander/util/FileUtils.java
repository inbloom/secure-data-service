package org.slc.sli.lander.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    
    static final int BUFFER = 1024 * 4;
    public static final String ZIP_FILE_NAME = "output.zip";
    
    public static final File zipIngestionData(String localDataDir) throws IOException {
        
        final class OnlyIngestionFiles implements FilenameFilter {
            public boolean accept(File file, String name) {
                return (name.endsWith(".xml") || name.endsWith(".ctl"));
            }
        }
        
        File pathDir = new File(localDataDir);
        File output = new File(pathDir, ZIP_FILE_NAME);
        if (output.exists()) {
            output.delete();
        }
        
        File zipFile = new File(pathDir, ZIP_FILE_NAME);
        FileOutputStream dest = null;
        try {
            dest = new FileOutputStream(zipFile);
            ZipOutputStream out = null;
            try {
                out = new ZipOutputStream(new BufferedOutputStream(dest));
                byte data[] = new byte[BUFFER];
                // get a list of files from the path
                String files[] = pathDir.list(new OnlyIngestionFiles());
                
                BufferedInputStream origin = null;
                for (String basename : files) {
                    File filename = new File(pathDir, basename);
                    FileInputStream fi = null;
                    try {
                        fi = new FileInputStream(filename);
                        try {
                            origin = new BufferedInputStream(fi, BUFFER);
                            ZipEntry entry = new ZipEntry(basename);
                            out.putNextEntry(entry);
                            int count;
                            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                                out.write(data, 0, count);
                            }
                        } finally {
                            if (null != origin) {
                                origin.close();
                            }
                        }
                    } finally {
                        if (null != fi) {
                            fi.close();
                        }
                    }
                }
            } finally {
                if (null != out) {
                    out.close();
                }
            }
        } finally {
            if (null != dest) {
                dest.close();
            }
        }
        return zipFile;
    }
}
