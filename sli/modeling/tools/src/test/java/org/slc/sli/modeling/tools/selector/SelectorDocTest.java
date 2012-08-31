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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slc.sli.modeling.uml.Model;
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

	private final String[] args = new String[]{"../../domain/src/main/resources/sliModel/SLI.xmi", "output.xml"};
	private final SelectorDoc selectorDoc = new SelectorDoc(args[0], args[1]);
	private final SelectorDoc spy = spy(selectorDoc);


    @Test
    public void testWriteBuffer() throws IOException {
        BufferedWriter writer = mock(BufferedWriter.class);
        when(spy.getBufferedWriter()).thenReturn(writer);
        assertTrue(spy.writeSelectorDocumentationToFile("foo"));
    }

	@Test
	public void testAppendClassTypeAttributes() {
		
		String attribute1Name = "foo";
		String attribute2Name = "bar";
		
		Identifier identifier = Identifier.random();
		
		Range range = new Range(Occurs.ONE, Occurs.ONE);
		Multiplicity multiplicity = new Multiplicity(range);
	    
		Attribute attribute1 = new Attribute(identifier, attribute1Name, identifier, multiplicity, new ArrayList<TaggedValue>());
		Attribute attribute2 = new Attribute(identifier, attribute2Name, identifier, multiplicity, new ArrayList<TaggedValue>());
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute1);
		attributes.add(attribute2);
		
		ClassType classType = new ClassType(identifier, "whatever", false, attributes, new ArrayList<TaggedValue>());
		
		StringBuffer stringBuffer = new StringBuffer();
		this.selectorDoc.appendClassTypeAttributes(stringBuffer, classType);
		
		String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name); 
	    String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
	    String expectedToString = part1 + part2;
		assertEquals(expectedToString, stringBuffer.toString());
	}

	@Test
	public void testAppendClassTypeAssociations() {
		
		
		List<AssociationEnd> associationEnds = new ArrayList<AssociationEnd>();
		
		ModelIndex modelIndex = mock(ModelIndex.class);
		when(modelIndex.getAssociationEnds(any(Identifier.class))).thenReturn(associationEnds);
		
		String attribute1Name = "foo";
		String attribute2Name = "bar";
		
		Identifier identifier = Identifier.random();
		
		Range range = new Range(Occurs.ONE, Occurs.ONE);
		Multiplicity multiplicity = new Multiplicity(range);
	    
		Attribute attribute1 = new Attribute(identifier, attribute1Name, identifier, multiplicity, new ArrayList<TaggedValue>());
		Attribute attribute2 = new Attribute(identifier, attribute2Name, identifier, multiplicity, new ArrayList<TaggedValue>());
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute1);
		attributes.add(attribute2);
		
		ClassType classType = new ClassType(identifier, "whatever", false, attributes, new ArrayList<TaggedValue>());
		
		StringBuffer stringBuffer = new StringBuffer();
		this.selectorDoc.appendClassTypeAttributes(stringBuffer, classType);
		
		String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name); 
	    String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
	    String expectedToString = part1 + part2;
		assertEquals(expectedToString, stringBuffer.toString());
	}
	
    @Test
    public void testGetModelIndex() throws FileNotFoundException {
        Model model = this.selectorDoc.readModel();
        assertNotNull("Expected non-null model", model);
    }


}
