//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;

public class SingleNodeIterator implements NodeIterator {

	private NodePointer fPointer;
	private int fPosition = 0;
	
	public SingleNodeIterator( NodePointer iteratedItem ){
		fPointer = iteratedItem;
	}
	public int getPosition() {
		return fPosition;
	}

	public boolean setPosition(int position) {
		fPosition = position;
		return fPosition == 1;
	}

	public NodePointer getNodePointer() {
		return fPointer;
	}
	
}
