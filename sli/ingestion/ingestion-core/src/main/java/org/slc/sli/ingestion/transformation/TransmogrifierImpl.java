package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.List;

import org.slc.sli.ingestion.Job;

/**
 * basic implementation of Transmogrifier
 * 
 * @author dduran
 * 
 */
public final class TransmogrifierImpl implements Transmogrifier {
    
    private final Job job;
    
    private final List<TransformationStrategy> transformationStrategies;
    
    // private constructor. use static factory method to create instances.
    private TransmogrifierImpl(Job job, List<TransformationStrategy> transformationStrategies) {
        
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
    public static Transmogrifier createInstance(Job job, List<TransformationStrategy> transformationStrategies) {
        return new TransmogrifierImpl(job, transformationStrategies);
    }
    
    @Override
    public void executeTransformations() {
        for (TransformationStrategy transformationStrategy : transformationStrategies) {
            transformationStrategy.perform(job);
        }
    }
    
}
