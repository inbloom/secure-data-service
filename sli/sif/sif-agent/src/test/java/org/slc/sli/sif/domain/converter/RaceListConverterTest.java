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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import openadk.library.common.RaceList;
import openadk.library.common.RaceType;
import openadk.library.common.Race;

import org.junit.Test;
import org.junit.Before;

import org.slc.sli.sif.AdkTest;

/**
 * RaceListConverter unit tests
 */
public class RaceListConverterTest extends AdkTest {

    private final RaceListConverter converter = new RaceListConverter();
    private Map<RaceType, String> map = new HashMap<RaceType, String>();

    @Before
    public void setUp() {
        map.clear();
        map.put(RaceType.AFRICAN_AMERICAN, "Black - African American");
        map.put(RaceType.AMERICAN_INDIAN, "American Indian - Alaskan Native");
        map.put(RaceType.ASIAN, "Asian");
        map.put(RaceType.PACISLANDER, "Native Hawaiian - Pacific Islander");
        map.put(RaceType.WHITE, "White");
    }

    @Test
    public void testNullObject() {
        List<String> result = converter.convert(null);
        Assert.assertNull("Race list should be null", result);
    }

    @Test
    public void testEmptyList() {
        RaceList list = new RaceList();
        List<String> result = converter.convert(list);
        Assert.assertEquals(0, result.size());
    }

    // Tests conversion of each single value. 
    @Test
    public void testConversion() {
        for (RaceType rt : map.keySet()) {
            RaceList rl = new RaceList();
            Race r = new Race();
            r.setCode(rt);
            rl.add(r);
            List<String> convertedList = converter.convert(rl);
            Assert.assertEquals(1, convertedList.size());
            Assert.assertEquals(map.get(rt), convertedList.get(0));
        }
    }

    // Tests conversion of entire list. 
    @Test
    public void testListConversion() {

        RaceList rl = new RaceList();
        for (RaceType rt : map.keySet()) {
            Race r = new Race();
            r.setCode(rt);
            rl.add(r);
        }
        List<String> convertedList = converter.convert(rl);

        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(map.values());
        
        Collections.sort(convertedList);
        Collections.sort(expectedList);
        
        Assert.assertEquals(expectedList.size(), convertedList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            Assert.assertEquals(expectedList.get(i), convertedList.get(i));
        }
    }

}
