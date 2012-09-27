package org.slc.sli.modeling.xmicomp;

import org.junit.Test;

public class XmiMappingValuesTest {

	@Test (expected = UnsupportedOperationException.class)
	public void testNonInstantiable() {
		new XmiMappingValues();
	}
}
