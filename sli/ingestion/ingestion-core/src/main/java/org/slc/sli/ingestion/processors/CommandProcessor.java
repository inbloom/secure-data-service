/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion.processors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.Aspects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.tenantdb.TenantContext;
import org.slc.sli.dal.aspect.MongoTrackingAspect;
import org.slc.sli.ingestion.aspect.StageTrackingAspect;
import org.slc.sli.ingestion.cache.CacheProvider;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;

/**
 * Process commands issued via a command topic
 *
 * @author dkornishev
 *
 */
@Component
public class CommandProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(CommandProcessor.class);
    private static final String BATCH_JOB_STAGE_DESC = "Processes commands issued via a command topic";
    private static final Object JOB_COMPLETED = "jobCompleted";
    private static final String BATCH_JOB_ID = "_id";

    @Resource(name = "batchJobMongoTemplate")
    private MongoTemplate mongo;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private CacheProvider cacheProvider;

    @Handler
    public void processCommand(Exchange exch) throws Exception {
        String command = exch.getIn().getBody().toString();

        LOG.info("Received: " + command);
        String[] chunks = command.split("\\|");

        if (JOB_COMPLETED.equals(chunks[0])) {

            LOG.info("Clearing cache at job completion.");

            cacheProvider.flush();

            dumpAspectTrackers(chunks);

        } else {
            LOG.error("Unsupported command");
        }
    }

    private void dumpAspectTrackers(String[] chunks) throws UnknownHostException {
        String jobId = chunks[1];

        TenantContext.setJobId(jobId);

        dumpMongoTrackingStats(jobId);

        dumpStageTrackingStats(jobId);
    }

    private void dumpMongoTrackingStats(String batchId) throws UnknownHostException {
        Map<String, ? extends Map<String, Pair<AtomicLong, AtomicLong>>> stats = Aspects.aspectOf(MongoTrackingAspect.class).getStats();

        if (stats != null) {
            String hostName = InetAddress.getLocalHost().getHostName();
            hostName = hostName.replaceAll("\\.", "#");
            Update update = new Update();
            update.set("executionStats." + hostName, stats);

            LOG.info("Dumping runtime stats to db for job {}", batchId);
            LOG.info(stats.toString());

            // TODO: move to BatchJobDAO
            mongo.updateFirst(new Query(Criteria.where(BATCH_JOB_ID).is(batchId)), update, "newBatchJob");
            Aspects.aspectOf(MongoTrackingAspect.class).reset();
        }
    }

    private void dumpStageTrackingStats(String jobId) {
        Map<String, Pair<AtomicLong, AtomicLong>> stats = Aspects.aspectOf(StageTrackingAspect.class).getStats();

        if (stats != null) {

            for (Entry<String, Pair<AtomicLong, AtomicLong>> statsEntry : stats.entrySet()) {
                Stage stage = new Stage(statsEntry.getKey(), BATCH_JOB_STAGE_DESC);
                stage.setElapsedTime(statsEntry.getValue().getRight().longValue());
                stage.setProcessingInformation("Invocation count: " + statsEntry.getValue().getLeft().longValue());

                batchJobDAO.saveBatchJobStage(jobId, stage);
            }

            Aspects.aspectOf(StageTrackingAspect.class).reset();
        }
    }

}
