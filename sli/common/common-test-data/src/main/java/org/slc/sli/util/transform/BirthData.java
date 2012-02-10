package org.slc.sli.util.transform;

/**
 * 
 * @author dwilliams
 *
 */
public class BirthData implements MongoDataEmitter {
    private String birthDate = null;
    
    public BirthData(String date) {
        birthDate = date;
    }
    
    @Override
    public String emit() {
        // "birthData":{"birthDate":"2000-01-01"}
        StringBuffer answer = new StringBuffer();
        answer.append("\"birthData\":{\"birthDate\":\"").append(birthDate).append("\"}");
        return answer.toString();
    }
    
    public String getBirthDate() {
        return birthDate;
    }
}
