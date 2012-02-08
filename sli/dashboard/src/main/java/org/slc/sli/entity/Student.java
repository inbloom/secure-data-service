package org.slc.sli.entity;

import org.slc.sli.util.Constants;

/**
 *
 * TODO: Write Javadoc
 *
 */
public class Student {

    private String id, studentUniqueStateId, sex, economicDisadvantaged, limitedEnglishProficiency, schoolFoodServicesEligibility, cohortYear;

    public void setLimitedEnglishProficiency(String limitedEnglishProficiency) {
        this.limitedEnglishProficiency = limitedEnglishProficiency;
    }

    public void setSchoolFoodServicesEligibility(String schoolFoodServicesEligibility) {
        this.schoolFoodServicesEligibility = schoolFoodServicesEligibility;
    }

    private static String[] studentEntityProgramCodes = {Constants.PROGRAM_ELL, Constants.PROGRAM_FRE};

    private NameData name;
    public NameData getName() {
        return name;
    }

    public void setName(NameData name) {
        this.name = name;

    }

    public BirthData getBirthData() {
        return birthData;
    }

    public void setBirthData(BirthData birthData) {
        this.birthData = birthData;
    }

    private BirthData birthData;

    private class BirthData {
        String birthDate;

        @Override
        public String toString() {
            return birthDate;
        }
    }


    public String getFirstName() {
        return name.getFirstName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return name.getLastSurname();
    }

    private class NameData {
        private String firstName, middleName, lastSurname;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastSurname() {
            return lastSurname;
        }

        public void setLastSurname(String lastSurname) {
            this.lastSurname = lastSurname;
        }

    }

    public static String[] getProgramCodesForStudent() {
        return studentEntityProgramCodes;
    }


    public boolean hasProgramParticipation(String programCode) {
        if (programCode.equals(Constants.PROGRAM_ELL)) {
            return hasLimitedEnglishProficiency();
        }
        if (programCode.equals(Constants.PROGRAM_FRE)) {
            return hasSchoolFoodServiceEligibility();
        }
        return false;
    }

    private boolean hasLimitedEnglishProficiency() {
        return (limitedEnglishProficiency != null) && (limitedEnglishProficiency.equals(Constants.SHOW_ELL_LOZENGE));
    }


    private boolean hasSchoolFoodServiceEligibility() {
        for (Constants.FREParticipation part : Constants.FREParticipation.values()) {
            if ((schoolFoodServicesEligibility != null) && (schoolFoodServicesEligibility.equals(part.getValue()))) {
                return true;
            }
        }
        return false;
    }

    /*
    private boolean isLimitedEnglishProficient() {
        return (limitedEnglishProficiency != null) && (limitedEnglishProficiency.equals("Yes"));
    }


    private boolean isSchoolFoodServiceEligile() {
        return (schoolFoodServicesEligibility != null) && (schoolFoodServicesEligibility.equals("Free"));
    }
    */
    
    public void setCohortYear(String cohortYear) {
        this.cohortYear = cohortYear;
    }

    public String getCohortYear() {
        return this.cohortYear;
    }    
}
