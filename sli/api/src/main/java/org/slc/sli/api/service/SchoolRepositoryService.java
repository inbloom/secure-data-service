/**
 * 
 */
package org.slc.sli.api.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slc.sli.domain.School;
import org.slc.sli.repository.SchoolRepository;

/**
 * School service that operates on a repository.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Service
public class SchoolRepositoryService implements SchoolService {
    
    @Autowired
    SchoolRepository schoolRepository;
    
    /**
     * @see org.slc.sli.api.service.SchoolService#getAll()
     */
    @Override
    public Collection<School> getAll() throws Exception {
        Iterable<School> schoolIter = schoolRepository.findAll();
        Collection<School> schools = new ArrayList<School>();
        for (School school : schoolIter) {
            schools.add(school);
        }
        return schools;
    }
    
    /**
     * @see org.slc.sli.api.service.SchoolService#deleteById(int)
     */
    @Override
    public void deleteById(int schoolId) throws Exception {
        School school = schoolRepository.findOne(schoolId);
        if (school != null) {
            schoolRepository.deleteWithAssoc(schoolId);
        }
    }
    
    /**
     * @see org.slc.sli.api.service.SchoolService#addOrUpdate(org.slc.sli.domain.School)
     */
    @Override
    public void addOrUpdate(School school) throws Exception {
        schoolRepository.save(school);
    }
    
    /**
     * @see org.slc.sli.api.service.SchoolService#getSchoolById(int)
     */
    @Override
    public School getSchoolById(int schoolId) {
        return schoolRepository.findOne(schoolId);
    }
    
}
