package org.slc.sli.entity.assessmentmetadata;

/**
 * Entity to represent an assessment's meta data (assessment config)
 */
public class AssessmentMetaData {

    private String name;
    private String windowStart;
    private String windowEnd;

    public String getName() {
        return name;
    }
    public void setName(String s) {
        this.name = s;
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
