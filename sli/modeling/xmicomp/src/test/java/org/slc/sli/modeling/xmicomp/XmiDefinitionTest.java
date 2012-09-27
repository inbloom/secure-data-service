package org.slc.sli.modeling.xmicomp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmiDefinitionTest {

	private final static String NAME = "NAME";
	private final static String VERSION = "VERSION";
	private final static String FILE = "FILE";
	
	private XmiDefinition xmiDefinition = new XmiDefinition(NAME, VERSION, FILE);
	
	@Test
	public void testGetName() {
		assertEquals(NAME, xmiDefinition.getName());
	}
	
	@Test
	public void testGetVersion() {
		assertEquals(VERSION, xmiDefinition.getVersion());
	}
	
	@Test
	public void testGetFile() {
		assertEquals(FILE, xmiDefinition.getFile());
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullParam1() {
		new XmiDefinition(null, VERSION, FILE);
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullParam2() {
		new XmiDefinition(NAME, null, FILE);
	}
	
	@Test (expected = NullPointerException.class)
	public void testNullParam3() {
		new XmiDefinition(NAME, VERSION, null);
	}
}
