package org.slc.sli.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.repository.custom.StudentSchoolAssociationRepositoryCustom;

/**
 * NOTE: These classes and interfaces have been deprecated and replaced with the new Entity and
 * Mongo repository classes.
 * 
 * Old student school repo
 * 
 */
@Repository
@Deprecated
public interface StudentSchoolAssociationRepository extends
        PagingAndSortingRepository<StudentSchoolAssociation, Integer>, StudentSchoolAssociationRepositoryCustom {
    
    public Iterable<StudentSchoolAssociation> findByStudentIdAndSchoolId(int studentId, int schoolId);
    
    public Iterable<StudentSchoolAssociation> findByStudentId(int studentId);
    
    public Iterable<StudentSchoolAssociation> findBySchoolId(int schoolId);
    
}
