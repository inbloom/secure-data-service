/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.dashboard.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.slc.sli.dashboard.entity.Config.Data;
import org.slc.sli.dashboard.entity.EdOrgKey;
import org.slc.sli.dashboard.entity.GenericEntity;
import org.slc.sli.dashboard.entity.util.GenericEntityComparator;
import org.slc.sli.dashboard.entity.util.GenericEntityEnhancer;
import org.slc.sli.dashboard.manager.ApiClientManager;
import org.slc.sli.dashboard.manager.UserEdOrgManager;
import org.slc.sli.dashboard.util.CacheableUserData;
import org.slc.sli.dashboard.util.Constants;
import org.slc.sli.dashboard.util.DashboardException;
import org.slc.sli.dashboard.util.SecurityUtil;
import org.slc.sli.dashboard.web.util.TreeGridDataBuilder;

/**
 * Retrieves and applies necessary business logic to obtain institution data
 * 
 * @author syau
 * 
 */
public class UserEdOrgManagerImpl extends ApiClientManager implements UserEdOrgManager {
    
    private GenericEntity getParentEducationalOrganization(String token, GenericEntity edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganization(token, edOrgOrSchool);
    }
    
    private List<GenericEntity> getParentEducationalOrganizations(String token, List<GenericEntity> edOrgOrSchool) {
        return getApiClient().getParentEducationalOrganizations(token, edOrgOrSchool);
    }
    
    protected boolean isEducator() {
        return !SecurityUtil.isNotEducator();
    }
    
    /**
     * read token. Then, find district name associated with school.
     * 
     * @param token
     *            token-id, it is also using token as key value for cache.
     * @return District name
     */
    @Override
    @CacheableUserData
    public EdOrgKey getUserEdOrg(String token) {
        GenericEntity edOrg = null;
        
        // For state-level ed-org - need to take default config, so keep state
        // ed org
        if (!isEducator()) {
            
            String id = getApiClient().getId(token);
            GenericEntity staff = getApiClient().getStaffWithEducationOrganization(token, id, null);
            if (staff != null) {
                
                GenericEntity staffEdOrg = (GenericEntity) staff.get(Constants.ATTR_ED_ORG);
                if (staffEdOrg != null) {
                    
                    @SuppressWarnings("unchecked")
                    List<String> edOrgCategories = (List<String>) staffEdOrg.get(Constants.ATTR_ORG_CATEGORIES);
                    if (edOrgCategories != null && !edOrgCategories.isEmpty()) {
                        
                        for (String edOrgCategory : edOrgCategories) {
                            if (edOrgCategory.equals(Constants.STATE_EDUCATION_AGENCY)) {
                                edOrg = staffEdOrg;
                                break;
                            } else if (edOrgCategory.equals(Constants.LOCAL_EDUCATION_AGENCY)) {
                                edOrg = staffEdOrg;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // otherwise get school's parent ed-org
        if (edOrg == null) {
            
            // get list of school
            List<GenericEntity> schools = getMySchools(token);
            
            if (schools != null && !schools.isEmpty()) {
                
                // read first school
                GenericEntity school = schools.get(0);
                
                // read parent organization
                edOrg = getParentEducationalOrganization(getToken(), school);
                if (edOrg == null) {
                    throw new DashboardException(
                            "No data is available for you to view. Please contact your IT administrator.");
                }
            }
        }
        
        // create ed-org key and save to cache
        if (edOrg != null) {
            return new EdOrgKey(edOrg.getId());
            
        }
        return null;
    }
    
    /**
     * Get user's schools. Cache the results so we don't have to make the call
     * twice.
     * 
     * @return
     */
    private List<GenericEntity> getSchools(String token) {
        
        return getApiClient().getSchools(token, null);
    }
    
    /**
     * Get user's associated schools. Cache the results so we don't have to make the call
     * twice.
     * 
     * @return
     */
    private List<GenericEntity> getMySchools(String token) {
        
        return getApiClient().getMySchools(token);
    }
    
    /**
     * Returns the institutional hierarchy visible to the user with the given
     * auth token as a list of generic entities, with the ed-org level flattened
     * This assumes there are no cycles in the education organization hierarchy
     * tree.
     * 
     * @return
     */
    private List<GenericEntity> getUserInstHierarchy(String token) {
        // Find all the schools first.
        List<GenericEntity> schools = getMySchools(token);
        if (schools == null) {
            return Collections.emptyList();
        }
        
        // This maps ids from educational organisations to schools reachable
        // from it via the "child" relationship
        Map<String, Set<GenericEntity>> schoolReachableFromEdOrg = new HashMap<String, Set<GenericEntity>>();
        
        // This just maps ed org ids to ed org objects.
        Map<String, GenericEntity> edOrgIdMap = new HashMap<String, GenericEntity>();
        
        for (GenericEntity school : schools) {
            String parentEdOrgId = (String) school.get(Constants.ATTR_PARENT_EDORG);
            if (parentEdOrgId != null) {
                if (!schoolReachableFromEdOrg.keySet().contains(parentEdOrgId)) {
                    schoolReachableFromEdOrg.put(parentEdOrgId, new HashSet<GenericEntity>());
                }
                schoolReachableFromEdOrg.get(parentEdOrgId).add(school);
            }
        }
        
        // traverse the ancestor chain from each school and find ed orgs that
        // the school is reachable from
        List<GenericEntity> edOrgs = getParentEducationalOrganizations(token, schools);
        while (!edOrgs.isEmpty()) {
            for (GenericEntity edOrg : edOrgs) {
                String parentEdOrgId = (String) edOrg.get(Constants.ATTR_PARENT_EDORG);
                String edOrgId = edOrg.getId();
                
                // if parentEdOrgId exists, you are not the top organization
                if (parentEdOrgId != null) {
                    
                    // insert ed-org id to - edOrg mapping
                    edOrgIdMap.put(edOrgId, edOrg);
                    
                    // insert ed-org - school mapping into the reverse map
                    if (!schoolReachableFromEdOrg.keySet().contains(parentEdOrgId)) {
                        schoolReachableFromEdOrg.put(parentEdOrgId, new HashSet<GenericEntity>());
                    }
                    Set<GenericEntity> reachableSchool = schoolReachableFromEdOrg.get(edOrgId);
                    if (reachableSchool != null) {
                        schoolReachableFromEdOrg.get(parentEdOrgId).addAll(reachableSchool);
                    }
                }
            }
            // next in the ancestor chain
            edOrgs = getParentEducationalOrganizations(token, edOrgs);
        }
        
        // build result list
        List<GenericEntity> retVal = new ArrayList<GenericEntity>();
        for (String edOrgId : schoolReachableFromEdOrg.keySet()) {
            GenericEntity obj = new GenericEntity();
            GenericEntity edOrgEntity = edOrgIdMap.get(edOrgId);
            // if edOrgEntity is null, it may be API could not return entity
            // because of error code 403.
            if (edOrgEntity != null) {
                obj.put(Constants.ATTR_NAME, edOrgEntity.get(Constants.ATTR_NAME_OF_INST));
                obj.put(Constants.ATTR_ID, edOrgEntity.get(Constants.ATTR_ID));
                // convert school ids to the school object array and sort based on the name of
                // the institution
                Set<GenericEntity> reachableSchools = new TreeSet<GenericEntity>(new GenericEntityComparator(
                        Constants.ATTR_NAME_OF_INST, String.class));
                reachableSchools.addAll(schoolReachableFromEdOrg.get(edOrgId));
                obj.put(Constants.ATTR_SCHOOLS, reachableSchools);
                retVal.add(obj);
            }
        }
        
        Collection<GenericEntity> orphanSchools = findOrphanSchools(schools, schoolReachableFromEdOrg);
        // insert a dummy edorg for all orphan schools.
        if (!orphanSchools.isEmpty()) {
            insertSchoolsUnderDummyEdOrg(retVal, orphanSchools);
        }
        // Sort the Districts based on the District Name
        Collections.sort(retVal, new GenericEntityComparator(Constants.ATTR_NAME, String.class));
        
        return retVal;
    }
    
    // ------------- helper functions ----------------
    
    private static Collection<GenericEntity> findOrphanSchools(List<GenericEntity> schools,
            Map<String, Set<GenericEntity>> schoolReachableFromEdOrg) {
        Vector<GenericEntity> orphanSchools = new Vector<GenericEntity>();
        for (int i = 0; i < schools.size(); i++) {
            GenericEntity s = schools.get(i);
            boolean isOrphan = true;
            for (Set<GenericEntity> reachableSchools : schoolReachableFromEdOrg.values()) {
                if (reachableSchools.contains(s)) {
                    isOrphan = false;
                    break;
                }
            }
            if (isOrphan) {
                orphanSchools.add(s);
            }
        }
        return orphanSchools;
    }
    
    // Insert schools into the list under a "dummy" ed-org
    private static List<GenericEntity> insertSchoolsUnderDummyEdOrg(List<GenericEntity> retVal,
            Collection<GenericEntity> schools) {
        if (retVal != null) {
            GenericEntity obj = new GenericEntity();
            obj.put(Constants.ATTR_NAME, DUMMY_EDORG_NAME);
            obj.put(Constants.ATTR_SCHOOLS, schools);
            retVal.add(obj);
        }

        return retVal;
    }
    
    /**
     * Override from UserEdOrgManager.
     * Signature is pre-defined by the architect.
     */
    @Override
    @CacheableUserData
    public GenericEntity getUserInstHierarchy(String token, Object key, Data config) {
        
        List<GenericEntity> entities = getUserInstHierarchy(token);
        GenericEntity entity = new GenericEntity();
        entity.put(Constants.ATTR_ROOT, entities);
        return entity;
    }
    
    /**
     * Override from UserEdOrgManager.
     * Signature is pre-defined by the architect.
     */
    @Override
    public GenericEntity getUserCoursesAndSections(String token, Object schoolIdObj, Data config) {
        
        String schoolId = (String) schoolIdObj;
        List<GenericEntity> entities = getApiClient().getCoursesSectionsForSchool(token, schoolId);
        GenericEntity entity = new GenericEntity();
        entity.put(Constants.ATTR_ROOT, entities);
        return entity;
    }
    
    /**
     * Get list of subjects, courses, and sections for a school.
     * Pass out a flattened structure with parent/child relationships defined.
     * 
     * @param token
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public GenericEntity getUserSectionList(String token, Object schoolIdObj, Data config) {
        
        String schoolId = (String) schoolIdObj;
        List<GenericEntity> courses = getApiClient().getCoursesSectionsForSchool(token, schoolId);
        
        // set any null subjects to Miscellaneous
        for (GenericEntity course : courses) {
            if (course.getString(Constants.ATTR_SUBJECTAREA) == null) {
                course.put(Constants.ATTR_SUBJECTAREA, "Miscellaneous");
            }
        }
        
        // sort courses by subject area
        Collections.sort(courses, new CourseSubjectComparator());
        
        Map<String, GenericEntity> subjects = new LinkedHashMap<String, GenericEntity>();
        int subjectIndex = 0;
        
        // do some restructuring
        for (GenericEntity course : courses) {
            
            String subjectArea = course.getString(Constants.ATTR_SUBJECTAREA);
            GenericEntity subject = null;
            
            // add/get subject entity
            if (!subjects.containsKey(subjectArea)) {
                subject = new GenericEntity();
                subject.put(Constants.ATTR_NAME, subjectArea);
                subject.put(Constants.ATTR_ID, String.valueOf(++subjectIndex));
                List<GenericEntity> c = new ArrayList<GenericEntity>();
                subject.put(Constants.ATTR_COURSES, c);
                subjects.put(subjectArea, subject);
            } else {
                subject = subjects.get(subjectArea);
            }
            
            course.put(Constants.ATTR_NAME, course.getString(Constants.ATTR_COURSE_TITLE));
            course.remove(Constants.ATTR_LINKS);
            ((List<GenericEntity>) subject.get(Constants.ATTR_COURSES)).add(course);
            
            List<GenericEntity> sections = (List<GenericEntity>) course.get(Constants.ATTR_SECTIONS);
            if (sections != null && sections.size() > 0) {
                for (GenericEntity section : sections) {
                    section.put(Constants.ATTR_NAME, section.getString(Constants.ATTR_SECTION_NAME));
                    section.remove(Constants.ATTR_LINKS);
                }
            }
        }
        
        // call the tree grid builder to format/structure the data
        List<String> subLevels = new ArrayList<String>();
        subLevels.add(Constants.ATTR_COURSES);
        subLevels.add(Constants.ATTR_SECTIONS);
        List<GenericEntity> entities = TreeGridDataBuilder.build(new ArrayList<GenericEntity>(subjects.values()),
                subLevels);
        
        GenericEntity entity = new GenericEntity();
        entity.put(Constants.ATTR_ROOT, entities);
        return entity;
    }
    
    @Override
    public GenericEntity getStaffInfo(String token) {
        String id = getApiClient().getId(token);
        GenericEntity staffEntity = getApiClient().getStaffWithEducationOrganization(token, id, null);
        if (staffEntity == null) {
            staffEntity = new GenericEntity();
        }
        return staffEntity;
    }
    
    private class CourseSubjectComparator implements Comparator<GenericEntity> {
        
        @Override
        public int compare(GenericEntity course1, GenericEntity course2) {
            
            // compare subject area
            String subject1 = course1.getString(Constants.ATTR_SUBJECTAREA);
            String subject2 = course2.getString(Constants.ATTR_SUBJECTAREA);
            if (subject1 == null) {
                return -1;
            }
            if (subject2 == null) {
                return 1;
            }
            int i = subject1.compareToIgnoreCase(subject2);
            if (i != 0) {
                return i;
            }
            
            // compare course title
            String courseTitle1 = course1.getString(Constants.ATTR_COURSE_TITLE);
            String courseTitle2 = course2.getString(Constants.ATTR_COURSE_TITLE);
            if (courseTitle1 == null) {
                return -1;
            }
            if (courseTitle2 == null) {
                return 1;
            }
            return courseTitle1.compareToIgnoreCase(courseTitle2);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public GenericEntity getSchoolInfo(String token, Object schoolIdObj, Data config) {
        
        // get school entity
        GenericEntity school = getApiClient().getSchool(token, (String) schoolIdObj);
        
        // convert grade strings
        List<String> gradesOffered = school.getList("gradesOffered");
        List<String> gradesOfferedCode = new ArrayList<String>();
        for (String gradeOffered : gradesOffered) {
            gradesOfferedCode.add(GenericEntityEnhancer.convertGradeLevel(gradeOffered));
        }
        school.put("gradesOfferedCode", gradesOfferedCode);
        
        return school;
    }
}
