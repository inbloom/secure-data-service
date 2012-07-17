//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import openadk.library.*;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * Implements an XPath iterator for attributes on SIF Elements
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 * 
 */
class ADKAttributeIterator extends ADKNodeIterator {

	/**
	 * Creates an ADK Attribute Iterator
	 * 
	 * @param parent
	 * @param name
	 */
	ADKAttributeIterator(SIFElementPointer parent, QName name) {
		super(parent);

		SIFVersion version = parent.getVersion();

		SIFElement node = (SIFElement) parent.getNode();
		String simpleName = name.getName();
		if (simpleName.equals("*")) {
			// Capture all fields
			for (SimpleField field : node.getFields()) {
				ElementDef fieldDef = field.getElementDef();
				if (fieldDef.isSupported(version)
						&& fieldDef.isAttribute(version)) {
					this.addNodeToIterate(field);
				}
			}

		} else {
			for( SimpleField sf : node.getFields() ){
				ElementDef def = sf.getElementDef();
				ElementVersionInfo evi = def.getVersionInfo( parent.getVersion() );
				if( evi == null ){
					// This element or attribute does not exist in this version of SIF
					continue;
				}
				if( evi.isAttribute() && simpleName.equals( evi.getTag() ) )
				{
					this.addNodeToIterate( sf );
				}	
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see custom.ADKNodeIterator#getNodePointer(custom.SIFElementPointer,
	 *      com.edustructures.sifworks.Element)
	 */
	@Override
	protected NodePointer getNodePointer(SIFElementPointer parent,
			Element element) {
		return SimpleFieldPointer.create(parent, (SimpleField) element);
	}

}
