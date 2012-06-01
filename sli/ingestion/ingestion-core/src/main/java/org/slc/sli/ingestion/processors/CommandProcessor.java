package org.slc.sli.ingestion.processors;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.tuple.Pair;
import org.slc.sli.dal.aspect.MongoTrackingAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/**
 * Process commands issued via a command topic
 * 
 * @author dkornishev
 * 
 */
@Component
public class CommandProcessor {
    
    private static final Logger LOG = LoggerFactory.getLogger(CommandProcessor.class);
    
    private static final Object FLUSH_STATS = "flushStats";
    
    @Resource(name = "batchJobMongoTemplate")
    private MongoTemplate mongo;
    
    @Handler
    public void processCommand(Exchange exch) throws Exception {
        String command = exch.getIn().getBody().toString();
        
        LOG.info("Received: " + command);
        String[] chunks = command.split("\\|");
        
        if (FLUSH_STATS.equals(chunks[0])) {
            String batchId = chunks[1];
            Map<String, Pair<AtomicLong, AtomicLong>> stats = MongoTrackingAspect.aspectOf().getStats();
            
            String hostName = InetAddress.getLocalHost().getHostName();
            Update update = new Update();
            update.set("executionStats." + hostName, stats);
            
            LOG.info("Dumping runtime stats to db...");
            mongo.updateFirst(new Query(Criteria.where("_id").is(batchId)), update, "newBatchJob");
            MongoTrackingAspect.aspectOf().reset();
            LOG.info("Runtime stats are now cleared.");
        } else {
            LOG.error("Unsupported command");
        }
    }
    
}
