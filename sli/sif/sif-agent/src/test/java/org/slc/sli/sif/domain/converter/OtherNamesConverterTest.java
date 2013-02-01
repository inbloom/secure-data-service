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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.Name;
import openadk.library.common.OtherNames;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.OtherName;

/**
 * Unit tests for OtherNamesConverter.
 *
 * @author jtully
 *
 */
public class OtherNamesConverterTest extends AdkTest {

    OtherNamesConverter converter;

    private Map<String, String> nameTypeMap;

    private final String firstName = "Michael";
    private final String middleName = "Valentino";
    private final String lastName = "Bridges";
    private final String preferredName = "Mick";
    private final String fullName = "Michael Bridges";
    private final String prefix = "Dr";
    private final String suffix = "Jr";

    private final String defaultName = "Unknown";

    @Before
    public void setup() {

        converter = new OtherNamesConverter();
        converter.setNameConverter(new NameConverter());

        nameTypeMap = new HashMap<String, String>();

        nameTypeMap.put("01", "Previous Legal Name");
        nameTypeMap.put("02", "Other Name");
        nameTypeMap.put("03", "Alias");
        nameTypeMap.put("05", "Previous Legal Name");
        nameTypeMap.put("07", "Other Name");
        nameTypeMap.put("08", "Other Name");
    }

    @Test
    public void testNullObject() {
        OtherNames sifOtherNames = null;
        List<OtherName> result = converter.convert(sifOtherNames);
        Assert.assertNull("OtherName list should be null", result);
    }

    @Test
    public void testEmptyList() {
        OtherNames sifOtherNames = new OtherNames();
        List<OtherName> result = converter.convert(sifOtherNames);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyEmail() {
        OtherNames sifOtherNames = new OtherNames();
        sifOtherNames.add(new Name());
        List<OtherName> result = converter.convert(sifOtherNames);

        Assert.assertEquals(1, result.size());

        OtherName sliOtherName = result.get(0);
        Assert.assertNull(sliOtherName.getOtherNameType());
        Assert.assertNotNull(sliOtherName.getFirstName());
        Assert.assertEquals(defaultName, sliOtherName.getFirstName());
        Assert.assertNotNull(sliOtherName.getLastSurname());
        Assert.assertEquals(defaultName, sliOtherName.getLastSurname());
        Assert.assertNull(sliOtherName.getMiddleName());
        Assert.assertNull(sliOtherName.getPersonalTitlePrefix());
        Assert.assertNull(sliOtherName.getGenerationCodeSuffix());
    }

    @Test
    public void testMappings() {
        OtherNames otherNames = createOtherNames();
        List<OtherName> result = converter.convert(otherNames);
        Assert.assertEquals("Should map 6 othernames", 6, result.size());

        Iterator<OtherName> resIt = result.iterator();
        for (String sliNameType : nameTypeMap.values()) {
            OtherName sliName = resIt.next();

            Assert.assertNotNull(sliName);
            Assert.assertEquals(sliNameType, sliName.getOtherNameType());
            Assert.assertEquals(firstName, sliName.getFirstName());
            Assert.assertEquals(lastName, sliName.getLastSurname());
            Assert.assertEquals(middleName, sliName.getMiddleName());
            Assert.assertEquals(suffix, sliName.getGenerationCodeSuffix());
            Assert.assertEquals(prefix, sliName.getPersonalTitlePrefix());
        }
    }

    private OtherNames createOtherNames() {
        OtherNames otherNames = new OtherNames();
        for (String sifNameType : nameTypeMap.keySet()) {
            Name sifName = new Name();
            sifName.setType(sifNameType);
            sifName.setFirstName(firstName);
            sifName.setLastName(lastName);
            sifName.setMiddleName(middleName);
            sifName.setFullName(fullName);
            sifName.setPrefix(prefix);
            sifName.setSuffix(suffix);
            sifName.setPreferredName(preferredName);
            otherNames.add(sifName);
        }
        return otherNames;
    }
}
