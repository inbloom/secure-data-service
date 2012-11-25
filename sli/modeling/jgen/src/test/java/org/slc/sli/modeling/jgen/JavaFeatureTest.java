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
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Multiplicity;
import org.slc.sli.modeling.uml.Occurs;
import org.slc.sli.modeling.uml.Range;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.index.ModelIndex;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit for JavaFeature
 * @author chung
 */
public class JavaFeatureTest {

    JavaFeature javaFeature;
    Attribute attr;
    ClassType classType;
    ModelIndex modelIndex;

    @Before
    public void setup() {
        Multiplicity oneToOne = new Multiplicity(new Range(Occurs.ONE, Occurs.ONE));
        attr = new Attribute(Identifier.random(), "Attr1", Identifier.fromString("Attr1Type"),
                oneToOne, new ArrayList<TaggedValue>());
        classType = new ClassType(
                Identifier.random(),
                "TestClassType",
                false,
                new ArrayList<Attribute>() {
                    {
                        add(attr);
                    }
                },
                new ArrayList<TaggedValue>());
        modelIndex = mock(ModelIndex.class);
        when(modelIndex.getType(any(Identifier.class))).thenReturn(classType);
        javaFeature = new JavaFeature(attr, modelIndex);
    }

    @Test
    public void testGetModel() {
        ModelIndex model = javaFeature.getModel();
        assertTrue(model.equals(modelIndex));
    }

    @Test
    public void testGetAttributeType() {
        JavaType javaType = javaFeature.getAttributeType(new JavaGenConfig(false));
        assertTrue(javaType.getSimpleName().equals(classType.getName()));
    }

    @Test
    public void testGetNavigableTypeName() {
        String str = javaFeature.getNavigableTypeName();
        assertTrue(str.equals("String"));
    }

    @Test
    public void testGetNavigableType() {
        JavaType javaType = javaFeature.getNavigableType();
        assertTrue(javaType.equals(JavaType.JT_STRING));
    }

    @Test
    public void testIsRequired() {
        assertTrue(javaFeature.isRequired());
    }

    @Test
    public void testIsZeroOrMore() {
        assertFalse(javaFeature.isZeroOrMore());
    }

    @Test
    public void testIsOneOrMore() {
        assertFalse(javaFeature.isOneOrMore());
    }

    @Test
    public void testIsAssociationEnd() {
        assertFalse(javaFeature.isAssociationEnd());
    }

    @Test
    public void testToString() {
        String str = javaFeature.toString();
        assertTrue(str.equals(attr.toString()));
    }
}
