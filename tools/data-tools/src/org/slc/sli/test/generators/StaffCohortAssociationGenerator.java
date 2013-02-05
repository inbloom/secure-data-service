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


package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCCohortIdentityType;
import org.slc.sli.test.edfi.entities.SLCCohortReferenceType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCStaffCohortAssociation;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.StaffCohortAssociation;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;

public class StaffCohortAssociationGenerator {
	private static String beginDate = "2011-03-04";
	private static String endDate = "2012-03-04";

    /**
     * Generates a list of StudentCohortAssociation from a CohortMeta.
     *
     * @param cohortMeta
     * 
     * @return <code>List<StudentCohortAssociation></code>
     */
    public static List<SLCStaffCohortAssociation> generateLowFi(CohortMeta cohortMeta) {
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
    
    
    public static List<SLCStaffCohortAssociation> generateLowFi(String cohortId, Collection<String> staffIds, String schoolId) {

    	List<SLCStaffCohortAssociation> staffCohortAssociations = new ArrayList<SLCStaffCohortAssociation>();
        
        // construct and add the staff reference
    	Iterator itr = staffIds.iterator();
    	 String staffId = null;
    	int counter = 0;
    	 while(itr.hasNext()) {
             staffId = (String) itr.next();
             counter++;
              	
	        SLCStaffIdentityType sit = new SLCStaffIdentityType();
	        sit.setStaffUniqueStateId(staffId);
	        SLCStaffReferenceType srt = new SLCStaffReferenceType();
	        srt.setStaffIdentity(sit);
	        
	        SLCStaffCohortAssociation staffCohortAssoc = new SLCStaffCohortAssociation();
            staffCohortAssoc.setStaffReference(srt);
            	        
	        
	
	        // construct and add the Cohort Reference       
	        SLCCohortIdentityType ci = new SLCCohortIdentityType();
	        ci.setCohortIdentifier(cohortId);
	        
	        SLCEducationalOrgIdentityType slceoit = new SLCEducationalOrgIdentityType ();
	        slceoit.setStateOrganizationId(schoolId);
	        SLCEducationalOrgReferenceType slceort = new SLCEducationalOrgReferenceType();
	        slceort.setEducationalOrgIdentity(slceoit);
        
        
	        ci.setEducationalOrgReference(slceort);
       
        
			SLCCohortReferenceType crt = new SLCCohortReferenceType();
			crt.setCohortIdentity(ci);
			staffCohortAssoc.setCohortReference(crt);
	    

        //set begin and end dates
      
			staffCohortAssoc.setBeginDate(beginDate);
			staffCohortAssoc.setEndDate(endDate);
        
			staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
			
			staffCohortAssociations.add(staffCohortAssoc);
    	 }
        return staffCohortAssociations;
    }
    /*
    public static StaffCohortAssociation generateLowFi(String cohortId, String staffId, String schoolId) {

        StaffCohortAssociation staffCohortAssoc = new StaffCohortAssociation();
        
        // construct and add the staff reference
        StaffIdentityType sit = new StaffIdentityType();
        sit.setStaffUniqueStateId(staffId);
        StaffReferenceType srt = new StaffReferenceType();
        srt.setStaffIdentity(sit);
        staffCohortAssoc.setStaffReference(srt);

        // construct and add the Cohort Reference       
        CohortIdentityType ci = new CohortIdentityType();
        ci.setCohortIdentifier(cohortId);
        
        EducationalOrgIdentityType edOrgType = new EducationalOrgIdentityType();
        edOrgType.setStateOrganizationId(schoolId);
        EducationalOrgReferenceType edOrgRef = new EducationalOrgReferenceType();
        edOrgRef.setEducationalOrgIdentity(edOrgType);
        
        
        ci.setEducationalOrgReference(edOrgRef);
        
        CohortReferenceType crt = new CohortReferenceType();
        crt.setCohortIdentity(ci);
        staffCohortAssoc.setCohortReference(crt);

        //set begin and end dates
        staffCohortAssoc.setBeginDate(beginDate);
        staffCohortAssoc.setEndDate(endDate);
        
        staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
        
        return staffCohortAssoc;
    }
	*/
    /**
     * Generates a StaffCohortAssociation between a cohort and a list of staffs 
     * with a school as a reference.
     *
     * @param cohortId
     * @param staffIds
     * @param schoolIds
     * 
     * @return <code>List<StaffCohortAssociation></code>
     */
    /*
    public static List<StaffCohortAssociation> generateLowFi(String cohortId, Collection<String> staffIds, String schoolId) {

        List<StaffCohortAssociation> staffCohortAssociations = new ArrayList<StaffCohortAssociation>();
        
        // construct and add the staff references
        for (String staffId : staffIds) {
            StaffIdentityType sit = new StaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            StaffReferenceType srt = new StaffReferenceType();
            srt.setStaffIdentity(sit);
            
            StaffCohortAssociation staffCohortAssoc = new StaffCohortAssociation();
            staffCohortAssoc.setStaffReference(srt);
            
            // construct and add the Cohort Reference
            CohortIdentityType ci = new CohortIdentityType();
            ci.setCohortIdentifier(cohortId);
            
            EducationalOrgIdentityType edOrgType = new EducationalOrgIdentityType();
            edOrgType.setStateOrganizationId(schoolId);
            EducationalOrgReferenceType edOrgRef = new EducationalOrgReferenceType();
            edOrgRef.setEducationalOrgIdentity(edOrgType);           
            ci.setEducationalOrgReference(edOrgRef);
            
            CohortReferenceType crt = new CohortReferenceType();
            crt.setCohortIdentity(ci);
            staffCohortAssoc.setCohortReference(crt);
            
            //set begin and end dates
            staffCohortAssoc.setBeginDate(beginDate);
            staffCohortAssoc.setEndDate(endDate);
            
            staffCohortAssoc.setStudentRecordAccess(Boolean.TRUE);
            
            staffCohortAssociations.add(staffCohortAssoc);
        }
        
        return staffCohortAssociations;
    }
*/

}

