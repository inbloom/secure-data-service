package org.slc.sli.modeling.xmigen;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.index.ModelIndex;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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
    Identifier identifier;

@Before
public void before() throws Exception {
    adapter = new Xsd2UmlPluginHostAdapter(mapper);

} 

@After
public void after() throws Exception { 
}

    @Test(expected = NullPointerException.class)
    public void testInvalidInstance() {
        Xsd2UmlPluginHostAdapter invalidAdapter= new Xsd2UmlPluginHostAdapter(null);
    }
/** 
* 
* Method: declareTagDefinitions(final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testDeclareTagDefinitions() throws Exception {
    Collection<TagDefinition> definitions = adapter.declareTagDefinitions(host);
    assertEquals (definitions,Collections.emptyList());
}

/** 
* 
* Method: ensureTagDefinitionId(final String name) 
* 
*/ 
@Test
public void testEnsureTagDefinitionId() throws Exception {
    when(tagDefinition.getId()).thenReturn(identifier);
    when(mapper.getTagDefinition(any(QName.class))).thenReturn(tagDefinition);
    assertEquals(identifier , adapter.ensureTagDefinitionId("mock"));
} 

/** 
* 
* Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testGetAssociationEndTypeName() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getTagDefinition(final Identifier id) 
* 
*/ 
@Test
public void testGetTagDefinition() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: getType(final Identifier typeId) 
* 
*/ 
@Test
public void testGetType() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testIsAssociationEnd() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testNameAssociation() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameFromComplexTypeExtension(final QName complexType, final QName base) 
* 
*/ 
@Test
public void testNameFromComplexTypeExtension() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameFromSchemaElementName(final QName name) 
* 
*/ 
@Test
public void testNameFromSchemaElementName() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameFromSchemaAttributeName(final QName name) 
* 
*/ 
@Test
public void testNameFromSchemaAttributeName() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameFromSimpleTypeRestriction(final QName simpleType, final QName base) 
* 
*/ 
@Test
public void testNameFromSimpleTypeRestriction() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: nameFromSchemaTypeName(final QName name) 
* 
*/ 
@Test
public void testNameFromSchemaTypeName() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testTagsFromAppInfo() throws Exception { 
//TODO: Test goes here... 
} 


} 
