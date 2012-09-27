package org.slc.sli.modeling.xmigen;

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.xsd.WxsNamespace;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/** 
* Xsd2UmlPluginDefault Tester. 
* 
* @author <Authors name> 
* @since <pre>Sep 11, 2012</pre> 
* @version 1.0 
*/ 
public class Xsd2UmlPluginDefaultTest {
    private Xsd2UmlPluginDefault defaultPlugin;
    private QName complexType;
    private QName name;

    @Before
public void before() throws Exception {
    defaultPlugin = new MockXsd2UmlPluginDefault();
    complexType = new QName(WxsNamespace.URI,"complexType");
    name = new QName(WxsNamespace.URI,"name");
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: nameFromComplexTypeExtension(final QName complexType, final QName base) 
* 
*/ 
@Test
public void testNameFromComplexTypeExtension() throws Exception {

    String extension = defaultPlugin.nameFromComplexTypeExtension(complexType,name);
    assertEquals("complexType extends name", extension);
}
@Test(expected = NullPointerException.class)
public void testValidateComplexType() throws Exception{
    defaultPlugin.nameFromComplexTypeExtension(null,name);
}
    @Test(expected = NullPointerException.class)
    public void testValidateBaseType() throws Exception{
        defaultPlugin.nameFromComplexTypeExtension(name,null);
    }

/** 
* 
* Method: nameFromSchemaAttributeName(final QName name) 
* 
*/ 
@Test
public void testNameFromSchemaAttributeName() throws Exception {
    assertEquals("name",defaultPlugin.nameFromSchemaAttributeName(name));
}
    @Test(expected = NullPointerException.class)
    public void testValidNameFromSchemaAttributeName() throws Exception {
        defaultPlugin.nameFromSchemaAttributeName(null);
    }

/** 
* 
* Method: nameFromSchemaElementName(final QName name)
* 
*/ 
@Test
public void testNameFromSchemaElementName() throws Exception {
    assertEquals("name",defaultPlugin.nameFromSchemaElementName(name));
}

@Test(expected = NullPointerException.class)
public void testValidNameFromSchemaElementName() throws Exception {
    defaultPlugin.nameFromSchemaElementName(null);
}
/**
* 
* Method: nameFromSchemaTypeName(final QName name)
* 
*/ 
@Test
public void testNameFromSchemaTypeName() throws Exception {
    assertEquals("name",defaultPlugin.nameFromSchemaTypeName(name));
}
    @Test(expected = NullPointerException.class)
    public void testValidNameFromSchemaTypeName() throws Exception {
        defaultPlugin.nameFromSchemaTypeName(null);
    }
/**
* 
* Method: nameFromSimpleTypeRestriction(final QName simpleType, final QName base)
* 
*/ 
@Test
public void testNameFromSimpleTypeRestriction() throws Exception {
    String restriction = defaultPlugin.nameFromSimpleTypeRestriction(complexType,name);
    assertEquals("complexType restricts name", restriction);
}
    @Test(expected = NullPointerException.class)
    public void testValidNameFromSimpleTypeRestriction() throws Exception{
        defaultPlugin.nameFromSimpleTypeRestriction(null,name);
    }
    @Test(expected = NullPointerException.class)
    public void testvalidNameFromBaseTypeRestriction() throws Exception{
        defaultPlugin.nameFromSimpleTypeRestriction(name,null);
    }

}
class MockXsd2UmlPluginDefault extends Xsd2UmlPluginDefault {

    public Collection<TagDefinition> declareTagDefinitions(Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }
    public String getAssociationEndTypeName(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host){
        return "association";
    }
    public boolean isAssociationEnd(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host){
        return true;
    }
    public List<TaggedValue> tagsFromAppInfo(XmlSchemaAppInfo appInfo, Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }

    public String nameAssociation(AssociationEnd lhs, AssociationEnd rhs, Xsd2UmlPluginHost host){
        return lhs.getName() + " <=> " + rhs.getName();
    }
}
