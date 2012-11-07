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
        int roll = generator.nextInt(20) + 1;
        switch (roll) {
        case 1:
            return GradingPeriodType.END_OF_YEAR;
        case 2:
            return GradingPeriodType.FIFTH_SIX_WEEKS;
        case 3:
            return GradingPeriodType.FIRST_NINE_WEEKS;
        case 4:
            return GradingPeriodType.FIRST_SEMESTER;
        case 5:
            return GradingPeriodType.FIRST_SIX_WEEKS;
        case 6:
            return GradingPeriodType.FIRST_SUMMER_SESSION;
        case 7:
            return GradingPeriodType.FIRST_TRIMESTER;
        case 8:
            return GradingPeriodType.FOURTH_NINE_WEEKS;
        case 9:
            return GradingPeriodType.FOURTH_SIX_WEEKS;
        case 10:
            return GradingPeriodType.SECOND_NINE_WEEKS;
        case 11:
            return GradingPeriodType.SECOND_SEMESTER;
        case 12:
            return GradingPeriodType.SECOND_SIX_WEEKS;
        case 13:
            return GradingPeriodType.SECOND_SUMMER_SESSION;
        case 14:
            return GradingPeriodType.SECOND_TRIMESTER;
        case 15:
            return GradingPeriodType.SIXTH_SIX_WEEKS;
        case 16:
            return GradingPeriodType.SUMMER_SEMESTER;
        case 17:
            return GradingPeriodType.THIRD_NINE_WEEKS;
        case 18:
            return GradingPeriodType.THIRD_SIX_WEEKS;
        case 19:
            return GradingPeriodType.THIRD_SUMMER_SESSION;
        default:
            return GradingPeriodType.THIRD_TRIMESTER;
        }
    }

}
