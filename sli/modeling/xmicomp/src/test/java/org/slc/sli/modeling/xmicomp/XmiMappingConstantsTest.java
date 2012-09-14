package org.slc.sli.modeling.xmicomp;

import org.junit.Test;

public class XmiMappingConstantsTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new XmiMappingConstants();
	}
}
