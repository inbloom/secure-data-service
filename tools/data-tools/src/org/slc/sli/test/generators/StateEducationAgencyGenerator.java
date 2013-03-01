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

import org.slc.sli.test.edfi.entities.EducationOrganizationCategoriesType;
import org.slc.sli.test.edfi.entities.EducationOrganizationCategoryType;
import org.slc.sli.test.edfi.entities.OperationalStatusType;
import org.slc.sli.test.edfi.entities.SLCProgramIdentityType;
import org.slc.sli.test.edfi.entities.SLCProgramReferenceType;
import org.slc.sli.test.edfi.entities.SLCStateEducationAgency;
import org.slc.sli.test.edfi.entities.meta.ProgramMeta;
import org.slc.sli.test.edfi.entities.meta.SeaMeta;

public class StateEducationAgencyGenerator {

    public static SLCStateEducationAgency generateLowFi(String id, SeaMeta seaMeta) {

        SLCStateEducationAgency stateEducationAgency = new SLCStateEducationAgency();
        stateEducationAgency.setId(id);
        stateEducationAgency.setStateOrganizationId(id);

        stateEducationAgency.setNameOfInstitution("Institution name " + id);
        stateEducationAgency.setShortNameOfInstitution("Institution " + id);

        EducationOrganizationCategoriesType category = new EducationOrganizationCategoriesType();
        category.getOrganizationCategory().add(EducationOrganizationCategoryType.STATE_EDUCATION_AGENCY);
        stateEducationAgency.setOrganizationCategories(category);
        stateEducationAgency.setOperationalStatus(OperationalStatusType.ACTIVE);

        stateEducationAgency.getAddress().add(AddressGenerator.generateLowFi());
        for (String pid : seaMeta.programs.keySet()) {
            ProgramMeta pm = seaMeta.programs.get(pid);
            SLCProgramIdentityType pit = new SLCProgramIdentityType();
            pit.setProgramId(pm.id);
            SLCProgramReferenceType prt = new SLCProgramReferenceType();
            prt.setProgramIdentity(pit);
            stateEducationAgency.getProgramReference().add(prt);
        }
    
        return stateEducationAgency;
    }
}
