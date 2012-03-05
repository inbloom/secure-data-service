package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * @author ifaybyshev
 *
 *         Factory for transformation strategies
 *
 */
public class TransformationFactory {

    private static NeutralRecordMongoAccess neutralRecordMongoAccess;

    public static final String ASSESSMENT_COMBINER = "AssessmentCombiner";

    // TODO: what is the pivot that our strategy should depend on? maybe the input should be the all
    // the collections involved in this job? can we use enums here? perhaps the return type should
    // actually be Set<TransformationStrategy> ?
    public static TransformationStrategy<?, ?> getTransformationStrategy(String name) {

        if (name.equals(TransformationFactory.ASSESSMENT_COMBINER)) {
            return new AssessmentCombiner(neutralRecordMongoAccess);
        }

        return null;

    }

    static void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        TransformationFactory.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

}
