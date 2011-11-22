package org.slc.sli.api.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;

public class StudentServiceMockImpl implements StudentService {
    
    private Logger log = LoggerFactory.getLogger(StudentServiceMockImpl.class);
    
    private Map<Integer, Student> students = createStudents();
    
    /**
     * Returns all students.
     * 
     * @FIXME Need to decide on pagination or returning list of student IDs.
     * @return List of students
     */
    public Collection<Student> getAll() {
        
        log.debug("Returning all students: {}", students);
        
        return students.values();
    }
    
    private static Map<Integer, Student> createStudents() {
        
        Map<Integer, Student> tempStudents = new LinkedHashMap<Integer, Student>();
        Student student1 = new Student();
        student1.setStudentId(1);
        student1.setFirstName("John");
        student1.setLastSurname("Doe");
        student1.setStudentId(UUID.randomUUID().hashCode());
        
        Student student2 = new Student();
        student1.setStudentId(2);
        student2.setFirstName("Jane");
        student2.setLastSurname("Smith");
        student2.setStudentId(UUID.randomUUID().hashCode());
        
        tempStudents.put(student1.getStudentId(), student1);
        tempStudents.put(student2.getStudentId(), student2);
        return tempStudents;
    }
    
    public void deleteById(int id) {
        
        log.debug("Deleting student by id: {}", id);
        
        students.remove(id);
        
    }
    
    public void addOrUpdate(Student student) {
        if (student.getStudentId() == null) {
            int max = 0;
            for (int key : students.keySet()) {
                max = Math.max(key, max);
            }
            student.setStudentId(max + 1);
        }
        students.put(student.getStudentId(), student);
        
    }
    
    @Override
    public Student getStudentById(int studentId) {
        return students.get(studentId);
    }
    
    @Override
    public Collection<School> getSchoolsByStudent(Student student) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
