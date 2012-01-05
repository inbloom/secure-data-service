package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent an assessment family's meta data ()
 */
public class AssessmentFamilyMetaData {

    private String name;
    private AssessmentFamilyMetaData[] childrenFamily;
    private AssessmentMetaData[] childrenAssessment;
    private PerfLevel[] perfLevels;
    private Cutpoint[] cutpoints;

    public String getName() {
        return name;
    }
    public void setName(String s) {
        this.name = s;
    }
    public void setChildrenAssessment(AssessmentMetaData[] d) {
        childrenAssessment = d;
    }
    public AssessmentMetaData[] getChildrenAssessment() {
        return childrenAssessment;
    }
    public void setChildrenFamily(AssessmentFamilyMetaData[] d) {
        childrenFamily = d;
    }
    public AssessmentFamilyMetaData[] getChildrenFamily() {
        return childrenFamily;
    }
    public void setPerfLevels(PerfLevel[] p) {
        perfLevels = p;
    }
    public PerfLevel[] getPerfLevels() {
        return perfLevels;
    }
    public void setCutpoints(Cutpoint[] c) {
        cutpoints = c;
    }
    public Cutpoint[] getCutpoints() {
        return cutpoints;
    }
}
