package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.List;

import org.slc.sli.ingestion.BatchJob;

/**
 * basic implementation of Transmogrifier
 * 
 * @author dduran
 * 
 */
public final class TransmogrifierImpl implements Transmogrifier {
    
    private final String batchJobId;
    
    private final BatchJob job;
    
    private final List<TransformationStrategy> transformationStrategies;
    
    // private constructor. use static factory method to create instances.
    private TransmogrifierImpl(BatchJob job, List<TransformationStrategy> transformationStrategies) {
        
        this.batchJobId = job.getId();
        this.job = job;
        
        // never have null collection
        if (transformationStrategies == null) {
            transformationStrategies = Collections.emptyList();
        }
        
        this.transformationStrategies = transformationStrategies;
    }
    
    /**
     * Static factory method for creating a basic Transmogrifier
     * 
     * @param batchJobId
     * @param transformationStrategies
     * @return
     */
    public static Transmogrifier createInstance(BatchJob job, List<TransformationStrategy> transformationStrategies) {
        return new TransmogrifierImpl(job, transformationStrategies);
    }
    
    @Override
    public void executeTransformations() {
        for (TransformationStrategy transformationStrategy : transformationStrategies) {
            transformationStrategy.perform(batchJobId);
        }
    }
    
}
