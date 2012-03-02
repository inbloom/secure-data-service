package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.handler.Handler;

/**
 * @author ifaybyshev
 *
 * @param <T>
 * @param <O>
 */
public interface TransformationStrategy<T, O> extends Handler<T, O> {
    
    /**
     * Transformation of data
     */
    void transform();
    
    /**
     * Loading data into local storage
     */
    void loadData();
    
    /**
     * Persisting transformed data (either file or database)
     * 
     * @return String status
     */
    String persist();
    
    /**
     * Specify the job id for which transformations will be done
     * 
     * @param id
     */
    void setJobId(String id);
    
}
