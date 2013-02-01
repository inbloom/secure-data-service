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

import java.util.Set;

import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.InterchangeStudentProgram;
import org.slc.sli.test.edfi.entities.SLCProgramIdentityType;
import org.slc.sli.test.edfi.entities.SLCProgramReferenceType;
import org.slc.sli.test.edfi.entities.ServiceDescriptorType;
import org.slc.sli.test.edfi.entities.SLCStudentIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentProgramAssociation;
import org.slc.sli.test.edfi.entities.SLCStudentReferenceType;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;
import org.slc.sli.test.edfi.entities.meta.SchoolMeta;
import org.slc.sli.test.utils.InterchangeWriter;

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
    public static int generateLowFi(InterchangeWriter<InterchangeStudentProgram> iWriter, ProgramMeta programMeta) {
    	int count =0;
        Set<String> studentIds = programMeta.studentIds;
        String programId = programMeta.id;
        String schoolId = programMeta.orgId;

        for (String studentId : studentIds) {
            SLCStudentProgramAssociation retVal = generateLowFi(studentId, programId, schoolId);
            
            iWriter.marshal(retVal);
            count++;
        }
        return count;
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
    public static SLCStudentProgramAssociation generateLowFi(String studentId, String programId, String schoolId) {

        SLCStudentProgramAssociation studentProgram = new SLCStudentProgramAssociation();

        // construct and add the student reference
        SLCStudentIdentityType sit = new SLCStudentIdentityType();
        sit.setStudentUniqueStateId(studentId);
        SLCStudentReferenceType srt = new SLCStudentReferenceType();
        srt.setStudentIdentity(sit);
        studentProgram.setStudentReference(srt);

        // construct and add the school references
        SLCEducationalOrgIdentityType edOrgIdentity = new SLCEducationalOrgIdentityType();
        // TODO: Remove this workaround when SLI data model supports inheritance:
        // StudentProgramAssociation should be associatable with either schools or ed orgs,
        // but since SLI has no inheritance in the data model, it is forced to be associated
        // with ed org only.
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(schoolId);
        // edOrgIdentity.getStateOrganizationIdOrEducationOrgIdentificationCode().add(((SchoolMeta)MetaRelations.SCHOOL_MAP.get(schoolId)).leaId);
        edOrgIdentity.setStateOrganizationId(((SchoolMeta) MetaRelations.SCHOOL_MAP.get(schoolId)).leaId);
        SLCEducationalOrgReferenceType schoolRef = new SLCEducationalOrgReferenceType();
        schoolRef.setEducationalOrgIdentity(edOrgIdentity);
        studentProgram.setEducationOrganizationReference(schoolRef);

        // construct and add the program reference
        SLCProgramIdentityType pi = new SLCProgramIdentityType();
        pi.setProgramId(programId);
        SLCProgramReferenceType prt = new SLCProgramReferenceType();
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
