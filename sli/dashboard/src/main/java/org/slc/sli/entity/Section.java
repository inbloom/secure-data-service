package org.slc.sli.entity;

public class Section {

    private Student[] studentList;
    private String name;
    public Student[] getStudentList() {
        return studentList;
    }
    public void setStudentList(Student[] studentList) {
        this.studentList = studentList;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
