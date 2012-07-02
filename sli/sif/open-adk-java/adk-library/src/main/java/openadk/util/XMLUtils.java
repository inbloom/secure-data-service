//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import org.w3c.dom.*;

import java.util.List;
import java.util.Vector;

/**
 *  Various static helper routines for manipulating DOM Documents.
 *
 *
 */
public class XMLUtils
{
	/**
	 *	Gets the text for a node by concatenating child TEXT elements.
	 */
	public synchronized static String getText( Node n )
	{
		if( n == null )
			throw new IllegalArgumentException("Node argument cannot be null");

		StringBuilder b = new StringBuilder();
		NodeList nl = n.getChildNodes();
		for( int i = 0; i < nl.getLength(); i++ ) 
		{
			if( nl.item(i).getNodeType() == Node.TEXT_NODE )
				b.append(nl.item(i).getNodeValue());
			else
			if( nl.item(i).getNodeType() == Node.CDATA_SECTION_NODE )
				b.append(nl.item(i).getNodeValue());
		}

		return b.toString().trim();
	}

	/**
	 *  Sets the text for a node
	 */
	public synchronized static void setText( Node n, String text )
	{
		if( n == null )
			throw new IllegalArgumentException("Node argument cannot be null");
		if( text == null )
			throw new IllegalArgumentException("Node text argument cannot be null");

		NodeList nl = n.getChildNodes();
		for( int i = 0; i < nl.getLength(); i++ ) {
			if( nl.item(i).getNodeType() == Node.TEXT_NODE ) {
				nl.item(i).setNodeValue( text );
				return;
			}
		}

		Node textNode = n.getOwnerDocument().createTextNode( text );
		n.appendChild( textNode );
	}

	/**
	 *  Gets a named attribute of a Node<p>
	 *  @param node The node to search
	 *  @param attr The name of the attribute to get
	 */
	public synchronized static String getAttribute( Node node, String attr )
	{
		if( node == null )
			throw new IllegalArgumentException("Node argument cannot be null");
		if( attr == null )
			throw new IllegalArgumentException("Node attribute argument cannot be null");

		NamedNodeMap map = node.getAttributes();
		if( map != null ) {
			Node an = map.getNamedItem(attr);
			if( an != null )
				return an.getNodeValue();
		}

		return null;
	}

	/**
	 *  Sets a named attribute of a Node
	 *  @param node The node
	 *  @param attr The name of the attribute to set
	 *  @param value The value to assign to the attribute
	 */
	public synchronized static void setAttribute( Node node, String attr, String value )
	{
		if( node == null )
			throw new IllegalArgumentException("Node argument cannot be null");
		if( attr == null )
			throw new IllegalArgumentException("Node attribute argument cannot be null");
		if( value == null )
			throw new IllegalArgumentException("Node attribute value argument cannot be null");

		Node attrN = null;
		NamedNodeMap map = node.getAttributes();
		if( map != null )
			attrN = map.getNamedItem(attr);

		if( attrN == null ) {
			attrN = node.getOwnerDocument().createAttribute( attr );
			map.setNamedItem( attrN );
		}

		attrN.setNodeValue( value );
	}
	
	/**
	 * Sets a named attribute of a Node, or removes the attribute if the value is null;
	 * @param node The node to search
	 * @param attr The name of the attribute to set or remove
	 * @param value The value to assign, or NULL to remove
	 */
	public synchronized static void setOrRemoveAttribute( Node node, String attr, String value )
	{
		if( value == null )
		{
			removeAttribute( node, attr );
		}
		else
		{
			setAttribute( node, attr, value );
		}
	}
	
	/**
	 * Sets a named attribute of an Element, or removes the attribute if the value is null;
	 * @param element The element to search
	 * @param attr The name of the attribute to set or remove
	 * @param value The value to assign, or NULL to remove
	 */
	public synchronized static void setOrRemoveAttribute( Element element, String attr, String value )
	{
		if( value == null )
		{
			element.removeAttribute( attr );
		}
		else
		{
			element.setAttribute( attr, value );
		}
	}

	/**
	 *  Removes a named attribute of a Node<p>
	 *  @param node The node to search
	 *  @param attr The name of the attribute to remove
	 */
	public synchronized static void removeAttribute( Node node, String attr )
	{
		if( node == null )
			throw new IllegalArgumentException("Node argument cannot be null");
		if( attr == null )
			throw new IllegalArgumentException("Node attribute argument cannot be null");

		NamedNodeMap map = node.getAttributes();
		if( map != null ) {
			Node an = map.getNamedItem(attr);
			if( an != null )
				map.removeNamedItem(attr);
		}
	}

	/**
	 *  Returns the text value of the first child element found with the
	 *  specified tag name.<p>
	 *  @param node The node to search
	 *  @param element The element tag name to search for
	 *  @return The value of the element if found
	 */
	public synchronized static String getElementTextValue( Node node, String element )
	{
		if( node == null || element == null ){
			return null;
		}
		Node n = getFirstElementWithTagName( node, element );
		return n == null ? null : getText( n );
	}

	/**
	 *  Returns the text value of the first child element found with the
	 *  specified tag name.<p>
	 *  @param element The node to search
	 *  @param elementName The element tag name to search for
	 *  @return The value of the element if found
	 */
	public synchronized static String getElementTextValue( Element element, String elementName )
	{
		if( element == null || elementName == null ){
			return null;
		}
		
		NodeList nl = element.getElementsByTagName( elementName );
		return nl.getLength() == 0 ? null : getText( nl.item(0) );
	}
	
	/**
	 *  Returns the text value of the first element in the document with the
	 *  specified tag name<p>
	 *  @param doc The Document to search
	 *  @param element The element tag name to search for
	 *  @return The value of the element if found
	 */
	public synchronized static String getElementTextValue( Document doc, String element )
	{
		if( doc == null || element == null ){
			return null;
		}

		NodeList nl = doc.getElementsByTagName( element );

		return nl.getLength() == 0 ? null : getText( nl.item(0) );
	}

	/**
	 *  Returns the first child element found with the specified tag name
	 *  @param node The node to search
	 *  @param element The element tag name to search for
	 *  @return The matching element Node
	 */
	public synchronized static Node getFirstElementWithTagName( Node node, String element )
	{
		if( node != null && element != null )
		{
			NodeList nl = node.getChildNodes();

			for( int i = 0; i < nl.getLength(); i++ )
			{
				Node n = nl.item(i);

				if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(element) )
					return n;
			}
		}

		return null;
	}
	
	/**
	 *  Returns the first child element found with the specified tag name
	 *  @param node The node to search
	 *  @param element The element tag name to search for
	 *  @return The matching element Node
	 */
	public synchronized static Node getFirstElementIgnoreCase( Node node, String element )
	{
		if( node != null && element != null )
		{
			NodeList nl = node.getChildNodes();

			for( int i = 0; i < nl.getLength(); i++ )
			{
				Node n = nl.item(i);

				if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equalsIgnoreCase(element) ){
					return n;
				}
			}
		}

		return null;
	}
	
	

	/**
	 *  Gets all child elements with the specified tag name, optionally excluding
	 *  those elements that have an <code>enabled</code> attribute set to false.
	 *  <p>
	 *
	 *  @param node The parent node
	 *  @param element The element tag name
	 *  @param filter Do not return elements that have the <code>enabled</code> attribute set to false
	 *
	 *  @return A Vector of Node objects
	 */
	public synchronized static List<Node> getElementsByTagName( Node node, String element, boolean filter )
	{
		List<Node> v = new Vector<Node>();

		if( node != null && element != null )
		{
			NodeList nl = node.getChildNodes();
			for( int i = 0; i < nl.getLength(); i++ )
			{
				Node n = nl.item(i);
				if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(element) )
				{
					if( filter ) {
						String enabled = XMLUtils.getAttribute(n,"enabled");
						if( enabled == null || enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("yes") )
							v.add(n);
					}
					else
						v.add(n);
				}
			}
		}

		return v;
	}

	/**
	 *  Gets the first child element with the specified tag name having an
	 *  attribute set to the specified value.
	 *
	 *  @param node The parent node
	 *  @param element The element tag name
	 *  @param attribute The attribute name
	 *  @param value The attribute value to match
	 *
	 *  @return The Node that matches the criteria, or null if no such element
	 *      is found as a child of <i>node</i>
	 */
	public synchronized static Node getElementByAttr( Node node, String element, String attribute, String value )
	{
		if( node == null || element == null || attribute == null || value == null )
			return null;

		NodeList nl = node.getChildNodes();

		for( int i = 0; i < nl.getLength(); i++ )
		{
			Node result = nl.item(i);

			if( result.getNodeType() == Node.ELEMENT_NODE &&
				result.getNodeName().equals(element) )
			{
				String attrVal = XMLUtils.getAttribute( result, attribute );
				if( attrVal != null && attrVal.equals( value ) )
					return result;
			}
		}

		return null;
	}

	/**
	 *  A convenience function to get the named attribute of a child element.<p>
	 *
	 *  This method searches the specified Node for the first matching element,
	 *  then returns the value of the named attribute. If the attribute does not
	 *  exist the default value is returned.<p>
	 *
	 *  @param node The node to search
	 *  @param element The tag name of the element to search (it must be a non-
	 *      repeating child of the node)
	 *  @param attribute The name of the attribute
	 *  @param defaultValue The value to return if the property is not found
	 *
	 *  @return The attribute value
	 */
	public synchronized static String getElementAttribute( Node node, String element, String attribute, String defaultValue )
	{
		if( node == null || element == null || attribute == null )
			return null;

		List<Node> v = XMLUtils.getElementsByTagName( node, element, false );
		if( v == null )
			return defaultValue;

		String val = null;

		for( int i = 0; i < v.size() && val == null; i++ )
		{
			Node chN = (Node)v.get(i);
			NamedNodeMap attrs = chN.getAttributes();
			if( attrs != null )
				val = attrs.getNamedItem(attribute).getNodeValue();
		}

		return val == null ? defaultValue : val;
	}

	/**
	 *  A convenience function to set the named attribute of a child element.<p>
	 *
	 *  This method searches the specified Node for the first matching element,
	 *  then sets the value of the named attribute. If the element does not exist,
	 *  no action is taken.<p>
	 *
	 *  @param node The node to search
	 *  @param element The tag name of the element to search (it must be a non-
	 *      repeating child of the node)
	 *  @param attribute The name of the attribute
	 *  @param value The value of the attribute
	 */
	public synchronized static void setElementAttribute( Node node, String element, String attribute, String value )
	{
		if( node == null || element == null || attribute == null || value == null )
			return;

		List<Node> v = XMLUtils.getElementsByTagName( node, element, false );
		if( v == null )
			return;

		Node chN = (Node)v.get( 0 );
		setAttribute( chN, attribute, value );
	}
}
