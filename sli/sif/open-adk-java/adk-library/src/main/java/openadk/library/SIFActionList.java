//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Represents a repeatable element list in SIF, in which each item
 * is identifed with a key. Elements in the list that are deleted
 * can be identified by the presence of a SIF_Action element with the
 * value of "Delete"
 * 
 * @author Andrew Elmhorst
 * @Version ADK 2.0
 *
 */
public class SIFActionList<T extends SIFKeyedElement> extends SIFKeyedList<T> {

	public SIFActionList(ElementDef def) {
		super(def);
		// TODO Auto-generated constructor stub
	}
}
