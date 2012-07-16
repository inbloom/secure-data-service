package org.slc.sli.shtick.pojomanual;

import java.util.*;

import org.slc.sli.shtick.pojo.*;

/**
 * The set of elements that capture relevant data regarding a person's birth, including birth date and place of birth.
 */
public final class BirthDataManual {
    private final Map<String, Object> data;

    public BirthDataManual(final Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getUnderlying() {
        return data;
    }

    public String getId() {
        return (String) data.get("id");
    }

    /**
     * The month, day, and year on which an individual was born.
     */
    public String getBirthDate() {
        return (String) data.get("birthDate");
    }

    public void setBirthDate(String date) {
        this.data.put("birthDate", date);
    }

    /**
     * The city the student was born in.
     */
    public City getCityOfBirth() {
        return new City((String) data.get("cityOfBirth"));
    }

    /**
     * The abbreviation for the name of the state (within the United States) or extra-state jurisdiction in which an individual was born.
     */
    public StateAbbreviationType getStateOfBirthAbbreviation() {
        return StateAbbreviationType.valueOfName((String) data.get("stateOfBirthAbbreviation"));
    }

    /**
     * The unique two digit International Organization for Standardization (ISO) code for the country in which an individual is born.
     */
    public CountryCodeType getCountryOfBirthCode() {
        return CountryCodeType.valueOfName((String) data.get("countryOfBirthCode"));
    }

    /**
     * For students born outside of the US, the date the student entered the US.
     */
    public String getDateEnteredUS() {
        return (String) data.get("dateEnteredUS");
    }

    /**
     * Indicator of whether the student was born with other siblings (i.e., twins, triplets, etc.)
     */
    public Boolean getMultipleBirthStatus() {
        return (Boolean) data.get("multipleBirthStatus");
    }
}
