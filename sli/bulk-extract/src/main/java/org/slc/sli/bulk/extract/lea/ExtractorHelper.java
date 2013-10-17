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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
        edOrgExtractHelper = new EdOrgExtractHelper();
        dateHelper = new DateHelper();
    }

    public ExtractorHelper(EdOrgExtractHelper edOrgExtractHelper) {
        this.edOrgExtractHelper = edOrgExtractHelper;
        dateHelper = new DateHelper();
    }

    private DateHelper dateHelper;

    private EdOrgExtractHelper edOrgExtractHelper;


    /**
     * Returns all current schools of the student.
     *
     * @param student - Student entity
     *
     * @return - Current schools associated with student
     */
    //F316: Old pipeline - remove this
    public Set<String> fetchCurrentSchoolsForStudent(Entity student) {
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
            if (lineages != null) {
                studentSchools.addAll(lineages);
            }
        }
        return studentSchools;
    }

    /**
     * Fetches the associated edOrgs with their expiration date for a student.
     *
     * @param student - Student entity
     *
     * @return - Student edOrgs and expiration dates
     */
    public Map<String, DateTime> fetchAllEdOrgsForStudent(Entity student) {
        Map<String, DateTime> studentEdOrgs = new HashMap<String, DateTime>();
        Map<String, List<Map<String, Object>>> data = student.getDenormalizedData();
        if (!data.containsKey("schools")) {
            return studentEdOrgs;
        }

        List<Map<String, Object>> schools = data.get("schools");
        for (Map<String, Object> school : schools) {
            updateEdorgToDateMap(school, studentEdOrgs, ParameterConstants.ID,
                    ParameterConstants.ENTRY_DATE, ParameterConstants.EXIT_WITHDRAW_DATE);
        }

        return studentEdOrgs;
    }

    /**
     * build edOrg to date map.
     *
     * @param edOrg - EdOrg entity body
     * @param edOrgToDate - Old edOrg to date map
     * @param edOrgIdField - EdOrg ID field name
     * @param beginDateField - Begin date field name
     * @param endDateField - end date field name
     */
    public void updateEdorgToDateMap(Map<String, Object> edOrg, Map<String, DateTime> edOrgToDate,
            String edOrgIdField, String beginDateField, String endDateField) {
        if (!dateHelper.getDate(edOrg, beginDateField).isAfter(DateTime.now())) {
            String id = (String) edOrg.get(edOrgIdField);
            DateTime expirationDateFromData = dateHelper.getDate(edOrg, endDateField);

            List<String> lineage = edOrgExtractHelper.getEdOrgLineages().get(id);
            List<String> edOrgLineage = new ArrayList<String>();
            if (lineage != null) {
                edOrgLineage.addAll(lineage);
            } else {
                edOrgLineage.add(id);
            }
            for (String edOrgId : edOrgLineage) {
                DateTime existingExpirationDate = edOrgToDate.get(edOrgId);
                DateTime finalExpirationDate = expirationDateFromData;
                if (edOrgToDate.containsKey(edOrgId)) {
                    if ((expirationDateFromData == null) || (existingExpirationDate == null)) {
                        finalExpirationDate = null;
                    } else if (existingExpirationDate.isAfter(expirationDateFromData)) {
                        finalExpirationDate = existingExpirationDate;
                    }
                }
                edOrgToDate.put(edOrgId, finalExpirationDate);
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
