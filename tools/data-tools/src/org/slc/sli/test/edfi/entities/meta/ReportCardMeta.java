package org.slc.sli.test.edfi.entities.meta;

import java.util.List;

public class ReportCardMeta {

    String id;
    List<String> studentCompetencyIds;
    List<String> learningObjectiveIds;
    List<SectionMeta> learningObjectiveSections;
    List<SectionMeta> studentCompetencyObjectiveSections;
    int gradePerReportCard;
    GradingPeriodMeta gradingPeriod;
    
    
    public List<String> getStudentCompetencyIds() {
        return studentCompetencyIds;
    }
    public void setStudentCompetencyIds(List<String> studentCompetencyIds) {
        this.studentCompetencyIds = studentCompetencyIds;
    }
    public List<String> getLearningObjectiveIds() {
        return learningObjectiveIds;
    }
    public void setLearningObjectiveIds(List<String> learningObjectiveIds) {
        this.learningObjectiveIds = learningObjectiveIds;
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
	public List<SectionMeta> getLearningObjectiveSections() {
		return learningObjectiveSections;
	}
	public void setLearningObjectiveSections(
			List<SectionMeta> learningObjectiveSections) {
		this.learningObjectiveSections = learningObjectiveSections;
	}
	public List<SectionMeta> getStudentCompetencyObjectiveSections() {
		return studentCompetencyObjectiveSections;
	}
	public void setStudentCompetencyObjectiveSections(
			List<SectionMeta> studentCompetencyObjectiveSections) {
		this.studentCompetencyObjectiveSections = studentCompetencyObjectiveSections;
	}

        
}
