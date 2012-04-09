package org.slc.sli.test.edfi.entities.relations;

public final class AssessmentMeta {

    public final String id;
    public String objectiveAssessmentId;
    public String assessmentItemId;
    public String assessmentPerformanceLevelId;
    public String assessmentFamilyId;
    public String assessmentPeriodId;

    private AssessmentMeta(String id) {
        this.id = id;
    }

    public static AssessmentMeta create(String id) {
        return new AssessmentMeta(id);
    }

    @Override
    public String toString() {
        return "AssessmentMeta [id=" + id + ", objectiveAssessmentId=" + objectiveAssessmentId + ", assessmentItemId="
                + assessmentItemId + ", assessmentPerformanceLevelId=" + assessmentPerformanceLevelId
                + ", assessmentFamilyId=" + assessmentFamilyId + ", assessmentPeriodId=" + assessmentPeriodId + "]";
    }

}
