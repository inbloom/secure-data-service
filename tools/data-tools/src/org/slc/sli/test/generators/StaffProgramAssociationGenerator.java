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
import java.util.List;
import java.util.Set;

import org.slc.sli.test.edfi.entities.SLCProgramIdentityType;
import org.slc.sli.test.edfi.entities.SLCProgramReferenceType;
import org.slc.sli.test.edfi.entities.SLCStaffIdentityType;
import org.slc.sli.test.edfi.entities.SLCStaffProgramAssociation;
import org.slc.sli.test.edfi.entities.SLCStaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;

/**
 * Generates StudentProgramAssociation from ProgramMeta
 * or
 * String studentId, String programId, String schoolId
 *
 * @author slee
 *
 */
public class StaffProgramAssociationGenerator {

    private static String beginDate = "2011-03-04";
    private static String endDate = "2012-03-04";
    private static Boolean HAS_STUDENT_RECORD_ACCESS = true;

    /**
     * Generates a StaffProgramAssociation.
     *
     * @param programMeta
     *
     * @return <code>List<StaffProgramAssociation></code>
     */
    public static List<SLCStaffProgramAssociation> generateLowFi(ProgramMeta programMeta) {
        Set<String> staffIds = programMeta.staffIds;
        String programId = programMeta.id;
        String schoolId = programMeta.orgId;

        List<SLCStaffProgramAssociation> staffProgramAssociations = new ArrayList<SLCStaffProgramAssociation>();

        for (String staffId : staffIds) {
            SLCStaffProgramAssociation staffProgram = new SLCStaffProgramAssociation();

            // construct and add the staff references
            SLCStaffReferenceType srt = new SLCStaffReferenceType();

            SLCStaffIdentityType sit = new SLCStaffIdentityType();
            sit.setStaffUniqueStateId(staffId);
            srt.setStaffIdentity(sit);

            staffProgram.setStaffReference(srt);

            // construct and add the program reference
            SLCProgramIdentityType pi = new SLCProgramIdentityType();
            pi.setProgramId(programId);
            SLCProgramReferenceType prt = new SLCProgramReferenceType();
            prt.setProgramIdentity(pi);
            staffProgram.setProgramReference(prt);

            // set begin and end dates
            staffProgram.setBeginDate(beginDate);
            staffProgram.setEndDate(endDate);

            // set has program access
            staffProgram.setStudentRecordAccess(HAS_STUDENT_RECORD_ACCESS);

            staffProgramAssociations.add(staffProgram);
        }

        return staffProgramAssociations;
    }
}
