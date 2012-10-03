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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slc.sli.search.process.Extractor;
import org.slc.sli.search.util.IndexEntityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

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

    @Autowired
    private MongoTemplate template;

    private IndexEntityConfig indexEntityConfig;

    private String extractDir = DEFAULT_EXTRACT_DIR;

    private String inboxDir;

    private ExecutorService executor;

    private int executorThreads = DEFAULT_EXECUTOR_THREADS;


    public void init() {
        new File(extractDir).mkdirs();
        // create thread pool to process files
        executor = Executors.newFixedThreadPool(executorThreads);
    }

    public void destroy() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.slc.sli.search.process.Extractor#execute()
     */
    public void execute() {

        for (String collection : indexEntityConfig.collections()) {
            executor.execute(new ExtractWorker(collection, indexEntityConfig.getFields(collection)));
            extractCollection(collection, indexEntityConfig.getFields(collection));
        }

    }

    public void extractCollection(String collectionName, List<String> fields) {

        logger.info("Extracting " + collectionName);

        // execute query, get cursor of results
        BasicDBObject keys = new BasicDBObject();
        for (String field : fields) {
            keys.put(field, 1);
        }

        DBCollection collection = template.getCollection(collectionName);
        DBCursor cursor = collection.find(new BasicDBObject(), keys);

        BufferedWriter bw = null;
        DBObject obj;
        int numberOfLineWritten = 0;
        File outFile = null;
        try {
            // write each record to file
            while (cursor.hasNext()) {
                if (numberOfLineWritten % maxLinePerFile == 0) {
                    numberOfLineWritten = 0;
                    IOUtils.closeQuietly(bw);

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
            // move file to inbox for indexer
            FileUtils.moveFileToDirectory(outFile, new File(inboxDir), true);

        } catch (FileNotFoundException e) {
            logger.error("Error writing entities file", e);
        } catch (IOException e) {
            logger.error("Error writing entities file", e);
        } finally {
            // close file
            IOUtils.closeQuietly(bw);
            // close cursor
            cursor.close();
            logger.info("Finished extracting " + collectionName);
        }

    }

    private File createTempFile(String collectionName) {
        return new File(extractDir, collectionName + "." + UUID.randomUUID() + ".json");
    }

    public void setIndexEntityConfig(IndexEntityConfig config) {
        this.indexEntityConfig = config;
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