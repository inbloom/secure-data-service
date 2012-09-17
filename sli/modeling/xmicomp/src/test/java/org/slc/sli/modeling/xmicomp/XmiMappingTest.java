package org.slc.sli.modeling.xmicomp;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class XmiMappingTest {

	private static final XmiMappingStatus xmiMappingStatus = XmiMappingStatus.UNKNOWN;
	private static final String TRACKING = "TRACKING";
	private static final String COMMENT = "COMMENT";
	private static final XmiFeature lhsFeature = new XmiFeature("foo", true, "bar", true);
	private static final XmiFeature rhsFeature = new XmiFeature("foo2", true, "bar2", true);
	private static final XmiMapping xmiMapping = new XmiMapping(lhsFeature, rhsFeature, xmiMappingStatus, TRACKING, COMMENT);

	
	@Test
	public void testCompareTo() {
		assertTrue(xmiMapping.compareTo(null) == 0);
		assertTrue(xmiMapping.compareTo(xmiMapping) == 0);
	}
	
	@Test
	public void testGetLhsFeature() {
		assertEquals(lhsFeature, xmiMapping.getLhsFeature());
	}
	
	@Test
	public void testGetRhsFeature() {
		assertEquals(rhsFeature, xmiMapping.getRhsFeature());
	}
	
	@Test
	public void testGetStatus() {
		assertEquals(xmiMappingStatus, xmiMapping.getStatus());
	}
	
	@Test
	public void testGetComment() {
		assertEquals(COMMENT, xmiMapping.getComment());
	}
	
	@Test
	public void testGetTracking() {
		assertEquals(TRACKING, xmiMapping.getTracking());
	}
	
	@Test
	public void testToString() {
		
		// test not null
		assertNotNull(xmiMapping.toString());
		
		//if interested in actual response, re-enable this:
		
		/*
		
		String expectedResult = "{lhs : {name : foo, exists : true, className : bar, classExists : true}, rhs : {name : foo2, exists : true, className : bar2, classExists : true}, status : UNKNOWN, comment : COMMENT}";
		String receivedResult = xmiMapping.toString();
		
		assertEquals(expectedResult, receivedResult);
		
		*/
	}
	
	@Test (expected = NullPointerException.class) 
	public void testNullParam1() {
		new XmiMapping(lhsFeature, rhsFeature, null, TRACKING, COMMENT);
	}
	
	@Test (expected = NullPointerException.class) 
	public void testNullParam2() {
		new XmiMapping(lhsFeature, rhsFeature, xmiMappingStatus, null, COMMENT);
	}
	
	@Test (expected = NullPointerException.class) 
	public void testNullParam3() {
		new XmiMapping(lhsFeature, rhsFeature, xmiMappingStatus, TRACKING, null);
	}
}
