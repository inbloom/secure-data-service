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
import org.slc.sli.modeling.uml.*;
import org.slc.sli.modeling.xsd.WxsNamespace;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Xsd2UmlPluginDefault Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Sep 11, 2012</pre>
 */
public class Xsd2UmlPluginDefaultTest {
    private Xsd2UmlPluginDefault defaultPlugin;
    private QName complexType;
    private QName name;

    @Before
    public void before() throws Exception {
        defaultPlugin = new MockXsd2UmlPluginDefault();
        complexType = new QName(WxsNamespace.URI, "complexType");
        name = new QName(WxsNamespace.URI, "name");
    }


    /**
     * Method: nameFromComplexTypeExtension(final QName complexType, final QName base)
     */
    @Test
    public void testNameFromComplexTypeExtension() throws Exception {

        String extension = defaultPlugin.nameFromComplexTypeExtension(complexType, name);
        Assert.assertEquals("complexType extends name", extension);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateComplexType() throws Exception {
        defaultPlugin.nameFromComplexTypeExtension(null, name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateBaseType() throws Exception {
        defaultPlugin.nameFromComplexTypeExtension(name, null);
    }

    /**
     * Method: nameFromSchemaAttributeName(final QName name)
     */
    @Test
    public void testNameFromSchemaAttributeName() throws Exception {
        Assert.assertEquals("name", defaultPlugin.nameFromSchemaAttributeName(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidNameFromSchemaAttributeName() throws Exception {
        defaultPlugin.nameFromSchemaAttributeName(null);
    }

    /**
     * Method: nameFromSchemaElementName(final QName name)
     */
    @Test
    public void testNameFromSchemaElementName() throws Exception {
        Assert.assertEquals("name", defaultPlugin.nameFromSchemaElementName(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidNameFromSchemaElementName() throws Exception {
        defaultPlugin.nameFromSchemaElementName(null);
    }

    /**
     * Method: nameFromSchemaTypeName(final QName name)
     */
    @Test
    public void testNameFromSchemaTypeName() throws Exception {
        Assert.assertEquals("name", defaultPlugin.nameFromSchemaTypeName(name));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidNameFromSchemaTypeName() throws Exception {
        defaultPlugin.nameFromSchemaTypeName(null);
    }

    /**
     * Method: nameFromSimpleTypeRestriction(final QName simpleType, final QName base)
     */
    @Test
    public void testNameFromSimpleTypeRestriction() throws Exception {
        String restriction = defaultPlugin.nameFromSimpleTypeRestriction(complexType, name);
        Assert.assertEquals("complexType restricts name", restriction);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidNameFromSimpleTypeRestriction() throws Exception {
        defaultPlugin.nameFromSimpleTypeRestriction(null, name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testvalidNameFromBaseTypeRestriction() throws Exception {
        defaultPlugin.nameFromSimpleTypeRestriction(name, null);
    }

}

/**
 * Mock
 */
class MockXsd2UmlPluginDefault extends Xsd2UmlPluginDefault {

    public Collection<TagDefinition> declareTagDefinitions(Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }

    public String getAssociationEndTypeName(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host) {
        return "association";
    }

    public boolean isAssociationEnd(ClassType classType, Attribute attribute, Xsd2UmlPluginHost host) {
        return true;
    }

    public List<TaggedValue> tagsFromAppInfo(XmlSchemaAppInfo appInfo, Xsd2UmlPluginHost host) {
        return Collections.emptyList();
    }

    public String nameAssociation(AssociationEnd lhs, AssociationEnd rhs, Xsd2UmlHostedPlugin host) {
        return lhs.getName() + " <=> " + rhs.getName();
    }
}
