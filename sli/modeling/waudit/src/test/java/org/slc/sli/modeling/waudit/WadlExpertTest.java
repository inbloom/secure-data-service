package org.slc.sli.modeling.waudit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.junit.Test;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Resource;

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
	
	private Resource createResource(String path) {
		String id = "";
		List<String> type = new ArrayList<String>();
		String queryType = "";
		List<Documentation> doc = new ArrayList<Documentation>();
		List<Param> params = new ArrayList<Param>();
		List<Method> methods = new ArrayList<Method>();
		List<Resource> resources = new ArrayList<Resource>();
		String resourceClass = "";
		
		return new Resource(id, type, queryType, path, doc, params, methods, resources, resourceClass);
	}
	
	@Test
	public void testToSteps() {
		
		Stack<Resource> ancestors = new Stack<Resource>();
		ancestors.push(this.createResource("a/b/c"));
		ancestors.push(this.createResource("d/e/f"));
		
		Resource resource = this.createResource("g/h/i");
		
		List<String> receivedResult = WadlExpert.toSteps(resource, ancestors);
		
		List<String> expectedResult = new ArrayList<String>();
		expectedResult.add("a");
		expectedResult.add("b");
		expectedResult.add("c");
		expectedResult.add("d");
		expectedResult.add("e");
		expectedResult.add("f");
		expectedResult.add("g");
		expectedResult.add("h");
		expectedResult.add("i");
		
		assertEquals(expectedResult, receivedResult);
		
	}



}
