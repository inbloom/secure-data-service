package org.slc.sli.test.edfi.entities.meta;

import java.util.ArrayList;
import java.util.List;

public final class ObjectiveAssessmentMeta {

    public final String id;
    public final List<String> learningObjectiveIds;
    public final List<String> assessmentItemIds;
    public final List<String> learningStandardIds;
    public final List<String> performanceLevelDescriptorIds;
    public final List<String> relatedObjectiveAssessmentIds;

    private ObjectiveAssessmentMeta(String id) {
        this.id = id;
        this.learningObjectiveIds = new ArrayList<String>();
        this.assessmentItemIds = new ArrayList<String>();
        this.learningStandardIds = new ArrayList<String>();
        this.performanceLevelDescriptorIds = new ArrayList<String>();
        this.relatedObjectiveAssessmentIds = new ArrayList<String>();
    }

    public static ObjectiveAssessmentMeta create(String id) {
        return new ObjectiveAssessmentMeta(id);
    }

    @Override
    public String toString() {
        return "ObjectiveAssessmentMeta [id=" + id + ", learningObjectiveIds=" + learningObjectiveIds
                + ", assessmentItemIds=" + assessmentItemIds + ", learningStandardIds=" + learningStandardIds
                + ", performanceLevelDescriptorIds=" + performanceLevelDescriptorIds
                + ", relatedObjectiveAssessmentIds=" + relatedObjectiveAssessmentIds + "]";
    }

}
