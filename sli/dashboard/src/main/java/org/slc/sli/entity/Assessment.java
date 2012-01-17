package org.slc.sli.entity;

/**
 * Entity to represent an assessment result
 */
public class Assessment {

    private String id;
    private String studentId;
    private String assessmentId;
    private String assessmentName;
    private String administrationDate;
    private String administrationEndDate;
    private Object[] links;
    private Object[] scoreResults;
    private int year;
    private int performanceLevel;
    private int scaleScore;
    private double percentile;
    private String lexileScore;
    private int	retestIndicator;
    
    public String getStudentId() { return studentId; }
    public String getAssessmentName() { return  assessmentName; }
    public int getYear() { return  year; }
    public int getPerfLevel() { return  performanceLevel; }
    public int getScaleScore() { return  scaleScore; }
    public double getPercentile() { return  percentile; }
    public String getLexileScore() { return  lexileScore; }
        
    public void setStudentId(String x) { studentId = x; }
    public void setAssessmentName(String x) { assessmentName = x; }
    public void setYear(int x) { year = x; }
    public void setPerfLevel(int x) { performanceLevel = x; }
    public void setScaleScore(int x) { scaleScore = x; }
    public void setPercentile(double x) { percentile = x; }
    public void setLexileScore(String x) { lexileScore = x; }
        
    // easy accessor functions
    public String getPerfLevelAsString() { return  new Integer(getPerfLevel()).toString(); }
    public String getScaleScoreAsString() { return  new Integer(getScaleScore()).toString(); }
    public String getPercentileAsString() { return  new Double(getPercentile()).toString(); }
    
	public String getId() {
		return id;
	}
	public String getAssessmentId() {
		return assessmentId;
	}
	public String getAdministrationDate() {
		return administrationDate;
	}
	public String getAdministrationEndDate() {
		return administrationEndDate;
	}
	public Object[] getLinks() {
		return links;
	}
	public Object[] getScoreResults() {
		return scoreResults;
	}
	public int getPerformanceLevel() {
		return performanceLevel;
	}
	public int getRetestIndicator() {
		return retestIndicator;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}
	public void setAdministrationDate(String administrationDate) {
		this.administrationDate = administrationDate;
	}
	public void setAdministrationEndDate(String administrationEndDate) {
		this.administrationEndDate = administrationEndDate;
	}
	public void setLinks(Object[] links) {
		this.links = links;
	}
	public void setScoreResults(Object[] scoreResults) {
		this.scoreResults = scoreResults;
	}
	public void setPerformanceLevel(int performanceLevel) {
		this.performanceLevel = performanceLevel;
	}
	public void setRetestIndicator(int retestIndicator) {
		this.retestIndicator = retestIndicator;
	}
}
