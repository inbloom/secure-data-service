package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class AssessmentMeta {

    public final String id;
    public final List<String> objectiveAssessmentIds;
    public final List<String> assessmentItemIds;
    public final List<String> performanceLevelDescriptorIds;
    public String assessmentFamilyId;
    public String assessmentPeriodDescriptorId;
    public final List<String> sectionIds;

    private AssessmentMeta(String id) {
        this.id = id;
        this.objectiveAssessmentIds = new ArrayList<String>();
        this.assessmentItemIds = new ArrayList<String>();
        this.performanceLevelDescriptorIds = new ArrayList<String>();
        this.sectionIds = new ArrayList<String>();
    }

    public static AssessmentMeta create(String id) {
        return new AssessmentMeta(id);
    }

    @Override
    public String toString() {
        return "AssessmentMeta [id=" + id + ", objectiveAssessmentIds=" + objectiveAssessmentIds
                + ", assessmentItemIds=" + assessmentItemIds + ", performanceLevelDescriptorIds="
                + performanceLevelDescriptorIds + ", assessmentFamilyId=" + assessmentFamilyId
                + ", assessmentPeriodDescriptorId=" + assessmentPeriodDescriptorId + ", sectionIds=" + sectionIds + "]";
    }

}
