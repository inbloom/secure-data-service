package org.slc.sli.ingestion.queues;

/**
* @author jtully
* Container for queue properties
*/
public class IngestionQueueProperties {
 
    private String hostName;
    
    private int port;
    
    private String queueName;
    
    /**
     * Whether the queue is a seda in memory queue as opposed to a jms queue.
     * If true, the port and hostName properties are redundant.
     */
    private boolean usingSedaQueues;
    
    public String getHostName() {
        return hostName;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public boolean isUsingSedaQueues() {
        return usingSedaQueues;
    }
    
    public void setHostName(String name) {
        hostName = name;
    }
    
    public void setPort(int p) {
        port = p;
    }
    
    public void setQueueName(String name) {
        queueName = name;
    }
   
    public void setUsingSedaQueues(boolean useSeda) {
        usingSedaQueues = useSeda;
    }
    
    /**
     * return the camel URI for the queue
     */
    public String getQueueUri() {
        if (usingSedaQueues) {
            return "seda:" + queueName;
        }
        else {
            return "jms:queue:" + queueName;
        }
    }  
}
