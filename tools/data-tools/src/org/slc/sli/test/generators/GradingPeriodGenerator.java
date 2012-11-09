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

public class GradingPeriodGenerator {
    private static final Logger log = Logger.getLogger(GradingPeriodGenerator.class);
    private String beginDate = null;
    private String endDate = null;
    Random generator = new Random();

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
        int roll = 45 + (int) (Math.random() * (150 - 45));
        gp.setTotalInstructionalDays(roll);

        return gp;
    }

    public GradingPeriodType getGradingPeriodType(int roll) {
        return GradingPeriodType.FIRST_NINE_WEEKS;
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
