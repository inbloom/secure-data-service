/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.modeling.waudit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.modeling.psm.PsmCollection;
import org.slc.sli.modeling.psm.PsmDocument;
import org.slc.sli.modeling.psm.PsmResource;
import org.slc.sli.modeling.rest.Documentation;
import org.slc.sli.modeling.rest.Method;
import org.slc.sli.modeling.rest.Param;
import org.slc.sli.modeling.rest.Resource;
import org.slc.sli.modeling.rest.Response;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.HasName;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.Visitor;
import org.slc.sli.modeling.uml.index.ModelIndex;

public class WadlExpertTest {

	private final String PREFIX = "prefix";
	private final String NAMESPACE = "namespaceURI";
	
	private ModelIndex modelIndex;
	private WadlAuditConfig config;
	private Map<String, PsmDocument<Type>> topLevel;
	private Map<String, QName> elementNames;
	
	private Stack<Resource> ancestors;
	private Resource resource;
	

	@Before
	public void setup() {
		
		this.topLevel = new HashMap<String, PsmDocument<Type>>();
		this.elementNames = new HashMap<String, QName>();
		this.elementNames.put("Home", new QName(NAMESPACE, "home", PREFIX));
		this.elementNames.put("Custom", new QName(NAMESPACE, "custom", PREFIX));
		
		this.modelIndex = this.createModelIndex(topLevel);
		
		this.config = new WadlAuditConfig(PREFIX, NAMESPACE, modelIndex, this.elementNames);

		this.ancestors = new Stack<Resource>();
		this.ancestors.push(this.createResource("v1"));
		this.ancestors.push(this.createResource("foo"));
		
		this.resource = this.createResource("{id}");
		
	}
	
	
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
		
		List<String> expectedResult = new ArrayList<String>();
		expectedResult.add("v1");
		expectedResult.add("foo");
		expectedResult.add("{id}");
		
		List<String> receivedResult = WadlExpert.toSteps(resource, ancestors);
		
		assertEquals(expectedResult, receivedResult);
		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetNamesWithNullNames() {
		WadlExpert.getNames(null);
	}
	
	@Test
	public void testGetNames() {
		
		//list of strings
		List<String> names = new ArrayList<String>();
		names.add("a");
		names.add("b");
		names.add("c");
		names.add("d");
		names.add("e");
		
		//make little objects out of the strings
		List<HasName> namedElements = new ArrayList<HasName>();
		for (final String name : names) {
			namedElements.add(new HasName() {
				@Override
				public String getName() {
					return name;
				}
			});
		}
		
		//run method
		List<String> expectedResponse = names;
		List<String> receivedResponse = WadlExpert.getNames(namedElements);
		
		//confirm results
		assertEquals(expectedResponse, receivedResponse);
	}
	
	private Type createMockType(String name) {
		Type type = mock(Type.class);
		when(type.getName()).thenReturn(name);
		return type;
	}

	private AssociationEnd createAssociationEnd(String name, Identifier identifier) {
		
		Range range = new Range(Occurs.ONE, Occurs.ONE);
		
		Multiplicity multiplicity = new Multiplicity(range);
		
		return new AssociationEnd(multiplicity, name, true, identifier, "associatedAttributeName");
	}
	
	private void setupType(String name, Map<String, PsmDocument<Type>> topLevel, List<AssociationEnd> associationEnds, Map<Identifier, Type> types) {
		Identifier identifier = Identifier.random();
		Type type = this.createMockType(name);
		
		topLevel.put(name, new PsmDocument<Type>(type, new PsmResource("graphResourceName"), new PsmCollection("singularResourceName")));
		associationEnds.add(this.createAssociationEnd(name, identifier));
		types.put(identifier, type);
		elementNames.put(name, new QName(NAMESPACE, name, PREFIX));
	}
	
	private ModelIndex createModelIndex(Map<String, PsmDocument<Type>> topLevel) {
		final List<AssociationEnd> associationEnds = new ArrayList<AssociationEnd>();
		final Map<Identifier, Type> types = new HashMap<Identifier, Type>();

		this.setupType("foo", topLevel, associationEnds, types);
		this.setupType("bar", topLevel, associationEnds, types);
		this.setupType("foo2", topLevel, associationEnds, types);
		this.setupType("bar2", topLevel, associationEnds, types);

		return new ModelIndex() {

			@Override
			public List<AssociationEnd> getAssociationEnds(Identifier type) {
				return associationEnds;
			}

			@Override
			public Map<String, ClassType> getClassTypes() {
				return null;
			}

			@Override
			public Map<QName, DataType> getDataTypes() {
				return null;
			}

			@Override
			public Iterable<EnumType> getEnumTypes() {
				return null;
			}

			@Override
			public List<Generalization> getGeneralizationBase(Identifier derived) {
				return null;
			}

			@Override
			public List<Generalization> getGeneralizationDerived(Identifier base) {
				return null;
			}

			@Override
			public String getNamespaceURI(Type type) {
				return null;
			}

			@Override
			public TagDefinition getTagDefinition(Identifier reference) {
				return null;
			}

			@Override
			public TagDefinition getTagDefinition(QName name) {
				return null;
			}

			@Override
			public Type getType(Identifier reference) {
				return types.get(reference);
			}

			@Override
			public void lookup(Identifier id, Visitor visitor) {
				
			}

			@Override
			@Deprecated
			public Set<ModelElement> lookupByName(QName name) {
				return null;
			}

			@Override
			public Set<ModelElement> whereUsed(Identifier id) {
				return null;
			}
			
		};
	}
	
	@Test
	public void testComputeType() {
		
		//map inputs to expected outputs
		Map<String, String> typesToCompute = new HashMap<String, String>();
		typesToCompute.put("v1/home", "Home");
		typesToCompute.put("v1/foo/{id}/bar", "bar");
		typesToCompute.put("v1/foo/{id}/bar/foo2", "foo2");
		typesToCompute.put("v1/foo/{id}/bar/custom", "Custom");
		typesToCompute.put("v1/foo/{id}/bar/aggregations", "Aggregations");
		typesToCompute.put("v1/foo/{id}/bar/calculatedValues", "CalculatedValues");
		typesToCompute.put("v1/foo/{id}/bar/createCheck", "Unknown");
		typesToCompute.put("v1/foo/{id}/bar/createWaitingListUser", "Unknown");
		typesToCompute.put("v1/foo/{id}/bar/studentWithGrade", "Unknown");
		
		for (Entry<String, String> entry : typesToCompute.entrySet()) {
			assertEquals(new QName(NAMESPACE, entry.getValue(), PREFIX), 
					WadlExpert.computeType(WadlExpert.splitBasedOnFwdSlash(entry.getKey()), config, topLevel));
		}
	}
	
	@Test (expected = EmptyStackException.class)
	public void testComputeTypeOnInvalidPath() {
		WadlExpert.computeType(WadlExpert.splitBasedOnFwdSlash("v2/invalidString"), config, topLevel);
	}
	
	@Test
	public void testComputeElementNameForRequestNoops() {
		this.assertComputeElementNameForRequestIsNull(Method.NAME_HTTP_GET);
		this.assertComputeElementNameForRequestIsNull(Method.NAME_HTTP_DELETE);
	}

	private void assertComputeElementNameForRequestIsNull(String httpMethodString) {
		Method method = new Method("id", httpMethodString, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		assertNull(WadlExpert.computeElementNameForRequest(null, null, method, null, null, null, null));
	}

	@Test
	public void testComputeElementNameForRequest() {
		
		Method putMethod = new Method("id", Method.NAME_HTTP_PUT, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		QName expectedResult = new QName(NAMESPACE, "foo", PREFIX);
		QName receivedResult = WadlExpert.computeElementNameForRequest(null, null, putMethod, resource, ancestors, config, topLevel);
		
		assertEquals(expectedResult, receivedResult);
		
	}

	@Test (expected = RuntimeException.class)
	public void testComputeElementNameForRequestUnknownElement() {
		
		Method putMethod = new Method("id", Method.NAME_HTTP_PUT, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		this.elementNames.remove("foo");

		this.config = new WadlAuditConfig(PREFIX, NAMESPACE, modelIndex, this.elementNames);
		WadlExpert.computeElementNameForRequest(null, null, putMethod, resource, ancestors, config, topLevel);
		
	}
	
	@Test
	public void testComputeElementNameForResponseNoops() {
		
		this.assertComputeElementNameForResponseIsNull(Method.NAME_HTTP_POST);
		this.assertComputeElementNameForResponseIsNull(Method.NAME_HTTP_PUT);
		this.assertComputeElementNameForResponseIsNull(Method.NAME_HTTP_PATCH);
		this.assertComputeElementNameForResponseIsNull(Method.NAME_HTTP_DELETE);
	}
	
	private void assertComputeElementNameForResponseIsNull(String httpMethodString) {
		Method method = new Method("id", httpMethodString, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		assertNull(WadlExpert.computeElementNameForResponse(null, null, method, null, null, null, null));
	}
	

	@Test
	public void testComputeElementNameForResponse() {
		
		Method getMethod = new Method("id", Method.NAME_HTTP_GET, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		QName expectedResult = new QName(NAMESPACE, "fooList", PREFIX);
		QName receivedResult = WadlExpert.computeElementNameForResponse(null, null, getMethod, resource, ancestors, config, topLevel);
		
		assertEquals(expectedResult, receivedResult);
		
	}

	@Test
	public void testComputeElementNameForResponseHome() {
		this.ancestors.clear();
		this.ancestors.push(this.createResource("v1"));
		this.resource = this.createResource("home");
		
		Method getMethod = new Method("id", Method.NAME_HTTP_GET, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		QName expectedResult = new QName(NAMESPACE, "home", PREFIX);
		QName receivedResult = WadlExpert.computeElementNameForResponse(null, null, getMethod, resource, ancestors, config, topLevel);
		
		assertEquals(expectedResult, receivedResult);
		
	}

	@Test
	public void testComputeElementNameForResponseCustom() {
		
		this.ancestors.push(this.resource);
		this.resource = this.createResource("custom");
		
		Method getMethod = new Method("id", Method.NAME_HTTP_GET, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		QName expectedResult = new QName(NAMESPACE, "custom", PREFIX);
		QName receivedResult = WadlExpert.computeElementNameForResponse(null, null, getMethod, resource, ancestors, config, topLevel);
		
		assertEquals(expectedResult, receivedResult);
		
	}
	

	@Test
	public void testComputeElementNameForResponseWithNullQName() {
		
		this.elementNames.put("foo", null);

		this.config = new WadlAuditConfig(PREFIX, NAMESPACE, modelIndex, this.elementNames);
		
		Method getMethod = new Method("id", Method.NAME_HTTP_GET, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		assertNull(WadlExpert.computeElementNameForResponse(null, null, getMethod, resource, ancestors, config, topLevel));
		
	}

	@Test (expected = RuntimeException.class)
	public void testComputeElementNameForResponseWithMissingQName() {
		
		this.elementNames.remove("foo");

		this.config = new WadlAuditConfig(PREFIX, NAMESPACE, modelIndex, this.elementNames);
		
		Method getMethod = new Method("id", Method.NAME_HTTP_GET, new ArrayList<Documentation>(), null, new ArrayList<Response>());
		WadlExpert.computeElementNameForResponse(null, null, getMethod, resource, ancestors, config, topLevel);
		
	}


}
