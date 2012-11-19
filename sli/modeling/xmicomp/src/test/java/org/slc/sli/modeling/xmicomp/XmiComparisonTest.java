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

package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class XmiComparisonTest {

	private static XmiFeature xmiFeature1 = new XmiFeature("foo", true, "bar", true);
	private static XmiFeature xmiFeature2 = new XmiFeature("foo2", true, "bar2", true);
	
	private static final List<XmiMapping> xmiMappings = new ArrayList<XmiMapping>();
	static {
		xmiMappings.add(new XmiMapping(xmiFeature1, xmiFeature1, XmiMappingStatus.UNKNOWN, "tracking1", "comment1"));
		xmiMappings.add(new XmiMapping(xmiFeature2, xmiFeature2, XmiMappingStatus.UNKNOWN, "tracking2", "comment2"));
	}
	private static final XmiDefinition lhsXmiDefinition = new XmiDefinition("leftName", "leftVersion", "leftFile");
	private static final XmiDefinition rhsXmiDefinition = new XmiDefinition("rightName", "rightVersion", "rightFile");
	private static final XmiComparison xmiComparison = new XmiComparison(lhsXmiDefinition, rhsXmiDefinition, xmiMappings);
	
	@Test
	public void testGetLhsDef() {
		assertEquals(lhsXmiDefinition, xmiComparison.getLhsDef());
	}
	
	@Test
	public void testGetRhsDef() {
		assertEquals(rhsXmiDefinition, xmiComparison.getRhsDef());
	}
	
	@Test
	public void testGetMappings() {
		assertEquals(xmiMappings, xmiComparison.getMappings());
	}

    @Test (expected = IllegalArgumentException.class)
    public void testNullParam1() {
    	new XmiComparison(null, rhsXmiDefinition, xmiMappings);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testNullParam2() {
    	new XmiComparison(lhsXmiDefinition, null, xmiMappings);
    }

    @Test (expected = NullPointerException.class)
    public void testNullParam3() {
    	new XmiComparison(lhsXmiDefinition, rhsXmiDefinition, null);
    }

}
