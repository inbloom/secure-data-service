//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.SIFVersion;

/**
 *  Classes that implement the DTD interface provide information about the
 *  schema of elements and attributes. For example, the SIFDTD class implements
 *  this interface to define all elements comprising the Schools Interoperability
 *  Framework.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface DTD
{
	/**
	 *  Lookup an ElementDef object describing an element or attribute<p>
	 *  @param key The name of the element in the form "parent_field", where
	 *      <i>parent</i> is the name of the parent element and <i>field</i> is
	 *      the name of the child element or attribute (e.g. "SIF_Ack_SIF_Header",
	 *      "StudentPersonal_Name", etc.)
	 *  @return The ElementDef that provides metadata about the requested
	 *      element or null if not found
	 */
    public ElementDef lookupElementDef( String key );
    
    /**
     * Lookup an ElementDef object describing an element or attribute
     * @param parent The parent ElementDef
     * @param childTag the name of the child element
     * @return The ElementDef that provides metadata about the requested
	 *      element or null if not found
     */
    public ElementDef lookupElementDef( ElementDef parent, String childTag );
    
    
	/**
	 *  Gets the namespace associated with this DTD<p>
	 *  @param version The SIF Version
	 *  @return The namespace (e.g. the "xmlns" value for SIF_Messages)
	 */
	public String getNamespace( SIFVersion version );
	
	public SIFFormatter getFormatter( SIFVersion version );
	
	
	/**
	 *  Gets the element tag corresponding to a type ID<p>
	 *  @param type A type identifier defined by the class that implements this
	 *      interface (e.g. <code>SIFDTD.MSGTYP_PROVIDE</code>)
	 *  @return The tag name of the element (e.g. "SIF_Provide")
	 */
	public String getElementTag( byte type );

	/**
	 *  Gets the type ID corresponding to an element tag name<p>
	 *  @param name The tag name of an element (e.g. "SIF_Provide")
	 *  @return A type identifier defined by the class that implements this
	 *      interface (e.g. <code>SIFDTD.MSGTYP_PROVIDE</code>)
	 */
	public byte getElementType( String name );
}
