//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import java.util.Locale;

import openadk.library.ADK;
import openadk.library.SIFElement;
import openadk.library.SIFFormatter;
import openadk.library.SIFVersion;
import openadk.util.ADKStringUtils;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;


/**
 * Creates new instances of ElementPointers when building XPaths
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
class ElementPointerFactory implements NodePointerFactory {

	/**
	 * Creates a new instance of ElementPointerFactory
	 */
	public ElementPointerFactory() {
	}

	public int getOrder() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.QName,
	 *      java.lang.Object, java.util.Locale)
	 */
	public NodePointer createNodePointer(QName name, Object obj, Locale locale) {
		if (obj instanceof SIFElement) {
			return createSIFElementPointer(null, name, (SIFElement) obj);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodePointerFactory#createNodePointer(org.apache.commons.jxpath.ri.model.NodePointer,
	 *      org.apache.commons.jxpath.ri.QName, java.lang.Object)
	 */
	public NodePointer createNodePointer(NodePointer parentPointer, QName name,
			Object obj) {
		if (obj instanceof SIFElement) {
			return createSIFElementPointer(parentPointer, name, (SIFElement) obj);
		}
		return null;
	}

	/**
	 * @param parentPointer
	 * @param element
	 * @return a NodePointer
	 */
	private NodePointer createSIFElementPointer(
			NodePointer parentPointer, 
			QName name,
			SIFElement element) {
		
		
		SIFVersion version = null;
		if( parentPointer instanceof ADKElementPointer ){
			version = ((ADKElementPointer)parentPointer).getVersion();
		}
		if( version == null ){
			version = element.getSIFVersion();
			if (version == null) {
				version = SIFVersion.LATEST;
			}
		}
		return SIFElementPointer.create(parentPointer, element, version);
	}
}
