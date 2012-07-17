//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;


import openadk.library.tools.cfg.ADKConfigException;
import openadk.util.XMLUtils;

import org.w3c.dom.*;

/**
 *  Encapsulates an &lt;OtherId&gt; field mapping
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class OtherIdMapping
{
	protected String fType;
	protected String fPrefix;
	protected Node fNode;

	/**
	 *  Constructor
	 */
	protected OtherIdMapping()
	{
		this( null ,null, null );
	}

	/**
	 *  Constructor
	 *  @param type The value of the OtherId/@Type attribute to match (e.g. 'ZZ')
	 *  @param prefix The prefix value to match (e.g. 'GRADE:')
	 */
    public OtherIdMapping( String type, String prefix )
	{
		this( type, prefix, null );
	}

    public OtherIdMapping( String type, String prefix, Node node )
	{
		fType = type;
		fPrefix = prefix;
		fNode = node;
    }

	/**
	 *  Produces a duplicate of this Rule object<p>
	 *
	 *  @return A "deep copy" of this Rule object
	 */
	public OtherIdMapping copy()
	{
		OtherIdMapping m = new OtherIdMapping();
		m.fType = fType;
		m.fPrefix = fPrefix;

		//  Copy the DOM Node
		m.fNode = fNode == null ? null : fNode.cloneNode( false );

		return m;
	}
	
	
	public static OtherIdMapping fromXML( 
			ObjectMapping parent, 
			FieldMapping field, 
			Element element )
		throws ADKConfigException
	{
		//  The OtherId type= attribute is required
		String type = XMLUtils.getAttribute( element, "type" );
		if( type == null )
			type =  XMLUtils.getAttribute( element,  "Type" );
		if( type == null )
			throw new ADKConfigException( "Field mapping rule " + parent.getObjectType() + "." + field.getFieldName() + " specifies an <OtherId> without a 'type' attribute" );
			
		//	The OtherId prefix= attribute is required
		String prefix =  XMLUtils.getAttribute( element,  "prefix" );
		if( prefix == null )
			prefix =  XMLUtils.getAttribute( element,  "Prefix" );
		if( prefix == null )
			throw new ADKConfigException( "Field mapping rule " + parent.getObjectType() + "." + field.getFieldName() + " specifies an <OtherId> without a 'prefix' attribute" );

		//  Create a new OtherIdMapping as a child of the FieldMapping
		OtherIdMapping id = new OtherIdMapping( type, prefix, element );
		return id;
	}

	/**
	 *  Sets the &lt;OtherId&gt; Type attribute value that must be present
	 *  for this rule to evaluate true
	 *  @param type The value of the Type attribute that must be present for
	 *      this rule to evaluate true (e.g. "ZZ", "06", etc.)
	 */
	public void setType( String type )
	{
		fType = type;

		if( fNode != null && type != null )
			XMLUtils.setAttribute( fNode, "type", type );
	}

	/**
	 *  Gets the &lt;OtherId&gt; Type attribute value that must be present
	 *  for this rule to evaluate true
	 *  @return The value of the Type attribute that must be present for
	 *      this rule to evaluate true (e.g. "ZZ", "06", etc.)
	 */
	public String getType()
	{
		return fType;
	}

	/**
	 *  Gets the optional OtherId <i>prefix</i> string that must be present at
	 *  the beginning of the OtherId value for this rule to evaluate true.
	 *  Prefix strings are not officially part of the SIF 1.0 specification but
	 *  are typically agreed upon by vendors and used with Type "ZZ"
	 *  (e.g. <code>&lt;OtherId Type="ZZ"&gt;GRADE:8&lt;/OtherId&gt;</code>)
	 *
	 *  @param a prefix string agreed upon by two or more agents to identify
	 *      multiple &lt;OtherId&gt; instances (e.g. "HOMEROOM:", "BARCODE:",
	 *      "GRADE:", etc.)
	 */
	public void setPrefix( String prefix )
	{
		fPrefix = prefix;

		if( fNode != null && prefix != null )
			XMLUtils.setAttribute( fNode, "prefix", prefix );
	}

	/**
	 *  Gets the optional OtherId <i>prefix</i> string that must be present at
	 *  the beginning of the OtherId value for this rule to evaluate true.
	 *  @return The prefix string agreed upon by two or more agents to identify
	 *      multiple &lt;OtherId&gt; instances (e.g. "HOMEROOM:", "BARCODE:",
	 *      "GRADE:", etc.)
	 */
	public String getPrefix()
	{
		return fPrefix;
	}
}
