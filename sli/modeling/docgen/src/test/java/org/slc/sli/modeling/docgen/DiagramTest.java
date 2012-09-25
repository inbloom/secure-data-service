package org.slc.sli.modeling.docgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DiagramTest {
	
	private static final String TITLE = "TITLE";
	private static final String SOURCE = "SOURCE";
	private static final String PROLOG = "PROLOGUE";
	private static final String EPILOG = "EPILOGUE";
	
	@Test
	public void test() {
		Diagram diagram = new Diagram(TITLE, SOURCE, PROLOG, EPILOG);
		
		assertEquals(TITLE, diagram.getTitle());
		assertEquals(SOURCE, diagram.getSource());
		assertEquals(PROLOG, diagram.getProlog());
		assertEquals(EPILOG, diagram.getEpilog());
	}

	@Test (expected = NullPointerException.class)
	public void testNullParam1() {
		new Diagram(null, SOURCE, PROLOG, EPILOG);
	}

	@Test (expected = NullPointerException.class)
	public void testNullParam2() {
		new Diagram(TITLE, null, PROLOG, EPILOG);
	}

	@Test (expected = NullPointerException.class)
	public void testNullParam3() {
		new Diagram(TITLE, SOURCE, null, EPILOG);
	}

	@Test (expected = NullPointerException.class)
	public void testNullParam4() {
		new Diagram(TITLE, SOURCE, PROLOG, null);
	}
	
	@Test
	public void testToString() {
		
		// just check not null
		assertNotNull(new Diagram(TITLE, SOURCE, PROLOG, EPILOG).toString());
		
		
		// if you want to test toString() expected output, reenable this:
		
		/*
		
		String expectedResult = "{title : \"TITLE\", source : \"SOURCE\", prolog : \"PROLOGUE\", epilog : \"EPILOGUE\"}";
		String receivedResult = new Diagram(TITLE, SOURCE, PROLOG, EPILOG).toString();
		
		assertEquals(expectedResult, receivedResult);
		
		*/
	}
}
