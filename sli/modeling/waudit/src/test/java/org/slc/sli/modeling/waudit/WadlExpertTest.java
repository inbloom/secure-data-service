package org.slc.sli.modeling.waudit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class WadlExpertTest {

	@Test (expected = RuntimeException.class)
	public void testBogusConstructor() {
		new WadlExpert();
	}
	
	@Test
	public void testIsTemplateParam() {
		assertTrue(WadlExpert.isTemplateParam("{foo}"));
		assertTrue(WadlExpert.isTemplateParam("{}"));
		assertFalse(WadlExpert.isTemplateParam("{"));
		assertFalse(WadlExpert.isTemplateParam("}"));
		assertFalse(WadlExpert.isTemplateParam("}{"));
	}
	
	@Test
	public void testSplitBasedOnFwdSlash() {
		String string = "foo/bar/foo2/bar2";
		
		List<String> expectedResult = new ArrayList<String>();
		expectedResult.add("foo");
		expectedResult.add("bar");
		expectedResult.add("foo2");
		expectedResult.add("bar2");
		
		assertEquals(expectedResult, WadlExpert.splitBasedOnFwdSlash(string));
	}




}
