package org.slc.sli.search.process.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.ThreadUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slc.sli.search.config.IndexEntityConfigStore;
import org.slc.sli.search.process.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Extractor pulls data from mongo and writes it to file.
 * 
 * @author dwu
 * 
 */
public class ExtractorImpl implements Extractor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String DEFAULT_EXTRACT_DIR = "tmp";
    private final static int DEFAULT_LINE_PER_FILE = 500000;
    private final static int DEFAULT_EXECUTOR_THREADS = 2;

    private int maxLinePerFile = DEFAULT_LINE_PER_FILE;

    private MongoOperations mongoTemplate;

    private IndexEntityConfigStore indexEntityConfigStore;

    private String extractDir = DEFAULT_EXTRACT_DIR;

    private String inboxDir;

    private ExecutorService executor;

    private int executorThreads = DEFAULT_EXECUTOR_THREADS;

    private boolean runOnStartup = false;

    public void init() {
        new File(extractDir).mkdirs();
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
        if (runOnStartup) {
            execute();
        }
    }

    public void destroy() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.search.process.Extractor#execute()
     */
    public void execute() {
        for (String collection : indexEntityConfigStore.getCollections()) {
            executor.execute(new ExtractWorker(collection, indexEntityConfigStore.getFields(collection)));
        }

    }

    /**
     * Create DBCUrsor
     * Also, make this method available to Mock for UT
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

    public void extractCollection(String collectionName, List<String> fields) {

        logger.info("Extracting " + collectionName);

        BufferedWriter bw = null;
        DBObject obj;
        int numberOfLineWritten = 0;
        File outFile = null;
        DBCursor cursor = null;
        try {
            cursor = getDBCursor(collectionName, fields);
            // write each record to file
            while (cursor.hasNext()) {
                if (numberOfLineWritten % maxLinePerFile == 0) {
                    numberOfLineWritten = 0;

                    // move file to inbox for indexer
                    if (outFile != null) {
                        IOUtils.closeQuietly(bw);
                        FileUtils.moveFile(outFile, new File(inboxDir, outFile.getName()));
                    }

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
            // finish up
            // move file to inbox for indexer
            if (outFile != null) {
                IOUtils.closeQuietly(bw);
                FileUtils.moveFile(outFile, new File(inboxDir, outFile.getName()));
                logger.info("File [" + outFile.getName() + "] " + new File(inboxDir, outFile.getName()).exists());
                ThreadUtil.sleep(1000);
                logger.info("File [" + outFile.getName() + "] " + new File(inboxDir, outFile.getName()).exists());
            }
            logger.info("Finished extracting " + collectionName);

        } catch (FileNotFoundException e) {
            logger.error("Error writing entities file", e);
        } catch (IOException e) {
            logger.error("Error writing entities file", e);
        } finally {
            // close file
            IOUtils.closeQuietly(bw);
            // close cursor
            cursor.close();
        }
    }

    private File createTempFile(String collectionName) {
        return new File(extractDir, collectionName + "." + UUID.randomUUID() + ".json");
    }

    public void setIndexEntityConfigStore(IndexEntityConfigStore config) {
        this.indexEntityConfigStore = config;
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

    public void setMongoTemplate(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Runnable Thread class to write into file read from Mongo.
     * 
     * @author tosako
     * 
     */
    private class ExtractWorker implements Runnable {

        private String collectionName;
        private List<String> fields;

        public ExtractWorker(String collectionName, List<String> fields) {
            this.collectionName = collectionName;
            this.fields = fields;
        }

        public void run() {
            extractCollection(collectionName, fields);
        }
    }

}