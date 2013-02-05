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


package org.slc.sli.dashboard.entity.util;

import java.util.Map;

import org.slc.sli.dashboard.util.Constants;

/**
 * This class is used by the StudentResolver to help determine student program participation
 *
 * @author dwu
 *
 */
public class StudentProgramUtil {

    private static String[] studentEntityProgramCodes = {Constants.PROGRAM_ELL, Constants.PROGRAM_FRE};
    
    public static String[] getProgramCodesForStudent() {
        return studentEntityProgramCodes.clone();
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
        String limitedEnglishProficiency = (String) (student.get(Constants.PROGRAM_ELL));
        return (limitedEnglishProficiency != null) && (limitedEnglishProficiency.equals(Constants.SHOW_ELL_LOZENGE));
    }
    
    
    private static boolean hasSchoolFoodServiceEligibility(Map student) {
        for (Constants.FREParticipation part : Constants.FREParticipation.values()) {
            String schoolFoodServicesEligibility = (String) (student.get(Constants.PROGRAM_FRE));
            if ((schoolFoodServicesEligibility != null) && (schoolFoodServicesEligibility.equals(part.getValue()))) {
                return true;
            }
        }
        return false;
    }

    
}
