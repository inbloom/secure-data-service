package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent a period in assessment family's meta data 
 */
public class Period {

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
