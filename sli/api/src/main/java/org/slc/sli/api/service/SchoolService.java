package org.slc.sli.api.service;

import java.util.Collection;

import org.slc.sli.domain.School;

/**
 * Provides CRUD and finder operations for schools.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
public interface SchoolService {
    
    /**
     * Returns all schools.
     * 
     * @FIXME Need to decide on pagination or returning list of school IDs.
     * @return List of schools
     * @throws Exception
     *             If an exception occurs fetching the schools.
     */
    public Collection<School> getAll() throws Exception;
    
    /**
     * Deletes the school by the given ID
     * 
     * @param id
     * @throws Exception
     *             If an error occurs deleting the school.
     */
    public void deleteById(int id) throws Exception;
    
    /**
     * Adds a new school, or replace one with the same ID if a school already exists.
     * 
     * @param school
     * @throws Exception
     */
    public void addOrUpdate(School school) throws Exception;
    
    /**
     * Returns the school with the given ID.
     * 
     * @param schoolId
     * @return
     */
    public School getSchoolById(int schoolId);
    
}
