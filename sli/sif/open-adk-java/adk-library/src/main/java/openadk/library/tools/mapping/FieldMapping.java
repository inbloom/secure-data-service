//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import openadk.library.*;
import openadk.library.tools.cfg.ADKConfigException;
import openadk.library.tools.xpath.SIFXPathContext;
import openadk.util.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.Element;


/**
 *  A FieldMapping defines how to map a local application field to an element or
 *  attribute of the SIF Data Object type encapsulated by the parent ObjectMapping.
 *  Each FieldMapping is associated with a <i>Rule</i> that is evaluated at
 *  runtime to carry out the actual mapping operation on a SIFDataObject instance.
 *  The way the rule behaves is up to its implementation.<p>
 *
 *  A FieldMapping may have a default value. If set, the default value is
 *  assigned to the SIF element or attribute if the corresponding field value is
 *  null or undefined. This is useful if you wish to ensure that a specific SIF
 *  element/attribute always has a value regardless of whether or not there is a
 *  corresponding value in your application's database.
 *  <p>
 *  
 *  A Field Mapping that has a default value can also set another attribute that 
 *  specifies the behavior of the Field Mapping if the application value being 
 *  mapped is null. 
 *  <p>
 *
 *  The application-defined field name that is associated with a FieldMapping
 *  must be unique; that is, there cannot be more than one FieldMapping for the
 *  same application field. However, if you wish to map the same field to more
 *  than one SIF element or attribute, you can create an <i>alias</i>. An alias
 *  is a FieldMapping that has a unique field name but refers to another field.
 *  For example, if your application defines the field STUDENT_NUM and you wish
 *  to define two FieldMappings for that field, create an alias:
 *  <p>
 *
 *  <code>
 *  // Create the default mapping<br/>
 *  FieldMapping fm = new FieldMapping("STUDENT_NUM","OtherId[@Type='06']");<br/><br/>
 *  <br/>
 *  // Create an alias (the field name must be unique)<br/>
 *  FieldMapping zz = new FieldMapping("MYALIAS","OtherId[@Type='ZZ']");<br/>
 *  zz.setAlias( "STUDENT_NUM" );<br/><br/>
 *  </code>
 *
 *  In the above example, the "STUDENT_NUM" mapping produces an &lt;OtherId&gt;
 *  element with its Type attribute set to '06'. The "MYALIAS" mapping produces
 *  a second &lt;OtherId&gt; element with its Type attribute set to 'ZZ'. Both
 *  elements will have the value of the application-defined STUDENT_NUM field.
 *  Note that if MYALIAS were an actual field name of your application, however,
 *  the value of the &lt;OtherId Type='ZZ'&gt; element would be equal to that
 *  field's value. When creating aliases be sure to choose a name that does not
 *  conflict with the real field names used by your application.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class FieldMapping extends Mapping
{
	
	private static final String ATTR_VALUESET = "valueset"; 
	private static final String ATTR_ALIAS = "alias";
	private static final String ATTR_DEFAULT = "default";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_DATATYPE = "datatype";
	private static final String ATTR_SIFVERSION = "sifVersion";
	private static final String ATTR_DIRECTION = "direction";
	private static final String ATTR_IFNULL = "ifnull";
	
	
	/**
	 * The field mapping behavior for null fields is unspecified. In this
	 * case, the behavior is identical to IFNULL_DEFAULT
	 * @see #setNullBehavior(byte)
	 */
	public static final byte IFNULL_UNSPECIFIED = 0;
	
	/**
	 * Specifies that if the field being mapped is NULL, this field mapping
	 * should use the default value, if set.
	 * @see #setNullBehavior(byte) 
	 */
	public static final byte IFNULL_DEFAULT = 1;
	/**
	 * Specifies that if the field being mapped is NULL, this field mapping
	 * should not generate a SIF Element, even if a default value is specified
	 * @see #setNullBehavior(byte)
	 */
	public static final byte IFNULL_SUPPRESS = 2;
	
	protected byte fNullBehavior = IFNULL_UNSPECIFIED;
	private Rule fRule;
	protected String fField;
	protected String fDefValue;
	protected String fAlias;
	protected String fValueSet;
	protected MappingsFilter fFilter;
	protected Node fNode;
	private SIFDataType fDataType = SIFDataType.STRING;

	/**
	 *  Constructor
	 */
    public FieldMapping()
	{
		this( null, (String)null, (Node)null );
    }

	/**
	 *  Constructs a FieldMapping with an XPath-like rule
	 *  @param name The name of the local application field that maps to the
	 *      SIF Data Object element or attribute described by this FieldMapping
	 *  @param rule An XPath-like query string described by the <code>SIFDTD.lookupByXPath</code>
	 *      method
	 */
	public FieldMapping( String name, String rule )
	{
		this( name, rule, null );
	}

	/**
	 * Creates a new FieldMapping rule
	 * @param name The name of the field being mapped
	 * @param rule The XPath rule associated with this mapping
	 * @param node The optional DOM Node associated with this FieldMapping
	 */
	public FieldMapping( String name, String rule, Node node )
	{
		setNode( node );
		setFieldName( name );
		if( rule != null ){
			setRule( rule );
		}
	}
	
	/**
	 *  Constructs a FieldMapping with an &lt;OtherId&gt; rule.<p>
	 * 
	 *  @param name The name of the local application field that maps to the
	 *      SIF Data Object element or attribute described by this FieldMapping
	 *  @param rule An OtherIdMapping object that describes how to select
	 *      a &lt;OtherId&gt; element during a mapping operation
	 */
	public FieldMapping( String name, OtherIdMapping rule )
	{
		this( name, rule, null );
	}

	/**
	 *  Constructs a FieldMapping with an &lt;OtherId&gt; rule.<p>
	 * 
	 *  @param name The name of the local application field that maps to the
	 *      SIF Data Object element or attribute described by this FieldMapping
	 *  @param rule An OtherIdMapping object that describes how to select
	 *      a &lt;OtherId&gt; element during a mapping operation
	 * @param node The optional DOM Node that this FieldMapping stores its configuration
	 * 		to, or Null
	 */
	public FieldMapping( String name, OtherIdMapping rule, Node 	node )
	{
		setNode( node );
		setFieldName( name );
		if( rule != null ){
			setRule( rule );
		}
	}

	/**
	 *  Gets the optional DOM Node associated with this FieldMapping instance. 
	 * 	The DOM Node is usually set by the parent ObjectMapping instance when a 
	 * 	FieldMapping is populated from a DOM Document.
	 * @return The DOM Node associated with this this FieldMapping instance
	 */
	public Node getNode()
	{
		return fNode;
	}

	/**
	 *  Sets the optional DOM Node associated with this FieldMapping instance. 
	 * 	The DOM Node is usually set by the parent ObjectMapping instance when a 
	 * 	FieldMapping is populated from a DOM Document.
	 * @param node The DOMNode associated with this FieldMapping instance
	 */
	public void setNode( Node node )
	{
		fNode = node;
	}
	
	/**
	 * Creates a new FieldMapping instance and populates its properties from
	 * the given XML Element
	 * @param parent 
	 * @param element
	 * @return a new FieldMapping instance
	 * @throws ADKConfigException If the FieldMapping cannot read expected 
	 * 		values from the DOM Node
	 */
	public static FieldMapping fromXML( 
			ObjectMapping parent, 
			Element element )
		throws ADKConfigException
	{
		if( element == null ){
			throw new IllegalArgumentException( "Argument: 'element' cannot be null" );
		}
		
		String name = element.getAttribute( ATTR_NAME );
		FieldMapping fm = new FieldMapping();
		fm.setNode( element );
		fm.setFieldName( name );
		fm.setDefaultValue( XMLUtils.getAttribute( element, ATTR_DEFAULT ) );
		fm.setAlias( XMLUtils.getAttribute( element, ATTR_ALIAS ) );
		fm.setValueSetID( XMLUtils.getAttribute( element, ATTR_VALUESET ) );
		
		String ifNullBehavior = element.getAttribute( ATTR_IFNULL );
		if( ifNullBehavior.length() > 0 ){
			if( ifNullBehavior.equalsIgnoreCase( "default" ) ){
				fm.setNullBehavior( IFNULL_DEFAULT );
			}
			else if (ifNullBehavior.equalsIgnoreCase( "suppress") ){
				fm.setNullBehavior( IFNULL_SUPPRESS );
			}
			
		}
		
		String dataType = element.getAttribute( ATTR_DATATYPE );
		if( dataType != null && dataType.length() > 0 ){
			try{
				fm.setDataType( SIFDataType.valueOf( SIFDataType.class, dataType.toUpperCase() ) );
			} catch ( IllegalArgumentException iae ){
				ADK.getLog().warn( "Unable to parse datatype '" + dataType + "' for field " + name, iae );
			}
		}
		
		String filtVer = XMLUtils.getAttribute( element, ATTR_SIFVERSION );
		String filtDir = XMLUtils.getAttribute( element, ATTR_DIRECTION );
		
		if( filtVer != null || filtDir != null )
		{
			MappingsFilter filt = new MappingsFilter();
			
			if( filtVer != null )
				filt.setSIFVersion( filtVer );
			
			if( filtDir != null ) 
			{
				if( filtDir.equalsIgnoreCase("inbound") )
					filt.setDirection( MappingsDirection.INBOUND );
				else
				if( filtDir.equalsIgnoreCase("outbound") )
					filt.setDirection( MappingsDirection.OUTBOUND );
				else
					throw new ADKConfigException( 
						"Field mapping rule for " + parent.getObjectType() + "." + fm.getFieldName() + 
						" specifies an unknown Direction flag: '" + filtDir + "'" );
			}
			fm.setFilter( filt );							
		}
		
		

	    //  FieldMapping must either have node text or an <otherid> child
		Node otherIdNode = XMLUtils.getFirstElementIgnoreCase( element, "otherid" );
		if( otherIdNode == null )
		{
			String def = XMLUtils.getText( element );
			if( def != null )
				fm.setRule( def );
			else
				fm.setRule( "" );
		}
		else
		{
			Element otherId = (Element)otherIdNode;
			fm.setRule( OtherIdMapping.fromXML( parent, fm, otherId ), otherId );
		}
		
		return fm;
	}
	
	/**
	 * Writes the values of this FieldMapping to the specified XML Element
	 * 
	 * @param element The XML Element to write values to
	 */
	public void toXML( Element element ){

		XMLUtils.setOrRemoveAttribute( element, ATTR_NAME, fField );
		if( fDataType == SIFDataType.STRING )
		{
			XMLUtils.removeAttribute( element, ATTR_DATATYPE );
		} else {
			XMLUtils.setAttribute( element, ATTR_DATATYPE, fDataType.name() );
		}
		XMLUtils.setOrRemoveAttribute( element, ATTR_DEFAULT, fDefValue );
		XMLUtils.setOrRemoveAttribute( element, ATTR_ALIAS, fAlias );
		XMLUtils.setOrRemoveAttribute( element, ATTR_VALUESET, fValueSet );
			
		MappingsFilter filt = getFilter();
		if( filt != null ) 
		{
			writeFilterToXml( filt, element );
		}
		writeNullBehaviorToXml( fNullBehavior, element );
		getRule().toXML( element );
	}
	
	/**
	 * Writes the mapping filter to an XML Element
	 * @param filter
	 * @param element The XML Element to write the filter to
	 */
	private void writeFilterToXml( MappingsFilter filter, Element element )
	{
		if( filter == null )
		{
			element.removeAttribute( ATTR_SIFVERSION );
			element.removeAttribute( ATTR_DIRECTION );
		}
		else
		{
			if( filter.hasVersionFilter() ){
				element.setAttribute( ATTR_SIFVERSION, filter.getSIFVersion() );
			} else {
				element.removeAttribute( ATTR_SIFVERSION );
			}
				
			MappingsDirection direction = filter.getDirection();
			if( direction == MappingsDirection.INBOUND )
			{
				element.setAttribute( ATTR_DIRECTION, "inbound" );
			}
			else if( direction == MappingsDirection.OUTBOUND )
			{
				element.setAttribute( ATTR_DIRECTION, "outbound" );
			}
			else
			{
				element.removeAttribute( ATTR_DIRECTION );
			}
		}
	}
	
	private void writeNullBehaviorToXml( byte behavior, Element element )
	{
		switch( behavior )
		{	
			case IFNULL_DEFAULT:
				element.setAttribute( ATTR_IFNULL, "default" );
				break;
			case IFNULL_SUPPRESS:
				element.setAttribute( ATTR_IFNULL, "suppress" );
				break;
			default:
				element.removeAttribute( ATTR_IFNULL );
				break;
		}
	}

	/**
	 *  Creates a copy this ObjectMapping instance.<p>
	 * @param newParent The parent that this FieldMapping is associated with
	 * @return A "deep copy" of this object
	 * @throws ADKMappingException 
	 */
	public FieldMapping copy( ObjectMapping newParent )
		throws ADKMappingException
	{
		FieldMapping m = new FieldMapping();
		
		if( fNode != null && newParent.fNode != null ) {
			m.fNode = newParent.fNode.getOwnerDocument().importNode( fNode, false );
		}
		
		m.setFieldName( fField );
		m.setDefaultValue( fDefValue );
		m.setAlias( fAlias );
		m.setValueSetID( fValueSet );
		m.setNullBehavior( fNullBehavior );
		
		if( fFilter != null ) {
			MappingsFilter filtCopy = new MappingsFilter();
			filtCopy.fVersion = fFilter.fVersion;
			filtCopy.fDirection = fFilter.fDirection;
			m.setFilter( filtCopy );
		}
		
		m.setDataType( fDataType );

		if( getRule() != null )
		    m.setRule(getRule().copy( m ));

		return m;
	}

	/**
	 *  Sets the name of the local application field that maps to the SIF Data
	 *  Object element or attribute
	 *
	 *  @param name A field name. (This value will be used as the key in the
	 *      HashMap populated by the Mappings.map methods.)
	 */
	public void setFieldName( String name )
	{
		fField = name;

		if( fNode != null && name != null )
			XMLUtils.setAttribute( fNode, "name", name );
	}

	/**
	 *  Gets the name of the local application field that maps to the SIF Data
	 *  Object element or attribute
	 *
	 *  @return The local application field name. (This value will be used as
	 *      the key in HashMaps populated by the Mappings.map methods)
	 */
	public String getFieldName()
	{
		return fField;
	}
	
	/**
	 * 
	 * Returns the key to a Field Mapping. The Key of a field mapping consists
	 * of it's alias or field name and any filters that are defined
	 * @return A string representing the key of this object
	 */
	public String getKey()
	{
		StringBuilder key = new StringBuilder();
		key.append( fField );
		if( fAlias != null ){
			key.append( '_' );
			key.append( fAlias );
		} 
		if( fFilter != null ){
			if( fFilter.hasDirectionFilter() ){
				key.append( '_' );
				key.append( fFilter.getDirection() );
			}
			if( fFilter.hasVersionFilter() ){
				key.append( '_' );
				key.append( fFilter.getSIFVersion() );
			}
		}
		return key.toString();
	}
	
	/**
	 * 	Sets the ID of the ValueSet that should be used to translate the value 
	 * 	of this field.<p>
	 * 
	 * 	Note: The Mappings classes do not automatically perform translations if
	 * 	this attribute is defined. Rather, it is provided so that agents can 
	 * 	associate a ValueSet with a field in the Mappings configuration file, 
	 * 	and have a means of looking up that association at runtime.<p>
	 *
	 *	@param id The ID of a ValueSet defined in the Mappings (e.g. "EthnicityCodes"). If
	 * set to NULL or "", the ValueSet is removed 
	 *
	 * 	@see #getValueSetID
	 *  
	 * 	@since ADK 1.5
	 */
	public void setValueSetID( String id )
	{
		fValueSet = id;
		if( fValueSet != null && fValueSet.trim().length() == 0 ){
			fValueSet = null;
		}

		if( fNode != null ) {
			XMLUtils.setOrRemoveAttribute( fNode, ATTR_VALUESET, fValueSet );
		}
	}	
	
	/**
	 * 	Gets the ID of the ValueSet that should be used to translate the value 
	 * 	of this field.<p>
	 * 
	 * 	Note: The Mappings classes do not automatically perform translations if
	 * 	this attribute is defined. Rather, it is provided so that agents can 
	 * 	associate a ValueSet with a field in the Mappings configuration file, 
	 * 	and have a means of looking up that association at runtime.<p>
	 * 
	 * 	@return The value passed to the <code>setValueSetID</code> method
	 * 
	 * 	@since ADK 1.5
	 * 
	 * 	@see #setValueSetID
	 */
	public String getValueSetID()
	{
		return fValueSet;
	}	

	/**
	 *  Sets a default value for this field when no corresponding element or
	 *  attribute is found in the SIF Data Object. The Mapping.map methods will
	 *  create an entry in the HashMap with this default value.
	 *
	 *  @param defValue A default string value for this field
	 */
	public void setDefaultValue( String defValue )
	{
		fDefValue = defValue;

		if( fNode != null ){
			XMLUtils.setOrRemoveAttribute( fNode, ATTR_DEFAULT, defValue );
		}
	}


	/**
	 *  Gets the default value for this field when no corresponding element or
	 *  attribute is found in the SIF Data Object. The Mapping.map methods will
	 *  create an entry in the HashMap with this default value.
	 *
	 *  @return The default string value for this field
	 */
	public String getDefaultValue()
	{
	    return fDefValue;
	}
	
	/**
	 * Quickly determines whether this field mapping has a default value defined
	 * without going through the extra work of actually resolving the default value
	 * @return True if this field mapping has a default value defined
	 */
	public boolean hasDefaultValue()
	{
		return fDefValue != null;
	}
	
	/**
	 * @param converter
	 * @param formatter
	 * @return The strongly-typed datatype
	 * @throws ADKMappingException If the default value specified cannot be converted to the
	 * 	proper data type
	 */
	public SIFSimpleType getDefaultValue( SIFTypeConverter converter, SIFFormatter formatter )
		throws ADKMappingException
	{
		if( fDefValue != null && converter != null ){
			try{
				return converter.parse( formatter, fDefValue );
			} catch( ADKParsingException adkpe ){
				throw new ADKMappingException( adkpe.getMessage(), null, adkpe );
			}
		} 
		return null;
	}

	/**
	 *  Defines this FieldMapping to be an alias of another field mapping. During
	 *  the mapping process, the FieldMapping will be applied if the referenced
	 *  field exists in the Map provided to the Mappings.map method. Aliases are
	 *  required when an application wishes to map a single application field to
	 *  more than one element or attribute in the SIF Data Object.<p>
	 *
	 *  To use aliases, create a FieldMapping where the field name is a unique
	 *  name and the alias is the name of an existing field. For example, to map
	 *  an application-defined field named "STUDENT_NUM" to more than one
	 *  element/attribute in the SIF Data Object,
	 *
	 *  <code>
	 *  // Create the default mapping<br/>
	 *  FieldMapping fm = new FieldMapping("STUDENT_NUM","OtherId[@Type='06']");<br/><br/>
	 *  <br/>
	 *  // Create an alias; the field name must be unique<br/>
	 *  FieldMapping fm2 = new FieldMapping("STUDENT_NUM_B","OtherId[@Type='ZZ']=STUDENTID:$(STUDENTNUM)");<br/>
	 *  </code>
	 *
	 *  @param fieldName The name of the field for which this entry is an alias
	 */
	public void setAlias( String fieldName )
	{
		fAlias = fieldName;
		if( fNode != null ){
			XMLUtils.setOrRemoveAttribute( fNode, ATTR_ALIAS, fieldName );
		}
	}

	/**
	 *  Determines if this FieldMapping is an alias of another field mapping.
	 *
	 *  @return The name of the field for which this entry is an alias, or null
	 *      if this FieldMapping is not an alias.
	 *
	 *  @see #setAlias
	 */
	public String getAlias()
	{
	    return fAlias;
	}

	
	/**
	 *  Evaluates this rule against a SIFXpathContexts and returns the text value
	 *  of the element or attribute that satisfied the query.<p>
	 *
	 *  @param xpathContext The SIFXpathContext the rule is evaluated against
	 * 	@param version The SIFVersion to use when lookup up the value
	 * @param returnDefault True if the default value should be returned when there is no value
	 *  @return The value of the element or attribute that satisfied the
	 *      query, or null if no match found. If the <code>returnDefault<code> parameter
	 *      is set to true, the default value will be returned, if specified
	 *  @exception ADKSchemaException is thrown if the rule associated with this
	 *      object is invalid or the default value cannot be parsed
	 */
	public SIFSimpleType evaluate( SIFXPathContext xpathContext, SIFVersion version, boolean returnDefault )
		throws ADKSchemaException
	{
		SIFSimpleType value = null;
		if( getRule() != null ){
			value = getRule().evaluate( xpathContext, version );
		}
		if( value == null && fDefValue != null && returnDefault ){
			// TODO: Support all data types
			try
			{
			return fDataType.getConverter().parse( ADK.getTextFormatter(), fDefValue  );
			} catch( ADKParsingException adkpe ){
				throw new ADKSchemaException( "Error parsing default value: '" + fDefValue +  "' for field " + fField + " : " + adkpe, null, adkpe );
			}
		}
		
		return value;
	}
	

	/**
	 *  Sets this FieldMapping rule to an XPath-like query string
	 *  @param definition An XPath-like query string described by the
	 *      <code>SIFDTD.lookupByXPath</code> method
	 */
	public void setRule( String definition )
	{
		fRule = new XPathRule( definition );

		if( fNode != null )
			XMLUtils.setText( fNode, definition );
	}

	/**
	 *  Sets this object's rule to an "&lt;OtherId&gt; rule"
	 *  @param otherId An OtherIdMapping object that describes how to select
	 *      a &lt;OtherId&gt; element during a mapping operation
	 */
	public void setRule( OtherIdMapping otherId )
	{
		fRule = new OtherIdRule( otherId );
		
		if( fNode != null ) {
			fRule.toXML( fNode );
		}
	}

	/**
	 * Sets an OtherId rule for this FieldMapping
	 * @param otherId The OtherIdMapping instance to use for this field mapping
	 * @param node The DOM Node associated with the OtherId mapping
	 */
	public void setRule( OtherIdMapping otherId, Node node )
	{
		fRule = new OtherIdRule( otherId, node );
	}

	/**
	 *  Gets the field mapping rule
	 *  @return A Rule instance
	 */
	public Rule getRule()
	{
		return fRule;
	}
	
	/**
	 * 	Sets optional filtering attributes. This field mapping rule will
	 * 	only be applied if the filters match the values passed to the
	 * 	<code>Mappings.map</code> method at runtime.<p>
	 * 
	 * 	@param filter A MappingsFilter instance (null to clear the current
	 * 		filter attributes)
	 * 	@see #getFilter
	 * 	@since ADK 1.5 
	 */
	public void setFilter( MappingsFilter filter )
	{
		fFilter = filter;
		if( fNode != null ){
			writeFilterToXml( filter, (Element)fNode );
		}
	}
	
	/**
	 * 	Gets optional filtering attributes.<p>
	 * 	@return A MappingsFilter instance or null if none defined for
	 * 		this field rule
	 * 	@see #setFilter
	 * 	@since ADK 1.5
	 */
	public MappingsFilter getFilter()
	{
		return fFilter;
	}
	
	/**
	 * Sets the behavior that the field mapping should follow when mapping
	 * a <code>null</code> value. The value set should be one of the the
	 * <code>IFNULL_XXX</code> constants defined in this class. The default
	 * behavior for null values if this value is not set is to use the 
	 * default value, if present.
	 * @param behavior  One of the the <code>IFNULL_XXX</code> constants defined in this class
	 */
	public void setNullBehavior( byte behavior )
	{
		if( behavior < IFNULL_UNSPECIFIED || behavior > IFNULL_SUPPRESS ){
			throw new IllegalArgumentException( "Value must be one of the FieldMapping.IFNULL_XXX constants." );
		}
		fNullBehavior = behavior;
		if( fNode != null ){
			writeNullBehaviorToXml( behavior, (Element)fNode );
		}
	}
	
	/**
	 * Returns the behavior that this field mapping will follow when mapping
	 * a <code>null</code> value.
	 * @return One of the the <code>IFNULL_XXX</code> constants defined in this class
	 */
	public byte getNullBehavior()
	{
		return fNullBehavior;
	}

	/**
	 * Sets the name of the data type this field represents.<p>
	 * 
	 * This datatype is used if the datatype cannot be derived from the field mapping. 
	 * If this value is null, this instance will use the <code>STRING</code> data type. 
	 * @param dataType
	 */
	public void setDataType( SIFDataType dataType) {
		this.fDataType = dataType;
		
		
		if( fNode != null ) {
			if( fDataType == SIFDataType.STRING )
			{
				XMLUtils.removeAttribute( fNode, ATTR_DATATYPE );
			} else {
				XMLUtils.setAttribute( fNode, ATTR_DATATYPE, fDataType.name() );
			}
		}
	}

	/**
	 * Returns the datatype that this FieldMapping represents.<p>
	 * 
	 *  If the <code>datatype<code> attribute is not set on the 
	 *  <code>&lt;field&gt;</code> mapping, a default of {@link SIFDataType#STRING}
	 *  is assumed. This value is primarily used for assigning default values
	 *  to field mappings.
	 * @return The Datatype associated with this FieldMapping
	 */
	public SIFDataType getDataType() {
		return fDataType;
	}

	public void setRule(Rule fRule) {
		this.fRule = fRule;
	}
	
	@Override
	public String toString()
	{
		return "Field: " + this.getKey() + ":" + this.getRule().toString();
	}
	
}
