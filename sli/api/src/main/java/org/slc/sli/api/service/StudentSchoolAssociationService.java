package org.slc.sli.api.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import org.slc.sli.domain.StudentSchoolAssociation;

/**
 * Provides service methods for StudentSchoolAssociations - objects that define enrollment in a
 * school.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Service
public interface StudentSchoolAssociationService {
    
    /**
     * Returns the student to school associations for the given school.
     * 
     * @param school
     * @return
     */
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForSchool(int schoolId);
    
    /**
     * Returns the student to school associations for the given school and student.
     * 
     * @param schoolId
     * @param studentId
     * @return
     */
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudentAndSchool(int studentId,
            int schoolId);
    
    /**
     * Returns the student to school associations for the given student.
     * 
     * @param studentId
     * @return
     */
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudent(int studentId);
    
    /**
     * Returns the student to school association represented by the given id.
     * 
     * @param associationId
     */
    public StudentSchoolAssociation getStudentSchoolAssociationById(int associationId);
    
    /**
     * Creates or updates a student to school association.
     * 
     * @param association
     */
    public Integer addOrUpdate(StudentSchoolAssociation association);
    
    /**
     * Deletes a student to school association.
     * 
     * @param associationId
     */
    public void deleteById(int associationId);
}
