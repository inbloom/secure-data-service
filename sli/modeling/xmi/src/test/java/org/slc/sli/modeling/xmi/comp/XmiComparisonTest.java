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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 * JUnit test for XmiComparison class.
 */
public class XmiComparisonTest {

    private static final XmiFeature XMI_FEATURE1 = new XmiFeature("foo", true, "bar", true);
    private static final XmiFeature XMI_FEATURE2 = new XmiFeature("foo2", true, "bar2", true);

    private static final List<XmiMapping> XMI_MAPPINGS = new ArrayList<XmiMapping>();

    static {
        XMI_MAPPINGS.add(new XmiMapping(XMI_FEATURE1, XMI_FEATURE1, XmiMappingStatus.UNKNOWN, "tracking1", "comment1"));
        XMI_MAPPINGS.add(new XmiMapping(XMI_FEATURE2, XMI_FEATURE2, XmiMappingStatus.UNKNOWN, "tracking2", "comment2"));
    }

    private static final XmiDefinition LHS_XMI_DEFINITION = new XmiDefinition("leftName", "leftVersion", "leftFile");
    private static final XmiDefinition RHS_XMI_DEFINITION = new XmiDefinition("rightName", "rightVersion", "rightFile");
    private static final XmiComparison XMI_COMPARISON = new XmiComparison(LHS_XMI_DEFINITION, RHS_XMI_DEFINITION, XMI_MAPPINGS);

    @Test
    public void testGetLhsDef() {
        Assert.assertEquals(LHS_XMI_DEFINITION, XMI_COMPARISON.getLhsDef());
    }

    @Test
    public void testGetRhsDef() {
        Assert.assertEquals(RHS_XMI_DEFINITION, XMI_COMPARISON.getRhsDef());
    }

    @Test
    public void testGetMappings() {
        Assert.assertEquals(XMI_MAPPINGS, XMI_COMPARISON.getMappings());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        new XmiComparison(null, RHS_XMI_DEFINITION, XMI_MAPPINGS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam2() {
        new XmiComparison(LHS_XMI_DEFINITION, null, XMI_MAPPINGS);
    }

    @Test(expected = NullPointerException.class)
    public void testNullParam3() {
        new XmiComparison(LHS_XMI_DEFINITION, RHS_XMI_DEFINITION, null);
    }

}
