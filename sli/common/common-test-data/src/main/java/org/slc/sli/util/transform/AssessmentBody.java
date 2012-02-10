package org.slc.sli.util.transform;

import java.util.ArrayList;

public class AssessmentBody implements MongoDataEmitter, Cloneable {
    private ArrayList<AssessmentCode> assessmentIdentificationCodes = null;
    private String academicSubject = null;
    private String assessmentCategory = null;
    private String assessmentTitle = null;
    private String gradeLevelAssessed = null;
    private String contentStandard = null;
    private String version = null;
    private Assessment.Period thePeriod = null;
    
    public AssessmentBody(String title, ArrayList<AssessmentCode> codes, String subject, String category, String grade,
            String standard, String version) {
        this.assessmentTitle = title;
        this.assessmentIdentificationCodes = codes;
        this.academicSubject = subject;
        this.assessmentCategory = category;
        this.gradeLevelAssessed = grade;
        this.contentStandard = standard;
        this.version = version;
    }
    
    public AssessmentBody clone() {
        AssessmentBody clone = new AssessmentBody(assessmentTitle, assessmentIdentificationCodes, academicSubject,
                assessmentCategory, gradeLevelAssessed, contentStandard, version);
        return clone;
    }
    
    public void setTitle(String title) {
        this.assessmentTitle = title;
    }
    
    @Override
    public String emit() {
        // "body":{"assessmentTitle":"Mathematics Achievement Assessment Test","assessmentIdentificationCode":[{"identificationSystem":"School","id":"01234B"}],
        // "academicSubject":"MATHEMATICS","assessmentCategory":"ACHIEVEMENT_TEST","gradeLevelAssessed":"ADULT_EDUCATION",
        // "contentStandard":"LEA_STANDARD","version":"1.2"}
        StringBuffer answer = new StringBuffer();
        answer.append("\"body\":{\"assessmentTitle\":\"").append(assessmentTitle)
                .append("\",\"assessmentIdentificationCode\":[");
        boolean firstAssessmentIdCode = true;
        for (AssessmentCode ac : assessmentIdentificationCodes) {
            if (!firstAssessmentIdCode) {
                answer.append(",");
            }
            answer.append(ac.emit());
            firstAssessmentIdCode = false;
        }
        answer.append("],\"academicSubject\":\"").append(academicSubject).append("\",\"assessmentCategory\":\"")
                .append(assessmentCategory).append("\",\"gradeLevelAssessed\":\"").append(gradeLevelAssessed)
                .append("\",\"contentStandard\":\"").append(contentStandard).append("\",");
        if (thePeriod != null) {
            answer.append("\"period\":").append(thePeriod.emit()).append(",");
        }
        answer.append("\"version\":\"").append(version).append("\"}");
        
        return answer.toString();
    }
    
    public void setPeriod(Assessment.Period p) {
        thePeriod = p;
    }
}
