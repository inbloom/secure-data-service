package org.slc.sli.entity.assessmentmetadata;


/**
 * Entity to represent the cutpoint of an assessment family 
 */
public class Cutpoint {

    private String name;
    private int[] range;

    public void setName(String s) {
        this.name = s;
    }
    public String getName() {
        return name;
    }
    public void setRange(int[] d) {
        range = d;
    }
    public int[] getRange() {
        return range;
    }
}
