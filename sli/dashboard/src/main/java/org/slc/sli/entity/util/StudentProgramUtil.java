package org.slc.sli.entity.util;

import java.util.Map;

import org.slc.sli.util.Constants;

/**
 * This class is used by the StudentResolver to help determine student program participation
 *
 * @author dwu
 *
 */
public class StudentProgramUtil {

    private static String[] studentEntityProgramCodes = {Constants.PROGRAM_ELL, Constants.PROGRAM_FRE};
    
    public static String[] getProgramCodesForStudent() {
        return studentEntityProgramCodes;
    }

    
    public static boolean hasProgramParticipation(Map student, String programCode) {
        if (programCode.equals(Constants.PROGRAM_ELL)) {
            return hasLimitedEnglishProficiency(student);
        }
        if (programCode.equals(Constants.PROGRAM_FRE)) {
            return hasSchoolFoodServiceEligibility(student);
        }
        return false;
    }
    
    private static boolean hasLimitedEnglishProficiency(Map student) {
        String limitedEnglishProficiency = (String) (student.get("limitedEnglishProficiency"));
        return (limitedEnglishProficiency != null) && (limitedEnglishProficiency.equals(Constants.SHOW_ELL_LOZENGE));
    }
    
    
    private static boolean hasSchoolFoodServiceEligibility(Map student) {
        for (Constants.FREParticipation part : Constants.FREParticipation.values()) {
            String schoolFoodServicesEligibility = (String) (student.get("schoolFoodServicesEligibility"));
            if ((schoolFoodServicesEligibility != null) && (schoolFoodServicesEligibility.equals(part.getValue()))) {
                return true;
            }
        }
        return false;
    }

    
}
