package org.slc.sli.ingestion.queues;

/**
* @author jtully
* Container for ingestion queue properties
*/
public class IngestionQueueProperties {
 
    private String hostName;
    
    private int port;
    
    private String queueName;
    
    //TODO Credential encryption.
    
    private String userName;
    
    private String password;
    
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
    
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
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
    
    public void setUserName(String name) {
        userName = name;
    }
    
    public void setPassword(String pass) {
        password = pass;
    }
    
    /**
     * return the camel URI for the queue
     */
    public String getQueueUri() {
        if (usingSedaQueues) {
            return "seda:" + queueName;
        } else {
            return "jms:queue:" + queueName;
        }
    }
    
    @Override
    public String toString() {
        String str = "";
        if (!usingSedaQueues) str += "host name: " + hostName + ", port: " + port + ", ";
        str += "queue URI: " + getQueueUri();
        return str;
    }
}
