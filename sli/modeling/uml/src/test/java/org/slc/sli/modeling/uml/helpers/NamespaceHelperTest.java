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

package org.slc.sli.modeling.uml.helpers;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.UmlPackage;
import org.slc.sli.modeling.uml.index.DefaultModelIndex;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * JUnit test for NamespaceHelper class.
 *
 * @author wscott
 */
public class NamespaceHelperTest {

    private DefaultModelIndex modelIndex;

    private static final String DATATYPE_NAME = "dataTypeName";
    private static final Identifier DATATYPE_ID = Identifier.random();
    private static final String CLASSTYPE_NAME = "classTypeName";
    private static final Identifier CLASSTYPE_ID = Identifier.random();
    private static final String UMLPACKAGE_NAME = "umlPackageName";

    @Before
    public void setUp() throws Exception {
        List<NamespaceOwnedElement> packageElements = new ArrayList<NamespaceOwnedElement>();
        List<NamespaceOwnedElement> modelElements = new ArrayList<NamespaceOwnedElement>();

        ClassType classType = new ClassType(CLASSTYPE_ID, CLASSTYPE_NAME, true, new ArrayList<Attribute>(0), new ArrayList<TaggedValue>(0));
        packageElements.add(classType);
        DataType dataType = new DataType(DATATYPE_ID, DATATYPE_NAME);
        packageElements.add(dataType);
        UmlPackage umlPackage = new UmlPackage(UMLPACKAGE_NAME, packageElements);
        modelElements.add(umlPackage);

        Model model = new Model(CLASSTYPE_ID, "modelName", new ArrayList<TaggedValue>(0), modelElements);
        modelIndex = new DefaultModelIndex(model);
    }

    @Test
    public void testGetNamespace() throws Exception {
        Type classType = modelIndex.getType(CLASSTYPE_ID);
        assertEquals(UMLPACKAGE_NAME, NamespaceHelper.getNamespace(classType, modelIndex));
    }

    @Test
    public void testGetNamespaceNonusedType() {
        Type nonusedType = mock(Type.class);
        when(nonusedType.getId()).thenReturn(Identifier.random());
        assertEquals("", NamespaceHelper.getNamespace(nonusedType, modelIndex));
    }

    @Test
    public void testConstructor() {
        new NamespaceHelper();
    }
}
