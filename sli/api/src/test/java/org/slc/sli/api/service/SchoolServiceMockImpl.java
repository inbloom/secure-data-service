package org.slc.sli.api.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slc.sli.domain.School;
import org.slc.sli.domain.SchoolTestData;

public class SchoolServiceMockImpl implements SchoolService {
    
    private Map<Integer, School> schools = new LinkedHashMap<Integer, School>();
    
    public SchoolServiceMockImpl() throws Exception {
        addOrUpdate(SchoolTestData.buildTestSchool1());
        addOrUpdate(SchoolTestData.buildTestSchool2());
    }
    
    @Override
    public Collection<School> getAll() throws Exception {
        return schools.values();
    }
    
    @Override
    public void deleteById(int id) throws Exception {
        schools.remove(id);
    }
    
    @Override
    public void addOrUpdate(School school) throws Exception {
        if (school.getSchoolId() == null) {
            int max = 0;
            for (int key : schools.keySet()) {
                max = Math.max(key, max);
            }
            school.setSchoolId(max + 1);
        }
        schools.put(school.getSchoolId(), school);
    }
    
    @Override
    public School getSchoolById(int schoolId) {
        return schools.get(schoolId);
    }
    
}
