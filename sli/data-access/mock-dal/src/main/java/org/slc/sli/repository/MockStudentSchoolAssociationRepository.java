package org.slc.sli.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.StudentSchoolAssociation;

@Component
public class MockStudentSchoolAssociationRepository extends MockCrudRepository<StudentSchoolAssociation, Integer> implements StudentSchoolAssociationRepository {
    
    public MockStudentSchoolAssociationRepository() {
        super(new MockIDProvider<StudentSchoolAssociation, Integer>() {
            final AtomicInteger idSeq = new AtomicInteger(100);
            
            @Override
            public Integer getIDForEntity(StudentSchoolAssociation entity) {
                if (entity.getAssociationId() == null) {
                    entity.setAssociationId(idSeq.getAndIncrement());
                }
                return entity.getAssociationId();
            }
            
        });
        
    }

    @Override
    public Iterable<StudentSchoolAssociation> findByStudentIdAndSchoolId(int studentId, int schoolId) {
        List<StudentSchoolAssociation> results = new ArrayList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : super.map.values()) {
            if (ssa.getStudentId() == studentId && ssa.getSchoolId() == schoolId) {
                results.add(ssa);
            }
        }
        return results;
    }
    
    @Override
    public Iterable<StudentSchoolAssociation> findByStudentId(int studentId) {
        List<StudentSchoolAssociation> results = new ArrayList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : super.map.values()) {
            if (ssa.getStudentId() == studentId) {
                results.add(ssa);
            }
        }
        return results;
    }
    
    @Override
    public Iterable<StudentSchoolAssociation> findBySchoolId(int schoolId) {
        List<StudentSchoolAssociation> results = new ArrayList<StudentSchoolAssociation>();
        for (StudentSchoolAssociation ssa : super.map.values()) {
            if (ssa.getSchoolId() == schoolId) {
                results.add(ssa);
            }
        }
        return results;
    }

    @Override
    public StudentSchoolAssociation saveWithAssoc(StudentSchoolAssociation ssa) {
        // TODO Auto-generated method stub
     return   super.save(ssa);
    }
    
    
}
