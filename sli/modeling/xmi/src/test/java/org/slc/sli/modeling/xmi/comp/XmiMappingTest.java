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

package org.slc.sli.modeling.xmi.comp;


import org.junit.Test;

import org.junit.Assert;

/**
 * JUnit test for XmiMapping class.
 */
public class XmiMappingTest {

    private static final XmiMappingStatus XMI_MAPPING_STATUS = XmiMappingStatus.UNKNOWN;
    private static final String TRACKING = "TRACKING";
    private static final String COMMENT = "COMMENT";
    private static final XmiFeature LHS_FEATURE = new XmiFeature("foo", true, "bar", true);
    private static final XmiFeature RHS_FEATURE = new XmiFeature("foo2", true, "bar2", true);
    private static final XmiMapping XMI_MAPPING = new XmiMapping(LHS_FEATURE, RHS_FEATURE, XMI_MAPPING_STATUS, TRACKING, COMMENT);


    @Test
    public void testCompareTo() {
        Assert.assertTrue(XMI_MAPPING.compareTo(null) == 0);
        Assert.assertTrue(XMI_MAPPING.compareTo(XMI_MAPPING) == 0);
    }

    @Test
    public void testGetLhsFeature() {
        Assert.assertEquals(LHS_FEATURE, XMI_MAPPING.getLhsFeature());
    }

    @Test
    public void testGetRhsFeature() {
        Assert.assertEquals(RHS_FEATURE, XMI_MAPPING.getRhsFeature());
    }

    @Test
    public void testGetStatus() {
        Assert.assertEquals(XMI_MAPPING_STATUS, XMI_MAPPING.getStatus());
    }

    @Test
    public void testGetComment() {
        Assert.assertEquals(COMMENT, XMI_MAPPING.getComment());
    }

    @Test
    public void testGetTracking() {
        Assert.assertEquals(TRACKING, XMI_MAPPING.getTracking());
    }

    @Test
    public void testToString() {

        // test not null
        Assert.assertNotNull(XMI_MAPPING.toString());

        //if interested in actual response, re-enable this:

        /*

          String expectedResult = "{lhs : {name : foo, exists : true, className : bar, classExists : true}, rhs : {name : foo2, exists : true, className : bar2, classExists : true}, status : UNKNOWN, comment : COMMENT}";
          String receivedResult = XMI_MAPPING.toString();

          assertEquals(expectedResult, receivedResult);

          */
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new XmiMapping(LHS_FEATURE, RHS_FEATURE, null, TRACKING, COMMENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new XmiMapping(LHS_FEATURE, RHS_FEATURE, XMI_MAPPING_STATUS, null, COMMENT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam3() {
        new XmiMapping(LHS_FEATURE, RHS_FEATURE, XMI_MAPPING_STATUS, TRACKING, null);
    }
}
