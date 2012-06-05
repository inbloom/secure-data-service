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

    public GradingPeriod getGradingPeriod (String orgId) {
        GradingPeriodIdentityType gpit = new GradingPeriodIdentityType();
        
        gpit.setGradingPeriod(getGradingPeriodType());
        gpit.setSchoolYear("2011-2012");
//        String orgId = new String("Test");
        gpit.getStateOrganizationIdOrEducationOrgIdentificationCode().add((Object) orgId);
        GradingPeriod gp = new GradingPeriod ();
//        gp.setGradingPeriod(getGradingPeriodType());
        //gp.setId(orgId);
        gp.setGradingPeriodIdentity(gpit);
        beginDate = "2011-03-04";
        endDate = "2012-03-04";
        gp.setBeginDate(beginDate);
        gp.setEndDate(endDate);
        int roll = 45 + (int) (Math.random() * (150 - 45));
        gp.setTotalInstructionalDays(roll);

        return gp;
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

    public static void main(String args[]) {
        GradingPeriodGenerator gpg = new GradingPeriodGenerator();
        for(int i = 0; i < 5; i++ ){
            GradingPeriod gp = gpg.getGradingPeriod(i+"");

            log.info("GradingPeriodType = " + gp.getGradingPeriodIdentity() + ",\n" +  "beginDate = " + gp.getBeginDate() + ",\n" +
                     "endDate = " + gp.getEndDate() + ",\n" + "totalInstructionDays = " + gp.getTotalInstructionalDays() + ",\n");

        }

    }

}
