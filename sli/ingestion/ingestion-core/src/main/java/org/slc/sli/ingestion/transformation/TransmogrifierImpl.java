package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.List;

import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.WorkNote;

/**
 * basic implementation of Transmogrifier
 * 
 * @author dduran
 * 
 */
public final class TransmogrifierImpl implements Transmogrifier {
    
    private final Job job;
    private final List<TransformationStrategy> transformationStrategies;
    private WorkNote workNote;
    
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
     * Constructor for transmogrifier that incorporates work notes.
     * 
     * @param job current job to be processed.
     * @param transformationStrategies set of transformation strategies to be executed.
     * @param workNote collection and range transformation strategy should act upon.
     */
    private TransmogrifierImpl(Job job, List<TransformationStrategy> transformationStrategies, WorkNote workNote) {
        
        this.job = job;
        
        if (transformationStrategies == null) {
            transformationStrategies = Collections.emptyList();
        }
        
        this.transformationStrategies = transformationStrategies;
        this.workNote = workNote;
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
    
    public static Transmogrifier createInstance(Job job, List<TransformationStrategy> transformationStrategies, WorkNote workNote) {
        return new TransmogrifierImpl(job, transformationStrategies, workNote);
    }
    
    @Override
    public void executeTransformations() {
        for (TransformationStrategy transformationStrategy : transformationStrategies) {
            if (workNote == null) {
                transformationStrategy.perform(job);
            } else {
                transformationStrategy.perform(job, workNote);
            }            
        }
    }
    
}
