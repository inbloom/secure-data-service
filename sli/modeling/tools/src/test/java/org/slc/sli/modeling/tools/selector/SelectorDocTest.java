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


import org.junit.Test;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit test for SelectorDoc class.
 *
 * @author wscott
 */
public class SelectorDocTest {

    private final static String INPUT_FILENAME = "src/test/resources/SLI.xmi";
    private final static String OUTPUT_FILENAME = "output.xml";

    private final static String[] args = new String[]{INPUT_FILENAME, OUTPUT_FILENAME};
    private SelectorDoc selectorDoc = new SelectorDoc(args[0], args[1]);
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
    public void testMain() throws IOException {
        SelectorDoc.main(args);

        File file = new File(OUTPUT_FILENAME);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testWriteBuffer() throws IOException {
        Writer writer = mock(Writer.class);
        assertTrue(this.selectorDoc.writeSelectorDocumentationToFile("foo", writer));
    }

    @Test
    public void testAppendClassTypeAttributes() {

        this.selectorDoc.appendClassTypeAttributes(stringBuffer, classType);

        String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name);
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
        String expectedToString = part1 + part2;
        assertEquals(expectedToString, stringBuffer.toString());
    }

    @Test
    public void testAppendClassTypeAssociations() {

        this.selectorDoc.appendClassTypeAssociations(stringBuffer, classType, modelIndex);

        String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd1Name);
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd2Name);
        String expectedToString = part1 + part2;
        assertEquals(expectedToString, stringBuffer.toString());
    }

    @Test
    public void testGetSelectorDocumentation() {

        String receivedResult = this.selectorDoc.getSelectorDocumentation(modelIndex);

        String part1 = String.format(SelectorDoc.SIMPLE_SECT_START, classTypeName) + SelectorDoc.FEATURES_START;
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute1Name);
        String part3 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, attribute2Name);
        String part4 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd1Name);
        String part5 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, associationEnd2Name);
        String part6 = SelectorDoc.FEATURES_END + SelectorDoc.SIMPLE_SECT_END;
        String expectedToString = part1 + part2 + part3 + part4 + part5 + part6;
        assertEquals(expectedToString, receivedResult);
    }

    @Test
    public void testGetModelIndex() throws FileNotFoundException {
        Model model = this.selectorDoc.readModel();
        ModelIndex modelIndex = this.selectorDoc.getModelIndex(model);
        assertNotNull("Expected non-null modelIndex", modelIndex);
    }


}
