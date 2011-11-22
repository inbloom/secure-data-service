package org.slc.sli.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

import org.slc.sli.domain.School;

@Component
public class MockSchoolRepository extends MockCrudRepository<School, Integer> implements SchoolRepository {
    
    public MockSchoolRepository() {
        super(new MockIDProvider<School, Integer>() {
            final AtomicInteger idSeq = new AtomicInteger(100);
            
            @Override
            public Integer getIDForEntity(School entity) {
                if (entity.getSchoolId() == null) {
                    entity.setSchoolId(idSeq.getAndIncrement());
                }
                return entity.getSchoolId();
            }
            
        });
        
    }
    
    @Override
    public Iterable<School> findByStateOrganizationId(String stateOrganizationId) {
        List<School> results = new LinkedList<School>();
        for (School s : map.values()) {
            if (stateOrganizationId.equals(s.getStateOrganizationId())) {
                results.add(s);
            }
        }
        return results;
    }
    
    @Override
    public void deleteWithAssoc(int schoolId) {
        // TODO Auto-generated method stub
        super.delete(schoolId);
    }
}
