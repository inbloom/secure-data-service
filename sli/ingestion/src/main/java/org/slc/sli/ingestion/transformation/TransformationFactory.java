package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * @author ifaybyshev
 * 
 * Factory for transformation strategies
 *
 */
public class TransformationFactory {
    
    private static NeutralRecordMongoAccess neutralRecordMongoAccess;
    
    private static String assessmentCombiner = "AssessmentCombiner"; 
    
    public static String getAssessmentCombiner() {
        return assessmentCombiner;
    }

    public static TransformationStrategy<?, ?> getTransformationStrategy(String name) {
        
        if (name.equals(TransformationFactory.assessmentCombiner)) {
            return new AssessmentCombiner(neutralRecordMongoAccess);
        }
        
        return null;
        
    }
    
    static void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        TransformationFactory.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }
    
}
