//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.impl.surrogates.RenderSurrogate;

/**
 * Represents information about an ElementDef that is specific to a version of SIF
 * @author Andrew
 *
 */
public interface ElementVersionInfo {
	
	/**
	 * The XML tag name to use for this version of SIF
	 * @return The XML Tag name that this element represents
	 */
	public String getTag();
	
	/**
	 * A RenderSurrogate instance, if necessary for rendering this element in this version of SIF
	 * @return The RenderSurrogate that is used to render this element
	 */
	public RenderSurrogate getSurrogate( );
	
	/**
	 * The Sequence number of this element in this version of SIF
	 * @return The sequence number this element has, compared to its peers 
	 */
	public int getSequence();
	
	
	/**
	 * Does this element "collapse" in this version of SIF? This happens with many of the
	 * list container elements in SIF 1.x
	 * @return True if this is element is collapsed (e.g. doesn't exist, but it's children do)
	 *  in this version of SIF
	 */
	public boolean isCollapsed();
	
	/**
	 * Does this ADK Element represent an XML element or attribute in this version of SIF?
	 * @return True if this Element represents an XML attribute in this version of SIF
	 */
	public boolean isAttribute();

	/**
	 * Is this element repeatable in this version of SIF?
	 * @return True if this element is repeatable in this version of SIF
	 */
	public boolean isRepeatable();

}
