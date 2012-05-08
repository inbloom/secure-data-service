package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
<<<<<<< HEAD
import org.slc.sli.test.edfi.entities.Cohort;
=======
import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Cohort;
import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.CohortScopeType;
import org.slc.sli.test.edfi.entities.CohortType;
>>>>>>> master
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ProgramIdentityType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
<<<<<<< HEAD

/**
 * Generates Cohort from CohortMeta
 * or
 * String studentId, String programId, String schoolId
 *
 * @author slee
 *
 */
public class CohortGenerator {
	private static final Logger log = Logger.getLogger(CohortGenerator.class);
	
    /**
     * Generates a StudentProgramAssociation from 
     * cohortId, programId and schoolId.
     *
     * @param cohortId
     * @param programId
     * @param schoolId
     * 
     * @return <code>StudentProgramAssociation</code>
     */
    public static Cohort generateLowFi(String cohortId, String programId, String schoolId) {
        Cohort cohort = new Cohort ();
        
        cohort.setCohortIdentifier(cohortId);
        cohort.setCohortDescription("The description of the cohort and its purpose");
        cohort.setCohortType(GeneratorUtils.generateCohortType());
        cohort.setCohortScope(GeneratorUtils.generateCohortScopeType());
        cohort.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        
        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);
        
        // construct and add the program reference       
        ProgramIdentityType pi = new ProgramIdentityType();
        pi.setProgramType(GeneratorUtils.generateProgramType());
        pi.setProgramId(programId);
        pi.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        ProgramReferenceType prt = new ProgramReferenceType();
        prt.setProgramIdentity(pi);
        
        cohort.getProgramReference().add(prt);
        
        return cohort;
    }

    /**
     * Generates a StudentProgramAssociation from 
     * cohortId and schoolId.
     *
     * @param cohortId
     * @param schoolId
     * 
     * @return <code>StudentProgramAssociation</code>
     */
    public static Cohort generateLowFi(String cohortId, String schoolId) {
        Cohort cohort = new Cohort ();
        
        cohort.setCohortIdentifier(cohortId);
        cohort.setCohortDescription("The description of the cohort and its purpose");
        cohort.setCohortType(GeneratorUtils.generateCohortType());
        cohort.setCohortScope(GeneratorUtils.generateCohortScopeType());
        cohort.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        
        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);
        
        return cohort;
    }
    
    /**
     * Generates a StudentProgramAssociation from 
     * cohortId, programId and a list of schoolIds.
     *
     * @param cohortId
     * @param programId
     * @param schoolIds
     * 
     * @return <code>StudentProgramAssociation</code>
     */
    public static Cohort generateLowFi(String cohortId, String programId, List<String> schoolIds) {
        Cohort cohort = new Cohort ();
        
        cohort.setCohortIdentifier(cohortId);
        cohort.setCohortDescription("The description of the cohort and its purpose");
        cohort.setCohortType(GeneratorUtils.generateCohortType());
        cohort.setCohortScope(GeneratorUtils.generateCohortScopeType());
        cohort.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        
        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(schoolIds);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);
        
        // construct and add the program reference       
        ProgramIdentityType pi = new ProgramIdentityType();
        pi.setProgramType(GeneratorUtils.generateProgramType());
        pi.setProgramId(programId);
        pi.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(schoolIds);
        ProgramReferenceType prt = new ProgramReferenceType();
        prt.setProgramIdentity(pi);
        
        cohort.getProgramReference().add(prt);
        
        return cohort;
    }

    public static void main (String args[]) throws Exception {
        Random r = new Random ();
        List<String> StateOrganizationIds = new ArrayList();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++){
                String StateOrganizationId = Integer.toString(Math.abs(r.nextInt()));
                StateOrganizationIds.add(StateOrganizationId);
            }

            String cohortIdentifier = Integer.toString(r.nextInt());
            String programId = Integer.toString(Math.abs(r.nextInt()));
            Cohort c = generateLowFi(cohortIdentifier,programId,StateOrganizationIds);
            
            int lsize =  c.getProgramReference().size();
            for (int k = 0; k < 5; k++) {
                log.info("Counter ====================== " + j  +
                        " ============= stateOrgId = " + c.getEducationOrgReference()
                        .getEducationalOrgIdentity()
                        .getStateOrganizationIdOrEducationOrgIdentificationCode().get(k));
            }

            log.info("List Program Reference = " +
            c.getProgramReference().size() + ",\n" +
                    "ProgramId = " + c.getProgramReference().get(0).getProgramIdentity().getProgramId()+ ",\n" +
                    "ProgramType = " + c.getProgramReference().get(0).getProgramIdentity().getProgramType() );
            for (int n = 0; n < 5; n ++) {
                log.info("IdentificationCode = " + c.getProgramReference().get(0)
                        .getProgramIdentity().getStateOrganizationIdOrEducationOrgIdentificationCode()
                        .get(n));
            }

            log.info(
                    "stateOrgId = " + c.getEducationOrgReference().getEducationalOrgIdentity()
                                    .getStateOrganizationIdOrEducationOrgIdentificationCode().size() + ",\n" +
                     "cohortIdentifier = " + cohortIdentifier + ",\n" +
                     "cohortType = " + c.getCohortType() + ",\n" +
                     "CohortScopeType = " + c.getCohortScope() + ",\n" +
                     "AcademicSubjectType = " + c.getAcademicSubject() + ",\n" +
                     "CohortDescription = " + c.getCohortDescription() + ",\n\n"

                    );

                StateOrganizationIds.clear();
        }   // end of for (j = 0)

    }
=======
import org.slc.sli.test.edfi.entities.ProgramType;

public class CohortGenerator {
	private static final Logger log = Logger.getLogger(CohortGenerator.class);
	private boolean  includeOptionalData = true;
	private boolean  includeAllData = true;
	static Random generator = new Random();

	public Cohort getCohort(String cohortIdentifier,List<String> StateOrganizationIds, String ProgramId)
			throws Exception {
		Cohort c = new Cohort ();
		Random r = new Random ();
		ProgramIdentityType pi = new ProgramIdentityType();
		ProgramReferenceType prt = new ProgramReferenceType ();
		if (includeAllData){
			c.setCohortType(getCohortType());
			c.setCohortIdentifier(cohortIdentifier);
			EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();
			for(String StateOrganizationId : StateOrganizationIds)
				eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(StateOrganizationId);
			EducationalOrgReferenceType eort = new EducationalOrgReferenceType ();
			eort.setEducationalOrgIdentity(eoit);
			c.setEducationOrgReference(eort);

		}
		else{
			log.info("includeAllData is invalid !");
		}

		if (includeOptionalData) {
			c.setCohortDescription("The description of he cohort and its purpose");
			c.setCohortScope(getCohortScopeType());
			c.setAcademicSubject(getAcademicSubjectType());
				pi.setProgramId(ProgramId);
				pi.setProgramType(getProgramType());
				for(String StateOrganizationId : StateOrganizationIds) {
					pi.getStateOrganizationIdOrEducationOrgIdentificationCode().add(StateOrganizationId);
					}
				prt.setProgramIdentity(pi);
				c.getProgramReference().add(prt);

		}
		else {
			log.info("includeOptionalData is invalid !");
		}
		return c;

	}
/*
	public static CohortReferenceType getCohortReferenceType(Cohort cohort) {
		CohortIdentityType cit = new CohortIdentityType ();

		cit.setCohortIdentifier(cohort.getCohortIdentifier());
		cit.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(
				cohort.getEducationOrgReference().getEducationalOrgIdentity().getStateOrganizationIdOrEducationOrgIdentificationCode());
//		cit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(cohort.getEducationOrgReference().getEducationalOrgIdentity()
//				.getStateOrganizationIdOrEducationOrgIdentificationCode().get(0));
		CohortReferenceType cft = new CohortReferenceType ();
		cft.setCohortIdentity(cit);
		System.out.println(cft.getCohortIdentity().getCohortIdentifier());
		return cft;
	}
*/
	public static CohortReferenceType getCohortReferenceType(String stateOrganizationId, String CohortIdentifier) {
		CohortIdentityType cit = new CohortIdentityType ();
		cit.setCohortIdentifier(CohortIdentifier);
		cit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(stateOrganizationId);
		CohortReferenceType cft = new CohortReferenceType ();
		cft.setCohortIdentity(cit);
		return cft;
	}

	public ProgramType getProgramType () {
		int roll = generator.nextInt(38);
		switch (roll) {

			case 0: return ProgramType.ADULT_CONTINUING_EDUCATION;
			case 1: return ProgramType.ALTERNATIVE_EDUCATION;
			case 2: return ProgramType.ATHLETICS;
			case 3: return ProgramType.BILINGUAL;
			case 4: return ProgramType.BILINGUAL_SUMMER;
			case 5: return ProgramType.CAREER_AND_TECHNICAL_EDUCATION;
			case 6: return ProgramType.COCURRICULAR_PROGRAMS;
			case 7: return ProgramType.COLLEGE_PREPARATORY;
			case 8: return ProgramType.COMMUNITY_JUNIOR_COLLEGE_EDUCATION_PROGRAM;
			case 9: return ProgramType.COMMUNITY_SERVICE_PROGRAM;
			case 10: return ProgramType.COMPENSATORY_SERVICES_FOR_DISADVANTAGED_STUDENTS;
			case 11: return ProgramType.COUNSELING_SERVICES;
			case 12: return ProgramType.ENGLISH_AS_A_SECOND_LANGUAGE_ESL;
			case 13: return ProgramType.EVEN_START;
			case 14: return ProgramType.EXTENDED_DAY_CHILD_CARE_SERVICES;
			case 15: return ProgramType.GIFTED_AND_TALENTED;
			case 16: return ProgramType.HEAD_START;
			case 17: return ProgramType.HEALTH_SERVICES_PROGRAM;
			case 18: return ProgramType.HIGH_SCHOOL_EQUIVALENCY_PROGRAM_HSEP;
			case 19: return ProgramType.IDEA;
			case 20: return ProgramType.IMMIGRANT_EDUCATION;
			case 21: return ProgramType.INDIAN_EDUCATION;
			case 22: return ProgramType.INTERNATIONAL_BACCALAUREATE;
			case 23: return ProgramType.LIBRARY_MEDIA_SERVICES_PROGRAM;
			case 24: return ProgramType.MAGNET_SPECIAL_PROGRAM_EMPHASIS;
			case 25: return ProgramType.MIGRANT_EDUCATION;
			case 26: return ProgramType.NEGLECTED_AND_DELINQUENT_PROGRAM;
			case 27: return ProgramType.OPTIONAL_FLEXIBLE_SCHOOL_DAY_PROGRAM_OFSDP;
			case 28: return ProgramType.OTHER;
			case 29: return ProgramType.REGULAR_EDUCATION;
			case 30: return ProgramType.REMEDIAL_EDUCATION;
			case 31: return ProgramType.SECTION_504_PLACEMENT;
			case 32: return ProgramType.SERVICE_LEARNING;
			case 33: return ProgramType.SPECIAL_EDUCATION;
			case 34: return ProgramType.STUDENT_RETENTION_DROPOUT_PREVENTION;
			case 35: return ProgramType.SUBSTANCE_ABUSE_EDUCATION_PREVENTION;
			case 36: return ProgramType.TEACHER_PROFESSIONAL_DEVELOPMENT_MENTORING;
			case 37: return ProgramType.TECHNICAL_PREPARATORY;
			case 38: return ProgramType.TITLE_I_PART_A;
			default: return ProgramType.VOCATIONAL_EDUCATION;


		}
	}

	public CohortType getCohortType () {
		int roll = generator.nextInt(10);
		switch (roll) {
			case 0: return CohortType.ACADEMIC_INTERVENTION;
			case 1: return CohortType.ATTENDANCE_INTERVENTION;
			case 2: return CohortType.CLASSROOM_PULLOUT;
			case 3: return CohortType.COUNSELOR_LIST;
			case 4: return CohortType.DISCIPLINE_INTERVENTION;
			case 5: return CohortType.EXTRACURRICULAR_ACTIVITY;
			case 6: return CohortType.FIELD_TRIP;
			case 7: return CohortType.IN_SCHOOL_SUSPENSION;
			case 8: return CohortType.OTHER;
			case 9: return CohortType.PRINCIPAL_WATCH_LIST;
			default: return CohortType.STUDY_HALL;
		}
	}

	public CohortScopeType getCohortScopeType () {
		int roll = generator.nextInt(6);
		switch (roll) {
			case 0: return CohortScopeType.CLASSROOM;
			case 1: return CohortScopeType.COUNSELOR;
			case 2: return CohortScopeType.DISTRICT;
			case 3: return CohortScopeType.PRINCIPAL;
			case 4: return CohortScopeType.SCHOOL;
			case 5: return CohortScopeType.STATEWIDE;
			default: return CohortScopeType.TEACHER;
		}
	}

	public AcademicSubjectType getAcademicSubjectType () {
		int roll = generator.nextInt(31);
		switch (roll) {
			case 0:  return AcademicSubjectType.AGRICULTURE_FOOD_AND_NATURAL_RESOURCES;
			case 1:  return AcademicSubjectType.ARCHITECTURE_AND_CONSTRUCTION;
			case 2:  return AcademicSubjectType.BUSINESS_AND_MARKETING;
			case 3:  return AcademicSubjectType.COMMUNICATION_AND_AUDIO_VISUAL_TECHNOLOGY;
			case 4:  return AcademicSubjectType.COMPOSITE;
			case 5:  return AcademicSubjectType.COMPUTER_AND_INFORMATION_SCIENCES;
			case 6:  return AcademicSubjectType.CRITICAL_READING;
			case 7:  return AcademicSubjectType.ELA;
			case 8:  return AcademicSubjectType.ENGINEERING_AND_TECHNOLOGY;
			case 9 : return AcademicSubjectType.MISCELLANEOUS;
			case 10: return AcademicSubjectType.ENGLISH;
			case 11: return AcademicSubjectType.ENGLISH_LANGUAGE_AND_LITERATURE;
			case 12: return AcademicSubjectType.FINE_AND_PERFORMING_ARTS;
			case 13: return AcademicSubjectType.FOREIGN_LANGUAGE_AND_LITERATURE;
			case 14: return AcademicSubjectType.HEALTH_CARE_SCIENCES;
			case 15: return AcademicSubjectType.HOSPITALITY_AND_TOURISM;
			case 16: return AcademicSubjectType.HUMAN_SERVICES;
			case 17: return AcademicSubjectType.LIFE_AND_PHYSICAL_SCIENCES;
			case 18: return AcademicSubjectType.MANUFACTURING;
			case 19: return AcademicSubjectType.MATHEMATICS;
			case 20: return AcademicSubjectType.MILITARY_SCIENCE;
			case 21: return AcademicSubjectType.MISCELLANEOUS;
			case 22: return AcademicSubjectType.OTHER;
			case 23: return AcademicSubjectType.PHYSICAL_HEALTH_AND_SAFETY_EDUCATION;
			case 24: return AcademicSubjectType.PUBLIC_PROTECTIVE_AND_GOVERNMENT_SERVICE;
			case 25: return AcademicSubjectType.READING;
			case 26: return AcademicSubjectType.RELIGIOUS_EDUCATION_AND_THEOLOGY;
			case 27: return AcademicSubjectType.SCIENCE;
			case 28: return AcademicSubjectType.SOCIAL_SCIENCES_AND_HISTORY;
			case 29: return AcademicSubjectType.SOCIAL_STUDIES;
			case 30: return AcademicSubjectType.TRANSPORTATION_DISTRIBUTION_AND_LOGISTICS;
			default: return AcademicSubjectType.WRITING;
		}
	}

	public static void main (String args[]) throws Exception {
		Random r = new Random ();
		CohortGenerator cg = new CohortGenerator ();
		List<String> StateOrganizationIds = new ArrayList();

		for (int j = 0; j < 5; j++) {
			for (int i = 0; i < 5; i++){
				String StateOrganizationId = Integer.toString(Math.abs(r.nextInt()));
				StateOrganizationIds.add(StateOrganizationId);
			}

			String cohortIdentifier = Integer.toString(r.nextInt());
			String programId = Integer.toString(Math.abs(r.nextInt()));
			Cohort c = cg.getCohort(cohortIdentifier,StateOrganizationIds,programId);
			int lsize =  c.getProgramReference().size();
			for (int k = 0; k < 5; k++) {
				log.info("Counter ====================== " + j  +
						" ============= stateOrgId = " + c.getEducationOrgReference()
						.getEducationalOrgIdentity()
						.getStateOrganizationIdOrEducationOrgIdentificationCode().get(k));
			}

			log.info("List Program Reference = " +
			c.getProgramReference().size() + ",\n" +
					"ProgramId = " + c.getProgramReference().get(0).getProgramIdentity().getProgramId()+ ",\n" +
					"ProgramType = " + c.getProgramReference().get(0).getProgramIdentity().getProgramType() );
			for (int n = 0; n < 5; n ++) {
				log.info("IdentificationCode = " + c.getProgramReference().get(0)
						.getProgramIdentity().getStateOrganizationIdOrEducationOrgIdentificationCode()
						.get(n));
			}

			log.info(
					"stateOrgId = " + c.getEducationOrgReference().getEducationalOrgIdentity()
									.getStateOrganizationIdOrEducationOrgIdentificationCode().size() + ",\n" +
					 "cohortIdentifier = " + cohortIdentifier + ",\n" +
					 "cohortType = " + c.getCohortType() + ",\n" +
					 "CohortScopeType = " + c.getCohortScope() + ",\n" +
					 "AcademicSubjectType = " + c.getAcademicSubject() + ",\n" +
					 "CohortDescription = " + c.getCohortDescription() + ",\n\n"

					);

				StateOrganizationIds.clear();



			//System.out.println("StateOrgId : " + StateOrganizationIds.size());
		}	// end of for (j = 0)

	}
>>>>>>> master
}
