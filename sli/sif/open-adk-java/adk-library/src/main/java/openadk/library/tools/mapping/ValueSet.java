//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import java.io.Serializable;
import java.util.*;

import openadk.util.ADKStringUtils;
import openadk.util.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *  A ValueSet is an arbitrary mapping table used to map an application's
 *  proprietary codes and constants to SIF codes and constants. For example, an
 *  agent might define a ValueSet to map grade levels, ethnicity codes, english
 *  proficiency codes, and so on.
 *  <p>
 *
 *  ValueSet stores its data in a HashMap. For each entry in the map, the key is
 *  a value defined by the application and the value is a ValueSetEntry object
 *  that encapsulates the associated SIF value and other fields like a display
 *  title for user interfaces.<p>
 *
 *  For example, a ValueSet that maps grade levels might be comprised of the
 *  following entries:
 *  <p>
 *
 *  <table>
 *      <tr><td><b>Key</b></td><td><b>Value</b></td></tr>
 *      <tr><td><b>PREK</b></td><td><b>PK</b></td></tr>
 *      <tr><td><b>K</b></td><td><b>0K</b></td></tr>
 *      <tr><td><b>1</b></td><td><b>01</b></td></tr>
 *      <tr><td><b>2</b></td><td><b>02</b></td></tr>
 *      <tr><td><b>3</b></td><td><b>03</b></td></tr>
 *      <tr><td><b>4</b></td><td><b>04</b></td></tr>
 *      <tr><td><b>5</b></td><td><b>05</b></td></tr>
 *      <tr><td><b>6</b></td><td><b>06</b></td></tr>
 *      <tr><td><b>7</b></td><td><b>07</b></td></tr>
 *      <tr><td><b>8</b></td><td><b>08</b></td></tr>
 *      <tr><td><b>9</b></td><td><b>09</b></td></tr>
 *  </table>
 *  <p>
 *
 *  To translate an application-defined value to its SIF equivalent, call the
 *  <code>translate</code> method. To translate a SIF-defined value to its
 *  application-defined equivalent, call the <code>translateReverse</code>
 *  method.
 *  <p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class ValueSet implements Comparator, Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -498576493089957332L;

	/**
	 *  The values of this ValueSet
	 */
	protected HashMap fTable = new HashMap();

	/**
	 *  Reverse lookup table
	 */
	protected HashMap fReverseTable = new HashMap();

	/**
	 *  The unique ID of this ValueSet
	 */
	protected String fId;

	/**
	 *  Optional display title for this ValueSet
	 */
	protected String fTitle;

	/**
	 *  Optional DOM Node that defines this ValueSet
	 */
	protected transient Node fNode;

	/**
	 * The default ValueSetEntry to use if no match is found
	 */
	protected ValueSetEntry fDefaultAppEntry;

	/**
	 * True to render the app default if the value being translated is null
	 */
	protected boolean fRenderAppDefaultIfNull;

	/**
	 * The default ValueSetEntry to use if no match is found
	 */
	protected ValueSetEntry fDefaultSifEntry;

	/**
	 * True to render SIF default if the value being translated is null
	 */
	protected boolean fRenderSifDefaultIfNull;

	/**
	 *  Constructs a ValueSet with an ID
	 */
    public ValueSet( String id )
	{
		this( id, null );
	}

	/**
	 *  Constructs a ValueSet with an ID and display title
	 */
	public ValueSet( String id, String title )
	{
		this( id, title, null );
	}

	/**
	 *  Constructs a ValueSet with an ID, display title, and associated DOM Node
	 */
	public ValueSet( String id, String title, Node node )
	{
		fId = id;
		fTitle = title;
		fNode = node;
		// TODO: Consider changing the constructor to require an Element instance
		if( node instanceof Element ){
			toXml( (Element)node );
		}
    }

    public ValueSet copy( Mappings newParent )
    {

    	Document newParentDOM = null;
    	if( newParent.fNode != null ) {
    		newParentDOM = newParent.fNode.getOwnerDocument();
    	}

    	ValueSet copy = new ValueSet( fId, fTitle );
    	if( fNode != null && newParentDOM != null ) {
    		copy.fNode = newParentDOM.importNode( fNode, false );
    		newParent.fNode.appendChild( copy.fNode );
    	}

    	//	Copy the ValueSetEntry's
		ValueSetEntry[] entries = getEntries();
		for( int i = 0; i < entries.length; i++ )
		{
			copy.define( entries[i].name, entries[i].value, entries[i].title );
		}

		try
		{
			if( fDefaultAppEntry != null ){
				copy.setAppDefault( fDefaultAppEntry.name, fRenderAppDefaultIfNull );
			}
			if( fDefaultSifEntry != null ){
				copy.setSifDefault( fDefaultSifEntry.value, fRenderSifDefaultIfNull );
			}
		}
		catch( ADKMappingException adkme ){
			// We're kind of stuck here.
			throw new RuntimeException( adkme.toString() + ADKStringUtils.getStackTrace( adkme ), adkme );
		}
		return copy;
    }

	public String getId()
	{
		return fId;
	}

	public void setId( String id )
	{
		fId = id;
	}

	public String getTitle()
	{
		return fTitle == null ? fId : fTitle;
	}

	public void setTitle( String title )
	{
		fTitle = title;
	}

	public Node getNode()
	{
		return fNode;
	}

	public void setNode( Node node )
	{
		fNode = node;
	}

	public String toString()
	{
		return getTitle();
	}

	/**
	 *  Return a sorted array of the ValueSet's entries
	 *  @return An array of ValueSetEntry objects sorted by display order
	 */
	public ValueSetEntry[] getEntries()
	{
		int i = 0;

		ValueSetEntry[] entries = new ValueSetEntry[ fTable.size() ];
		for( Iterator it = fTable.values().iterator(); it.hasNext(); )
			entries[i++] = (ValueSetEntry)it.next();

		Arrays.sort( entries, this );

		return entries;
	}


	/**
	 *  Compares two ValueSetEntry objects for order
	 */
	public int compare( Object o1, Object o2 )
	{
		int i1 = ((ValueSetEntry)o1).displayOrder;
		int i2 = ((ValueSetEntry)o2).displayOrder;
		if( i1 < i2 )
			return -1;
		if( i1 == i2 )
			return 0;

		return 1;
	}

	/**
	 *  Sets a value
	 */
	public void define( String appValue, String sifValue, String title )
	{
		Element element = null;
		if( fNode != null ){
			element = fNode.getOwnerDocument().createElement( "value" );
			fNode.appendChild( element );
		}

		define( appValue, sifValue, title, element );
	}

	/**
	 *  Sets a value
	 */
	public void define( String appValue, String sifValue, String title, Node node )
	{
		if( appValue != null ) {
			ValueSetEntry entry = new ValueSetEntry( appValue, sifValue, title );
			entry.displayOrder = fTable.size();
			entry.node = node;

			if( node != null ){
				entry.toXml( (Element)node );
			}
			fTable.put( appValue, entry );
			fReverseTable.put( sifValue, entry );
		}
	}

	/**
	 *  Gets a value
	 */
	public String lookup( String appValue )
	{
		ValueSetEntry e = appValue == null ? null : (ValueSetEntry)fTable.get(appValue);
		return e == null ? null : e.value;
	}

	/**
	 *  Translates a value.<p>
	 *
	 *  This method differs from <code>lookup</code> in that it returns a default value
	 *  if it is available and no mapping is defined in the ValueSet. If there is no
	 *  default value, the <code>appValue</code> passed in is returned.
	 *
	 *  The <code>lookup</code> method
	 *  returns null if no mapping is defined.
	 *  <p>
	 *
	 *  @param appValue An application-defined value
	 *  @return The corresponding SIF-defined value
	 */
	public String translate( String appValue )
	{
		ValueSetEntry e = getEntry( appValue );
		if( e != null ){
			return e.value;
		} else {
			return evaluateDefault(
				fDefaultSifEntry == null ? null : fDefaultSifEntry.value,
						fRenderSifDefaultIfNull, appValue );
		}

	}

	/**
	 * Encapsulates the logic for returning default values for a valueset
	 * @param defaultValue
	 * @param renderIfNull
	 * @param srcValue
	 * @return
	 */
	private String evaluateDefault(
			String defaultValue,
			boolean renderIfNull,
			String srcValue )
	{
		if( srcValue == null && !renderIfNull ){
			 return null;
		 }
		 if( defaultValue != null ){
			return defaultValue;
		}
		return srcValue;
	}

	/**
	 *  Translates a value.<p>
	 *
	 *  If there is no mapping defined, the default value passed in is returned.
	 *  If, however, the default value passed in is NULL, the valueset will be searched
	 *  for a default value and that value will be returned. If there is no default,
	 *  the <code>appValue</code> passed in is returned.
	 *
	 *  The <code>lookup</code> method
	 *  returns null if no mapping is defined.
	 *  <p>
	 *
	 *  @param appValue An application-defined value
	 *  @return The corresponding SIF-defined value
	 */
	public String translate( String appValue, String defaultValue )
	{
		ValueSetEntry e = getEntry( appValue );
		if( e != null ){
			return e.value;
		}
		if( defaultValue != null ){
			return defaultValue;
		}

		return evaluateDefault(
			fDefaultSifEntry == null ? null : fDefaultSifEntry.value,
					fRenderSifDefaultIfNull, appValue );

	}

	/**
	 *  Performs a reverse translation.<p>
	 *
	 *  If there is no match found, but there is a default value found that
	 *  applies to inbound mappings, the default value will be returned.
	 *
	 *  If no default value is found, the sifValue will be returned
	 *
	 *  @param sifValue An SIF-defined value
	 *  @return The corresponding application-defined value
	 */
	public String translateReverse( String sifValue )
	{
		ValueSetEntry e = getReverseEntry( sifValue );
		if( e!= null ){
			return e.name;
		} else {
			return evaluateDefault(
				fDefaultAppEntry == null ? null : fDefaultAppEntry.name,
						fRenderAppDefaultIfNull, sifValue );
		}
	}

	/**
	 *  Performs a reverse translation.<p>
	 *
	 *  If there is no match found, the <code>defaultValue</code> is returned.
	 *  If, however, the defaultValue is NULL, the Valueset's outbound default value
	 *  will be used. If the valueset does not have a default outbound value, the
	 *  <code>sifValue</code> will be returned
	 *
	 *  @param sifValue An SIF-defined value
	 *  @return The corresponding application-defined value
	 */
	public String translateReverse( String sifValue, String defaultValue )
	{
		ValueSetEntry e = getReverseEntry( sifValue );
		if( e!= null ){
			return e.name;
		}
		if( defaultValue != null ){
			return defaultValue;
		}
		return evaluateDefault(
			fDefaultAppEntry == null ? null : fDefaultAppEntry.name,
					fRenderAppDefaultIfNull, sifValue );
	}

	/**
	 * Gets a ValueSet entry for the specified application value
	 * @param appValue
	 * @return The ValueSetEntry that matches, if found, or null
	 */
	private ValueSetEntry getEntry( String appValue )
	{
		ValueSetEntry e = appValue != null ? (ValueSetEntry)fTable.get(appValue) : null;
		return e;
	}

	/**
	 * Looks up a ValueSetEntry by a SIF value
	 * @param sifValue
	 * @return The ValueSetEntry that matches, if found, or null
	 */
	private ValueSetEntry getReverseEntry( String sifValue )
	{
		ValueSetEntry e = sifValue != null ? (ValueSetEntry)fReverseTable.get(sifValue) : null;
		return e;
	}

	/**
	 * Sets the default application value that will be returned if no match
	 * is found during a valueset translation
	 * @param appValue The value to return if there is no match. Pass in <code>Null</code> if the
	 * previously-set default is to be removed
	 * @param renderIfNull True if the default value should be returned even if the SIF Value being
	 * translated is NULL. If false, NULL will be returned.
	 * @throws ADKMappingException Thrown if the value has not yet been defined in this valueset
 	 * by calling <code>define</code>
	 */
	public void setAppDefault( String appValue, boolean renderIfNull )
		throws ADKMappingException
	{
		fRenderAppDefaultIfNull = renderIfNull;
		if( fDefaultAppEntry != null ){
			ValueSetEntry oldEntry = fDefaultAppEntry;
			fDefaultAppEntry = null;
			toXml(oldEntry, (Element)oldEntry.node );
		}

		if( appValue != null ){
			ValueSetEntry entry = getEntry( appValue );
			if( entry == null ){
				throw new ADKMappingException( "Value: '" + appValue + "' is not defined.",  null  );
			}
			fDefaultAppEntry = entry;
			toXml( fDefaultAppEntry, (Element)fDefaultAppEntry.node );
		}



	}

	/**
	 * Sets the default SIF value that will be returned if no match
	 * is found during a valueset translation
	 * @param sifValue The value to return if there is no match. Pass in <code>Null</code> if the
	 * previously-set default is to be removed
	 * @param renderIfNull True if the default value should be returned even if the app value
	 * being translated is null. If false, NULL will be returned.
	 * @throws ADKMappingException Thrown if the value has not yet been defined in this valueset
	 * by calling <code>define</code>
	 *
	 * @see #define(String, String, String)
	 * @see #define(String, String, String, Node)
	 */
	public void setSifDefault( String sifValue, boolean renderIfNull )
		throws ADKMappingException
	{
		fRenderSifDefaultIfNull = renderIfNull;
		if( fDefaultSifEntry != null ){
			ValueSetEntry oldEntry = fDefaultSifEntry;
			fDefaultSifEntry = null;
			toXml(oldEntry, (Element)oldEntry.node );
		}
		if( sifValue != null ){
			ValueSetEntry entry = getReverseEntry( sifValue );
			if( entry == null ){
				throw new ADKMappingException( "Value: '" + sifValue + "' is not defined.",  null  );
			}
			fDefaultSifEntry = entry;
			toXml( fDefaultSifEntry, (Element)fDefaultSifEntry.node );
		}





	}


	/**
	 *  Clears the table
	 */
	public void clear()
	{
		fTable.clear();
		fReverseTable.clear();
		fDefaultAppEntry = null;
		fDefaultSifEntry = null;
		fRenderAppDefaultIfNull = false;
		fRenderSifDefaultIfNull = false;
	}

	/**
	 *  Removes a value
	 */
	public void remove( String appValue )
	{
		if( appValue != null ) {
			ValueSetEntry entry = (ValueSetEntry)fTable.get( appValue );
			if( entry != null ) {
				fTable.remove( appValue );
	    		fReverseTable.remove( entry.value );
	    		if (fNode != null) {
	    		    fNode.removeChild( entry.node );
	    		}

	    		if( fDefaultAppEntry == entry ){
					fDefaultAppEntry = null;
				}
				if( fDefaultSifEntry == entry ){
					fDefaultSifEntry = null;
				}

			}
		}
	}

	public Map getMap() {
		return fTable;
	}
	public Map getReverseMap() {
		return fReverseTable;
	}

	/**
	 * Writes this valueset to an XML element.
	 * @param element
	 */
	public void toXml( Element element )
	{
		if( element == null ){
			return;
		}
		XMLUtils.setAttribute( element, "id", fId );
		XMLUtils.setOrRemoveAttribute( element, "title", fTitle );

		//  Add <value> elements to the <valueset>...
		ValueSetEntry[] entries = getEntries();
		for( int i = 0; i < entries.length; i++ )
		{
			Element vsElement = element.getOwnerDocument().createElement( "value" );
			element.appendChild( vsElement );
			entries[i].node = vsElement;
			toXml( entries[i], vsElement );
		}
	}

	private void toXml( ValueSetEntry entry, Element element )
	{
		// If the element passed in is null, this ValueSet doesn't currently have an
		// XML Element that it is associated with. This is OK. Exit Gracefully.
		if( element == null ){
			return;
		}

		entry.toXml( element );

		// Since this class controls the notion of defaults, write the
		// attributes controlling defaults here
		boolean isDefaultAppValue =  fDefaultAppEntry == entry;
		boolean isDefaultSifValue = fDefaultSifEntry == entry;
		if( isDefaultAppValue ){
			if( isDefaultSifValue ){
				element.setAttribute( "default", "both" );
				element.setAttribute( "ifnull", getIfNull( fRenderSifDefaultIfNull ) );
			} else {
				element.setAttribute( "default", "inbound" );
				element.setAttribute( "ifnull", getIfNull( fRenderAppDefaultIfNull ) );
			}
		}
		else if ( isDefaultSifValue ){
			element.setAttribute( "default", "outbound" );
			element.setAttribute( "ifnull", getIfNull( fRenderSifDefaultIfNull ) );
		}
		else {
			element.removeAttribute( "default" );
			element.removeAttribute( "ifnull" );
		}
	}

	private String getIfNull( boolean value ){
		return value ? "default" : "suppress";
	}


}
