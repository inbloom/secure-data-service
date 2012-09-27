package org.slc.sli.modeling.xmigen;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;

import javax.swing.text.html.HTML;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/** 
* Xsd2UmlPluginGeneric Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 11, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlPluginGenericTest { 

    @Mock
    Xsd2UmlPluginHost host;

    @Mock
    ClassType classType;

    @Mock
    Attribute attribute;

    Xsd2UmlPluginGeneric generic;
@Before
public void before() throws Exception {
    generic = new Xsd2UmlPluginGeneric();
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
    assertEquals(Collections.emptyList(), generic.declareTagDefinitions(host));
}

/** 
* 
* Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testGetAssociationEndTypeName() throws Exception {
    generic.getAssociationEndTypeName(classType,attribute,host);
}

/** 
* 
* Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testIsAssociationEnd() throws Exception {
    generic.isAssociationEnd(classType,attribute,host);
}

/** 
* 
* Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testNameAssociation() throws Exception {
    AssociationEnd end = mock(AssociationEnd.class);
    generic.nameAssociation(end,end,host);
}

/** 
* 
* Method: tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host) 
* 
*/ 
@Test(expected = UnsupportedOperationException.class)
public void testTagsFromAppInfo() throws Exception {
    XmlSchemaAppInfo appInfo = mock(XmlSchemaAppInfo.class);
    generic.tagsFromAppInfo(appInfo,host);
}


} 
