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

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
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
    
    @Autowired
    MongoTemplate template;
    
    ExtractConfig config;
    
    public Extractor() {
        
    }
    
    public void init() throws SchedulerException {
        
        // read configuration, which fields to extract
        config = new ExtractConfig();
        
        // set up job scheduler
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("template", template);
        dataMap.put("config", config);
        
        JobDetail job = JobBuilder.newJob(ExtractorJob.class)
                .withIdentity("dummyJobName", "group1")
                .usingJobData(dataMap)
                .build();
        
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .withIdentity("dummyTriggerName", "group1")
                .withSchedule(
                    CronScheduleBuilder.cronSchedule("0/15 * * * * ?"))
                .build();
        
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
        
    }
    
    public void destroy() {
        
    }
    
    
    /**
     *
     *
     */
    public static class ExtractorJob implements Job {
        
        private final Logger logger = LoggerFactory.getLogger(getClass());
        
        public ExtractorJob() {
            
        }

        public void execute(JobExecutionContext context) throws JobExecutionException { 
            
            MongoTemplate template = (MongoTemplate) context.getMergedJobDataMap().get("template");
            ExtractConfig config = (ExtractConfig) context.getMergedJobDataMap().get("config");
            
            for (String collection : config.collections()) {
                extractCollection(template, collection, config.getFields(collection));
            }
 
        }
    
        public void extractCollection(MongoTemplate template, String collectionName, List<String> fields) {
        
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
                File outFile = new File(collectionName + ".json");
                pw = new PrintWriter(outFile);
        
                // write each record to file
                while( cursor.hasNext() ) {
                    DBObject obj = cursor.next();
                    pw.println(JSON.serialize(obj));
                }
                pw.flush();
            
            } catch (FileNotFoundException e) {
                logger.error("Error writing entities file", e);
            } finally {
                if (pw != null) {
                    pw.close();
                }
                logger.info("Finished extracting " + collectionName);
            }

        }
    
    }
    
}