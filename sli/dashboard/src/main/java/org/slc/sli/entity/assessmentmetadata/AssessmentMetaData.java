package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent an assessment family's meta data ()
 */
public class AssessmentMetaData {

    private String name;
    private AssessmentMetaData[] children;
    private PerfLevel[] perfLevels;
    private Cutpoint[] cutpoints;
    private String windowStart;
    private String windowEnd;

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
    public String getWindowStart() {
        return windowStart;
    }
    public void setWindowStart(String s) {
        this.windowStart = s;
    }
    public String getWindowEnd() {
        return windowEnd;
    }
    public void setWindowEnd(String s) {
        this.windowEnd = s;
    }
}
