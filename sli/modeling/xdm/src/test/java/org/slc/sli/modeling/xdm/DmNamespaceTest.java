package org.slc.sli.modeling.xdm;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

public class DmNamespaceTest {
	
	@Test
	public void test() {
		String prefix = "foo";
		String namespace = "bar";
		
		DmNamespace dmNamespace = new DmNamespace(prefix, namespace);

		assertTrue(dmNamespace.getChildAxis().size() == 0);
		assertTrue(dmNamespace.getName().equals(new QName(prefix)));
		assertTrue(dmNamespace.getStringValue().equals(namespace));
	}

	@Test (expected = NullPointerException.class)
	public void testNullPrefixThrowsException() {
		new DmNamespace("foo", null);
	}

	@Test (expected = NullPointerException.class)
	public void testNullNamespaceThrowsException() {
		new DmNamespace(null, "bar");
	}
}
