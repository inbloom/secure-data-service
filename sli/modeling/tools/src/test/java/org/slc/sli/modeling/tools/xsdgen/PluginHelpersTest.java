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

package org.slc.sli.modeling.tools.xsdgen;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.util.ArrayList;
import java.util.List;

/**;
 * @author jstokes
 */
public class PluginHelpersTest {
    @Test
    public void testWriteDocumentation() throws Exception {
        final Taggable mockTaggable = Mockito.mock(Taggable.class);
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = Mockito.mock(Uml2XsdPluginWriter.class);
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        final String testValue = "test\n\nvalue\n\n\r\t";
        taggedValueList.add(new TaggedValue(testValue, id));
        final Multiplicity mult = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final TagDefinition mockTagDef = new TagDefinition(id, TagName.DOCUMENTATION, mult);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(mockTagDef);
        Mockito.when(mockTaggable.getTaggedValues()).thenReturn(taggedValueList);

        PluginHelpers.writeDocumentation(mockTaggable, mockIndex, mockWriter);

        Mockito.verify(mockWriter).characters("test value");
        Mockito.verify(mockWriter).documentation();
        Mockito.verify(mockWriter).end();
    }

    @Test
    public void testIsMongoNavigable() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue("true", id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAVIGABLE, multiplicity);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);
        Assert.assertTrue(PluginHelpers.isMongoNavigable(mockEnd, mockIndex));

        final TagDefinition falseTagDef = new TagDefinition(id, TagName.FRACTION_DIGITS, multiplicity);
        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(falseTagDef);
        Assert.assertFalse(PluginHelpers.isMongoNavigable(mockEnd, mockIndex));
    }

    @Test
    public void testHasMongoName() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue(TagName.MONGO_NAME, id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAME, multiplicity);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);
        Assert.assertTrue(PluginHelpers.hasMongoName(mockEnd, mockIndex));

        final TagDefinition falseTagDef = new TagDefinition(id, TagName.FRACTION_DIGITS, multiplicity);
        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(falseTagDef);
        Assert.assertFalse(PluginHelpers.hasMongoName(mockEnd, mockIndex));
    }

    @Test
    public void testGetMongoName() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue("CollectionName", id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = Mockito.mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAME, multiplicity);

        Mockito.when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);

        Assert.assertEquals("CollectionName", PluginHelpers.getMongoName(mockEnd, mockIndex));
    }

    @Test
    public void testInit() {
        final PluginHelpers pluginHelpers = new PluginHelpers();
        Assert.assertNotNull(pluginHelpers);
    }
}
