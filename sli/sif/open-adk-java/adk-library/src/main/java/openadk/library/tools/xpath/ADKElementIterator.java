//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import java.util.ArrayList;
import java.util.List;

import openadk.library.Element;
import openadk.library.SIFElement;
import openadk.library.SIFFormatter;
import openadk.library.SimpleField;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * An XPath iterator for ADK Elements
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
class ADKElementIterator extends ADKNodeIterator {

	/**
	 * Creates an ADKElement iterator
	 * 
	 * @param parent
	 *            The parent iterator
	 * @param nodesToIterate
	 *            A list of children to iterate over
	 */
	ADKElementIterator(SIFElementPointer parent, List<Element> nodesToIterate) {
		super(parent, nodesToIterate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see custom.ADKNodeIterator#getNodePointer(custom.SIFElementPointer,
	 *      com.edustructures.sifworks.Element)
	 */
	@Override
	protected NodePointer getNodePointer(SIFElementPointer parent,	Element element) {
		if( element instanceof SimpleField ){
			return SimpleFieldPointer.create( parent, (SimpleField)element );
		}else {
			return SIFElementPointer.create( parent, (SIFElement)element, parent.getVersion() );	
		}
		
	}

}
