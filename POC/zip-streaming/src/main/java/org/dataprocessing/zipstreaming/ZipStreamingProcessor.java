package org.dataprocessing.zipstreaming;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;


@Component
public class ZipStreamingProcessor<T> {
    private String filePath;
    private List<String> innerXMLFiles;
    private int totalExecutors;
    
    private CopyOnWriteArrayList<Pair<String, Integer>> opCounts;
    
    public void run(int executorCount, String filePath) {
        this.totalExecutors = executorCount;
        this.filePath = filePath;
        
        this.innerXMLFiles = getXMLFileNames();
        
        if (this.innerXMLFiles != null && !this.innerXMLFiles.isEmpty()) {
            List<FutureTask<Boolean>> futureTaskList = processOperationsInFuture();
            boolean errors = false;
            
            try {
                runFutureTasks(futureTaskList, errors);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("ERROR: No Inner XML Files detected.");
        }
    }
    
    public List<String> getXMLFileNames() {
        FileInputStream fis = null;
        ZipArchiveInputStream zis = null;

        ArrayList<String> innerFiles = new ArrayList<String>();
        
        try {
            // Create input stream
            fis = new FileInputStream(this.filePath);
            zis = new ZipArchiveInputStream(new BufferedInputStream(fis));

            ArchiveEntry entry;

            // Extract files
            while ((entry = zis.getNextEntry()) != null) {

                if (!entry.isDirectory()) {
                    System.out.println("Detected inner file: " + entry);
                    System.out.println("--- File uncompressed size = <" + entry.getSize() + ">");
                    
                    innerFiles.add(entry.getName());
                }
            }
            
            return innerFiles;
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zis);
            IOUtils.closeQuietly(fis);
        }
        
        return null;
    }

    
    public void writeStatistics() {
        System.out.println("-------------");
        
        Map<String, Integer> finalCounts = new HashMap<String, Integer>();
        
        Iterator<Pair<String, Integer>> iter = this.opCounts.iterator();
        Integer newValue;
        
        while (iter.hasNext()) {
            Pair<String, Integer> pair = iter.next();
            
            if (finalCounts.get(pair.getLeft()) != null) {
                newValue = finalCounts.get(pair.getLeft()) + pair.getRight();
            } else {
                newValue = pair.getRight();
            }
            finalCounts.put(pair.getLeft(), newValue);
        }
        
        Iterator<Entry<String, Integer>> it = finalCounts.entrySet().iterator();
        
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            System.out.println("OPERATION " + String.format("%1$16s", entry.getKey()) +
                    "          TOTAL ELAPSED MS = " + String.format("%1$10s", entry.getValue()) 
            );
        }
        
        System.out.println("-------------");
        System.out.println("--- EXECUTION COMPLETED ---");
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<FutureTask<Boolean>> processOperationsInFuture() {

        List<FutureTask<Boolean>> futureTaskList = new ArrayList<FutureTask<Boolean>>(this.totalExecutors);
        this.opCounts = new CopyOnWriteArrayList<Pair<String, Integer>>();

        for (int i = 0; i < this.totalExecutors; i++) {
            Callable<Boolean> callable = new ZipStreamer(i, this.filePath, (String) this.innerXMLFiles.get(i), this.opCounts);
            FutureTask<Boolean> futureTask = ZipStreamerExecutor.execute(callable);
            futureTaskList.add(futureTask);
        }

        return futureTaskList;
    }   
    
    private void runFutureTasks(List<FutureTask<Boolean>> futureTaskList, boolean errors) throws InterruptedException, ExecutionException {
        for (FutureTask<Boolean> futureTask : futureTaskList) {
            // will block on FutureTask.get until task finishes
            if (futureTask.get()) {
                errors = true;
            }
        }
    }
    
}

