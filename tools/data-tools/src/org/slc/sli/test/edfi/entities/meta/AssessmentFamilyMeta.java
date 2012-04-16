package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class AssessmentFamilyMeta {

    public final String id;
    public final List<String> assessmentPeriodDescriptorIds;
    public String relatedAssessmentFamilyId;

    private AssessmentFamilyMeta(String id) {
        this.id = id;
        this.assessmentPeriodDescriptorIds = new ArrayList<String>();
    }

    public static AssessmentFamilyMeta create(String id) {
        return new AssessmentFamilyMeta(id);
    }

    @Override
    public String toString() {
        return "AssessmentFamilyMeta [id=" + id + ", assessmentPeriodDescriptorIds=" + assessmentPeriodDescriptorIds
                + ", relatedAssessmentFamilyId=" + relatedAssessmentFamilyId + "]";
    }

}
