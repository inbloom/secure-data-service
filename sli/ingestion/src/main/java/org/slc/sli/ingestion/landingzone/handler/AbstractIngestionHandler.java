package org.slc.sli.ingestion.landingzone.handler;

import org.slc.sli.ingestion.landingzone.validation.IngestionValidator;

/**
 * Abstract class for all handlers.
 * 
 * @author dduran
 * 
 */
public abstract class AbstractIngestionHandler<T> implements Handler<T> {
    
    IngestionValidator<T> preValidator;
    IngestionValidator<T> postValidator;
    
    abstract void doHandling(T item);
    
    void pre(T item) {
        // TODO: fix after validators are refactored
        // preValidator.validate();
    };
    
    void post(T item) {
        // TODO: fix after validators are refactored
        // postValidator.validate();
    };
    
    @Override
    public void handle(T item) {
        
        pre(item);
        
        doHandling(item);
        
        post(item);
        
    };
    
}
