package org.slc.sli.search.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slc.sli.search.util.ExtractConfig;
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
public class Extractor {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final static String DEFAULT_EXTRACT_DIR = "tmp";
    
    @Autowired
    private MongoTemplate template;
    
    private ExtractConfig config;
    
    private String extractDir = DEFAULT_EXTRACT_DIR;
    
    
    public Extractor() {
        
    }
    
    public void init() throws SchedulerException {
        
        // read configuration, which fields to extract
        config = new ExtractConfig();
    }
    
    public void destroy() {
        
    }
    

    public void execute() throws JobExecutionException { 
            
        for (String collection : config.collections()) {
            extractCollection(collection, config.getFields(collection));
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
        
        PrintWriter pw = null;
        try {
            
            // open file to write
            File outFile = new File(extractDir + "/" + collectionName + ".json");
            outFile.getParentFile().mkdirs();
            pw = new PrintWriter(outFile);
            DBObject obj;
                
            // write each record to file
            while( cursor.hasNext() ) {
                obj = cursor.next();
                pw.println(JSON.serialize(obj));
            }
            pw.flush();
            
        } catch (FileNotFoundException e) {
            logger.error("Error writing entities file", e);
        } finally {
            // close file
            if (pw != null) {
                pw.close();
            }
                
            // close cursor
            cursor.close();
                
            logger.info("Finished extracting " + collectionName);
        }

    }
    
}