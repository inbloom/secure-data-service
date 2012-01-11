package org.slc.sli.ingestion.queues;

import java.util.HashMap;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.jms.client.HornetQConnectionFactory;

/**
 * Contains methods to directly connect to a HornetQ JMS queue
 * 
 * @author jtully
 *
 */
public class IngestionJmsQueue {
    
    private Session session;
    private Queue queue;
    
    /**
     * initialize a hornet queue connection
     * @throws JMSException
     */
    public void initializeConnection(IngestionQueueProperties properties) throws JMSException {

        HashMap<String, Object> tcMap = new HashMap<String, Object>();
           
        tcMap.put(TransportConstants.HOST_PROP_NAME, properties.getHostName());
        tcMap.put(TransportConstants.PORT_PROP_NAME, properties.getPort());
        
        TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), tcMap); 

        HornetQConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
        
        //JMS queue object
        queue = HornetQJMSClient.createQueue(properties.getQueueName());
        
        //JMS connection
        Connection connection = cf.createConnection();
        
        //set the session to be non transactional
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        connection.start();
    }
    
    public MessageProducer createProducer() throws JMSException {
        MessageProducer producer = session.createProducer(queue);
        return producer;
    }
    
    public MessageConsumer createConsumer() throws JMSException {
        MessageConsumer consumer = session.createConsumer(queue);
        return consumer;
    }
    
    public Session getSession(){
        return session;
    }
    
    public Queue getQueue(){
        return queue;
    }
    
   
}

