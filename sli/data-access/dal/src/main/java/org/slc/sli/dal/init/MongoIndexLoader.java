package org.slc.sli.dal.init;

import org.slc.sli.common.constants.EntityNames;
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
    
    @Autowired(required = false)
    private MongoTemplate mongoTemplate;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.mongoTemplate != null) {
            try {
                LOG.debug("Creating compound index on attendance collection");
                IndexDefinition index = new Index().on("body.studentId", ASCENDING)
                        .on("body.schoolId", ASCENDING)
                        .on("metaData.tenantId", ASCENDING).unique();
                mongoTemplate.ensureIndex(index, EntityNames.ATTENDANCE);
                LOG.debug("Successfully created compound index on attendance collection");
            } catch (Exception e) {
                LOG.error("There was an error creating index on attendance collection");
            }
            
            try {
                LOG.debug("Creating compound index on parent collection");
                IndexDefinition index = new Index().on("body.parentUniqueStateId", ASCENDING)
                        .on("metaData.tenantId", ASCENDING).unique();
                mongoTemplate.ensureIndex(index, EntityNames.PARENT);
                LOG.debug("Successfully created compound index on parent collection");
            } catch (Exception e) {
                LOG.error("There was an error creating index on parent collection");
            }
            
            try {
                LOG.debug("Creating compound index on student-parent association collection");
                IndexDefinition index = new Index().on("body.studentId", ASCENDING).on("body.parentId", ASCENDING)
                        .on("metaData.tenantId", ASCENDING).unique();
                mongoTemplate.ensureIndex(index, EntityNames.STUDENT_PARENT_ASSOCIATION);
                LOG.debug("Successfully created compound index on student-parent association collection");
            } catch (Exception e) {
                LOG.error("There was an error creating index on student-parent association collection");
            }
        }
    }
}
