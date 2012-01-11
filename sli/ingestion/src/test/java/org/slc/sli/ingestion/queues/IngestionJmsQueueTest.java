package org.slc.sli.ingestion.queues;

//import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.queues.IngestionJmsQueue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:/spring/applicationContext-test.xml" })
public class IngestionJmsQueueTest {
   
    @Autowired
    IngestionQueueProperties assembledJobsQueueProperties;
    
    @Test
    public void testConnectAndSendMessagesToJmsQueue() throws Exception {
        checkQueueIsJMS();
        
        IngestionJmsQueue jmsQueue = new IngestionJmsQueue();
        jmsQueue.initializeConnection(assembledJobsQueueProperties);
        
        MessageProducer msgProducer = jmsQueue.createProducer();
        MessageConsumer msgConsumer = jmsQueue.createConsumer();
        
        String msgText = "Test text message";
        TextMessage message = jmsQueue.getSession().createTextMessage(msgText);
        msgProducer.send(message);
        
        Message receivedMsg = msgConsumer.receive();
        
        assertTrue(receivedMsg instanceof TextMessage);
        
        TextMessage receivedTextMsg = (TextMessage) receivedMsg;
        System.out.println("received message: " + msgText);
        assertEquals(receivedTextMsg.getText(), msgText);
    }
    
    @Before
    public void checkQueueIsJMS() {
        Assume.assumeTrue(!assembledJobsQueueProperties.isUsingSedaQueues());
    }
}
