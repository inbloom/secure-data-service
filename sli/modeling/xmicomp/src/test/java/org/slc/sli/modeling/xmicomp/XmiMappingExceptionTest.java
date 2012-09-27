package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmiMappingExceptionTest {
	
	@Test
	public void test() {
		String message = "foo";
		
		XmiMappingException xmiMappingException = new XmiMappingException(message);
		
		assertEquals(message, xmiMappingException.getMessage());
	}
}
