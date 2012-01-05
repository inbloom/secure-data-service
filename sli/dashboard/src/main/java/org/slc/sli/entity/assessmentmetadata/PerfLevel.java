package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent the performance level of an assessment family 
 */
public class PerfLevel {

    private String name;
    private String code;
    private String shortName;
    private String longName;

    public void setName(String s) {
        this.name = s;
    }
    public String getName() {
        return name;
    }
    public void setCode(String s) {
        this.code = s;
    }
    public String getCode() {
        return code;
    }
    public void setShortName(String s) {
        this.shortName = s;
    }
    public String getShortName() {
        return shortName;
    }
    public void setLongName(String s) {
        this.longName = s;
    }
    public String getLongName() {
        return longName;
    }
}
