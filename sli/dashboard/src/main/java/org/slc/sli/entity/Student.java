package org.slc.sli.entity;
import com.google.gson.Gson;

public class Student {

    private String nameSuffix, stateId, sex, birthDate, name_first, nameVerification, hispanicLatinoEthnicity, 
                   nameMiddle, racialCategory, Name_Last;


    public String getName_first() {
        return name_first;
    }




    public void setName_first(String name_first) {
        this.name_first = name_first;
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




    public String getNameMiddle() {
        return nameMiddle;
    }




    public void setNameMiddle(String nameMiddle) {
        this.nameMiddle = nameMiddle;
    }




    public String getRacialCategory() {
        return racialCategory;
    }




    public void setRacialCategory(String racialCategory) {
        this.racialCategory = racialCategory;
    }




    public String getName_Last() {
        return Name_Last;
    }




    public void setName_Last(String nameLast) {
        this.Name_Last = nameLast;
    }




    public Student(String nameSuffix, String stateId, String sex, String birthDate,	String nameFirst,
            String nameVerification, String hispanicLatinoEthnicity,	String nameMiddle,	String racialCategory,
            String nameLast) {
		this.setNameSuffix(nameSuffix);
		this.stateId = stateId;
		this.sex = sex;
		this.birthDate = birthDate;
		this.name_first = nameFirst;
		this.nameVerification = nameVerification;
		this.hispanicLatinoEthnicity = hispanicLatinoEthnicity;
		this.nameMiddle = nameMiddle;
		this.racialCategory = racialCategory;
		this.Name_Last = nameLast;
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
