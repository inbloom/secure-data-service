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


package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.StaffCohortAssociation;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;

public class StaffCohortAssociationGenerator {
	private static final Logger log = Logger.getLogger(StaffCohortAssociationGenerator.class);

	private static String beginDate = "2011-03-04";
	private static String endDate = "2012-03-04";

    /**
     * Generates a list of StudentCohortAssociation from a CohortMeta.
     *
     * @param cohortMeta
     * 
     * @return <code>List<StudentCohortAssociation></code>
     */
    public static StaffCohortAssociation generateLowFi(CohortMeta cohortMeta) {
        String cohortId = cohortMeta.id;
        String schoolId = cohortMeta.programMeta==null ? cohortMeta.schoolMeta.id : cohortMeta.programMeta.orgId;
        Set<String> staffIds = cohortMeta.staffIds;
        
        return generateLowFi(cohortId, staffIds, schoolId);
    }

    /**
     * Generates a StaffCohortAssociation between a cohort and a staff  
     * with a school as a reference.
     *
     * @param cohortId
     * @param staffId
     * @param schoolId
     * 
     * @return <code>StaffCohortAssociation</code>
     */
    public static StaffCohortAssociation generateLowFi(String cohortId, String staffId, String schoolId) {

        StaffCohortAssociation staffCohortAssoc = new StaffCohortAssociation();
        
        // construct and add the staff reference
        StaffIdentityType sit = new StaffIdentityType();
        sit.setStaffUniqueStateId(staffId);
        StaffReferenceType srt = new StaffReferenceType();
        srt.setStaffIdentity(sit);
        staffCohortAssoc.getStaffReference().add(srt);

        // construct and add the Cohort Reference       
        CohortIdentityType ci = new CohortIdentityType();
        ci.setCohortIdentifier(cohortId);
        ci.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        CohortReferenceType crt = new CohortReferenceType();
        crt.setCohortIdentity(ci);
        staffCohortAssoc.getCohortReference().add(crt);

        //set begin and end dates
        staffCohortAssoc.setBeginDate(beginDate);
        staffCohortAssoc.setEndDate(endDate);
        
        staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
        
        return staffCohortAssoc;
    }

    /**
     * Generates a StaffCohortAssociation between a cohort and a list of staffs 
     * with a school as a reference.
     *
     * @param cohortId
     * @param staffIds
     * @param schoolIds
     * 
     * @return <code>StaffCohortAssociation</code>
     */
    public static StaffCohortAssociation generateLowFi(String cohortId, Collection<String> staffIds, String schoolId) {

        StaffCohortAssociation staffCohortAssoc = new StaffCohortAssociation();
        
        // construct and add the staff references
        List<StaffReferenceType> srts = new ArrayList<StaffReferenceType>(staffIds.size());
        for (String staffId : staffIds) {
            StaffIdentityType sit = new StaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            StaffReferenceType srt = new StaffReferenceType();
            srt.setStaffIdentity(sit);
            srts.add(srt);
        }
        staffCohortAssoc.getStaffReference().addAll(srts);

        // construct and add the Cohort Reference
        CohortIdentityType ci = new CohortIdentityType();
        ci.setCohortIdentifier(cohortId);
        ci.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        CohortReferenceType crt = new CohortReferenceType();
        crt.setCohortIdentity(ci);
        staffCohortAssoc.getCohortReference().add(crt);
        
        //set begin and end dates
        staffCohortAssoc.setBeginDate(beginDate);
        staffCohortAssoc.setEndDate(endDate);
        
        staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
        
        return staffCohortAssoc;
    }

    /**
     * Generates a StaffCohortAssociation between a cohort and a list of staffs 
     * with a list of schools as a reference.
     *
     * @param cohortId
     * @param staffIds
     * @param schoolIds
     * 
     * @return <code>StaffCohortAssociation</code>
     */
    public static StaffCohortAssociation generateLowFi(String cohortId, Collection<String> staffIds, Collection<String> schoolIds) {

        StaffCohortAssociation staffCohortAssoc = new StaffCohortAssociation();
        
        // construct and add the staff references
        List<StaffReferenceType> srts = new ArrayList<StaffReferenceType>(staffIds.size());
        for (String staffId : staffIds) {
            StaffIdentityType sit = new StaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            StaffReferenceType srt = new StaffReferenceType();
            srt.setStaffIdentity(sit);
            srts.add(srt);
        }
        staffCohortAssoc.getStaffReference().addAll(srts);

        // construct and add the Cohort Reference
        CohortIdentityType ci = new CohortIdentityType();
        ci.setCohortIdentifier(cohortId);
        ci.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(schoolIds);
        CohortReferenceType crt = new CohortReferenceType();
        crt.setCohortIdentity(ci);
        staffCohortAssoc.getCohortReference().add(crt);
        
        //set begin and end dates
        staffCohortAssoc.setBeginDate(beginDate);
        staffCohortAssoc.setEndDate(endDate);
        
        staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
        
        return staffCohortAssoc;
    }
}

