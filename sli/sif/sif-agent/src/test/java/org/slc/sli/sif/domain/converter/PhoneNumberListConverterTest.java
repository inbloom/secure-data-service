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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.PhoneNumber;
import openadk.library.common.PhoneNumberList;
import openadk.library.common.PhoneNumberType;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;

/**
 * PhoneNumberListConverter unit tests
 */
public class PhoneNumberListConverterTest extends AdkTest {

    private final PhoneNumberListConverter converter = new PhoneNumberListConverter();
    private int counter = 0;
    private Map<PhoneNumberType, String> map = new HashMap<PhoneNumberType, String>();

    @Test
    public void testNullObject() {
        List<InstitutionTelephone> result = converter.convertInstitutionTelephone(null);
        Assert.assertNull("Telephone list should be null", result);
    }

    @Test
    public void testEmptyList() {
        PhoneNumberList list = new PhoneNumberList();
        list.setPhoneNumbers(new PhoneNumber[0]);
        List<InstitutionTelephone> result = converter.convertInstitutionTelephone(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyPhoneNumber() {
        PhoneNumberList list = new PhoneNumberList();
        PhoneNumber original = new PhoneNumber();
        list.add(original);
        List<InstitutionTelephone> result = converter.convertInstitutionTelephone(list);

        Assert.assertEquals(1, result.size());

        InstitutionTelephone it = result.get(0);
        Assert.assertEquals(original.getNumber(), it.getTelephoneNumber());
        Assert.assertEquals("Other", it.getInstitutionTelephoneNumberType());
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(PhoneNumberType.ALT, "Other");
        map.put(PhoneNumberType.ANSWERING_SERVICE, "Other");
        map.put(PhoneNumberType.APPOINTMENT, "Other");
        map.put(PhoneNumberType.BEEPER, "Other");
        map.put(PhoneNumberType.FAX, "Fax");
        map.put(PhoneNumberType.INSTANT_MESSAGING, "Other");
        map.put(PhoneNumberType.MEDIA_CONFERENCE, "Other");
        map.put(PhoneNumberType.PRIMARY, "Main");
        map.put(PhoneNumberType.SIF1x_ALT, "Other");
        map.put(PhoneNumberType.SIF1x_ANSWERING_SERVICE, "Other");
        map.put(PhoneNumberType.SIF1x_APPT_NUMBER, "Other");
        map.put(PhoneNumberType.SIF1x_BEEPER, "Other");
        map.put(PhoneNumberType.SIF1x_EXT, "Other");
        map.put(PhoneNumberType.SIF1x_HOME_FAX, "Fax");
        map.put(PhoneNumberType.SIF1x_HOME_PHONE, "Other");
        map.put(PhoneNumberType.SIF1x_NIGHT_PHONE, "Other");
        map.put(PhoneNumberType.SIF1x_OTHER_RES_FAX, "Other");
        map.put(PhoneNumberType.SIF1x_OTHER_RES_PHONE, "Other");
        map.put(PhoneNumberType.SIF1x_PERSONAL_CELL, "Other");
        map.put(PhoneNumberType.SIF1x_PERSONAL_PHONE, "Other");
        map.put(PhoneNumberType.SIF1x_TELEMAIL, "Other");
        map.put(PhoneNumberType.SIF1x_TELEX, "Other");
        map.put(PhoneNumberType.SIF1x_VOICEMAIL, "Other");
        map.put(PhoneNumberType.SIF1x_WORK_CELL, "Other");
        map.put(PhoneNumberType.SIF1x_WORK_FAX, "Other");
        map.put(PhoneNumberType.SIF1x_WORK_PHONE, "Other");
        map.put(PhoneNumberType.TELEMAIL, "Other");
        map.put(PhoneNumberType.TELEX, "Other");
        map.put(PhoneNumberType.VOICE_MAIL, "Other");

        PhoneNumberList list = getPhoneNumberList();
        List<InstitutionTelephone> results = converter.convertInstitutionTelephone(list);

        Assert.assertEquals(list.size(), results.size());

        int newCounter = 0;
        for (InstitutionTelephone it : results) {
            Assert.assertNotNull(it);
            PhoneNumber original = list.get(newCounter++);
            testMapping(original, it);
        }
    }

    private PhoneNumberList getPhoneNumberList() {
        PhoneNumberList phoneNumberList = new PhoneNumberList();

        for (PhoneNumberType type : map.keySet()) {
            phoneNumberList.add(getPhoneNumber(type));
        }
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.wrap("something else")));

        return phoneNumberList;
    }

    private PhoneNumber getPhoneNumber(PhoneNumberType type) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("919-555-000" + counter++);
        phoneNumber.setType(type);
        return phoneNumber;
    }

    private void testMapping(PhoneNumber original, InstitutionTelephone it) {
        Assert.assertEquals(it.getTelephoneNumber(), original.getNumber());

        String expectedType = "Other";
        PhoneNumberType originalType = PhoneNumberType.wrap(original.getType());
        if (map.containsKey(originalType)) {
            expectedType = map.get(originalType);
        }
        Assert.assertEquals(expectedType, it.getInstitutionTelephoneNumberType());
    }
}
