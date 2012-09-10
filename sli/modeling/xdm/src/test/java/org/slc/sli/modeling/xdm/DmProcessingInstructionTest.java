package org.slc.sli.modeling.xdm;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

public class DmProcessingInstructionTest {
	
	@Test
	public void test() {
		String target = "foo";
		String data = "bar";
		
		DmProcessingInstruction dmProcessingInstruction = new DmProcessingInstruction(target, data);

		assertTrue(dmProcessingInstruction.getChildAxis().size() == 0);
		assertTrue(dmProcessingInstruction.getName().equals(new QName(target)));
		assertTrue(dmProcessingInstruction.getStringValue().equals(data));
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullPrefixThrowsException() {
		new DmProcessingInstruction("foo", null);
	}

	@Test (expected = NullPointerException.class)
	public void testNullNamespaceThrowsException() {
		new DmProcessingInstruction(null, "bar");
	}
}
