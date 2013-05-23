/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.bulk.extract.lea;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
public class ExtractorHelper{

    public ExtractorHelper() {
        //LocalEdOrgExtractHelper is optional
    }

    public ExtractorHelper(LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }

    private DateHelper dateHelper;

    private LocalEdOrgExtractHelper localEdOrgExtractHelper;


    /**
     * returns all current schools of the student
     * @param student
     * @return
     */
    @SuppressWarnings("unchecked")
    public Set<String> fetchCurrentSchoolsFromStudent(Entity student) {
        if (dateHelper == null) {
            dateHelper = new DateHelper();
        }
        Set<String> studentSchools = new HashSet<String>();
        Map<String, List<Map<String, Object>>> data = student.getDenormalizedData();
        if (!data.containsKey("schools")) {
            return studentSchools;
        }
        List<Map<String, Object>> schools = data.get("schools");
        for (Map<String, Object> school : schools) {
             if (dateHelper.isFieldExpired(school, "exitWithdrawDate")) {
                continue;
            }
            String id = (String)school.get("_id");
            List<String> lineages = localEdOrgExtractHelper.getEdOrgLineages().get(id);
            if(lineages != null) {
                studentSchools.addAll(lineages);
            }
        }
        return studentSchools;
    }
    
    public void setDateHelper(DateHelper helper) {
        this.dateHelper = helper;
    }

    /**
     * returns all parents of the student
     * @param student
     * @return
     */
    public Set<String> fetchCurrentParentsFromStudent(Entity student) {
        Set<String> parents = new TreeSet<String>();
        if (student.getEmbeddedData().containsKey(EntityNames.STUDENT_PARENT_ASSOCIATION)) {
            for (Entity assoc : student.getEmbeddedData().get(EntityNames.STUDENT_PARENT_ASSOCIATION)) {
                String parentId = (String) assoc.getBody().get(ParameterConstants.PARENT_ID);
                if (parentId != null) {
                    parents.add(parentId);
                }
            }
        }
        return parents;
    }

    /**
     * uses the date helper to tell us if the entity is current or not
     * 
     * @param staffAssociation
     * @return
     */
    public boolean isStaffAssociationCurrent(Entity staffAssociation) {
        if (dateHelper == null) {
            dateHelper = new DateHelper();
        }
        return !dateHelper.isFieldExpired(staffAssociation.getBody(), ParameterConstants.END_DATE);
    }
    
    public Map<String, String> buildSubToParentEdOrgCache(EntityToLeaCache edOrgCache) {
    	Map<String, String> result = new HashMap<String, String>();
    	for(String lea : edOrgCache.getEntityIds()) {
    		for (String child : edOrgCache.getEntriesById(lea)) {
    			result.put(child, lea);
    		}
    	}
    	return result;
    }

    public LocalEdOrgExtractHelper getLocalEdOrgExtractHelper() {
        return localEdOrgExtractHelper;
    }

    public void setLocalEdOrgExtractHelper(LocalEdOrgExtractHelper localEdOrgExtractHelper) {
        this.localEdOrgExtractHelper = localEdOrgExtractHelper;
    }
}
