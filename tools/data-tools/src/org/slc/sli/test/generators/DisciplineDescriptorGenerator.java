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

import org.slc.sli.test.edfi.entities.*;

public class DisciplineDescriptorGenerator {
    public DisciplineDescriptor generate(int iBehavior, String schoolId) {
        DisciplineDescriptor disciplineDescriptor = new DisciplineDescriptor();

        try {
            EducationalOrgReferenceType eor = SchoolGenerator.getNonSLCEducationalOrgReferenceType(schoolId);
            switch (iBehavior) {
            case 0:
                disciplineDescriptor.setCodeValue("DET");
                disciplineDescriptor.setShortDescription("Detention");
                disciplineDescriptor.getEducationOrganizationReference().add(eor);
                break;
            case 1:
                disciplineDescriptor.setCodeValue("SUS");
                disciplineDescriptor.setShortDescription("Suspension");
                disciplineDescriptor.getEducationOrganizationReference().add(eor);
                break;
            case 2:
                disciplineDescriptor.setCodeValue("EXP");
                disciplineDescriptor.setShortDescription("Expulsion");
                disciplineDescriptor.getEducationOrganizationReference().add(eor);
                break;
            default:
                disciplineDescriptor.setCodeValue("ISS");
                disciplineDescriptor.setShortDescription("In-School Suspension");
                disciplineDescriptor.getEducationOrganizationReference().add(eor);
                break;
            }
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return disciplineDescriptor;
    }


}
