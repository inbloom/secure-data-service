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

package org.slc.sli.modeling.uml.index;

import javax.xml.namespace.QName;

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
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit test for IndexingVisitor class.
 *
 * @author wscott
 */
public class IndexingVisitorTest {
    private IndexingVisitor iv;

    @Before
    public void setUp() throws Exception {
        iv = new IndexingVisitor();
    }

    @Test
    public void testVisitAssociationEnd() {
        Identifier assocEndId = Identifier.random();
        String assocEndName = "assocEndName";

        AssociationEnd mockAssociationEnd = mock(AssociationEnd.class);
        when(mockAssociationEnd.getId()).thenReturn(assocEndId);
        when(mockAssociationEnd.getName()).thenReturn(assocEndName);

        iv.visit(mockAssociationEnd);
        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(assocEndId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(assocEndName)));
    }

    @Test
    public void testVisitAttribute() {
        Identifier attributeId = Identifier.random();
        Identifier typeId = Identifier.random();
        String attributeName = "attributeName";

        Attribute mockAttribute = mock(Attribute.class);
        when(mockAttribute.getId()).thenReturn(attributeId);
        when(mockAttribute.getName()).thenReturn(attributeName);
        when(mockAttribute.getType()).thenReturn(typeId);

        iv.visit(mockAttribute);
        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(attributeId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(attributeName)));
        assertEquals(1, iv.getWhereUsed().size());
        assertTrue(iv.getWhereUsed().containsKey(typeId));
    }

    @Test
    public void testVisitClassType() {
        Identifier classTypeId = Identifier.random();
        String classTypeName = "classTypeName";

        ClassType mockClassType = mock(ClassType.class);
        when(mockClassType.getId()).thenReturn(classTypeId);
        when(mockClassType.getName()).thenReturn(classTypeName);
        when(mockClassType.isAssociation()).thenReturn(false);

        iv.visit(mockClassType);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(classTypeId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(classTypeName)));
        assertEquals(1, iv.getNamespaceMap().size());
        assertTrue(iv.getNamespaceMap().containsKey(classTypeId));
        assertEquals(1, iv.getClassTypesByName().size());
        assertTrue(iv.getClassTypesByName().containsKey(classTypeName));
    }

    @Test
    public void testVisitDataType() {
        Identifier dataTypeId = Identifier.random();
        String dataTypeName = "dataTypeName";

        DataType mockDataType = mock(DataType.class);
        when(mockDataType.getId()).thenReturn(dataTypeId);
        when(mockDataType.getName()).thenReturn(dataTypeName);

        iv.visit(mockDataType);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(dataTypeId));
        assertEquals(1, iv.getDataTypesByName().size());
        assertTrue(iv.getDataTypesByName().containsKey(new QName(dataTypeName)));
        assertEquals(1, iv.getNamespaceMap().size());
        assertTrue(iv.getNamespaceMap().containsKey(dataTypeId));

    }

    @Test
    public void testVisitEnumLiteral() {
        Identifier enumLiteralId = Identifier.random();
        String enumLiteralName = "enumLiteralName";

        EnumLiteral mockEnumLiteral = mock(EnumLiteral.class);
        when(mockEnumLiteral.getId()).thenReturn(enumLiteralId);
        when(mockEnumLiteral.getName()).thenReturn(enumLiteralName);

        iv.visit(mockEnumLiteral);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(enumLiteralId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(enumLiteralName)));
    }

    @Test
    public void testVisitEnumType() {
        Identifier enumTypeId = Identifier.random();
        String enumTypeName = "enumTypeName";

        EnumType mockEnumType = mock(EnumType.class);
        when(mockEnumType.getId()).thenReturn(enumTypeId);
        when(mockEnumType.getName()).thenReturn(enumTypeName);

        iv.visit(mockEnumType);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(enumTypeId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(enumTypeName)));
        assertEquals(1, iv.getNamespaceMap().size());
        assertTrue(iv.getNamespaceMap().containsKey(enumTypeId));
    }

    @Test
    public void testVisitGeneralization() {
        Identifier generalizationId = Identifier.random();
        String generalizationName = "generalizationName";

        Generalization mockGeneralization = mock(Generalization.class);
        when(mockGeneralization.getId()).thenReturn(generalizationId);
        when(mockGeneralization.getName()).thenReturn(generalizationName);

        iv.visit(mockGeneralization);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(generalizationId));
        assertEquals(1, iv.getNameMap().size());
        assertTrue(iv.getNameMap().containsKey(new QName(generalizationName)));
    }

    @Test
    public void testVisitMultiplicity() {
        Identifier multiplicityId = Identifier.random();

        Multiplicity mockMultiplicity = mock(Multiplicity.class);
        when(mockMultiplicity.getId()).thenReturn(multiplicityId);

        iv.visit(mockMultiplicity);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(multiplicityId));
    }

    @Test
    public void testVisitRange() {
        Identifier rangeId = Identifier.random();

        Range mockRange = mock(Range.class);
        when(mockRange.getId()).thenReturn(rangeId);

        iv.visit(mockRange);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(rangeId));
    }

    @Test
    public void testVisitTagDefinition() {
        Identifier tagDefinitionId = Identifier.random();
        String tagDefinitionName = "tagDefinitionName";

        TagDefinition mockTagDefinition = mock(TagDefinition.class);
        when(mockTagDefinition.getId()).thenReturn(tagDefinitionId);
        when(mockTagDefinition.getName()).thenReturn(tagDefinitionName);

        iv.visit(mockTagDefinition);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(tagDefinitionId));
        assertEquals(1, iv.getTagDefinitionsByName().size());
        assertTrue(iv.getTagDefinitionsByName().containsKey(new QName(tagDefinitionName)));
    }

    @Test
    public void testVisitTaggedValue() {
        Identifier taggedValueId = Identifier.random();

        TaggedValue mockTaggedValue = mock(TaggedValue.class);
        when(mockTaggedValue.getId()).thenReturn(taggedValueId);

        iv.visit(mockTaggedValue);

        assertEquals(1, iv.getModelElementMap().size());
        assertTrue(iv.getModelElementMap().containsKey(taggedValueId));
    }

}
