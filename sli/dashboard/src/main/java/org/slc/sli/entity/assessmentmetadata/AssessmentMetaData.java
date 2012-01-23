package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent an assessment family's meta data ()
 */
public class AssessmentMetaData {

    private String name;
    private AssessmentMetaData[] children;
    private PerfLevel[] perfLevels;
    private Cutpoint[] cutpoints;

    private Period[] periods;
    private String period;

    public String getName() {
        return name;
    }
    public void setName(String s) {
        this.name = s;
    }
    public void setChildren(AssessmentMetaData[] d) {
        children = d;
    }
    public AssessmentMetaData[] getChildren() {
        return children;
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
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String s) {
        this.period = s;
    }
    public Period[] getPeriods() {
        return periods;
    }
    public void setPeriods(Period[] p) {
        this.periods = p;
    }
}
