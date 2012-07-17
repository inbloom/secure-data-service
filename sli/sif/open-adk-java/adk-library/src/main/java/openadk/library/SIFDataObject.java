//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


import java.io.*;

import openadk.library.common.*;
import openadk.library.datamodel.DatamodelDTD;
import openadk.library.datamodel.SIF_Metadata;
import openadk.library.tools.mapping.FieldAdaptor;
import openadk.library.tools.xpath.SIFXPathContext;

import org.apache.commons.jxpath.Variables;

/**
 *  The abstract base class for all root-level SIF Data Object classes.
 *  SIFDataObject encapsulates top-level data objects defined by SIF Working
 *  Groups, including <code>&lt;StudentPersonal&gt;</code>,
 *  <code>&lt;LibraryPatronStatus&gt;</code>, <code>&lt;BusInfo&gt;</code>,
 *  and so on.
 *  <p>
 *
 *  <b>Setting Elements &amp; Attributes of a SIF Data Object</b><p>
 *
 *  There are two general approaches to getting and setting the element/attribute
 *  values of a SIFDataObject. First, you can call the getXxx and setXxx methods
 *  of the subclass to manipulate the elements and attributes in an object-oriented
 *  fashion. For example, to assign a first and last name to a StudentPersonal
 *  object, create a Name object and attach it to the StudentPersonal with the
 *  setName method:<p>
 *
 *  <code>&nbsp;&nbsp;&nbsp;&nbsp;// Build a StudentPersonal object<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;StudentPersonal sp = new StudentPersonal();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setRefId( ADK.makeGUID() );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setName( new Name( "Davis", "Alan" ) );</br>
 *  </code><p>
 *
 *  The second approach to getting and setting element/attribute values is to
 *  call the <code>setElementOrAttribute</code> and <code>getElementOrAttribute</code>
 *  methods, which accept an XPath-like query string that identifies a specific
 *  SIF element or attribute relative to the SIFDataObject. (See also the
 *  Mappings class for a higher-level mechanism that performs much of the work
 *  involved in dynamically mapping application fields to SIF elements and
 *  attributes).<p>
 *
 *  <code>&nbsp;&nbsp;&nbsp;&nbsp;// Build a StudentPersonal object<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;StudentPersonal sp = new StudentPersonal();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setRefId( ADK.makeGUID() );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setElementOrAttribute( "Name[@Type='02']/LastName", "Davis", null );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setElementOrAttribute( "Name[@Type='02']/FirstName", "Brian", null );<br/>
 *  </code><p>
 *
 *  XPath-like query strings can include substitution tokens and can even call
 *  static Java methods. For example, the following uses name/value pairs defined
 *  in a Map to select the first and last name. The static <code>capitalize</code>
 *  method of the "MyFunctions" class is called to capitalize the last name:
 *  <p>
 *
 *  <code>&nbsp;&nbsp;&nbsp;&nbsp;// Prepare a table with field values<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;HashMap values = new HashMap();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;values.put( "LASTNAME", "Davis" );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;values.put( "FIRSTNAME", "Brian" );<br/><br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;// Build a StudentPersonal object<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;StudentPersonal sp = new StudentPersonal();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setRefId( ADK.makeGUID() );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setElementOrAttribute( "Name[@Type='02']/LastName=@MyFunctions.capitalize( $(LASTNAME) )", null, values );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;sp.setElementOrAttribute( "Name[@Type='02']/FirstName=$(FIRSTNAME)", null, values );<br/>
 *  </code><p>
 *
 *  <b>Object Type</b><p>
 *
 *  The <code>getObjectType</code> method returns the an ElementDef constant
 *  from the SIFDTD class that identifies the SIF Data Object. The the
 *  <code>getObjectTag</code> convenience method returns the element tag name of
 *  the object for the version of SIF associated with the instance. For example,
 *  <p>
 *
 *  <code>
 *  &nbsp;&nbsp;&nbsp;&nbsp;// Lookup a Topic instance<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;SIFDataObject data = new SIFDataObject( ADK.DTD().STUDENTPERSONAL );<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;TopicFactory factory = myAgent.getTopicFactory();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;Topic t = factory.getInstance( data.getObjectType() );<br/>
 *  </code>
 *  <p>
 *
 *  <b>SIF Version</b><p>
 *
 *  Each SIFDataObject is associated with a SIFVersion instance. The version
 *  is used by the SIFWriter class when rendering the object as XML. By default,
 *  it is assumed to be the version of SIF in effect for this agent; that is,
 *  the value passed to the <code>ADK.initialize</code> method. However, to
 *  support mixed environments where an agent may send and receive objects
 *  using different versions of SIF, the version may be changed by the ADK
 *  during message processing:<p>
 *
 *  <ul>
 *      <li>
 *          When constructing a SIFDataObject instance from parsed XML content,
 *          the SIFParser class sets its SIFVersion to the version identified by
 *          the <i>xmlns</i> attribute of the <code>&lt;SIF_Message&gt;</code>
 *          envelope.<br/><br/>
 *      </li>
 *      <li>
 *          The version may be changed by the ADK prior to rendering a
 *          SIFDataObject as XML. For example, when your agent responds to a
 *          &lt;SIF_Request&gt; message that specifically identifies a version
 *          to use for the results, the ADK will change the version of the
 *          SIFDataObject when generating &lt;SIF_Response&gt; messages. Once
 *          messages have been generated, it restores the SIFVersion to its
 *          original setting.
 *          <br/><br/>
 *      </li>
 *      <li>
 *          An agent may manually change the SIFVersion associated with a
 *          SIFDataObject by calling the <code>setSIFVersion</code> method.
 *      </li>
 *  </ul>
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public abstract class SIFDataObject extends SIFElement
{
	/**
	 *  The version of SIF that should be used to render this SIFDataObject and
	 *  its child elements. If this SIFDataObject is the result of parsing a SIF
	 *  message, this is the version of SIF identified by the message envelope.
	 *  The version is initially set by the constructor but may be changed at
	 *  any time by the <code>setVersion</code> method.
	 */
	protected transient SIFVersion fVersion;

	/**
	 *  Constructs a SIFDataObject
	 *  @param version The version of SIF that should be used to render this
	 *      SIFDataObject and its child elements. If this SIFDataObject is the
	 *      result of parsing a SIF message, this is the version of SIF
	 *      identified by the message envelope.
	 *
	 *  @param def The ElementDef that provides metadata for this element
	 */
    public SIFDataObject( SIFVersion version, ElementDef def )
	{
		super(def);

		fVersion = version;
    }

	/**
	 *  Gets this object's <i>RefId</i>.<p>
	 *
	 *  Most SIF Data Object elements define a RefId value to uniquely identify
	 *  the object. However, some objects such as SIF_ZoneStatus and StudentMeal
	 *  do not have a RefId. For these, a blank string will be returned.
	 *  <p>
	 *
	 *  @return The value of this object's <i>RefId</i> element
	 */
	public String getRefId() {
		return "";
	}

	/**
	 * Returns the unique identifier that was set to the {@link #setXmlId(String)} method.<p>
	 * If the xmlId is not set, SIFDataObject will return the value of the
	 * @see #getRefId() property.
	 * @return a string value that uniquely identifies this object to the application.
	 */
	public String getXmlId()
	{
		String id = super.getXmlId();
		if( id == null ){
			return getRefId();
		}
		return id;
	}



	/**
	 *  Gets the ElementDef that identifies this SIF Data Object type
	 *  @return An ElementDef constant defined by the SIFDTD class
	 */
	public ElementDef getObjectType() {
		return getElementDef();
	}

	/**
	 *  Gets the element tag name of this object
	 *  @return The element tag for the version of SIF associated with the object
	 */
	public String getObjectTag() {
		return getElementDef().tag(fVersion);
	}

	/**
	 *  Gets the version of SIF that should be used to render this SIFDataObject
	 *  and its child elements
	 *  @return A SIFVersion
	 */
	public SIFVersion getSIFVersion() {
		return fVersion;
	}

	/**
	 *  Changes the version of SIF that should be used to render this SIFDataObject
	 *  and its children. The calling thread may change the way a SIFDataObject
	 *  is rendered by calling this method. It is recommended the version be
	 *  restored to its original value after rendering is completed.
	 * @param version The version of SIF that should be used
	 */
	public void setSIFVersion( SIFVersion version ) {
		fVersion = version;
	}

	/**
	 *  Sets an element or attribute value
	 *
	 *  NOTE: This method makes calls to SIFXPathContext. If multiple calls to
	 *  <code>setElementOrAttribute</code> are being done, it is much more efficient to create
	 *  a new <code>SIFXPathContext</code> by calling <code>SIFXPathContext.newInstance(sdo)</code> and then
	 *  calling <code>.setElementorAttributeon</code> on that SIFXPathContext instance
	 *
	 *  @param xpath An XPath-like query string that identifies identifies
	 *      the element or attribute to set. The string must reference elements
	 *      and attributes by their <i>version-independent</i> names.
	 *  @param value The value of the element or attribute
	 * @throws ADKSchemaException IF the xpath is not valid
	 */
	public void setElementOrAttribute( String xpath, String value )
		throws ADKSchemaException
	{
		this.setSIFVersion( ADK.getSIFVersion() );
		SIFXPathContext spc = SIFXPathContext.newSIFContext( this );
		spc.setElementOrAttribute( xpath, value );
	}

	/**
	 *  Sets an element or attribute value identified by an XPath-like query string.<p>
	 *
	 *  NOTE: This method makes calls to SIFXPathContext. If multiple calls to
	 *  <code>setElementOrAttribute</code> are being done, it is much more efficient to create
	 *  a new <code>SIFXPathContext</code> by calling <code>SIFXPathContext.newInstance(sdo)</code> and then
	 *  calling <code>.setElementorAttributeon</code> on that SIFXPathContext instance
	 *
	 *  @param xpath An XPath-like query string that identifies the element or
	 *      attribute to set. The string must reference elements and attributes
	 *      by their <i>version-independent</i> names.
	 *  @param value The value to assign to the element or attribute if the
	 *      query string does not set a value; may be null
	 *  @param adaptor A data source may be used for variable
	 *      substitutions within the query string
	 * @throws ADKSchemaException If the xpath is not valid
	 */
	public void setElementOrAttribute( String xpath, String value, FieldAdaptor adaptor )
		throws ADKSchemaException
	{
		this.setSIFVersion( ADK.getSIFVersion() );
		SIFXPathContext spc = SIFXPathContext.newSIFContext( this );
		if( adaptor instanceof Variables ){
			spc.setVariables( (Variables)adaptor );
		}
		spc.setElementOrAttribute( xpath, value );
	}

	/**
	 *  Sets an element or attribute value identified by an XPath-like query string.<p>
	 *
	 *  NOTE: This method makes calls to SIFXPathContext. If multiple calls to
	 *  <code>setElementOrAttribute</code> are being done, it is much more efficient to create
	 *  a new <code>SIFXPathContext</code> by calling <code>SIFXPathContext.newInstance(sdo)</code> and then
	 *  calling <code>.setElementorAttributeon</code> on that SIFXPathContext instance
	 *
	 *  @param xpath An XPath-like query string that identifies the element or
	 *      attribute to set. The string must reference elements and attributes
	 *      by their <i>version-independent</i> names.
	 *  @param value The value to assign to the element or attribute if the
	 *      query string does not set a value; may be null
	 *  @param valueBuilder a ValueBuilder implementation that evaluates
	 *      expressions in XPath-like query strings using name/value pairs in
	 *      the <i>variables</i> map
	 * @throws ADKSchemaException If the xpath is not valid
	 */
	public void setElementOrAttribute( String xpath, String value, ValueBuilder valueBuilder )
		throws ADKSchemaException
	{
		value = valueBuilder == null ? value : valueBuilder.evaluate( value );
		setElementOrAttribute( xpath, value );
	}

	/**
	 *  Gets an element or attribute value identified by an XPath-like query string.<p>
	 *
	 *  @param xpath An XPath-like query string that identifies the element or
	 *      attribute to get. The string must reference elements and attributes
	 *      by their <i>version-independent</i> names.
	 *  @return An Element instance encapsulating the element or attribute if
	 *      found. If not found, <code>null</code> is returned. To retrieve the
	 *      value of the Element, call its <code>getTextValue</code> method.
	 * @throws ADKSchemaException If the xpath is not valid
	 */
	public Element getElementOrAttribute( String xpath )
		throws ADKSchemaException
	{
		// XPath Navigation using this API causes the object to have to
		// remember the SIF Version being evaluated
		this.setSIFVersion( ADK.getSIFVersion() );
		SIFXPathContext spc = SIFXPathContext.newSIFContext( this );
		return spc.getElementOrAttribute( xpath );
	}

	/**
	 * 	Sets a SIF_ExtendedElement.<p>
	 * 	@param name The element name
	 * 	@param value The element value
	 *
	 * 	@since ADK 1.5
	 */
	public void addSIFExtendedElement( String name, String value )
	{
		if( CommonDTD.SIF_EXTENDEDELEMENTS == null || name == null || value == null )
			return;

		SIF_ExtendedElement ele = null;

		//	Lookup existing SIF_ExtendedElements container
		SIF_ExtendedElements see = (SIF_ExtendedElements)getChild( CommonDTD.SIF_EXTENDEDELEMENTS );
		if( see == null )
		{
			//	Create a new SIF_ExtendedElements container
			see = new SIF_ExtendedElements();
			addChild( see );
		}
		else
		{
			//	Lookup existing SIF_ExtendedElement with this name
			ele = see.getSIF_ExtendedElement( name );
		}

		//	Create/update SIF_ExtendedElement
		if( ele == null ) {
			ele = new SIF_ExtendedElement( name, value );
			see.addChild( ele );
		}
		else
			ele.setTextValue( value );
	}

	/**
	 * 	Gets all SIF_ExtendedElements/SIF_ExtendedElement children of this object.<p>
	 * 	@return An array of SIF_ExtendedElement instances. If no SIF_ExtendedElements
	 * 		child element was found, an empty array is returned
	 *
	 * 	@since ADK 1.5
	 */
	public SIF_ExtendedElement[] getSIFExtendedElements()
	{
		if( CommonDTD.SIF_EXTENDEDELEMENTS == null )
			return new SIF_ExtendedElement[0];

		SIF_ExtendedElements container = (SIF_ExtendedElements)getChild( CommonDTD.SIF_EXTENDEDELEMENTS );
		if( container == null )
			return new SIF_ExtendedElement[0];

		return container.getSIF_ExtendedElements();
	}

	/**
	 * Sets an array of <code>SIF_ExtendedElement</code> objects. All existing
	 * <code>SIF_ExtendedElement</code> instances
	 * are removed and replaced with this list. Calling this method with the
	 * parameter value set to null removes all <code>SIF_ExtendedElements</code>.
	 * @param elements The SIF_Extended elements instances to set on this object
	 *
	 *  @since ADK 1.5
	 */
	public void setSIFExtendedElements(SIF_ExtendedElement[] elements) {
		getSIFExtendedElementsContainer().setSIF_ExtendedElements(elements);
	}


	/**
	 * 	Gets the SIF_ExtendedElements container in which all child SIF_ExtendedElement
	 * 	elements are placed by the {@link #addSIFExtendedElement(String, String)}
	 * 	method. Note if there is currently no container element, one is created and
	 * 	added as a child of the SIFDataObject.<p>
	 *
	 * 	This method is provided as a convenience to agents that wish to obtain the
	 * 	SIF_ExtendedElements container element in order to manually add extended
	 * 	elements to it. This is useful, for example, if you need to call methods on
	 * 	the extended element before adding it to the container (e.g. the <code>setDoNotEncode</code>
	 * 	method). The equivalent functionality is possible by making this call:<p>
	 *
	 * 	<code>
	 *	SIF_ExtendedElements container = (SIF_ExtendedElements)getChild( SIFDTD.SIF_EXTENDEDELEMENTS );<br/>
	 *	</code>
	 *
	 *	@return The SIF_ExtendedElements container element, which is created and
	 *		added as a child to this SIFDataObject if it does not currently exist.
	 */
	public SIF_ExtendedElements getSIFExtendedElementsContainer()
	{
		SIF_ExtendedElements container = (SIF_ExtendedElements)getChild( CommonDTD.SIF_EXTENDEDELEMENTS.name() );
		if( container == null ) {
			container = new SIF_ExtendedElements();
			addChild( container );
		}

		return container;
	}

	/**
	 * Sets the SIF_ExtendedElements container for this object.<P>
	 * Normally, agents can just call {@link #addSIFExtendedElement(String, String)},
	 * which automatically creates a SIF_ExtendedElements container, if necessary and
	 * allows for easy addition of SIF_ExtendedElements.<p>
	 * This method is provided as a convenience to agents that need more control or
	 * wish to set or completely replace the existing SIF_ExtendedElements container.
	 * @param container The new SIF_ExtendedElements container
	 */
	public void setSIFExtendedElementsContainer( SIF_ExtendedElements container )
	{
	    removeChild( CommonDTD.SIF_EXTENDEDELEMENTS );
	    addChild( container );
	}


	/**
	 * 	Gets the SIF_Metadata for this object.
	 *	@return The SIF_Metadata  element, which is created and
	 *		added as a child to this SIFDataObject if it does not currently exist.
	 */
	public SIF_Metadata getSIFMetadata()
	{
		SIF_Metadata metadata = (SIF_Metadata)getChild( DatamodelDTD.SIF_METADATA.name() );
		if( metadata == null ) {
			metadata = new SIF_Metadata();
			setSIFMetadata( metadata );
		}
		return metadata;
	}

	/**
	 * Sets the SIF_Metadata for this object.<P>
	 * @param metadata The new SIF_Metadata object
	 */
	public void setSIFMetadata( SIF_Metadata metadata )
	{
	    removeChild( DatamodelDTD.SIF_METADATA );
	    addChild( metadata );
	}



	/**
	 * 	Gets the SIF_ExtendedElement with the specified Name attribute.<p>
	 * 	@param name The value of the SIF_ExtendedElement/@Name attribute to search for
	 * 	@return The SIF_ExtendedElement that has a Name attribute matching the
	 * 		<i>name</i> parameter, or null if no such element exists
	 *
	 * 	@since ADK 1.5
	 */
	public SIF_ExtendedElement getSIFExtendedElement( String name )
	{
		if( CommonDTD.SIF_EXTENDEDELEMENTS == null || name == null )
			return null;

		SIF_ExtendedElements container = (SIF_ExtendedElements)getChild( CommonDTD.SIF_EXTENDEDELEMENTS );
		if( container == null )
			return null;

		return container.getSIF_ExtendedElement( name );
	}

	/**
	 *  Returns the XML representation of this SIF Data Object
	 *  @return The XML representation of this SIF Data Object
	 */
	public String toXML()
	{
		StringWriter out = null;
		SIFWriter w = null;

		try
		{
			out = new StringWriter();
			w = new SIFWriter(out,false);
			w.write(this);
			w.flush();

			return out.toString();
		}
		catch( Exception e )
		{
			// TODO: This shouldn't be eating the exception
			System.out.println("Unlogged Exception: " + e.getLocalizedMessage());
			return "";
		}
		finally
		{
			try {
				if( out != null )
	    			out.close();
		    	if( w != null )
			    	w.close();
			} catch( Exception ignored ) {
				// TODO: This shouldn't be eating the exception
				System.out.println("Unlogged Exception: " + ignored.getLocalizedMessage());
			}
		}
	}


	private void writeObject( java.io.ObjectOutputStream out )
		throws IOException
	{
		out.defaultWriteObject();
		out.writeUTF( getSIFVersion().toString() );
	}

	private void readObject( java.io.ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		String versionString = in.readUTF();
		fVersion = SIFVersion.parse( versionString );
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SIFDataObject clonedObject = (SIFDataObject) super.clone();
		clonedObject.setSIFVersion( fVersion );
		return clonedObject;
	}


	/**
	 *  Changes the default behavior of SIFElement so that
	 *  the root element and attributes of a SIFData object are always
	 *  written, even if setChanged(false) is called. Callers can
	 *  call this method to ensure that the root elements and attibutes
	 *  are always rendered even if a previous call to setChanged(false) was done.
	 *
	 *  The reason for this is that the ADK uses the change tracking to determine
	 *  whether an element should be written out or not when a Query is received with
	 *  conditions. No matter what, even if the object does not have element specified as
	 *  a query condition, the root SIFDataObjectElement and its mandatory attributes
	 *  should still b rendered.
	 */
	public void ensureRootElementRendered( )
	{
		// Make sure that the root element and attributes are written out
		this.setChildChanged();
		synchronized( fSyncLock ){
			if( fFields != null ){
				for( SimpleField field : fFields.values() ){
					if( field.getElementDef().isAttribute( this.getSIFVersion() ) ){
						field.setChanged( true );
					}
				}
			}
		}
	}

}
