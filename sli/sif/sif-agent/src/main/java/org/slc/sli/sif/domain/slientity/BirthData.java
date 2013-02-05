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

package org.slc.sli.sif.domain.slientity;


/**
 * Corresponding to the birthData defined in SLI schema.
 * BirthData is mapped from a subset of openadk.library.common.Demographics
 *
 * @author slee
 *
 */
public class BirthData {
    //required fields
    private String birthDate;
    //optional fields
    private String cityOfBirth;
    private String stateOfBirthAbbreviation;
    private String countryOfBirthCode;
    private String dateEnteredUS;
    //unmatched fields
    private boolean multipleBirthStatus;

    public BirthData() {
        // Empty Default constructor
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDateEnteredUS() {
        return this.dateEnteredUS;
    }

    public void setDateEnteredUS(String dateEnteredUS) {
        this.dateEnteredUS = dateEnteredUS;
    }

    public String getCityOfBirth() {
        return this.cityOfBirth;
    }

    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }

    public String getStateOfBirthAbbreviation() {
        return this.stateOfBirthAbbreviation;
    }

    public void setStateOfBirthAbbreviation(String stateOfBirthAbbreviation) {
        this.stateOfBirthAbbreviation = stateOfBirthAbbreviation;
    }

    public String getCountryOfBirthCode() {
        return this.countryOfBirthCode;
    }

    public void setCountryOfBirthCode(String countryOfBirthCode) {
        this.countryOfBirthCode = countryOfBirthCode;
    }

}
