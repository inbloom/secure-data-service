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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

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

public class WadlAuditConfigTest {
	
	private final static ModelIndex MODEL_INDEX = new ModelIndex() {

		@Override
		public List<AssociationEnd> getAssociationEnds(Identifier type) {
			// no op
			return null;
		}

		@Override
		public Map<String, ClassType> getClassTypes() {
			// no op
			return null;
		}

		@Override
		public Map<QName, DataType> getDataTypes() {
			// no op
			return null;
		}

		@Override
		public Iterable<EnumType> getEnumTypes() {
			// no op
			return null;
		}

		@Override
		public List<Generalization> getGeneralizationBase(Identifier derived) {
			// no op
			return null;
		}

		@Override
		public List<Generalization> getGeneralizationDerived(Identifier base) {
			// no op
			return null;
		}

		@Override
		public String getNamespaceURI(Type type) {
			// no op
			return null;
		}

		@Override
		public TagDefinition getTagDefinition(Identifier reference) {
			// no op
			return null;
		}

		@Override
		public TagDefinition getTagDefinition(QName name) {
			// no op
			return null;
		}

		@Override
		public Type getType(Identifier reference) {
			// no op
			return null;
		}

		@Override
		public void lookup(Identifier id, Visitor visitor) {
			// no op
			
		}

		@Override
		@Deprecated
		public Set<ModelElement> lookupByName(QName name) {
			// no op
			return null;
		}

		@Override
		public Set<ModelElement> whereUsed(Identifier id) {
			// no op
			return null;
		}
		
	};
	
	private static final Map<String, QName> ELEMENT_NAMES = new HashMap<String, QName>();
	static {
		ELEMENT_NAMES.put("foo1", null);
		ELEMENT_NAMES.put("foo2", null);
	}
	
	private static String prefix = "foo";
	private static String namespaceURI = "bar";

	@Test
	public void test() {
		
		
		WadlAuditConfig wadlAuditConfig = new WadlAuditConfig(prefix, namespaceURI, MODEL_INDEX, ELEMENT_NAMES);
		

		assertEquals(prefix, wadlAuditConfig.getPrefix());
		assertEquals(namespaceURI, wadlAuditConfig.getNamespaceURI());
		assertTrue(MODEL_INDEX == wadlAuditConfig.getModel());
		assertEquals(ELEMENT_NAMES, wadlAuditConfig.getElementNameMap());
	}
	

	@Test (expected = IllegalArgumentException.class)
	public void testNullPrefixThrowsException() {
		new WadlAuditConfig(null, namespaceURI, MODEL_INDEX, ELEMENT_NAMES);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullNamespaceUriThrowsException() {
		new WadlAuditConfig(prefix, null, MODEL_INDEX, ELEMENT_NAMES);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testNullModelIndexThrowsException() {
		new WadlAuditConfig(prefix, namespaceURI, null, ELEMENT_NAMES);
	}

	@Test (expected = NullPointerException.class)
	public void testNullElementNamesThrowsException() {
		new WadlAuditConfig(prefix, namespaceURI, MODEL_INDEX, null);
	}
	
	
}
