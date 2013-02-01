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

package org.slc.sli.modeling.tools.xmigen;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Xsd2UmlTweakerVisitor Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Sep 11, 2012</pre>
 */
public class Xsd2UmlTweakerVisitorTest {

    private static final String DATATYPE_NAME = "dataTypeName";
    private static final Identifier DATATYPE_ID = Identifier.random();
    private static final String CLASSTYPE_NAME = "classTypeName";
    private static final Identifier CLASSTYPE_ID = Identifier.random();
    private static final String UMLPACKAGE_NAME = "umlPackageName";

    private Xsd2UmlTweakerVisitor visitor;
    private ModelIndex indexModel;

    @Before
    public void before() throws Exception {
        List<NamespaceOwnedElement> packageElements = new ArrayList<NamespaceOwnedElement>();
        List<NamespaceOwnedElement> modelElements = new ArrayList<NamespaceOwnedElement>();

        ClassType classType = new ClassType(CLASSTYPE_ID, CLASSTYPE_NAME, true, new ArrayList<Attribute>(0), new ArrayList<TaggedValue>(0));
        packageElements.add(classType);
        DataType dataType = new DataType(DATATYPE_ID, DATATYPE_NAME);
        packageElements.add(dataType);
        UmlPackage umlPackage = new UmlPackage(UMLPACKAGE_NAME, packageElements);
        modelElements.add(umlPackage);
        modelElements.add(classType);
        Model model = new Model(CLASSTYPE_ID, "modelName", new ArrayList<TaggedValue>(0), modelElements);
        indexModel = new DefaultModelIndex(model);
        visitor = new Xsd2UmlTweakerVisitor(indexModel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInstance() throws Exception {
        Xsd2UmlTweakerVisitor invalidVisitor = new Xsd2UmlTweakerVisitor(null);
    }

    /**
     * Method: visit(final AssociationEnd associationEnd)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitAssociationEnd() throws Exception {
        visitor.visit(Mockito.mock(AssociationEnd.class));
    }

    /**
     * Method: visit(final Attribute attribute)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitAttribute() throws Exception {
        visitor.visit(Mockito.mock(Attribute.class));
    }

    /**
     * Method: visit(final ClassType classType)
     */
    @Test
    public void testVisitClassType() throws Exception {
        ClassType classType = Mockito.mock(ClassType.class);
        Mockito.when(classType.isClassType()).thenReturn(true);
        Mockito.when(classType.isAssociation()).thenReturn(false);
        visitor.visit(classType);


        ClassType assocType = Mockito.mock(ClassType.class);
        Mockito.when(assocType.isClassType()).thenReturn(false);
        Mockito.when(assocType.isAssociation()).thenReturn(true);
        visitor.visit(assocType);

        List<NamespaceOwnedElement> ownedElements = visitor.getOwnedElements();
        Assert.assertEquals(2, ownedElements.size());
    }

    @Test(expected = AssertionError.class)
    public void testVisitClassTypeAssertion() throws Exception {
        ClassType classType = Mockito.mock(ClassType.class);
        Mockito.when(classType.isClassType()).thenReturn(true);
        Mockito.when(classType.isAssociation()).thenReturn(true);
        visitor.visit(classType);
    }

    @Test(expected = AssertionError.class)
    public void testVisitAssocTypeAssertion() throws Exception {
        ClassType classType = Mockito.mock(ClassType.class);
        Mockito.when(classType.isClassType()).thenReturn(false);
        Mockito.when(classType.isAssociation()).thenReturn(false);
        visitor.visit(classType);
    }

    /**
     * Method: visit(final DataType dataType)
     */
    @Test
    public void testVisitDataType() throws Exception {
        int expectedCount = visitor.getOwnedElements().size() + 1;
        visitor.visit(Mockito.mock(DataType.class));
        Assert.assertEquals(expectedCount, visitor.getOwnedElements().size());
    }

    /**
     * Method: visit(final EnumLiteral enumLiteral)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitEnumLiteral() throws Exception {
        visitor.visit(Mockito.mock(EnumLiteral.class));
    }

    /**
     * Method: visit(final EnumType enumType)
     */
    @Test
    public void testVisitEnumType() throws Exception {
        int expectedCount = visitor.getOwnedElements().size() + 1;
        visitor.visit(Mockito.mock(EnumType.class));
        Assert.assertEquals(expectedCount, visitor.getOwnedElements().size());
    }

    /**
     * Method: visit(final Generalization generalization)
     */
    @Test
    public void testVisitGeneralization() throws Exception {
        int expectedCount = visitor.getOwnedElements().size() + 1;
        visitor.visit(Mockito.mock(Generalization.class));
        Assert.assertEquals(expectedCount, visitor.getOwnedElements().size());
    }

    /**
     * Method: visit(final Multiplicity multiplicity)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitMultiplicity() throws Exception {
        visitor.visit(Mockito.mock(Multiplicity.class));
    }

    /**
     * Method: visit(final Range range)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitRange() throws Exception {
        visitor.visit(Mockito.mock(Range.class));
    }

    /**
     * Method: visit(final TagDefinition tagDefinition)
     */
    @Test
    public void testVisitTagDefinition() throws Exception {
        int expectedCount = visitor.getOwnedElements().size() + 1;
        visitor.visit(Mockito.mock(TagDefinition.class));
        Assert.assertEquals(expectedCount, visitor.getOwnedElements().size());
    }

    /**
     * Method: visit(final TaggedValue taggedValue)
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testVisitTaggedValue() throws Exception {
        visitor.visit(Mockito.mock(TaggedValue.class));
    }


    /**
     * Method: transform(final ClassType classType, final ModelIndex model)
     */
    @Test(expected = InvocationTargetException.class)
    public void testTransformException() throws Exception {
        Method method = visitor.getClass().getDeclaredMethod("transform", ClassType.class, ModelIndex.class);
        method.setAccessible(true);
        method.invoke(visitor, null, null);

    }

    @Test
    public void testTransform() throws Exception {
        Method method = visitor.getClass().getDeclaredMethod("transform", ClassType.class, ModelIndex.class);
        method.setAccessible(true);
        ClassType classType = Mockito.mock(ClassType.class);
        ModelIndex modelIndex = Mockito.mock(ModelIndex.class);
        AssociationEnd lhs = Mockito.mock(AssociationEnd.class);
        AssociationEnd rhs = Mockito.mock(AssociationEnd.class);
        Multiplicity multiplicity = Mockito.mock(Multiplicity.class);
        TagDefinition tagDefinition = Mockito.mock(TagDefinition.class);
        Range range = Mockito.mock(Range.class);
        List<AssociationEnd> ends = new ArrayList<AssociationEnd>();
        ends.add(lhs);
        ends.add(rhs);
        Mockito.when(modelIndex.getAssociationEnds(Matchers.any(Identifier.class))).thenReturn(ends);
        Mockito.when(modelIndex.getTagDefinition(Matchers.any(Identifier.class))).thenReturn(tagDefinition);
        Mockito.when(lhs.getMultiplicity()).thenReturn(multiplicity);
        Mockito.when(rhs.getMultiplicity()).thenReturn(multiplicity);
        TaggedValue taggedValue = Mockito.mock(TaggedValue.class);
        List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        taggedValueList.add(taggedValue);
        Mockito.when(lhs.getTaggedValues()).thenReturn(taggedValueList);
        Mockito.when(rhs.getTaggedValues()).thenReturn(taggedValueList);
        Mockito.when(multiplicity.getRange()).thenReturn(range);
        Mockito.when(range.getLower()).thenReturn(Occurs.ONE);
        Mockito.when(range.getUpper()).thenReturn(Occurs.ONE);
        Mockito.when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_NATURAL_KEY);
        Mockito.when(taggedValue.getValue()).thenReturn("true");

        Mockito.when(lhs.getName()).thenReturn("lhsTest");
        Mockito.when(rhs.getName()).thenReturn("rhsTest");
        Mockito.when(lhs.getType()).thenReturn(Identifier.random());
        Mockito.when(rhs.getType()).thenReturn(Identifier.random());
        Mockito.when(lhs.getAssociatedAttributeName()).thenReturn("attribute");
        Mockito.when(rhs.getAssociatedAttributeName()).thenReturn("attribute");
        Mockito.when(classType.getName()).thenReturn("Association");
        Mockito.when(classType.getId()).thenReturn(Identifier.random());

        method.invoke(visitor, classType, modelIndex);

    }


} 
