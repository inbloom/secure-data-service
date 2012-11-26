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

package org.slc.sli.modeling.xsdgen;

import org.junit.Test;
import org.slc.sli.modeling.psm.helpers.TagName;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Taggable;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**;
 * @author jstokes
 */
public class PluginHelpersTest {
    @Test
    public void testWriteDocumentation() throws Exception {
        final Taggable mockTaggable = mock(Taggable.class);
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final Uml2XsdPluginWriter mockWriter = mock(Uml2XsdPluginWriter.class);
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        final String testValue = "test\n\nvalue\n\n\r\t";
        taggedValueList.add(new TaggedValue(testValue, id));
        final Multiplicity mult = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final TagDefinition mockTagDef = new TagDefinition(id, TagName.DOCUMENTATION, mult);

        when(mockIndex.getTagDefinition(id)).thenReturn(mockTagDef);
        when(mockTaggable.getTaggedValues()).thenReturn(taggedValueList);

        PluginHelpers.writeDocumentation(mockTaggable, mockIndex, mockWriter);

        verify(mockWriter).characters("test value");
        verify(mockWriter).documentation();
        verify(mockWriter).end();
    }

    @Test
    public void testIsMongoNavigable() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue("true", id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAVIGABLE, multiplicity);

        when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);
        assertTrue(PluginHelpers.isMongoNavigable(mockEnd, mockIndex));

        final TagDefinition falseTagDef = new TagDefinition(id, TagName.FRACTION_DIGITS, multiplicity);
        when(mockIndex.getTagDefinition(id)).thenReturn(falseTagDef);
        assertFalse(PluginHelpers.isMongoNavigable(mockEnd, mockIndex));
    }

    @Test
    public void testHasMongoName() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue(TagName.MONGO_NAME, id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAME, multiplicity);

        when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);
        assertTrue(PluginHelpers.hasMongoName(mockEnd, mockIndex));

        final TagDefinition falseTagDef = new TagDefinition(id, TagName.FRACTION_DIGITS, multiplicity);
        when(mockIndex.getTagDefinition(id)).thenReturn(falseTagDef);
        assertFalse(PluginHelpers.hasMongoName(mockEnd, mockIndex));
    }

    @Test
    public void testGetMongoName() throws Exception {
        final Multiplicity multiplicity = new Multiplicity(new Range(Occurs.ZERO, Occurs.ONE));
        final List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        final Identifier id = Identifier.fromString("1234");
        taggedValueList.add(new TaggedValue("CollectionName", id));
        final AssociationEnd mockEnd =
                new AssociationEnd(multiplicity, "MockEnd", true, taggedValueList, Identifier.random(), "AssocEnd");
        final ModelIndex mockIndex = mock(ModelIndex.class);
        final TagDefinition trueTagDef = new TagDefinition(id, TagName.MONGO_NAME, multiplicity);

        when(mockIndex.getTagDefinition(id)).thenReturn(trueTagDef);

        assertEquals("CollectionName", PluginHelpers.getMongoName(mockEnd, mockIndex));
    }

    @Test
    public void testInit() {
        final PluginHelpers pluginHelpers = new PluginHelpers();
        assertNotNull(pluginHelpers);
    }
}
