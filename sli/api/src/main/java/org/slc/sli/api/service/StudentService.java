package org.slc.sli.api.service;

import java.util.Collection;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import org.slc.sli.domain.School;
import org.slc.sli.domain.Student;

/**
 * A service responsible for operations on Student entities.
 * 
 * @author smelody
 * 
 */

@Service
public interface StudentService {
    
    /**
     * Returns all students.
     * 
     * @FIXME Need to decide on pagination or returning list of student IDs.
     * @return List of students
     * @throws Exception
     *             If an exception occurs fetching the students.
     */
    
    public Collection<Student> getAll() throws Exception;
    
    /**
     * Deletes the student by the given ID
     * 
     * @param id
     * @throws Exception
     *             If an error occurs deleting the student.
     */
    public void deleteById(int id) throws Exception;
    
    /**
     * Adds a new student, or replace one with the same ID if a student already exists.
     * 
     * @param student
     * @throws Exception
     */
    public void addOrUpdate(Student student) throws Exception;
    
    /**
     * Returns the student with the given ID.
     * 
     * @param studentId
     * @return
     */
    
    @PostAuthorize( "hasPermission(returnObject, 'READ') ")
    public Student getStudentById(int studentId);
    
    /**
     * Returns any school that this student has ever been enrolled in.
     * 
     * @param student
     * @return
     */
    public Collection<School> getSchoolsByStudent(Student student);
    
}
