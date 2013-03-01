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

public class BehaviorDescriptorGenerator {
    public BehaviorDescriptor generate(int iBehavior, String schoolId) {
        BehaviorDescriptor behaviorDescriptor = new BehaviorDescriptor();

        try {
            EducationalOrgReferenceType eor = SchoolGenerator.getNonSLCEducationalOrgReferenceType(schoolId);
            switch (iBehavior) {
            case 0:
                behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.SCHOOL_CODE_OF_CONDUCT);
                behaviorDescriptor.setCodeValue("PDA");
                behaviorDescriptor.setShortDescription("Public Display of Affection");
                behaviorDescriptor.getEducationOrganizationReference().add(eor);
                break;
            case 1:
                behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.SCHOOL_VIOLATION);
                behaviorDescriptor.setCodeValue("BUL");
                behaviorDescriptor.setShortDescription("Bullying");
                behaviorDescriptor.getEducationOrganizationReference().add(eor);
                break;
            case 2:
                behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.STATE_LAW_CRIME);
                behaviorDescriptor.setCodeValue("PCS");
                behaviorDescriptor.setShortDescription("Possession of Controlled Substance");
                behaviorDescriptor.getEducationOrganizationReference().add(eor);
                break;
            default:
                behaviorDescriptor.setBehaviorCategory(BehaviorCategoryType.STATE_OFFENSE);
                behaviorDescriptor.setCodeValue("PFA");
                behaviorDescriptor.setShortDescription("Possession of Firearm");
                behaviorDescriptor.getEducationOrganizationReference().add(eor);
                break;
            }
              
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return behaviorDescriptor;
    }


}
