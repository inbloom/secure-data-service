package org.slc.sli.util.transform;

public class StudentName implements MongoDataEmitter {
    private String firstName = null;
    private String middleName = null;
    private String lastSurname = null;
    private String suffix = null;
    private String nameVerification = null;
    
    public StudentName(String first, String middle, String last) {
        firstName = first;
        middleName = middle;
        lastSurname = last;
    }
    
    @Override
    public String emit() {
        // "name":{"firstName":"Gil","middleName":"","lastSurname":"Prince"}
        StringBuffer answer = new StringBuffer();
        answer.append("\"name\":{\"firstName\":\"").append(firstName).append("\",\"middleName\":\"")
                .append(middleName == null ? "" : middleName).append("\",\"verification\":\"")
                .append(nameVerification == null ? "" : nameVerification).append("\",\"lastSurname\":\"")
                .append(lastSurname).append("\",\"generationCodeSuffix\":\"").append(suffix == null ? "" : suffix)
                .append("\"}");
        return answer.toString();
    }
    
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
    public void setNameVerification(String nameVerification) {
        this.nameVerification = nameVerification;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastSurname() {
        return lastSurname;
    }
    
    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }
    
    public String getSuffix() {
        return suffix;
    }
    
    public String getNameVerification() {
        return nameVerification;
    }
}
