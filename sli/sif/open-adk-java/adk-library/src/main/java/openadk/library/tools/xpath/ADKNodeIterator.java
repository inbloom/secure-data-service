//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import java.util.ArrayList;
import java.util.List;

import openadk.library.*;

import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.model.NodeIterator;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * An iterator for a list of ADK element objects
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
abstract class ADKNodeIterator implements NodeIterator {

	private List<Element> fChildNodes;

	private SIFElementPointer fParent;

	private int fPosition = 0;

	/**
	 * Creates a new instance of ADKNodeIterator
	 * 
	 * @param parent
	 */
	protected ADKNodeIterator(SIFElementPointer parent) {
		fParent = parent;
		fChildNodes = new ArrayList<Element>();
	}

	/**
	 * Creates a new instance of ADKNodeIterator
	 * 
	 * @param parent
	 */
	protected ADKNodeIterator(SIFElementPointer parent,
			List<Element> nodesToIterate) {
		fParent = parent;
		fChildNodes = nodesToIterate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodeIterator#getNodePointer()
	 */
	public NodePointer getNodePointer() {
		if (fPosition == 0) {
			if (!setPosition(1)) {
				return null;
			}
		}
		int index = fPosition - 1;
		if (index > -1 && index <= (fChildNodes.size() - 1)) {
			return getNodePointer(fParent, fChildNodes.get(index));
		}
		// TODO: This is an exceptional case and perhaps should throw an
		// exception
		return null;
	}

	/**
	 * Called by subclasses when they have found a new node that should be
	 * iterated
	 * 
	 * @param node
	 *            The child node to add to the iteration list
	 */
	protected void addNodeToIterate(Element node) {
		fChildNodes.add(node);
	}

	/**
	 * Called on the subclass when a specific NodePointer is requested by JXPath
	 * 
	 * @param parent
	 *            The parent element
	 * @param element
	 *            The child node being requested
	 * @return A NodePointer to the requested child
	 */
	protected abstract NodePointer getNodePointer(SIFElementPointer parent,
			Element element);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodeIterator#getPosition()
	 */
	public int getPosition() {
		return fPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.commons.jxpath.ri.model.NodeIterator#setPosition(int)
	 */
	public boolean setPosition(int position) {
		this.fPosition = position;
		return position >= 1 && position <= fChildNodes.size();
	}

}
