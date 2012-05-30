package org.slc.sli.test.generators;

import java.util.Random;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.GraduationPlanType;
import org.slc.sli.test.edfi.entities.Parent;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.StateAbbreviationType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.StudentSchoolAssociation;

public class StudentSchoolAssociationGenerator {
	
	static StudentSchoolAssociation ssa; 
	static StudentIdentityType sit;
	static StudentReferenceType srt;
	static EducationalOrgIdentityType eoit;
	static EducationalOrgReferenceType eor;
	static int c = 0;
	 
	 
	  static {
		  try {
			  ssa = new StudentSchoolAssociation();
			  sit = new StudentIdentityType();
			  srt = new StudentReferenceType();
			  eoit = new EducationalOrgIdentityType();
			  eor = new EducationalOrgReferenceType();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	  }
     

    public static StudentSchoolAssociation generateLowFi(String studentId, String schoolId) {
    	
    	//StudentSchoolAssociation ssa = new StudentSchoolAssociation();

       // StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
       // StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        ssa.setStudentReference(srt);

       // EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
        eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
       // EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);
        ssa.setSchoolReference(eor);

        ssa.setEntryGradeLevel(GradeLevelType.FIFTH_GRADE);
        
        return ssa;
    }
}
