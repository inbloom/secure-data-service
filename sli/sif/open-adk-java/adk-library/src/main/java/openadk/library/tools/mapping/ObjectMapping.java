//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import org.w3c.dom.*;
import java.util.*;


/**
 *  An ObjectMapping defines a set of field mapping rules for a specific SIF Data
 *  Object type such as StudentPersonal, StaffPersonal, or BusInfo. ObjectMapping
 *  is comprised of zero or more FieldMapping children.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ObjectMapping
{
	/**
	 *  The SIF Data Object type (e.g. StudentPersonal)
	 */
	protected String fObjType;

	/**
	 *  Optional DOM Node from which this ObjectType was produced
	 */
	protected Node fNode;

	/**
	 *  Vector of FieldMappings
	 */
//	protected List<FieldMapping> fFieldRules = null; JEN
	protected List<Mapping> fFieldRules = null;
	
	/**
	 * Map of prefix to namespace URI for this object
	 * Using string for URI type as it matches the XML DOM API
	 */
	protected Map<String, String> prefixToNamespace = new HashMap<String, String>();

	/**
	 *  The parent Mappings object
	 */
	protected Mappings fParent;


	/**
	 *  Constructor
	 *  @param objType The name of a SIF Data Object (e.g. "StudentPersonal")
	 */
    public ObjectMapping( String objType )
		throws ADKMappingException
	{
		this( objType, null );
    }

	/**
	 *  Constructor
	 *  @param objType The name of a SIF Data Object (e.g. "StudentPersonal")
	 *  @param node The optional DOM Node from which this ObjectType was produced
	 */
	public ObjectMapping( String objType, Node node )
		throws ADKMappingException
	{
		fObjType = objType;
		fNode = node;
	}

	/**
	 *  Gets the optional DOM Node associated with this ObjectMapping instance.
	 * 	The DOM Node is usually set by the parent Mappings object when an
	 * 	ObjectMapping is populated from a DOM Document.
	 */
	public Node getNode()
	{
		return fNode;
	}

	/**
	 *  Sets the optional DOM Node associated with this ObjectMapping instance.
	 * 	The DOM Node is usually set by the parent Mappings object when an
	 * 	ObjectMapping is populated from a DOM Document.
	 */
	public void setNode( Node node )
	{
		fNode = node;
	}

	/**
	 *  Creates a copy this ObjectMapping instance.<p>
	 *
	 * 	This method performs a "deep copy", such that a clone is made of each
	 * 	child FieldMapping. The parent of the new ObjectMapping will be the
	 * 	Mappings object passed to this function. Any DOM Nodes assigned to this
	 * 	object or its children are cloned and appended to the parent Mappings's
	 * 	DOM Node if one exists.<p>
	 *
	 *  @return A "deep copy" of this object
	 */
	public ObjectMapping copy( Mappings newParent )
		throws ADKMappingException
	{
		//	Create a new ObjectMapping instance
		ObjectMapping m = new ObjectMapping( fObjType );

		//  Copy the DOM Node
		if( fNode != null && newParent.fNode != null ) {
			m.fNode = newParent.fNode.getOwnerDocument().importNode(  fNode, false );
			if( newParent.fNode != null )
				newParent.fNode.appendChild( m.fNode );
		}

		//  Copy fFieldRules
		if( fFieldRules != null )
		{
			if( m.fFieldRules == null )
				// m.fFieldRules = new Vector<FieldMapping>(); JEN
				m.fFieldRules = new Vector<Mapping>();

			for( int i = 0; i < fFieldRules.size(); i++ )
			{
				// FieldMapping copy = (fFieldRules.get(i)).copy( m ); JEN 
				Mapping copy = (fFieldRules.get(i)).copy( m );
				
				// m.addRule( copy ); JEN
				if (copy instanceof FieldMapping)
					m.addRule( (FieldMapping)copy );

			}
		}

		return m;
	}

	/**
	 *  Gets the SIF Data Object type of this ObjectMapping
	 *  @return An object type name such as "StudentPersonal" or "BusInfo"
	 */
	public String getObjectType()
	{
		return fObjType;
	}

	/**
	 *  Appends a FieldMapping definition<p>
	 *
	 *  @param mapping A FieldMapping that defines the rules for mapping a field
	 * 		of the application to an element or attribute of a SIF Data Object.
	 *      There can only be one FieldMapping per unique field name (i.e. if
	 * 		you have defined a FieldMapping rule with a field name of 'STUDENTNUM',
	 * 		there cannot be another FieldMapping rule with that same field name.)
	 * 		To map a single application field to more than one SIF element or
	 * 		attribute, create a FieldMapping with a unique field name (e.g. 'STUDENTNUM_2')
	 * 		and call the <code>setAlias</code> method to define it as an alias
	 * 		of an existing field.
	 *
	 *  @exception ADKMappingException thrown if there is already a FieldMapping
	 *      with the specified field name
	 */
	public void addRule( FieldMapping mapping )
		throws ADKMappingException
	{
		insertRule( mapping, fFieldRules == null ? 0 : fFieldRules.size(), true );
	}

	/**
	 *  Appends a FieldMapping definition<p>
	 *
	 *  @param mapping A FieldMapping that defines the rules for mapping a field
	 * 		of the application to an element or attribute of a SIF Data Object.
	 *      There can only be one FieldMapping per unique field name (i.e. if
	 * 		you have defined a FieldMapping rule with a field name of 'STUDENTNUM',
	 * 		there cannot be another FieldMapping rule with that same field name.)
	 * 		To map a single application field to more than one SIF element or
	 * 		attribute, create a FieldMapping with a unique field name (e.g. 'STUDENTNUM_2')
	 * 		and call the <code>setAlias</code> method to define it as an alias
	 * 		of an existing field.
	 *
	 * 	@param buildDomTree true to create a DOM Node element for this
	 * 		FieldMapping and append it to the parent Node
	 *
	 *  @exception ADKMappingException thrown if there is already a FieldMapping
	 *      with the specified field name
	 */
	protected void addRule( FieldMapping mapping, boolean buildDomTree )
		throws ADKMappingException
	{
		insertRule( mapping, fFieldRules == null ? 0 : fFieldRules.size(), buildDomTree );
	}

	/**
	 *  Insert a FieldMapping definition at the specified index.<p>
	 *
	 *  @param mapping A FieldMapping that defines the rules for mapping a field
	 * 		of the application to an element or attribute of a SIF Data Object.
	 *      There can only be one FieldMapping per unique field name (i.e. if
	 * 		you have defined a FieldMapping rule with a field name of 'STUDENTNUM',
	 * 		there cannot be another FieldMapping rule with that same field name.)
	 * 		To map a single application field to more than one SIF element or
	 * 		attribute, create a FieldMapping with a unique field name (e.g. 'STUDENTNUM_2')
	 * 		and call the <code>setAlias</code> method to define it as an alias
	 * 		of an existing field.
	 *
	 *  @exception ADKMappingException thrown if there is already a FieldMapping
	 *      with the specified field name
	 */
	public void insertRule( FieldMapping mapping, int index )
		throws ADKMappingException
	{
		insertRule( mapping, index, true );
	}

	/**
	 *  Insert a FieldMapping definition at the specified index.<p>
	 *
	 *  @param mapping A FieldMapping that defines the rules for mapping a field
	 * 		of the application to an element or attribute of a SIF Data Object.
	 *      There can only be one FieldMapping per unique field name (i.e. if
	 * 		you have defined a FieldMapping rule with a field name of 'STUDENTNUM',
	 * 		there cannot be another FieldMapping rule with that same field name.)
	 * 		To map a single application field to more than one SIF element or
	 * 		attribute, create a FieldMapping with a unique field name (e.g. 'STUDENTNUM_2')
	 * 		and call the <code>setAlias</code> method to define it as an alias
	 * 		of an existing field.
	 *
	 * 	@param buildDomTree true to create a DOM Node element for this
	 * 		FieldMapping and append it to the parent Node
	 *
	 *  @exception ADKMappingException thrown if there is already a FieldMapping
	 *      with the specified field name
	 */
	protected void insertRule( FieldMapping mapping, int index, boolean buildDomTree )
		throws ADKMappingException
	{
		Node relativeTo = null;

		if( fFieldRules == null )
			// fFieldRules = new Vector<FieldMapping>(); JEN
			fFieldRules = new Vector<Mapping>();
		else
		{
			//  Check for duplicate
			for( int i = 0; i < fFieldRules.size(); i++ ) {
				if( (fFieldRules.get(i)).getKey().equals( mapping.getKey() ) )
					throw new ADKMappingException( "Duplicate field mapping: " + mapping.getFieldName(), null );
			}

			//	If we'll be building a child DOM Node, find the existing Node
			//	that it will be inserted at
			if( buildDomTree && fNode != null && fFieldRules.size() > index )
				relativeTo = (fFieldRules.get( index )).getNode();
		}

		try {
			fFieldRules.add( index, mapping );
		} catch( Exception ex ) {
			throw new ADKMappingException( ex.toString(), null, ex );
		}

		if( buildDomTree && fNode != null )
		{
			//  Create and insert a child DOM Node
			Element element = fNode.getOwnerDocument().createElement( Mappings.XML_FIELD );
			mapping.setNode( element );
			mapping.toXML( element );
			if( relativeTo != null )
				fNode.insertBefore( element , relativeTo );
			else
				fNode.appendChild( element );
		}
	}

	/**
	 *  Remove a FieldMapping definition
	 */
	public void removeRule( FieldMapping mapping )
	{
		if( fFieldRules != null )
		{
			fFieldRules.remove( mapping );

			//  Remove the DOM Node if there is one
			Node n = mapping.getNode();
			if( n != null )
				n.getParentNode().removeChild( n );
		}
	}

	/**
	 *  Removes the FieldMapping at the specified index
	 *  @param index The zero-based index of the FieldMapping
	 */
	public void removeRule( int index )
	{
		if( fFieldRules != null && index >= 0 && index < fFieldRules.size() )
		{
			// FieldMapping existing = fFieldRules.get( index ); JEN
			Mapping existing = fFieldRules.get( index );

			fFieldRules.remove( index );

			//  Remove the DOM Node if there is one
			Node n = existing == null ? null : existing.getNode();
			if( n != null )
				n.getParentNode().removeChild( n );
		}
	}

	/**
	 *  Return an array of all FieldMapping definitions
	 *  @param inherit True to inherit FieldMapping definitions from the
	 *      parent Mappings ancestry
	 *  @return A list of all FieldMapping definitions
	 *  @deprecated Please change all references to use getAllRulesList();
	 */
	public FieldMapping[] getRules( boolean inherit )
	{
		List<FieldMapping> rules = getRulesList( inherit ); 
		FieldMapping[] arr = new FieldMapping[ rules.size() ];
		rules.toArray( arr );
		return arr;
	}

	/**
	 * Return a list of all FieldMapping definitions
	 * @param inherit True to inherit FieldMapping definitions from the
	 *      parent Mappings ancestry
	 * @return A list of all FieldMapping definitions
	 * @deprecated Please use getAllRulesList
	 */
	public List<FieldMapping> getRulesList( boolean inherit )
	{
		List<FieldMapping> rules = new ArrayList<FieldMapping>();
		if( inherit )
		{
			Set<String> set = new HashSet<String>();
			// Keep the rules in a list because the ordering of
			// rules is important
			Mappings m = fParent;
			while( m != null )
			{
				ObjectMapping om = m.getRules( fObjType, false );
				if( om != null && om.fFieldRules != null ) {
					// for( FieldMapping fm : om.fFieldRules ) { JEN
					for( Mapping fm : om.fFieldRules ) {
						String key = fm.getKey();
						if( !set.contains( key ) ) {
							set.add( key );
							if (fm instanceof FieldMapping) {
								rules.add((FieldMapping)fm );
							}
						}
					}
				}
				if( inherit )
					m = m.fParent;
				else
					m = null;
			}
		}
		else if( fFieldRules != null )
		{
//			rules.addAll( fFieldRules );
			for( Mapping mapping : fFieldRules ) {
				if (mapping instanceof FieldMapping) {
					rules.add((FieldMapping)mapping );
				}
			}
		}

		return rules;
	}
	
	/**
	 * Return a list of all FieldMapping definitions
	 * @param inherit True to inherit FieldMapping definitions from the
	 *      parent Mappings ancestry
	 * @return A list of all FieldMapping definitions
	 */
	public List<Mapping> getAllRulesList( boolean inherit )
	{
		List<Mapping> rules = new ArrayList<Mapping>();
		if( inherit )
		{
			Set<String> set = new HashSet<String>();
			// Keep the rules in a list because the ordering of
			// rules is important
			Mappings m = fParent;
			while( m != null )
			{
				ObjectMapping om = m.getRules( fObjType, false );
				if( om != null && om.fFieldRules != null ) {
					// for( FieldMapping fm : om.fFieldRules ) { JEN
					for( Mapping fm : om.fFieldRules ) {
						String key = fm.getKey();
						if( !set.contains( key ) ){
							set.add( key );
							rules.add( fm );
						}
					}
				}
				if( inherit )
					m = m.fParent;
				else
					m = null;
			}
		}
		else if( fFieldRules != null )
		{
			rules.addAll( fFieldRules );
		}

		return rules;
	}

	/**
	 *  Count the number of FieldMapping definitions.
	 */
	public int getRuleCount()
	{
		return fFieldRules == null ? 0 : fFieldRules.size();
	}

	/**
	 *  Gets the FieldMapping at the specified index
	 *  @param index The zero-based index of the FieldMapping
	 */
	public FieldMapping getRule( int index )
	{
		return fFieldRules == null || index >= fFieldRules.size() ? null :
			(FieldMapping)fFieldRules.get( index );
	}

	/**
	 *  Clear all FieldMapping definitions.
	 */
	public void clearRules()
	{
		if( fFieldRules != null )
		{
			if( fNode != null )
			{
				for( Iterator<Mapping> it = fFieldRules.iterator(); it.hasNext(); ) {
					Mapping m = it.next();
					Node n = m.getNode();
					if( n != null )
						n.getParentNode().removeChild( n );
				}
			}

			fFieldRules.clear();
		}
	}
	
	public void addListingMapping(ListMapping listMapping) {
		if( fFieldRules == null ) {
			fFieldRules = new Vector<Mapping>();
		}
		fFieldRules.add(listMapping);
	}
	
	
	/**
	 * Return a map of all the namespace prefix mappings for this object mapping
	 * @return a map of all the namespace prefix mappings for this object mapping
	 */
	public Map<String, String> getAllNamespaceURIs() {
		return getAllNamespaceURIs(false);
	}
	
	/**
	 * Returns all known xml namespace declarations as prefix mapped to URI
	 * The inheritence feature of this method is different than regular mappings inheritence. It
	 * actually causes the associated XML DOM tree, if any exists, to be searched for other namespace
	 * prefixes declared in the scopes containing this object mapping, and may search outside the mappings
	 * definitions themselves if the associated DOM document exceeds them.
	 * @param inheritXmlNS if true, the accessible XML tree will be traversed upwards and all declared namespaces will be included
	 * @return all known prefix to namespace URI mappings
	 */
	public Map<String, String> getAllNamespaceURIs(boolean inheritXmlNS) {
		Map<String, String> fullMap = prefixToNamespace; 
		if (inheritXmlNS && fNode != null) {
			fullMap = new HashMap<String, String>(prefixToNamespace);
			Node node = fNode.getParentNode();
			while(node != null) {
				if (node.hasAttributes()) {
					NamedNodeMap nnm = node.getAttributes();
					for (int nnmIdx = 0; nnmIdx < nnm.getLength(); nnmIdx++) {
						Node item = nnm.item(nnmIdx);
						if (item instanceof Attr) {
							Attr attr = (Attr)item;
							if (attr.getNodeName().startsWith("xmlns:")) {
								String foundPrefix = attr.getNodeName().substring(6);
								if (!fullMap.containsKey(foundPrefix)) {
									fullMap.put(foundPrefix, attr.getNodeValue());
								}
							}
						}
					}
				}
				node = node.getParentNode();
			}
		}
		return Collections.unmodifiableMap(fullMap);
	}
	
	public String getNamespaceURIForPrefix(String prefix) {
		return prefixToNamespace.get(prefix);
	}
	
	public void setNamespaceURIForPrefix(String prefix, String namespaceURI) {
		setNamespaceURIForPrefix(prefix, namespaceURI, true);
	}
	
	protected void setNamespaceURIForPrefix(String prefix, String namespaceURI, boolean buildDomTree) {
		if (prefix == null || namespaceURI == null) throw new IllegalArgumentException("This method does not accept null parameters");
		prefixToNamespace.put(prefix, namespaceURI);
		if (fNode != null && fNode instanceof Element && buildDomTree) {
			Element object = (Element)fNode;
			object.setAttribute("xmlns:" + prefix, namespaceURI);
		}
	}
	
	public void removePrefix(String prefix) {
		prefixToNamespace.remove(prefix);
		if (fNode != null && fNode instanceof Element) {
			((Element)fNode).removeAttribute("xmlns:" + prefix);
		}
	}
}
