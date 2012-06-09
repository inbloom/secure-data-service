package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.Cohort;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationCode;
import org.slc.sli.test.edfi.entities.EducationOrgIdentificationSystemType;
import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ProgramIdentityType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.meta.CohortMeta;

/**
 * Generates Cohort data
 *
 * @author slee
 *
 */
public class CohortGenerator {
	private static final Logger log = Logger.getLogger(CohortGenerator.class);

    /**
     * Generates a Cohort from a CohortMeta.
     *
     * @param cohortMeta
     *
     * @return <code>Cohort</code>
     */
    public static Cohort generateLowFi(CohortMeta cohortMeta) {
        String cohortId = cohortMeta.id;
        String programId = cohortMeta.programMeta==null ? null : cohortMeta.programMeta.id;
        String schoolId = cohortMeta.programMeta==null ? cohortMeta.schoolMeta.id : cohortMeta.programMeta.orgId;

        return programId==null ? generateLowFi(cohortId, schoolId) : generateLowFi(cohortId, programId, schoolId);
    }

    /**
     * Generates a Cohort for a combination of a school and a program.
     *
     * @param cohortId
     * @param programId
     * @param schoolId
     *
     * @return <code>Cohort</code>
     */
    public static Cohort generateLowFi(String cohortId, String programId, String schoolId) {
        Cohort cohort = basicLowFiFactory(cohortId);

        // construct and add the school references

        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);

        // construct and add the program reference
        ProgramIdentityType pi = new ProgramIdentityType();
//        pi.setProgramType(GeneratorUtils.generateProgramType());
        pi.setProgramId(programId);
        pi.setStateOrganizationId(schoolId);
//        pi.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        ProgramReferenceType prt = new ProgramReferenceType();
        prt.setProgramIdentity(pi);

        cohort.getProgramReference().add(prt);

        return cohort;
    }

    /**
     * Generates a Cohort for a school.
     *
     * @param cohortId
     * @param schoolId
     *
     * @return <code>Cohort</code>
     */
    public static Cohort generateLowFi(String cohortId, String schoolId) {
        Cohort cohort = basicLowFiFactory(cohortId);

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);

        return cohort;
    }

    /**
     * Generates a Cohort for a combination of a list of schools and a program,
     * where the program is also associated to the list of schools.
     *
     * @param cohortId
     * @param programId
     * @param schoolIds
     *
     * @return <code>Cohort</code>
     */
    public static Cohort generateLowFi(String cohortId, String programId, Collection<String> schoolIds) {
        Cohort cohort = basicLowFiFactory(cohortId);

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        for (String schoolId : schoolIds) {
            EducationOrgIdentificationCode educationOrgIdentificationCode = new EducationOrgIdentificationCode();
            educationOrgIdentificationCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
            educationOrgIdentificationCode.setID(schoolId);
            edOrgIdentity.getEducationOrgIdentificationCode().add(educationOrgIdentificationCode);
        }

        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);

        // construct and add the program reference
        ProgramIdentityType pi = new ProgramIdentityType();
//        pi.setProgramType(GeneratorUtils.generateProgramType());
        pi.setProgramId(programId);
//        pi.getStateOrganizationIdOrEducationOrgIdentificationCode().addAll(schoolIds);
        for (String schoolId : schoolIds) {
            EducationOrgIdentificationCode educationOrgIdentificationCode = new EducationOrgIdentificationCode();
            educationOrgIdentificationCode.setIdentificationSystem(EducationOrgIdentificationSystemType.SCHOOL);
            educationOrgIdentificationCode.setID(schoolId);
            pi.getEducationOrgIdentificationCode().add(educationOrgIdentificationCode);
        }
        ProgramReferenceType prt = new ProgramReferenceType();
        prt.setProgramIdentity(pi);

        cohort.getProgramReference().add(prt);

        return cohort;
    }

    /**
     * Factory a basic Cohort.
     *
     * @param cohortId
     *
     * @return <code>Cohort</code>
     */
    private static Cohort basicLowFiFactory(String cohortId) {
        Cohort cohort = new Cohort ();

        cohort.setCohortIdentifier(cohortId);
        cohort.setCohortDescription("The cohort description of cohortId-"+cohortId);
        cohort.setCohortType(GeneratorUtils.generateCohortType());
        cohort.setCohortScope(GeneratorUtils.generateCohortScopeType());
        cohort.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        return cohort;
    }

    public static void main (String args[]) throws Exception {
        Random r = new Random ();
        List<String> StateOrganizationIds = new ArrayList<String>();

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
                        .getEducationOrgIdentificationCode().get(k));
            }

            log.info("List Program Reference = " +
            c.getProgramReference().size() + ",\n" +
                    "ProgramId = " + c.getProgramReference().get(0).getProgramIdentity().getProgramId()+ ",\n" +
                    "ProgramType = " + c.getProgramReference().get(0).getProgramIdentity().getProgramType() );
            for (int n = 0; n < 5; n ++) {
                log.info("IdentificationCode = " + c.getProgramReference().get(0)
                        .getProgramIdentity().getEducationOrgIdentificationCode()
                        .get(n));
            }

            log.info(
                    "stateOrgId = " + c.getEducationOrgReference().getEducationalOrgIdentity()
                                    .getEducationOrgIdentificationCode().size() + ",\n" +
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
