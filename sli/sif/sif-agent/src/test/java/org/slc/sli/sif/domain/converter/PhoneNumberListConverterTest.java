/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

import org.slc.sli.sif.ADKTest;
import org.slc.sli.sif.domain.slientity.InstitutionTelephone;

public class PhoneNumberListConverterTest extends ADKTest {

    private final PhoneNumberListConverter converter = new PhoneNumberListConverter();
    private int counter = 0;
    private Map<PhoneNumberType, String> map = new HashMap<PhoneNumberType, String>();

    @Test
    public void testNullObject(){
        List<InstitutionTelephone> result = converter.convert(null);
        Assert.assertNull("Telephone list should be null", result);
    }

    @Test
    public void testEmptyList(){
        PhoneNumberList list = new PhoneNumberList();
        list.setPhoneNumbers(new PhoneNumber[0]);
        List<InstitutionTelephone> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyPhoneNumber(){
        PhoneNumberList list = new PhoneNumberList();
        PhoneNumber original = new PhoneNumber();
        list.add(original);
        List<InstitutionTelephone> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        InstitutionTelephone it = result.get(0);
        Assert.assertEquals(original.getNumber(), it.getTelephoneNumber());
        Assert.assertEquals("Other", it.getInstitutionTelephoneNumberType());
    }

    @Test
    public void testMappings(){
        map.clear();
        map.put(PhoneNumberType.ANSWERING_SERVICE, "Administrative");
        map.put(PhoneNumberType.FAX, "Fax");
        map.put(PhoneNumberType.PRIMARY, "Main");
        map.put(PhoneNumberType.VOICE_MAIL, "Attendance");

        PhoneNumberList list = getPhoneNumberList();
        List<InstitutionTelephone> results = converter.convert(list);

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

        phoneNumberList.add(getPhoneNumber(PhoneNumberType.ANSWERING_SERVICE));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.FAX));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.PRIMARY));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.VOICE_MAIL));

        phoneNumberList.add(getPhoneNumber(PhoneNumberType.ALT));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.APPOINTMENT));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.BEEPER));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.INSTANT_MESSAGING));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.MEDIA_CONFERENCE));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.TELEMAIL));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.TELEX));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.VOICE_MAIL));
        phoneNumberList.add(getPhoneNumber(PhoneNumberType.wrap("something else")));

        return phoneNumberList;
    }

    private PhoneNumber getPhoneNumber(PhoneNumberType type) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("919-555-000" + counter++);
        phoneNumber.setType(type);
        return phoneNumber;
    }

    private void testMapping(PhoneNumber original, InstitutionTelephone it){
        Assert.assertEquals(it.getTelephoneNumber(), original.getNumber());

         String expectedType = "Other";
         PhoneNumberType originalType = PhoneNumberType.wrap(original.getType());
         if (map.containsKey(originalType)) {
             expectedType = map.get(originalType);
         }
         Assert.assertEquals(expectedType, it.getInstitutionTelephoneNumberType());
    }
}
