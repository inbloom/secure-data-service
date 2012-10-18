package org.slc.sli.search.process;

public interface IncrementalListener {
    
    /**
     * Start listening on ActiveMQ queue for search event messages
     * 
     * @throws Exception
     */
    public abstract void listen() throws Exception;
    
}