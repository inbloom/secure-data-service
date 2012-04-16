package org.slc.sli.ingestion.processors;

import java.util.HashSet;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJob;

/**
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PurgeProcessorTest {

    @Autowired
    private PurgeProcessor purgeProcessor;

    @Test
    public void testNoTenantId() throws Exception {

        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        BatchJob job = BatchJob.createDefault();


        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("testId");
        Mockito.when(message.getBody(BatchJob.class)).thenReturn(job);

        Logger log = Mockito.mock(org.slf4j.Logger.class);
        PrivateAccessor.setField(purgeProcessor, "logger", log);

        purgeProcessor.process(ex);
        Mockito.verify(log, Mockito.atLeastOnce()).error("Error:", "TenantId missing. No purge operation performed.");
    }

    @Test
    public void testPurging() throws Exception {
        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        BatchJob job = BatchJob.createDefault();
        job.setProperty("tenantId", "SLI");

        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("testId");
        Mockito.when(message.getBody(BatchJob.class)).thenReturn(job);

        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        purgeProcessor.setMongoTemplate(mongoTemplate);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("student");
        collectionNames.add("teacher");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(ex);

        Mockito.verify(mongoTemplate, Mockito.atLeastOnce()).remove(Mockito.any(Query.class), Mockito.eq("student"));
    }

    @Test
    public void testPurgingSystemCollections() throws Exception {
        Exchange ex = Mockito.mock(Exchange.class);
        Message message = Mockito.mock(Message.class);
        BatchJob job = BatchJob.createDefault();
        job.setProperty("tenantId", "SLI");

        Mockito.when(ex.getIn()).thenReturn(message);
        Mockito.when(message.getBody(String.class)).thenReturn("testId");
        Mockito.when(message.getBody(BatchJob.class)).thenReturn(job);

        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        purgeProcessor.setMongoTemplate(mongoTemplate);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("system.js");
        collectionNames.add("system.indexes");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(ex);

        Mockito.verify(mongoTemplate, Mockito.never()).remove(Mockito.any(Query.class), Mockito.eq("system.js"));
    }

}
