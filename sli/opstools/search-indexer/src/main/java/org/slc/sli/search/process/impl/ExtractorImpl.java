package org.slc.sli.search.process.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.ThreadUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slc.sli.search.config.IndexConfig;
import org.slc.sli.search.config.IndexConfigStore;
import org.slc.sli.search.process.Extractor;
import org.slc.sli.search.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Extractor pulls data from mongo and writes it to file.
 * 
 * @author dwu
 * 
 */
public class ExtractorImpl implements Extractor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static int DEFAULT_LINE_PER_FILE = 500000;
    private final static int DEFAULT_EXECUTOR_THREADS = 2;

    private final static int DEFAULT_JOB_WAIT_TIMEOUT_MINS = 180;

    private final static int DEFAULT_EXTRACTOR_JOB_TIME = 600;

    private int maxLinePerFile = DEFAULT_LINE_PER_FILE;

    private MongoTemplate mongoTemplate;

    private IndexConfigStore indexConfigStore;

    private String extractDir = Constants.DEFAULT_TMP_DIR;

    private String inboxDir;

    private ExecutorService executor;

    private int executorThreads = DEFAULT_EXECUTOR_THREADS;

    private int jobWaitTimeoutInMins = DEFAULT_JOB_WAIT_TIMEOUT_MINS;

    private boolean runOnStartup = false;


    public void destroy() {
        executor.shutdown();
    }

    public void init() {
        createExtractDir();
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
        if (runOnStartup) {
            execute();
        }
    }

    public void createExtractDir() {
        new File(extractDir).mkdirs();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.search.process.Extractor#execute()
     */
    public void execute() {

        IndexConfig config;
        Collection<String> collections = indexConfigStore.getCollections();
        Future<List<File>> call;
        List<Future<List<File>>> futures = new LinkedList<Future<List<File>>>();
        for (String collection : collections) {
            config = indexConfigStore.getConfig(collection);
            // child docs will be processed as dependents
            if (config.isChildDoc()) {
                continue;
            }
            call = executor.submit(new ExtractWorker(config));
            futures.add(call);
            if (config.hasDependents()) {
                for (String dependent : config.getDependents()) {
                    Future<List<File>> dependentCall = executor.submit(new DependentExtractWorker(indexConfigStore
                            .getConfig(dependent), call));
                    futures.add(dependentCall);
                }
            }
        }
        
        //wait job to be finished.
        for (Future<List<File>> future : futures) {
            try {
                future.get(DEFAULT_EXTRACTOR_JOB_TIME, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error("Error while waiting extractor job to be finished", e);
            }
        }
    }

    /**
     * Create DBCUrsor
     * Also, make this method available to Mock for UT
     * 
     * @param collectionName
     * @param fields
     * @return
     */
    protected DBCursor getDBCursor(String collectionName, List<String> fields) {
        // execute query, get cursor of results
        BasicDBObject keys = new BasicDBObject();
        for (String field : fields) {
            keys.put(field, 1);
        }

        DBCollection collection = mongoTemplate.getCollection(collectionName);
        return collection.find(new BasicDBObject(), keys);
    }

    public List<File> extractCollection(IndexConfig config, int retryCount) {

        logger.info("Extracting " + config);
        String collectionName = config.getCollectionName();
        BufferedWriter bw = null;
        DBObject obj;
        int numberOfLineWritten = 0;
        File outFile = null;
        DBCursor cursor = null;
        List<File> producedFiles = new ArrayList<File>();
        try {
            cursor = getDBCursor(collectionName, config.getFields());
            // write each record to file
            while (cursor.hasNext()) {
                if (numberOfLineWritten % maxLinePerFile == 0) {
                    numberOfLineWritten = 0;
                    IOUtils.closeQuietly(bw);
                    // move file to inbox for indexer
                    finishProcessing(outFile, producedFiles);

                    // open file to write
                    outFile = createTempFile(collectionName);
                    bw = new BufferedWriter(new FileWriter(outFile));
                    logger.info("File [" + outFile.getName() + "] was created");
                }
                obj = cursor.next();
                bw.write(JSON.serialize(obj));
                bw.newLine();
                numberOfLineWritten++;
            }
            IOUtils.closeQuietly(bw);

            finishProcessing(outFile, producedFiles);
            logger.info("Finished extracting " + collectionName);
        } catch (FileNotFoundException e) {
            logger.error("Error writing entities file", e);
        } catch (Exception e) {
            logger.error("Error while extracting " + collectionName, e);
            if (retryCount <= 1) {
                logger.error("Retrying extract for " + collectionName);
                ThreadUtil.sleep(1000);
                extractCollection(config, ++retryCount);
            }
        } finally {

            // close file
            IOUtils.closeQuietly(bw);
            // close cursor
            if (cursor != null)
                cursor.close();
        }
        return producedFiles;
    }

    private void finishProcessing(File outFile, List<File> producedFiles) throws IOException {
        // finish up
        // move file to inbox for indexer
        if (outFile != null) {
            File movedFile = new File(inboxDir, outFile.getName());
            FileUtils.moveFile(outFile, movedFile);
            producedFiles.add(movedFile);
        }
    }

    protected DBCursor getCursor(String collectionName, List<String> fields) {
        // execute query, get cursor of results
        BasicDBObject keys = new BasicDBObject();
        for (String field : fields) {
            keys.put(field, 1);
        }

        DBCollection collection = mongoTemplate.getCollection(collectionName);
        return collection.find(new BasicDBObject(), keys);
    }

    private File createTempFile(String collectionName) {
        return new File(extractDir, collectionName + "." + UUID.randomUUID() + ".json");
    }

    public void setIndexConfigStore(IndexConfigStore config) {
        this.indexConfigStore = config;
    }

    public void setExtractDir(String extractDir) {
        this.extractDir = extractDir;
    }

    public void setInboxDir(String inboxDir) {
        this.inboxDir = inboxDir;
    }

    public void setMaxLinePerFile(int maxLinePerFile) {
        this.maxLinePerFile = maxLinePerFile;
    }

    public void setExecutorThreads(int executorThreads) {
        this.executorThreads = executorThreads;
    }

    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setJobWaitTimeoutInMins(int jobWaitTimeoutInMins) {
        this.jobWaitTimeoutInMins = jobWaitTimeoutInMins;
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     * 
     * @author tosako
     * 
     */
    private class ExtractWorker implements Callable<List<File>> {

        final IndexConfig config;

        public ExtractWorker(IndexConfig config) {
            this.config = config;
        }

        public List<File> call() throws Exception {
            return extractCollection(config, 0);
        }
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     * 
     * @author tosako
     * 
     */
    private class DependentExtractWorker extends ExtractWorker {
        private final Future<List<File>> parentJob;

        public DependentExtractWorker(IndexConfig config, Future<List<File>> parentJob) {
            super(config);
            this.parentJob = parentJob;
        }

        @Override
        public List<File> call() throws Exception {
            try {
                final List<File> files = parentJob.get(20, TimeUnit.MINUTES);
                long timeToStopWaiting = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(jobWaitTimeoutInMins);
                File inbox = new File(inboxDir);
                String[] filesFound = null;
                while (filesFound == null || filesFound.length != 0) {
                    if (System.currentTimeMillis() <= timeToStopWaiting) {
                        ThreadUtil.sleep(30000);
                        filesFound = inbox.list(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return files.contains(name);
                            }
                        });
                    }

                }
            } catch (Exception e) {
                logger.error("Error while waiting for parent job to finish for " + config, e);
            }
            return super.call();
        }
    }

}