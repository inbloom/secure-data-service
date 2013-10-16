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

import java.util.*;

import com.google.common.collect.HashMultimap;

import org.joda.time.DateTime;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.datetime.DateHelper;
import org.slc.sli.domain.Entity;
public class ExtractorHelper{

    public ExtractorHelper() {
        //LocalEdOrgExtractHelper is optional
    }

    public ExtractorHelper(EdOrgExtractHelper edOrgExtractHelper) {
        this.edOrgExtractHelper = edOrgExtractHelper;
    }

    private DateHelper dateHelper;

    private EdOrgExtractHelper edOrgExtractHelper;


    /**
     * returns all current schools of the student
     * @param student
     * @return
     */
    //F316: Old pipeline - remove this
    @SuppressWarnings("unchecked")
    public Set<String>fetchCurrentSchoolsForStudent(Entity student) {
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
            List<String> lineages = edOrgExtractHelper.getEdOrgLineages().get(id);
            if(lineages != null) {
                studentSchools.addAll(lineages);
            }
        }
        return studentSchools;
    }

    /**
     * Fetches the associated edOrgs with their expiration date for a student
     * @param student
     * @return
     */
    public Map<String, DateTime> fetchAllEdOrgsForStudent(Entity student) {

        Map<String, DateTime> studentEdOrgs = new HashMap<String, DateTime>();
        Map<String, List<Map<String, Object>>> data = student.getDenormalizedData();
        if (!data.containsKey("schools")) {
            return studentEdOrgs;
        }

        List<Map<String, Object>> schools = data.get("schools");
        for (Map<String, Object> school : schools) {
            buildEdorgToDateMap(school, studentEdOrgs, ParameterConstants.ID, ParameterConstants.ENTRY_DATE, ParameterConstants.EXIT_WITHDRAW_DATE);
        }

        return studentEdOrgs;
    }

    /**
     * build edorg to Date cache.
     *
     * @param edorg
     * @param edOrgToDate
     * @param edorgIdField
     * @param beginDateField
     * @param endDateField
     */
    public void buildEdorgToDateMap(Map<String, Object> edorg, Map<String, DateTime> edOrgToDate, String edorgIdField, String beginDateField, String endDateField) {
        if (dateHelper == null) {
            dateHelper = new DateHelper();
        }

        if (!dateHelper.getDate(edorg, beginDateField).isAfter(DateTime.now())) {
            String id = (String) edorg.get(edorgIdField);
            DateTime expirationDateFromData = dateHelper.getDate(edorg, endDateField);

            List<String> lineages = edOrgExtractHelper.getEdOrgLineages().get(id);
            if (lineages != null) {
                for (String edOrg : lineages) {
                    DateTime existingExpirationDate = edOrgToDate.get(edOrg);
                    DateTime finalExpirationDate = expirationDateFromData;
                    if (edOrgToDate.containsKey(edOrg) && (expirationDateFromData == null || existingExpirationDate == null)) {
                        finalExpirationDate = null;
                    } else if (edOrgToDate.containsKey(edOrg) &&  existingExpirationDate.isAfter(expirationDateFromData)) {
                        finalExpirationDate = edOrgToDate.get(edOrg);
                    }
                    edOrgToDate.put(edOrg, finalExpirationDate);
                }
            }
        }
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
    
    public Map<String, Collection<String>> buildSubToParentEdOrgCache(EntityToEdOrgCache edOrgCache) {
    	Map<String, String> result = new HashMap<String, String>();
        HashMultimap<String, String> map = HashMultimap.create();
    	for(String lea : edOrgCache.getEntityIds()) {
    		for (String child : edOrgCache.getEntriesById(lea)) {
    			result.put(child, lea);
                map.put(child, lea);
    		}
    	}
        return map.asMap();
    }

    public EdOrgExtractHelper getEdOrgExtractHelper() {
        return edOrgExtractHelper;
    }

    public void setEdOrgExtractHelper(EdOrgExtractHelper edOrgExtractHelper) {
        this.edOrgExtractHelper = edOrgExtractHelper;
    }
}
