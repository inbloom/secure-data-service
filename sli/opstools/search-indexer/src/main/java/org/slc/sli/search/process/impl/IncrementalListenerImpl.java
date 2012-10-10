package org.slc.sli.search.process.impl;

import org.apache.activemq.transport.stomp.Stomp.Headers.Subscribe;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;
import org.slc.sli.search.process.IncrementalListener;
import org.slc.sli.search.process.Indexer;
import org.slc.sli.search.transform.IndexEntityConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens to ActiveMQ for Sarje messages. Filters the message and passes
 * data to the Indexer to be indexed in elasticsearch.
 * 
 * @author dwu
 *
 */
public class IncrementalListenerImpl extends Thread implements IncrementalListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private Indexer indexer;

    private IndexEntityConverter indexEntityConverter;
    
    private String mqHost = "localhost";
    
    private int mqPort= 61612;
    
    private String mqUsername = "system";
    
    private String mqPswd = "manager";
    
    private String queue = "/queue/test";
    
    public void init() throws Exception {
        start();
    }
    
    public void run() {
        try {
            listen();
        } catch (Exception e) {
            logger.error("Listener exception", e);
        }
    }
    
    public void destroy() {
        
    }
    
    /* (non-Javadoc)
     * @see org.slc.sli.search.process.impl.IncrementalListener#listen()
     */
    public void listen() throws Exception {
    
        // connect to queue
        logger.info("Connecting to ActiveMQ");
        StompConnection connection = new StompConnection();
        connection.open(mqHost, mqPort);        
        connection.connect(mqUsername, mqPswd);
        logger.info("Connected to ActiveMQ");
        
        // listen for messages
        logger.info("Listening");
        connection.subscribe(queue, Subscribe.AckModeValues.AUTO);
        while (true) {
            StompFrame message = connection.receive(0);
            logger.info(message.getBody());
            process(message.getBody());
        }
    }
    
    /**
     * Process a message from the queue
     * 
     * @param message
     */
    private void process(String message) {
        
        logger.info("Processing message");
        
        logger.info("Done processing message");
    }
    
    private void filter() {
        
    }
    
    private void sendToIndexer() {
        
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }

    public void setIndexEntityConverter(IndexEntityConverter indexEntityConverter) {
        this.indexEntityConverter = indexEntityConverter;
    }
    
}