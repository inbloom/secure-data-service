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

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;

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
		
		Attribute attribute1 = mock(Attribute.class);
		when(attribute1.getName()).thenReturn(attribute1Name);
		Attribute attribute2 = mock(Attribute.class);
		when(attribute2.getName()).thenReturn(attribute2Name);
		
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(attribute1);
		attributes.add(attribute2);
		
		ClassType classType = mock(ClassType.class);
		when(classType.getAttributes()).thenReturn(attributes);
		
		StringBuffer stringBuffer = new StringBuffer();
		this.selectorDoc.appendClassTypeAttributes(stringBuffer, classType);
		
		String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name); 
	    String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
	    String expectedToString = part1 + part2;
		assertEquals(expectedToString, stringBuffer.toString());
	}

}
