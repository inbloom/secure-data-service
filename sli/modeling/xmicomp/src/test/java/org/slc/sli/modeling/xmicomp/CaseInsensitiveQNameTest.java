package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Test;

public class CaseInsensitiveQNameTest {
	
	private CaseInsensitiveQName ciqn1 = new CaseInsensitiveQName("tYPe", "fEaTuRe");
	private CaseInsensitiveQName ciqn2 = new CaseInsensitiveQName("TypE", "FeAtUrE");
	
	@Test
	public void testCompare() {
		assertTrue(ciqn1.compareTo(ciqn2) == 0);
	}
	
	@Test
	public void testEquals() {
		assertTrue(ciqn1.equals(ciqn2));
		assertFalse(ciqn1.equals(new QName("type", "feature")));
	}
	
	@Test
	public void testGetLocalPart() {
		assertEquals(ciqn1.getLocalPart(), ciqn2.getLocalPart());
	}
	
	@Test
	public void testGetNamespaceUri() {
		assertEquals(ciqn1.getNamespaceURI(), ciqn2.getNamespaceURI());
	}
	
	@Test
	public void testHashCode() {
		assertEquals(ciqn1.hashCode(), ciqn2.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals(ciqn1.toString(), ciqn2.toString());
	}
}
