package org.slc.sli.search.process.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.lucene.util.IOUtils;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.process.Loader;
import org.slc.sli.search.util.IndexEntityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.util.ThreadUtil;

@Service
public class LoaderImpl implements FileAlterationListener, Loader {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String DEFAULT_DROP_OFF_DIR = "inbox";
    private static final long DEFAULT_INTERVAL_MILLIS = 1000L;
    private static final int DEFAULT_EXECUTOR_THREADS = 5;
    
    @Autowired
    private Indexer indexer;
    
    @Autowired
    IndexEntityConverter indexEntityConverter;
    
    @Autowired(required=false)
    private String inboxDir = DEFAULT_DROP_OFF_DIR;
    
    @Autowired(required=false)
    private long pollIntervalMillis = DEFAULT_INTERVAL_MILLIS;
    
    private FileAlterationMonitor monitor;
    
    private ExecutorService executor;
    private int executorThreads = DEFAULT_EXECUTOR_THREADS;
    
    public void init() throws Exception {
        monitor = new FileAlterationMonitor(pollIntervalMillis);
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
        File inbox = new File(inboxDir);
        FileAlterationObserver observer = new FileAlterationObserver(inbox);
        monitor.addObserver(observer);
        observer.addListener(this);
        // watch directory for files
        for (File f : inbox.listFiles())
        {
            processFile(f);
        }
        monitor.start();
    }
    
    public void destroy()  throws Exception{
        monitor.stop();
        executor.shutdown();
    }
    
    /**
     * A worker to process an individual file
     *
     */
    private class LoaderWorker implements Runnable {
        
        File inFile;
        
        LoaderWorker(File inFile) {
            this.inFile = inFile;
        }
        
        public void run() {
            // read records from file
            BufferedReader br = null;
            String entity;
            boolean success = false;
            try {
                br = new BufferedReader(new FileReader(inFile));
                while ((entity = br.readLine()) != null) {
                    indexer.index(indexEntityConverter.fromEntityJson(entity));
                }
                success = true;
            } catch (Throwable e) {
                logger.error("Error loading from file", e);
            }
            finally {
                try {
                    IOUtils.closeWhileHandlingException(br);
                    logger.info("Done processing file: " + inFile.getName());
                } catch (IOException e) {
                    logger.error("Error closing file", e);
                }
                if (success)
                    archive(inFile);
            }
        }
    }
    
    /**
     * What to do after processing
     * @param inFile
     */
    public void archive(File inFile) {
        inFile.delete();
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Loader#processFile(java.io.File)
     */
    public void processFile(File inFile) {
        // TODO: make sure file is not being written to still
        logger.info("Processing file: " + inFile.getName());
        long size1 = 0L, size2 = 1L;
        while (size1 != size2) {
            size1 = inFile.length();
            ThreadUtil.sleep(100);
            size2 = inFile.length();
            try {
                new FileInputStream(inFile);
                if (size1 == size2) {
                    break;
                }
            } catch (IOException ioe) {
            }
        }
        executor.execute(new LoaderWorker(inFile));
    }

    public void onDirectoryChange(File arg0) {
    }

    public void onDirectoryCreate(File arg0) {
    }

    public void onDirectoryDelete(File arg0) {
    }

    public void onFileChange(File arg0) {
    }

    public void onFileCreate(File inFile) {
        processFile(inFile);
    }

    public void onFileDelete(File arg0) {
    }

    public void onStart(FileAlterationObserver arg0) {
    }

    public void onStop(FileAlterationObserver arg0) {
    }
    
    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setIndexEntityConverter(IndexEntityConverter indexEntityConverter) {
        this.indexEntityConverter = indexEntityConverter; 
    }
    
    public void setPollIntervalMillis(long pollIntervalMillis) {
        this.pollIntervalMillis = pollIntervalMillis;
    }
    
}