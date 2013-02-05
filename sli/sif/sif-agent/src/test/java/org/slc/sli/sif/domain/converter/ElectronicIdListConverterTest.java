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
import openadk.library.common.ElectronicId;
import openadk.library.common.ElectronicIdList;
import openadk.library.common.ElectronicIdType;

import org.junit.Test;

import org.slc.sli.sif.AdkTest;
import org.slc.sli.sif.domain.slientity.StaffIdentificationCode;

/**
 * ElectronicIdListConverter unit tests
 */
public class ElectronicIdListConverterTest extends AdkTest {

    private final ElectronicIdListConverter converter = new ElectronicIdListConverter();
    private int counter = 0;
    private Map<ElectronicIdType, String> map = new HashMap<ElectronicIdType, String>();

    @Test
    public void testNullObject() {
        List<StaffIdentificationCode> result = converter.convert((ElectronicIdList) null);
        Assert.assertNull("StaffIdentificationCode list should be null", result);

        result = converter.convert((ElectronicIdList) null);
        Assert.assertNull("StaffIdentificationCode list should be null", result);
    }

    @Test
    public void testEmptyElectronicIdList() {
        ElectronicIdList list = new ElectronicIdList();
        list.setElectronicIds(new ElectronicId[0]);
        List<StaffIdentificationCode> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testEmptyElectronicId4ElectronicIdList() {
        ElectronicIdList list = new ElectronicIdList();
        ElectronicId original = new ElectronicId();
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
        map.put(ElectronicIdType.BARCODE, "Other");
        map.put(ElectronicIdType.MAGSTRIPE, "Other");
        map.put(ElectronicIdType.PIN, "PIN");
        map.put(ElectronicIdType.RFID, "Other");

        ElectronicIdList list = getElectronicIdList();
        List<StaffIdentificationCode> results = converter.convert(list);

        Assert.assertEquals(list.size(), results.size());

        int newCounter = 0;
        for (StaffIdentificationCode it : results) {
            Assert.assertNotNull(it);
            ElectronicId original = list.get(newCounter++);
            testMapping(original, it);
        }

        ElectronicIdList list2 = getElectronicIdList();
        List<StaffIdentificationCode> results2 = converter.convert(list2);
        Assert.assertEquals(list2.size(), results2.size());

        newCounter = 0;
        for (StaffIdentificationCode it : results2) {
            Assert.assertNotNull(it);
            ElectronicId original = list2.get(newCounter++);
            testMapping(original, it);
        }

    }

    private ElectronicIdList getElectronicIdList() {
        ElectronicIdList otherIdList = new ElectronicIdList();

        for (ElectronicIdType type : map.keySet()) {
            otherIdList.add(getElectronicId(type));
        }
        otherIdList.add(getElectronicId(ElectronicIdType.wrap("something else")));

        return otherIdList;
    }

    private ElectronicId getElectronicId(ElectronicIdType type) {
        ElectronicId otherId = new ElectronicId();
        otherId.setValue("919-555-000" + counter++);
        otherId.setType(type);
        return otherId;
    }

    private void testMapping(ElectronicId original, StaffIdentificationCode it) {
        Assert.assertEquals(it.getID(), original.getValue());

        String expectedType = "Other";
        ElectronicIdType originalType = ElectronicIdType.wrap(original.getType());
        if (map.containsKey(originalType)) {
            expectedType = map.get(originalType);
        }
        Assert.assertEquals(expectedType, it.getIdentificationSystem());
    }
}
