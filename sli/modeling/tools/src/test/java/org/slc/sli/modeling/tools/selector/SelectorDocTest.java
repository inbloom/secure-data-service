/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

/**
 * JUnit test for SelectorDoc class.
 *
 * @author wscott
 */
public class SelectorDocTest {

    private static final String INPUT_FILENAME = "src/test/resources/SLI.xmi";
    private static final String OUTPUT_FILENAME = "output.xml";

    private static final String[] ARGS = new String[]{INPUT_FILENAME, OUTPUT_FILENAME};
    private SelectorDoc selectorDoc = new SelectorDoc(ARGS[0], ARGS[1]);
    private static final Range RANGE = new Range(Occurs.ONE, Occurs.ONE);
    private static final Multiplicity MULTIPLICITY = new Multiplicity(RANGE);
    private static final Identifier IDENTIFIER = Identifier.random();

    private static final String ATTRIBUTE1_NAME = "attributeFoo";
    private static final String ATTRIBUTE2_NAME = "attributeBar";

    private static final String ASSOCIATION_END1_NAME = "associationFoo";
    private static final String ASSOCIATION_END2_NAME = "associationBar";

    private static final String CLASS_TYPE_NAME = "classFoo";

    private static final AssociationEnd ASSOCIATION_END1 = new AssociationEnd(MULTIPLICITY, ASSOCIATION_END1_NAME, false, IDENTIFIER, "whatever");
    private static final AssociationEnd ASSOCIATION_END2 = new AssociationEnd(MULTIPLICITY, ASSOCIATION_END2_NAME, false, IDENTIFIER, "whatever");

    private static final List<AssociationEnd> ASSOCIATION_ENDS = new ArrayList<AssociationEnd>();

    static {
        ASSOCIATION_ENDS.add(ASSOCIATION_END1);
        ASSOCIATION_ENDS.add(ASSOCIATION_END2);
    }

    private static final Attribute ATTRIBUTE1 = new Attribute(IDENTIFIER, ATTRIBUTE1_NAME, IDENTIFIER, MULTIPLICITY, new ArrayList<TaggedValue>());
    private static final Attribute ATTRIBUTE2 = new Attribute(IDENTIFIER, ATTRIBUTE2_NAME, IDENTIFIER, MULTIPLICITY, new ArrayList<TaggedValue>());

    private static final List<Attribute> ATTRIBUTES = new ArrayList<Attribute>();

    static {
        ATTRIBUTES.add(ATTRIBUTE1);
        ATTRIBUTES.add(ATTRIBUTE2);
    }

    private static final ClassType CLASS_TYPE = new ClassType(IDENTIFIER, CLASS_TYPE_NAME, false, ATTRIBUTES, new ArrayList<TaggedValue>());

    private static final Map<String, ClassType> CLASS_TYPES = new HashMap<String, ClassType>();

    static {
        CLASS_TYPES.put("", CLASS_TYPE);
    }

    private static final ModelIndex MODEL_INDEX = mock(ModelIndex.class);

    static {
        when(MODEL_INDEX.getAssociationEnds(any(Identifier.class))).thenReturn(SelectorDocTest.ASSOCIATION_ENDS);
        when(MODEL_INDEX.getClassTypes()).thenReturn(SelectorDocTest.CLASS_TYPES);
    }

    private final StringBuffer stringBuffer = new StringBuffer();

    @Test
    public void testMain() throws IOException {
        SelectorDoc.main(ARGS);

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

        this.selectorDoc.appendClassTypeAttributes(stringBuffer, CLASS_TYPE);

        String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, ATTRIBUTE1_NAME);
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, ATTRIBUTE2_NAME);
        String expectedToString = part1 + part2;
        assertEquals(expectedToString, stringBuffer.toString());
    }

    @Test
    public void testAppendClassTypeAssociations() {

        this.selectorDoc.appendClassTypeAssociations(stringBuffer, CLASS_TYPE, MODEL_INDEX);

        String part1 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, ASSOCIATION_END1_NAME);
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, ASSOCIATION_END2_NAME);
        String expectedToString = part1 + part2;
        assertEquals(expectedToString, stringBuffer.toString());
    }

    @Test
    public void testGetSelectorDocumentation() {

        String receivedResult = this.selectorDoc.getSelectorDocumentation(MODEL_INDEX);

        String part1 = String.format(SelectorDoc.SIMPLE_SECT_START, CLASS_TYPE_NAME) + SelectorDoc.FEATURES_START;
        String part2 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, ATTRIBUTE1_NAME);
        String part3 = String.format(SelectorDoc.FEATURE, SelectorDoc.ATTRIBUTE, ATTRIBUTE2_NAME);
        String part4 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, ASSOCIATION_END1_NAME);
        String part5 = String.format(SelectorDoc.FEATURE, SelectorDoc.ASSOCIATION, ASSOCIATION_END2_NAME);
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
