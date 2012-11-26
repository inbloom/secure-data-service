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

package org.slc.sli.modeling.xmigen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.Model;
import org.slc.sli.modeling.uml.NamespaceOwnedElement;
import org.slc.sli.modeling.uml.TaggedValue;
import org.slc.sli.modeling.uml.UmlPackage;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: pghosh
 * Date: 9/12/12
 * Time: 9:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Xsd2UmlLinkerTest {
    private Model model;
    //private Xsd2UmlLinker linker;
    private Xsd2UmlPlugin plugin;

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
        ClassType mockClass = mock(ClassType.class);
        Attribute attribute = mock(Attribute.class);
        plugin = mock(Xsd2UmlPlugin.class);
        List<Attribute> attributeList = new ArrayList<Attribute>();
        attributeList.add(attribute);
        when(mockClass.getId()).thenReturn(Identifier.random());
        when(mockClass.getName()).thenReturn("test");
        when(mockClass.getAttributes()).thenReturn(attributeList);
        modelElements.add(mockClass);
        model = new Model(CLASSTYPE_ID, "modelName", new ArrayList<TaggedValue>(0), modelElements);

    }

    @Test
    public void testLink() throws Exception {
        assertNotNull(Xsd2UmlLinker.link(model, plugin));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidAssociationEnd() throws Exception {
        when(plugin.isAssociationEnd(Mockito.any(ClassType.class), Mockito.any(Attribute.class), Mockito.any(Xsd2UmlPluginHost.class))).thenReturn(true);
        when(plugin.getAssociationEndTypeName(Mockito.any(ClassType.class), Mockito.any(Attribute.class), Mockito.any(Xsd2UmlPluginHost.class))).thenReturn("test");
        assertNotNull(Xsd2UmlLinker.link(model, plugin));
    }
}
