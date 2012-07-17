//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import javax.xml.parsers.ParserConfigurationException;

import openadk.library.SIFElement;
import openadk.library.SIFVersion;
import openadk.library.common.SIF_ExtendedElement;
import openadk.library.tools.xpath.SIFElementPointer.AddChildDirective;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.dom.DOMNodePointer;


/**
 * Represents a NodePointer for a SIFElement
 *
 * @author Stephen Miller
 * @version ADK 2.0
 */
public class SIFExtendedElementPointer extends DOMNodePointer {
	/**
	 *
	 */
	private static final long serialVersionUID = -2805505553878070975L;

	protected SIFVersion fVersion;
	protected SIF_ExtendedElement fSIFElement;

	/**
	 * @param parentpointer
	 * @param element
	 * @param version
	 * @throws ParserConfigurationException 
	 */
	public SIFExtendedElementPointer(NodePointer parentpointer, SIF_ExtendedElement element,
			SIFVersion version) {
		super(parentpointer, element.getXML());
		this.setNamespaceResolver(parentpointer.getNamespaceResolver());
		fSIFElement = element;
		fVersion = version;
	}

	
	/*
	 * 
	 */
	public SIFVersion getVersion()
	{
		return fVersion;
	}
	
	@Override 
	public Object getValue() {
		return fSIFElement.getSIFValue();
	}
	
	/*
	 * 
	 */
	public boolean isLegacyVersion()
	{
		return fVersion != null && fVersion.compareTo(SIFVersion.SIF20) < 0;
	}

	/**
	 * Use by SIFXPathContext when determining if it can add a child
	 * or not.
	 * @param childName
	 * @return True if a child should be added, false if it already exists
	 */
	public AddChildDirective getAddChildDirective( QName childName ){

		return SIFElementPointer.AddChildDirective.ADD;
	}


	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.dom.DOMNodePointer#createChild(org.apache.commons.jxpath.JXPathContext, org.apache.commons.jxpath.ri.QName, int)
	 */
	@Override
	public NodePointer createChild(JXPathContext context, QName name, int index) {
		// TODO Auto-generated method stub
		return super.createChild(context, name, index);
	}


	/* (non-Javadoc)
	 * @see org.apache.commons.jxpath.ri.model.dom.DOMNodePointer#createChild(org.apache.commons.jxpath.JXPathContext, org.apache.commons.jxpath.ri.QName, int, java.lang.Object)
	 */
	@Override
	public NodePointer createChild(JXPathContext context, QName name,
			int index, Object value) {
		// TODO Auto-generated method stub
		return super.createChild(context, name, index, value);
	}
	
	
}
