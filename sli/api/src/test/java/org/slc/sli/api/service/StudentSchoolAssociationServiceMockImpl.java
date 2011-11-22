package org.slc.sli.api.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slc.sli.domain.StudentSchoolAssociation;

public class StudentSchoolAssociationServiceMockImpl implements StudentSchoolAssociationService {
    
    Map<Integer,StudentSchoolAssociation> map = new HashMap<Integer,StudentSchoolAssociation>();
    AtomicInteger idSeq = new AtomicInteger();
    
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForSchool(int schoolId) {
        List<StudentSchoolAssociation> result = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : map.values()) {
            if (ssa.getSchoolId() == schoolId) {
                result.add(ssa);
            }
        }
        return result;
    }
    
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudentAndSchool(int studentId,
            int schoolId) {
        List<StudentSchoolAssociation> result = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : map.values()) {
            if (ssa.getStudentId() == studentId && ssa.getSchoolId() == schoolId) {
                result.add(ssa);
            }
        }
        return result;
    }
    
    
    @Override
    public Collection<StudentSchoolAssociation> getStudentSchoolAssociationsForStudent(int studentId) {
        List<StudentSchoolAssociation> result = new LinkedList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : map.values()) {
            if (ssa.getStudentId() == studentId) {
                result.add(ssa);
            }
        }
        return result;
    }
    
    
    @Override
    public StudentSchoolAssociation getStudentSchoolAssociationById(int associationId) {
        return map.get(associationId);
    }
    
    
    @Override
    public Integer addOrUpdate(StudentSchoolAssociation association) {
        if (association.getAssociationId() == null) {
            association.setAssociationId(idSeq.getAndIncrement());
        }
        map.put(association.getAssociationId(), association);
        return association.getAssociationId();
    }
    
    
    @Override
    public void deleteById(int associationId) {
        map.remove(associationId);
    }
    
}
