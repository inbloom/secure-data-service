//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Aggregates publish, subscribe, and query activity across multiple zones.<p>
 *
 *  An agent typically registers with its zones at startup, creates Topic
 *  instances for the data types it is interested in, and then joins topics to
 *  each zone by calling {@link #join(Zone)}. Topics can be joined to any number
 *  of zones. If an agent wishes to manage publish and subscribe
 *  activity on a per-zone basis, it can do so by provisioning the zones directly
 *  by calling {@link Provisioner#setPublisher(Publisher)}, 
 *  {@link Provisioner#setQueryResults(QueryResults)}, or
 *  {@link Provisioner#setSubscriber(Subscriber, ElementDef, SubscriptionOptions)}<p>
 *
 *  The ADK applies publish, subscribe, and query functionality equally to all
 *  zones joined to a topic. Thus, if the topic provides authoritative data to
 *  its zones (that is, it is registered with the ZIS as a Provider), it will
 *  do so for all zones by delegating incoming queries to the Publisher
 *  registered with this topic. Similarly, the topic will 
 *  query all zones for data objects when the {@link #query(Query)} method is
 *  called. (An exception to this is directed queries, which require that the
 *  caller specify a zone and agent name to direct the query.)<p>
 *
 *  Consult the ADK Developer Guide for more information about working with
 *  multiple zones.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public interface Topic
{
	/**
	 *  Joins a zone with this topic.<p>
	 *
	 *  All SIF messaging performed on this topic will be propagated to all
	 *  zones joined with the topic. For example, calling the <code>query</code>
	 *  method queries all of the topic's zones, and SIF_Responses received by
	 *  each zone are first dispatched to the topic's <i>QueryResults</i> object.<p>
	 *
	 *  @param zone A Zone created by the agent ZoneFactory
	 *  @exception ADKException is thrown if the zone is already joined to a
	 *      topic or if there is a SIF error during agent registration.
	 */
	public void join( Zone zone )
		throws ADKException;

	/**
	 *  Gets the name of the SIF Data Object associated with this topic
	 *  @return A SIF Data Object type such as "StudentPersonal"
	 */
	public String getObjectType();
	
	
	/**
	 * Gets the ElementDef representing the SIF Data Object associated with this topic
	 * @return The ElementDef used to create this Topic instance, such as StudentDTD.STUDENTPERSONAL
	 */
	public ElementDef getObjectDef();
	
	
	/**
	 * Gets the SIFContext that this Topic is bound to. A single Topic can only be bound to a single
	 * SIF Context.
	 * @return The SIFContext that this Topic is bound to.
	 */
	public SIFContext getSIFContext();

	/**
	 *  Gets all zones joined to this topic
	 *  @return An array of Zones
	 */
	public Zone[] getZones();

	/**
	 *  Register a Publisher message handler to process SIF_Request messages 
	 * 	received on the zones joined with this topic. The message handler will 
	 * 	be called whenever a SIF_Request is received for the SIF Data Object type 
	 * 	associated with the topic.<p>
	 * 
	 * 	For Topics created to represent the SIF_ReportObject object type, register 
	 * 	a publisher message handler with the <code>setReportPublisher</code>
	 * 	method instead.<p> 
	 *
	 *  @param publisher An object that implements the <code>Publisher</code> interface.
	 * @throws ADKException Thrown 
	 */
	public void setPublisher( Publisher publisher )	throws ADKException;
	
	
	/**
	 *  Register a Publisher message handler to process SIF_Request messages 
	 * 	received on the zones joined with this topic. The message handler will 
	 * 	be called whenever a SIF_Request is received for the SIF Data Object type 
	 * 	associated with the topic.<p>
	 * 
	 * 	For Topics created to represent the SIF_ReportObject object type, register 
	 * 	a publisher message handler with the <code>setReportPublisher</code>
	 * 	method instead.<p> 
	 *
	 *  @param publisher An object that implements the <code>Publisher</code> interface.
	 *
	 * 	@param flags Specify PublishingOptions to control whether the
	 * agent registers as the default Responder for this object type in the
	 * zone. Also allows other options, such as the SIF_Contexts supported.
	 * @throws ADKException Thrown 
	 */
	public void setPublisher( Publisher publisher, PublishingOptions flags )
		throws ADKException;

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
	 * 
	 * 	@since ADK 1.5
	 */
	public void setReportPublisher( ReportPublisher publisher );

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
	 * 	@param flags Specify PublishingOptions to control whether the
	 * agent registers as the default Responder for SIF_ReportObject in the
	 * zone. Also allows other options, such as the SIF_Contexts supported.
	 * 
	 * 	@since ADK 1.5
	 */
	public void setReportPublisher( ReportPublisher publisher, ReportPublishingOptions flags );

	/**
	 *  Gets the <i>Publisher</i> registered with this topic
	 *  @return The object passed to the <code>setPublisher</code> method
	 */
	public Publisher getPublisher();
	
	/**
	 *  Gets the <i>ReportPublisher</i> registered with this topic
	 *  @see #setReportPublisher(ReportPublisher, ReportPublishingOptions)
	 *  @return The object passed to the <code>setReportPublisher</code> method
	 */
	public ReportPublisher getReportPublisher();

	
	/**
	 *  Register a Subscriber message handler to process SIF_Event messages 
	 * 	received on the zones joined with this topic. The message handler will 
	 * 	be called whenever a SIF_Event is received for the SIF Data Object type 
	 * 	associated with the topic.<p>
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface

	 * @throws ADKException 
	 */
	public void setSubscriber( Subscriber subscriber )
		throws ADKException;

	
	/**
	 *  Register a Subscriber message handler to process SIF_Event messages 
	 * 	received on the zones joined with this topic. The message handler will 
	 * 	be called whenever a SIF_Event is received for the SIF Data Object type 
	 * 	associated with the topic.<p>
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface
	 *
	 * 	@param flags Specify <code>ADKFlags.PROV_SUBSCRIBE</code> to register the
	 * 		agent as a subscriber of the object type. The ADK will send a 
	 * 		SIF_Subscribe message to each zone joined with the topic.
	 * @throws ADKException 
	 */
	public void setSubscriber( Subscriber subscriber, SubscriptionOptions flags )
		throws ADKException;

	/**
	 *  Gets the <i>Subscriber</i> registered with this topic
	 *  @return The object passed to the <code>setSubscriber</code> method
	 */
	public Subscriber getSubscriber();

	
	/**
	 *  Sets the <i>QueryResults</i> message handler registered with this topic.
	 *  This object will be called whenever a SIF_Response message is received
	 *  by one of the zones joined with this topic and the response contains
	 *  data associated with the topic. Note that SIF_Response messages are
	 *  dispatched to this handler only if the initial request was issued by
	 *  calling one of the <code>Topic.query</code> methods.<p>
	 *
	 *  @param queryResultsObject An <i>QueryResults</i> message handler
	 *  @throws ADKException 
	 *
	 *  @see #query(Query)
	 *  @see #getQueryResultsObject
	 */
	public void setQueryResults( QueryResults queryResultsObject )throws ADKException;
	
	/**
	 *  Sets the <i>QueryResults</i> message handler registered with this topic.
	 *  This object will be called whenever a SIF_Response message is received
	 *  by one of the zones joined with this topic and the response contains
	 *  data associated with the topic. Note that SIF_Response messages are
	 *  dispatched to this handler only if the initial request was issued by
	 *  calling one of the <code>Topic.query</code> methods.<p>
	 *
	 *  @param queryResultsObject An <i>QueryResults</i> message handler
	 *  @param flags The QueryResultsOptions that should be used for this agent, or <code>Null</code>
	 *  to accept defaults 
	 *  @throws ADKException 
	 *
	 *  @see #query(Query)
	 *  @see #getQueryResultsObject
	 */
	public void setQueryResults( QueryResults queryResultsObject, QueryResultsOptions flags )
		throws ADKException;

	/**
	 *  Gets the <i>QueryResults</i> object that is registered with this topic.
	 *
	 *  @return The message handler instance passed to the <code>setQueryResults</code>
	 *      method, or null if no message handler is registered with this topic.
	 */
	public QueryResults getQueryResultsObject();
	
	/**
	 * Creates a Query instance that is initialized to request the object type
	 * encapsulated by this Topic and this SIFContext.<p>
	 * 
	 * It is good practice that this factory method be used when creating Query
	 * instances for Topics. If a Query object is created outside of this method, it
	 * may still work, but if created incorrectly (using the wrong ElementDef or SIFContext),
	 * This topic will not be able to process the results of the Query. 
	 * 
	 * @return a <code>Query</code> instance that has been set to represent a
	 * SIF_Request for the SIFDataObject type represented by this topic
	 */
	public Query createQuery();

	/**
	 * Query the topic by sending a SIF_Request message to all zones joined
	 * with the topic. To specify query conditions for this object, call
	 * {@link #createQuery()} to get a <code>Query</code> instance, add query
	 * Conditions by calling {@link Query#addCondition(ElementDef, ComparisonOperators, String)},
	 * and then call {@link #query(Query)} 
	 * @throws ADKException 
	 */
	public void query()
		throws ADKException;
		
	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results
	 * @throws ADKException 
	 */
	public void query( Query query )
		throws ADKException;

	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic. This form of the <code>query</code> method also notifies a 
	 * 	MessagingListener of any SIF_Request messaging that takes place.<p>
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called.
	 * @throws ADKException 
	 * 
	 * 	@since ADK 1.5 
	 */
	public void query( Query query, MessagingListener listener )
		throws ADKException;

	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results
	 *  @param queryOptions Reserved for future use
	 * @throws ADKException 
	 */
	public void query( Query query, int queryOptions )
		throws ADKException;

	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic. This form of the <code>query</code> method also notifies a 
	 * 	MessagingListener of any SIF_Request activity that takes place.<p>
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results
	 *  @param queryOptions Reserved for future use
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called. 
	 * @throws ADKException 
	 * 
	 * 	@since ADK 1.5
	 */
	public void query( Query query, MessagingListener listener, int queryOptions )
		throws ADKException;

	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results
	 *  @param destinationId The SourceId of the agent to which the SIF_Request
	 *      message should be delivered. When null, the message is delivered to
	 *      the object provider as defined by the SIF Zone
	 *  @param queryOptions Reserved for future use
	 * @throws ADKException 
	 */
	public void query( Query query, String destinationId, int queryOptions )
		throws ADKException;

	/**
	 *  Query the topic by sending a SIF_Request message to all zones joined
	 *  with the topic. This form of the <code>query</code> method also notifies 
	 * 	a MessagingListener of any SIF_Request messaging that takes place.<p>
	 *
	 *  @param query A Query object that encapsulates the elements to query and
	 *      the optional field restrictions placed on the results.
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called. 
	 *  @param destinationId The SourceId of the agent to which the SIF_Request
	 *      message should be delivered. When null, the message is delivered to
	 *      the object provider as defined by the SIF Zone.
	 *  @param queryOptions Reserved for future use.
	 * @throws ADKException 
	 * 
	 * 	@since ADK 1.5
	 */
	public void query( Query query, MessagingListener listener, String destinationId, int queryOptions )
		throws ADKException;
	
	/**
	 *  Purge all pending incoming and/or outgoing messages from this agent's
	 *  queue. Only messages destined to and originating from the zones joined
	 *  to this topic are affected. See also the Agent.purgeQueue and
	 *  Zone.purgeQueue methods to purge the queues of all zones to which the
	 *  agent is connected or a specific zone, respectively.<p>
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
		throws ADKException;
}
