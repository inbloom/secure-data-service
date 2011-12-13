package org.slc.sli.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.Student;

/**
 * Mock for old repo
 * 
 */
@Component
public class MockStudentRepository extends MockCrudRepository<Student, Integer> implements StudentRepository {
    
    public MockStudentRepository() {
        super(new MockIDProvider<Student, Integer>() {
            final AtomicInteger idSeq = new AtomicInteger(100);
            
            @Override
            public Integer getIDForEntity(Student entity) {
                if (entity.getStudentId() == null) {
                    // This is a bogus, but we cannot access the underlying map from a constructor
                    // to figure out the max Id.
                    entity.setStudentId(idSeq.getAndIncrement());
                }
                return entity.getStudentId();
            }
            
        });
        
    }

    @Override
    public Iterable<Student> findByStudentSchoolId(String studentSchoolId) {
        List<Student> results = new LinkedList<Student>();
        for (Student s : map.values()) {
            if (studentSchoolId.equals(s.getStudentSchoolId())) {
                results.add(s);
            }
        }
        return results;
    }

    @Override
    public void deleteWithAssoc(int studentId) {
        // TODO Auto-generated method stub
        super.delete(studentId);  
    }
    
    
}
