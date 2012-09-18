package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmiMappingStatusTest {

	@Test
	public void test() {
		for (XmiMappingStatus xmiMappingStatus : XmiMappingStatus.values()) {
			assertEquals(xmiMappingStatus, XmiMappingStatus.valueOf(xmiMappingStatus.toString()));
		}
	}
}
