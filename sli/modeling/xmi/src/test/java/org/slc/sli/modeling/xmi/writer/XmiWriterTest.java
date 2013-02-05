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

package org.slc.sli.modeling.xmi.writer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumLiteral;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

/**
 * JUnit test for XmiWriter class.
 *
 * @author wscott
 */
public class XmiWriterTest {
    private static final Identifier MODEL_ID = Identifier.random();
    private static final String MODEL_NAME = "unitTestModelName";
    private static final Identifier TAGDEFINITION_ID = Identifier.random();
    private static final String TAGDEFINITION_NAME = "unitTestTagDefinitionName";
    private static final String TAGGEDVALUE_VALUE = "unitTestTaggedValueValue";
    private static final Identifier PACKAGE_ID = Identifier.random();
    private static final String PACKAGE_NAME = "unitTestUMLPackageName";
    private static final Identifier DATATYPE_ID = Identifier.random();
    private static final String DATATYPE_NAME = "unitTestDataTypeName";
    private static final Multiplicity MULTIPLICITY_ZEROTOONE = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
    private static final List<TaggedValue> EMPTY_TAGGEDVALUES = new ArrayList<TaggedValue>(0);
    private static final Identifier ATTRIBUTE_ID = Identifier.random();
    private static final String ATTRIBUTE_NAME = "unitTestAttributeName";
    private static final Identifier CLASSTYPE_ID = Identifier.random();
    private static final String CLASSTYPE_NAME = "unitTestClassTypeName";
    private static final Identifier ENUMLITERALA_ID = Identifier.random();
    private static final Identifier ENUMLITERALB_ID = Identifier.random();
    private static final String ENUMLITERALB_NAME = "unitTestEnumLiteralBName";
    private static final String ENUMLITERALA_NAME = "unitTestEnumLiteralAName";
    private static final Identifier ENUMTYPE_ID = Identifier.random();
    private static final String ENUMTYPE_NAME = "unitTestEnumName";
    private static final String ASSOCIATIONEND_PARENT_NAME = "unitTestParentAssociationEnd";
    private static final Identifier ASSOCIATIONEND_PARENT_ID = Identifier.random();
    private static final String ASSOCIATIONEND_PARENT_ATTR = "classTypeAssocParent";
    private static final String ASSOCIATIONEND_CHILD_NAME = "unitTestChildAssociationEnd";
    private static final Identifier ASSOCIATIONEND_CHILD_ID = Identifier.random();
    private static final String ASSOCIATIONEND_CHILD_ATTR = "classTypeAssocChild";
    private static final Identifier ASSOCIATION_ID = Identifier.random();
    private static final String ASSOCIATION_NAME = "unitTestAssociationClassType";
    private static final String GENERALIZATION_NAME = "unitTestGeneralizationName";
    private static final Identifier GENERALIZATION_ID = Identifier.random();

    private Model model;
    private ModelIndex modelIndex;

    @Before
    public void setUp() throws Exception {
        TaggedValue taggedValue = new TaggedValue(TAGGEDVALUE_VALUE, TAGDEFINITION_ID);

        List<TaggedValue> taggedValues = new ArrayList<TaggedValue>(1);
        taggedValues.add(taggedValue);

        TagDefinition tagDefinition = new TagDefinition(TAGDEFINITION_ID, TAGDEFINITION_NAME, MULTIPLICITY_ZEROTOONE);

        DataType dataType = new DataType(DATATYPE_ID, DATATYPE_NAME);

        Attribute attribute = new Attribute(ATTRIBUTE_ID, ATTRIBUTE_NAME, DATATYPE_ID, MULTIPLICITY_ZEROTOONE, taggedValues);
        List<Attribute> attributeList = new ArrayList<Attribute>(1);
        attributeList.add(attribute);
        ClassType classType = new ClassType(CLASSTYPE_ID, CLASSTYPE_NAME, false, attributeList, EMPTY_TAGGEDVALUES);

        EnumLiteral enumLiteralA = new EnumLiteral(ENUMLITERALA_ID, ENUMLITERALA_NAME, EMPTY_TAGGEDVALUES);
        EnumLiteral enumLiteralB = new EnumLiteral(ENUMLITERALB_ID, ENUMLITERALB_NAME, EMPTY_TAGGEDVALUES);
        List<EnumLiteral> enumLiteralList = new ArrayList<EnumLiteral>(2);
        enumLiteralList.add(enumLiteralA);
        enumLiteralList.add(enumLiteralB);
        EnumType enumType = new EnumType(ENUMTYPE_ID, ENUMTYPE_NAME, enumLiteralList, EMPTY_TAGGEDVALUES);

        AssociationEnd associationEndParent = new AssociationEnd(MULTIPLICITY_ZEROTOONE, ASSOCIATIONEND_PARENT_NAME, true, ASSOCIATIONEND_PARENT_ID, EMPTY_TAGGEDVALUES, CLASSTYPE_ID, ASSOCIATIONEND_PARENT_ATTR);
        AssociationEnd associationEndChild = new AssociationEnd(MULTIPLICITY_ZEROTOONE, ASSOCIATIONEND_CHILD_NAME, true, ASSOCIATIONEND_CHILD_ID, EMPTY_TAGGEDVALUES, CLASSTYPE_ID, ASSOCIATIONEND_CHILD_ATTR);
        ClassType associationClassType = new ClassType(ASSOCIATION_ID, ASSOCIATION_NAME, associationEndParent, associationEndChild, EMPTY_TAGGEDVALUES);

        Generalization generalization = new Generalization(GENERALIZATION_NAME, GENERALIZATION_ID, EMPTY_TAGGEDVALUES, CLASSTYPE_ID, ENUMTYPE_ID);

        List<NamespaceOwnedElement> packageElements = new ArrayList<NamespaceOwnedElement>(6);
        packageElements.add(tagDefinition);
        packageElements.add(dataType);
        packageElements.add(classType);
        packageElements.add(enumType);
        packageElements.add(associationClassType);
        packageElements.add(generalization);

        UmlPackage umlPackage = new UmlPackage(PACKAGE_NAME, PACKAGE_ID, taggedValues, packageElements);
        List<NamespaceOwnedElement> modelElements = new ArrayList<NamespaceOwnedElement>(1);
        modelElements.add(umlPackage);

        model = new Model(MODEL_ID, MODEL_NAME, EMPTY_TAGGEDVALUES, modelElements);
        modelIndex = new DefaultModelIndex(model);
    }

    @Test
    public void testWriteDocumentOutputStream() throws Exception {

        OutputStream os = new ByteArrayOutputStream();
        OutputStream mockOutputStream = spy(os);
        XmiWriter.writeDocument(model, modelIndex, mockOutputStream);

        String output = mockOutputStream.toString();

        //check for model name and id
        assertTrue(output.contains(MODEL_ID.toString()));
        assertTrue(output.contains(MODEL_NAME));

        //check package name and id
        assertTrue(output.contains(PACKAGE_ID.toString()));
        assertTrue(output.contains(PACKAGE_NAME));

        //check for tagged values
        assertTrue(output.contains(TAGGEDVALUE_VALUE));

        //check for tag definitions
        assertTrue(output.contains(TAGDEFINITION_ID.toString()));
        assertTrue(output.contains(TAGDEFINITION_NAME));

        //check for multiplicity
        assertTrue(output.contains("lower=\"0\""));
        assertTrue(output.contains("upper=\"1\""));
        assertFalse(output.contains("upper=\"-1\""));

        //check for datatype
        assertTrue(output.contains(DATATYPE_ID.toString()));
        assertTrue(output.contains(DATATYPE_NAME));

        //check for attribute
        assertTrue(output.contains(ATTRIBUTE_ID.toString()));
        assertTrue(output.contains(ATTRIBUTE_NAME));

        //check for classtype
        assertTrue(output.contains(CLASSTYPE_ID.toString()));
        assertTrue(output.contains(CLASSTYPE_NAME));

        //check for enum literal
        assertTrue(output.contains(ENUMLITERALA_ID.toString()));
        assertTrue(output.contains(ENUMLITERALA_NAME));

        //check for enumtype
        assertTrue(output.contains(ENUMTYPE_ID.toString()));
        assertTrue(output.contains(ENUMTYPE_NAME));

        //check for associationend
        assertTrue(output.contains(ASSOCIATIONEND_PARENT_ID.toString()));
        assertTrue(output.contains(ASSOCIATIONEND_PARENT_NAME));

        //check for generalization
        assertTrue(output.contains(GENERALIZATION_ID.toString()));
        assertTrue(output.contains(GENERALIZATION_NAME));
    }

    @Test
    public void testWriteDocumentFile() {
        File file = new File("unittest-writedocumentfile.xmi");
        assertFalse(file.exists());

        XmiWriter.writeDocument(model, modelIndex, file);

        assertTrue(file.exists());
        file.deleteOnExit();
    }

    @Test
    public void testWriteDocumentString() {
        String filename = "unittest-writedocumentstring.xmi";
        File file = new File(filename);
        assertFalse(file.exists());

        XmiWriter.writeDocument(model, modelIndex, filename);

        assertTrue(file.exists());
        file.deleteOnExit();
    }

    @Test
    public void testConstructor() {
        new XmiWriter();
    }

}
