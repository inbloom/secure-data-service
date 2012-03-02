package org.slc.sli.dal.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Component;

/**
 * Creates compound indices on mongo database collections.
 *
 * @author shalka
 *
 */
@Component
public class MongoIndexLoader implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(MongoIndexLoader.class);
    private static final Order ASCENDING = Order.ASCENDING;
    private static final String ATTENDANCE_EVENT_COLLECTION = "attendanceEvent";

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.mongoTemplate != null) {

            LOG.debug("Creating compound index on attendanceEvent collection");
            IndexDefinition indexDefinition = new Index().on("body.eventDate", ASCENDING)
                    .on("body.studentId", ASCENDING).on("body.attendanceEventCategory", ASCENDING).unique();
            try {
                mongoTemplate.ensureIndex(indexDefinition, ATTENDANCE_EVENT_COLLECTION);
                LOG.debug("Successfully created compound index on attendanceEvent collection");
            } catch(Exception e) {
                LOG.warn("There was an error creating index");
            }
        }
    }
}
