package org.slc.sli.search.process.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
    
    @Autowired
    private MongoTemplate template;
    
    private IndexEntityConfig indexEntityConfig;

    private String extractDir = DEFAULT_EXTRACT_DIR;

    private String inboxDir;
    
    public void init() {
        new File(extractDir).mkdirs();
    }
    
    public void destroy() {
        
    }
    

    /* (non-Javadoc)
     * @see org.slc.sli.search.process.Extractor#execute()
     */
    public void execute() { 
            
        for (String collection : indexEntityConfig.collections()) {
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
        
        DBCollection collection = template.getCollection( collectionName );
        DBCursor cursor = collection.find(new BasicDBObject(), keys);
        
        BufferedWriter bw = null;
        try {
            
            // open file to write
            File outFile = new File(extractDir, collectionName + ".json");
           
            bw = new BufferedWriter(new FileWriter(outFile));
            DBObject obj;
                
            // write each record to file
            while (cursor.hasNext()) {
                obj = cursor.next();
                bw.write(JSON.serialize(obj));
                bw.newLine();
            }
            
            // move file to inbox for indexer
            FileUtils.moveFileToDirectory(outFile, new File(inboxDir), true);
            
        } catch (FileNotFoundException e) {
            logger.error("Error writing entities file", e);
        } catch (IOException e) {
            logger.error("Error writing entities file", e);
        } finally {
            // close file
            if (bw != null) {
                IOUtils.closeQuietly(bw);
            }
            // close cursor
            cursor.close();
            
            logger.info("Finished extracting " + collectionName);
        }

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
}