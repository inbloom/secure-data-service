//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import openadk.library.Element;

import org.apache.commons.jxpath.ri.model.NodePointer;


abstract class SurrogateElementPointer<T extends Element> extends FauxElementPointer 
{
	private T fElement;
	
	protected SurrogateElementPointer(NodePointer parent, String fauxName ) {
		super(parent, fauxName);
	}
	
	protected SurrogateElementPointer(NodePointer parent, String fauxName, T pointedNode ) {
		super(parent, fauxName);
		fElement = pointedNode;
	}

	@Override
	public Object getBaseValue() {
		return fElement;
	}
	
	protected void setElement( T node ){
		fElement = node;
	}
	
	protected T getElement(  ){
		return fElement;
	}

	@Override
	public Object getImmediateNode() {
		return fElement;
	}

}
