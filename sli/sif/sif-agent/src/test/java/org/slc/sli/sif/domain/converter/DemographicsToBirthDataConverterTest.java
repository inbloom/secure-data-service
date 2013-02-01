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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.BirthData;

/**
 * Unit tests for DemographicsToBirthDataConverter
 *
 * @author jtully
 *
 */
public class DemographicsToBirthDataConverterTest extends AdkTest {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DemographicsToBirthDataConverter converter = new DemographicsToBirthDataConverter();

    private Calendar birthDate = Calendar.getInstance();
    private String countryOfBirth = "US";
    private String placeOfBirth = "Durham";
    private Calendar countryArrivalDate = Calendar.getInstance();

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

    @Before
    public void setup() {

        birthDate.set(1992, 11, 25);
        countryArrivalDate.set(2011, 06, 04);
    }

    @Test
    public void nullNameShouldReturnNull() {
        openadk.library.common.Demographics sifDemographics = null;
        BirthData sliName = converter.convert(sifDemographics);
        Assert.assertNull(sliName);
    }

    @Test
    public void emptySifDemographicShouldMapToNullBirthDataValues() {
        openadk.library.common.Demographics sifDemographics = new openadk.library.common.Demographics();
        BirthData sliBirthData = converter.convert(sifDemographics);

        Assert.assertNotNull(sliBirthData);
        Assert.assertNull(sliBirthData.getCityOfBirth());
        Assert.assertNull(sliBirthData.getCountryOfBirthCode());
        Assert.assertNull(sliBirthData.getStateOfBirthAbbreviation());
        Assert.assertNull(sliBirthData.getBirthDate());
        Assert.assertNull(sliBirthData.getDateEnteredUS());
    }

    @Test
    public void shouldMapSifDemographicToSliBirthData() {
        for (String state : SLI_STATE_CODES) {
            BirthData sliBirthData = converter.convert(createSifDemographics(state));
            Assert.assertNotNull(sliBirthData);
            Assert.assertEquals(placeOfBirth, sliBirthData.getCityOfBirth());
            Assert.assertEquals(countryOfBirth, sliBirthData.getCountryOfBirthCode());
            Assert.assertEquals(state, sliBirthData.getStateOfBirthAbbreviation());
            Assert.assertEquals(DATE_FORMAT.format(birthDate.getTime()), sliBirthData.getBirthDate());
            Assert.assertEquals(DATE_FORMAT.format(countryArrivalDate.getTime()), sliBirthData.getDateEnteredUS());
        }

        //check invalid state codes are not mapped
        BirthData sliBirthData = converter.convert(createSifDemographics("invlaid_state"));
        Assert.assertNotNull(sliBirthData);
        Assert.assertNull(sliBirthData.getStateOfBirthAbbreviation());
    }

    private openadk.library.common.Demographics createSifDemographics(String stateOfBirth) {
        openadk.library.common.Demographics sifDemographics = new openadk.library.common.Demographics();
        sifDemographics.setBirthDate(birthDate);
        sifDemographics.setCountryArrivalDate(countryArrivalDate);
        sifDemographics.setCountryOfBirth(countryOfBirth);
        sifDemographics.setPlaceOfBirth(placeOfBirth);
        sifDemographics.setStateOfBirth(stateOfBirth);

        return sifDemographics;
    }

}
