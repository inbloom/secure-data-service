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


/**
 *
 */
package org.slc.sli.test.generators;

import java.util.Set;

import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentCohort;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;
import org.slc.sli.test.utils.InterchangeWriter;

/**
 * Generates StudentCohortAssociation data
 * 
 * @author slee
 *
 */
public class StudentCohortAssociationGenerator {

	private static String beginDate = "2011-03-04";
	private static String endDate = "2012-03-04";

    /**
     * Generates a list of StudentCohortAssociation from a CohortMeta.
     *
     * @param cohortMeta
     * 
     * @return <code>List<StudentCohortAssociation></code>
     */
    public static int generateLowFi(InterchangeWriter<InterchangeStudentCohort> iWriter, CohortMeta cohortMeta) {
    	int count=0;
        String cohortId = cohortMeta.id;
        String schoolId = cohortMeta.programMeta==null ? cohortMeta.schoolMeta.id : cohortMeta.programMeta.orgId;
        Set<String> studentIds = cohortMeta.studentIds;
        
        
        for (String studentId : studentIds) {
        	StudentCohortAssociation retVal = generateLowFi(cohortId, studentId, schoolId);

            
            iWriter.marshal(retVal);
            count++;
        }

        return count;
    }

    /**
     * Generates a StudentCohortAssociation between a cohort and a student 
     * with a school as a reference.
     *
     * @param cohortId
     * @param studentId
     * @param schoolId
     * 
     * @return <code>StudentCohortAssociation</code>
     */
    public static StudentCohortAssociation generateLowFi(String cohortId, String studentId, String schoolId) {

        StudentCohortAssociation studentCohortAssoc = new StudentCohortAssociation();
        
        // construct and add the student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        studentCohortAssoc.setStudentReference(srt);

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
        studentCohortAssoc.setCohortReference(crt);

        //set begin and end dates
        studentCohortAssoc.setBeginDate(beginDate);
        studentCohortAssoc.setEndDate(endDate);
        
        return studentCohortAssoc;
    }

}
