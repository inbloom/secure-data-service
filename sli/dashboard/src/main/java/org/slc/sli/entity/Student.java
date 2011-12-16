package org.slc.sli.entity;
import com.google.gson.Gson;

/**
 * 
 * TODO: Write Javadoc
 *
 */
public class Student {

    private String nameSuffix, stateId, sex, birthDate, firstName, nameVerification, hispanicLatinoEthnicity, 
                   middleName, racialCategory, lastName;


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




    public String getLastName() {
        return lastName;
    }




    public void setLastName(String lastName) {
        this.lastName = lastName;
    }




    public String getStateId() {
        return stateId;
    }




    public void setStateId(String stateId) {
        this.stateId = stateId;
    }




    public String getSex() {
        return sex;
    }




    public void setSex(String sex) {
        this.sex = sex;
    }




    public String getBirthDate() {
        return birthDate;
    }




    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }




    public String getNameVerification() {
        return nameVerification;
    }




    public void setNameVerification(String nameVerification) {
        this.nameVerification = nameVerification;
    }




    public String getHispanicLatinoEthnicity() {
        return hispanicLatinoEthnicity;
    }




    public void setHispanicLatinoEthnicity(String hispanicLatinoEthnicity) {
        this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
    }




    public String getRacialCategory() {
        return racialCategory;
    }




    public void setRacialCategory(String racialCategory) {
        this.racialCategory = racialCategory;
    }


    public static Student fromJson(String json) {
        Gson gson = new Gson();
        Student student = gson.fromJson(json, Student.class);
        return student;
    }




    public String getNameSuffix() {
        return nameSuffix;
    }




    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

}
