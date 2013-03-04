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

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradeLevelType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.SLCEducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.SLCStudentCompetencyObjectiveReferenceType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjective;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveIdentityType;
import org.slc.sli.test.edfi.entities.StudentCompetencyObjectiveReferenceType;

public class StudentCompetancyObjectiveGenerator {

    private static int scId = 0;

    public static SLCStudentCompetencyObjective getStudentCompetencyObjective(String scoId, SLCEducationalOrgReferenceType edOrgRef)
    {
        scId++;
        SLCStudentCompetencyObjective sco = new SLCStudentCompetencyObjective();
        String id = scoId==null?"SCO Id" + scId:scoId;
        sco.setId(id);
        sco.setStudentCompetencyObjectiveId(id);
        sco.setDescription("Student Competency Description " + scId);
        sco.setObjective("Student Competency Objective " +  scId);
        sco.setObjectiveGradeLevel(GradeLevelType.OTHER);


        if(edOrgRef != null) {

          SLCEducationalOrgIdentityType eoit = new SLCEducationalOrgIdentityType();

          eoit.setStateOrganizationId(edOrgRef.getEducationalOrgIdentity().getStateOrganizationId());
          edOrgRef.setEducationalOrgIdentity(eoit);
          sco.setEducationOrganizationReference(edOrgRef);
        }


        return sco;
    }

    public static SLCStudentCompetencyObjectiveReferenceType getStudentCompetencyObjectiveReferenceType(StudentCompetencyObjective sco)
    {
        SLCStudentCompetencyObjectiveReferenceType ref = new SLCStudentCompetencyObjectiveReferenceType();
        SLCStudentCompetencyObjectiveIdentityType scoIdentity = new SLCStudentCompetencyObjectiveIdentityType();
        scoIdentity.setStudentCompetencyObjectiveId(sco.getStudentCompetencyObjectiveId());
        scoIdentity.setStudentCompetencyObjectiveId(sco.getStudentCompetencyObjectiveId());
        return ref;
    }
}
