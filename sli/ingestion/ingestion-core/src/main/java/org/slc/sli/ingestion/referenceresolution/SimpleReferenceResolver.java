package org.slc.sli.ingestion.referenceresolution;

/**
 *
 *
 *
 */
public class SimpleReferenceResolver implements ReferenceResolutionStrategy {

    @Override
    public String resolveReference(String referenceName, String referenceId, String enclosingEntityName, String idContent, String interchangeName) {
        //String stub = "<AssessmentReference><AssessmentIdentity><AssessmentTitle>ACT</AssessmentTitle><AcademicSubject>Reading</AcademicSubject><GradeLevelAssessed>TA-ACT-8</GradeLevelAssessed><Version>1</Version></AssessmentIdentity></AssessmentReference>";
        return idContent;
    }

}
