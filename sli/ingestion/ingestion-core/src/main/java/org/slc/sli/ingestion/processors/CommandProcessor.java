package org.slc.sli.ingestion.processors;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.dal.aspect.MongoTrackingAspect;
import org.slc.sli.ingestion.cache.CacheProvider;

/**
 * Process commands issued via a command topic
 *
 * @author dkornishev
 *
 */
@Component
public class CommandProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CommandProcessor.class);

    private static final Object JOB_COMPLETED = "jobCompleted";

    @Resource(name = "batchJobMongoTemplate")
    private MongoTemplate mongo;

    @Autowired
    private CacheProvider cacheProvider;

    @Handler
    public void processCommand(Exchange exch) throws Exception {
        String command = exch.getIn().getBody().toString();

        LOG.info("Received: " + command);
        String[] chunks = command.split("\\|");

        if (JOB_COMPLETED.equals(chunks[0])) {

            LOG.info("Clearing cache at job completion." );

            cacheProvider.flush();

            String batchId = chunks[1];
            Map<String, Pair<AtomicLong, AtomicLong>> stats = MongoTrackingAspect.aspectOf().getStats();

            String hostName = InetAddress.getLocalHost().getHostName();
            hostName = hostName.replaceAll("\\.", "#");
            Update update = new Update();
            update.set("executionStats." + hostName, stats);

            LOG.info("Dumping runtime stats to db for job {}", batchId);
            LOG.info(stats.toString());

            mongo.updateFirst(new Query(Criteria.where("_id").is(batchId)), update, "newBatchJob");
            MongoTrackingAspect.aspectOf().reset();
            LOG.info("Runtime stats are now cleared.");


        } else {
            LOG.error("Unsupported command");
        }
    }

}
