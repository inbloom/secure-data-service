//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl.surrogates;

import javax.xml.stream.XMLStreamReader;

import openadk.library.*;
import openadk.util.XMLWriter;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.model.NodePointer;


/**
 * The Interface representing a class that acts as a surrogate for
 * reading and writing XML and navigating XPathss.
 *
 * A RenderSurrogate is assigned to ElementDefs that drastically changed
 * structure between SIF 1.5r1 and SIF 2.0.
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
public interface RenderSurrogate {

	/**
	 * Render this element using the underlying SIFWriter
	 * @param writer The XmlWriter to write to
	 * @param version The SIFVersion being written in
	 * @param o The Element to write
	 * @param formatter The SIFFormatter to use for rendering string values
	 * @throws SIFException
	 */
	void renderRaw(
			XMLWriter writer,
			SIFVersion version,
			Element o,
			SIFFormatter formatter )
	throws ADKException;

	/**
	 * Called by the parser when it is on an element it cannot parse. Multiple RenderSurrogates
	 * may be called during a failed parse operation. If the RenderSurrogate successfully parses
	 * the XML, it returns true
	 * @param reader An XmlStreamReader that is positioned on the current node
	 * @param version The SIFVersion being parsed
	 * @param parent The SIFElement that is the parent of this field
	 * @param formatter The SIFFormatter to use for parsing xsd datatypes
	 * @return True if the surrogates successfully parses the node. False if the node is not
	 * recognize by the surrogate.
	 * @throws SIFException If the XML cannot be read by this surrogate
	 */
	boolean readRaw(
			XMLStreamReader reader,
			SIFVersion version,
			SIFElement parent,
			SIFFormatter formatter )
	throws ADKParsingException;

//	/**
//	 * Called by the ADK SIF Query Pattern parser when it is attempting to resolve
//	 * an element def by a SIF Query pattern
//	 * @param sqp The SIF Query Pattern to use when looking for this object
//	 * @return The resolved ElementDef or NULL
//	 */
//	ElementDef lookupBySQP(String sqp);

	/**
	 * Called by the ADK XPath traversal code when it is attempting to create a
	 * child element in a legacy version of SIF
	 * @param parentPointer The parent SIFElement that the child will be added to
	 * @param version The SIFVersion in effect
	 * @param context The current JXPath context
	 * @return A NodePointer representing the child element
	 */
	public NodePointer createChild( NodePointer parentPointer, SIFFormatter formatter, SIFVersion version, JXPathContext context );

	/**
	 * Called by the ADK XPath traversal code when it is traversing the given element
	 * in a legace version of SIF
	 * @param parent The parent element pointer
	 * @param element The Element to create a node pointer for
	 * @param version The SIFVersion in effect
	 * @return A NodePointer representing the current element
	 */
	public NodePointer createNodePointer( NodePointer parentPointer, Element element, SIFVersion version);

	/**
	 * Gets the element name or path to the element in this version of SIF
	 * @return
	 */
	public String getPath();


}
