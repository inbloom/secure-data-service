/**
 *
 */
package org.slc.sli.test.generators;

import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Cohort;
import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.Student;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;

/**
 * @author lchen
 *
 */
public class StudentCohortAssociationGenerator {
	private static final Logger log = Logger.getLogger(StudentCohortAssociation.class);
	private boolean includeOptionalData = true;
	private boolean includeAllData = true;
	private String beginDate = "2011-03-04";
	private String endDate = "2012-03-04";
	Random generator = new Random();

	public StudentCohortAssociation getStudentCohortAssociation( CohortReferenceType CohortReference, StudentReferenceType StudentReference) throws Exception {
		StudentCohortAssociation sca = new StudentCohortAssociation();
		if (includeAllData) {
			sca.setStudentReference(StudentReference);
			sca.setCohortReference(CohortReference);
			sca.setBeginDate(beginDate);

		}
		else {
			log.info("Required Data is invalid !");
		}

		if (includeOptionalData)
			sca.setEndDate(endDate);
		else log.info("Optional Data is invalid!");

			/*
		if (includeAllData) {

			   	StudentIdentityType sit = new StudentIdentityType();
		        sit.setStudentUniqueStateId(studentUniqueStateId);
		        StudentReferenceType srt = new StudentReferenceType();
		        srt.setStudentIdentity(sit);
		        sca.setStudentReference(srt);

		        CohortIdentityType cit =  new CohortIdentityType();
		        cit.setCohortIdentifier(cohortIdentifier);

		        CohortReferenceType r = CohortGenerator.getCohortReferenceType(cit);
		        sca.setCohortReference(r);
		} else {
			log.info("Required Data is invalid !");
		}

		if (includeOptionalData)
			sca.setEndDate(endDate);
		else log.info("Optional Data is invalid!");
		*/
		return sca;
	}

}
