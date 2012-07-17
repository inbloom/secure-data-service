//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import openadk.util.XMLUtils;

import org.w3c.dom.*;


import java.io.Serializable;

/**
 *  An entry in a ValueSet.<p>
 *
 *  Each ValueSet entry describes a mapping between a local application value
 *  and a SIF value. Additional fields include a display title and display order
 *  for presenting ValueSet entries in a user interface.
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class ValueSetEntry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6514144585555510962L;

	/**
	 *  The name of this entry (i.e. the application-defined value)
	 */
	public String name;

	/**
	 *  The value of this entry (i.e. the corresponding SIF-defined value)
	 */
	public String value;

	/**
	 *  An optional title for displaying ValueSetEntries in a user interface
	 */
	public String title;

	/**
	 *  An optional display order for arranging ValueSetEntries in a user interface
	 */
	public int displayOrder;

	/**
	 *  The optional DOM Node that defines this ValueSetEntry in the configuration file
	 */
	public transient Node node;


	/**
	 *  Constructor
	 *  @param name The name of this entry (i.e. the application-defined value)
	 *  @param value The value of this entry (i.e. the corresponding SIF-defined value)
	 *  @param title An optional title for displaying ValueSetEntries in a user interface
	 */
	public ValueSetEntry( String name, String value, String title )
	{
		this( name, value, title, 0 );
	}

	/**
	 *  Constructor
	 *  @param name The name of this entry (i.e. the application-defined value)
	 *  @param value The value of this entry (i.e. the corresponding SIF-defined value)
	 *  @param title An optional title for displaying ValueSetEntries in a user interface
	 *  @param displayOrder The order in which this item should be displayed in a list
	 */
	public ValueSetEntry( String name, String value, String title, int displayOrder )
	{
		this.name=name;
		this.value=value;
		this.title=title;
		this.displayOrder = displayOrder;
	}
	
	/**
	 * Writes this valueset entry to XML
	 * @param element
	 */
	public void toXml( Element element ) 
	{
		element.setAttribute( "name", name );
		XMLUtils.setOrRemoveAttribute( element, "title", title );
		XMLUtils.setText( element, value );
	}
}
