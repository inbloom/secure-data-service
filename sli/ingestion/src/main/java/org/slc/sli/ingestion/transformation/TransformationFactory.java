package org.slc.sli.ingestion.transformation;

import java.util.ArrayList;
import java.util.Collection;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * @author ifaybyshev
 *
 *         Factory for transformation strategies
 *
 */
public class TransformationFactory {

    // TODO can we make this a service instead of passing it through every layer?
    private static NeutralRecordMongoAccess neutralRecordMongoAccess;

    // TODO: can we use enums?
    public static final String ASSESSMENT_COMBINER = "AssessmentCombiner";

    /**
     * Create a transmogrifier based on a jobId and collection names that, when executed, will
     * perform all transformations required for this job.
     *
     * @param collectionNames
     * @param jobId
     * @return
     */
    public static Transmogrifier createTransmogrifier(Collection<String> collectionNames, String jobId) {

        Collection<TransformationStrategy> transformationStrategies = deriveTransformsRequired(collectionNames);

        return TransmogrifierImpl.createInstance(neutralRecordMongoAccess, jobId, transformationStrategies);
    }

    private static Collection<TransformationStrategy> deriveTransformsRequired(Collection<String> collectionNames) {

        Collection<TransformationStrategy> transformationStrategies = new ArrayList<TransformationStrategy>();

        // TODO: again, can we use enums / enumset ?
        if (collectionNames.contains(TransformationFactory.ASSESSMENT_COMBINER)) {
            transformationStrategies.add(new AssessmentCombiner(neutralRecordMongoAccess));
        }

        return transformationStrategies;
    }

    // invoked via spring
    public static void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        TransformationFactory.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

    // invoked via spring
    public static NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return  neutralRecordMongoAccess;
    }
}
