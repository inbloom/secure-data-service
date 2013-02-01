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

import java.util.Date;

/**
 * Corresponding to the address defined in SLI schema.
 *
 * The following fields have no counterparts found in SIF doamin,
 * and hence not mapped:
 * <ol>
 * <li>countyFIPSCode</li>
 * <li>latitude</li>
 * <li>longitude</li>
 * <li>openDate</li>
 * <li>closeDate</li>
 * </ol>
 *
 * @author slee
 *
 */
public class Address {
    private String streetNumberName;
    private String apartmentRoomSuiteNumber;
    private String buildingSiteNumber;
    private String city;
    private String stateAbbreviation;
    private String postalCode;
    private String nameOfCounty;
    private String countyFIPSCode;
    private String countryCode;
    private String latitude;
    private String longitude;
    private Date openDate;
    private Date closeDate;
    private String addressType;

    public Address() {
        // Empty Default constructor
    }

    public String getStreetNumberName() {
        return this.streetNumberName;
    }

    public void setStreetNumberName(String streetNumberName) {
        this.streetNumberName = streetNumberName;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateAbbreviation() {
        return this.stateAbbreviation;
    }

    public void setStateAbbreviation(String stateAbbreviation) {
        this.stateAbbreviation = stateAbbreviation;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
