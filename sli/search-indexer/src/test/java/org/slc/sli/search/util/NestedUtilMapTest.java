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
package org.slc.sli.search.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class NestedUtilMapTest {
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFlatMap() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        
        NestedMapUtil.put(new DotPath("test.levl1.levl2"), 1, testMap);
        NestedMapUtil.put(new DotPath("test.levl1.name"), "my name", testMap);
        Map<String, Object> map = (Map<String, Object>) ((Map<String, Object>)testMap.get("test")).get("levl1");
        Assert.assertEquals(1, map.get("levl2"));
        Assert.assertEquals("my name", map.get("name"));
        
        Map<String, Object> flatMap = NestedMapUtil.toFlatMap(testMap);
        Assert.assertEquals(2, flatMap.size());
        Assert.assertEquals("my name", flatMap.get("test.levl1.name"));
        Assert.assertEquals(1, flatMap.get("test.levl1.levl2"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testArray() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        Map<String, Object> testMap1 = new HashMap<String, Object>();
        testMap1.put("id", 1111);
        testMap1.put("cond", "a");
        Map<String, Object> testMap2 = new HashMap<String, Object>();
        testMap2.put("id", "2xy");
        
        NestedMapUtil.put(new DotPath("test.levl1.levl2"), 1, testMap);
        NestedMapUtil.put(new DotPath("test.levl1.array"), Arrays.asList(testMap1, testMap2), testMap);
      
        Assert.assertEquals(1111, NestedMapUtil.get(new DotPath("test.levl1.array.$.id"), testMap));
    }
    
    @SuppressWarnings("unchecked")
    @Test()
    public void testRecursionOk() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        // 10 deep and 13 wide is ok
        NestedMapUtil.put(new DotPath("l0.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);        
        NestedMapUtil.put(new DotPath("l00.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l01.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l02.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l03.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l04.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l05.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l06.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l07.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l08.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l09.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l10.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        NestedMapUtil.put(new DotPath("l11.l1.l2.l3.l4.l5.l6.l7.l8.l9.l10"), 1, testMap);
        
        Map<String, Object> testMap9 = (Map<String, Object>) NestedMapUtil.get(new DotPath("l0.l1.l2.l3.l4.l5.l6.l7.l8.l9"), testMap);
        Assert.assertEquals(1, NestedMapUtil.get(new DotPath("l10"), testMap9));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRecursionTooDeep() {
        Map<String, Object> testMap = new HashMap<String, Object>();
        // 11 deep is too much
        NestedMapUtil.put(new DotPath("l0.ll1.ll2.ll3.ll4.ll5.ll6.ll7.ll8.ll9.ll10.ll11"), 1, testMap);
    }
}
