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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.Name;

/**
 * Unit Test for the name converter
 *
 * @author jtully
 *
 */
public class NameConverterTest extends AdkTest {

    private NameConverter converter = new NameConverter();

    private final String type = "04";
    private final String firstName = "Michael";
    private final String middleName = "Valentino";
    private final String lastName = "Bridges";
    private final String preferredName = "Mick";
    private final String fullName = "Michael Bridges";

    private final String defaultName = "Unknown";

    private List<String> prefixes;
    private List<String> suffixes;

    @Before
    public void setup() {

        prefixes = new ArrayList<String>();
        prefixes.add("Colonel");
        prefixes.add("Dr");
        prefixes.add("Mr");
        prefixes.add("Mrs");
        prefixes.add("Reverend");
        prefixes.add("Sr");
        prefixes.add("Sister");

        suffixes = new ArrayList<String>();
        suffixes.add("Jr");
        suffixes.add("Sr");
        suffixes.add("II");
        suffixes.add("III");
        suffixes.add("IV");
        suffixes.add("V");
        suffixes.add("VI");
        suffixes.add("VII");
        suffixes.add("VIII");
    }

    @Test
    public void nullNameShouldReturnNull() {
        openadk.library.common.Name sifName = null;
        Name sliName = converter.convert(sifName);
        Assert.assertNull(sliName);
    }

    @Test
    public void emptySifNameShouldMapToNullAndDefaultValues() {
        openadk.library.common.Name sifName = new openadk.library.common.Name();
        Name sliName = converter.convert(sifName);
        Assert.assertNotNull(sliName);
        Assert.assertEquals("Missing first name should map to Unknown", defaultName, sliName.getFirstName());
        Assert.assertEquals("Missing last name should map to Unknown", defaultName, sliName.getLastSurname());
        Assert.assertNull("Missing optional fields should map to null", sliName.getGenerationCodeSuffix());
        Assert.assertNull("Missing optional fields should map to null", sliName.getMiddleName());
        Assert.assertNull("Missing optional fields should map to null", sliName.getPersonalTitlePrefix());
    }

    @Test
    public void shouldMapSifNameToSliName() {
        // check field mappings
        Name sliName = converter.convert(createSifName(prefixes.get(0), suffixes.get(0)));
        Assert.assertNotNull(sliName);
        Assert.assertEquals("Incorrect prefix mapping", prefixes.get(0), sliName.getPersonalTitlePrefix());
        Assert.assertEquals("Incorrect firstName mapping", firstName, sliName.getFirstName());
        Assert.assertEquals("Incorrect lastName mapping", lastName, sliName.getLastSurname());
        Assert.assertEquals("Incorrect middleName mapping", middleName, sliName.getMiddleName());
        Assert.assertEquals("Incorrect suffix", suffixes.get(0), sliName.getGenerationCodeSuffix());

        // check mapping of all prefixes
        for (String prefix : prefixes) {
            sliName = converter.convert(createSifName(prefix, suffixes.get(0)));
            Assert.assertNotNull(sliName);
            Assert.assertEquals("Incorrect prefix mapping", prefix, sliName.getPersonalTitlePrefix());
        }
        // check mapping of all suffixes
        for (String suffix : suffixes) {
            sliName = converter.convert(createSifName(prefixes.get(0), suffix));
            Assert.assertNotNull(sliName);
            Assert.assertEquals("Incorrect suffix", suffix, sliName.getGenerationCodeSuffix());
        }

        // check mapping of unsupported prefix
        sliName = converter.convert(createSifName("unsupported", suffixes.get(0)));
        Assert.assertNotNull(sliName);
        Assert.assertNull("Unsupported prefix should map to null", sliName.getPersonalTitlePrefix());

        // check mapping of unsupported prefix
        sliName = converter.convert(createSifName(prefixes.get(0), "unsupported"));
        Assert.assertNotNull(sliName);
        Assert.assertNull("Unsupported suffix should map to null", sliName.getGenerationCodeSuffix());
    }

    private openadk.library.common.Name createSifName(String prefix, String suffix) {
        openadk.library.common.Name sifName = new openadk.library.common.Name();
        sifName.setType(type);
        sifName.setLastName(lastName);
        sifName.setFirstName(firstName);
        sifName.setPrefix(prefix);
        sifName.setMiddleName(middleName);
        sifName.setPreferredName(preferredName);
        sifName.setFullName(fullName);
        sifName.setSuffix(suffix);

        return sifName;
    }
}
