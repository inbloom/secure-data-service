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

package org.slc.sli.modeling.uml.helpers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;

import static org.junit.Assert.*;

/**
 * JUnit test for TaggedValueHelper class.
 *
 * @author wscott
 */
public class TaggedValueHelperTest {
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String RANDOM_TAG_NAME = "randomTagName";
    private DefaultModelIndex modelIndex;

    private static final String CLASSTYPE_NAME = "classTypeName";
    private static final Identifier CLASSTYPE_ID = Identifier.random();
    public static final String STRING_VALUE = "false";
    public static final String TAG_DEFINITION_NAME = "tagDefinitionName";

    @Before
    public void setUp() throws Exception {
        List<NamespaceOwnedElement> modelElements = new ArrayList<NamespaceOwnedElement>(1);
        List<TaggedValue> taggedValues = new ArrayList<TaggedValue>(2);

        Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        Identifier tagDefinitionId = Identifier.random();
        TagDefinition tagDefinition = new TagDefinition(tagDefinitionId, TAG_DEFINITION_NAME, multiplicity);
        modelElements.add(tagDefinition);

        TaggedValue booleanTaggedValue = new TaggedValue(STRING_VALUE, tagDefinitionId);
        taggedValues.add(booleanTaggedValue);

        ClassType classType = new ClassType(CLASSTYPE_ID, CLASSTYPE_NAME, true, new ArrayList<Attribute>(0), taggedValues);
        modelElements.add(classType);

        Model model = new Model(CLASSTYPE_ID, "modelName", new ArrayList<TaggedValue>(0), modelElements);
        modelIndex = new DefaultModelIndex(model);
    }

    @Test
    public void testGetBooleanTag() throws Exception {
        Type classType = modelIndex.getType(CLASSTYPE_ID);
        assertEquals(STRING_VALUE, TaggedValueHelper.getStringTag(TAG_DEFINITION_NAME, classType, modelIndex, ""));
        assertEquals(DEFAULT_VALUE, TaggedValueHelper.getStringTag(RANDOM_TAG_NAME, classType, modelIndex, DEFAULT_VALUE));
    }

    @Test
    public void testGetStringTag() throws Exception {
        Type classType = modelIndex.getType(CLASSTYPE_ID);
        assertEquals(false, TaggedValueHelper.getBooleanTag(TAG_DEFINITION_NAME, classType, modelIndex, true));
        assertEquals(true, TaggedValueHelper.getBooleanTag("randomTagName", classType, modelIndex, true));
    }

    @Test
    public void testHasTag() throws Exception {
        Type classType = modelIndex.getType(CLASSTYPE_ID);
        assertTrue(TaggedValueHelper.hasTag(TAG_DEFINITION_NAME, classType, modelIndex));
    }

    @Test
    public void testHasTagFalse() {
        Type classType = modelIndex.getType(CLASSTYPE_ID);
        assertFalse(TaggedValueHelper.hasTag("", classType, modelIndex));
    }

    @Test
    public void testConstructor() {
        new TaggedValueHelper();
    }
}
