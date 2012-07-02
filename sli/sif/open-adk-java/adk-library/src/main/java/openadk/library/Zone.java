//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.net.URL;
import java.util.List;

import openadk.library.impl.Transport;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_AgentACL;
import openadk.library.infra.SIF_ZoneStatus;
import openadk.library.log.ServerLog;
import openadk.library.services.SIFZoneService;
import openadk.library.services.SIFZoneServiceProxy;
import openadk.library.services.ServiceRequestInfo;
import openadk.library.threadpool.ThreadPoolManager;

import org.apache.log4j.Logger;


/**
 *  A Zone is a logical grouping of applications that exchange data through the
 *  Schools Interoperability Framework. Each zone is managed by a Zone Integration
 *  Server (ZIS). Refer to the SIF Specifications for a more detailed definition.
 *  Agents developed with the ADK may connect to multiple zones concurrently.<p>
 *
 *  Zones have the following characteristics:
 *  <p>
 *
 *  <ul>
 *      <li>
 *          Each zone connects to a Zone Integration Server, which provides a
 *          reliable and persistent queue for the storage of messages pending
 *          delivery to the agent. The "connection" is a logical one. The ADK
 *          handles retrieving messages from the queue and dispatching them to
 *          the <i>Publisher</i>, <i>Subscriber</i>, and <i>QueryResults</i>
 *          interfaces registered with your agent's Zone or Topic objects.
 *          <br/><br/>
 *      </li>
 *      <li>
 *          The ADK provides a persistent and reliable Agent Local Queue (ALQ)
 *          for each zone. Messages retrieved from the Zone Integration Server
 *          are stored in the local queue before being processed. A given
 *          message exists in the local queue or in the server queue but never
 *          in both. The Agent Local Queue provides for enhanced reliability
 *          and supports disconnected communications with the server.<br><br>
 *      </li>
 *      <li>
 *          Each zone maintains its own set of properties. By default, a zone
 *          inherits its default properties from the agent. To set properties
 *          on a per-zone basis, call the <code>Zone.getProperties</code> method
 *          to obtain the zone's AgentProperties object, then call its accessor
 *          methods. Properties must be set prior to calling <code>connect</code>.
 *          <br><br>
 *      </li>
 *  </ul>
 *
 *  In order to exchange messages with a zone, the agent must be <i>connected</i>
 *  to the zone. The act of connecting establishes local resources for transport
 *  protocols and queuing. Depending on the flags passed to the <code>connect</code>
 *  method, the agent may also send SIF registration messages to establish
 *  similar resources on the server.<p>
 *
 *  An agent obtains a Zone instance by calling <code>Agent.getZoneFactory</code>.
 *  When finished interacting with a zone, call the <code>disconnect</code>
 *  method to release local resources held by the ADK as well as to optionally
 *  send a SIF_Unregister message to the server. Disconnecting all zones to
 *  which an agent is connected is easily done by calling the <code>Agent.shutdown</code>
 *  method when the agent exits.<p>
 *
 *  Agents typically use <i>Topic</i> objects to aggregate publish, subscribe,
 *  and query activity across multiple zones. Refer to the Topic interface for
 *  details.
 *
 *  @author Eric Petersn
 *  @version ADK 1.0
 */
public interface Zone extends Provisioner
{
	/**
	 *  Gets the properties for this zone.<p>
	 *
	 *  By default, a zone inherits the default agent properties.
	 *
	 *  @return The zone properties
	 */
	public AgentProperties getProperties();

	/**
	 *  Assigns a new properties object to this zone.<p>
	 *
	 *  By default, a zone inherits the default agent properties. To change a
	 *  property, first call the getProperties method to obtain the zone's
	 *  properties object, then call any of the accessor methods available on
	 *  that object. Because zones are constructed with a default AgentProperties
	 *  object, it is typically not necessary to call this method.
	 *
	 *  @param props A new properties object to replace the existing object
	 */
	public void setProperties( AgentProperties props );

	/**
	 *  Gets the Agent object<p>
	 *  @return The Agent that created this Zone instance
	 */
	public Agent getAgent();

	/**
	 *  Gets the zone identifier<p>
	 *  @return The name of the zone as defined by the Zone Integration Server
	 */
	public String getZoneId();

	/**
	 *  Gets the URL of the Zone Integration Server that manages this zone<p>
	 *  @return The URL to the ZIS (e.g. "http://host:port/zoneId" is the URL
	 *      convention employed by the SIFWorks ZIS)
	 */
	public URL getZoneUrl();

	/**
	 *  Connects the agent with this zone.<p>
	 *
	 *  An agent must connect to a zone in order to perform messaging within the
	 *  context of the ADK Class Framework. A typical agent calls this method at
	 *  startup for each zone it will connect to, then optionally joins the Zone
	 *  with one or more Topics.<p>
	 *
	 *  The ADK's Provisioning Mode affects the messages sent by this method.
	 *  Refer to the AgentProperties class for more information.<p>
	 *
	 *  The ADK will send <code>&lt;SIF_Subscribe&gt;</code> and <code>&lt;SIF_Provide&gt;</code>
	 *  messages to the zone to provision the SIF Data Objects for which the agent has registered a
	 *  <i>Subscriber</i> and <i>Publisher</i> message handlers, respectively.
	 *  The <code>adk.provisioning.batch</code> agent property (refer to the
	 *  AgentProperties class) determines whether the ADK sends a single message
	 *  or individual messages for each SIF Data Object. When individual messages
	 *  are sent (the default), Category 4 Access Control errors are treated as
	 *  warnings. Connect warnings can be retrieved by calling the
	 *  <code>getConnectWarnings</code> method. When a single message is sent,
	 *  any SIF Error will result in the raising of a SIFException.<p>
	 *
	 *  Note the SIF_Wakeup and SIF_Ping system control messages are sent to
	 *  the server upon successful connection.<p>
	 *
	 *  @param flags One or more of the following flags:<p>
	 *
	 *  <table border="1">
	 *      <tr>
	 *          <td>Flag</td>
	 *          <td>Description</td>
	 *      </tr>
	 *      <tr>
	 *          <td><code>ADKFlags.REGISTER</code></td>
	 *          <td>Sends a SIF_Register message to the ZIS and wakes up the
	 *          agent queue on the server if previously in sleep mode.</td>
	 *      </tr>
	 *  </table>
	 *  </p>
	 *
	 *  @exception IllegalStateException is thrown if this zone is already in
	 *      the connected state (i.e. <i>connect</i> has already been called
	 *      without a corresponding call to <i>disconnect</i>)
	 *  @exception ADKException is thrown if there is a SIF Error acknowledgement
	 *      to a <code>&lt;SIF_Register&gt;</code>, <code>&lt;SIF_Subscribe&gt;</code>,
	 *      or <code>&lt;SIF_Provide&gt;</code> message as described above
	 *
	 *  @see #disconnect
	 */
	public void connect( int flags )
		throws ADKException;

	/**
	 *  Disconnects the agent from this zone.<p>
	 *
	 *  Resources held by the Class Framework, including the Agent Local Queue,
	 *  are closed. To ensure these resources are properly closed, agents should
	 *  disconnect from zones even when not planning on sending a <code>&lt;
	 *  SIF_Unregister&gt;</code> provisioning message.<p>
	 *
	 *  Provisioning messages are sent as follows:<p>
	 *
	 *  <ul>
	 *      <li>
	 *          If the agent is using ADK-managed provisioning, a <code>&lt;
	 *          SIF_Unregister&gt;</code> message is sent to the ZIS when the
	 *          ADKFlags.PROV_UNREGISTER flag is specified. When
	 *          ADK-managed provisioning is disabled, no messages are sent to
	 *          the zone.
	 *      </li>
	 *      <li>
	 *          If Agent-managed provisioning is enabled, the flags
	 *          flags have no affect. The agent must explicitly call the
	 *          sifUnregister method to manually send those message to the zone.
	 *      </li>
	 *      <li>
	 *          If ZIS-managed provisioning is enabled, no provisioning messages
	 *          are sent by the agent regardless of the flags
	 *          used and the methods are called.
	 *      </li>
	 *  </ul>
	 *  <p>
	 *
	 *  Note that SIF Agent sessions are long-lived and therefore an agent may
	 *  remain registered with a ZIS even when it is "disconnected" from the
	 *  perspective of the ADK Class Framework.<p>
	 *
	 *  Disconnecting a zone also places the agent's server queue in sleep mode.
	 *  This functionality can be disabled via the zone properties.<p>
	 *
	 *  @param flags One or more flags as described above
	 *
	 *  @exception IllegalStateException is thrown if this zone is not in the
	 *      connected state (i.e. connect has not been called, or disconnect has
	 *      already been called)
	 *  @exception ADKException is thrown if there is an error sending a
	 *      <code>&lt;SIF_Unregister&gt;</code> message
	 *
	 *  @see #connect
	 */
	public void disconnect( int flags )
		throws ADKException;

	/**
	 *  Gets a read-only list of any SIF Errors that resulted from the sending of provisioning
	 *  messages to the zone. Only access control errors (Category 4) are treated as
	 *  warnings rather than errors; all other SIF Errors result in an exception
	 *  thrown by the <code>connect</code> method
	 *
	 *  @return A List of SIFExceptions
	 *
	 */
	public List<SIFException> getConnectWarnings();

	/**
	 *  Gets the connection state of this zone<p>
	 *  @return true if the <i>connect</i> method has been called but the <i>
	 *      disconnect</i> method has not
	 */
	public boolean isConnected();

	/**
	 *  Report a SIF Event to the zone
	 *  @param event An Event object describing the SIF Data Object that has
	 *      changed and how it has changed (added, updated, or removed)
	 * @throws ADKException
	 */
	public void reportEvent( Event event )
		throws ADKException;

	/**
	 *  Report a SIF Event to the zone
	 *  @param obj The object that was added, changed, or deleted
	 *  @param actionCode  The action being taken on the object 
	 *  ( <code>ADD</code>, <code>CHANGE</code>, or <code>DELETE</code> )
	 * @throws ADKException
	 */
	public void reportEvent( SIFDataObject obj, EventAction actionCode )
		throws ADKException;
	
	/**
	 * Report a SIF Event to the zone in one or more specific SIF Contexts
	 * @param obj the object that was added, changed, or deleted
	 * @param actionCode The action being taken on the object
	 * @param contexts An array or sequence (varargs) of specific SIF Contexts to
	 * report this event in.
	 * @throws ADKException
	 */
	public void reportEvent( SIFDataObject obj, EventAction actionCode, SIFContext... contexts )
	throws ADKException;

	/**
	 *  Report a directed SIF Event to the agent in the zone identified by 
	 * 	<code>destinationId</code>. Note: Directed SIF Events may not be supported 
	 * 	by all zone integration servers.<p>
	 * 
	 * @param obj The object that was added, changed, or deleted
	 * @param action The action being taken on the object 
	 * @param destinationId The SourceId of the agent to which the SIF Event
	 * 		will be routed by the zone integration server
	 * @throws ADKException If an error occurs while reporting the event
	 */
	public void reportEvent( SIFDataObject obj, EventAction action, String destinationId )
		throws ADKException;

	/**
	 *  Query the zone.<p>
	 * 	
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 * @throws ADKException If an error occurs while sending the Query to the zone
	 */
	public String query( Query query )
		throws ADKException;
	
	/**
	 *  Query the zone and notify a MessagingListener<p>
	 * 	
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called. 
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 * @throws ADKException If an error occurs while sending the query to the zone
	 * 
	 * 	@since ADK 1.5
	 */
	public String query( Query query, MessagingListener listener )
		throws ADKException;

	/**
	 *  Query the zone with options.<p>
	 * 
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions.
	 *  @param queryOptions Reserved for future use.
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone
	 * @throws ADKException If an error occurs while sending the query to the zone
	 */
	public String query( Query query, int queryOptions )
		throws ADKException;

	/**
	 *  Query the zone with options and notify a MessagingListener<p>
	 * 
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions.
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called. 
	 *  @param queryOptions Reserved for future use.
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone
	 * 
	 * 	@since ADK 1.5
	 */
	public String query( Query query, MessagingListener listener, int queryOptions )
		throws ADKException;

	/**
	 *  Query a specific agent registered with this zone (a <i>directed query</i>).<p>
	 *
	 *  Directed queries are used primarily when the source of data is known
	 *  because of a message previously received from that agent. For example,
	 *  if your agent receives a SIF_Event and you wish to query the author of
	 *  that event for additional data, a directed query is appropriate.<p>
	 * 
	 * 	In addition, some kinds of SIF Data Objects in SIF 1.5 and later may be 
	 * 	designed to require agents to send directed queries if more than one 
	 * 	agent in a zone typically offers support for the object. This is necessary 
	 * 	because only one agent can be the authoritative provider of a given object 
	 * 	type in each zone.<p>
	 *
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 *  @param destinationId The SourceId of the agent to which the SIF Request
	 * 		will be routed by the zone integration server
	 *  @param queryOptions Reserved for future use
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 */
	public String query( Query query, String destinationId, int queryOptions )
		throws ADKException;

	/**
	 *  Query using a service a specific agent registered with this zone (a <i>directed query</i>).<p>
	 *
	 *  Directed queries are used primarily when the source of data is known
	 *  because of a message previously received from that agent. For example,
	 *  if your agent receives a SIF_Event and you wish to query the author of
	 *  that event for additional data, a directed query is appropriate.<p>
	 * 
	 * 	In addition, some kinds of SIF Data Objects in SIF 1.5 and later may be 
	 * 	designed to require agents to send directed queries if more than one 
	 * 	agent in a zone typically offers support for the object. This is necessary 
	 * 	because only one agent can be the authoritative provider of a given object 
	 * 	type in each zone.<p>
	 *
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 *  @param destinationId The SourceId of the agent to which the SIF Request
	 * 		will be routed by the zone integration server
	 *  @param queryOptions Reserved for future use
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 */
	public String invokeService( Zone zone, ServiceRequestInfo requestInfo, SIFElement payload )
		throws ADKException;


	/**
	 *  Query a specific agent registered with this zone (a <i>directed query</i>)
	 * 	and notify a MessagingListener.<p>
	 *
	 *  Directed queries are used primarily when the source of data is known
	 *  because of a message previously received from that agent. For example,
	 *  if your agent receives a SIF_Event and you wish to query the author of
	 *  that event for additional data, a directed query is appropriate.<p>
	 * 
	 * 	In addition, some kinds of SIF Data Objects in SIF 1.5 and later may be 
	 * 	designed to require agents to send directed queries if more than one 
	 * 	agent in a zone typically offers support for the object. This is necessary 
	 * 	because only one agent can be the authoritative provider of a given object 
	 * 	type in each zone.<p>
	 *
	 *  @param query A Query object describing the parameters of the query,
	 *      including optional conditions and field restrictions
	 * 	@param listener A MessagingListener that will be notified when the
	 * 		SIF_Request message is sent to the zone. Any other MessagingListeners 
	 * 		registered with the zone will also be called. 
	 *  @param destinationId The SourceId of the agent to which the SIF Request
	 * 		will be routed by the zone integration server
	 *  @param queryOptions Reserved for future use
	 * 
	 * 	@return The SIF_MsgId of the SIF_Request that was sent to the zone.
	 * @throws ADKException Thrown if an error occurs while sending the query to the zone
	 * 
	 * 	@since ADK 1.5
	 */
	public String query( Query query, MessagingListener listener, String destinationId, int queryOptions )
		throws ADKException;


	
	/**
	 *  Gets the SIF_ZoneStatus object from the ZIS managing this zone. The
	 *  method blocks until a result is received.
	 * @return The SIF_ZoneStatus object for the zone
	 * @throws ADKException If the ADK cannot connect to the zone to request
	 * the SIF_ZoneStatus object
	 */
	public SIF_ZoneStatus getZoneStatus()
		throws ADKException;

	/**
	 *  Gets the SIF_ZoneStatus object from the ZIS managing this zone. The
	 *  method blocks for the specified timeout period.
	 *  @param timeout The amount of time to wait for a SIF_ZoneStatus object to
	 *      be received by the agent (or -1 to wait infinitely)
	 * @return The SIF_ZoneStatus object for the zone
	 * @throws ADKException If the ADK cannot connect to the zone to request
	 * the SIF_ZoneStatus object
	 */
	public SIF_ZoneStatus getZoneStatus( int timeout )
		throws ADKException;

	
	/**
	 * Gets the SIF_AgentACL object from the ZIS managing this zone.
	 * @return The SIF_AgentACL specific to this agent from the zone
	 * @throws ADKException If the ADK cannot connect to the zone to request
	 * the SIF_AgentACL object
	 */
	public SIF_AgentACL getAgentACL()
		throws ADKException;
	
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
	public void addMessagingListener( MessagingListener listener );
	
	/**
	 * 	Remove a <i>MessagingListener</i> previously registered with the
	 * 	<code>addMessagingListener</code> method.<p>
	 * 
	 * 	@param listener a MessagingListener implementation
	 */
	public void removeMessagingListener( MessagingListener listener );

	/**
	 *  Purge all pending incoming and/or outgoing messages from this agent's
	 *  queue. Only messages associated with this zone are affected. See also
	 *  the Agent.purgeQueue and Topic.purgeQueue methods to purge the queues of
	 *  all zones with which the agent is connected, or all zones joined with a
	 *  given topic, respectively.<p>
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
	 * @throws ADKException Thrown if an error occurs while purging the queue
	 */
	public void purgeQueue( boolean incoming, boolean outgoing )
		throws ADKException;

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
		throws ADKException;

	/**
	 *  Wakes up this zone if currently in sleep mode.<p>
	 *
	 *  A SIF_Wakeup message is sent to the Zone Integration Server to request
	 *  that sleep mode be removed from this agent's queue. Note the ADK keeps
	 *  an internal sleep flag for each zone, which is initialized when the
	 *  <code>connect</code> method is called by sending a SIF_Ping to the ZIS.
	 *  This flag is cleared so that the ADK will no longer return a Status Code
	 *  8 ("Receiver is sleeping") in response to messages received by the ZIS.
	 *  <p>
	 *
	 *  If the SIF_Sleep message is not successful, an exception is thrown and
	 *  the ADK's internal sleep flag for this zone is not changed.
	 *  <p>
	 *
	 *  @exception ADKException thrown if the SIF_Wakeup message is unsuccessful
	 */
	public void wakeup()
		throws ADKException;

	/**
	 *  Sets the <i>UndeliverableMessageHandler</i> to be called when a dispatching
	 *  error occurs on this zone. For more information, please refer to the
	 *  UnderliverableMessageHandler class comments.<p>
	 *
	 *  @param handler The handler to call when the ADK cannot dispatch an
	 *      inbound message
	 */
	public void setErrorHandler( UndeliverableMessageHandler handler );

	/**
	 *  Gets the <i>UndeliverableMessageHandler</i> for this zone. If not set,
	 *  the error handler of the Agent is used if defined.<p>
	 * @return The class that has been set as the {@link UndeliverableMessageHandler}
	 * for this zone
	 *
	 *  @see #setErrorHandler
	 */
	public UndeliverableMessageHandler getErrorHandler();

	/**
	 *  Determines if the agent's queue for this zone is in sleep mode.<p>
	 *
	 *  @param flags When ADKFlags.LOCAL_QUEUE is specified, returns true if the
	 *      Agent Local Queue is currently in sleep mode. False is returned if
	 *      the Agent Local Queue is disabled. When ADKFlags.SERVER_QUEUE is
	 *      specified, queries the sleep mode of the Zone Integration Server
	 *      by sending a SIF_Ping message.
	 * @return True if the zone is sleeping
	 * @throws ADKException Thrown if an error occurs while checking the zone's
	 * sleep status 
	 */
	public boolean isSleeping( int flags )
		throws ADKException;

	/**
	 *  Sends a SIF_Register message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.<p>
	 * @return The SIF_Ack returned from the ZIS
	 * @throws ADKException Thrown if an error occurs while sending the sif
	 */
	public SIF_Ack sifRegister()
		throws ADKException;

	/**
	 *  Sends a SIF_Unregister message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.<p>
	 */
	public SIF_Ack sifUnregister()
		throws ADKException;

	/**
	 *  Sends a SIF_Subscribe message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifSubscribe( String[] objectType )
		throws ADKException;

	/**
	 *  Sends a SIF_Unsubscribe message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifUnsubscribe( String[] objectType )
		throws ADKException;

	/**
	 *  Sends a SIF_Provide message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifProvide( String[] objectType )
		throws ADKException;

	/**
	 *  Sends a SIF_Unprovide message to the ZIS. This method can be called by
	 *  agents that have chosen to use Agent-managed provisioning. If ZIS-managed
	 *  or ADK-managed provisioning is enabled for this zone, the method has no
	 *  effect.
	 */
	public SIF_Ack sifUnprovide( String[] objectType )
		throws ADKException;

	/**
	 *  Sends a SIF_Ping message to the ZIS that manages this zone.
	 */
	public SIF_Ack sifPing()
		throws ADKException;

	/**
	 *  Sends arbitrary SIF_Message content to the zone. This method does not
	 *  alter the message or wrap it in an envelope prior to sending it.<p>
	 *
	 *  @param xml A valid SIF_Message complete with a SIF_Header header and a
	 *      payload such as SIF_Register, SIF_Request, SIF_Event, etc.
	 *  @return A SIF_Ack object encapsulating the SIF_Ack response that was
	 *      returned from the Zone Integration Server
	 */
	public SIF_Ack sifSend( String xml )
		throws ADKException;
	
	/**
	 *  Sends arbitrary SIF_Message content to the zone. This method does not
	 *  alter the message or wrap it in an envelope prior to sending it.<p>
	 *
	 *  @param xml A valid SIF_Message complete with a SIF_Header header and a
	 *      payload such as SIF_Register, SIF_Request, SIF_Event, etc.
	 *  @return A SIF_Ack object encapsulating the SIF_Ack response that was
	 *      returned from the Zone Integration Server
	 */
	public SIF_Ack sifCancelRequests (String destinationId, String[] sif_MsgIds)
		throws ADKException;

	/**
	 *  Assigns an application-supplied object to this Zone
	 *  @param data Any object the application wishes to attach to this Zone instance
	 */
	public void setUserData( Object data );

	/**
	 *  Gets the application-supplied object for this Zone
	 *  @return The object passed to the <code>setUserData</code> method
	 */
	public Object getUserData();

	/**
	 *  Gets the Log4j Logger for this zone.
	 */
	public Logger getLog();
	
	/**
	 * 	Gets the ServerLog for this zone.<p>
	 * 	@return The ServerLog instance for the zone
	 *  @since ADK 1.5
	 */
	public ServerLog getServerLog();
	
	/**
	 *  Returns the string representation of this zone as "zoneId@zoneUrl"
	 */
	public String toString();
	
	public void setServiceProviders(String serviceName, SIFZoneService sifZoneService);
	public void setServiceSubscribers(String serviceName, SIFZoneServiceProxy sifZoneServiceProxy);
	public void reportServiceEvent(ServiceEvent event, String destinationId) throws ADKException;
	public void setServiceNotifier(String serviceName, SIFZoneServiceProxy sifZoneServiceProxy);
}
