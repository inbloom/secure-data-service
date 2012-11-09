package org.dataprocessing.zipstreaming;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ZipStreamer<T> implements Callable<Boolean>  {

    static final int BUFFER = 1048576;
    
    public int id;
    public String filePath;
    public String innerFileName;
    
    public CopyOnWriteArrayList<Pair<String, Integer>> opCounts;

    public ZipStreamer() {
        
    }
    
    public ZipStreamer(int id, String filePath, String innerFileName, CopyOnWriteArrayList<Pair<String, Integer>> opCounts) {
        this.id = id;
        this.opCounts = opCounts;
        this.filePath = filePath;
        this.innerFileName = innerFileName;
    }

    @Override
    public Boolean call() throws Exception {
        System.out.println("CALLING THREAD " + this.id);
        execute();
        
        return false;
    }

    public void execute() {
        this.readDataFromZip();
    }
    
    
    private void readDataFromZip() {
        FileInputStream fis = null;
        ZipArchiveInputStream zis = null;
        
        try {
            // Create input stream
            fis = new FileInputStream(this.filePath);
            zis = new ZipArchiveInputStream(new BufferedInputStream(fis));

            ArchiveEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory() && this.innerFileName.equals(entry.getName())) {
                    System.out.println("LOCATED INNER FILE THAT SHOULD BE PROCESSED <" + entry.getName() + ">");
                    extractTo(zis, entry);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }
    }
    
    public void extractTo(ZipArchiveInputStream zis, ArchiveEntry entry) throws IOException {
        int count = 0;
        int curCount = 0;
        byte[] data = new byte[BUFFER];

        while ((count = zis.read(data, 0, BUFFER)) != -1) {
            curCount += count;
            processInputData(entry.getName(), curCount);
       }
    
    }

    
    public void processInputData(String currentFileName, long amount) {
        System.out.println("Streaming data from file = " + currentFileName + " " + amount);
    }
    
    
}