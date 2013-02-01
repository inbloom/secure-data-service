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

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.uml.index.ModelIndex;
import org.slc.sli.modeling.xsd.WxsNamespace;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;

/** 
* Xsd2UmlPluginHostAdapter Tester. 
* 
* @author pghosh@wgen.net
* @since <pre>Sep 10, 2012</pre> 
* @version 1.0 
*/

@RunWith(MockitoJUnitRunner.class)
public class Xsd2UmlPluginHostAdapterTest { 

    private Xsd2UmlPluginHostAdapter adapter;

    @Mock
    ModelIndex mapper;

    @Mock
    Xsd2UmlPluginHost host;

    @Mock
    TagDefinition tagDefinition;

    @Mock
    Attribute attribute;

    @Mock
    ClassType classType;
    @Mock
    AssociationEnd lhs;

    @Mock
    AssociationEnd rhs;

    QName qName;

    Identifier identifier;

@Before
public void before() throws Exception {
    adapter = new Xsd2UmlPluginHostAdapter(mapper);
    identifier = Identifier.random();
    qName = new QName(WxsNamespace.URI, "test");

}


    @Test(expected = IllegalArgumentException.class)
    public void testInvalidInstance() {
        Xsd2UmlPluginHostAdapter invalidAdapter = new Xsd2UmlPluginHostAdapter(null);
    }
/** 
* 
* Method: declareTagDefinitions(final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testDeclareTagDefinitions() throws Exception {
    Collection<TagDefinition> definitions = adapter.declareTagDefinitions(host);
    Assert.assertEquals(definitions, Collections.emptyList());
}

/** 
* 
* Method: ensureTagDefinitionId(final String name) 
* 
*/ 
@Test
public void testEnsureTagDefinitionId() throws Exception {
    Mockito.when(tagDefinition.getId()).thenAnswer(new Answer<Identifier>() {
        @Override
        public Identifier answer(InvocationOnMock invocation) throws Throwable {
            return Identifier.random();
        }
    });
    Mockito.when(mapper.getTagDefinition(Matchers.any(QName.class))).thenReturn(tagDefinition);
    Assert.assertNotNull(adapter.ensureTagDefinitionId(Mockito.anyString()));
}
@Test(expected = IllegalArgumentException.class)
public void testEnsureTagDefinitionException() throws Exception {
    Mockito.when(mapper.getTagDefinition(Matchers.any(QName.class))).thenReturn(null);
    adapter.ensureTagDefinitionId(Mockito.anyString());
}

/** 
* 
* Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testGetAssociationEndTypeName() throws Exception {
    adapter.getAssociationEndTypeName(classType, attribute, host);
}

/** 
* 
* Method: getTagDefinition(final Identifier id) 
* 
*/ 
@Test
public void testGetTagDefinition() throws Exception { 
   Mockito.when(mapper.getTagDefinition(Matchers.any(Identifier.class))).thenReturn(tagDefinition);
    Assert.assertEquals(tagDefinition, adapter.getTagDefinition(identifier));
}

/** 
* 
* Method: getType(final Identifier typeId) 
* 
*/ 
@Test
public void testGetType() throws Exception {
    Mockito.when(mapper.getType(Matchers.any(Identifier.class))).thenReturn(classType);
    Type type = adapter.getType(identifier);
    Assert.assertEquals(classType, type);
}

/** 
* 
* Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testIsAssociationEnd() throws Exception {
    Assert.assertFalse(adapter.isAssociationEnd(classType, attribute, host));
}

/** 
* 
* Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testNameAssociation() throws Exception {
    Mockito.when(lhs.getName()).thenReturn("lhs");
    Mockito.when(rhs.getName()).thenReturn("rhs");
    String assocEnd = adapter.nameAssociation(lhs, rhs, host.getPlugin());
    Assert.assertEquals("lhs<=>rhs", assocEnd);
}

/** 
* 
* Method: nameFromComplexTypeExtension(final QName complexType, final QName base) 
* 
*/ 
@Test
public void testNameFromComplexTypeExtension() throws Exception {

    Assert.assertEquals("test extends test", adapter.nameFromComplexTypeExtension(qName, qName));
}

/** 
* 
* Method: nameFromSchemaElementName(final QName name) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testNameFromSchemaElementName() throws Exception {
    adapter.nameFromSchemaElementName(qName);
}

/** 
* 
* Method: nameFromSchemaAttributeName(final QName name) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testNameFromSchemaAttributeName() throws Exception {
    adapter.nameFromSchemaAttributeName(qName);
}

/** 
* 
* Method: nameFromSimpleTypeRestriction(final QName simpleType, final QName base) 
* 
*/ 
@Test
public void testNameFromSimpleTypeRestriction() throws Exception { 
    Assert.assertEquals("test restricts test", adapter.nameFromSimpleTypeRestriction(qName, qName));
}

/** 
* 
* Method: nameFromSchemaTypeName(final QName name) 
* 
*/ 
@Test
public void testNameFromSchemaTypeName() throws Exception {
    Assert.assertEquals("test", adapter.nameFromSchemaTypeName(qName));
}

/** 
* 
* Method: tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testTagsFromAppInfo() throws Exception {
    Assert.assertEquals(Collections.emptyList(), adapter.tagsFromAppInfo(new XmlSchemaAppInfo(), host));
}


} 
