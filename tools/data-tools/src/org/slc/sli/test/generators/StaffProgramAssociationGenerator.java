/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.slc.sli.test.edfi.entities.ProgramIdentityType;
import org.slc.sli.test.edfi.entities.ProgramReferenceType;
import org.slc.sli.test.edfi.entities.Ref;
import org.slc.sli.test.edfi.entities.StaffIdentityType;
import org.slc.sli.test.edfi.entities.StaffProgramAssociation;
import org.slc.sli.test.edfi.entities.StaffReferenceType;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.relations.MetaRelations;

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
    public static List<StaffProgramAssociation> generateLowFi(ProgramMeta programMeta) {
        Set<String> staffIds = programMeta.staffIds;
        String programId = programMeta.id;
        String schoolId = programMeta.orgId;
        
        List<StaffProgramAssociation> staffProgramAssociations = new ArrayList<StaffProgramAssociation>();
        
        for (String staffId : staffIds) {
            StaffProgramAssociation staffProgram = new StaffProgramAssociation();
            
            // construct and add the staff references
            StaffReferenceType srt = new StaffReferenceType();
            if (MetaRelations.StaffProgramAssociation_Ref) {
                srt.setRef(new Ref(staffIds.iterator().next()));
            } else {
                StaffIdentityType sit = new StaffIdentityType();
                sit.setStaffUniqueStateId(staffId);
                srt.setStaffIdentity(sit);
            }
            
            staffProgram.setStaffReference(srt);
            
            // construct and add the program reference
            ProgramIdentityType pi = new ProgramIdentityType();
            pi.setProgramId(programId);
            ProgramReferenceType prt = new ProgramReferenceType();
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
