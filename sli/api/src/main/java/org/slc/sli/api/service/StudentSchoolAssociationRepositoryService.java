/**
 * 
 */
package org.slc.sli.api.service;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.repository.StudentSchoolAssociationRepository;

/**
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Service
public class StudentSchoolAssociationRepositoryService implements StudentSchoolAssociationService {
    
    @Autowired
    StudentSchoolAssociationRepository associationRepo;
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudent(int studentId) {
        Collection<StudentSchoolAssociation> associations = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : associationRepo.findByStudentId(studentId)) {
            associations.add(ssa);
        }
        return associations;
    }
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForSchool(int schoolId) {
        LinkedList<StudentSchoolAssociation> results = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : associationRepo.findBySchoolId(schoolId)) {
            results.add(ssa);
        }
        return results;
    }
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudentAndSchool(int studentId,
            int schoolId) {
        LinkedList<StudentSchoolAssociation> results = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : associationRepo.findByStudentIdAndSchoolId(studentId, schoolId)) {
            results.add(ssa);
        }
        return results;
    }
    
    @Override
    public StudentSchoolAssociation getStudentSchoolAssociationById(int associationId) {
        return associationRepo.findOne(associationId);
    }
    
    @Override
    public Integer addOrUpdate(StudentSchoolAssociation association) {
        StudentSchoolAssociation ssa = associationRepo.saveWithAssoc(association);
        return ssa.getAssociationId();
    }
    
    @Override
    public void deleteById(int associationId) {
        StudentSchoolAssociation found = associationRepo.findOne(associationId);
        if (found != null) {
            associationRepo.delete(associationId);
        }
    }
    
}
