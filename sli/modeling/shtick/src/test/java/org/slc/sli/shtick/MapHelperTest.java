package org.slc.sli.shtick;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author jstokes
 */
public class MapHelperTest {
    @Before
    public void setup() {

    }

    @Test
    public void testDeepCopy() {
        final Map<String, Object> testMap = new HashMap<String, Object>();
        testMap.put("testField", "testValue");
        testMap.put("testField2", "testValue2");

        final Map<String, Object> innerMap = new HashMap<String, Object>();
        innerMap.put("innerField1", "innerValue1");

        final List<String> innerList = new ArrayList<String>();
        innerList.add("testListElem1");
        innerList.add("testListElem2");

        final List<Map<String, Object>> innerMapList = new ArrayList<Map<String, Object>>();
        final Map<String, Object> innerListMap1 = new HashMap<String, Object>();
        innerListMap1.put("innerListMap1", "innerListMap1Value");
        final Map<String, Object> innerListMap2 = new HashMap<String, Object>();
        innerListMap2.put("innerListMap2", "innerListMap2Value");
        innerMapList.add(innerListMap1);
        innerMapList.add(innerListMap2);

        testMap.put("map", innerMap);
        testMap.put("list", innerList);
        testMap.put("listOfMaps", innerMapList);

        Map<String, Object> copy = MapHelper.deepCopy(testMap);
        assertNotNull(copy);
        assertNotSame(testMap, copy);

        @SuppressWarnings("unchecked")
        final List<String> origInnerList = (List<String>) testMap.get("list");
        @SuppressWarnings("unchecked")
        final List<String> copyInnerList = (List<String>) testMap.get("list");

        assertTrue(origInnerList.containsAll(copyInnerList));
    }
}
