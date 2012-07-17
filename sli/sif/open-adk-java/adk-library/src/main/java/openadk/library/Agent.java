//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


import openadk.library.impl.*;
import openadk.library.impl.ObjectFactory.ADKFactoryType;
import openadk.library.log.ServerLog;
import openadk.library.reporting.ReportingDTD;
import openadk.library.threadpool.ThreadPoolManager;
import openadk.util.GUIDGenerator;

import org.apache.log4j.*;
import java.util.*;
import java.io.*;

/**
 *  The base class for a SIF Agent.<p>
 *
 *  Derive your own class from this one and pass the <i>SourceId</i> to the
 *  superclass constructor. Call the <code>initialize</code> method to initialize
 *  the agent prior to use.<p>
 *
 */
public class Agent
{
    /// <summary>
    /// The Identifier that is used to identify the ADK itself for logging operations ("ADK")
    /// </summary>
    /**
     * 
     */
    public static final String LOG_IDENTIFIER = ADK.LOG_IDENTIFIER + ".Agent";
    
	/**
	 *  The root log Category. Subcategories exist for each zone, where the
	 *  subcategory name is "ADK.Agent$<i>zoneId</i>". The ADK uses the root
	 *  Category when writing log events that are not associated with a
	 *  specific zone. Your agent may also use this Category to post log
	 *  events.
	 */
	protected static Logger log = LogManager.getLogger( LOG_IDENTIFIER );
	
	/**
	 *  The root ServerLog instance. Subcategories exist for each zone, where 
	 * 	the subcategory name is "ADK.Agent$<i>zoneId</i>". The Agent uses the
	 * 	root ServerLog instance only to establish the agent-global chain of 
	 * 	loggers; no actual logging is performed outside the context of a zone.
	 */
	protected static ServerLog serverLog = ServerLog.getInstance( LOG_IDENTIFIER, null );

	/**
	 *  The agent's SourceId
	 */
	protected String fSourceId;

	/**
	 *  The display name used in SIF_Register/SIF_Name and in HTTP "UserAgent" headers
	 */
	protected String fName;
	
	
	/**
	 *  The ZoneFactory for this agent. Each agent has its own ZoneFactory in
	 *  case the application vendor creates multiple Agent instances in the same
	 *  virtual machine, a rare case but one that it supported
	 */
	private ZoneFactory fZoneFactory;

	/**
	 *  The TopicFactory for this agent. Each agent has its own TopicFactory in
	 *  case the application vendor creates multiple Agent instances in the same
	 *  virtual machine, a rare case but one that it supported
	 */
	private TopicFactory fTopicFactory;

	/**
	 *  The UndeliverableMessageHandler for the agent
	 */
	private UndeliverableMessageHandler fErrHandler;

	/**
	 *  Agent and default zone properties
	 */
	private AgentProperties fProps;

	/**
	 *  Initialization state
	 */
	private boolean fInit = false;

	/**
	 *  Is the agent in the process of shutting down?
	 */
	private boolean fShutdownInProgress = false;

	/**
	 *  Has the agent been shutdown?
	 */
	private boolean fShutdown = false;
	
	/**
	 *  The Object that configured this instance of the agent, e.g. an instance of AgentConfig
	 */
	private Object fConfigurationSource;

	/**
	 *  The Subscribers registered with this agent. The map is keyed by SIF
	 *  data object names (e.g. "StudentPersonal"). If a Subscriber is registered
	 *  for all object types, it is keyed by the ElementDef SIFDTD.SIF_MESSAGE.
	 */
	protected HashMap<ElementDef,Subscriber> fSubs = new HashMap<ElementDef,Subscriber>();

	/**
	 *  The Publishers registered with this agent. The map is keyed by SIF
	 *  data object names (e.g. "StudentPersonal"). If a Publisher is registered
	 *  for all object types, it is keyed by the ElementDef SIFDTD.SIF_MESSAGE.
	 */
	protected HashMap<ElementDef,Object> fPubs = new HashMap<ElementDef,Object>();

	/**
	 *  The QueryResults objects registered with this agent. The map is keyed by
	 *  SIF data object names (e.g. "StudentPersonal"). If a QueryResults object
	 *  is registered for all object types, it is keyed by the ElementDef SIFDTD.SIF_MESSAGE.
	 */
	protected HashMap<ElementDef,QueryResults> fQueryResults = new HashMap<ElementDef, QueryResults>();

	/**
	 * 	Manages the MessagingListeners
	 */
	protected List<MessagingListener> fMessagingListeners = new ArrayList<MessagingListener>(0);

	/**
	 * The TransportManager instances that manages all open transports for this agent 
	 */
	private final TransportManagerImpl fTransportManager;

	/**
	 * The thread pool shared across transports for pull mode
	 */
	private ThreadPoolManager fThreadPoolManager = null;
	
	/**
	 *  Constructor<p>
	 *
	 *  @param agentId The string name that uniquely identifies this agent in SIF Zones.
	 *      This string is used as the <code>SourceId</code> in all SIF message
	 *      headers created by the agent.
	 */
	public Agent( String agentId )
	{
		if( agentId == null || agentId.trim().length() == 0 )
			ADKUtils._throw( new IllegalArgumentException("Agent ID cannot be null or a blank string"), log );

		fSourceId = agentId;
		try
		{
			ObjectFactory factory = ObjectFactory.getInstance();
			fZoneFactory = (ZoneFactory) factory.createInstance( ADKFactoryType.ZONE, this );
			fTopicFactory = (TopicFactory) factory.createInstance( ADKFactoryType.TOPIC, this );
		}
		catch( ADKException objectFactoryEx ){
			throw new RuntimeException( "Unable to create an instance of Agent: " + objectFactoryEx.getMessage(), objectFactoryEx );
		}
		
		fTransportManager = new TransportManagerImpl();

		//  Change the default LogSink that Jetty uses so that it is re-routed to
		//  the Log4j Category for the agent
		System.setProperty( "LOG_SINKS", "openadk.library.impl.Log4jLogSink" );

	}

	/**
	 *  Gets the ZoneFactory for this agent. The ZoneFactory is used to create
	 *  Zone instances to represent logical SIF zones. An application can also
	 *  call ZoneFactory methods to enumerate the Zones that have been created.
	 *  <p>
	 *
	 *  @return The agent's ZoneFactory
	 */
	public ZoneFactory getZoneFactory() {
		_checkInit();
		return fZoneFactory;
	}

	/**
	 *  Gets the TopicFactory for this agent. The TopicFactory is used to create
	 *  Topic instances to aggregate publish and subscribe activity for a given
	 *  type of SIF data object across one or more zones. An application can
	 *  also call TopicFactory methods to enumerate the Topics that have been
	 *  created.<p>
	 *
	 *  @return The agent's TopicFactory
	 */
	public TopicFactory getTopicFactory() {
		_checkInit();
		return fTopicFactory;
	}

	/**
	 *  Gets the properties for this agent.<p>
	 *
	 *  The agent properties serve as defaults for new Zone objects created by
	 *  the ZoneFactory. Properties may be customized on a zone-by-zone basis.
	 *  If a property is not specified for a given zone, its value is inherited
	 *  from the AgentProperties object returned by this method. Note this
	 *  method returns the same object as getDefaultZoneProperties.<p>
	 *
	 *  Agent properties should be set prior to calling the Agent.initialize
	 *  method.<p>
	 *
	 *  @return The agent properties
	 */
	public AgentProperties getProperties()
	{
		if( fProps == null )
		{
			fProps = new AgentProperties( this );
		}
		return fProps;
	}

	/**
	 *  Gets the default properties for a transport protocol.<p>
	 *
	 *  Each transport protocol supported by the ADK is represented by a class
	 *  that implements the Transport interface. Transports are identified by
	 *  a string such as "http" or "https". Like Zones, each Transport instance
	 *  is associated with a set of properties specific to the transport
	 *  protocol. Such properties may include IP address, port, SSL security
	 *  attributes, and so on. The default properties for a given transport
	 *  protocol may be obtained by calling this method.<p>
	 * @param protocol The protocol to get default properties for e.g. "http"
	 *
	 *  @return The default properties for the specified protocol
	 *
	 *  @exception ADKTransportException is thrown if the protocol is not supported
	 *      by the ADK
	 */
	public TransportProperties getDefaultTransportProperties( String protocol )
		throws ADKTransportException
	{
		return fTransportManager.getDefaultTransportProperties( protocol );
	}

	/**
	 *  Convenience method to get the default HTTP transport properties
	 * @return the default HTTP transport properties
	 */
	public HttpProperties getDefaultHttpProperties() {
		try {
			return (HttpProperties)getDefaultTransportProperties("http");
		} catch( ADKTransportException impossible ) {
			throw new RuntimeException( impossible.getMessage(), impossible );
		}
	}

	/**
	 *  Convenience method to get the default HTTPS transport properties
	 * @return the default HTTPS transport properties
	 */
	public HttpsProperties getDefaultHttpsProperties() {
		try {
			return (HttpsProperties)getDefaultTransportProperties("https");
		} catch( ADKTransportException impossible ) {
			throw new RuntimeException( impossible.getMessage(), impossible );
		}
	}

	/**
	 *  Gets the default properties for Zones created by the ZoneFactory.
	 *  @return The default zone properties
	 */
	public AgentProperties getDefaultZoneProperties()
	{
		return getProperties();
	}

	/**
	 *  Gets the agent's <i>home directory</i>. By default, the home directory
	 *  is the directory the agent was started from (if defined, the "adk.home"
	 *  System property overrides this value.) You may override this method to
	 *  return a home directory specific to your product's installation
	 *  directory structure.<p>
	 *
	 *  The Agent Runtime creates all work directories and files relative to
	 *  the home directory. In some cases the Class Framework creates workspaces
	 *  even when the Agent Runtime is not enabled.<p>
	 *
	 *  @return The agent's home directory
	 */
	public String getHomeDir()
	{
		String dir = System.getProperty("adk.home");
		if( dir != null )
			return dir;

		return System.getProperty("user.dir");
	}

	/**
	 *  Gets the agent's SourceId
	 *  @return The string name that uniquely identifies this agent in a SIF Zone.
	 */
	public String getId()
	{
		return fSourceId;
	}

	/**
	 *  Sets the agent's SourceId
	 *  @param sourceId A string name that uniquely identifies this agent in
	 *      a SIF Zone. SIF does not specify any restrictions on the length or
	 *      characters that may appear in the SourceId.
	 */
	public void setId( String sourceId )
	{
		fSourceId = sourceId;

		if( BuildOptions.PROFILED ) 
		{
			Agent.log.debug( "SIF Profiling Harness support enabled in ADK" );

			//	Establish the SIFProfilerClient name (i.e. "sourceId_ADK")
			ProfilerUtils.setProfilerName( getId() + "_ADK" );
		}
	}

	/**
	 *  Gets the descriptive name of the SIF Agent.<p>
	 *
	 *  This string is used to identify the agent whenever a descriptive name is
	 *  preferred over the agent ID. If the @link #getAgentInfo() method is not overrident,
	 *  The class framework uses this string for the value of the &lt;SIF_Name&gt; element 
	 *  during agent registration.<p>
	 *
	 *  By default, the agent ID is returned.<p>
	 *
	 *  @return A descriptive name for the agent
	 *
	 *  @see #getId
	 *  @see #setName(String)
	 */
	public String getName()
	{
		if( fName == null )
			return getId();

		return fName;
	}

	/**
	 *  Sets the descriptive name of the SIF Agent.<p>
	 *
	 *  This string is used to identify the agent whenever a descriptive name is
	 *  preferred over the agent ID. The class framework uses this string for the
	 *  value of the &lt;SIF_Name&gt; element during agent registration.<p>
	 *
	 *  By default, the agent ID is used as the descriptive name. This method
	 *  must be called at agent initialization time prior to connecting to
	 *  zones.<p>
	 *
	 *  @param name A descriptive name for the agent
	 *
	 *  @see #getName
	 */
	public void setName( String name )
	{
		fName = name;
	}
	

	/**
	 *  Initialize the agent.<p>
	 *
	 *  An application must call this method to initialize the class framework
	 *  and runtime. No other methods can be called until the agent has been
	 *  successfully initialized. When the agent exits it is important that the
	 *  <code>shutdown</code> method be called to safely release the resources
	 *  allocated by the runtime.<p>
	 *
	 *  If an application overrides this method, it should call the superclass
	 *  implementation <i>after</i> performing its own initialization.
	 * @throws Exception If the agent is unable to initialize due to a file
	 * or resource exception
	 * @exception ADKException is thrown if the agent has already
	 *      been initialized 
	 *
	 *  @see #shutdown()
	 */
	public synchronized void initialize()
		throws Exception
	{
		if( fInit )
			ADKUtils._throw( new ADKException("Agent is already initialized",null),log );
		if( fShutdownInProgress )
			ADKUtils._throw( new ADKException("Agent is in the process of shutting down",null),log );

		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
			log.info("Initializing agent...");

		//  Verify home directory exists and can be written to
		File f = new File( getHomeDir() );
		if( !f.exists() ) {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
	    		log.debug("Creating home directory: " + f.getAbsolutePath() );
			f.mkdirs();
		}
		if( !f.isDirectory() ) {
			ADKUtils._throw( new ADKException( "The home directory is not a directory: " + f.getAbsolutePath(), null ), log );
		}

		//  Verify work directory exists and can be written to
		f = new File( getHomeDir() + File.separator + "work" );
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
			log.info( "Setting work directory to: " + f.getAbsolutePath() );
		if( !f.exists() ) {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
	    		log.debug("Creating work directory: " + f.getAbsolutePath() );
			f.mkdirs();
		}
		if( !f.isDirectory() ) {
			ADKUtils._throw( new ADKException( "The work directory is not a directory: " + f.getAbsolutePath(), null ), log );
		}

		if( BuildOptions.PROFILED ) 
		{
			System.out.println( "SIF Profiling Harness support enabled in ADK" );

			//	Establish the SIFProfilerClient name (i.e. "sourceId_ADK")
			ProfilerUtils.setProfilerName( getId() + "_ADK" );
		}
				
		getProperties();
		fThreadPoolManager = new ThreadPoolManager(fProps.getThreadingCorePoolSize(), fProps.getThreadingMaximumPoolSize(), fProps.getThreadingKeepAliveTime());
		
		//Initialize the TransportManager
		fTransportManager.activate( this );
		
		fShutdownInProgress = false;
		fInit = true;
		if( log.isInfoEnabled() && ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
			log.info("Agent initialized");
		}
	}
	
	public ThreadPoolManager getThreadPoolManager() {
		return fThreadPoolManager;
	}
	

	/**
	 *  Shutdown the agent.<p>
	 *
	 *  This method should always be called before the application ends. It
	 *  closes resources held by the class framework and runtime.<p>
	 *
	 *  Calling this form of shutdown does not send any provisioning messages.
	 * @throws ADKException 
	 */
	public synchronized void shutdown()
		throws ADKException
	{
		shutdown(0);
	}

	/**
	 *  Shutdown the agent.<p>
	 *
	 *  This method should always be called before the application ends. It
	 *  closes resources held by the ADK Class Framework and ADK Agent Runtime.
	 *  <p>
	 *
	 *  Provisioning messages are sent as follows:<p>
	 *
	 *  <ul>
	 *      <li>
	 *          If the agent is using ADK-managed provisioning, a <code>&lt;
	 *          SIF_Unregister&gt;</code> message is sent to each zone to which
	 *          the agent is connected if the ADKFlags.PROV_UNREGISTER
	 *          flag is specified. <code>&lt;SIF_Unsubscribe&gt;</code> and
	 *          <code>&lt;SIF_Unprovide&gt;</code> messages are sent to each
	 *          zone joined to a Topic when the ADKFlags.PROV_UNSUBSCRIBE
	 *          and ADKFlags.PROV_UNPROVIDE flags are specified, respectively.
	 *          When ADK-managed provisioning is disabled, no provisioning
	 *          messages are sent to zones.
	 *      </li>
	 *      <li>
	 *          If Agent-managed provisioning is enabled, the ProvisioningOptions
	 *          flags have no affect. The agent must explicitly call the
	 *          Zone.sifUnregister, Zone.sifUnsubscribe, and Zone.sifUnprovide
	 *          methods to manually send those messages to each zone.
	 *      </li>
	 *      <li>
	 *          If ZIS-managed provisioning is enabled, no provisioning messages
	 *          are sent by the agent regardless of the ProvisioningOptions
	 *          used and the methods are called.
	 *      </li>
	 *  </ul>
	 *  <p>
	 * @param provisioningOptions The options from the ADKFlags constants
	 * @throws ADKException 
	 *
	 *  @see #initialize
	 */
	public synchronized void shutdown( int provisioningOptions )
		throws ADKException
	{
		if( !fInit )
			return;

		fShutdownInProgress = true;
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
			log.info( "Shutting down agent..." );

		//	Close the SIFProfilerClient if supported
//		if( BuildOptions.PROFILED ) {
//			openadk.profiler.SIFProfilerClient prof = 
//				openadk.profiler.SIFProfilerClient.getInstance( ProfilerUtils.getProfilerName() );
//			if( prof != null ) {
//				try {
//					prof.close();
//				} catch( Exception ex ) {
//					log.error( ex );
//				}
//			}
//		}
		
		try
		{
			//  TODO: Unsubscribe, Unprovide topics

			//  Disconnect and shutdown each zone...
			ZoneFactory zf = getZoneFactory();
			Zone[] zones = zf.getAllZones();
			for( int i = 0; i < zones.length; i++ ) {
				zones[i].disconnect(provisioningOptions);
				((ZoneImpl)zones[i]).shutdown();
			}

			if( fTransportManager != null )
			{
				//  Shutdown transports
				if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
					log.info("Shutting down Transports...");
				}
				fTransportManager.shutdown();
			}
			
			

			//  Close RequestCache
			try {
				RequestCache rc = RequestCache.getInstance(this);
				if( rc != null )
					rc.close();
			} catch( Exception ex ) {
			}

			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
				log.debug("Agent shutdown complete");
		}
		finally
		{
			fInit = false;
	    	fShutdown = true;
		}
	}

	/**
	 *  Determines if the agent has been initialized
	 *  @return true if the <code>initialize</code> method was called successfully;
	 *      false if that method has not been called successfully or the agent
	 *      has since been shut down.
	 */
	public boolean isInitialized()
	{
		return fInit;
	}

	/**
	 *  Determines if the agent has been shutdown.<p>
	 *
	 *  @return true if the <code>shutdown</code> method was called and the
	 *      agent is either in the process of shutting down or has finished
	 *      shutting down. Note the agent is considered to be shutdown even
	 *      if the <code>shutdown</code> method fails.
	 */
	public boolean isShutdown()
	{
		return fShutdownInProgress || !fInit;
	}

	/**
	 *  Puts all connected zones into sleep mode.<p>
	 *
	 *  For each zone in the connected state, a SIF_Sleep message is sent to the
	 *  Zone Integration Server to request that this agent's queue be put into
	 *  sleep mode for that zone. If successful, the ZIS should not deliver
	 *  further messages to this agent until it is receives a SIF_Register or
	 *  SIF_Wakeup message from the agent. Note the ADK keeps an internal sleep
	 *  flag for each zone, which is initialized when the <code>connect</code>
	 *  method is called by sending a SIF_Ping to the ZIS. This flag is set so
	 *  that the ADK will return a Status Code 8 ("Receiver is sleeping") in
	 *  response to any message received by the ZIS for the duration of the
	 *  session.
	 *  <p>
	 *
	 *  @exception ADKException thrown if the SIF_Sleep message is unsuccessful.
	 *      The ADK will attempt to send a SIF_Sleep to all connected zones; the
	 *      exception describes the zone or zones that failed
	 */
	public synchronized void sleep()
		throws ADKException
	{
		_checkInit();

		ADKMessagingException err = null;

		Zone[] zones = fZoneFactory.getAllZones();
		for( int i = 0; i < zones.length; i++ )
		{
			try
			{
				zones[i].sleep();
			}
			catch( ADKMessagingException ex )
			{
				if( err == null )
					err = new ADKMessagingException("An error occurred sending SIF_Sleep to zone \""+zones[i].getZoneId()+"\"",zones[i]);
				err.add(ex);
			}
		}

		if( err != null )
			ADKUtils._throw( err,log );
	}

	/**
	 *  Wakes up all connected zones if currently in sleep mode.<p>
	 *
	 *  For each connected zone, a SIF_Wakeup message is sent to the Zone
	 *  Integration Server to request that sleep mode be removed from this agent's
	 *  queue for that zone. Note the ADK keeps an internal sleep flag for each
	 *  zone, which is initialized when the <code>connect</code> method is called
	 *  by sending a SIF_Ping to the ZIS. This flag is cleared so that the ADK
	 *  will no longer return a Status Code 8 ("Receiver is sleeping") in response
	 *  to messages received by the ZIS.
	 *  <p>
	 *
	 *  @exception ADKException thrown if the SIF_Wakeup message is unsuccessful.
	 *      The ADK will attempt to send a SIF_Wakeup to all connected zones; the
	 *      exception describes the zone or zones that failed
	 */
	public synchronized void wakeup()
		throws ADKException
	{
		_checkInit();

		ADKMessagingException err = null;

		Zone[] zones = fZoneFactory.getAllZones();
		for( int i = 0; i < zones.length; i++ )
		{
			try
			{
				zones[i].wakeup();
			}
			catch( ADKMessagingException ex )
			{
				if( err == null )
					err = new ADKMessagingException("An error occurred sending SIF_Wakeup to zone \""+zones[i].getZoneId()+"\"",zones[i]);
				err.add(ex);
			}
		}

		if( err != null )
			ADKUtils._throw(err,log);
	}

	/**
	 *  Register a global Publisher message handler with this agent for all SIF object types.<p>
	 *
	 *  Note agents typically register message handlers with Topics or with Zones
	 *  instead of with the Agent. The message dispatcher first
	 *  delivers messages to Topics, then to Zones, and finally to the Agent
	 *  itself.<p>
	 *
	 *  In order to receive SIF_Request messages, the agent is expected to be
	 *  registered as a Provider of one or more object types in at least one
	 *  zone. This method does not send SIF_Provide messages to any zones.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent.
	 *      This object will be called whenever a SIF_Request is received by
	 *      and no other object in the message dispatching chain has processed
	 *      the message.
	 */
	public void setPublisher( Publisher publisher )
	{
		setPublisher(publisher,null);
	}

	/**
	 *  Register a global Publisher message handler with the agent for the specified SIF object type.<p>
	 *
	 *  Note agents typically register message handlers with Topics or with Zones
	 *  instead of with the Agent. The message dispatcher first delivers messages 
	 * 	to Topics, then to Zones, and finally to the Agent itself.<p>
	 *
	 *  In order to receive SIF_Request messages, the agent is expected to be
	 *  registered as a Provider of one or more object types in at least one
	 *  zone. This method does not send SIF_Provide messages to any zones.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Publisher will be called whenever a
	 *      SIF_Request is received and no other object in the message dispatching
	 *      chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies the
	 *      type of SIF Data Object this Publisher will respond to.
	 */
	public void setPublisher( Publisher publisher, ElementDef objectType )
	{
		if( publisher == null )
			ADKUtils._throw( new IllegalArgumentException("Publisher object cannot be null"),Agent.getLog() );

		if( objectType == null )
			// Set the wildcard handler for all requests
			fPubs.put( SIFDTD.SIF_MESSAGE, publisher );
		else
		{
			if( objectType == ReportingDTD.SIF_REPORTOBJECT ){
				throw new IllegalArgumentException("You must call setReportPublisher for SIF_ReportObject objects");
			}
			fPubs.put( objectType, publisher );
		}
	}

	/**
	 *  Register a global ReportPublisher message handler with the agent.<p>
	 *
	 *  Note agents typically register message handlers with Topics or with Zones
	 *  instead of with the Agent. The message dispatcher first delivers messages 
	 * 	to Topics, then to Zones, and finally to the Agent itself.
	 *
	 *  In order to receive SIF_Request messages, the agent is expected to be
	 *  registered as a Provider of one or more object types in at least one
	 *  zone. This method does not send SIF_Provide messages to any zones.<p>
	 *
	 *  @param publisher An object that implements the <code>ReportPublisher</code>
	 *      interface to respond to SIF_Request queries received for SIF_ReportObject 
	 * 		objects. This Publisher will be called whenever a request is received 
	 * 		and no other object in the message dispatching chain has processed 
	 * 		the message.
	 * 
	 * 	@since ADK 1.5
	 */
	public void setReportPublisher( ReportPublisher publisher )
	{
		if( publisher == null ){
			ADKUtils._throw( new IllegalArgumentException("ReportPublisher object cannot be null"),Agent.getLog() );
		}

		if( ReportingDTD.SIF_REPORTOBJECT == null ){
			ADKUtils._throw( new IllegalStateException("The ADK Reporting package is not loaded"),Agent.getLog() );
		}
		
		fPubs.put( ReportingDTD.SIF_REPORTOBJECT, publisher );
	}

	/**
	 *  Register a global Subscriber message handler with the agent for all SIF object types.<p>
	 *
	 *  Note agents typically register message handlers with Topics or with Zones
	 *  instead of with the Agent. The message dispatcher first
	 *  delivers messages to Topics, then to Zones, and finally to the Agent
	 *  itself.<p>
	 *
	 *  In order to receive SIF_Event messages, the agent is expected to be
	 *  registered as a Subscriber of one or more object types in at least one
	 *  zone. This method does not send SIF_Subscribe messages to any zones.<p>
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface to respond to SIF_Event notifications received by the agent.
	 *      This object will be called whenever a SIF_Event is received and no
	 *      other object in the message dispatching chain has processed the
	 *      message.
	 */
	public void setSubscriber( Subscriber subscriber )
	{
		setSubscriber(subscriber,null);
	}

	/**
	 *  Register a global Subscriber message handler with this agent for the 
	 *  specified SIF object type and only for the default SIF Context<p>
	 *
	 *  Note agents typically register message handlers with Topics or with Zones
	 *  instead of with the Agent. The message dispatcher first
	 *  delivers messages to Topics, then to Zones, and finally to the Agent
	 *  itself.<p>
	 *
	 *  In order to receive SIF_Event messages, the agent is expected to be
	 *  registered as a Subscriber of one or more object types in at least one
	 *  zone. This method does not send SIF_Subscribe messages to any zones.<p>
	 *  
	 *  @see Provisioner#setSubscriber(Subscriber, ElementDef, SubscriptionOptions)
	 *  @see Topic#setSubscriber(Subscriber, SubscriptionOptions)
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface to respond to SIF_Event notifications received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Subscriber will be called whenever a
	 *      SIF_Event is received and no other object in the message dispatching
	 *      chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies the
	 *      type of SIF Data Object this Subscriber will respond to.
	 */
	public void setSubscriber( Subscriber subscriber, ElementDef objectType )
	{
		if( subscriber == null )
			ADKUtils._throw( new IllegalArgumentException("Subscriber object cannot be null"),Agent.getLog() );

		if( objectType == null )
			// Set the wildcard subscriber
			fSubs.put( SIFDTD.SIF_MESSAGE, subscriber );
		else
			fSubs.put(objectType,subscriber);
	}

	/**
	 *  Register a global QueryResults message handler with this agent for all 
	 *  SIF object types and only for the default SIF Context<p>
	 *
	 *  Note agents typically register message handlers with Topics or with
	 *  Zones instead of with the Agent. The message dispatcher first
	 *  delivers messages to Topics, then to Zones, and finally to the Agent
	 *  itself.<p>
	 *  
	 *  @see Provisioner#setQueryResults(QueryResults)
	 *  @see Topic#setQueryResults(QueryResults, QueryResultsOptions)
	 *
	 *  @param queryResults An object that implements the <code>QueryResults</code>
	 *      interface to respond to SIF_Response query results received by the
	 *      agent. This object will be called whenever a SIF_Response is received
	 *      and no other object in the message dispatching chain has processed
	 *      the message.
	 */
	public void setQueryResults( QueryResults queryResults )
	{
		setQueryResults( queryResults, null );
	}

	/**
	 *  Register a global QueryResults message handler object with this agent for 
	 *  the specified SIF object type and only for the default SIF Context<p>
	 *
	 *  Note agents typically register message handlers with Topics or with
	 *  Zones instead of with the Agent. The message dispatcher first
	 *  delivers messages to Topics, then to Zones, and finally to the Agent
	 *  itself.<p>
	 *  
	 *  @see Provisioner#setQueryResults(QueryResults, ElementDef, QueryResultsOptions)
	 *  @see Topic#setQueryResults(QueryResults, QueryResultsOptions)
	 *
	 *  @param queryResults An object that implements the <code>QueryResults</code>
	 *      interface to respond to SIF_Response query results received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This QueryResults object will be called whenever
	 *      a SIF_Response is received and no other object in the message
	 *      dispatching chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies the
	 *      type of SIF Data Object this QueryResults message handler will
	 *      respond to.
	 */
	public void setQueryResults( QueryResults queryResults, ElementDef objectType )
	{
		if( queryResults == null )
			ADKUtils._throw( new IllegalArgumentException("QueryResults object cannot be null"),Agent.getLog() );

		if( objectType == null )
			// Set the wildcard query results handler
			fQueryResults.put( SIFDTD.SIF_MESSAGE, queryResults );
		else
			fQueryResults.put( objectType, queryResults );
	}

	/**
	 *  Gets the global Publisher message handler registered with the Agent for the 
	 * 	specified SIF object type<p>
	 * 	@param context The SIF context to look up the Publisher handler for.
	 * 		The default implementation of Agent only returns handlers for 
	 * 		SIFContext.DEFAULT
	
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 * 
	 *  @return The Publisher message handler registered for this object type by the 
	 * 		agent when it called the <code>setPublisher</code> method, or <code>null</code> if 
	 * 		no Publisher has been registered for the specified object type.
	 */
	public Publisher getPublisher( SIFContext context, ElementDef objectType )
	{
		Publisher p = null;
		if( SIFContext.DEFAULT.equals( context ) ){
			p =(Publisher)fPubs.get( objectType );
			if( p == null ){
				p = (Publisher)fPubs.get( SIFDTD.SIF_MESSAGE );
			}
		}
		return p;
	}

	/**
	 *  Gets the global ReportPublisher message handler registered with the Agent.<p>
	 * 
	 * 	@param context The SIF context to look up the ReportPublisher handler for.
	 * 		The default implementation of Agent only returns handlers for 
	 * 		SIFContext.DEFAULT
	 * 
	 *  @return The ReportPublisher message handler registered by the agent when
	 *      it called the <code>setReportPublisher</code> method, or <code>null</code> 
	 * 		if no ReportPublisher has been registered
	 * 
	 *	@since ADK 1.5
	 */
	public ReportPublisher getReportPublisher( SIFContext context )
	{
		if( !SIFContext.DEFAULT.equals( context ) ){
			return null;
		}
		return (ReportPublisher)fPubs.get( ReportingDTD.SIF_REPORTOBJECT );
	}

	/**
	 *  Gets the global Subscriber message handler registered with the Agent for 
	 * 	the specified SIF object type.<p>
	 * 
	 * 	@param context The SIF context to look up the Subscriber handler for.
	 * 		The default implementation of Agent only returns handlers for 
	 * 		SIFContext.DEFAULT
	 * 
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 * 
	 *  @return The Subscriber registered for this object type by the agent when
	 *      it called the <code>setSubscriber</code> method, or <code>null</code> 
	 * 		if no Subscriber has been registered for the specified object type.
	 */
	public Subscriber getSubscriber( SIFContext context, ElementDef objectType )
	{
		Subscriber s = null; 
		if( SIFContext.DEFAULT.equals( context ) ){
			s = fSubs.get( objectType );
			if( s == null ){
				s = fSubs.get( SIFDTD.SIF_MESSAGE );
			}
		}

		return s;
	}

	/**
	 *  Gets the global QueryResults message handler registered with the Agent 
	 * 	for the specified SIF object type.<p>
	 * 
	 * @param context The SIF context to look up the QueryResults handler for.
	 * 		The default implementation of Agent only returns handlers for 
	 * 		SIFContext.DEFAULT
	 * 
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 * 
	 *  @return The QueryResults object registered for this object type by the
	 *      agent when it called the <code>setQueryResults</code> method, or 
	 * 		<code>null</code> if no QueryResults object has been registered
	 * 		for the specified object type.
	 */
	public QueryResults getQueryResults( SIFContext context, ElementDef objectType )
	{
		QueryResults q = null;
		if( SIFContext.DEFAULT.equals( context ) ){
			q = (QueryResults)fQueryResults.get(objectType.name());
			if( q == null ){
				q = (QueryResults)fQueryResults.get( SIFDTD.SIF_MESSAGE );
			}
		}
		return q;
	}
	
	/**
	 * 	Register a <i>MessagingListener</i> to listen to messages received by the
	 * 	message handlers of this class.<p>
	 * 
	 * 	NOTE: Agents may register a MessagingListener with the Agent or Zone
	 * 	classes. When a listener is registered with both classes, it will be 
	 * 	called twice. Consequently, it is recommended that most implementations 
	 * 	choose to register MessagingListeners with only one of these classes 
	 * 	depending on whether the agent is interested in receiving global
	 * 	notifications or notifications on only a subset of zones.<p>
	 * 
	 * 	@param listener a MessagingListener implementation
	 */
	public void addMessagingListener( MessagingListener listener )
	{
		fMessagingListeners.add( listener );
	} 	
	
	/**
	 * 	Remove a <i>MessagingListener</i> previously registered with the
	 * 	<code>addMessagingListener</code> method.<p>
	 * 
	 * 	@param listener a MessagingListener implementation
	 */
	public void removeMessagingListener( MessagingListener listener )
	{
		fMessagingListeners.remove( listener );
	} 	
	
	/**
	 * 	Return a list of all MessagingListeners registered with the agent
	 * @return A list of MessagingListener objects
	 */
	public List<MessagingListener> getMessagingListeners()
	{
		return Collections.unmodifiableList( fMessagingListeners );
	}	

	/**
	 *  Purge all pending incoming and/or outgoing messages from this agent's
	 *  queue. Affects all zones with which the agent is currently connected.
	 *  See also the Topic.purgeQueue and Zone.purgeQueue methods to purge all
	 *  zones associated with a topic or a specific zone, respectively.<p>
	 *
	 *  <ul>
	 *      <li>
	 *          If the Agent Local Queue is enabled, messages are permanently
	 *          and immediately removed from the queue. Any messages in transit
	 *          are not affected.
	 *      </li>
	 *      <li>
	 *          If the underlying messaging protocol offers a mechanism to clear
	 *          the agent's queue, it is invoked. (SIF 1.0 does not have such a
	 *          mechanism.)
	 *      <li>
	 *          Otherwise, all incoming messages received by the agent having a
	 *          timestamp earlier than or equal to the time this method is called
	 *          are discarded. This behavior persists until the agent is
	 *          terminated or until a message is received having a later
	 *          timestamp.
	 *      </li>
	 *  </ul>
	 *
	 *  @param incoming true to purge incoming messages
	 *  @param outgoing true to purge outgoing messages (e.g. pending SIF_Events)
	 *      when the Agent Local Queue is enabled
	 * @throws ADKException 
	 */
	public void purgeQueue( boolean incoming, boolean outgoing )
		throws ADKException
	{
	}

	/**
	 *  Utility method to generate a GUID for SIF Data Objects and messages
	 *  @return A GUID
	 *  @see openadk.util.GUIDGenerator
	 */
	public static String makeGUID()
	{
		return GUIDGenerator.makeGUID();
	}

	/**
	 *  Sets the <i>UndeliverableMessageHandler</i> to be called when a dispatching
	 *  error occurs on a zone but no handler is registered with that zone. For
	 *  more information, please refer to the UnderliverableMessageHandler
	 *  class comments.<p>
	 *
	 *  @param handler The handler to call when the ADK cannot dispatch an
	 *      inbound message
	 */
	public void setErrorHandler( UndeliverableMessageHandler handler )
	{
		fErrHandler = handler;
	}

	/**
	 *  Gets the <i>UndeliverableMessageHandler</i> for this agent.<p>
	 * @return The UndeliverableMessageHandler assigned to this agent
	 *  @see #setErrorHandler
	 */
	public UndeliverableMessageHandler getErrorHandler()
	{
		return fErrHandler;
	}

	/**
	 *  Gets the root Log4j Logger for this agent.
	 * @return The Logger for this agent
	 */
	public static Logger getLog()
	{
		return log;
	}

	/**
	 *  Gets the Log4j Logger for a specific zone.
	 * @param zone The zone to get the logger for 
	 * @return the Log4j Category for a specific zone.
	 */
	public static Logger getLog( Zone zone )
	{
		Logger zlog = Logger.getLogger( ADK.LOG_IDENTIFIER + ".Agent$" + zone.getZoneId() );

		return zlog == null ? log : zlog;
	}
	
	/**
	 * 	Gets the agent-global ServerLog instance.<p>
	 * 
	 * 	Agents that wish to customize server-side logging may call this 
	 * 	method to obtain the global Agent ServerLog instance. Call any of the 
	 * 	following methods to set up the chain of loggers that will be inherited 
	 * 	by all Zones:<p>
	 * 
	 * 	<ul>
	 * 		<li><code>addLogger</code></li>
	 * 		<li><code>removeLogger</code></li>
	 * 		<li><code>clearLoggers</code></li>
	 * 		<li><code>getLoggers</code></li>
	 * 	</ul>
	 *
	 * 	Unlike client-side logging, server logging requires a connection to a 
	 * 	Zone Integration Server. Because the current SIF 1.x infrastructure does 
	 * 	not allow connections to servers independent of a zone, the logging 
	 * 	methods of ServerLog are useful only when called within the context of a 
	 * 	zone. Therefore, calling any of the logging methods on the ServerLog 
	 * 	instance returned by this method will result in an IllegalStateException. 
	 * 	This method is provided only to set up the ServerLog logger chain at
	 * 	the global Agent level.<p> 
	 * 
	 * 	@return The agent's ServerLog instance
	 * 
	 * 	@since ADK 1.5
	 */
	public static ServerLog getServerLog()
	{
		return serverLog;
	}	
	
	/**
	 * 	Gets the ServerLog for a specific zone.<p>
	 * 
	 * 	This form of <code>getServerLog</code> is provided for consistency with 
	 * 	the <code>getLog</code> method. Note you may also call the 
	 * 	<code>Zone.getServerLog</code> method directly to obtain a ServerLog for 
	 * 	for a zone.<p>
	 * 
	 * 	@param zone The zone to obtain a ServerLog instance for
	 * 	@return The ServerLog instance for the zone
	 */
	public static ServerLog getServerLog( Zone zone )
	{
		return zone.getServerLog();
	}	

	/**
	 *  Helper routine to check that the <code>initialize</code> method has been called
	 *  @throws ADKException if not initialized
	 */
	private void _checkInit()
	{
		if( fShutdown )
			throw new LifecycleException("Agent has been shutdown");

		if( !fInit ) {
			Thread.dumpStack();
			ADKUtils._throw( new LifecycleException("Agent not initialized"), log );
		}
	}

	/**
	 * Sets the source of this agent's configuration. This property can be 
	 * used by other subsystems in the ADK to retrieve the configuration instance
	 * and pull additional configuration information.
	 * @param config The object that was used to configure the agent
	 */
	public void setConfigurationSource(Object config) {
		fConfigurationSource = config;
	}
	
	/**
	 * Gets the source of this agent's configuration. For example, if AgentConfig is
	 * used to configure this instance of the agent, this property will return the
	 * AgentConfig object.
	 * @return The object that was used to configure the agent
	 */
	public Object getConfigurationSource(){
		return fConfigurationSource;	
	}

	/**
	 * Returns the TransportManager instance for this agent. 
	 * TransportManager manages the state of all open transports
	 * known to this agent instance. The TransportManager will
	 * be null until {@link #initialize()} is called.
	 * @return the TransportManager instance for this agent.
	 */
	public final TransportManager getTransportManager() {
		return fTransportManager;
	}
	
}
