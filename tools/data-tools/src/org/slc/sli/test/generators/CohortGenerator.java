package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.AcademicSubjectType;
import org.slc.sli.test.edfi.entities.Cohort;
import org.slc.sli.test.edfi.entities.CohortIdentityType;
import org.slc.sli.test.edfi.entities.CohortReferenceType;
import org.slc.sli.test.edfi.entities.CohortScopeType;
import org.slc.sli.test.edfi.entities.CohortType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ProgramIdentityType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.ProgramType;

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

}
