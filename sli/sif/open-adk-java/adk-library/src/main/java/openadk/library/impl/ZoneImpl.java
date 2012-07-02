//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.ADKTransportException;
import openadk.library.ADKZoneNotConnectedException;
import openadk.library.Agent;
import openadk.library.AgentMessagingMode;
import openadk.library.AgentProperties;
import openadk.library.AgentProvisioningMode;
import openadk.library.ElementDef;
import openadk.library.Event;
import openadk.library.EventAction;
import openadk.library.IProtocolHandler;
import openadk.library.MessagingListener;
import openadk.library.ProvisioningOptions;
import openadk.library.Publisher;
import openadk.library.PublishingOptions;
import openadk.library.Query;
import openadk.library.QueryResults;
import openadk.library.QueryResultsOptions;
import openadk.library.ReportPublisher;
import openadk.library.ReportPublishingOptions;
import openadk.library.RequestInfo;
import openadk.library.SIFContext;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFMessageInfo;
import openadk.library.SIFParser;
import openadk.library.SIFStatusCodes;
import openadk.library.SIFVersion;
import openadk.library.ServiceEvent;
import openadk.library.Subscriber;
import openadk.library.SubscriptionOptions;
import openadk.library.Topic;
import openadk.library.TopicFactory;
import openadk.library.UndeliverableMessageHandler;
import openadk.library.Zone;
import openadk.library.infra.InfraDTD;
import openadk.library.infra.LogLevel;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_AgentACL;
import openadk.library.infra.SIF_Context;
import openadk.library.infra.SIF_Contexts;
import openadk.library.infra.SIF_Data;
import openadk.library.infra.SIF_Object;
import openadk.library.infra.SIF_ProvideObjects;
import openadk.library.infra.SIF_Provider;
import openadk.library.infra.SIF_Providers;
import openadk.library.infra.SIF_PublishAddObjects;
import openadk.library.infra.SIF_PublishChangeObjects;
import openadk.library.infra.SIF_PublishDeleteObjects;
import openadk.library.infra.SIF_Request;
import openadk.library.infra.SIF_RequestObjects;
import openadk.library.infra.SIF_RespondObjects;
import openadk.library.infra.SIF_ServiceInput;
import openadk.library.infra.SIF_Status;
import openadk.library.infra.SIF_SubscribeObjects;
import openadk.library.infra.SIF_ZoneStatus;
import openadk.library.log.ServerLog;
import openadk.library.policy.ADKDefaultPolicy;
import openadk.library.policy.PolicyFactory;
import openadk.library.reporting.ReportingDTD;
import openadk.library.services.SIFZoneService;
import openadk.library.services.SIFZoneServiceProxy;
import openadk.library.services.ServiceRequestInfo;
import openadk.library.services.impl.ServiceResponseDelivery;

import org.apache.log4j.Logger;


/**
 *  Implementation of the Zone interface.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ZoneImpl implements Zone
{
	// Values for the fState variable
	protected static final int
		UNINIT =            0x00000000, // connect() not yet called
		CONNECTED =         0x00000001, // connect() called successfully
		CLOSED =            0x00000002, // disconnect() called; ZoneImpl object now invalid
		SHUTDOWN =          0x00000004, // shutdown() called
		SLEEPING =          0x00000020, // Zone is in sleep mode
		GETZONESTATUS =     0x00000040; // getZoneStatus is blocking on results

	/**
	 *  Log4j logging category for this zone
	 */
	public Logger log;


	/**
	 *  The Agent that owns this zone
	 */
	protected Agent fAgent;

	/**
	 *  The unique string identifier for this zone
	 */
	protected String fZoneId;

	/**
	 *  The URL of the Zone Integration Server that manages this zone
	 */
	protected URL fZoneUrl;

	/**
	 *  Zone properties
	 */
	private AgentProperties fProps;


	/**
	 *  Connection state
	 */
	protected int fState = UNINIT;

	/**
	 *  The ProtocolHandler for this zone
	 */
	protected IProtocolHandler fProtocolHandler;

	/**
	 *  The MessageDispatcher for this zone
	 */
	private MessageDispatcher fDispatcher;

	/**
	 *  ResponseDelivery thread for this zone that handles sending SIF_Response packets
	 *  that have been stored on the local file system as a result of calling
	 *  the Publisher.onQuery message handler.
	 */
	private ResponseDelivery fResponseDelivery;
	
	/**
	 *  ResponseDelivery thread for this zone that handles sending SIF_Response packets
	 *  that have been stored on the local file system as a result of calling
	 *  the Publisher.onQuery message handler.
	 */
	private ServiceResponseDelivery fServiceResponseDelivery;

	/**
	 *  ResponseDelivery thread for this zone that handles sending SIF_Response packets
	 *  for SIF_ReportObject requests stored on the local file system as a result of
	 * 	calling the ReportPublisher.onQuery message handler method.
	 */
	private ResponseDelivery frptResponseDelivery;

	/**
	 *  The IAgentQueue for this zone
	 */
	protected IAgentQueue fQueue;

	/**
	 *  Thread group for all threads associated with this zone
	 */
	protected ThreadGroup threadgrp;

	/**
	 * 	Manages the MessagingListeners
	 */
	protected List<MessagingListener>  fMessagingListeners = new ArrayList<MessagingListener>(0);

	/**
	 *  The UndeliverableMessageHandler for this zone
	 */
	protected UndeliverableMessageHandler fErrHandler;


	/**
	 * The matrix of all Publishers, Subsriber, etc. for each SIF Context running in this
	 */
	private ProvisioningMatrix fProvMatrix = new ProvisioningMatrix();


	/**
	 *  If a SIF Error is received in response to a SIF_Provide or SIF_Subscribe
	 *  message sent by the provision() method, it is added to this list and
	 *  returned when <code>getConnectWarnings</code> is called
	 */
	protected List<SIFException> fProvWarnings = new ArrayList<SIFException>();

	/**
	 *  Reference to the ISIFPrimitives object to use for SIF messaging
	 */
	protected ISIFPrimitives fPrimitives;

	/**
	 *  The last SIF_ZoneStatus object received by MessageDispatcher. When
	 *  getZoneStatus is called, it blocks on fZSLock until setZoneStatus()
	 *  is called by MessageDispatcher or the timeout period specified by the
	 *  caller has elapsed.
	 */
	protected SIF_ZoneStatus fZoneStatus = null;

	//  Objects used as semaphores
	private Object fZSLock = new Object();
	private Object fConnLock = new Object();

	/**
	 * 	Semaphore to ensure that MessageDispatcher does not attempt to process a
	 * 	SIF_Response message before the Zone.onQuery has completed. Zone.onQuery
	 * 	synchronizes on this object; MessageDispatcher must also synchronize on
	 * 	this object when it receives a SIF_Response.
	 *
	 *	@see #waitForRequestsInProgress
	 */
	private Object fReqLock = new Object();

	/**
	 *  Optional user data set with the setUserData method
	 */
	protected Object fUserData;
	
	/**
	 * Whenever SIF_ZoneStatus is requested, whatever is received will be cached here.
	 */
	private SIF_ZoneStatus lastReceivedZoneStatus = null;
	
	public SIF_ZoneStatus getLastReceivedSIF_ZoneStatus(boolean requestIfMissing) {
		if (lastReceivedZoneStatus == null && requestIfMissing) {
			try {
				this.getZoneStatus();
			} catch (ADKException e) {
				log.error("Unexpected failure requesting SIF_ZoneStatus: " + e, e);
			}
		}
		return lastReceivedZoneStatus;
	}


	@SuppressWarnings("unused")
	private ZoneImpl() { }

	/**
	 *  Constructs a Zone instance
	 *  @param zoneId The name of the zone
	 *  @param zoneUrl The URL of the Zone Integration Server that manages this zone
	 *  @param agent The Agent object. Any ADK object such as a Topic can always
	 *      find a reference to the Agent by asking a Zone
	 *  @param props Zone properties
	 */
	protected ZoneImpl( String zoneId, String zoneUrl, Agent agent, AgentProperties props )
		throws ADKTransportException
	{
		if( zoneId == null || zoneId.trim().length() < 1 )
			ADKUtils._throw( new IllegalArgumentException("Zone name cannot be null or blank"),log );

		log = Logger.getLogger( Agent.LOG_IDENTIFIER + "$" + zoneId );

		if( zoneUrl == null || zoneUrl.trim().length() < 1 )
			ADKUtils._throw( new IllegalArgumentException("Zone URL cannot be null or blank"),log );
		if( agent == null )
			ADKUtils._throw( new IllegalArgumentException("Agent cannot be null"),log );

		//  Create a ThreadGroup to organize this zone's threads (e.g. the
		//  MessageDispatcher, the ProtocolHandlers, etc.)
		threadgrp = new ThreadGroup("Zone-"+zoneId);

		fAgent = agent;
		fZoneId = zoneId;
		setProperties(props);

		try {
			fZoneUrl = new URL(zoneUrl);
		} catch( MalformedURLException mue ) {
			throw new ADKTransportException("Zone URL is malformed: " + zoneUrl, this );
		}
	}

	/**
	 *  Shutdown the zone.
	 * @throws ADKException
	 */
	public void shutdown()
		throws ADKException
	{
		if( fProtocolHandler == null )
			return;

		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
			log.info( "Shutting down zone..." );

		fState |= SHUTDOWN;

		if( getFDispatcher() != null ) {
			try {
				if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 )
					log.info( "Shutting down Message Dispatcher" );
				getFDispatcher().shutdown();
			} catch( Throwable ignored ) {
				log.error("Error shutting down Message Dispatcher: "+ignored, ignored);
			}
		}

		setFDispatcher(null);

		if( fProtocolHandler != null ) {
			try {
				if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
					log.info( "Shutting down Protocol Handler" );
				}
								
				fProtocolHandler.close( this );
			} catch( Throwable ignored ) {
				log.error("Error shutting down Protocol Handler: "+ignored, ignored);
			}
		}

		fProtocolHandler = null;

		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
			log.info( "Zone shutdown complete" );
		}
	}

	/**
	 *  Gets the ZoneProperties for this zone<p>
	 *  @return The ZoneProperties object for this zone
	 */
	public AgentProperties getProperties()
	{
		return fProps;
	}

	/**
	 *  Sets the ZoneProperties for this zone<p>
	 *  @param props A ZoneProperties object
	 */
	public void setProperties( AgentProperties props )
	{
		fProps = props == null ? new AgentProperties( fAgent.getProperties() ) : props;
	}

	/**
	 *  Gets the Agent object
	 */
	public Agent getAgent() {
		return fAgent;
	}

	/**
	 *  Gets the zone name
	 *  @returns The name of the zone
	 */
	public String getZoneId() {
		return fZoneId;
	}

	/**
	 *  Gets the URL of the Zone Integration Server that manages this zone
	 *  @returns The URL to the ZIS in whatever format is expected by the ZIS
	 *      (e.g. "https://host:port/opensif/zis/zone-name" for the OpenSIF ZIS,
	 *      "https://host:port/zone-name" for the SIFWorks ZIS, etc.)
	 */
	public URL getZoneUrl()
	{
		return fZoneUrl;
	}

	public void connect( int provOptions )
		throws ADKException
	{
		synchronized( fConnLock )
		{
		if( isConnected() ){
			ADKUtils._throw( new IllegalStateException("Zone already connected"),log );
		}

		fPrimitives = ADK.getPrimitives();

		fState = 0;

		//
		//  Initialize the Agent Local Queue if supported and enabled
		//
		/*
		if( ADK.supportsALQ() && !fProps.getDisableQueue() )
		{
			String clazz = fProps.getProperty("adk.queue.impl","openadk.library.impl.JDBCQueue");
			try {
				queue = (IAgentQueue)Class.forName(clazz).newInstance();
			} catch( ClassNotFoundException cnfe ) {
				ADKUtils._throw( new ADKQueueException("Agent Local Queue is not supported ("+cnfe+")", this ), log);
			} catch( Exception ex ) {
				ADKUtils._throw( new ADKQueueException("Failed to create Agent Local Queue implementation class: "+ex, this ),log );
			}

			queue.initialize(this,fProps);
		}
		*/

		//  Sleep on connect will cause any messages received by the dispatcher
		//  to be turned away with a status code 8 ("Receiver sleeping")
		if( ( provOptions & ADKFlags.SLEEP_ON_CONNECT ) != 0 )
			fState |= SLEEPING;

		//  Initialize the MessageDispatcher
		if( getFDispatcher() == null ){
			setFDispatcher(new MessageDispatcher(this));
		}

		try
		{
		    //  Start the IProtocolHandler for this zone. The protocol handler
			//  is transport-specific so it is created by the Transport object
			//  associated with the zone. Starting the handler typically
			//  establishes a socket to the ZIS and starts a thread to pull
			//  messages when the zone is running is Pull mode.
			fProtocolHandler = ((TransportManagerImpl)fAgent.getTransportManager()).activate( this );
			fProtocolHandler.open( this );

		}
		catch( ADKException adke )
		{
			ADKUtils._throw(adke,log);
		}
		catch( Exception ex )
		{
			ADKUtils._throw( new ADKException("Failed to start transport protocol: "+ex, this ),log );
		}

		//	When running in Push mode, make sure protocol handler is
		//	started right away because the ZIS may send a message to us
		//	immediately after registration. The SIF Compliance test harness
		//	does this, and if the protocol handler is not started a 404
		//	error is returned (which causes the agent to fail one of the
		//	certification tests.)  In Pull mode, the handler is started
		//	later in this function since starting it invokes the Pull
		//	thread (which should not be done until after SIF Register).
		//
		try
		{
			if( getProperties().getMessagingMode() == AgentMessagingMode.PUSH ) {
				fProtocolHandler.start();

				//  Transport protocol still active? With Jetty, the Acceptor thread may
				//  have gone down by now and so we need to check again to see if the
				//  Listeners are still active for https/http.
				if( !fProtocolHandler.isActive( this ) ){
					ADKUtils._throw( new ADKTransportException("The transport protocol is not available for this zone", this ),log );
				}
			}
		}
		catch( ADKException adke )
		{
			ADKUtils._throw(adke,log);
		}
		catch( Exception ex )
		{
			ADKUtils._throw( new ADKException("Failed to start transport protocol: "+ex, this ),log );
		}

		//
		//  If any exception is thrown from this point on, catch-rethrow but
		//  set the state to Disconnected.
		//
		boolean _connectFailed = true;

		SIF_AgentACL acl = null;
		try
		{
			//  Send SIF_Register
			if( ( ( provOptions & ADKFlags.PROV_REGISTER ) != 0 ) &&
				getProperties().getProvisioningMode() == AgentProvisioningMode.ADK )
			{
				SIF_Ack ack = fPrimitives.sifRegister(this);
				if( ack.hasStatusCode(8) )
					fState |= SLEEPING;
				if( !ack.hasStatusCode(0) ) {
					fState &= ~CONNECTED;
					ADKUtils._throw( new SIFException(ack,this),log );
				}
				else
				if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
					log.info("SIF_Register successful");
				}

				SIF_Data payload = ack.getSIF_Status().getSIF_Data();
				if( payload != null && payload.getChildCount() > 0 ){
					SIFElement child = payload.getChildList().get( 0  );
					if( child.getElementDef() == InfraDTD.SIF_AGENTACL ){
						acl = ( SIF_AgentACL)child;
					}
				}
			}

			//  We're now connected
			fState |= CONNECTED;

			//  Send pending SIF_Responses if any leftover from prior session
			if( ResponseDelivery.hasPendingPackets( ResponseDelivery.SRC_GENERIC, this ) )
			{
				final ResponseDelivery _rd = getResponseDelivery();
				Runnable task = new Runnable() {
					public void run() {
						try {
							_rd.process();
						} catch( ADKException adke ) {
							log.debug( "Failed to send SIF_Response packets from a previous session", adke );
						}
					}
					
				};
				new Thread(threadgrp,task){}.start();
			}

			//  Send pending SIF_Responses if any leftover from prior session
			if( ResponseDelivery.hasPendingPackets( ResponseDelivery.SRC_SIFREPORTOBJECT, this ) )
			{
				if( frptResponseDelivery == null )
					frptResponseDelivery = new ResponseDelivery( this, ResponseDelivery.SRC_SIFREPORTOBJECT );

				final ResponseDelivery _rd2 = frptResponseDelivery;
				Runnable task = new Runnable() {
					public void run() {
						try {
							_rd2.process();
						} catch( ADKException adke ) {
							log.debug( "Failed to send SIF_ReportObject response packets from a previous session", adke );
						}
					}
				};
				new Thread(threadgrp,task){}.start();
			}

			if( ( provOptions & ADKFlags.SLEEP_ON_CONNECT ) != 0 )
			{
				try {
					//  Sleep immediately if requested by the caller
					sleep();
					fState |= SLEEPING;
				} catch( Exception ignored ) {
				}
			}
			else
			{
				//  SIF spec recommends sending SIF_Wakeup on startup; no need
				//  to send this when SIF_Register is sent because it is implicit
				//  in that case
				SIF_Ack ack = null;
				if( ( provOptions & ADKFlags.PROV_REGISTER ) == 0 ) {
					if( getProperties().getProvisioningMode() == AgentProvisioningMode.ADK  )
						wakeup();
				}

				//  Calling sifPing will determine the sleep mode of the zone
				//  from the server's perspective
				ack = sifPing();
				if( ack.hasError() )
					throw new SIFException( ack, this );
			}

			//  Send SIF_Provide and SIF_Subscribe
			// JEN ADKFlags.PROV_NONE
			if (provOptions != ADKFlags.PROV_NONE)
				provision( acl );

			try
			{
				//  Protocol handler can start now that the zone is connected. If
				//	running in Push mode, this was done earlier in this function.
				if( getProperties().getMessagingMode() == AgentMessagingMode.PULL ){
					fProtocolHandler.start();
					fState |= CONNECTED;
				}
				
				//  Success!
				_connectFailed = false;
			}
			catch( Exception ex )
			{
				throw new ADKException( "Failed to start " + fProtocolHandler.getName() + " protocol handler: " + ex.getMessage(), this );
			}
		}
		finally
		{
			try
			{
				//	When running in Push mode, stop the protocol handler if
				//	SIF_Register failed
				if( ! ( ( fState & CONNECTED ) != 0 )  ) {
					if( getProperties().getMessagingMode() == AgentMessagingMode.PUSH )
						fProtocolHandler.shutdown();
				}
			}
			catch( Exception ex )
			{
				log.error( "Failed to stop transport protocol: " + ex );
			}

			if( _connectFailed )
			{
				//  Put the agent to sleep and set the zone connection state to
				//  Disconnected. The client should try and reconnect the zone again.
				try {
					sleep();
				} catch( Exception ignored ) {
				}

				fState &= ~CONNECTED;
			}
		}
}
	}

	/**
	 * 	Gets the ResponseDelivery thread for generic SIF_Response processing
	 * 	on this zone, creating it if not already initialized. This method is
	 * 	normally called by the DataObjectOutputStream class to notify the
	 * 	delivery thread that new packets are waiting for delivery to the ZIS.
	 * @return The ResponseDelivery thread used for SIF_Response processing
	 * @throws ADKException
	 */
	public synchronized ResponseDelivery getResponseDelivery()
		throws ADKException
	{
		if( fResponseDelivery == null )
			fResponseDelivery = new ResponseDelivery( this, ResponseDelivery.SRC_GENERIC );

		return fResponseDelivery;
	}

	/**
	 * 	Gets the ResponseDelivery thread for generic SIF_Response processing
	 * 	on this zone, creating it if not already initialized. This method is
	 * 	normally called by the DataObjectOutputStream class to notify the
	 * 	delivery thread that new packets are waiting for delivery to the ZIS.
	 * @return The ResponseDelivery thread used for SIF_Response processing
	 * @throws ADKException
	 */
	public synchronized ServiceResponseDelivery getServiceResponseDelivery()
		throws ADKException
	{
		if( fServiceResponseDelivery == null )
			fServiceResponseDelivery = new ServiceResponseDelivery( this, ResponseDelivery.SRC_GENERIC );

		return fServiceResponseDelivery;
	}

	/**
	 * 	Gets the ResponseDelivery thread for SIF_ReportObject SIF_Response
	 * 	processing on this zone, creating it if not already initialized. This
	 *	method is normally called by the ReportObjectOutputStream class to
	 *	notify the delivery thread that new packets are waiting for delivery
	 *	to the ZIS.
	 * @return The ResponseDelivery thread used for SIF_Response processing
	 * @throws ADKException
	 */
	public synchronized ResponseDelivery getReportResponseDelivery()
		throws ADKException
	{
		if( frptResponseDelivery == null )
			frptResponseDelivery = new ResponseDelivery( this, ResponseDelivery.SRC_SIFREPORTOBJECT );

		return frptResponseDelivery;
	}

	protected void provision()
		throws ADKException
	{
		provision( null );
	}

	/**
	 * Provisions the agent with the zone
	 * @param acl The SIF_AgentACL object to use for provisioning, if available, otherwise null
	 */
	protected void provision( SIF_AgentACL acl )
		throws ADKException
	{
		if( !isConnected() )
			return;

		if(	getProperties().getProvisioningMode() != AgentProvisioningMode.ADK ) {
			if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
    			log.info( "Zone not sending provisioning messages because ADK-managed provisioning is not in effect" );
			return;
		}

		fProvWarnings.clear();

		SIFVersion effectiveZisVersion = getHighestEffectiveZISVersion();
		if( ( effectiveZisVersion.compareTo( SIFVersion.SIF20 ) ) < 0 ||
				getProperties().getProvisionInLegacyMode() ){
			provisionLegacy();
		} else {
			provisionSIF20( acl );
		}

	}

	/**
	 * Send SIF_Subscribe and SIF_Provide provisioning messages.
	 */
	private void provisionLegacy()
		throws ADKException
	{
		//  - Visit each Topic to which this zone is joined and send a message
		//    if the Topic has a publisher and/or subscriber and no message has
		//    yet been sent for the Topic's data object type.
		//
		//  - Send a message for each data object type for which a subscriber
		//    and/or publisher is registered with this zone (and no message has
		//    yet been sent for that object type.)
		//
		List<String> subSent = new ArrayList<String>();
		List<String> pubSent = new ArrayList<String>();

		//  Send provisioning messages for all topics to which this zone is
		//  currently joined...
		for( Topic topic : fAgent.getTopicFactory().getAllTopics( SIFContext.DEFAULT ) )
		{
			TopicImpl t = (TopicImpl)topic;
			if( t.fZones.contains( this ) )
			{
				//  This zone is joined to the topic. Send a SIF_Subscribe and/or
				//  SIF_Provide for the topic's data type.
				String objType = t.getObjectType();
	    		if( t.fSub != null && ( t.fSubOpts == null || t.fSubOpts.getSendSIFSubscribe() ) ){
					subSent.add( objType );
	    		}

	    		if( t.fPub != null && ( t.fPubOpts == null || t.fPubOpts.getSendSIFProvide() ) ){
					pubSent.add( objType );
	    		}
				else if ( t.fReportPub != null &&
							( t.fReportPubOpts == null || t.fReportPubOpts.getSendSIFProvide() ) ){
						pubSent.add( objType );
				}
			}
		}

		// Add subscribers
		for( ProvisionedObject<Subscriber, SubscriptionOptions> subOption : fProvMatrix.getAllSubscribers( true ) ){
			SubscriptionOptions subOptions = subOption.getProvisioningOptions();
			if( subSent.contains( subOption.getObjectType().name() ) || !subOptions.getSendSIFSubscribe() ){
				continue;
			}
			// For legacy provisioning, only add subscribers in the default context
			for( SIFContext context : subOptions.getSupportedContexts() ){
				if( context.equals( SIFContext.DEFAULT ) ){
					subSent.add( subOption.getObjectType().name() );
				} else {
					log.debug( String.format( "SIF_Subscribe will not be sent in legacy mode for %s in SIF Context %s",
							subOption.getObjectType().name(), context.getName() ) );
				}
			}
		}

		// Add publishers
		for( ProvisionedObject<Publisher, PublishingOptions> pubOption : fProvMatrix.getAllPublishers( true ) ){
			PublishingOptions pubOptions = pubOption.getProvisioningOptions();
			if( pubSent.contains( pubOption.getObjectType().name() ) ||  !pubOptions.getSendSIFProvide() ){
				continue;
			}
			// For legacy provisioning, only add subscribers in the default context
			for( SIFContext context : pubOptions.getSupportedContexts() ){
				if( context.equals( SIFContext.DEFAULT ) ){
					pubSent.add( pubOption.getObjectType().name() );
				} else {
					log.debug( String.format( "SIF_Provide will not be sent in legacy mode for %s in SIF Context %s",
							pubOption.getObjectType().name(), context.getName() ) );
				}
			}
		}

		// Add report publishers
		for( ProvisionedObject<ReportPublisher, ReportPublishingOptions> pubOption : fProvMatrix.getAllReportPublishers() ){
			ReportPublishingOptions pubOptions = pubOption.getProvisioningOptions();
			if( !pubOptions.getSendSIFProvide() ){
				continue;
			}
			// For legacy provisioning, only add subscribers in the default context
			for( SIFContext context : pubOptions.getSupportedContexts() ){
				if( context.equals( SIFContext.DEFAULT ) ){
					pubSent.add( pubOption.getObjectType().name() );
				} else {
					log.debug( String.format( "SIF_Provide will not be sent in legacy mode for %s in SIF Context %s",
							pubOption.getObjectType().name(), context.getName() ) );
				}
			}
		}

		if( subSent.size() > 0 )
		{
			if( getProperties().isBatchProvisioning() )
			{
				//  Send a single SIF_Provide for the set of objects identified above
				String[] subTypes = new String[ subSent.size() ];
				subSent.toArray( subTypes );
				SIF_Ack ack = fPrimitives.sifSubscribe( this, subTypes );
				if( !ack.hasStatusCode( SIFStatusCodes.ALREADY_SUBSCRIBED_5 ) && ack.hasError() )
				{
					if( !getProperties().getIgnoreProvisioningErrors() ) {
						SIFException se = new SIFException( ack, this );
						if( se.getSIFErrorCategory() == SIFErrorCategory.ACCESS_PERMISSIONS ) {
							fProvWarnings.add( se );
						}
						else {
							ADKUtils._throw( se, null );
						}
					}
					else {
						if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
							log.info("SIF_Subscribe errors ignored for " + arrayToStr(subTypes));
						}
					}
				}
				else {
					if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ) {
						log.info("SIF_Subscribe successful for "+arrayToStr(subTypes));
					}
				}
			}
			else
			{
				//  Send individual SIF_Provide messages for the set of objects identified above.
				String[] subTypes = new String[ subSent.size() ];
				subSent.toArray( subTypes );
				for( int i = 0; i < subTypes.length; i++ )
				{
					SIF_Ack ack = fPrimitives.sifSubscribe( this, new String[] { subTypes[i] } );
					if( !ack.hasStatusCode( SIFStatusCodes.ALREADY_SUBSCRIBED_5 ) && ack.hasError() )
					{
						if( !getProperties().getIgnoreProvisioningErrors() )
						{
							SIFException se = new SIFException( ack, this );
							if( se.getSIFErrorCategory() == SIFErrorCategory.ACCESS_PERMISSIONS )
								fProvWarnings.add( se );
							else
								ADKUtils._throw( se, null );
						}
						else
						if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
							log.info("SIF_Subscribe errors ignored for " + subTypes[ i ] );
					}
					else
					if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
						log.info("SIF_Subscribe successful for " + subTypes[ i ] );
				}
			}
		}

		if( pubSent.size() > 0 )
		{
			if( getProperties().isBatchProvisioning() )
			{
				//  Send a single SIF_Provide for the set of objects identified above
				String[] pubTypes = new String[ pubSent.size() ];
				pubSent.toArray( pubTypes );
				SIF_Ack ack = fPrimitives.sifProvide( this, pubTypes );
				if( !ack.hasStatusCode( SIFStatusCodes.ALREADY_PROVIDER_6 ) && ack.hasError() )
				{
					if( !getProperties().getIgnoreProvisioningErrors() )
					{
						SIFException se = new SIFException( ack, this );
						if( se.getSIFErrorCategory() == SIFErrorCategory.ACCESS_PERMISSIONS )
							fProvWarnings.add( se );
						else
							ADKUtils._throw( se, null );
					}
					else
					if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
						log.info("SIF_Provide errors ignored for "+arrayToStr(pubTypes));
				}
				else
				if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
					log.info("SIF_Provide successful for "+arrayToStr(pubTypes));
			}
			else
			{
				//  Send individual SIF_Provide messages for the set of objects identified above.
				String[] pubTypes = new String[ pubSent.size() ];
				pubSent.toArray( pubTypes );
				for( int i = 0; i < pubTypes.length; i++ )
				{
					SIF_Ack ack = fPrimitives.sifProvide( this, new String[] { pubTypes[i] } );
					if( !ack.hasStatusCode( SIFStatusCodes.ALREADY_PROVIDER_6 ) && ack.hasError() )
					{
						if( !getProperties().getIgnoreProvisioningErrors() )
						{
							SIFException se = new SIFException( ack, this );
							if( se.getSIFErrorCategory() == SIFErrorCategory.ACCESS_PERMISSIONS )
	    						fProvWarnings.add( se );
							else
								ADKUtils._throw( se, null );
						}
						else
						if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
							log.info("SIF_Provide errors ignored for " + pubTypes[ i ] );
					}
					else
					if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 )
						log.info("SIF_Provide successful for " + pubTypes[ i ] );
				}
			}
		}
	}

	/**
	 * Provisions the agent with the zone using the SIF_Provision message
	 * @param acl The SIF_AgentACL object, if received from SIF_Register, or null
	 * @throws ADKException
	 */
	private void provisionSIF20( SIF_AgentACL acl )
		throws ADKException
	{

		SIF_SubscribeObjects subscribeObjects = new SIF_SubscribeObjects();;
		SIF_RequestObjects requestObjects = new SIF_RequestObjects();
		SIF_ProvideObjects provideObjects = new SIF_ProvideObjects();
		SIF_RespondObjects respondObjects = new SIF_RespondObjects();

		//
		// Add subscribers
		//
		provisionHandlers( subscribeObjects, fProvMatrix.getAllSubscribers( true ) );

		//
		// Add requestors
		//
		provisionHandlers( requestObjects,  fProvMatrix.getAllQueryResults( true ) );

		//
		// Add publishers
		//
		List<ProvisionedObject<Publisher,PublishingOptions>> publishers = fProvMatrix.getAllPublishers( true );
		provisionPublisher( provideObjects, publishers );
		provisionPublisher( respondObjects, publishers );



		//
		// Add report publishers
		//
		List<ProvisionedObject<ReportPublisher, ReportPublishingOptions>> reportPublishers = fProvMatrix.getAllReportPublishers() ;
		provisionPublisher( provideObjects, reportPublishers );
		provisionPublisher( respondObjects, reportPublishers );



		//  Send provisioning messages for all topics to which this zone is
		//  currently joined...
		TopicFactory tFactory = fAgent.getTopicFactory();
		for( SIFContext context : tFactory.getAllSupportedContexts() )
		{
			for( Topic topic : fAgent.getTopicFactory().getAllTopics( context ) )
			{
				TopicImpl t = (TopicImpl)topic;
				if( t.fZones.contains( this ) )
				{
					//  This zone is joined to the topic. Send a SIF_Subscribe and/or
					//  SIF_Provide for the topic's data type.
					String objType = t.getObjectType();
		    		if( t.fSub != null && ( t.fSubOpts == null || t.fSubOpts.getSendSIFSubscribe() ) ){
		    			addProvisionedObject( subscribeObjects, objType, context, false );
		    		}

		    		if( t.fQueryResults != null ){
		    			addProvisionedObject( requestObjects, objType, context, t.fQueryResultsOptions.getSupportsExtendedQuery() );
		    		}

		    		if( t.fPub != null && ( t.fPubOpts == null || t.fPubOpts.getSendSIFProvide() ) ){
						addProvisionedObject( provideObjects, objType, context, t.fPubOpts.getSupportsExtendedQuery() );
						addProvisionedObject( respondObjects, objType, context, t.fPubOpts.getSupportsExtendedQuery() );
		    		}
					else if ( t.fReportPub != null &&
								( t.fReportPubOpts == null || t.fReportPubOpts.getSendSIFProvide() ) ){
						addProvisionedObject( provideObjects, objType, context, t.fReportPubOpts.getSupportsExtendedQuery() );
						addProvisionedObject( respondObjects, objType, context, t.fReportPubOpts.getSupportsExtendedQuery() );
					}
				}
			}
		}

		// Get the SIF_ZoneStatus and SIF_AgentACL objects. Compare that matrix
		// with the list of provisioned objects in the agent. If there is a different provider
		// for an object, or an ACL restricts this agent from participating, log the anomaly and
		// remove the object from the list

		// The zone status check only needs to be done if this agent provides any objects
		if( provideObjects.getChildCount() > 0 )
		{
			SIF_ZoneStatus zoneStatus = this.getZoneStatus();
			if( zoneStatus == null ) { // should never be null
				log.warn( "Unable to obtain SIF_ZoneStatus for provisioning." );
			} else	{
				// Remove any provided objects if they are provided by any other agent
				SIF_Providers zoneProviders = zoneStatus.getSIF_Providers();
				if( zoneProviders != null ){
					for( SIF_Provider zoneProvider : zoneProviders.getSIF_Providers() ){
						if( !zoneProvider.getSourceId().equals( getAgent().getId() ) && zoneProvider.getSIF_ObjectList() != null ){
							// determine if any objects being provided match ones this agent wants
							// to provide
							for( SIF_Object zoneProvidedObject : zoneProvider.getSIF_ObjectList().getSIF_Objects() ){
								SIF_Object po = provideObjects.getSIF_Object( zoneProvidedObject.getObjectName() );
								if( po != null ){
									SIF_Contexts publishContexts = po.getSIF_Contexts();
									// Remove any contexts supported by this provider
									SIF_Contexts zoneObjectContexts = zoneProvidedObject.getSIF_Contexts();
									if( zoneObjectContexts == null ){
										publishContexts.removeSIF_Context( SIFContext.DEFAULT.getName() );
										if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
											log.info( "Unable to provide " + po.getObjectName() +
													" in SIF Context " + SIFContext.DEFAULT.getName() +
													" because it is being provided by " + zoneProvider.getSourceId() );
										}
									} else {
										for( SIF_Context context : zoneObjectContexts.getSIF_Contexts() )
										{
											publishContexts.removeSIF_Context( context.getValue() );
											if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
												log.info( "Unable to provide " + po.getObjectName() +
														" in SIF Context " + context.getValue() +
														" because it is being provided by " + zoneProvider.getSourceId() );
											}
										}
									}
									// If there are not remaining contexts left, remove the
									// support for publishing this object
									if( publishContexts.getChildCount() == 0 ){
										provideObjects.removeChild( po );
									}
								}
							}
						}
					}
				}
			}
		}

		// Now check Agent ACL permissions
		if( acl == null ){
			acl = this.getAgentACL();
		}
		if( acl == null ) { // Should never be null
			log.warn( "Unable to obtain SIF_AgentACL for provisioning." );
		} else {
			compareToACL( acl.getSIF_ProvideAccess(), provideObjects, "Publish" );
			compareToACL( acl.getSIF_RespondAccess(), respondObjects, "Respond to" );
			compareToACL( acl.getSIF_SubscribeAccess(), subscribeObjects, "Subscribe to" );
			compareToACL( acl.getSIF_RequestAccess(), requestObjects, "Request" );
		}

		SIF_Ack ack = fPrimitives.sifProvision(
				this,  provideObjects,
				 subscribeObjects,
				 new SIF_PublishAddObjects(),
				 new SIF_PublishChangeObjects(),
				 new SIF_PublishDeleteObjects(),
				 requestObjects,
				 respondObjects  );
		if( ack.hasError() ){
				SIFException se = new SIFException( ack, this );
				ADKUtils._throw( se, null );
		} else {
			if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ) {
				log.info( "SIF_Provision successful" );
			}
		}

	}


	/**
	 * Checks to see if the list of Published objects already contains the instance. If so, a check
	 * is done to ensure that the specified context is included.
	 * @param provObjects
	 * @param provObject
	 */
	private void addProvisionedObject(
			SIFElement provObjects,
			String objectName,
			SIFContext context,
			boolean supportsSIFExtendedQuery )
	{
		SIF_Object lookedUp = (SIF_Object)provObjects.getChild( InfraDTD.SIF_OBJECT.name(), objectName );
		if( lookedUp == null ){
			lookedUp = new SIF_Object( objectName );
			lookedUp.setSIF_Contexts( new SIF_Contexts() );
			provObjects.addChild( lookedUp );
		}
		if( supportsSIFExtendedQuery ){
			lookedUp.setSIF_ExtendedQuerySupport( true );
		}
		SIF_Contexts contexts = lookedUp.getSIF_Contexts();
		SIF_Context ctxt = contexts.getSIF_Context( context.getName() );
		if( ctxt == null ){
			contexts.addSIF_Context( context.getName() );
		}
	}

	/**
	 * Looks at all of the object actions currently desired to be provisioned by the agent. If the
	 * corresponding ACL is not found in the ACL list, a warning is written to the log and the
	 * corresponding provisioning option is removed from the desiredProvisionedObjects list
	 * @param aclPermissions A SIFElement instance from SIF_AgentACL
	 * @param desiredProvisionedObjects a ProvisionedObjects or PublishedObjects instance for
	 * the corresponding provisioning action
	 */
	private void compareToACL( SIFElement aclPermissions, SIFElement desiredProvisionedObjects, String actionString )
	{
		SIFElement[] allProvisionedObjects = desiredProvisionedObjects.getChildren();
		for( SIFElement provisionedObject : allProvisionedObjects )
		{
			// TODO: It would be nice to be able to better support inheritance. This method is called
			// with both ProvisionedObjects and PublishedObjects as its second parameter. They are similar,
			// but PublishedObjects has an additional element child

			String objectName = provisionedObject.getFieldValue( InfraDTD.SIF_OBJECT_OBJECTNAME );
			SIFElement aclObject = null;
			if( aclPermissions != null ){
				aclObject = aclPermissions.getChild( InfraDTD.SIF_OBJECT, objectName );
			}

			SIF_Contexts aclContexts = null;
			if( aclObject != null ){
				aclContexts = (SIF_Contexts)aclObject.getChild( InfraDTD.SIF_CONTEXTS.name() );
			}

			if( aclContexts == null )
			{
				desiredProvisionedObjects.removeChild( provisionedObject );
				if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
					log.info( "Unable to " + actionString + " " + objectName +
					" because the agent does not have ACL permission to do so." );
				}
			}
			else
			{
				SIF_Contexts provContexts = (SIF_Contexts)provisionedObject.getChild( InfraDTD.SIF_CONTEXTS.name());
				for( SIF_Context desiredContext : provContexts.getSIF_Contexts() ){
					SIF_Context aclContext = aclContexts.getSIF_Context( desiredContext.getValue() );
					if( aclContext == null ){
						provContexts.removeChild( desiredContext );
						if( ( ADK.debug & ADK.DBG_PROVISIONING ) != 0 ){
							log.info( "Unable to " + actionString + " " + objectName +
									" in Context \"" + desiredContext.getValue() +
									"\" because the agent does not have ACL permission to do so." );
						}
					}
				}

				if( provContexts.getChildCount() == 0 ){
					desiredProvisionedObjects.removeChild( provisionedObject );
				}
			}
		}

	}

	/**
	 * Takes a SIFElement such as SIF_SubscribeObjects or SIF_RequestObjects and adds an additional
	 * child nodes describing support for each of the objects in the specified provisioning list
	 * @param addTo A SIF_SubscribeObjects or SIF_RequestObjects element
	 * @param list A list of objects and associated publishing options related to them
	 */
	@SuppressWarnings("rawtypes")
	private <V extends Object, Y extends ProvisioningOptions> // NOTE: the V parameter is unused, but Java is not allowing it to be wildcarded. Strange
		void provisionHandlers( SIFElement addTo, List<ProvisionedObject<V,Y>> list)
	{
		for( ProvisionedObject subOption : list ){
			ProvisioningOptions opts = subOption.getProvisioningOptions();
			SIF_Object qr = new SIF_Object( subOption.getObjectType().name() );
			SIF_Contexts qContexts = new SIF_Contexts();
			qr.setSIF_Contexts( qContexts );

			if( opts != null ){
				// Add the list of supported contexts for this object
				for( SIFContext context : opts.getSupportedContexts() ){
					qContexts.addSIF_Context( context.getName() );
				}
			}
			addTo.addChild( qr );
		}
	}

	/**
	 * Takes a SIFElement such as SIF_ProvideObjects or SIF_RespondObjects and adds an additional
	 * child node describing support for each of the objects in the specified provisioning list
	 * @param addTo A SIF_ProvideObjects or SIF_RespondObjects element
	 * @param list A list of objects and associated publishing options related to them
	 */
	private <V extends Object, Y extends PublishingOptions> // NOTE: the V parameter is unused, but Java is not allowing it to be wildcarded. Strange
	 void provisionPublisher( SIFElement addTo, List<ProvisionedObject<V,Y>> list)
	{
		for( ProvisionedObject<V,Y> pubOption : list ){
			PublishingOptions pubOptions = pubOption.getProvisioningOptions();
			SIF_Object published = new SIF_Object( pubOption.getObjectType().name() );
			SIF_Contexts pContexts = new SIF_Contexts();
			published.addChild( pContexts );
			if( pubOptions != null ){
				published.setSIF_ExtendedQuerySupport( pubOptions.getSupportsExtendedQuery() );

				// Add the list of supported contexts for this object
				for( SIFContext context : pubOptions.getSupportedContexts() ){
					pContexts.addSIF_Context( context.getName() );
				}
			}
			addTo.addChild( published );
		}
	}


	protected String arrayToStr( String[] arr )
	{
		StringBuilder b = new StringBuilder();
		for( int i = 0; i < arr.length; i++ ) {
			if( i != 0 )
				b.append( ", " );
			b.append( arr[i] );
		}

		return b.toString();
	}

	/**
	 *  Gets a read-only list of any SIF Errors that resulted from sending SIF_Provide and
	 *  SIF_Subscribe provisioning messages to the zone. Currently, only access
	 *  control errors (Category 4) are treated as warnings rather than errors.
	 *  All other SIF Errors result in an exception thrown by the
	 *  <code>connect</code> method.<p>
	 *
	 *  @return An array of SIFExceptions
	 */
	public List<SIFException> getConnectWarnings()
	{
		synchronized( fConnLock )
		{
			return Collections.unmodifiableList( fProvWarnings );
		}
	}

	/**
	 *  Disconnects the agent from this zone
	 */
	public void disconnect( int provOptions )
		throws ADKException
	{
synchronized( fConnLock )
{
		if( !isConnected() )
			return;

		SIFException exc = null;

		try
		{
			if( fQueue != null ) {
				fQueue.shutdown();
				fQueue = null;
			}

			if( ( provOptions & ADKFlags.PROV_UNREGISTER ) != 0 &&
					getProperties().getProvisioningMode() == AgentProvisioningMode.ADK )
			{
				fPrimitives.sifUnregister(this);
			}
			else
			{
				if( getProperties().getSleepOnDisconnect() ) {
					try	{
						sleep();
					}
					catch( SIFException se ){
						//	Ignore Category 4, Code 9 error (invalid SourceId, which
						//	with SIFWorks would be the case if the admin manually
						//	unregistered the agent but the agent still thinks it is
						//	connected.
						//
						if( !( se.getSIFErrorCategory() == SIFErrorCategory.ACCESS_PERMISSIONS &&
							se.getErrorCode() == SIFErrorCodes.ACCESS_UNKNOWN_SOURCEID_9 ) )
						{
							exc = se;
						}
					}
				}
			}

		    //  Shutdown the message dispatcher and transport protocol
			shutdown();
		}
		finally
		{
			fState |= CLOSED;
			fState &= ~CONNECTED;
		}

		if( exc != null ){
			throw exc;
		}
}
	}

	/**
	 *  Gets the connection state of this Zone<p>
	 *  @return true if the connect method has been called but the disconnect
	 *      method has not; false if the connect method has not yet been called
	 *      or the disconnect method has been called
	 */
	public boolean isConnected()
	{
		return( fState & CONNECTED ) == CONNECTED;
	}

	/**
	 *  Determines if this zone is shutting down<p>
	 *  @return true if the shutdown method has been called
	 */
	public boolean isShutdown()
	{
		return( fState & SHUTDOWN ) == SHUTDOWN;
	}

	/**
	 *  Puts this zone into sleep mode.<p>
	 *
	 *  A SIF_Sleep message is sent to the Zone Integration Server to request
	 *  that this agent's queue be put into sleep mode. If successful, the ZIS
	 *  should not deliver further messages to this agent until it is receives
	 *  a SIF_Register or SIF_Wakeup message from the agent. Note the ADK keeps
	 *  an internal sleep flag for each zone, which is initialized when the
	 *  <code>connect</code> method is called by sending a SIF_Ping to the ZIS.
	 *  This flag is set so that the ADK will return a Status Code 8 ("Receiver
	 *  is sleeping") in response to any message received by the ZIS for the
	 *  duration of the session.
	 *  <p>
	 *
	 *  If the SIF_Sleep message is not successful, an exception is thrown and
	 *  the ADK's internal sleep flag for this zone is not changed.
	 *  <p>
	 *
	 *  @exception ADKException thrown if the SIF_Sleep message is unsuccessful
	 */
	public void sleep()
		throws ADKException
	{
		_checkConnect();

		fState |= SLEEPING;

		SIF_Ack ack = sifSleep();
		if( ack.hasError() ){
			ADKUtils._throw( new SIFException(ack,this), log );
		}
	}

	/**
	 *  Execute a SIF_Sleep received from the ZIS
	 */
	public void execSleep()
	{
		//  TODO: Notify the zone's ResponseDelivery thread to pause.

		fState |= SLEEPING;
	}

	/**
	 *  Wakes up this zone if currently in sleep mode.<p>
	 */
	public void wakeup()
		throws ADKException
	{
		_checkConnect();

		fState &= ~SLEEPING;

		SIF_Ack ack = sifWakeup();
		if( ack.hasError() )
			ADKUtils._throw( new SIFException( ack, this ), log );

	}

	/**
	 *  Execute a SIF_Wakeup received from the ZIS
	 */
	public void execWakeup()
	{
		//  TODO: Notify the zone's ResponseDelivery thread to continue
		//  sending any SIF_Responses that are pending delivery but were
		//  interrupted due to a SIF_Sleep ( see execSleep() )

		fState &= ~SLEEPING;
	}

	/**
	 *  Determines if the agent's queue for this zone is in sleep mode.<p>
	 *
	 *  @param flags When ADKFlags.LOCAL_QUEUE is specified, returns true if the
	 *      Agent Local Queue is currently in sleep mode. False is returned if
	 *      the Agent Local Queue is disabled. When ADKFlags.SERVER_QUEUE is
	 *      specified, queries the sleep mode of the Zone Integration Server
	 *      by sending a SIF_Ping message.
	 */

	public boolean isSleeping( int flags )
		throws ADKException
	{
		_checkConnect();

		if( ( flags & ADKFlags.QUEUE_SERVER ) != 0 )
		{
			//  Determine if agent's queue on server is sleeping
			SIF_Ack ack = sifPing();
			if( ack.hasError() )
				ADKUtils._throw( new SIFException( ack, this ), log );
    		return( ack.hasStatusCode( SIFStatusCodes.SLEEPING_8 ) );
		}
		else
		if( ( flags & ADKFlags.QUEUE_LOCAL ) != 0 )
		{
			//  TODO: Move state into ALQ object when it exists
			return( ( fState & SLEEPING ) != 0 );
		}
		else
			throw new IllegalArgumentException("Invalid flags (specify SERVER_QUEUE or LOCAL_QUEUE)");
	}

	/**
	 *  Determines if this zone is awaiting a SIF_ZoneStatus result
	 */
	protected boolean awaitingZoneStatus() {
		return( fState & GETZONESTATUS ) != 0;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#reportEvent(com.edustructures.sifworks.SIFDataObject, com.edustructures.sifworks.EventAction)
	 */
	public void reportEvent( SIFDataObject obj, EventAction actionCode )
		throws ADKException
	{
		reportEvent( new Event( obj, actionCode ), null, null );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#reportEvent(com.edustructures.sifworks.SIFDataObject, com.edustructures.sifworks.EventAction, com.edustructures.sifworks.SIFContext[])
	 */
	public void reportEvent( SIFDataObject obj, EventAction actionCode, SIFContext... contexts )
	throws ADKException
	{
		reportEvent( new Event( obj, actionCode, contexts ), null, null );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#reportEvent(com.edustructures.sifworks.SIFDataObject, com.edustructures.sifworks.EventAction, java.lang.String)
	 */
	public void reportEvent( SIFDataObject obj, EventAction actionCode, String destinationId )
		throws ADKException
	{
		reportEvent( new Event( obj, actionCode ), destinationId, null );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#reportEvent(com.edustructures.sifworks.Event)
	 */
	public void reportEvent( Event event )
		throws ADKException
	{
		reportEvent( event, null, null );
	}

	/**
	 * Reports an event with a specific destination ID specified
	 * @param event The event to report
	 * @param destinationId the destination id for this event
	 * @throws ADKException
	 */
	public void reportEvent( Event event, String destinationId )
		throws ADKException
	{
		reportEvent( event, destinationId, null );
	}

	/**
	 * Reports an event with a specific destination ID and SIF message Id specified
	 * @param event The event to report
	 * @param destinationId the destination id for this event
	 * @param sifMsgId The SIF message ID to use
	 * @throws ADKException
	 */
	public void reportEvent( Event event, String destinationId, String sifMsgId )
		throws ADKException
	{
		_checkConnect();

		SIF_Ack ack = fPrimitives.sifEvent( this, event, destinationId, sifMsgId );
		if( ack.hasError() )
			ADKUtils._throw( new SIFException(ack,this),log );
	}

	/**
	 * 	Report an informative message to the zone in the form of a SIF_LogEntry object.<p>
	 *
	 * 	@param desc A textual description
	 * @throws ADKException
	 */
	public void reportInfoLogEntry( String desc )
		throws ADKException
	{
		getServerLog().log( desc );
	}

	/**
	 * 	Report an informative message to the zone in the form of a SIF_LogEntry object.<p>
	 *
	 * 	@param desc A textual description
	 * 	@param extDesc An optional extended description, or null if not applicable
	 * 	@param appCode An optional application-defined code
	 * 	@param objects An optional array of one or more SIFDataObjects to be
	 * 		included with the SIF_LogEntry, or null if not applicable
	 * @throws ADKException
	 */
	public void reportInfoLogEntry(
			String desc,
			String extDesc,
			String appCode,
			SIFDataObject... objects )
		throws ADKException
	{
		getServerLog().log( LogLevel.INFO, desc, extDesc, appCode, null, objects );
	}

	/**
	 * 	Report a warning message to the zone in the form of a SIF_LogEntry object.<p>
	 *
	 * 	@param desc A textual description
	 * 	@param extDesc An optional extended description, or null if not applicable
	 * 	@param category The SIF_LogEntry category
	 * 	@param code The SIF_LogEntry code
	 * 	@param appCode An optional application-defined code
	 * 	@param objects An optional array or sequence of one or more SIFDataObjects to be
	 * 		included with the SIF_LogEntry, or null if not applicable
	 * @throws ADKException
	 */
	public void reportWarningLogEntry(
			String desc,
			String extDesc,
			int category,
			int code,
			String appCode,
			SIFDataObject... objects )
		throws ADKException
	{
		getServerLog().log( LogLevel.WARNING, desc, extDesc, appCode, category, code, null, objects );
	}

	/**
	 * 	Report a warning message to the zone in the form of a SIF_LogEntry object.
	 * 	Use this form of the <code>reportWarningLogEntry</code> method if the warning
	 * 	references a SIF_Message received by the agent.<p>
	 *
	 * 	@param msgInfo A SIFMessageInfo instance describing the SIF_Message that
	 * 		to which this warning relates. A SIFMessageInfo value is passed to all
	 * 		message handler functions by the ADK.
	 * 	@param desc A textual description
	 * 	@param extDesc An optional extended description, or null if not applicable
	 * 	@param category The SIF_LogEntry category
	 * 	@param code The SIF_LogEntry code
	 * 	@param appCode An optional application-defined code
	 * 	@param objects An optional array or sequence of one or more SIFDataObjects to be
	 * 		included with the SIF_LogEntry, or null if not applicable
	 * @throws ADKException
	 */
	public void reportWarningLogEntry(
		SIFMessageInfo msgInfo,
		String desc,
		String extDesc,
		int category,
		int code,
		String appCode,
		SIFDataObject... objects )
			throws ADKException
	{
		getServerLog().log( LogLevel.WARNING, desc, extDesc, appCode, category, code, msgInfo, objects );
	}

	/**
	 * 	Report an error message to the zone in the form of a SIF_LogEntry object.<p>
	 *
	 * 	@param desc A textual description
	 * 	@param extDesc An optional extended description, or null if not applicable
	 * 	@param category The SIF_LogEntry category
	 * 	@param code The SIF_LogEntry code
	 * 	@param appCode An optional application-defined code
	 * 	@param objects An optional array or sequence (varargs) of one or more SIFDataObjects to be
	 * 		included with the SIF_LogEntry, or null if not applicable
	 * @throws ADKException
	 */
	public void reportErrorLogEntry(
			String desc,
			String extDesc,
			int category,
			int code,
			String appCode,
			SIFDataObject... objects )
		throws ADKException
	{
		getServerLog().log( LogLevel.ERROR, desc, extDesc, appCode, category, code, null, objects );
	}

	/**
	 * 	Report an error message to the zone in the form of a SIF_LogEntry object.
	 * 	Use this form of the <code>reportErrorLogEntry</code> method if the error
	 * 	references a SIF_Message received by the agent.<p>
	 *
	 * 	@param msgInfo A SIFMessageInfo instance describing the SIF_Message that
	 * 		to which this error relates. A SIFMessageInfo value is passed to all
	 * 		message handler functions by the ADK.
	 * 	@param desc A textual description
	 * 	@param extDesc An optional extended description, or null if not applicable
	 * 	@param category The SIF_LogEntry category
	 * 	@param code The SIF_LogEntry code
	 * 	@param appCode An optional application-defined code
	 * 	@param objects An optional array or sequence (varargs) of one or more
	 * 		SIFDataObjects to be included with the SIF_LogEntry, or null if not applicable
	 * @throws ADKException
	 */
	public void reportErrorLogEntry(
		SIFMessageInfo msgInfo,
		String desc,
		String extDesc,
		int category,
		int code,
		String appCode,
		SIFDataObject... objects )
			throws ADKException
	{
		getServerLog().log( LogLevel.ERROR, desc, extDesc, appCode, category, code, msgInfo, objects );
	}



	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setPublisher(com.edustructures.sifworks.Publisher)
	 */
	public void setPublisher( Publisher publisher )
		throws ADKException
	{
		fProvMatrix.setPublisher( publisher );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setPublisher(com.edustructures.sifworks.Publisher, com.edustructures.sifworks.ElementDef)
	 */
	public void setPublisher(Publisher publisher, ElementDef objectType) throws ADKException {
		fProvMatrix.setPublisher(publisher, objectType );
		provision();
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setPublisher(com.edustructures.sifworks.Publisher, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.PublishingOptions)
	 */
	public void setPublisher( Publisher publisher, ElementDef objectType, PublishingOptions options )
		throws ADKException
	{
		fProvMatrix.setPublisher( publisher, objectType, options );
		provision();
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setReportPublisher(com.edustructures.sifworks.ReportPublisher)
	 */
	public void setReportPublisher(ReportPublisher publisher) throws ADKException {
		fProvMatrix.setReportPublisher( publisher );
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setReportPublisher(com.edustructures.sifworks.ReportPublisher, com.edustructures.sifworks.ReportPublishingOptions)
	 */
	public void setReportPublisher( ReportPublisher publisher, ReportPublishingOptions options )
		throws ADKException
	{
		fProvMatrix.setReportPublisher( publisher, options );
		provision();
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setSubscriber(com.edustructures.sifworks.Subscriber, com.edustructures.sifworks.ElementDef)
	 */
	public void setSubscriber(Subscriber subscriber, ElementDef objectType) throws ADKException {
		fProvMatrix.setSubscriber(subscriber, objectType );
		provision();

	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setSubscriber(com.edustructures.sifworks.Subscriber, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.SubscriptionOptions)
	 */
	public void setSubscriber( Subscriber subscriber, ElementDef objectType, SubscriptionOptions options )
		throws ADKException
	{
		fProvMatrix.setSubscriber( subscriber, objectType, options );
		provision();
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults)
	 */
	public void setQueryResults( QueryResults queryResults )
		throws ADKException
	{
		fProvMatrix.setQueryResults(queryResults );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults, com.edustructures.sifworks.ElementDef)
	 */
	public void setQueryResults(QueryResults queryResults, ElementDef objectType) throws ADKException {
		fProvMatrix.setQueryResults( queryResults, objectType );

	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Provisioner#setQueryResults(com.edustructures.sifworks.QueryResults, com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.QueryResultsOptions)
	 */
	public void setQueryResults( QueryResults queryResults, ElementDef objectType, QueryResultsOptions flags )
		throws ADKException
	{
		fProvMatrix.setQueryResults( queryResults, objectType, flags );
	}

	/**
	 *  Gets the Publisher for a SIF object type
	 *  @param context The SIFContext to obtain the publisher for
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 *  @return The Publisher registered for this object type by the agent when
	 *      it called the setPublisher method, or null if no Publisher has been
	 *      registered
	 */
	protected Publisher getPublisher( SIFContext context, ElementDef objectType )
	{
		if( objectType == ReportingDTD.SIF_REPORTOBJECT )
			throw new IllegalArgumentException( "You must call getReportPublisher to obtain the Publisher message handler for SIF_ReportObject");

		return fProvMatrix.lookupPublisher( context, objectType );
	}
	

	/**
	 *  Gets the ReportPublisher for the SIF_ReportObject object type.<p>
	 *  @return The ReportPublisher registered by the agent when it called the
	 * 		setReportPublisher method, or null if no ReportPublisher has been
	 *      registered
	 */
	protected ReportPublisher getReportPublisher( SIFContext context )
	{
		return fProvMatrix.lookupReportPublisher( context );
	}
	
	protected ReportPublisher getServicePublisher( SIFContext context )
	{
		return fProvMatrix.lookupServicePublisher( context );
	}

	/**
	 *  Gets the Subscriber for a SIF object type
	 *  @param context The SIF Context to look for the subscriber in
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 *  @return The Subscriber registered for this object type by the agent when
	 *      it called the setSubscriber method, or null if no Subscriber has
	 *      been registered
	 */
	protected Subscriber getSubscriber(SIFContext context, ElementDef objectType )
	{
		return fProvMatrix.lookupSubscriber( context, objectType );
	}

	/**
	 *  Gets the QueryResults object for a SIF object type
	 *  @param context The SIF Context to use for looking up the handler
	 *  @param objectType A SIFDTD constant identifying a SIF Data Object type
	 *      (e.g. <code>SIFDTD.STUDENTPERSONAL</code>)
	 *  @return The QueryResults object registered for this object type by the
	 *      agent when it called the setQueryResults method, or null if no
	 *      QueryResults object has been registered
	 */
	protected QueryResults getQueryResults(SIFContext context, ElementDef objectType )
	{
		return fProvMatrix.lookupQueryResults( context, objectType );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query)
	 */
	public String query( Query query )
		throws ADKException
	{
		return query(query,null,null,0,null);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query, com.edustructures.sifworks.MessagingListener)
	 */
	public String query( Query query, MessagingListener listener )
		throws ADKException
	{
		return query(query,listener,null,0,null);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query, int)
	 */
	public String query( Query query, int queryOptions )
		throws ADKException
	{
		return query(query,null,null,queryOptions,null);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query, com.edustructures.sifworks.MessagingListener, int)
	 */
	public String query( Query query, MessagingListener listener, int queryOptions )
		throws ADKException
	{
		return query(query,listener,null,queryOptions,null);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query, java.lang.String, int)
	 */
	public String query( Query query, String destinationId, int queryOptions )
		throws ADKException
	{
		return query(query,null,destinationId,queryOptions,null);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#query(com.edustructures.sifworks.Query, com.edustructures.sifworks.MessagingListener, java.lang.String, int)
	 */
	public String query( Query query, MessagingListener listener, String destinationId, int queryOptions )
		throws ADKException
	{
		return query( query, listener, destinationId, queryOptions, null );
	}

	/**
	 * 	Undocumented version of the <code>query</code> method that allows the caller to
	 * 	specify the SIF_MsgId that will be used in the SIF_Message/SIF_Header.<p>
	 *
	 *  This method is not part of the public Zone interface. It is intended to be used
	 * 	by agents that must decide upon the SIF_MsgId of a query before the ADK is called,
	 * 	or that want to choose the SIF_MsgId that will be used for a query for debugging
	 * 	purposes or to satisfy some other special requirement. (SIFWorks Student Locator
	 * 	agent is an example of an agent that requires this method.)
	 *
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners
	 * 		registered with the zone will also be called.
	 *  @param destinationId The SourceId of the agent to which the SIF Request
	 * 		will be routed by the zone integration server
	 *  @param queryOptions Reserved for future use
	 * 	@param sifMsgId The value to assign to the SIF_Message/SIF_Header/SIF_MsgId
	 * 		element, or <code>null</code> to let the framework assign its own value
	 *
	 * @return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 * @throws ADKException If the query cannot be sent
	 *
	 * 	@since ADK 1.5
	 */
	public String query( Query query, MessagingListener listener, String destinationId, int queryOptions, String sifMsgId )
		throws ADKException
	{
		//	Synchronized to prevent MessageDispatcher from processing a SIF_Response (on the
		//	zone thread) while this thread is issuing a SIF_Request. This is required to ensure
		//	that the query() method completes before any SIF_Response processing is performed.
		//	It can lead to situations where the RequestCache is not updated in time, and/or
		//	the QueryResults.onQueryResults method is called before the onQueryPending method
		//	is called to notify the agent that a request was issued.
		//
		synchronized( fReqLock )
		{
			_checkConnect();

			SIF_Request req = null;

			try
			{
				SIF_Ack ack = fPrimitives.sifRequest(this,query,destinationId,sifMsgId);
				if( ack.hasError() )
					ADKUtils._throw( new SIFException(ack,this),log );

				req = (SIF_Request)ack.message;

				//  Record the request in the agent's RequestCache
				RequestInfo ri = getFDispatcher().fRequestCache.storeRequestInfo( req, query, this );

				//  Call the onQueryResults.onQueryPending method...
				QueryResults target = getFDispatcher().getQueryResultsTarget(null,req,null,query,this);
				if( target != null )
				{
					SIFMessageInfo inf = new SIFMessageInfo(ack.message,this);
					inf.setSIFRequestMsgId( inf.getMsgId() );
					inf.setSIFRequestVersion( query.getSIFVersions() );
					inf.setSIFRequestObjectType( query.getObjectType() );
					inf.setSIFRequestInfo( ri );

					try
					{
//						if( BuildOptions.PROFILED )
//							ProfilerUtils.profileStart( String.valueOf( openadk.profiler.api.OIDs.ADK_SIFREQUEST_REQUESTOR_MESSAGING ), query.getObjectType(), inf.getMsgId() );

						target.onQueryPending( inf,this );
					}
					finally
					{
						if( BuildOptions.PROFILED )
							ProfilerUtils.profileStop();
					}
				}
			}
			catch( ADKException adke )
			{
				throw adke;
			}

			//	New to 1.5: Return the SIF_Request/SIF_MsgId to the caller
			return req.getHeader().getSIF_MsgId();
		}
	}

	/**
	 * 	Blocks the calling thread until Zone.onQuery has completed.
	 *	@see #query(Query)
	 */
	protected void waitForRequestsToComplete()
	{
		synchronized( fReqLock )
		{
			//	do nothing - synchronization on fReqLock hidden within this method
			//	so we can shield the caller from knowledge of our protected data
			//	members and have the room to expand on this functionality later on.
		}
	}

	private SIF_ZoneStatus cacheSIF_ZoneStatus(SIF_ZoneStatus potentialZoneStatus) {
		if (potentialZoneStatus != null) {
			lastReceivedZoneStatus = potentialZoneStatus;
		}
		return potentialZoneStatus;
	}
	
	/**
	 *  Gets the SIF_ZoneStatus object from the ZIS managing this zone. The
	 *  method blocks until a result is received.
	 */
	public SIF_ZoneStatus getZoneStatus()
		throws ADKException
	{
		return getZoneStatus( getProperties().getDefaultTimeout() );
	}

	/**
	 *  Gets the SIF_ZoneStatus object from the ZIS managing this zone. The
	 *  method blocks for the specified timeout period.<p>
	 *
	 *  <b>Note</b> getZoneStatus cannot be called from the Subscriber.onEvent
	 *  method when the agent is operating in Pull mode or a deadlock situation
	 *  may arise. If you must obtain a SIF_ZoneStatus while processing a
	 *  SIF Event, use the TrackQueryResults object to first issue a query for
	 *  the object, then call this method to wait for the reply to be
	 *  received. TrackQueryObjects is the only safe way to perform queries
	 *  while processing SIF Events because it can invoke Selective Message
	 *  Blocking if necessary.<p>
	 *
	 *  @param timeout The amount of time to wait for a SIF_ZoneStatus object to
	 *      be received by the agent (or 0 to wait infinitely)
	 *  @returns The SIF_ZoneStatus object, or null if no response was received
	 *      within the specified timeout period
	 *
	 *  @exception ADKException is thrown if an error occurs
	 */
	public SIF_ZoneStatus getZoneStatus( int timeout )
		throws ADKException
	{
		_checkConnect();

		synchronized( fZSLock )
		{
			try
			{
				fState |= GETZONESTATUS;

				if( getProperties().getUseZoneStatusSystemControl() )//&& ADK.getSIFVersion().compareTo( SIFVersion.SIF15 ) >= 0 )
				{
					//
					//	SIF 1.5 EXPERIMENTAL / SIF 2.0 Official - Use a SIF_SystemControl message to
					//	immediately retrieve the SIF_ZoneStatus
					//
					SIF_Ack ack = fPrimitives.sifZoneStatus(this);
					if( ack.hasError() )
						throw new SIFException( ack, this );

					//	The SIF_ZoneStatus object will be in the SIF_Ack/SIF_Data
					//	The SIF_AgentACL object will be in the SIF_Ack/SIF_Data
				    SIF_Status status = ack.getSIF_Status();
			        if (status != null)
			        {
			            SIF_Data data = status.getSIF_Data();
			            if (data != null)
			            {
			                List<SIFElement> childList = data.getChildList( InfraDTD.SIF_ZONESTATUS );
			                if (childList != null && childList.size() > 0)
			                {
			                    return cacheSIF_ZoneStatus((SIF_ZoneStatus)childList.get(0));
			                }
			            }
			        }
				    return null;
				}
				else
				{
					fZoneStatus = null;
					int pullState = 0;

					SIF_Ack ack = fPrimitives.sifRequest(this,new Query( InfraDTD.SIF_ZONESTATUS ),null,null);
					if( ack.hasError() )
						throw new SIFException(ack,this);

					//  ** When in pull mode: force a pull immediately regardless of
					//     the usual pull frequency (e.g. if it is 22 seconds until the
					//     next pull we don't want to sit and wait here for the
					//     SIF_ZoneStatus to be returned in 22 seconds.) **
					boolean isPull = getProperties().getMessagingMode() == AgentMessagingMode.PULL;
					if( isPull ) {
						do {
							pullState = getFDispatcher().pull();
						} while (fZoneStatus == null && pullState == 1);
					}

					//  Wait for MessageDispatcher to hand us the response
					if( fZoneStatus == null )
					{
						try
						{
							if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
								log.debug("Waiting "+timeout+"ms for SIF_ZoneStatus reply...");

							fZSLock.wait(timeout);

							if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
								log.debug("Received SIF_ZoneStatus reply...");
						}
						catch( InterruptedException ie )
						{
							fZoneStatus = null;
						}
					}
				}
			}
			finally
			{
				fState &= ~GETZONESTATUS;
			}
		}

		return cacheSIF_ZoneStatus(fZoneStatus);
	}

	/**
	 *  Called by MessageDispatcher to inform this Zone of a SIF_Response
	 *  received for the SIF_ZoneStatus object type.
	 */
	protected void setZoneStatus( SIF_ZoneStatus stat )
	{
		synchronized( fZSLock )
		{
			fZoneStatus = stat;
	    	fZSLock.notifyAll();
		}
	}

	/**
	 * Gets the latest version of SIF that should be used for messaging in this zone.
	 *
	 * <p>The implementation checks the AgentProperties-ZisVersion property and the
	 * ADK-SIFVersion property. The lowest version between the two properties is returned.</p>
	 *
	 * <p>For example, if the ADK-SIFVersion property is set to 1.5r1, SIFVersion.15r1 will
	 * be returned. If, however, the ADK-SIFVersion property is set to 2.1, but the
	 * AgentProperties-ZisVersion property is set to "1.1", SIFVersion.11 will be returned.</p>
	 *
	 * This behavior allows the ADK to be configured in one of two ways to support older versions
	 * of SIF
	 * <ol>
	 * <li> Support an older version across the board. If this is the desired behavior, setting
	 * the ADK-SIFVersion property is the proper way to achieve this.</li>
	 *
	 * <li> Run using the latest version of SIF, but connect to specific zones using an older
	 * version of SIF. In this case, setting the AgentProperties-ZisVersion is the proper way
	 * to achieve this.</li>
	 * </ol>
	 *
	 *  <p><b>NOTE:</b> This property is primarily for internal use only and is meant for use with
	 *  messages that are meant to be sent directly to the ZIS, which include the provisioning
	 *  messages (SIF_Register, SIF_Subscribe, SIF_Provide, etc.), and the system messages
	 *  (SIF_SystemControl).</p>
	 *
	 *  <p>To override behavior of messages that are sent to another agent (SIF_Event, SIF_Request),
	 *  the ADK supports a more granular approach for dealing with versions down to the object
	 *  level. To accomplish this, use ADK object versioning policy</p>
	 *
	 *  @see PolicyFactory
	 *  @see ADKDefaultPolicy
	 *
	 * @return The latest SIFVersion that should be used for messaging in this zone.
	 */
	public SIFVersion getHighestEffectiveZISVersion() {

		SIFVersion zisVersion = SIFVersion.LATEST;
		try
		{
			zisVersion = SIFVersion.parse( fProps.getZisVersion() );
		} catch ( IllegalArgumentException iae ){
			// Unable to parse the ZIS Version from props
			getLog().warn(
					"Unable to parse adk.provisioning.zisVersion value:'" +
					fProps.getZisVersion() +
					"' into a SIFVersion instance. Using " +
					SIFVersion.LATEST.toString(), iae );
		}
		SIFVersion adkVersion = ADK.getSIFVersion();

		if( zisVersion.compareTo( adkVersion ) < 0 ){
			return zisVersion;
		} else {
			return adkVersion;
		}
	}



	/**
	 * Gets the Agent ACL list from the zone. This method invokes the zone
	 * synchronously before returning.
	 * @return the SIF_AgentACL object returned from the ZIS
	 * @throws ADKException
	 */
	public SIF_AgentACL getAgentACL()
		throws ADKException
	{
		_checkConnect();
		SIF_Ack ack = fPrimitives.sifGetAgentACL(this);
		if( ack.hasError() ){
			throw new SIFException( ack, this );
		}

		//	The SIF_AgentACL object will be in the SIF_Ack/SIF_Data
	    SIF_Status status = ack.getSIF_Status();
        if (status != null)
        {
            SIF_Data data = status.getSIF_Data();
            if (data != null)
            {
            	List<SIFElement> childList = data.getChildList( InfraDTD.SIF_AGENTACL );
                if (childList != null && childList.size() > 0)
                {
                    return (SIF_AgentACL)childList.get(0);
                }
            }
        }
	    return null;
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

	public void setErrorHandler( UndeliverableMessageHandler handler )
	{
		fErrHandler = handler;
	}

	public UndeliverableMessageHandler getErrorHandler()
	{
		if( fErrHandler != null )
			return fErrHandler;

		return fAgent.getErrorHandler();
	}

	public SIF_Ack sifSend( String xml )
		throws ADKException
	{
		_checkConnect();

		SIF_Ack ack = null;

		try
		{
			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 ) {
				log.debug( "Sending user message ("+xml.length()+" bytes): " );
				log.debug( xml );
			}

			String ackStr = fProtocolHandler.send( xml );

			//  Parse the results into a SIF_Ack
			SIFParser parser = SIFParser.newInstance();
			ack = (SIF_Ack)parser.parse(ackStr,this,0);
			if( ack != null ) {
				ack.message = null;
				if( ( ADK.debug & ADK.DBG_MESSAGING_PULL ) != 0 ) {
					ack.LogRecv(log);
				}
			}
		}
		catch( Exception e )
		{
			throw new ADKException( e.toString(), this );
		}

		return ack;
	}
	

	public SIF_Ack sifCancelRequests(String destinationId, String[] sif_MsgIds) throws ADKException {
		_checkConnect();

		SIF_Ack ack = null;

		try
		{
			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 ) {
				log.debug( "Sending Cancel Requests message : " );
				log.debug( sif_MsgIds.toString() );
			}
			_checkConnect();

			ack = fPrimitives.sifCancelRequests(this,destinationId,sif_MsgIds);
		}
		catch( Exception e )
		{
			throw new ADKException( e.toString(), this );
		}

		return ack;
	}


	/**
	 *  Sends a SIF_Register message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.<p>
	 */
	public SIF_Ack sifRegister()
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() == AgentProvisioningMode.AGENT ){
			return fPrimitives.sifRegister(this);
		}

		return null;
	}

	/**
	 *  Sends a SIF_Unregister message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.<p>
	 */
	public SIF_Ack sifUnregister()
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() == AgentProvisioningMode.AGENT ){
			return fPrimitives.sifUnregister(this);
		}

		return null;
	}

	/**
	 *  Sends a SIF_Subscribe message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifSubscribe( String[] objectType )
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() == AgentProvisioningMode.AGENT ){
			return fPrimitives.sifSubscribe(this,objectType);
		}

		return null;
	}

	/**
	 *  Sends a SIF_Unsubscribe message to the ZIS.
	 */
	public SIF_Ack sifUnsubscribe( String[] objectType )
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() != AgentProvisioningMode.ZIS ){
			return fPrimitives.sifUnsubscribe( this,objectType );
		}

		return null;
	}

	/**
	 *  Sends a SIF_Provide message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifProvide( String[] objectType )
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() == AgentProvisioningMode.AGENT ){
			return fPrimitives.sifProvide( this,objectType );
		}

		return null;
	}

	/**
	 *  Sends a SIF_Unprovide message to the ZIS.
	 */
	public SIF_Ack sifUnprovide( String[] objectType )
		throws ADKException
	{
		_checkConnect();

		if( getProperties().getProvisioningMode() != AgentProvisioningMode.ZIS ){
			fPrimitives.sifUnprovide( this,objectType );
		}

		return null;
	}

	/**
	 *  Sends a SIF_Ping message to the ZIS that manages this zone.
	 */
	public SIF_Ack sifPing()
		throws ADKException
	{
		_checkConnect();

		return fPrimitives.sifPing(this);
	}

	/**
	 * Sends a SIF_Sleep System Control message to the zone
	 * @return the SIF_Ack returned from the ZIS
	 * @throws ADKException
	 */
	public SIF_Ack sifSleep()
		throws ADKException
	{
		_checkConnect();
		return fPrimitives.sifSleep(this);
	}

	/**
	 * Sends a SIF_Wakeup System Control message to the Zone
	 * @return he SIF_Ack returned from the ZIS
	 * @throws ADKException
	 */
	public SIF_Ack sifWakeup()
		throws ADKException
	{
		_checkConnect();
		return fPrimitives.sifWakeup(this);
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Zone#purgeQueue(boolean, boolean)
	 */
	public void purgeQueue( boolean incoming, boolean outgoing )
		throws ADKException
	{
	}

	/**
	 *  Assigns an application-supplied object to this Zone
	 *  @param data Any object the application wishes to attach to this Zone instance
	 */
	public void setUserData( Object data ) {
		fUserData = data;
	}

	/**
	 *  Gets the application-supplied object for this Zone
	 *  @return The object passed to the <code>setUserData</code> method
	 */
	public Object getUserData() {
		return fUserData;
	}

	/**
	 *  Returns the string representation of this zone as "zoneId@zoneUrl"
	 */
	@Override
	public String toString()
	{
		StringBuffer b = new StringBuffer(fZoneId);
		b.append('@');
		b.append(fZoneUrl);

		return b.toString();
	}

	protected void _checkConnect()
		throws ADKException
	{
		if( !isConnected() )
			throw new ADKZoneNotConnectedException( "Zone is not connected", this );
	}

	/**
	 *  Gets the root Log4j Category for this agent.
	 */
	public Logger getLog()
	{
		return Agent.getLog( this );
	}

	/**
	 * 	Gets the ServerLog for the zone.<p>
	 * 	@return The ServerLog instance for the zone
	 */
	public ServerLog getServerLog()
	{
		return ServerLog.getInstance( "ADK.Agent$"+getZoneId(),this );
	}

	/**
	 * Returns the ProtocolHandler being used for the zone
	 * @return the ProtocolHandler being used for the zone
	 */
	public IProtocolHandler getProtocolHandler() {
		return fProtocolHandler;
	}

	public void setFDispatcher(MessageDispatcher fDispatcher) {
		this.fDispatcher = fDispatcher;
	}

	public MessageDispatcher getFDispatcher() {
		return fDispatcher;
	}


	/**
	 * 	Undocumented version of the <code>query</code> method that allows the caller to
	 * 	specify the SIF_MsgId that will be used in the SIF_Message/SIF_Header.<p>
	 *
	 *  This method is not part of the public Zone interface. It is intended to be used
	 * 	by agents that must decide upon the SIF_MsgId of a query before the ADK is called,
	 * 	or that want to choose the SIF_MsgId that will be used for a query for debugging
	 * 	purposes or to satisfy some other special requirement. (SIFWorks Student Locator
	 * 	agent is an example of an agent that requires this method.)
	 *
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners
	 * 		registered with the zone will also be called.
	 *  @param destinationId The SourceId of the agent to which the SIF Request
	 * 		will be routed by the zone integration server
	 *  @param queryOptions Reserved for future use
	 * 	@param sifMsgId The value to assign to the SIF_Message/SIF_Header/SIF_MsgId
	 * 		element, or <code>null</code> to let the framework assign its own value
	 *
	 * @return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 * @throws ADKException If the query cannot be sent
	 *
	 * 	@since ADK 1.5
	 */
	public String invokeService( Zone zone, ServiceRequestInfo requestInfo, SIFElement payload )
		throws ADKException
	{
		//	Synchronized to prevent MessageDispatcher from processing a SIF_Response (on the
		//	zone thread) while this thread is issuing a SIF_Request. This is required to ensure
		//	that the query() method completes before any SIF_Response processing is performed.
		//	It can lead to situations where the RequestCache is not updated in time, and/or
		//	the QueryResults.onQueryResults method is called before the onQueryPending method
		//	is called to notify the agent that a request was issued.
		//
		synchronized( fReqLock )
		{
			_checkConnect();

			SIF_ServiceInput req = null;

			try
			{
				SIF_Ack ack = fPrimitives.sifServiceRequest(zone, requestInfo, payload);
				if( ack.hasError() )
					ADKUtils._throw( new SIFException(ack,this),log );

				req = (SIF_ServiceInput)ack.message;

				//  Record the request in the agent's RequestCache
//				RequestInfo ri = getFDispatcher().fRequestCache.storeServiceRequestInfo( req, query, this );

				//  Call the onQueryResults.onQueryPending method...
/*				QueryResults target = getFDispatcher().getQueryResultsTarget(null,req,null,query,this);
				if( target != null )
				{
					SIFMessageInfo inf = new SIFMessageInfo(ack.message,this);
					inf.setSIFRequestMsgId( inf.getMsgId() );
					inf.setSIFRequestVersion( query.getSIFVersions() );
					inf.setSIFRequestObjectType( query.getObjectType() );
					inf.setSIFRequestInfo( ri );

					try
					{
						if( BuildOptions.PROFILED )
							ProfilerUtils.profileStart( String.valueOf( com.edustructures.sifprofiler.api.OIDs.ADK_SIFREQUEST_REQUESTOR_MESSAGING ), query.getObjectType(), inf.getMsgId() );

						target.onQueryPending( inf,this );
					}
					finally
					{
						if( BuildOptions.PROFILED )
							ProfilerUtils.profileStop();
					}
				}
*/			}
			catch( ADKException adke )
			{
				throw adke;
			}

			//	New to 1.5: Return the SIF_Request/SIF_MsgId to the caller
			return req.getHeader().getSIF_MsgId();
		}
	}

	/*
	 * JEN Services
	 */
	private HashMap<String, SIFZoneService> serviceProviders = new HashMap<String, SIFZoneService> ();  // onRequest
	private HashMap<String, SIFZoneServiceProxy> serviceSubscribers = new HashMap<String, SIFZoneServiceProxy> ();  // onQueryResults
	private HashMap<String, ArrayList<SIFZoneServiceProxy>> serviceNotifiers = new HashMap<String, ArrayList<SIFZoneServiceProxy>>();
	
	/*
	 * 
	 */
	protected SIFZoneService getServicePublisher(String serviceName ) {
		return serviceProviders.get(serviceName);
	}
	
	public void setServiceProviders(String serviceName, SIFZoneService sifZoneService) {
		serviceProviders.put(serviceName, sifZoneService);
	}

	protected SIFZoneServiceProxy getServiceSubscriber(String serviceName ) {
		return serviceSubscribers.get(serviceName);
	}
	
	public void setServiceSubscribers(String serviceName, SIFZoneServiceProxy sifZoneServiceProxy) {
		serviceSubscribers.put(serviceName, sifZoneServiceProxy);
	}

	protected ArrayList<SIFZoneServiceProxy> getServiceNotifiers(String serviceName) {
		return serviceNotifiers.get(serviceName);
	}
	
	
	public void setServiceNotifier(String serviceName, SIFZoneServiceProxy sifZoneServiceProxy) {
		ArrayList<SIFZoneServiceProxy> list = getServiceNotifiers(serviceName);
		if (list == null)
			list = new ArrayList<SIFZoneServiceProxy>();
		list.add(sifZoneServiceProxy);
		
		serviceNotifiers.put(serviceName, list);
	}
	
	
	public void reportServiceEvent(ServiceEvent event, String destinationId) throws ADKException {
		_checkConnect();

		SIF_Ack ack = fPrimitives.sifServiceEvent(this, event, destinationId);
		if( ack.hasError() )
			ADKUtils._throw( new SIFException(ack,this),log );
	}
}
