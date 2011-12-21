package org.slc.sli.ingestion.landingzone.handler;

/**
 * 
 * @author dduran
 *
 * @param <T>
 */
public interface Handler<T> {
    
    void handle(T item);
    
}
