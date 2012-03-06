package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collection;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * basic implementation of Transmogrifier
 *
 * @author dduran
 *
 */
public final class TransmogrifierImpl implements Transmogrifier {

    private final String batchJobId;

    private final Collection<TransformationStrategy> transformationStrategies;

    // private constructor. use static factory method to create instances.
    private TransmogrifierImpl(NeutralRecordMongoAccess neutralRecordMongoAccess, String jobId,
            Collection<TransformationStrategy> transformationStrategies) {

        this.batchJobId = jobId;

        // never have null collection
        if (transformationStrategies == null) {
            transformationStrategies = new ArrayList<TransformationStrategy>(0);
        }
        this.transformationStrategies = transformationStrategies;
    }

    /**
     * Static factory method for creating a basic Transmogrifier
     *
     * @param neutralRecordMongoAccess
     * @param batchJobId
     * @param transformationStrategies
     * @return
     */
    public static Transmogrifier createInstance(NeutralRecordMongoAccess neutralRecordMongoAccess, String batchJobId,
            Collection<TransformationStrategy> transformationStrategies) {

        return new TransmogrifierImpl(neutralRecordMongoAccess, batchJobId, transformationStrategies);
    }

    @Override
    public void executeTransformations() {

        for (TransformationStrategy transformationStrategy : transformationStrategies) {

            transformationStrategy.perform(batchJobId);
        }
    }

}
