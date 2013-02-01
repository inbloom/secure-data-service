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
 * Corresponding to the name defined in SLI schema.
 *
 * @author slee
 *
 */
public class Name {
    //required fields
    private String firstName;
    private String lastSurname;
    //optional fields
    private String middleName;
    private String personalTitlePrefix;
    private String generationCodeSuffix;
    //unmatched fields
    private String maidenName;
    private String personalInformationVerificationType;

    public Name() {
        // Empty Default constructor
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastSurname() {
        return this.lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPersonalTitlePrefix() {
        return this.personalTitlePrefix;
    }

    public void setPersonalTitlePrefix(String personalTitlePrefix) {
        this.personalTitlePrefix = personalTitlePrefix;
    }

    public String getGenerationCodeSuffix() {
        return this.generationCodeSuffix;
    }

    public void setGenerationCodeSuffix(String generationCodeSuffix) {
        this.generationCodeSuffix = generationCodeSuffix;
    }

}
