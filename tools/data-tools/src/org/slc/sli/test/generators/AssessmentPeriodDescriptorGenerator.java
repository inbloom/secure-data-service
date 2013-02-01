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

import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptor;
import org.slc.sli.test.edfi.entities.AssessmentPeriodDescriptorType;
import org.slc.sli.test.edfi.entities.ObjectFactory;
import org.slc.sli.test.edfi.entities.meta.AssessmentPeriodDescriptorMeta;

public class AssessmentPeriodDescriptorGenerator {

    // TODO: maybe make public and move to one common generator class for use by all
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static AssessmentPeriodDescriptor generateLowFi(AssessmentPeriodDescriptorMeta assessPeriodDescMeta) {
        AssessmentPeriodDescriptor assessmentPeriodDescriptor = new AssessmentPeriodDescriptor();
        assessmentPeriodDescriptor.setCodeValue(assessPeriodDescMeta.id);
        assessmentPeriodDescriptor.setDescription("Description");
        assessmentPeriodDescriptor.setShortDescription("ShortDescription");
        assessmentPeriodDescriptor.setBeginDate("2012-01-12");
        assessmentPeriodDescriptor.setEndDate("2012-11-12");
        return assessmentPeriodDescriptor;
    }

    public static AssessmentPeriodDescriptorType getAssessmentPeriodDescriptorType(String assessPeriodDescIdString) {
        AssessmentPeriodDescriptorType assessPeriodDescType = new AssessmentPeriodDescriptorType();
        assessPeriodDescType.setCodeValue(assessPeriodDescIdString);
//        assessPeriodDescType.getCodeValueOrShortDescriptionOrDescription().add(
//                OBJECT_FACTORY.createAssessmentPeriodDescriptorTypeCodeValue(assessPeriodDescIdString));

        return assessPeriodDescType;
    }
}
