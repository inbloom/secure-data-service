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
	

	@Test (expected = NullPointerException.class)
	public void testNullPrefixThrowsException() {
		new WadlAuditConfig(null, namespaceURI, modelIndex, elementNames);
	}

	@Test (expected = NullPointerException.class)
	public void testNullNamespaceUriThrowsException() {
		new WadlAuditConfig(prefix, null, modelIndex, elementNames);
	}

	@Test (expected = NullPointerException.class)
	public void testNullModelIndexThrowsException() {
		new WadlAuditConfig(prefix, namespaceURI, null, elementNames);
	}

	@Test (expected = NullPointerException.class)
	public void testNullElementNamesThrowsException() {
		new WadlAuditConfig(prefix, namespaceURI, modelIndex, null);
	}
	
	
}
