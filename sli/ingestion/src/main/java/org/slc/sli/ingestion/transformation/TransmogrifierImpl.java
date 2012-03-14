package org.slc.sli.ingestion.transformation;

import java.util.Collections;
import java.util.List;

/**
 * basic implementation of Transmogrifier
 *
 * @author dduran
 *
 */
public final class TransmogrifierImpl implements Transmogrifier {

    private final String batchJobId;

    private final List<TransformationStrategy> transformationStrategies;

    // private constructor. use static factory method to create instances.
    private TransmogrifierImpl(String jobId, List<TransformationStrategy> transformationStrategies) {

        this.batchJobId = jobId;

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
    public static Transmogrifier createInstance(String batchJobId, List<TransformationStrategy> transformationStrategies) {
        return new TransmogrifierImpl(batchJobId, transformationStrategies);
    }

    @Override
    public void executeTransformations() {
        for (TransformationStrategy transformationStrategy : transformationStrategies) {
            transformationStrategy.perform(batchJobId);
        }
    }

}
