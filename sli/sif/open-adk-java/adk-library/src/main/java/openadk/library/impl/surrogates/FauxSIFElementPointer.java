//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import openadk.library.Element;
import openadk.library.tools.xpath.SingleNodeIterator;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * @author Andy
 *
 */
 class FauxSIFElementPointer extends FauxElementPointer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4982299661528481266L;
	
	private NodePointer fChild;
	private String fChildName;
	private boolean fChildIsAttribute;
	
	
	/**
	 * @param parent
	 * @param fauxName
	 */
	public FauxSIFElementPointer(
			NodePointer parent, 
			String fauxName ){ 
		super(parent, fauxName);
	}

	/**
	 * @param singleChild
	 * @param childName
	 * @param childIsAttribute
	 */
	public void setChild(	NodePointer singleChild, 
			String childName,
			boolean childIsAttribute ) {
	
		fChild = singleChild;
		fChildName = childName;
		fChildIsAttribute = childIsAttribute;
	}
		 
	
	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#attributeIterator(org.apache.commons.jxpath.ri.QName)
	 */
	@Override
	public NodeIterator attributeIterator(QName qname) {
		if( fChildIsAttribute && childNameMatches( qname ) ){
			return new SingleNodeIterator(fChild );
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.NodePointer#createAttribute(org.apache.commons.jxpath.JXPathContext, org.apache.commons.jxpath.ri.QName)
	 */
	@Override
	public NodePointer createAttribute(JXPathContext context, QName name) {
		if( fChildIsAttribute && childNameMatches( name ) ){
			return fChild;
		}
		return super.createAttribute( context, name );
			
	}
	
	
	
	@Override
	public NodeIterator childIterator(NodeTest test, boolean reverse, NodePointer startWith) {
		if( fChildIsAttribute ){
			return null;
		}
		if( test instanceof NodeNameTest ){
			NodeNameTest nnt = (NodeNameTest)test;
			if( nnt.isWildcard() || childNameMatches( nnt.getNodeName() ) ){
				return new SingleNodeIterator( fChild );
			} else {
				return null;
			}
		}
		return new SingleNodeIterator( fChild );
	}
	
	private boolean childNameMatches( QName qname )
	{
		String localName = qname.getName();
		return localName.equals( fChildName ) || localName.equals( "*" );
	}
	
	

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Object getBaseValue() {
		return null;
	}

	@Override
	public Object getImmediateNode() {
		// This object doesnt actually represent "anything",
		// but return the child so that it maps to something
		return fChild.getNode();
	}

	@Override
	public void setValue(Object value) {
		Object childObj = fChild.getNode();
		if( childObj != null && childObj instanceof Element ){
			setFieldValue( (Element)childObj, value );
		}
	}
	
	@Override
	public NodePointer createChild(JXPathContext context, QName name, int index) {
		// Do nothing. The child has already been created
		return fChild;
	}

}
