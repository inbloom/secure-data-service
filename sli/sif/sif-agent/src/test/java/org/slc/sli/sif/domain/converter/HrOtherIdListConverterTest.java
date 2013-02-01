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
import openadk.library.common.OtherId;
import openadk.library.common.OtherIdList;
import openadk.library.common.OtherIdType;
import openadk.library.hrfin.HrOtherIdList;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.StaffIdentificationCode;

/**
 * HrOtherIdListConverter unit tests
 */
public class HrOtherIdListConverterTest extends AdkTest {

    private final HrOtherIdListConverter converter = new HrOtherIdListConverter();
    private int counter = 0;
    private Map<OtherIdType, String> map = new HashMap<OtherIdType, String>();

    @Test
    public void testNullObject() {
        List<StaffIdentificationCode> result = converter.convert((HrOtherIdList) null);
        Assert.assertNull("StaffIdentificationCode list should be null", result);

        result = converter.convert((OtherIdList) null);
        Assert.assertNull("StaffIdentificationCode list should be null", result);
    }

    @Test
    public void testEmptyHrOtherIdList() {
        HrOtherIdList list = new HrOtherIdList();
        list.setOtherIds(new OtherId[0]);
        List<StaffIdentificationCode> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyOtherIdList() {
        OtherIdList list = new OtherIdList();
        list.setOtherIds(new OtherId[0]);
        List<StaffIdentificationCode> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyOtherId4HrOtherIdList() {
        HrOtherIdList list = new HrOtherIdList();
        OtherId original = new OtherId();
        list.add(original);
        List<StaffIdentificationCode> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        StaffIdentificationCode it = result.get(0);
        Assert.assertEquals(original.getValue(), it.getID());
        Assert.assertEquals("Other", it.getIdentificationSystem());
    }

    @Test
    public void testEmptyOtherId4OtherIdList() {
        OtherIdList list = new OtherIdList();
        OtherId original = new OtherId();
        list.add(original);
        List<StaffIdentificationCode> result = converter.convert(list);

        Assert.assertEquals(1, result.size());

        StaffIdentificationCode it = result.get(0);
        Assert.assertEquals(original.getValue(), it.getID());
        Assert.assertEquals("Other", it.getIdentificationSystem());
    }

    @Test
    public void testMappings() {
        map.clear();
        map.put(OtherIdType.ACT_INST, "Other");
        map.put(OtherIdType.ACT_PROG, "Other");
        map.put(OtherIdType.ATP, "Other");
        map.put(OtherIdType.CA_SIN, "Canadian SIN");
        map.put(OtherIdType.CERTIFICATE, "Professional Certificate");
        map.put(OtherIdType.CONTRACTOR, "Other");
        map.put(OtherIdType.DISTRICT_ASSIGNED, "District");
        map.put(OtherIdType.DRIVERS_LICENSE, "Drivers License");
        map.put(OtherIdType.DUNS, "Other");
        map.put(OtherIdType.FAMILY_UNIT, "Other");
        map.put(OtherIdType.FEDERAL, "Federal");
        map.put(OtherIdType.HEALTH_RECORD, "Health Record");
        map.put(OtherIdType.IPEDS, "Other");
        map.put(OtherIdType.LEA_SCHOOL, "School");
        map.put(OtherIdType.MEDICAID, "Medicaid");
        map.put(OtherIdType.MIGRANT, "Other");
        map.put(OtherIdType.NCES_LEA, "Other");
        map.put(OtherIdType.NCES_LEA, "Other");
        map.put(OtherIdType.OTHER, "Other");
        map.put(OtherIdType.OTHER_AGENCY, "Other");
        map.put(OtherIdType.OTHER_FEDERAL, "Other Federal");
        map.put(OtherIdType.PERSONAL, "Other");
        map.put(OtherIdType.SCHOOL_ASSIGNED, "School");
        map.put(OtherIdType.SEA_LEA, "State");
        map.put(OtherIdType.SEA_SCHOOL, "State");
        map.put(OtherIdType.SELECTIVE_SERVICE, "Other");
        map.put(OtherIdType.SIF1x_DISTRICT_ASSIGNED_NUM, "District");
        map.put(OtherIdType.SIF1x_DRIVERS_LICENSE, "Drivers License");
        map.put(OtherIdType.SIF1x_FAMILY_UNIT, "Other");
        map.put(OtherIdType.SIF1x_HEATH_RECORD, "Health Record");
        map.put(OtherIdType.SIF1x_MEDICAID, "Medicaid");
        map.put(OtherIdType.SIF1x_MIGRANT, "Other");
        map.put(OtherIdType.SIF1x_OTHER, "Other");
        map.put(OtherIdType.SIF1x_PIN, "PIN");
        map.put(OtherIdType.SIF1x_PROFESSIONAL_LICENSE, "Professional Certificate");
        map.put(OtherIdType.SIF1x_SCHOOL_ASSIGNED_NUM, "School");
        map.put(OtherIdType.SIF1x_SELECTIVE_SERVICE, "Selective Service");
        map.put(OtherIdType.SIF1x_SSN, "SSN");
        map.put(OtherIdType.SIF1x_STATE_ASSIGNED_NUM, "State");
        map.put(OtherIdType.SIF1x_VISA, "US Visa");

        HrOtherIdList list = getHrOtherIdList();
        List<StaffIdentificationCode> results = converter.convert(list);

        Assert.assertEquals(list.size(), results.size());

        int newCounter = 0;
        for (StaffIdentificationCode it : results) {
            Assert.assertNotNull(it);
            OtherId original = list.get(newCounter++);
            testMapping(original, it);
        }

        OtherIdList list2 = getOtherIdList();
        List<StaffIdentificationCode> results2 = converter.convert(list2);
        Assert.assertEquals(list2.size(), results2.size());

        newCounter = 0;
        for (StaffIdentificationCode it : results2) {
            Assert.assertNotNull(it);
            OtherId original = list2.get(newCounter++);
            testMapping(original, it);
        }

    }

    private HrOtherIdList getHrOtherIdList() {
        HrOtherIdList hrOtherIdList = new HrOtherIdList();

        for (OtherIdType type : map.keySet()) {
            hrOtherIdList.add(getOtherId(type));
        }
        hrOtherIdList.add(getOtherId(OtherIdType.wrap("something else")));

        return hrOtherIdList;
    }

    private OtherIdList getOtherIdList() {
        OtherIdList otherIdList = new OtherIdList();

        for (OtherIdType type : map.keySet()) {
            otherIdList.add(getOtherId(type));
        }
        otherIdList.add(getOtherId(OtherIdType.wrap("something else")));

        return otherIdList;
    }

    private OtherId getOtherId(OtherIdType type) {
        OtherId otherId = new OtherId();
        otherId.setValue("919-555-000" + counter++);
        otherId.setType(type);
        return otherId;
    }

    private void testMapping(OtherId original, StaffIdentificationCode it) {
        Assert.assertEquals(it.getID(), original.getValue());

        String expectedType = "Other";
        OtherIdType originalType = OtherIdType.wrap(original.getType());
        if (map.containsKey(originalType)) {
            expectedType = map.get(originalType);
        }
        Assert.assertEquals(expectedType, it.getIdentificationSystem());
    }
}

