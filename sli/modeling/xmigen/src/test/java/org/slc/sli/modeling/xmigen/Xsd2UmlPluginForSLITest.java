package org.slc.sli.modeling.xmigen;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.modeling.psm.helpers.SliUmlConstants;
import org.slc.sli.modeling.uml.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 
* Xsd2UmlPluginForSLI Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 12, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlPluginForSLITest { 

    private Xsd2UmlPluginForSLI pluginForSLI;
    private TagDefinition tagDefinition;

@Before
public void before() throws Exception {
    pluginForSLI = new Xsd2UmlPluginForSLI();
}

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: declareTagDefinitions(final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testDeclareTagDefinitions() throws Exception { 
//TODO: Test goes here...
    Xsd2UmlPluginHost host = mock(Xsd2UmlPluginHost.class);
    when(host.ensureTagDefinitionId(anyString())).thenReturn(Identifier.random());
    List<TagDefinition> tagDefinitionList = pluginForSLI.declareTagDefinitions(host);
    assertNotNull(tagDefinitionList);
    assertEquals(11 , tagDefinitionList.size());
} 

/** 
* 
* Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testGetAssociationEndTypeName() throws Exception { 
//TODO: Test goes here...
    ClassType classType = mock(ClassType.class);
    Attribute attribute = mock(Attribute.class);
    Xsd2UmlPluginHost host = mock(Xsd2UmlPluginHost.class);
    TaggedValue taggedValue = mock(TaggedValue.class);
    TagDefinition tagDefinition = mock(TagDefinition.class);
    Identifier id = mock(Identifier.class);
    List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
    taggedValueList.add(taggedValue);

    when(attribute.getTaggedValues()).thenReturn(taggedValueList);
    when(taggedValue.getTagDefinition()).thenReturn(id);
    when(host.getTagDefinition(any(Identifier.class))).thenReturn(tagDefinition);
    when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REFERENCE);
    when(taggedValue.getValue()).thenReturn("test");

    String name = pluginForSLI.getAssociationEndTypeName(classType,attribute,host);
    assertNotNull(name);
} 

/** 
* 
* Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testIsAssociationEnd() throws Exception { 
//TODO: Test goes here...
    ClassType classType = mock(ClassType.class);
    Attribute attribute = mock(Attribute.class);
    Xsd2UmlPluginHost host = mock(Xsd2UmlPluginHost.class);
    TaggedValue taggedValue = mock(TaggedValue.class);
    TagDefinition tagDefinition = mock(TagDefinition.class);
    Identifier id = mock(Identifier.class);
    List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
    taggedValueList.add(taggedValue);

    when(attribute.getTaggedValues()).thenReturn(taggedValueList);
    when(taggedValue.getTagDefinition()).thenReturn(id);
    when(host.getTagDefinition(any(Identifier.class))).thenReturn(tagDefinition);
    when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REFERENCE);
    when(taggedValue.getValue()).thenReturn("test");
    assertTrue(pluginForSLI.isAssociationEnd(classType,attribute,host));
}
    @Test
    public void testIsAssociationEndFalse() throws Exception {
//TODO: Test goes here...
        ClassType classType = mock(ClassType.class);
        Attribute attribute = mock(Attribute.class);
        Xsd2UmlPluginHost host = mock(Xsd2UmlPluginHost.class);
        TaggedValue taggedValue = mock(TaggedValue.class);
        TagDefinition tagDefinition = mock(TagDefinition.class);
        Identifier id = mock(Identifier.class);
        List<TaggedValue> taggedValueList = new ArrayList<TaggedValue>();
        taggedValueList.add(taggedValue);

        when(attribute.getTaggedValues()).thenReturn(taggedValueList);
        when(taggedValue.getTagDefinition()).thenReturn(id);
        when(host.getTagDefinition(any(Identifier.class))).thenReturn(tagDefinition);
        when(tagDefinition.getName()).thenReturn(SliUmlConstants.TAGDEF_REST_RESOURCE);
        when(taggedValue.getValue()).thenReturn("test");
        assertFalse(pluginForSLI.isAssociationEnd(classType,attribute,host));
    }

    /**
* 
* Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testNameAssociation() throws Exception { 
//TODO: Test goes here...
    AssociationEnd associationEnd = mock(AssociationEnd.class);
    Xsd2UmlPluginHost host = mock(Xsd2UmlPluginHost.class);
    assertTrue(pluginForSLI.nameAssociation(associationEnd,associationEnd,host).isEmpty());
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


/** 
* 
* Method: makeTagDefinition(final String name, final Occurs lower, final Occurs upper, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test
public void testMakeTagDefinition() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Xsd2UmlPluginForSLI.getClass().getMethod("makeTagDefinition", final.class, final.class, final.class, final.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: stringValue(final NodeList markup) 
* 
*/ 
@Test
public void testStringValue() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Xsd2UmlPluginForSLI.getClass().getMethod("stringValue", final.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

/** 
* 
* Method: titleCase(final String text) 
* 
*/ 
@Test
public void testTitleCase() throws Exception { 
//TODO: Test goes here... 
/* 
try { 
   Method method = Xsd2UmlPluginForSLI.getClass().getMethod("titleCase", final.class); 
   method.setAccessible(true); 
   method.invoke(<Object>, <Parameters>); 
} catch(NoSuchMethodException e) { 
} catch(IllegalAccessException e) { 
} catch(InvocationTargetException e) { 
} 
*/ 
} 

} 
