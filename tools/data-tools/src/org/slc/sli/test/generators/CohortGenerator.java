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
import org.slc.sli.test.edfi.entities.SLCCohort;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCProgramIdentityType;
import org.slc.sli.test.edfi.entities.SLCProgramReferenceType;
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
    public static SLCCohort generateLowFi(CohortMeta cohortMeta) {
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
    
    public static SLCCohort generateLowFi(String cohortId, String programId, String schoolId) {
        SLCCohort SLCC = basicLowFiFactory(cohortId);

        // construct and add the school references
        
        SLCEducationalOrgReferenceType slceort = new SLCEducationalOrgReferenceType();
        SLCEducationalOrgIdentityType slceoit = new SLCEducationalOrgIdentityType ();
        slceoit.setStateOrganizationId(schoolId);
        slceort.setEducationalOrgIdentity(slceoit);
            
        SLCC.setEducationOrgReference(slceort);

        // construct and add the program reference
        SLCProgramIdentityType pi = new SLCProgramIdentityType();
        pi.setProgramId(programId);
        SLCProgramReferenceType prt = new SLCProgramReferenceType();
        prt.setProgramIdentity(pi);

        SLCC.getProgramReference().add(prt);

        return SLCC;
    }

    public static Cohort generateLowFi_dep(String cohortId, String programId, String schoolId) {
        Cohort cohort = basicLowFiFactory_dep(cohortId);

        // construct and add the school references

        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);

        // construct and add the program reference
        ProgramIdentityType pi = new ProgramIdentityType();
        pi.setProgramId(programId);
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
    public static Cohort generateLowFi_dep(String cohortId, String schoolId) {
        Cohort cohort = basicLowFiFactory_dep(cohortId);

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        edOrgIdentity.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        cohort.setEducationOrgReference(schoolRef);

        return cohort;
    }
    
    
    public static SLCCohort generateLowFi(String cohortId, String schoolId) {
        SLCCohort slcc = basicLowFiFactory(cohortId);

        // construct and add the school references
        SLCEducationalOrgIdentityType slceoit = new SLCEducationalOrgIdentityType ();
        slceoit.setStateOrganizationId(schoolId);
//        edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        SLCEducationalOrgReferenceType slceort = new SLCEducationalOrgReferenceType();
        slceort.setEducationalOrgIdentity(slceoit);
        
        slcc.setEducationOrgReference(slceort);

        return slcc;
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
        Cohort cohort = basicLowFiFactory_dep(cohortId);

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
        pi.setProgramId(programId);
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
    private static Cohort basicLowFiFactory_dep(String cohortId) {
        Cohort cohort = new Cohort ();

        cohort.setCohortIdentifier(cohortId);
        cohort.setCohortDescription("The cohort description of cohortId-"+cohortId);
        cohort.setCohortType(GeneratorUtils.generateCohortType());
        cohort.setCohortScope(GeneratorUtils.generateCohortScopeType());
        cohort.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        return cohort;
    }
    
    private static SLCCohort basicLowFiFactory(String cohortId) {
        SLCCohort SLCC = new SLCCohort ();

        SLCC.setCohortIdentifier(cohortId);
        SLCC.setCohortDescription("The cohort description of cohortId-"+cohortId);
        SLCC.setCohortType(GeneratorUtils.generateCohortType());
        SLCC.setCohortScope(GeneratorUtils.generateCohortScopeType());
        SLCC.setAcademicSubject(GeneratorUtils.generateAcademicSubjectType());
        return SLCC;
    }

    public static void main (String args[]) throws Exception {
        Random r = new Random (31);
        List<String> StateOrganizationIds = new ArrayList<String>();

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++){
                String StateOrganizationId = Integer.toString(Math.abs(r.nextInt()));
                StateOrganizationIds.add(StateOrganizationId);
            }

            String cohortIdentifier = Integer.toString(r.nextInt());
            String programId = Integer.toString(Math.abs(r.nextInt()));
            Cohort c = generateLowFi(cohortIdentifier,programId,StateOrganizationIds);

            for (int k = 0; k < 5; k++) {
                log.info("Counter ====================== " + j  +
                        " ============= stateOrgId = " + c.getEducationOrgReference()
                        .getEducationalOrgIdentity()
                        .getEducationOrgIdentificationCode().get(k));
            }

            log.info("List Program Reference = " +
                    c.getProgramReference().size() + ",\n" +
                    "ProgramId = " + c.getProgramReference().get(0).getProgramIdentity().getProgramId());

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
