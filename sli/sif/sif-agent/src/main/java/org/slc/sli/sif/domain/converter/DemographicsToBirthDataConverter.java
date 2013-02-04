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

package org.slc.sli.sif.domain.converter;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import openadk.library.common.Demographics;

import org.springframework.stereotype.Component;

import org.slc.sli.sif.domain.slientity.BirthData;

/**
 * Converts SIF Demographics to SLI BirthData
 *
 * @author jtully
 *
 */
@Component
public class DemographicsToBirthDataConverter {

    //SIF state codes are a subset of SLI state codes
    private static final Set<String> SLI_STATE_CODES = new HashSet<String>();

    static {
        SLI_STATE_CODES.add("AL");
        SLI_STATE_CODES.add("CT");
        SLI_STATE_CODES.add("DC");
        SLI_STATE_CODES.add("DE");
        SLI_STATE_CODES.add("FL");
        SLI_STATE_CODES.add("GA");
        SLI_STATE_CODES.add("GU");
        SLI_STATE_CODES.add("HI");
        SLI_STATE_CODES.add("ID");
        SLI_STATE_CODES.add("IL");
        SLI_STATE_CODES.add("IN");
        SLI_STATE_CODES.add("IA");
        SLI_STATE_CODES.add("KS");
        SLI_STATE_CODES.add("KY");
        SLI_STATE_CODES.add("LA");
        SLI_STATE_CODES.add("ME");
        SLI_STATE_CODES.add("MD");
        SLI_STATE_CODES.add("MA");
        SLI_STATE_CODES.add("MI");
        SLI_STATE_CODES.add("MN");
        SLI_STATE_CODES.add("MS");
        SLI_STATE_CODES.add("MO");
        SLI_STATE_CODES.add("MT");
        SLI_STATE_CODES.add("NE");
        SLI_STATE_CODES.add("NV");
        SLI_STATE_CODES.add("NH");
        SLI_STATE_CODES.add("NJ");
        SLI_STATE_CODES.add("NM");
        SLI_STATE_CODES.add("NY");
        SLI_STATE_CODES.add("NC");
        SLI_STATE_CODES.add("ND");
        SLI_STATE_CODES.add("OH");
        SLI_STATE_CODES.add("OK");
        SLI_STATE_CODES.add("OR");
        SLI_STATE_CODES.add("PA");
        SLI_STATE_CODES.add("PR");
        SLI_STATE_CODES.add("RI");
        SLI_STATE_CODES.add("SC");
        SLI_STATE_CODES.add("SD");
        SLI_STATE_CODES.add("TN");
        SLI_STATE_CODES.add("TX");
        SLI_STATE_CODES.add("UT");
        SLI_STATE_CODES.add("VT");
        SLI_STATE_CODES.add("VA");
        SLI_STATE_CODES.add("WA");
        SLI_STATE_CODES.add("WV");
        SLI_STATE_CODES.add("WI");
        SLI_STATE_CODES.add("WY");
    }

    public BirthData convert(Demographics sifDemographics) {
        if (sifDemographics == null) {
            return null;
        }

        BirthData sliBirthData = new BirthData();

        sliBirthData.setCountryOfBirthCode(sifDemographics.getCountryOfBirth());

        sliBirthData.setCityOfBirth(sifDemographics.getPlaceOfBirth());

        if (SLI_STATE_CODES.contains(sifDemographics.getStateOfBirth())) {
            sliBirthData.setStateOfBirthAbbreviation(sifDemographics.getStateOfBirth());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (sifDemographics.getBirthDate() != null) {
            sliBirthData.setBirthDate(dateFormat.format(sifDemographics.getBirthDate().getTime()));
        }

        if (sifDemographics.getCountryArrivalDate() != null) {
            sliBirthData.setDateEnteredUS(dateFormat.format(sifDemographics.getCountryArrivalDate().getTime()));
        }

        return sliBirthData;
    }

}
