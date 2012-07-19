//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.cfg;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import openadk.library.*;
import openadk.library.tools.mapping.ADKMappingException;
import openadk.library.tools.mapping.Mappings;
import openadk.util.XMLUtils;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;



/**
 *  Implements an XML-based configuration file for agents.<p>
 *
 *  To read the configuration file into memory, call the <code>read</code>
 *  method and specify whether to validate the XML document against a DTD. The
 *  methods of the AgentConfig class do not perform validation of required
 *  elements and attributes or their values, so it is recommended that you
 *  validate against a DTD. A default DTD is located in the <code>docs</code>
 *  directory.
 *  <p>
 *
 *  <b>Applying the Configuration</b>
 *
 *  Once the configuration file is parsed, you can inspect the elements and
 *  attributes by calling the various methods of this class. Or, you can call
 *  the <code>apply</code> method prior to agent initialization to automatically
 *  apply the configuration settings to your agent as follows:
 *  <p>
 *
 *  <ul>
 *      <li>
 *          The root <code>&lt;agent&gt;</code> attributes are inspected to set
 *          the SourceId and default SIF Version for the agent.
 *      </li>
 *      <li>
 *          For each <code>&lt;property&gt;</code> element, the associated
 *          property is set in the agent's AgentProperties object.
 *      </li>
 *      <li>
 *          The agent's transports are configured according to the properties
 *          of the <code>&lt;transport&gt;</code> elements
 *      <li>
 *          A zone is created for each <code>&lt;zone&gt;</code> element
 *      </li>
 *  </ul>
 *
 *  Most agents will extend or modify this class to add additional configuration
 *  settings specific to the agent. The source code can be found in the ADK's
 *  <code>extras</code> directory.<p>
 *
 *  <b>Properties</b>
 *
 *  Agent and Zone properties can be defined at three levels:<p>
 *
 *  <ul>
 *      <li>
 *          <code>&lt;property&gt;</code> elements of the root <code>&lt;agent&gt;</code>
 *          node are intended to serve as global defaults. When then applyProperties
 *          method is called, these property values are assigned to the Agent's
 *          AgentProperties object.
 *      </li>
 *      <li>
 *          <code>&lt;property&gt;</code> elements of a zone <code>&lt;template&gt;</code>
 *          are inherited by zones that reference that template. When the applyZones
 *          method is called, these property values are assigned to the Zone's
 *          AgentProperties object unless specifically overridden by <code>&lt;property&gt;</code>
 *          elements defined by each <code>&lt;zone&gt;</code>
 *      </li>
 *      <li>
 *          <code>&lt;property&gt;</code> elements of a <code>&lt;zone&gt;</code>
 *          are specific to that zone and override any property defined in the
 *          template referenced by the zone
 *      </li>
 *  </ul>
 *
 *  <b>Updating the Configuration Programmatically</b>
 *
 *  The elements and attributes of the configuration file are represented as DOM
 *  Nodes that can be programmatically updated using the standard DOM interface.
 *  Use the convenience methods getZoneNode, getZoneTemplateNode, getTransportNode,
 *  and so on to obtain a reference to a DOM Node instance for a group of
 *  configuration settings. The <code>getProperty</code> and <code>setProperty</code>
 *  methods can also be used to manipulate the <code>&lt;property&gt;</code>
 *  children of an element. Static helper routines from the
 *  com.edustructures.util.XMLUtils class may also be used to get and set
 *  element attributes and text values.
 *  <p>
 *
 *  Call the <code>save</code> method to write the configuration to a file.
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class AgentConfig
{
    /** Namespaces feature id (http://xml.org/sax/features/namespaces). */
    protected static final String NAMESPACES_FEATURE_ID = "http://xml.org/sax/features/namespaces";

    /** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";

    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";

    /** Schema full checking feature id (http://apache.org/xml/features/validation/schema-full-checking). */
    protected static final String SCHEMA_FULL_CHECKING_FEATURE_ID = "http://apache.org/xml/features/validation/schema-full-checking";

	/** Xerces input buffer size (default is 2K) **/
	protected static final String XERCES_BUFFERSIZE_PROP_ID = "http://apache.org/xml/properties/input-buffer-size";

	/**
	 *  The configuration file
	 *  @see #read
	 */
	private File fSrc;

	/**
	 *  The DOM document produced by reading the configuration file
	 *  @see #read
	 */
	private Document fDoc;

	/**
	 *  The root <code>&lt;mappings&gt;</code> object defines rules for mapping
	 *  application database fields to SIF Data Object elements and attributes.
	 *  Refer to the Mappings class for details.
	 */
	private Mappings fMappings;


	private String CRLF = "\r\n";

	/**
	 *  Constructor
	 */
	public AgentConfig()
	{
	}

	/**
	 *  Read a configuration file into memory.<p>
	 *
	 *  @param file The path to the configuration file
	 *  @param validate true to validate the configuration file
	 *
	 *  @return A DOM Document encapsulating the configuration file. This
	 *      object may also be obtained by calling <code>getDocument</code>
	 *  @exception IOException thrown if an error occurs reading the file
	 *  @exception ADKException thrown if the ADK is not initialized
	 *  @exception ADKMappingException thrown if an error occurs parsing any
	 *      <code>&lt;mappings&gt;</code> elements
	 *  @exception ADKConfigException thrown if the configuration file fails
	 *      to parse or is not valid when validation is turned on
	 *
	 *  @see #getDocument
	 */
	public Document read( String file, boolean validate )
		throws IOException,
		       ADKConfigException,
			   ADKMappingException,
			   ADKException
	{
		if( !ADK.isInitialized() )
			throw new ADKException( "ADK is not initialized",null );

		String fileName = JUnitHelper.fixPathSeparator(file);
		fSrc = new File(fileName);

		if( fSrc.isDirectory() )
			throw new IllegalArgumentException("The configuration file \""+file+"\" is a directory");
		if( !fSrc.exists() )
			throw new FileNotFoundException(fileName);

		try
		{
			//  Use the Xerces parser
		    org.apache.xerces.parsers.DOMParser parser =
				new org.apache.xerces.parsers.DOMParser();

			//  Turn validation on if requested
	        parser.setFeature( VALIDATION_FEATURE_ID, validate );

	        //  Parse into a DOM Document
	    	parser.parse( fSrc.toURI().toString() );
			fDoc = parser.getDocument();
		}
		catch( Exception ex )
		{
			throw new ADKConfigException( ex.toString() );
		}

		//  Build the Mappings object
		fMappings = new Mappings();
		fMappings.populate( fDoc, getRootNode() );

		return fDoc;
	}

	/**
	 *  Saves the XML document back to the file from which it was read.<p>
	 *  @exception IOException thrown if an error occurs writing the document
	 */
	public void save()
		throws IOException,
			   ADKConfigException
	{
	    FileOutputStream out = null;
		try {
			out = new FileOutputStream( fSrc );
			save( out );
		} 
		finally {
			if( out != null )
				out.close();
		}
	}

	/**
	 *  Saves the XML document to the specified output stream.<p>
	 *  @param out The OutputStream to which the XML document is written
	 *  @exception IOException thrown if an error occurs writing the document
	 */
	public void save( OutputStream out )
		throws IOException,
			   ADKConfigException
	{
		Document doc = getDocument();
		XMLSerializer serializer = getSerializer( doc );
		serializer.setOutputByteStream( out );
		serializer.serialize(doc);
	}
	
	private XMLSerializer getSerializer( Document doc )
	{
		OutputFormat format = new OutputFormat(doc);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(4);
		format.setLineSeparator(CRLF);
		XMLSerializer serializer = new XMLSerializer( format );
		return serializer;
	}
	

	/**
	 *  Saves the XML document to the specified Writer.<p>
	 *  @param out The Writer to which the XML document is written
	 *  @exception IOException thrown if an error occurs writing the document
	 */
	public void save( BufferedWriter out )
		throws IOException,
			   ADKConfigException
	{
		Document doc = getDocument();
		XMLSerializer serializer = getSerializer( doc );
		serializer.setOutputCharStream( out );
		serializer.serialize(doc);
	}

	/**
	 *  Determines if the configuration file has been loaded
	 *  @return true if the <code>read</code> method has been successfully called
	 */
	public boolean isLoaded()
	{
		return fDoc != null;
	}

	/**
	 *  Applies the settings in the configuration to the Agent. This method
	 *  should be called at most once during agent startup, usually from
	 *  <coce>Agent.initialize</code><p>
	 *
	 *  <ul>
	 *      <li>
	 *          For each <code>&lt;property&gt;</code> element, the associated
	 *          property is set in the agent properties.
	 *      </li>
	 *      <li>
	 *          A Zone instance is created for each <code>&lt;zone&gt;</code>
	 *          element. Any <code>&lt;property&gt</code> elements defined for
	 *          the zone are set in the zone's AgentProperties object. An array
	 *          of all zones created is returned. The caller can then join those
	 *          zones to topics.
	 *      </li>
	 *  </ul>
	 *
	 *  @param agent The Agent to apply the configuration settings to
	 *
	 *  @param overwrite When true, properties defined in the configuration file
	 *      replace properties of the same name that are already defined in the
	 *      agent properties
	 */
	public Zone[] apply( Agent agent, boolean overwrite )
		throws ADKException, ADKConfigException
	{
		applyProperties(agent,overwrite);
		applyTransports(agent,overwrite);

		agent.setConfigurationSource( this );
		
		return applyZones(agent);
	}

	/**
	 *  Applies <code>&lt;property&gt;</code> elements to the Agent
	 *
	 *  @param agent The Agent to apply properties to
	 *  @param overwrite When true, properties defined in the configuration file
	 *      replace properties of the same name that are already defined in the
	 *      agent properties
	 */
	public void applyProperties( Agent agent, boolean overwrite )
		throws ADKException, ADKConfigException
	{
		//  Set the agent's ID if specified in the configuration
		String sourceId = getSourceId();
		if( sourceId != null )
			agent.setId(sourceId);

		//  Apply root-level properties to the agent's AgentProperties
		getAgentProperties( agent.getProperties() );
	}

	/**
	 *  Applies <code>&lt;transport&gt;</code> elements to the Agent.<p>
	 *
	 *  This method selects the <code>&lt;transport&gt;</code> child of the root
	 *  <code>&lt;agent&gt;</code> with the <i>enabled</i> attribute set to true.
	 *  More than one transport may be enabled; if more than one is enabled the first
	 *  definition that is enabled is considered the agent's default transport protocol. The
	 *  <code>&lt;property&gt;</code> elements are then assigned to the agent's
	 *  default transport properties for this protocol.
	 *  <p>
	 *
	 *  @param agent The Agent to apply the transport properties to
	 *  @param overwrite When true, properties defined in the configuration file
	 *      replace properties of the same name that are already defined in the
	 *      agent's default transport properties
	 * @throws ADKException 
	 * @throws ADKConfigException 
	 */
	public void applyTransports( Agent agent, boolean overwrite )
		throws ADKException,
			   ADKConfigException
	{
		//
		//  Enumerate all <transport> elements of the root; ignore elements
		//  where the 'enabled' attribute is set to false
		//
		List<Node> v = XMLUtils.getElementsByTagName( getRootNode(), "transport", false );

		for( int i = 0; i < v.size(); i++ )
		{
			Node n = v.get(i);

			String proto = XMLUtils.getAttribute( n, "protocol" );

			//  Get default properties for this transport protocol
			TransportProperties tp = agent.getDefaultTransportProperties( proto );
			if( tp == null )
				throw new ADKConfigException( "Transport protocol not supported: " + proto );

			//  Set the agent to use this transport protocol by default
			String enabled = XMLUtils.getAttribute( n, "enabled" );
			boolean isEnabled = enabled != null && ( enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("yes") );
			if( isEnabled )
			{
				String prev = agent.getProperties().getProperty( AgentProperties.PROP_MESSAGING_TRANSPORT );
				if( prev == null )
				{
					if( ( ADK.debug & ADK.DBG_PROPERTIES ) != 0 ) {
							Agent.getLog().debug( "Configuration file selecting " + proto.toUpperCase() + " as the default transport protocol" );
					}
	
					agent.getProperties().setTransportProtocol( proto );
				}
			}
			tp.setEnabled( isEnabled );

			//
			//  Enumerate all <property> elements of this <transport> ...
			//
			List<Node> props = XMLUtils.getElementsByTagName( n, "property", false );
		    for( int p = 0; p < props.size(); p++ )
			{
				Node prop = props.get(p);
				String nam = (String)XMLUtils.getAttribute( prop,"name" );
				String val = (String)XMLUtils.getAttribute( prop, "value" );

				if( tp.containsKey( nam ) )
				{
					if( overwrite ) {
						if( ( ADK.debug & ADK.DBG_PROPERTIES ) != 0 )
							Agent.getLog().debug( "Configuration file overwriting " + proto.toUpperCase() + " transport property: " + nam + " = " + val );
						tp.setProperty(nam,val);
					}
				}
				else
				{
					if( ( ADK.debug & ADK.DBG_PROPERTIES ) != 0 )
						Agent.getLog().debug( "Setting " + proto.toUpperCase() + " transport property from configuration file: " + nam + " = " + val );

				    tp.setProperty(nam,val);
				}
			}
		}
	}

	/**
	 *  Applies <code>&lt;zone&gt;</code> elements to create new Zone instances.
	 *  The caller is responsible for setting up topics, connecting to zones,
	 *  and joining zones to topics.
	 */
	public Zone[] applyZones( Agent agent )
		throws ADKException, ADKConfigException
	{
		ZoneFactory zf = agent.getZoneFactory();

		Node[] zones = getZoneNodes();

		for( int i = 0; i < zones.length; i++ )
		{
			//  Get any properties defined for this zone. Zone properties
			//  should inherit from the agent's AgentProperties object, so
			//  pass that object as the parent
			AgentProperties props = new AgentProperties( agent.getProperties() );
			getZoneProperties(props,zones[i]);

			String zoneId = XMLUtils.getAttribute(zones[i],"id");
			if( zoneId == null || zoneId.trim().length() == 0 )
				throw new ADKConfigException( "<zone> cannot have an empty id attribute");
			String zoneUrl = XMLUtils.getAttribute(zones[i],"url");
			if( zoneUrl == null || zoneUrl.trim().length() == 0 )
				throw new ADKConfigException( "<zone> cannot have an empty url attribute");

			//  Ask ZoneFactory to create a Zone instance
		    zf.getInstance( zoneId, zoneUrl, props );
		}

		return agent.getZoneFactory().getAllZones();
	}

	/**
	 *  Gets the DOM document<p>
	 *  @return The loaded configuration file
	 */
	public Document getDocument()
	{
		return fDoc;
	}

	/**
	 *  Adds a new zone
	 *  @param zoneId The zone ID
	 *  @param zoneUrl The zone URL
	 *  @param templateId The ID of the zone template
	 */
	public Node addZone( String zoneId, String zoneUrl, String templateId )
		throws ADKConfigException
	{
		if( zoneId == null || zoneId.trim().length() == 0 )
			throw new ADKConfigException( "Zone ID cannot be blank" );

		Node zone = getZoneNode( zoneId );
		if( zone != null )
			throw new ADKConfigException( "Zone already defined" );

		//  Create a new <zone> element
		zone = getDocument().createElement( "zone" );
		getRootNode().appendChild( zone );
		XMLUtils.setAttribute( zone, "id", zoneId );
		if( zoneUrl != null )
			XMLUtils.setAttribute( zone, "url", zoneUrl );
		XMLUtils.setAttribute( zone, "template", templateId == null ? "Default" : templateId );

		return zone;
	}

	/**
	 *  Deletes a zone
	 *  @param zoneId The zone ID
	 */
	public void deleteZone( String zoneId )
	{
		if( zoneId == null )
			return;

		Node zone = getZoneNode( zoneId );
		if( zone != null )
			zone.getParentNode().removeChild(zone);
	}

	/**
	 *  Gets the version of SIF that should be used by the agent
	 *  @return A SIFVersion object for the value of the <code>sifVersion</code>
	 *      attribute specified by the <code>&lt;agent&gt;</code> element, or
	 *      the ADK's default SIFVersion if this attribute was not set
	 */
	public SIFVersion getVersion()
	{
		Node agent = getRootNode();
		if( agent != null ) {
			String version = XMLUtils.getAttribute(agent,"sifVersion");
			if( version != null )
				return SIFVersion.parse(version);
		}

		return SIFVersion.LATEST;
	}

	/**
	 *  Gets the Mappings object
	 *  @return The <code>&lt;agent&gt;</code> node if defined
	 */
	public Mappings getMappings()
	{
		return fMappings;
	}

	/**
	 *  Gets the root <code>&lt;agent&gt;</code> element
	 *  @return The root node
	 */
	public Node getRootNode()
	{
		try
		{
		    return (Node)XMLUtils.getElementsByTagName( getDocument(), "agent", false ).get( 0 );
		}
		catch( Throwable thr )
		{
			return null;
		}
	}

	/**
	 *  Gets the SourceId that should be used by the agent
	 */
	public String getSourceId()
	{
		Node agent = getRootNode();
		return agent != null ? XMLUtils.getAttribute(agent,"id") : null;
	}

	/**
	 *  Populates an AgentProperties object with all <code>&lt;property&gt;</code>
	 *  values defined for the root <code>&lt;agent&gt;</code> element.
	 *
	 *  Properties defined at the root level are applied to the AgentProperties
	 *  object of the Agent class. These serve as global defaults to all zones.
	 *  Use the getTemplateProperties method to obtain properties for a zone
	 *  template, which apply to all zones that reference that template, or the
	 *  getZoneProperties method to obtain properties specific to a zone.
	 *  <p>
	 *
	 *  @return An AgentProperties object populated with all <code>&lt;property&gt;</code>
	 *      values defined for the root <code>&lt;agent&gt;</code> element
	 */
	public void getAgentProperties( AgentProperties props )
	{
		populateProperties( props, getRootNode() );
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> values defined for the root
	 *  <code>&lt;agent&gt;</code> node.
	 */
	public Node[] getAgentPropertyNodes()
	{
		return getPropertyNodes( getRootNode() );
	}

	/**
	 *  Sets the value of a <code>&lt;property&gt;</code> child of the root
	 *  <code>&lt;agent&gt;</code> node. If a <code>&lt;property&gt;</code>
	 *  element already exists, its value is updated; otherwise a new element is
	 *  appended to the root <code>&lt;agent&gt;</code> node.<p>
	 *
	 *  @param property The name of the property
	 *  @param value The property value
	 */
	public void setAgentProperty( String property, String value )
	{
		setProperty( getRootNode(), property, value );
	}

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined by a <code>&lt;zone&gt;</code> node as well as all
	 *  properties defined by the referenced zone template. Properties defined
	 *  by the <code>&lt;zone&gt;</code> override properties defined by the
	 *  template.
	 */
	public void getZoneProperties( Properties props, String zone )
	{
		getZoneProperties( props, getZoneNode( zone ) );
	}

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined by a <code>&lt;zone&gt;</code> node as well as all
	 *  properties defined by the referenced zone template. Properties defined
	 *  by the <code>&lt;zone&gt;</code> override properties defined by the
	 *  template.
	 */
	public void getZoneProperties( Properties props, Node zone )
	{
		populateProperties( props, zone );

		String template = XMLUtils.getAttribute( zone, "template" );
		if( template != null ) {
			getZoneTemplateProperties( props, template );
		}
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> values defined for a
	 *  <code>&lt;zone&gt;</code> node.
	 */
	public Node[] getZonePropertyNodes( String zoneId )
	{
		return getPropertyNodes( getZoneNode( zoneId ) );
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> values defined for a
	 *  <code>&lt;zone&gt;</code> node.
	 */
	public Node[] getZonePropertyNodes( Node zone )
	{
		return getPropertyNodes( zone );
	}

	/**
	 *  Sets the value of a <code>&lt;property&gt;</code> child of the specified
	 *  <code>&lt;zone&gt;</code> node. If a <code>&lt;property&gt;</code> child
	 *  already exists with the same name, its value is updated; otherwise a new
	 *  element is appended to the <code>&lt;zone&gt;</code> node.<p>
	 *
	 *  @param zoneId The zone ID
	 *  @param property The name of the property
	 *  @param value The property value
	 */
	public void setZoneProperty( String zoneId, String property, String value )
	{
		Node zone = getZoneNode( zoneId );
		if( zone != null )
			setProperty( zone, property, value );
	}

	/**
	 *  Gets the value of a <code>&lt;property&gt;</code> child of the specified
	 *  <code>&lt;zone&gt;</code> node.
	 *
	 *  @param zoneId The zone ID
	 *  @param property The name of the property
	 *  @param defaultValue The value that will be returned if the property is
	 *      not defined
	 */
	public String getZoneProperty( String zoneId, String property, String defaultValue )
	{
		Node n = getZoneNode( zoneId );
		if( n != null )
			return getProperty( n, property, defaultValue );

		return defaultValue;
	}

	/**
	 *  Sets the value of a <code>&lt;zone&gt;</code> node attribute.
	 *
	 *  @param zone The zone ID
	 *  @param attr The name of the attribute
	 *  @param value The attribute value
	 */
	public void setZoneAttribute( String zoneId, String attr, String value )
	{
		Node zone = getZoneNode( zoneId );
		if( zone != null )
			XMLUtils.setAttribute( zone, attr, value );
	}

	/**
	 *  Gets the value of a <code>&lt;zone&gt;</code> node attribute.
	 *
	 *  @param zone The zone ID
	 *  @param attr The name of the attribute
	 *  @param defaultValue The value that will be returned if the attribute is
	 *      not defined
	 */
	public String getZoneAttribute( String zoneId, String attr, String defaultValue )
	{
		String s = null;
		Node zone = getZoneNode( zoneId );
		if( zone != null )
			s = XMLUtils.getAttribute( zone, attr );

		return s == null ? defaultValue : s;
	}

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined for a <code>&lt;template&gt;</code> node.
	 */
	public void getZoneTemplateProperties( Properties props, String template )
	{
		populateProperties( props, getZoneTemplateNode( template ) );
	}

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined for a <code>&lt;template&gt;</code> node.
	 */
	public void getZoneTemplateProperties( Properties props, Node template )
	{
		populateProperties( props, template );
	}

	/**
	 *  Gets a zone template property.<p>
	 *  @param templateId The ID of the <code>&lt;template&gt;</code>
	 *  @param property The name of the <code>&lt;property&gt;</code>
	 *  @param defValue The default value to return if the property is not defined
	 */
	public String getZoneTemplateProperty( String templateId, String property, String defValue )
	{
		Node n = getZoneTemplateNode( templateId );
		if( n != null )
			return getProperty( n, property, defValue );

		return defValue;
	}

	/**
	 *  Gets a zone template property node.<p>
	 *  @param templateId The ID of the <code>&lt;template&gt;</code>
	 *  @param property The name of the <code>&lt;property&gt;</code>
	 *  @return the DOM Node if found
	 */
	public Node getZoneTemplatePropertyNode( String templateId, String property )
	{
		Node n = getZoneTemplateNode( templateId );
		if( n != null )
			return getPropertyNode( n, property );

		return null;
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> values defined for a <code>&lt;zone&gt;</code> node.
	 */
	public Node[] getZoneTemplatePropertyNodes( String templateId )
	{
		Node[] templates = getZoneTemplates( false );

		for( int i = 0; i < templates.length; i++ ) {
			String id = XMLUtils.getAttribute( templates[i], "id" );
			if( id != null && id.equals( templateId ) ) {
				return getPropertyNodes( templates[i] );
			}
		}

		return null;
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> values defined for a <code>&lt;zone&gt;</code> node.
	 */
	public Node[] getZoneTemplatePropertyNodes( Node template )
	{
		return getPropertyNodes( template );
	}

	/**
	 *  Sets the value of a <code>&lt;property&gt;</code> child of the specified
	 *  <code>&lt;zone&gt;</code> node. If a <code>&lt;property&gt;</code> child
	 *  already exists with the same name, its value is updated; otherwise a new
	 *  element is appended to the <code>&lt;template&gt;</code> node.<p>
	 *
	 *  @param template The zone template ID
	 *  @param property The name of the property
	 *  @param value The property value
	 */
	public void setZoneTemplateProperty( String template, String property, String value )
	{
		Node temp = getZoneTemplateNode( template );
		if( temp == null )
			temp = addZoneTemplateNode( template );

		setProperty( temp, property, value );
	}
	
	/**
	 *  Deletes a property.<p>
	 *  @param property The name of the <code>&lt;property&gt;</code>
	 */
	public void deleteProperty( String property )
	{
		Node prn = getPropertyNode( getRootNode(), property );
		if( prn != null )
			prn.getParentNode().removeChild( prn );
	}
	
	/**
	 *  Deletes a zone property.<p>
	 *  @param zoneId The ID of the <code>&lt;zone&gt;</code>
	 *  @param property The name of the <code>&lt;property&gt;</code>
	 */
	public void deleteZoneProperty( String zoneId, String property )
	{
		Node n = getZoneNode( zoneId );
		if( n != null ) {
			Node prn = getPropertyNode( n, property );
			if( prn != null )
				prn.getParentNode().removeChild( prn );
		}
	}

	/**
	 *  Deletes a zone template property.<p>
	 *  @param templateId The ID of the <code>&lt;template&gt;</code>
	 *  @param property The name of the <code>&lt;property&gt;</code>
	 */
	public void deleteZoneTemplateProperty( String templateId, String property )
	{
		Node n = getZoneTemplateNode( templateId );
		if( n != null ) {
			Node prn = getPropertyNode( n, property );
			if( prn != null )
				prn.getParentNode().removeChild( prn );
		}
	}
	

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined as children of the specified node. Properties that already
	 *  exist in the Properties object are not overwritten by properties defined
	 *  by the node.
	 */
	public void populateProperties( Properties props, Node node )
	{
		populateProperties( props, node, false );
	}

	/**
	 *  Populates a Properties object with all <code>&lt;property&gt;</code>
	 *  values defined as children of the specified node. Properties that already
	 *  exist in the Properties object are optionally overwritten with properties
	 *  defined by the node.
	 *
	 *  @param props The Properties object to populate
	 *  @param node The Node to search for child <code>&lt;property&gt;</code> elements
	 *  @param replace true to replace the values of properties already defined
	 *      in the <code>props</code> object
	 */
	public void populateProperties( Properties props, Node node, boolean replace )
	{
		if( node != null )
		{
			List<Node> v = XMLUtils.getElementsByTagName( node, "property", true );

			for( int i = 0; i < v.size(); i++ )
			{
				Node n = v.get(i);
				String nam = XMLUtils.getAttribute(n,"name");
				String val = XMLUtils.getAttribute(n,"value");
				if( nam != null && val != null && ( replace || !props.containsKey( nam ) ) )
					props.put(nam,val);
			}
		}
	}

	/**
	 *  Sets the value of a <code>&lt;property&gt;</code> child of the specified
	 *  node. If a <code>&lt;property&gt;</code> element already exists, its value
	 *  is updated; otherwise a new element is appended to the node.<p>
	 *
	 *  @param node The parent node of the property
	 *  @param property The name of the property
	 *  @param value The property value
	 */
	public void setProperty( Node node, String property, String value )
	{
		Node propN = null;
		List<Node> v = XMLUtils.getElementsByTagName( node, "property", false );
		for( int i = 0; i < v.size(); i++ ) {
			String name = XMLUtils.getAttribute( v.get(i),"name" );
			if( name.equals(property) ) {
				propN = v.get(i);
				break;
			}
		}

		if( propN == null ) {
			propN = getDocument().createElement("property");
			node.appendChild( propN );
		}

		XMLUtils.setAttribute( propN, "name", property );
		XMLUtils.setAttribute( propN, "value", value );
	}

	public void setProperty( Node node, String element, String property, String value )
	{
		Node _ele = null;
		Node _prop = null;

		//  Find/create container node...
		List<Node> v = XMLUtils.getElementsByTagName( node, element, false );
		if( v.size() == 0 ) {
			_ele = node.getOwnerDocument().createElement( element );
			node.appendChild( _ele );
		}

		//	Find existing <property> node if any
		for( int i = 0; i < v.size() && _prop == null; i++ )
		{
			_ele = v.get(i);
			List<Node> v2 = XMLUtils.getElementsByTagName( _ele, "property", false );

			for( int p = 0; p < v2.size() && _prop == null; p++ )
			{
				Node n = v2.get(p);
				String nam = XMLUtils.getAttribute(n,"name");
				if( nam != null && nam.equals(property) )
					_prop = n;
			}
		}

		//	Create <property> node if none found above
		if( _prop == null ) {
			_prop = _ele.getOwnerDocument().createElement( "property" );
			XMLUtils.setAttribute( _prop, "name", property );
			_ele.appendChild( _prop );
		}

		if( _prop != null )
			XMLUtils.setAttribute( _prop, "value", value );
	}

	/**
	 *  Gets the value of a <code>&lt;property&gt;</code> element defined by a node.
	 *
	 *  @param node The parent node to search
	 *  @param property The name of the property
	 *  @param defaultValue The value to return if the property is not found
	 *
	 *  @return The property value
	 */
	public String getProperty( Node node, String property, String defaultValue )
	{
		String val = null;

		List<Node> v = XMLUtils.getElementsByTagName( node, "property", true );
		for( int i = 0; i < v.size(); i++ ) {
			Node ch = v.get(i);
			if( XMLUtils.getAttribute( ch, "name" ).equals( property ) ) {
				val = XMLUtils.getAttribute( ch, "value" );
				break;
			}
		}

		return val == null ? defaultValue : val;
	}

	/**
	 *  Gets a <code>&lt;property&gt;</code> DOM Node
	 *
	 *  @param node The parent node to search
	 *  @param property The name of the property
	 *
	 *  @return The DOM Node if the property is found, otherwise <code>null</code>
	 */
	public Node getPropertyNode( Node node, String property  )
	{
		Node n = null;

		List<Node> v = XMLUtils.getElementsByTagName( node, "property", true );
		for( int i = 0; i < v.size(); i++ ) {
			Node ch = (Node)v.get(i);
			if( XMLUtils.getAttribute( ch, "name" ).equals( property ) ) {
				n = ch;
				break;
			}
		}

		return n;
	}

	/**
	 *  A convenience function to get the value of a <code>&lt;property&gt;</code>
	 *  defined by the first child element of the specified node that has a
	 *  matching tag name. This routine is often used to obtain properties that
	 *  are organized into named groups (e.g. <code>&lt;database-settings&gt;</code>,
	 *  <code>&lt;transport-settings&gt;</code>, etc.)<p>
	 *
	 *  For example, to lookup a property named "user" of a top-level
	 *  <code>&lt;database-settings&gt;</code> element,<p>
	 *
	 *  <code>getProperty( getRootNode(), "database-settings", "user", "sa" );</code>
	 *  <p>
	 *
	 *  @param node The parent node to search
	 *  @param element The tag name of the element to search for (it must be a
	 *      non-repeating child of the parent node)
	 *  @param property The name of the property
	 *  @param defaultValue The value to return if the property is not found
	 *
	 *  @return The property value
	 */
	public String getProperty( Node node, String element, String property, String defaultValue )
	{
		List<Node> v = XMLUtils.getElementsByTagName( node, element, true );
		if( v.size() == 0 )
			return defaultValue;

		String val = null;

		for( int i = 0; i < v.size() && val == null; i++ )
		{
			Node ch = v.get(i);

			List<Node> v2 = XMLUtils.getElementsByTagName( ch, "property", true );

			for( int p = 0; p < v2.size() && val == null; p++ )
			{
				Node n = v2.get(p);
				String nam = XMLUtils.getAttribute(n,"name");
				if( nam != null && nam.equals(property) )
					val = XMLUtils.getAttribute(n,"value");
			}
		}

		return val == null ? defaultValue : val;
	}

	/**
	 *  Gets all <code>&lt;property&gt;</code> child nodes of an element
	 */
	public Node[] getPropertyNodes( Node node )
	{
		List<Node> v = XMLUtils.getElementsByTagName( node, "property", false );
		Node[] arr = new Node[v.size()];
		v.toArray(arr);
		return arr;
	}

	/**
	 *  Gets an array of all zones defined by the configuration file<p>
	 *  @return An array of DOM Nodes representing the <code>&lt;zone&gt;</code> elements
	 *      defined by the configuration file
	 */
	public Node[] getZoneNodes()
	{
		List<Node> v = XMLUtils.getElementsByTagName( getRootNode(), "zone", false );
		Node[] n = new Node[ v == null ? 0 : v.size() ];
		v.toArray( n );
		return n;
	}

	/**
	 *  Gets the <code>&lt;zone&gt;</code> element with the specified ID
	 */
	public Node getZoneNode( String zoneId )
	{
		return XMLUtils.getElementByAttr( getRootNode(), "zone", "id", zoneId );
	}

	/**
	 *  Gets an array of all zones templates.<p>
	 *  @return An array of DOM Nodes comprised of <code>&lt;template&gt;</code>
	 *      elements defined as children of the root <code>&lt;agent&gt;</code> node
	 */
	public Node[] getZoneTemplates( boolean filtered )
	{
		List<Node> v = XMLUtils.getElementsByTagName( getRootNode(), "template", filtered );

		Node[] n = new Node[ v == null ? 0 : v.size() ];
		v.toArray( n );
		return n;
	}

	/**
	 *  Gets a zone template by ID.<p>
	 *  @param templateId The ID of the template to return
	 *  @return The DOM Node encapsulating the <code>&lt;template&gt;</code>
	 *      element with the specified ID
	 */
	public Node getZoneTemplateNode( String templateId )
	{
		return XMLUtils.getElementByAttr( getRootNode(), "template", "id", templateId );
	}

	/**
	 *  Add a &lt;template&gt; node for the specified zone template.
	 *  @return The newly created node, or if a zone template node already
	 *      exists with this templateId, a reference to it is returned
	 */
	public Node addZoneTemplateNode( String templateId )
	{
		Node n = getZoneTemplateNode( templateId );

		if( n == null )
		{
			//  Create and append a new template node
			Node root = getRootNode();
	    	n = root.getOwnerDocument().createElement( "template" );
			XMLUtils.setAttribute( n, "id", templateId );
	    	root.appendChild( n );
		}
		return n;
	}

	/**
	 *  Gets a &lt;transport&gt; node.<p>
	 *  @param protocol The protocol type (e.g. "http", "https", etc.)
	 *  @return The DOM Node encapsulating the <code>&lt;transport&gt;</code> element
	 */
	public Node getTransportNode( String protocol )
	{
		return XMLUtils.getElementByAttr( getRootNode(), "transport", "protocol", protocol );
	}

	/**
	 *  Add a &lt;transport&gt; node for the specified transport protocol.
	 *  @return The newly created node, or if a transport node already exists
	 *      for this protocol, a reference to it is returned
	 */
	public Node addTransportNode( String protocol )
	{
		Node n = getTransportNode( protocol );

		if( n == null )
		{
			//  Create and append a new protocol node
			Node root = getRootNode();
			n = root.getOwnerDocument().createElement( "transport" );
			XMLUtils.setAttribute( n, "protocol", protocol );
			root.appendChild( n );
		}

		return n;
	}
}
