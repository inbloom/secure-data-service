package org.slc.sli.util.transform;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author dwilliams
 *
 */
public class Assessment implements MongoDataEmitter {
    private AssessmentBody body = null;
    private String studentId = null;
    private String generatedUuid = null;
    private ArrayList<Period> abstractPeriods = new ArrayList<Period>();
    
    public Assessment(AssessmentBody body, String studentId) {
        this.body = body;
        this.studentId = studentId;
        generatedUuid = Base64.nextUuid("aaaa");
    }
    
    public String getUuid() {
        return generatedUuid;
    }
    
    @Override
    public String emit() {
        // {"_id":{"$binary":"gU1X6jgLK1TWO5piWrWcqg==","$type":"03"},
        // "type":"assessment",
        // "body":{"assessmentTitle":"Mathematics Achievement Assessment Test","assessmentIdentificationCode":[{"identificationSystem":"School","id":"01234B"}],
        // "academicSubject":"MATHEMATICS","assessmentCategory":"ACHIEVEMENT_TEST","gradeLevelAssessed":"ADULT_EDUCATION",
        // "contentStandard":"LEA_STANDARD","version":"1.2"}}
        StringBuffer answer = new StringBuffer();
        answer.append("{\"_id\":{\"$binary\":\"").append(Base64.toBase64(generatedUuid))
                .append("\",\"$type\":\"03\"},\"type\":\"assessment\",").append(body.emit()).append("}\n");
        
        return answer.toString();
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public AssessmentBody getBody() {
        return body;
    }
    
    public void addAbstractPeriod(String periodName, String periodStart, String periodEnd) {
        Period p = new Period(periodName, periodStart, periodEnd);
        abstractPeriods.add(p);
    }
    
    public void setPeriod(String periodName) {
        Iterator<Period> iter = abstractPeriods.iterator();
        while (iter.hasNext()) {
            Period test = iter.next();
            if (test.name.equals(periodName)) {
                body.setPeriod(test);
                break;
            }
        }
    }
    
    /**
     * 
     * @author dwilliams
     *
     */
    public class Period {
        private String name = null;
        private String start = null;
        private String end = null;
        
        public Period(String periodName, String periodStart, String periodEnd) {
            this.name = periodName;
            this.start = periodStart;
            this.end = periodEnd;
        }
        
        public String emit() {
            StringBuffer answer = new StringBuffer();
            answer.append("{\"codeValue\":\"").append(name).append("\",\"beginDate\":\"").append(start)
                    .append("\",\"endDate\":\"").append(end).append("\"}");
            return answer.toString();
        }
    }
    
    public Assessment copyWithName(String childName) {
        Assessment child = new Assessment(getBody().clone(), studentId);
        child.getBody().setTitle(childName);
        return child;
    }
}
