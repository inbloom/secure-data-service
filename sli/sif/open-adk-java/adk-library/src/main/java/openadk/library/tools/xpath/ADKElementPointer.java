//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import openadk.library.*;
import openadk.library.impl.ElementSorter;
import openadk.library.impl.surrogates.RenderSurrogate;

import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.NodeTypeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * A base class for ADK NodePointer instances
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
public abstract class ADKElementPointer extends NodePointer {

	protected ElementDef fElementDef;

	protected Element fElement;

	private SIFVersion fVersion;

	protected ADKElementPointer(NodePointer parentPointer, Element element,
			SIFVersion version) {
		super(parentPointer);
		fVersion = version;
		fElement = element;
		fElementDef = element.getElementDef();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#getBaseValue()
	 */
	@Override
	public Object getBaseValue() {

		return fElement;
	}

	/**
	 * The SIFVersion in effect for this object
	 * 
	 * @return The SIFVersion being used for this element
	 */
	public SIFVersion getVersion() {
		return fVersion;
	}

	@Override
	public int compareChildNodePointers(NodePointer arg0, NodePointer arg1) {
		ADKElementPointer enp1 = (ADKElementPointer) arg0;
		ADKElementPointer enp2 = (ADKElementPointer) arg1;

		return ElementSorter.getInstance(fVersion).compare(enp1.fElement,
				enp2.fElement);
	}

	@Override
	public Object getImmediateNode() {
		return fElement;
	}

	@Override
	public QName getName() {
		return new QName(null, fElementDef.tag(fVersion));
	}

	protected boolean testElement(Element node, NodeTest test) {
		return testElement(node, test, fVersion);
	}

	static boolean testElement(Element node, NodeTest test, SIFVersion version) {
		if (test == null) {
			return true;
		}

		ElementDef def = node.getElementDef();
		ElementVersionInfo evi = def.getVersionInfo( version );
		

		if (test instanceof NodeNameTest) {
			
			if (evi.isAttribute()) {
				return false;
			}

			NodeNameTest nodeNameTest = (NodeNameTest) test;
			QName testName = nodeNameTest.getNodeName();
			// TODO: For now we don't support namespaces in the ADK
			// String namespaceURI = nodeNameTest.getNamespaceURI();
			boolean wildcard = nodeNameTest.isWildcard();
			String testPrefix = testName.getPrefix();
			if (wildcard && testPrefix == null) {
				return true;
			}
			
			// If there is a render surrogate assigned to this ElementDef
			// for this version of SIF, do a match against the legacy
			// path that the surrogate represents
			if( version.getMajor() < 2 ){
				RenderSurrogate surrogate = evi.getSurrogate();
				if( surrogate != null ){
					return surrogate.getPath().startsWith( testName.getName() );
				}
			}
			

			if ( wildcard || testName.getName().equals( evi.getTag())) {
				// TODO: For now we don't support namespaces in the ADK
				// String nodeNS = DOMNodePointer.getNamespaceURI(node);
				// return equalStrings(namespaceURI, nodeNS);
				return true;
			}
		} else if (test instanceof NodeTypeTest) {
			switch (((NodeTypeTest) test).getNodeType()) {
			case Compiler.NODE_TYPE_NODE:
				// The test to determine whether this is an attribute
				// or not has already succeeded
				return !def.isField();
			case Compiler.NODE_TYPE_TEXT:
				return def.isField();
			case Compiler.NODE_TYPE_COMMENT:
				return false;
			case Compiler.NODE_TYPE_PI:
				return false;
			}
			return false;
		}
		return false;
	}

	protected SIFSimpleType getSIFSimpleTypeValue(ElementDef def, Object rawValue) {
		if( rawValue instanceof SIFSimpleType ){
			return (SIFSimpleType)rawValue;
		}
		SIFSimpleType sst = def.getTypeConverter().getSIFSimpleType( rawValue );
		return sst;
	}

	/**
	 * @return True if the SIFVersion is set to less than SIF20
	 */
	public boolean isLegacyVersion(){
		return fVersion.getMajor() < 2;
	}
	
	

}
