//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import openadk.library.Element;

import org.apache.commons.jxpath.ri.model.NodePointer;


class SurrogateSimpleFieldPointer extends SurrogateElementPointer<Element> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1254762288956897540L;

	protected SurrogateSimpleFieldPointer(NodePointer parent, String fauxName, Element pointedNode) {
		super(parent, fauxName, pointedNode);
	}

	protected SurrogateSimpleFieldPointer(NodePointer parent, String fauxName ) {
		super(parent, fauxName);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		setFieldValue( getElement(), value );
	}

}
