//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.util.List;
import java.util.Vector;
import java.util.Enumeration;

import openadk.library.*;
import openadk.library.reporting.ReportingDTD;

import org.apache.log4j.Category;
import org.apache.log4j.Logger;

/**
 *  Default implementation of the Topic interface.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class TopicImpl implements Topic
{
	/**
	 *  Log4j logging category for this topic
	 */
	public Category log;

	/**
	 *  The Subscriber registered with this topic.
	 */
	protected Subscriber fSub;

	/**
	 *  The Publisher registered with this topic.
	 */
	protected Publisher fPub;
	
	/**
	 * 	The ReportPublisher registered with this topic (for SIF_ReportObject topics only)
	 */
	protected ReportPublisher fReportPub;

	/**
	 *  The QueryResults registered with this topic.
	 */
	protected QueryResults fQueryResults;
	

	/**
	 *  The SIF data object type associated with this topic
	 */
	protected ElementDef fObjType;

	/**
	 * The options for Publishing
	 */
	protected PublishingOptions fPubOpts;
	
	/**
	 *  The options for ReportPublishing
	 */
	protected ReportPublishingOptions fReportPubOpts;

	protected SubscriptionOptions fSubOpts;
	
	/**
	 * The SIF Context that this topic is joined to
	 */
	private SIFContext fContext;

	/**
	 *  The Zones joined with this topic
	 */
	protected List<Zone> fZones = new Vector<Zone>();

	/**
	 * The options for QueryResults handling
	 */
	protected QueryResultsOptions fQueryResultsOptions;

    protected TopicImpl( ElementDef objType, SIFContext context )
	{
		fObjType = objType;
		fContext = context;
		log = Logger.getLogger( Agent.LOG_IDENTIFIER + ".Topic$"+objType.name());
    }

	/**
	 *  Adds a zone to this topic
	 *  @param zone The Zone to join with this topic
	 *  @exception ADKException is thrown if the zone is already joined to a
	 *      topic or if there is a SIF error during agent registration.
	 */
	public synchronized void join( Zone zone )
		throws ADKException
	{
		//  Check that zone is not already joined with this topic
		if( fZones.contains( zone ) )
			ADKUtils._throw( new IllegalStateException("Zone already joined with topic \""+fObjType+"\""),((ZoneImpl)zone).log );

		//  Check that topic has a Provider, Subscriber, or QueryResults object
		if( fSub == null && fPub == null && fReportPub == null && fQueryResults == null )
			ADKUtils._throw( new IllegalStateException("Agent has not registered a Subscriber, Publisher, or QueryResults object with this topic"),((ZoneImpl)zone).log );

		fZones.add( zone );

		if( zone.isConnected() )
			((ZoneImpl)zone).provision();
	}

	/**
	 *  Gets the name of the SIF data object type associated with this topic
	 *  @return The name of a root level SIF data object such as "StudentPersonal",
	 *      "BusInfo", or "LibraryPatronStatus"
	 */
	public String getObjectType()
	{
		return fObjType.name();
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Topic#getObjectDef()
	 */
	public ElementDef getObjectDef() {
		return fObjType;
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Topic#getSIFContext()
	 */
	public SIFContext getSIFContext()
	{
		return fContext;
	}

	/**
	 *  Gets the zones to which this topic is bound
	 *  @return The zone that created this topic instance
	 */
	public synchronized Zone[] getZones()
	{
		Zone[] arr = new Zone[ fZones.size() ];
		fZones.toArray( arr );
		return arr;
	}

	/**
	 *  Checks that at least one zone is joined with the topic
	 *  @exception ADKException is thrown if no zones are joined with this topc
	 */
	private void _checkZones() throws ADKException
	{
		if( fZones.size() == 0 )
			throw new ADKException("No zones are joined with the \"" + fObjType + "\" topic",null);
	}

	
	/**
	 *  Register a publisher of this topic.<p>
	 *
	 *  Provisioning messages are sent as follows:<p>
	 *
	 *  <ul>
	 *      <li>
	 *          If the agent is using ADK-managed provisioning, a <code>&lt;
	 *          SIF_Provide&gt;</code> message is sent to the ZIS when the
	 *          ADKFlags.PROV_PROVIDE flag is specified. When
	 *          ADK-managed provisioning is disabled, no messages are sent to
	 *          the zone.
	 *      </li>
	 *      <li>
	 *          If Agent-managed provisioning is enabled, the ProvisioningOptions
	 *          flags have no affect. The agent must explicitly call the
	 *          sifProvide method to manually send those message to the zone.
	 *      </li>
	 *      <li>
	 *          If ZIS-managed provisioning is enabled, no provisioning messages
	 *          are sent by the agent regardless of the ProvisioningOptions
	 *          used and the methods are called.
	 *      </li>
	 *  </ul>
	 *  <p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to publish change events and to evaluate SIF queries
	 *      received by the agent
	 */
	public synchronized void setPublisher( Publisher publisher )
	{
		setPublisher( publisher, null );
	}
	
	/**
	 *  Register a publisher of this topic.<p>
	 *
	 *  Provisioning messages are sent as follows:<p>
	 *
	 *  <ul>
	 *      <li>
	 *          If the agent is using ADK-managed provisioning, a <code>&lt;
	 *          SIF_Provide&gt;</code> message is sent to the ZIS when the
	 *          ADKFlags.PROV_PROVIDE flag is specified. When
	 *          ADK-managed provisioning is disabled, no messages are sent to
	 *          the zone.
	 *      </li>
	 *      <li>
	 *          If Agent-managed provisioning is enabled, the ProvisioningOptions
	 *          flags have no affect. The agent must explicitly call the
	 *          sifProvide method to manually send those message to the zone.
	 *      </li>
	 *      <li>
	 *          If ZIS-managed provisioning is enabled, no provisioning messages
	 *          are sent by the agent regardless of the ProvisioningOptions
	 *          used and the methods are called.
	 *      </li>
	 *  </ul>
	 *  <p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to publish change events and to evaluate SIF queries
	 *      received by the agent
	 * @param provisioningOptions The options to use for this Publisher
	 */
	public synchronized void setPublisher( Publisher publisher, PublishingOptions provisioningOptions )
	{
		assertProvisioningOptions( provisioningOptions );
		if( ( fObjType == ReportingDTD.SIF_REPORTOBJECT ) ){
			throw new IllegalArgumentException( "You must call the setReportPublisher method for SIF_ReportObject topics" );
		}

		if( publisher == null ){
			fPub = null;
			fPubOpts = null;
		} else {
			fPub = publisher;
			if( provisioningOptions == null ){
				provisioningOptions = new PublishingOptions();
			}
			fPubOpts = provisioningOptions;
		}
	}

	
	/**
	 *  Register a ReportPublisher message handler to process SIF_Request messages 
	 * 	received for SIF_ReportObject objects from the zones joined with this topic. 
	 * 	ReportPublisher is implemented by Vertical Reporting applications that 
	 * 	publish report data via the SIF_ReportObject object (SIF 1.5 and later).<p>
	 * 
	 * 	This method must be called instead of <code>setPublisher</code> for topics 
	 * 	created to represent the SIF_ReportObject data type.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code> interface.
	 *
	 */
	public synchronized void setReportPublisher( ReportPublisher publisher )
	{
		setReportPublisher( publisher, null );
	}
	
	/**
	 *  Register a ReportPublisher message handler to process SIF_Request messages 
	 * 	received for SIF_ReportObject objects from the zones joined with this topic. 
	 * 	ReportPublisher is implemented by Vertical Reporting applications that 
	 * 	publish report data via the SIF_ReportObject object (SIF 1.5 and later).<p>
	 * 
	 * 	This method must be called instead of <code>setPublisher</code> for topics 
	 * 	created to represent the SIF_ReportObject data type.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code> interface.
	 *
	 * 	@param flags Specify <code>ADKFlags.PROV_PROVIDE</code> to register the
	 * 		agent as the default provider of the object type associated with this
	 * 		topic. The ADK will send a SIF_Provide message to each zone joined 
	 * 		with the topic. Specify <code>ADKFlags.PROV_NONE</code> if the agent 
	 * 		can respond to requests for the object type but will not register as 
	 * 		the authoritative provider.
	 * 
	 * 	@since ADK 1.5
	 */
	public synchronized void setReportPublisher( ReportPublisher publisher, ReportPublishingOptions flags )
	{
		assertProvisioningOptions( flags );
		if( !(fObjType == ReportingDTD.SIF_REPORTOBJECT) ) {
			throw new IllegalArgumentException( "You must call the setPublisher method for topics that do not represent SIF_ReportObject" );
		}
		if( publisher == null ){
			fReportPub = null;
			fReportPubOpts = null;
		} else {
			fReportPub = publisher;
			if( flags == null ){
				flags = new ReportPublishingOptions();
			}
			fReportPubOpts = flags;
		}
	}

	public synchronized Publisher getPublisher()
	{
		return fPub;
	}

	public synchronized ReportPublisher getReportPublisher()
	{
		return fReportPub;
	}

	public synchronized void setSubscriber( Subscriber subscriber )
	{
		setSubscriber(subscriber, null );
	}
	
	
	public synchronized void setSubscriber( Subscriber subscriber, SubscriptionOptions flags )
	{
		assertProvisioningOptions( flags );
		if( subscriber == null ){
			fSub = null;
			fSubOpts = null;
		} else {
			fSub = subscriber;
			if( flags == null ){
				flags = new SubscriptionOptions();
			}
			fSubOpts = flags;
		}
	}

	public synchronized Subscriber getSubscriber()
	{
		return fSub;
	}

	public synchronized void setQueryResults( QueryResults results )
	throws ADKException
	{
		setQueryResults( results, null );
	}
	
	public synchronized void setQueryResults( QueryResults results, QueryResultsOptions flags )
		throws ADKException
	{
		assertProvisioningOptions( flags );
		if( results == null ){
			fQueryResults = null;
			fQueryResultsOptions = null;
		} else {
			fQueryResults = results;
			if( flags == null ){
				flags = new QueryResultsOptions();
			}
			fQueryResultsOptions = flags;
		}
	}

	public synchronized QueryResults getQueryResultsObject()
	{
		return fQueryResults;
	}
	
	
	private void assertProvisioningOptions( ProvisioningOptions opts ){
		if( opts != null && opts.getSupportedContexts().size() > 1 )  {
			throw new IllegalArgumentException( "Cannot provision a single topic for more than one SIF Context.\r\n" + 
					"To use Topics with multiple SIF contexts, call TopicFactory.getInstance( ElementDef, SIFContext )." );
		}
	}

	/**
	 *  Publishes a change in topic data by sending a SIF_Event to all zones
	 *  joined with this topic<p>
	 *
	 *  This method is useful for communicating a single change event. If an
	 *  agent changes data that spans several object types, it should consider
	 *  using the BatchEvent class to publish changes as a group. BatchEvent
	 *  aggregates changes in multiple SIF data objects, then sends a single
	 *  SIF_Event message to each zone. This is much more efficient than calling
	 *  the publishChange method of each Topic, which results in a single
	 *  SIF_Event message being sent for each object type. Another alternative
	 *  is to call the publishChange method of each Zone directly. That method
	 *  accepts an Event object, which can describe changes in multiple data
	 *  objects.<p>
	 *
	 *  @param data The data that has changed. The objects in this array must all
	 *      be of the same SIF object type (e.g. all <code>StudentPersonal</code>
	 *      objects if this topic encapsulates the "StudentPersonal" object type),
	 *      and must all communicate the same state change (i.e. all added,
	 *      all changed, or all deleted).
	 * @throws ADKException 
	 */
	public synchronized void publishEvent( Event data )
		throws ADKException
	{
		ADKMessagingException err = null;

		_checkZones();

		for( Zone zone : fZones )
		{
			ZoneImpl z = (ZoneImpl)zone;

			try
			{
				z.fPrimitives.sifEvent(z,data,null,null);
			}
			catch( Throwable th )
			{
				if( err == null )
					err = new ADKMessagingException("Error publishing event to topic \""+fObjType+"\"",z);

				if( th instanceof ADKException )
					err.add( th );
				else
					err.add( new ADKMessagingException(th.toString(),z) );
			}
		}

		if( err != null )
		    ADKUtils._throw( err, log );
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Topic#createQuery()
	 */
	public Query createQuery() {
		return new Query( fObjType );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.Topic#query()
	 */
	public void query() throws ADKException {
		query( createQuery() );		
	}

	public synchronized void query( Query query )
		throws ADKException
	{
		query(query,null,null,0);
	}

	public synchronized void query( Query query, MessagingListener listener )
		throws ADKException
	{
		query(query,listener,null,0);
	}

	public synchronized void query( Query query, int queryOptions )
		throws ADKException
	{
		query(query,null,null,queryOptions);
	}

	public synchronized void query( Query query, MessagingListener listener, int queryOptions )
		throws ADKException
	{
		query(query,listener,null,queryOptions);
	}

	public synchronized void query( Query query, String destinationId, int queryOptions )
		throws ADKException
	{
		query( query, null, destinationId, queryOptions );
	}
	
	public synchronized void query( Query query, MessagingListener listener, String destinationId, int queryOptions )
		throws ADKException
	{
		if( query == null ){
			ADKUtils._throw( new IllegalArgumentException("Query object cannot be null"), log );
		}
		// Validate that the query object type and SIF Context are valid for this Topic
		if( query.getObjectType() != fObjType ){
			ADKUtils._throw( new IllegalArgumentException("Query object type: {" + query.getObjectTag() +
					"} does not match Topic object type: " + fObjType + "}"), log );
		}
		
		if( !query.getSIFContext().equals( fContext ) ){
			ADKUtils._throw( new IllegalArgumentException("Query SIF_Context: {" + query.getSIFContext() +
					"} does not match Topic SIF_Context: " + fContext + "}"), log );
		}

		_checkZones();
		
		ADKMessagingException err = null;

		//  Send the SIF_Request to each zone
		for( Zone zone: fZones)
		{
			ZoneImpl z = (ZoneImpl)zone;

			try
			{
				z.query(query,listener,destinationId,queryOptions);
			}
			catch( Throwable th )
			{
				if( err == null )
					err = new ADKMessagingException("Error querying topic \"" + fObjType.name() + "\"",z);

				if( th instanceof ADKException )
					err.add( th );
				else
					err.add( new ADKMessagingException(th.toString(),z) );
			}
		}

		if( err != null )
			ADKUtils._throw( err, log );
	}

	public void purgeQueue( boolean incoming, boolean outgoing )
		throws ADKException
	{
	}

	public String toString() {
		return "Topic:" + fObjType.name();
	}



}
