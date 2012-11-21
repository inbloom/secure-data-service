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

package org.slc.sli.modeling.waudit;

import org.junit.Test;
import org.slc.sli.modeling.uml.AssociationEnd;
import org.slc.sli.modeling.uml.ClassType;
import org.slc.sli.modeling.uml.DataType;
import org.slc.sli.modeling.uml.EnumType;
import org.slc.sli.modeling.uml.Generalization;
import org.slc.sli.modeling.uml.Identifier;
import org.slc.sli.modeling.uml.ModelElement;
import org.slc.sli.modeling.uml.TagDefinition;
import org.slc.sli.modeling.uml.Type;
import org.slc.sli.modeling.uml.Visitor;
import org.slc.sli.modeling.uml.index.ModelIndex;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WadlAuditConfigTest {

    private static ModelIndex modelIndex = new ModelIndex() {

        @Override
        public List<AssociationEnd> getAssociationEnds(Identifier type) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<String, ClassType> getClassTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Map<QName, DataType> getDataTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Iterable<EnumType> getEnumTypes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Generalization> getGeneralizationBase(Identifier derived) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Generalization> getGeneralizationDerived(Identifier base) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getNamespaceURI(Type type) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public TagDefinition getTagDefinition(Identifier reference) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public TagDefinition getTagDefinition(QName name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Type getType(Identifier reference) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void lookup(Identifier id, Visitor visitor) {
            // TODO Auto-generated method stub

        }

        @Override
        @Deprecated
        public Set<ModelElement> lookupByName(QName name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Set<ModelElement> whereUsed(Identifier id) {
            // TODO Auto-generated method stub
            return null;
        }

    };

    private static final Map<String, QName> elementNames = new HashMap<String, QName>();

    static {
        elementNames.put("foo1", null);
        elementNames.put("foo2", null);
    }

    private static String prefix = "foo";
    private static String namespaceURI = "bar";

    @Test
    public void test() {


        WadlAuditConfig wadlAuditConfig = new WadlAuditConfig(prefix, namespaceURI, modelIndex, elementNames);


        assertEquals(prefix, wadlAuditConfig.getPrefix());
        assertEquals(namespaceURI, wadlAuditConfig.getNamespaceURI());
        assertTrue(modelIndex == wadlAuditConfig.getModel());
        assertEquals(elementNames, wadlAuditConfig.getElementNameMap());
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullPrefixThrowsException() {
        new WadlAuditConfig(null, namespaceURI, modelIndex, elementNames);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNamespaceUriThrowsException() {
        new WadlAuditConfig(prefix, null, modelIndex, elementNames);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullModelIndexThrowsException() {
        new WadlAuditConfig(prefix, namespaceURI, null, elementNames);
    }

    @Test(expected = NullPointerException.class)
    public void testNullElementNamesThrowsException() {
        new WadlAuditConfig(prefix, namespaceURI, modelIndex, null);
    }


}
