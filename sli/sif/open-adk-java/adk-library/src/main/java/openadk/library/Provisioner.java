//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Provisioner is a common interface implemented by the Agent, Zone, and Topic
 * classes. It provides common APIs for setting Subscribers, Publisher, and QueryResults
 * handlers
 * @author Andrew
 *
 */
public interface Provisioner {
	
	/**
	 *  Register a Publisher message handler to process SIF_Request
	 * 	messages for all object types.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent.
	 *      This object will be called whenever a SIF_Request is received and 
	 *      no other object in the message dispatching chain has
	 *      processed the message.
	 */
	public void setPublisher( Publisher publisher ) throws ADKException;
	
	/**
	 *  Register a Publisher message handler with this zone to process SIF_Requests
	 * 	for the specified object type. This method may be called repeatedly for
	 * 	each SIF Data Object type the agent will publish on this zone.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Publisher will be called whenever a
	 *      SIF_Request is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType An ElementDef constant from the SIFDTD class that 
	 *  identifies a SIF Data Object type. E.g. SIFDTD.STUDENTPERSONAL
	 * 
	 */
	public void setPublisher( Publisher publisher, ElementDef objectType )
		throws ADKException;
	
	
	/**
	 *  Register a Publisher message handler with this zone to process SIF_Requests
	 * 	for the specified object type. This method may be called repeatedly for
	 * 	each SIF Data Object type the agent will publish on this zone.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Publisher will be called whenever a
	 *      SIF_Request is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType An ElementDef constant from the SIFDTD class that 
	 *  identifies a SIF Data Object type. E.g. SIFDTD.STUDENTPERSONAL
	 * 
	 * 	@param options Specify options about which SIF Contexts to join and whether
	 * SIF_Provide messagees will be sent when the agent is running in SIF 1.5r1 or lower
	 */
	public void setPublisher( Publisher publisher, ElementDef objectType, PublishingOptions options )
		throws ADKException;

	/**
	 *  Register a ReportPublisher message handler to respond to
	 * 	SIF_Requests for SIF_ReportObject objects. ReportPublisher is implemented
	 * 	by Vertical Reporting applications that publish report data via the 
	 * 	SIF_ReportObject object introduced in SIF 1.5.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent.
	 *      This object will be called whenever a SIF_Request is received and 
	 *      no other object in the message dispatching chain has
	 *      processed the message.
	 *
	 */
	public void setReportPublisher( ReportPublisher publisher )
		throws ADKException;
	
	
	/**
	 *  Register a ReportPublisher message handler to respond to
	 * 	SIF_Requests for SIF_ReportObject objects. ReportPublisher is implemented
	 * 	by Vertical Reporting applications that publish report data via the 
	 * 	SIF_ReportObject object introduced in SIF 1.5.<p>
	 *
	 *  @param publisher An object that implements the <code>Publisher</code>
	 *      interface to respond to SIF_Request queries received by the agent.
	 *      This object will be called whenever a SIF_Request is received and 
	 *      no other object in the message dispatching chain has
	 *      processed the message.
	 *
	 * 	@param options Specify which contexts to join as a provider and whether
	 * to send a SIF_Provide message when the agent is running in SIF 1.5r1 or lower
	 */
	public void setReportPublisher( ReportPublisher publisher, ReportPublishingOptions options )
		throws ADKException;

	/**
	 *  Register a Subscriber message handler with this zone to process SIF_Event
	 * 	messages for the specified object type. This method may be called 
	 * 	repeatedly for each SIF Data Object type the agent subscribes to on 
	 * 	this zone.<p>
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface to respond to SIF_Event notifications received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Subscriber will be called whenever a
	 *      SIF_Event is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies a
	 *      SIF Data Object type.
	 *
	 */
	public void setSubscriber( Subscriber subscriber, ElementDef objectType )
		throws ADKException;
	
	
	/**
	 *  Register a Subscriber message handler with this zone to process SIF_Event
	 * 	messages for the specified object type. This method may be called 
	 * 	repeatedly for each SIF Data Object type the agent subscribes to on 
	 * 	this zone.<p>
	 *
	 *  @param subscriber An object that implements the <code>Subscriber</code>
	 *      interface to respond to SIF_Event notifications received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This Subscriber will be called whenever a
	 *      SIF_Event is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies a
	 *      SIF Data Object type.
	 *
	 * 	@param options Specify which contexts to join and whether SIF_Subscribe
	 * messages will be sent when the agent is running in SIF 1.5r1 or lower
	 */
	public void setSubscriber( Subscriber subscriber, ElementDef objectType, SubscriptionOptions options )
		throws ADKException;

	/**
	 *  Register a QueryResults message handler with this zone to process
	 * 	SIF_Response messages for all object types.<p>
	 *
	 *  @param queryResults An object that implements the <code>QueryResults</code>
	 *      interface to respond to SIF_Response query results received by the
	 *      agent. This object will be called whenever a SIF_Response is received
	 *      and no other object in the message dispatching chain has processed the message.
	 */
	public void setQueryResults( QueryResults queryResults )
		throws ADKException;

	/**
	 *  Register a QueryResults object with this zone for the specified SIF object type.<p>
	 *
	 *  @param queryResults An object that implements the <code>QueryResults</code>
	 *      interface to respond to SIF_Response query results received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This QueryResults object will be called whenever
	 *      a SIF_Response is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies a
	 *      SIF Data Object type.
	 *    
	 */
	public void setQueryResults( QueryResults queryResults, ElementDef objectType )
		throws ADKException;
	
	
	/**
	 *  Register a QueryResults object with this zone for the specified SIF object type.<p>
	 *
	 *  @param queryResults An object that implements the <code>QueryResults</code>
	 *      interface to respond to SIF_Response query results received by the agent,
	 *      where the SIF object type referenced by the request matches the
	 *      specified objectType. This QueryResults object will be called whenever
	 *      a SIF_Response is received on this zone and no other object in the
	 *      message dispatching chain has processed the message.
	 *
	 *  @param objectType A constant from the SIFDTD class that identifies a
	 *      SIF Data Object type.
	 *    
	 *  @param options Specify which contexts to join
	 */
	public void setQueryResults( QueryResults queryResults, ElementDef objectType, QueryResultsOptions options )
		throws ADKException;

}
