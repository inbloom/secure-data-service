package org.slc.sli.client.entitywrapper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.slc.sli.domain.Student;

/**
 * wrapper around students
 */
@XmlRootElement(name = "students")
public class StudentWrapper {
    
    private List<Student> students = new ArrayList<Student>();
    
    @XmlElements({ @XmlElement(name = "student", type = Student.class) })
    public List<Student> getStudents() {
        return students;
    }
    
    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
