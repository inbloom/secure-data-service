package org.slc.sli.test.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.ProgramIdentityType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.ServiceDescriptorType;
import org.slc.sli.test.edfi.entities.StudentIdentityType;
import org.slc.sli.test.edfi.entities.StudentProgramAssociation;
import org.slc.sli.test.edfi.entities.StudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;

/**
 * Generates StudentProgramAssociation from ProgramMeta
 * or
 * String studentId, String programId, String schoolId
 *
 * @author slee
 *
 */
public class StudentProgramAssociationGenerator {

    private static String beginDate = "2011-03-04";
    private static String endDate = "2012-03-04";

    /**
     * Generates a list of StudentProgramAssociation.
     *
     * @param programMeta
     *
     * @return <code>List<StudentProgramAssociation></code>
     */
    public static List<StudentProgramAssociation> generateLowFi(ProgramMeta programMeta) {
        Set<String> studentIds = programMeta.studentIds;
        String programId = programMeta.id;
        String schoolId = programMeta.orgId;

        List<StudentProgramAssociation> list = new ArrayList<StudentProgramAssociation>(studentIds.size());

        for (String studentId : studentIds) {
            list.add(generateLowFi(studentId, programId, schoolId));
        }
        return list;
    }

    /**
     * Generates a StudentProgramAssociation.
     *
     * @param studentId
     * @param programId
     * @param schoolId
     *
     * @return <code>StudentProgramAssociation</code>
     */
    public static StudentProgramAssociation generateLowFi(String studentId, String programId, String schoolId) {

        StudentProgramAssociation studentProgram = new StudentProgramAssociation();

        // construct and add the student reference
        StudentIdentityType sit = new StudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        StudentReferenceType srt = new StudentReferenceType();
        srt.setStudentIdentity(sit);
        studentProgram.setStudentReference(srt);

        // construct and add the school references
        EducationalOrgIdentityType edOrgIdentity = new EducationalOrgIdentityType();
        // TODO: Remove this workaround when SLI data model supports inheritance:
        // StudentProgramAssociation should be associatable with either schools or ed orgs,
        // but since SLI has no inheritance in the data model, it is forced to be associated
        // with ed org only.
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(((SchoolMeta)MetaRelations.SCHOOL_MAP.get(schoolId)).leaId);
        edOrgIdentity.setStateOrganizationId(((SchoolMeta) MetaRelations.SCHOOL_MAP.get(schoolId)).leaId);
        EducationalOrgReferenceType schoolRef = new EducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        studentProgram.setEducationOrganizationReference(schoolRef);

        // construct and add the program reference
        ProgramIdentityType pi = new ProgramIdentityType();
        pi.setProgramId(programId);
        // pi.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        pi.setStateOrganizationId(schoolId);
        ProgramReferenceType prt = new ProgramReferenceType();
        prt.setProgramIdentity(pi);
        studentProgram.setProgramReference(prt);

        // set begin and end dates
        studentProgram.setBeginDate(beginDate);
        studentProgram.setEndDate(endDate);

        // set reasonExited property
        studentProgram.setReasonExited(GeneratorUtils.generateReasonExitedType());

        // construct and add the ServiceDescriptorType references
        // Do we need that
        // how to set ServiceDescriptorType properties? Are they related to ServiceDescriptor
        // more work
        ServiceDescriptorType sdt = new ServiceDescriptorType();
        studentProgram.getServices().add(sdt);

        return studentProgram;
    }
}
