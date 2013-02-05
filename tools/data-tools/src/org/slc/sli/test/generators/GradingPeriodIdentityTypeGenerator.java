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

import java.util.List;
import java.util.Random;

import org.slc.sli.test.edfi.entities.EducationalOrgIdentityType;
import org.slc.sli.test.edfi.entities.EducationalOrgReferenceType;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;

public class GradingPeriodIdentityTypeGenerator {
    Random generator = new Random(31);

    public GradingPeriodIdentityType getGradingPeriodIdentityType(List<String> StateOrgnizationId,
            String EducationOrgIdentificationCode) {
        GradingPeriodIdentityType gpit = new GradingPeriodIdentityType();
        gpit.setGradingPeriod(getGradingPeriodType());
        gpit.setSchoolYear("2011-2012");

        EducationalOrgIdentityType eoit = new EducationalOrgIdentityType();

        for (String stateOrgId : StateOrgnizationId) {
            // eoit.getStateOrganizationIdOrEducationOrgIdentificationCode().add(StateOrgnizationId);
            eoit.setStateOrganizationId(stateOrgId);
        }

        // GradingPeriodIdentityType
        // EducationalOrgIdentityType
        EducationalOrgReferenceType eor = new EducationalOrgReferenceType();
        eor.setEducationalOrgIdentity(eoit);

        return gpit;
    }

    public GradingPeriodType getGradingPeriodType() {
        return GradingPeriodType.FIRST_NINE_WEEKS;
    }

}
