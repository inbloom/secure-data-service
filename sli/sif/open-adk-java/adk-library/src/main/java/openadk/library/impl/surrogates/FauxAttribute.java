//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class FauxAttribute extends FauxElementPointer implements NodeIterator {

	/**
	 *
	 */
	private static final long serialVersionUID = -6257363323404719642L;
	String fValue;
	public FauxAttribute(NodePointer parent, String fauxName, String value ) {
		super(parent, fauxName);
		fValue = value;
	}

	private int fPosition;

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Object getBaseValue() {
		return fValue;
	}

	@Override
	public Object getImmediateNode() {
		return fValue;
	}

	@Override
	public void setValue(Object value) {
		fValue = value.toString();
	}

	public int getPosition() {
		return fPosition;
	}

	public boolean setPosition(int position) {
		fPosition = position;
		return fPosition == 1;
	}

	public NodePointer getNodePointer() {
		if( fPosition == 1 ){
			return this;
		}
		return null;
	}

}
