package org.slc.sli.test.edfi.entities.meta;

import java.util.List;

public class GradeBookEntryMeta {

    String id;
    List<String> learningObjectiveIds;
    GradingPeriodMeta gradingPeriod;
    SectionMeta section;

    public void setLearningObjectiveIds(List<String> learningObjectiveIds) {
        this.learningObjectiveIds = learningObjectiveIds;
    }

    public List<String> getLearningObjectiveIds() {
		return learningObjectiveIds;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GradingPeriodMeta getGradingPeriod() {
        return gradingPeriod;
    }

    public void setGradingPeriod(GradingPeriodMeta gradingPeriod) {
        this.gradingPeriod = gradingPeriod;
    }

    public SectionMeta getSection() {
        return section;
    }

    public void setSection(SectionMeta section) {
        this.section = section;
    }

     
}
