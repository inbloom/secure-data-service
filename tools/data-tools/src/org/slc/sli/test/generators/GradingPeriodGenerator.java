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

import java.util.Random;

import org.apache.log4j.Logger;
import org.slc.sli.test.edfi.entities.GradingPeriod;
import org.slc.sli.test.edfi.entities.GradingPeriodIdentityType;
import org.slc.sli.test.edfi.entities.GradingPeriodType;
import org.slc.sli.test.generators.interchange.InterchangeEdOrgCalGenerator;

public class GradingPeriodGenerator {
    private static final Logger log = Logger.getLogger(GradingPeriodGenerator.class);
    
    private String beginDate = null;
    private String endDate = null;
    Random generator = new Random(31);

    public GradingPeriod getGradingPeriod (String orgId,  int gradePeriodType) {
        GradingPeriodIdentityType gpit = new GradingPeriodIdentityType();
        
        gpit.setGradingPeriod(getGradingPeriodType(gradePeriodType));
        gpit.setSchoolYear("2011-2012");
        gpit.setStateOrganizationId(orgId);
        GradingPeriod gp = new GradingPeriod();
//        gp.setGradingPeriod(getGradingPeriodType());
        //gp.setId(orgId);
        gp.setGradingPeriodIdentity(gpit);
        beginDate = "2011-03-" + inTwoDigits(gradePeriodType);
        endDate = "2012-03-" + inTwoDigits(gradePeriodType);
        gp.setBeginDate(beginDate);
        gp.setEndDate(endDate);
        Random random = new Random(31);
        int roll = 45 + (int) (random.nextDouble() * (150 - 45));
        gp.setTotalInstructionalDays(roll);

        return gp;
    }

    public static GradingPeriodType getGradingPeriodType(int index) {
        index = index % InterchangeEdOrgCalGenerator.MAX_GRADING_PERIODS;
        switch (index) {
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

    private String inTwoDigits(int number) {
        return String.format("%02d", number);
    }

    public static void main(String args[]) {
        GradingPeriodGenerator gpg = new GradingPeriodGenerator();
        for(int i = 0; i < 5; i++ ){
            GradingPeriod gp = gpg.getGradingPeriod(i+"",i);

            log.info("GradingPeriodType = " + gp.getGradingPeriodIdentity() + ",\n" +  "beginDate = " + gp.getBeginDate() + ",\n" +
                     "endDate = " + gp.getEndDate() + ",\n" + "totalInstructionDays = " + gp.getTotalInstructionalDays() + ",\n");

        }

    }

}
