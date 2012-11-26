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

package org.slc.sli.modeling.jgen;

import org.junit.Before;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit for ClassTypeHelper
 * @author chung
 */
public class ClassTypeHelperTest {

    private ModelIndex modelIndex;
    private ClassType classType;

    @Before
    public void setup() {
        Multiplicity oneToOne = new Multiplicity(new Range(Occurs.ONE, Occurs.ONE));
        final Attribute attr1 = new Attribute(Identifier.random(), "Attr1", Identifier.fromString("Attr1Type"),
                oneToOne, new ArrayList<TaggedValue>());
        final Attribute attr2 = new Attribute(Identifier.random(), "Attr2", Identifier.fromString("Attr2Type"),
                oneToOne, new ArrayList<TaggedValue>());
        classType = new ClassType(
                Identifier.random(),
                "TestClassType",
                false,
                new ArrayList<Attribute>() {
                    {
                        add(attr1);
                        add(attr2);
                    }
                },
                new ArrayList<TaggedValue>());

        final AssociationEnd assocEnd1 = new AssociationEnd(oneToOne, "AssocEnd1", false, Identifier.random(), "foo");
        final AssociationEnd assocEnd2 = new AssociationEnd(oneToOne, "AssocEnd2", false, Identifier.random(), "bar");
        List<AssociationEnd> associationEnds = new ArrayList<AssociationEnd>() {
            {
                add(assocEnd1);
                add(assocEnd2);
            }
        };
        modelIndex = mock(ModelIndex.class);
        when(modelIndex.getAssociationEnds(any(Identifier.class))).thenReturn(associationEnds);
        when(modelIndex.getType(any(Identifier.class))).thenReturn(classType);
    }

    @Test
    public void testCreateObject() {
        @SuppressWarnings("unused")
        ClassTypeHelper helper = new ClassTypeHelper();
    }

    @Test
    public void testWriteClassType() throws IOException {
        String packageName = "test.pack";
        List<String> importNames = Arrays.asList(new String[] {"test.import1", "test.import2"});
        File outFile = new File("TestClassType.java");
        JavaGenConfig javaGenConfig = new JavaGenConfig(true);

        ClassTypeHelper.writeClassType(packageName, importNames, classType, modelIndex, outFile, javaGenConfig);

        FileReader reader = new FileReader(new File(outFile.getAbsolutePath()));
        char[] chars = new char[4096];
        reader.read(chars);
        String content = new String(chars);

        assertTrue(content.contains("package test.pack;"));
        assertTrue(content.contains("import test.import1;"));
        assertTrue(content.contains("import test.import2;"));
        assertTrue(content.contains("private final TestClassType attr1"));
        assertTrue(content.contains("private final TestClassType attr2"));
        assertTrue(content.contains("public TestClassType getAttr1()"));
        assertTrue(content.contains("public TestClassType getAttr2()"));

        outFile.deleteOnExit();
    }

}
