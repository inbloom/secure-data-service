/**
 *
 */
package org.slc.sli.test.generators;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;

/**
 * Generates StudentCohortAssociation data
 * 
 * @author slee
 *
 */
public class StudentCohortAssociationGenerator {
	private static final Logger log = Logger.getLogger(StudentCohortAssociationGenerator.class);

	private static String beginDate = "2011-03-04";
	private static String endDate = "2012-03-04";

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
        ci.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        CohortReferenceType crt = new CohortReferenceType();
        crt.setCohortIdentity(ci);
        studentCohortAssoc.setCohortReference(crt);

        //set begin and end dates
        studentCohortAssoc.setBeginDate(beginDate);
        studentCohortAssoc.setEndDate(endDate);
        
        return studentCohortAssoc;
    }

}
