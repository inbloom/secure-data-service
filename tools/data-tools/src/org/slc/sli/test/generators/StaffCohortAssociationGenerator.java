package org.slc.sli.test.generators;

import java.util.List;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.StaffCohortAssociation;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.StudentCohortAssociation;

public class StaffCohortAssociationGenerator {
	private static final Logger log = Logger.getLogger(StudentCohortAssociation.class);
	private boolean includeOptionalData = true;
	private boolean includeAllData = true;
	private String beginDate = "2011-03-04";
	private String endDate = "2012-03-04";

	public StaffCohortAssociation getStaffCohortAssociation (List<CohortReferenceType> CohortReference, List<StaffReferenceType> StaffReference) throws Exception {
		StaffCohortAssociation sca = new StaffCohortAssociation ();
		if (includeAllData) {
			sca.getCohortReference().addAll(CohortReference);
			sca.getStaffReference().addAll(StaffReference);
			sca.setBeginDate(beginDate);
		}
		else {
			log.info("Required Data is invalid !");
		}

		if (includeOptionalData) {
			sca.setEndDate(endDate);
			sca.setStudentRecordAccess(true);
		}
		else {
			log.info("Optional Data is invalid!");
		}
		return sca;


	}

}

