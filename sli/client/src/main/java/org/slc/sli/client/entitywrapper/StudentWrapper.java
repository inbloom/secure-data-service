package org.slc.sli.client.entitywrapper;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.slc.sli.domain.Student;

import java.util.List;
import java.util.ArrayList;

@XmlRootElement(name = "students")
public class StudentWrapper {


private List<Student> students = new ArrayList<Student>();

@XmlElements({
@XmlElement(name = "student", type = Student.class) })
public List<Student> getStudents() {
return students;
}

public void setStudents(List<Student> students) {
this.students = students;
}
}
