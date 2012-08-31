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



package org.slc.sli.modeling.tools.selector;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

/**
 * JUnit test for SelectorDoc class.
 *
 * @author wscott
 *
 */
public class SelectorDocTest {

	private final static String[] args = new String[]{"../../domain/src/main/resources/sliModel/SLI.xmi", "output.xml"};
	private final static SelectorDoc selectorDoc = new SelectorDoc(args[0], args[1]);
	private final static SelectorDoc spy = spy(selectorDoc);
	private final static Range range = new Range(Occurs.ONE, Occurs.ONE);
	private final static Multiplicity multiplicity = new Multiplicity(range);
	private final static Identifier identifier = Identifier.random();

	private final static String attribute1Name = "attributeFoo";
	private final static String attribute2Name = "attributeBar";
	
	private final static String associationEnd1Name = "associationFoo";
	private final static String associationEnd2Name = "associationBar";
	
	private final static String classTypeName = "classFoo";

	private final static AssociationEnd associationEnd1 = new AssociationEnd(multiplicity, associationEnd1Name, false, identifier, "whatever");
	private final static AssociationEnd associationEnd2 = new AssociationEnd(multiplicity, associationEnd2Name, false, identifier, "whatever");
	
	private final static List<AssociationEnd> associationEnds = new ArrayList<AssociationEnd>();
	static {
		associationEnds.add(associationEnd1);
		associationEnds.add(associationEnd2);
	}
	
	
	private final static Attribute attribute1 = new Attribute(identifier, attribute1Name, identifier, multiplicity, new ArrayList<TaggedValue>());
	private final static Attribute attribute2 = new Attribute(identifier, attribute2Name, identifier, multiplicity, new ArrayList<TaggedValue>());
	
	private final static List<Attribute> attributes = new ArrayList<Attribute>();
	static {
		attributes.add(attribute1);
		attributes.add(attribute2);
	}
	
	private final static ClassType classType = new ClassType(identifier, classTypeName, false, attributes, new ArrayList<TaggedValue>());
	
	private final static Map<String, ClassType> classTypes = new HashMap<String, ClassType>();
	static {
		classTypes.put("", classType);
	}

	private final static ModelIndex modelIndex = mock(ModelIndex.class);
	static {
		when(modelIndex.getAssociationEnds(any(Identifier.class))).thenReturn(SelectorDocTest.associationEnds);
		when(modelIndex.getClassTypes()).thenReturn(SelectorDocTest.classTypes);
	}
	
	private final StringBuffer stringBuffer = new StringBuffer();
	
    @Test
    public void testWriteBuffer() throws IOException {
        BufferedWriter writer = mock(BufferedWriter.class);
        when(spy.getBufferedWriter()).thenReturn(writer);
        assertTrue(spy.writeSelectorDocumentationToFile("foo"));
    }

	@Test
	public void testAppendClassTypeAttributes() {
		
		SelectorDocTest.selectorDoc.appendClassTypeAttributes(stringBuffer, classType);
		
		String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name); 
	    String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
	    String expectedToString = part1 + part2;
		assertEquals(expectedToString, stringBuffer.toString());
	}

	@Test
	public void testAppendClassTypeAssociations() {
		
		SelectorDocTest.selectorDoc.appendClassTypeAssociations(stringBuffer, classType, modelIndex);
		
		String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd1Name); 
	    String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd2Name);
	    String expectedToString = part1 + part2;
		assertEquals(expectedToString, stringBuffer.toString());
	}

	@Test
	public void testGetSelectorDocumentation() {
		
		String receivedResult = SelectorDocTest.selectorDoc.getSelectorDocumentation(modelIndex);

		String part1 = String.format(SelectorDoc.SIMPLE_SECT_START, classTypeName) + SelectorDoc.FEATURES_START;
		String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name); 
	    String part3 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
		String part4 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd1Name); 
	    String part5 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd2Name);
	    String part6 = SelectorDoc.FEATURES_END + SelectorDoc.SIMPLE_SECT_END;
	    String expectedToString = part1 + part2 + part3 + part4 + part5 + part6;
		assertEquals(expectedToString, receivedResult);
	}
	
	

}
