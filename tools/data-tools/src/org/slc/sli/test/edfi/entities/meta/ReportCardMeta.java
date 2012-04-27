package org.slc.sli.test.edfi.entities.meta;

import java.util.List;

public class ReportCardMeta {

    String id;
    List<String> studentCompetencyIds;
    List<String> learningObjectiveIds;
    List<SectionMeta> loSections;
    List<SectionMeta> scoSections;
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
    public List<SectionMeta> getLoSections() {
        return loSections;
    }
    public void setLoSections(List<SectionMeta> loSections) {
        this.loSections = loSections;
    }
    public List<SectionMeta> getScoSections() {
        return scoSections;
    }
    public void setScoSections(List<SectionMeta> scoSections) {
        this.scoSections = scoSections;
    }
        
}
