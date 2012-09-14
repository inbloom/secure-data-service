package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CaseInsensitiveStringTest {
	private static final String string1 = "i am a string";
	private static final String string2 = "I aM a StRiNg";
	
	private static final CaseInsensitiveString cis1 = new CaseInsensitiveString(string1);
	private static final CaseInsensitiveString cis2 = new CaseInsensitiveString(string2);

	@Test
	public void testCompareTo() {
		assertFalse(string1.compareTo(string2) == 0);
		assertTrue(cis1.compareTo(cis2) == 0);
	}
	
	@Test
	public void testEquals() {
		assertFalse(cis1.equals(string1));
		assertTrue(cis1.equals(new CaseInsensitiveString(string2)));
	}
	
	@Test
	public void testHashCode() {
		assertTrue(cis1.hashCode() == cis2.hashCode());
	}
	
	@Test
	public void testToString() {
		assertEquals(cis1.toString(), cis2.toString());
	}
}
