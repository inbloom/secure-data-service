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

    private final String jobId;

    private final Collection<TransformationStrategy> transformationStrategies;

    // private constructor. use static factory method to create instances.
    private TransmogrifierImpl(NeutralRecordMongoAccess neutralRecordMongoAccess, String jobId,
            Collection<TransformationStrategy> transformationStrategies) {

        this.jobId = jobId;

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
     * @param jobId
     * @param transformationStrategies
     * @return
     */
    public static Transmogrifier createInstance(NeutralRecordMongoAccess neutralRecordMongoAccess, String jobId,
            Collection<TransformationStrategy> transformationStrategies) {

        return new TransmogrifierImpl(neutralRecordMongoAccess, jobId, transformationStrategies);
    }

    @Override
    public void execute() {

        for (TransformationStrategy strategy : transformationStrategies) {

            strategy.loadData();

            strategy.transform();

            strategy.persist();
        }
    }

}
