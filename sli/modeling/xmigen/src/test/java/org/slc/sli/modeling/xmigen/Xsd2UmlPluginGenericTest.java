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

import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.mockito.Mock;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.Attribute;
import org.slc.sli.modeling.uml.ClassType;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Xsd2UmlPluginGeneric Tester.
 * 
 * @author <Authors name>
 * @since <pre>
 * Sep 11, 2012
 * </pre>
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
     * Method: getAssociationEndTypeName(final ClassType classType, final Attribute attribute, final
     * Xsd2UmlPluginHost host)
     * 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetAssociationEndTypeName() throws Exception {
        generic.getAssociationEndTypeName(classType, attribute, host);
    }
    
    /**
     * 
     * Method: isAssociationEnd(final ClassType classType, final Attribute attribute, final
     * Xsd2UmlPluginHost host)
     * 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testIsAssociationEnd() throws Exception {
        generic.isAssociationEnd(classType, attribute, host);
    }
    
    /**
     * 
     * Method: nameAssociation(final AssociationEnd lhs, final AssociationEnd rhs, final
     * Xsd2UmlPluginHost host)
     * 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testNameAssociation() throws Exception {
        AssociationEnd end = mock(AssociationEnd.class);
        generic.nameAssociation(end, end, host);
    }
    
    /**
     * 
     * Method: tagsFromAppInfo(final XmlSchemaAppInfo appInfo, final Xsd2UmlPluginHost host)
     * 
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testTagsFromAppInfo() throws Exception {
        XmlSchemaAppInfo appInfo = mock(XmlSchemaAppInfo.class);
        generic.tagsFromAppInfo(appInfo, host);
    }
    
}
