package org.slc.sli.entity;

/**
 * Entity to represent an assessment result
 */
public class Assessment {

    private String studentId;
    private String assessmentName;
    private int year;
    private int perfLevel;
    private int scaleScore;
    private double percentile;
    private String lexileScore;
    
    public String getStudentId() { return studentId; }
    public String getAssessmentName() { return  assessmentName; }
    public int getYear() { return  year; }
    public int getPerfLevel() { return  perfLevel; }
    public int getScaleScore() { return  scaleScore; }
    public double getPercentile() { return  percentile; }
    public String getLexileScore() { return  lexileScore; }
        
    public void setStudentId(String x) { studentId = x; }
    public void setAssessmentName(String x) { assessmentName = x; }
    public void setYear(int x) { year = x; }
    public void setPerfLevel(int x) { perfLevel = x; }
    public void setScaleScore(int x) { scaleScore = x; }
    public void setPercentile(double x) { percentile = x; }
    public void setLexileScore(String x) { lexileScore = x; }
        
    // easy accessor functions
    public String getPerfLevelAsString() { return  new Integer(getPerfLevel()).toString(); }
    public String getScaleScoreAsString() { return  new Integer(getScaleScore()).toString(); }
    public String getPercentileAsString() { return  new Double(getPercentile()).toString(); }
}
