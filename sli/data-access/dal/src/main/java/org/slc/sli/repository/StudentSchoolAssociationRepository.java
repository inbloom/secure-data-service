package org.slc.sli.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import org.slc.sli.domain.StudentSchoolAssociation;
import org.slc.sli.repository.custom.StudentSchoolAssociationRepositoryCustom;

@Repository
public interface StudentSchoolAssociationRepository extends
        PagingAndSortingRepository<StudentSchoolAssociation, Integer>, StudentSchoolAssociationRepositoryCustom {
    
    public Iterable<StudentSchoolAssociation> findByStudentIdAndSchoolId(int studentId, int schoolId);
    
    public Iterable<StudentSchoolAssociation> findByStudentId(int studentId);
    
    public Iterable<StudentSchoolAssociation> findBySchoolId(int schoolId);
    
}
