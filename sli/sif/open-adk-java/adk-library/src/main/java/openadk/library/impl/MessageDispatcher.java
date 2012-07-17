//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.ADKMessagingException;
import openadk.library.ADKNotSupportedException;
import openadk.library.ADKParsingException;
import openadk.library.ADKQueueException;
import openadk.library.ADKTransportException;
import openadk.library.ADKZoneNotConnectedException;
import openadk.library.AgentProperties;
import openadk.library.Condition;
import openadk.library.DataObjectOutputStream;
import openadk.library.ElementDef;
import openadk.library.Event;
import openadk.library.MessageInfo;
import openadk.library.MessagingListener;
import openadk.library.Publisher;
import openadk.library.Query;
import openadk.library.QueryResults;
import openadk.library.RawMessageListener;
import openadk.library.ReportObjectOutputStream;
import openadk.library.ReportPublisher;
import openadk.library.RequestInfo;
import openadk.library.SIFContext;
import openadk.library.SIFDTD;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;
import openadk.library.SIFErrorCategory;
import openadk.library.SIFErrorCodes;
import openadk.library.SIFException;
import openadk.library.SIFMessageInfo;
import openadk.library.SIFMessagePayload;
import openadk.library.SIFMessagingListener;
import openadk.library.SIFParser;
import openadk.library.SIFStatusCodes;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.Subscriber;
import openadk.library.Topic;
import openadk.library.TrackQueryResults;
import openadk.library.UndeliverableMessageHandler;
import openadk.library.Zone;
import openadk.library.infra.AuthenticationLevel;
import openadk.library.infra.EncryptionLevel;
import openadk.library.infra.InfraDTD;
import openadk.library.infra.SIF_Ack;
import openadk.library.infra.SIF_Body;
import openadk.library.infra.SIF_CancelRequests;
import openadk.library.infra.SIF_Error;
import openadk.library.infra.SIF_Event;
import openadk.library.infra.SIF_EventObject;
import openadk.library.infra.SIF_ExtendedQueryResults;
import openadk.library.infra.SIF_GetMessage;
import openadk.library.infra.SIF_Header;
import openadk.library.infra.SIF_LogEntry;
import openadk.library.infra.SIF_LogEntryHeader;
import openadk.library.infra.SIF_ObjectData;
import openadk.library.infra.SIF_Query;
import openadk.library.infra.SIF_QueryObject;
import openadk.library.infra.SIF_Request;
import openadk.library.infra.SIF_RequestMsgId;
import openadk.library.infra.SIF_Response;
import openadk.library.infra.SIF_SecureChannel;
import openadk.library.infra.SIF_Security;
import openadk.library.infra.SIF_ServiceInput;
import openadk.library.infra.SIF_ServiceNotify;
import openadk.library.infra.SIF_ServiceOutput;
import openadk.library.infra.SIF_Status;
import openadk.library.infra.SIF_SystemControl;
import openadk.library.infra.SIF_SystemControlData;
import openadk.library.infra.SIF_Version;
import openadk.library.infra.SIF_ZoneStatus;
import openadk.library.policy.PolicyManager;
import openadk.library.reporting.ReportingDTD;
import openadk.library.services.SIFZoneService;
import openadk.library.services.SIFZoneServiceProxy;
import openadk.library.services.impl.ServiceOutputFileStream;
import openadk.library.services.impl.ServiceOutputStreamImpl;
import openadk.util.ADKStringUtils;
import openadk.util.GUIDGenerator;


/**
 *  Handles message dispatching within the class framework.<p>
 *
 *  There is a MessageDispatcher object for each zone. It is protocol-independent:
 *  the MessageDispatcher works in conjunction with either the zone's protocol
 *  handler (IProtocolHandler) or its Agent Local Queue (IAgentQueue) to produce
 *  and consume messages, but never participates in the actual sending or
 *  receiving of messages on the wire. Conversely, the protocol handler and
 *  queue never dispatch messages; they are only concerned with sending and
 *  receiving them.
 *  <p>
 *
 *  Message dispatching is at the heart of the class framework and the most
 *  complex implementation class because of the multiple ways in which an agent
 *  can exchange messages with a ZIS -- namely, Push and Pull modes, Selective
 *  Message Blocking, and optional Agent Local Queue (with negates the need for
 *  SMB when enabled). Further complicating matters is the fact that an agent
 *  may be connected to multiple zones, some of which may be using Push mode
 *  while others are using Pull mode.
 *  <p>
 *
 *  <b>Message Acknowledgement</b><p>
 *
 *  SIF guarantees the delivery (and hopefully the processing) of a message by
 *  requiring that it remain in its queue until acknowledged by the recipient.
 *  An agent should only acknowledge a message after it has processed it, so the
 *  ADK does not send acknowledgements when a message is received but rather
 *  after it has been successfully dispatched without exception. In other words,
 *  a message is not acknowledged simply because it was received successfully;
 *  it must be processed by the agent first. This is an important distinction
 *  to keep in mind and critical to the operation of MessageDispatcher.
 *  <p>
 *
 *  <b>Message Consumption</b><p>
 *
 *  Throughout the rest of this commentary, ALQ refers to "Agent Local Queue"
 *  and PH refers to "Protocol Handler".<p>
 *
 *  MessageDispatcher consumes messages from one of two sources: either the ALQ
 *  or a zone's PH. When the ALQ is enabled, the PH stores messages in the queue
 *  as they are received from the network. It immediately acknowledges them
 *  because they are safely persisted in the ALQ and will remain there even if
 *  the agent goes down. Further, Selective Message Blocking is not needed when
 *  the ALQ is enabled, so there is no reason to ever send anything but an
 *  Immediate acknowledgement to the originating zone.<p>
 *
 *  When the ALQ is disabled, however, messages must be dispatched and processed
 *  before the PH can return an acknowledgement to the zone. This means a full
 *  cycle through the class framework from the time a message is received by
 *  the PH until the time it has been processed and a SIF_Ack is returned. To
 *  complicate matters, an agent may need to invoke Selective Message Blocking
 *  because the ALQ is not available, and in this case the framework may need
 *  to return an Intermediate ack before the agent has had a chance to fully
 *  process the message.<p>
 *
 *  Thus, when ALQ is enabled MessageDispatcher runs in a thread to consume
 *  messages from the queue as follows:<p>
 *
 *  <ul>
 *      <li>Waits for the next message to become available (blocks)</li>
 *      <li>Dispatches the message to the appropriate Subscriber, Publisher,
 *          or QueryResults object</li>
 *      <li>If an exception is thrown during the dispatch call, the message is
 *          left in the ALQ and the process repeats</li>
 *      <li>If the dispatch call succeeds, the message is permanently removed
 *          from the ALQ</li>
 *  </ul>
 *
 *  Remember, each message in the ALQ was acknowledged at the time it was placed
 *  into the queue by the PH so the above algorithm never sends SIF_Ack messages.
 *  It is purely concerned with dispatching messages waiting in the queue.<p>
 *
 *  When ALQ is disabled MessageDispatcher does not run in a thread. Rather
 *  than <i>consume</i> messages, it is handed them by the PH for synchronous
 *  processing as they're received from the network. This process works as
 *  follows:<p>
 *
 *  <ul>
 *      <li>The PH receives a message and calls MessageDispatcher.dispatch
 *          to dispatch it</li>
 *      <li>The message is dispatched to the appropriate Subscriber, Publisher,
 *          or QueryResults object</li>
 *      <li>If an exception is thrown during the dispatch call, the exception
 *          is propagated up the call stack to the PH, which will respond by
 *          simply not sending an acknowledgement. Thus, the message is left in
 *          the agent queue on the zone server.</li>
 *      <li>If the dispatch call succeeds, it returns a status code 1, 2, or 3
 *          to the PH and the PH sends an acknowledgement with that status code.
 *          These codes corresponding to the Immediate, Intermediate, and Final
 *          acknowledgement types. Status code 2 (Intermediate) is only returned
 *          if the message was dispatched to a Subscriber. Status code 3 (Final)
 *          is only returned if the message was dispatched to a QueryResults
 *          object in response to an earlier SIF_Event that invoked Selective
 *          Message Blocking. Status code 1 (Immediate) is returned in all other
 *          cases. Note MessageDispatcher <b>does not</b> keep track of SMB
 *          state; this is the job of the dispatch recipient.</li>
 *  </ul>
 *
 *  <b>Message Production</b><p>
 *
 *  MessageDispatcher also "produces" messages on behalf of Topic and Zone
 *  objects when posting outgoing SIF_Event, SIF_Request, and SIF_Response
 *  messages (these are the only outgoing infrastructure messages that pass
 *  through MessageDispatcher; all others are immediately handed to the PH for
 *  synchronous delivery). Like message consumption, the process of sending
 *  messages depends on whether or not the ALQ is enabled as well as the type of
 *  message being sent.<p>
 *
 *  When the ALQ is enabled, SIF_Event messages are immediately stored in the
 *  queue and will eventually be sent to the appropriate PH by a worker thread
 *  that is built into the ALQ. This ensures that regardless of whether the
 *  agent goes down or not, the events it has generated are guaranteed to make
 *  their way to the ZIS eventually. SIF_Request and SIF_Response messages are
 *  not handled in this way; rather, they are sent synchronously in the same
 *  way as SIF_Events are sent when the ALQ is disabled.<p>
 *
 *  When the ALQ is disabled, SIF_Event, SIF_Request, and SIF_Response messages
 *  are immediately handed to the PH of each destination zone. If an exception
 *  occurs before the message is acknowledged by the ZIS, it will propagate up
 *  the call stack to the agent code that originally initiated the message (e.g.
 *  to a Topic.publishEvent call). An agent can either retry the operation by
 *  calling the same method a second time, or can abandon the transaction
 *  altogether.<p>
 *
 *  <b>Push vs. Pull Mode</b><p>
 *
 *  Push and Pull mode have no effect on MessageDispatcher, its interfaces, or
 *  its logic. When Push mode is active for a zone, the PH will receive incoming
 *  messages as they're pushed by the ZIS. Those messages will then be handled
 *  as described in "Message Consumption" above. When Pull mode is active for a
 *  zone, the PH runs as a thread to periodically get messages from the ZIS. In
 *  short, MessageDispatcher does not care how messages were obtained. It works
 *  consistently in both modes.<p>
 *
 *  <b>Selective Message Blocking</b><p>
 *
 *  The ADK addresses Selective Message Blocking by forcing agents to use the
 *  TrackQueryResults class to perform queries while processing SIF_Events. (If
 *  an agent attempts to call a query method while a SIF_Event is being
 *  dispatched for a given zone, an exception is thrown.)  TrackQueryResults
 *  houses all of the logic for Selective Message Blocking. When the ALQ is
 *  enabled, TrackQueryResults does not invoke SMB; instead it draws upon the
 *  ALQ for SIF_Responses. When the ALQ is disabled, TrackQueryResults keeps
 *  a tab of which messages it has sent Intermediate acknowledgements for and
 *  will eventually need to send Final acknowledgements for. To accomplish this
 *  it works closely with MessageDispatcher so that when the dispatching of a
 *  given SIF_Event has ended the TrackQueryResults object is asked to send its
 *  pending Final acknowledgements. For more details on this consult that
 *  class.
 *
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class MessageDispatcher implements Runnable
{
	protected Object fRunning;
	protected IAgentQueue fQueue;
	protected ZoneImpl fZone;
	protected String fSourceId;
	protected SIFParser fParser;
	protected boolean fKeepMsg;
	protected boolean fAckAckOnPull;
	protected Hashtable fEvDispCache;
	protected RequestCache fRequestCache;
	private final MessageIdCache msgIdCache = new MessageIdCache();
	
	/**
	 *  Constructs a MessageDispatcher for a zone
	 */
    public MessageDispatcher( ZoneImpl zone ) throws ADKException
	{
		fRequestCache = RequestCache.getInstance( zone.getAgent() );

		fZone = zone;
		fQueue = zone.fQueue;
		if( fQueue != null ) {
			if( !fQueue.isReady() )
				throw new ADKQueueException("Agent Queue is not ready for agent \""+zone.getAgent().getId()+"\" zone \""+zone.getZoneId()+"\"",fZone);
			new Thread(this,zone.getAgent().getId()+"@"+zone.getZoneId()+".MessageDispatcher").start();
		}

		fSourceId = zone.getAgent().getId();
		fKeepMsg = zone.getAgent().getProperties().getKeepMessageContent();
		fAckAckOnPull = zone.getAgent().getProperties().getPullAckAck();

		try
		{
			fParser = SIFParser.newInstance();
		}
		catch( ADKException adke )
		{
			throw new InternalError(adke.toString());
		}
    }

	/**
	 *  Find the QueryResults object for a zone by searching up the message
	 *  dispatching chain until a Zone, Topic, or Agent is found with a registered
	 *  QueryResults implementation.
	 *
	 *  @param rsp The SIF_Response message (if a SIF_Response was received).
	 *      Either rsp or req must be specified, but not both.
	 *  @param req The SIF_Request message (if a SIF_Request is being sent).
	 *      Either rsp or req must be specified, but not both.
	 *  @param query Only applicable when <i>req</i> is non-null: The Query
	 *      associated with the SIF_Request
	 *  @param zone The Zone to begin the search at
	 */
	protected QueryResults getQueryResultsTarget( SIF_Response rsp, SIF_Request req, ElementDef objType, Query query, Zone zone )
		throws SIFException
	{
		//
		//  - First check TrackQueryResults for a matching pending request
		//  - Next check the Topic, the Zone, and finally the Agent. The
		//    message is dispatched to the first one that results a
		//    QueryResults object
		//
		QueryResults target = null;
		SIFContext context = null;

		if( req != null )
		{
			//  First check TrackQueryResults
			TrackQueryResults tracker = (TrackQueryResults)TrackQueryResultsImpl.sRequestQueries.get(query);
			if( tracker != null ) {
				TrackQueryResultsImpl.sRequestQueries.remove(query);
				target = tracker;
			}
			else
			{
				SIF_Query q = req.getSIF_Query();
				if( q == null )
					throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
						SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
						"SIF_Request message missing mandatory element",
						"SIF_Query is required", fZone );

				SIF_QueryObject qo = q.getSIF_QueryObject();
				if( qo == null )
					throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
						SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
						"SIF_Request message missing mandatory element",
						"SIF_QueryObject is required", fZone );

				objType = ADK.DTD().lookupElementDef( qo.getObjectName() );
				if( objType == null )
					throw new SIFException(
						SIFErrorCategory.REQUEST_RESPONSE,
						SIFErrorCodes.REQRSP_INVALID_OBJ_3,
						"Agent does not support this object type",
						qo.getObjectName(), fZone );
			}

			// Check to see if the Context is supported
			// TODO: Determine if a SIFException should be thrown at this point?
			try
			{
				context = req.getSIFContexts().get( 0 );
			}
			catch( ADKNotSupportedException contextNotSupported ){
				throw new SIFException(
						SIFErrorCategory.GENERIC,
						SIFErrorCodes.GENERIC_CONTEXT_NOT_SUPPORTED_4,
						contextNotSupported.getMessage(), fZone );
			}

		}
		else if( rsp != null )
		{
			//  First check TrackQueryResults object to see if it is expecting
			//  to be called for this SIF_Response
			String reqId = rsp.getSIF_RequestMsgId();
			TrackQueryResults tracker = (TrackQueryResults)TrackQueryResultsImpl.sRequestMsgIds.get( reqId );
			if( tracker != null )
			{
				//  Dispatch to the TrackQueryResults object
				target = tracker;
			}

			//	Check to see if the Context is supported
			// TODO: Determine if a SIFException should be thrown at this point?
			try
			{
				context = rsp.getSIFContexts().get( 0 );
			}
			catch( ADKNotSupportedException contextNotSupported ){
				throw new SIFException(
						SIFErrorCategory.GENERIC,
						SIFErrorCodes.GENERIC_CONTEXT_NOT_SUPPORTED_4,
						contextNotSupported.getMessage(), fZone );
			}

		}
		else
			throw new IllegalArgumentException("A SIF_Request or SIF_Response object must be passed to getQueryResultsTarget");

		if( target == null )
		{
			//  Try the Topic... (if the context is default )
			TopicImpl topic = (TopicImpl)fZone.getAgent().getTopicFactory().lookupInstance( objType, context );
			if( topic != null ){
				target = topic.fQueryResults;
			}

			if( target == null )
			{
				//  Next try the Zone...
				target = fZone.getQueryResults( context, objType);
			}
			if( target == null )
			{
				//  Finally, try the Agent...
				target = fZone.getAgent().getQueryResults( context, objType );
			}
		}

		return target;
	}


	/**
	 *  Find the MessagingListenerImpl objects to notify when a message is
	 * 	received or sent on this zone.
	 */
	protected static List<MessagingListener> getMessagingListeners( ZoneImpl zone )
	{
		List<MessagingListener> v = new ArrayList<MessagingListener>();

		//	Contribute the Zone's listeners to the group
		v.addAll( zone.fMessagingListeners );

		//	Contribute the Agent's listeners to the group
		v.addAll( zone.getAgent().getMessagingListeners() );

		return v;
	}

	/**
	 *  Dispatch a message.<p>
	 *
	 *  @param msg The infrastructure message to dispatch
	 */
	public int dispatch( SIFMessagePayload msg )
		throws SIFException,
			   ADKMessagingException,
			   ADKException,
			   LifecycleException
	{
		String errTyp = null;
		List<MessagingListener> msgList = null;
		int status = 1;

		try
		{
			byte pload = ADK.DTD().getElementType(msg.getElementDef().name());

			if( pload == SIFDTD.MSGTYP_SYSTEMCONTROL ) {
				SIFElement[] ch = ((SIF_SystemControl)msg).getSIF_SystemControlData().getChildren();

				if( ch != null && ch.length > 0 ) {
					if( ch[0].getElementDef() == InfraDTD.SIF_SLEEP ) {
						fZone.execSleep();
					}
					else if( ch[0].getElementDef() == InfraDTD.SIF_WAKEUP ) {
						fZone.execWakeup();
					}
					else if( ch[0].getElementDef() == InfraDTD.SIF_PING ) {
						//	Notify MessagingListeners...
						msgList = getMessagingListeners( fZone );
						if( msgList != null && msgList.size() > 0 )
						{
							SIFMessageInfo msginfo = new SIFMessageInfo( msg,fZone );
							for( Iterator vi = msgList.iterator(); vi.hasNext(); ) {
								((MessagingListener)vi.next()).onMessageProcessed( SIFMessagingListener.SIF_SYSTEM_CONTROL, msginfo );
							}
						}

						if( fZone.isSleeping( ADKFlags.QUEUE_LOCAL ) )
							return 8;

						return 1;
					}
					else if( ch[0].getElementDef() == InfraDTD.SIF_CANCELREQUESTS ) {
						// Remove requests from cache - JEN
						SIF_CancelRequests cancelRequests = (SIF_CancelRequests)ch[0];
						for (SIF_RequestMsgId sif_RequestMsgId : cancelRequests.getSIF_RequestMsgIds()) {
							RequestInfo requestInfo = fRequestCache.getRequestInfo(sif_RequestMsgId.getValue(), null);
							if (requestInfo == null)
								fZone.log.warn( "Agent can not cancel request " + sif_RequestMsgId.getValue() );
						}
						
						return 1;
					}
					else {
						fZone.log.warn( "Received unknown SIF_SystemControlData: " + ch[0].tag() );

						throw new SIFException(
							SIFErrorCategory.XML_VALIDATION,
							SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
							"SIF_SystemControlData must contain SIF_Ping, SIF_Wakeup, or SIF_Sleep",
							fZone );
				    }

					//	Notify MessagingListeners...
					msgList = getMessagingListeners( fZone );
					if( msgList != null && msgList.size() > 0 ) {
						SIFMessageInfo msginfo = new SIFMessageInfo(msg,fZone);
						for( Iterator vi = msgList.iterator(); vi.hasNext(); ) {
							((MessagingListener)vi.next()).onMessageProcessed( SIFMessagingListener.SIF_SYSTEM_CONTROL, msginfo );
						}
					}
				}

				return status;
			}

			//  If zone is asleep, return status code
			if( fZone.isSleeping( ADKFlags.QUEUE_LOCAL ) )
				return SIFStatusCodes.SLEEPING_8;
			
			// do not allow duplicate message into dispatch logic
			String msgId = msg.getMsgId();
			if (msgIdCache.containsKey(msgId)) {
			    return SIFStatusCodes.DUPLICATE_MESSAGE_7;
			} else {
			    msgIdCache.put(msgId, msgId);
			}

			//  Some agents don't want to receive messages - for example, the
			//  SIFSend ADK Example agent. This is very rare but we offer a property
			//  to allow for it
			if( fZone.getProperties().getDisableMessageDispatcher() )
				return status;
			else if (msg instanceof SIF_ServiceInput) {
				dispatchServiceRequest((SIF_ServiceInput)msg);
			}
			else if (msg instanceof SIF_ServiceOutput) {
				dispatchServiceResponse((SIF_ServiceOutput)msg);
			}
			else if (msg instanceof SIF_ServiceNotify) {
				dispatchServiceNotify((SIF_ServiceNotify)msg);
			}
			else {
				switch( pload )
				{
					case SIFDTD.MSGTYP_EVENT:
						errTyp = "Subscriber.onEvent";
						status = dispatchEvent( (SIF_Event)msg );
						break;
					case SIFDTD.MSGTYP_REQUEST:
						errTyp = "Publisher.onRequest";
						dispatchRequest( (SIF_Request)msg );
						break;
					case SIFDTD.MSGTYP_RESPONSE:
						errTyp = "QueryResults";
						dispatchResponse( (SIF_Response)msg );
						break;
	
					default:
						fZone.log.warn( "Agent does not know how to dispatch " + msg.getElementDef().name() + " messages" );
						throw new SIFException(
							SIFErrorCategory.GENERIC,
							SIFErrorCodes.GENERIC_MESSAGE_NOT_SUPPORTED_2,
							"Message not supported",
							msg.getElementDef().name(),
							fZone );
				}
			}
		}
		catch( LifecycleException le )
		{
			throw le;
		}
		catch( SIFException se )
		{
			msgIdCache.remove(msg.getMsgId());
			//	Check if ADKException.setRetry() was called; use transport
			//	error category to force ZIS to resend message
			if( se.getRetry() ) {
				se.setErrorCategory( SIFErrorCategory.TRANSPORT );
				se.setErrorCode( SIFErrorCodes.WIRE_GENERIC_ERROR_1 );
			}
			logAndRethrow("SIFException in " + errTyp + " message handler for " + msg.getElementDef().name(), se);
		}
		catch( ADKZoneNotConnectedException adkznce ){
			msgIdCache.remove(msg.getMsgId());
			// Received a message while the zone was disconnected. Return a system transport
			// error so that the message is not removed from the queue
			SIFException sifEx = new SIFException(
					SIFErrorCategory.TRANSPORT,
					SIFErrorCodes.WIRE_GENERIC_ERROR_1,
					adkznce.getMessage(), fZone );
			logAndRethrow( "Message received while zone is not connected",  sifEx );
		}
		catch( ADKException adke )
		{
			msgIdCache.remove(msg.getMsgId());
			//	Check if ADKException.setRetry() was called; use transport
			//	error category to force ZIS to resent message
			if( adke.getRetry() ) {
				logAndThrowRetry(adke.getMessage(), adke);
			}
			logAndThrowSIFException("ADKException in " + errTyp + " message handler for " + msg.getElementDef().name(), adke);
		}
		catch( Throwable uncaught )
		{
			msgIdCache.remove(msg.getMsgId());
			logAndThrowSIFException("Uncaught exception in " + errTyp + " message handler for " + msg.getElementDef().name(), uncaught);
		}

		return status;
	}

	/*
	 * 
	 */
	private void dispatchServiceNotify(SIF_ServiceNotify msg) throws ADKException {
		// TODO Complete method stub
		ADK.getLog().info("Dispatch Service Notify called with " + msg.toString());
		ArrayList<SIFZoneServiceProxy> targets =  fZone.getServiceNotifiers(msg.getSIF_Service());
		if (targets != null) {
			SIF_Body sifBody = msg.getSIF_Body();
			//SIFDataObject[] dataObjects = new SIFDataObject[rsp.getChildCount()];
			ArrayList<SIFDataObject> list = new ArrayList<SIFDataObject>();
			for (SIFElement element : sifBody.getChildList()) {
				if (element instanceof SIFDataObject)
					list.add((SIFDataObject) element);
			}
			
			//SIFPullParser parser = new SIFPullParser();
			//SIFElement sifElement = parser.parse(xmlData);

			DataObjectInputStreamImpl data = DataObjectInputStreamImpl.newInstance();
			SIFDataObject[] wua = new SIFDataObject[list.size()];
			for (int i = 0; i < list.size(); ++i)
				wua[i] = list.get(i);
			
			data.setData(wua);
			MessageInfo info = new SIFMessageInfo(msg,fZone);
			
			for (SIFZoneServiceProxy target : targets)
				target.onServiceEvent(data, msg.getSIF_Error(), fZone, info);
		}

	}

	/**
	 *  Dispatch a SIF_Event.<p>
	 *
	 *  <b>When ALQ Disabled:</b> Dispatching of this event is handled in a
	 *  separate EvDisp thread in case SMB is invoked. This makes it possible
	 *  to asynchronously return a SIF_Ack code to the dispatchEvent() caller
	 *  before the handling of the event by the Subscriber is completed. The
	 *  EvDisp also tracks the internal dispatch state of this particular message.
	 *  The Topic matching the object type is then notified via its Subscriber's
	 *  onEvent method. If a TrackQueryResults object is created within that
	 *  method, its constructor will wakeup the EvDisp thread, instructing it to
	 *  return a value of 2 (Intermediate). If no TrackQueryResults object is
	 *  instantiated during the Subscriber.onEvent method, the EvDisp thread
	 *  returns a 1 (Immediate) status code upon completion.<p>
	 *
	 *  <b>When ALQ Enabled:</b> Dispatching is immediate. The Topic matching
	 *  the object type is then notified via its Subscriber's onEvent method,
	 *  then processing ends. No EvDisp thread is needed because if a
	 *  TrackQueryResults is used it will draw upon the ALQ instead of invoking
	 *  SMB on the zone server.<p>
	 *
	 *  Note if an exception is thrown at any time during the processing of a
	 *  SIF_Event, it is propagated up the call stack. The PH must not return a
	 *  SIF_Ack for the message; the ALQ must not delete the message from its
	 *  queue.<p>
	 *
	 */
	protected int dispatchEvent( SIF_Event sifEvent )
		throws ADKException
	{
		if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
			fZone.log.debug( "Dispatching SIF_Event ("+sifEvent.getMsgId()+")..." );

		//  Was this event reported by this agent?
		if( !fZone.getProperties().getProcessEventsFromSelf() &&
			sifEvent.getHeader().getSIF_SourceId().equals( fZone.getAgent().getId() ) )
		{
			if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
	    		fZone.log.debug( "SIF_Event ignored because it was originally reported by this agent (see the adk.messaging.processEventsFromSelf property)" );

			return 1;
		}

		SIF_ObjectData odata = sifEvent.getSIF_ObjectData();
		if( odata == null )
			throw new SIFException(
				SIFErrorCategory.XML_VALIDATION,
				SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
				"SIF_Event message missing mandatory element",
				"SIF_ObjectData is a required element", fZone );

		//
		//  Loop through all SIF_EventObjects inside this SIF_Event and dispatch
		//  to corresponding topics
		//
		SIF_EventObject eventObj = odata.getSIF_EventObject();
		if( eventObj == null )
			throw new SIFException(
					SIFErrorCategory.XML_VALIDATION,
				SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
				"SIF_Event message missing mandatory element",
				"SIF_ObjectData/SIF_EventObject is a required element", fZone );

		int ackCode=1;
		int thisCode=1;

		SIFMessageInfo msgInfo = new SIFMessageInfo( sifEvent, fZone );

		ElementDef typ = ADK.DTD().lookupElementDef( eventObj.getObjectName() );
		if( typ == null )
		{
			//  SIF Data Object type not supported
			throw new SIFException(
				SIFErrorCategory.EVENTS,
				SIFErrorCodes.EVENT_INVALID_EVENT_3,
				"Agent does not support this object type",
				eventObj.getObjectName(), fZone );
		}

		// TODO: For now, the ADK only routes SIF Events to the first context
		// in the event. This needs to be implemented to support
		// events in multiple contexts
		SIFContext eventContext = msgInfo.getSIFContexts()[0];
		Subscriber target = null;
		Topic topic = null;
		//
		//  Lookup the Topic for this SIF object type
		// Topics are only used for the SIF Default context
		//
		topic = fZone.getAgent().getTopicFactory().lookupInstance( typ, eventContext );
		if( topic != null ){
			target = topic.getSubscriber();
		}

		if( target == null )
		{
			//  Is a Subscriber registered with the Zone?
			target = fZone.getSubscriber( eventContext, typ );
			if( target == null )
			{
				//  Is a Subscriber registered with the Agent?
				target = fZone.getAgent().getSubscriber( eventContext, typ );
				if( target == null )
				{
					//
					//  No Subscriber message handler found. Try calling the Undeliverable-
					//  MessageHandler for the zone or agent. If none is registered,
					//  return an error SIF_Ack indicating the object type is not
					//  supported.
					//
					boolean handled = false;
					UndeliverableMessageHandler errHandler = fZone.getErrorHandler();
					if( errHandler != null )
					{
						handled = errHandler.onDispatchError( sifEvent, fZone, msgInfo );

						//	Notify MessagingListeners...
						List<MessagingListener> mList = getMessagingListeners( fZone );
						for( MessagingListener ml : mList ){
							ml.onMessageProcessed( SIFMessagingListener.SIF_EVENT, msgInfo );
						}
					}

					if( !handled )
					{
						fZone.log.warn( "Received a SIF_Event (" + sifEvent.getMsgId() + "), but no Subscriber object is registered to handle it" );

						throw new SIFException(
							SIFErrorCategory.EVENTS,
							SIFErrorCodes.EVENT_INVALID_EVENT_3,
							"Agent does not support this object type",
							eventObj.getObjectName(), fZone );
					}

					return 1;
				}
			}
		}

		//
		//  Call Subscriber.onEvent with the event data
	    //
		SIFElement[] arr = eventObj.getChildren();
		SIFDataObject[] data = new SIFDataObject[arr.length];
		for( int x = 0; x < arr.length; x++ ){
			data[x] = (SIFDataObject)arr[x];
		}

		//  Wrap in an Event object
		DataObjectInputStreamImpl dataStr = DataObjectInputStreamImpl.newInstance();
		dataStr.setData( data );
		Event adkEvent = new Event(dataStr, eventObj.getAction(), data[0].getElementDef());
		adkEvent.setZone(fZone);
		adkEvent.setContexts( new SIFContext[] { eventContext} );

		if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
			fZone.log.debug( "SIF_Event contains " + data.length + " " + eventObj.getObjectName() + " objects (" + eventObj.getAction() + ")" );

		if( fQueue == null )
		{
			if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
				fZone.log.debug( "Dispatching SIF_Event to Subscriber message handler via EvDisp" );

			//
			//  -- No ALQ available --
			//  Dispatch in a separate EvDisp thread. Block until an ack
			//  status code is available, then return it
			//
			EvDisp disp = null;
			try {
				disp = checkoutEvDisp(adkEvent);
				disp.dispatch(target,adkEvent,fZone,topic,msgInfo);
				thisCode = disp.waitForAckCode();
			} finally {
				checkinEvDisp(adkEvent);
			}
		}
		else
		{
			if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
				fZone.log.debug( "Dispatching SIF_Event to Subscriber message handler" );

			//
			//  -- ALQ is available --
			//  Dispatch immediately.
			//
			try
			{
				target.onEvent(adkEvent,fZone,msgInfo);
			}
			catch( SIFException sifEx )
			{
				throw sifEx;
			}
			catch( Throwable thr )
			{
				throw new SIFException(
					SIFErrorCategory.EVENTS,
					SIFErrorCodes.EVENT_GENERIC_ERROR_1,
					"Error processing SIF_Event",
					"Exception in Subscriber.onEvent message handler: " + ADKStringUtils.getStackTrace(thr),
					fZone );
			}

			thisCode = 1;
		}

		if( thisCode > ackCode )
			ackCode = thisCode;

		if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
			fZone.log.debug( "SIF_Event (" + sifEvent.getMsgId() + ") dispatching returning SIF_Ack status "+ackCode );

		return ackCode;
	}

	/**
	 *  Dispatch a SIF_Response.<p>
	 *
	 *  SIF_Response messages are dispatched as follows:
	 *
	 *  <ul>
	 *      <li>
	 *          If a TrackQueryResults object issued the original SIF_Request
	 *          during this agent session (i.e. the agent process has not
	 *          terminated since the SIF_Request was issued), the response is
	 *          dispatched to that TrackQueryResults object via its QueryResults
	 *          interface.
	 *      </li>
	 *      <li>
	 *          If a Topic exists for the data type associated with the
	 *          SIF_Response, it is dispatched to the QueryResults object
	 *          registered with that Topic.
	 *      </li>
	 *      <li>
	 *          If no Topic exists for the data type associated with the
	 *          SIF_Response, it is dispatched to the QueryResults object
	 *          registered with the Zone from which the SIF_Response was
	 *          received.
	 *      </li>
	 *  </ul>
	 *
	 *  <b>SIF_ZoneStatus</b> is handled specially. When Zone.awaitingZoneStatus
	 *  returns true, the agent is blocking on a call to Zone.getZoneStatus().
	 *  In this case, the SIF_ZoneStatus object is routed directly to the Zone
	 *  object instead of being dispatched via the usual QueryResults mechanism.
	 *  <p>
	 */
	protected void dispatchResponse( SIF_Response rsp )
		throws ADKException
	{
		//	block thread until Zone.query() has completed in case it is in the
		//	midst of a SIF_Request. This is done to ensure that we don't receive
		//	the SIF_Response from the zone before the ADK and agent have finished
		//	with the SIF_Request in Zone.query()
		fZone.waitForRequestsToComplete();
		boolean retry = false;
		RequestInfo reqInfo = null;
		ADKException cacheErr = null;

		try
		{
			try
			{
				reqInfo = fRequestCache.lookupRequestInfo( rsp.getSIF_RequestMsgId(), fZone );
			}
			catch( ADKException adke )
			{
				cacheErr = adke;
			}

//			if( BuildOptions.PROFILED ) {
//				if( reqInfo != null ) {
//					ProfilerUtils.profileStart( String.valueOf( openadk.profiler.api.OIDs.ADK_SIFRESPONSE_REQUESTOR_MESSAGING ), ADK.DTD().lookupElementDef( reqInfo.getObjectType() ), rsp.getMsgId() );
//				}
//			}



			SIF_ObjectData od = rsp.getSIF_ObjectData();
			List<SIFElement> elementList = null;
			SIFElement firstChild = null;
			if( od != null ){
				elementList = od.getChildList();
				if( elementList.size() > 0 ){
					firstChild = elementList.get( 0 );
				}
			}

			SIF_Error error = rsp.getSIF_Error();
			SIF_ExtendedQueryResults seqResults = rsp.getSIF_ExtendedQueryResults();
			// Validate that the SIF_Response has at least one of the required choice
			// elements, SIF_Error, SIF_ObjectData, or SIF_ExtendedQueryResults
			if( od == null && error == null && seqResults == null )
			{
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Response missing mandatory element",
					"SIF_ObjectData is a required element of SIF_Response", fZone );
			}

			// TODO: For now, the ADK does not support SIF_ExtendedQueryResults
			if( seqResults != null ){
				throw new SIFException(
						SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_NO_SUPPORT_FOR_SIF_EXT_QUERY,
					"Agent does not support SIF_ExtendedQueryResults", fZone );
			}


			String objectType = reqInfo != null ? reqInfo.getObjectType() : null;

			if( objectType == null && firstChild != null ){
				objectType = firstChild.getElementDef().tag( rsp.getSIFVersion() );
			}

			if( objectType != null && objectType.equalsIgnoreCase("SIF_ZoneStatus") )
			{
				//  SIF_ZoneStatus is a special case
				if( fZone.awaitingZoneStatus() ) {
				    fZone.setZoneStatus((SIF_ZoneStatus)firstChild );
				    return;
				}
			}

			//
			//  If the SIF_Response has no SIF_ObjectData elements but does
			//  have a SIF_Error child, the associated object type can
			//  only be gotten from the RequestCache, but that had
			//  failed so try and call the UndeliverableMessageHandler.
			//
			if( reqInfo == null && objectType == null )
			{
				boolean handled = false;

				UndeliverableMessageHandler errHandler = fZone.getErrorHandler();
				if( errHandler != null )
				{
					SIFMessageInfo msginfo = new SIFMessageInfo(rsp,fZone);

					handled = errHandler.onDispatchError( rsp, fZone, msginfo );

					//	Notify MessagingListeners...
					for( MessagingListener ml : getMessagingListeners( fZone ) ){
						ml.onMessageProcessed( SIFMessagingListener.SIF_RESPONSE, msginfo );
					}
				}

				if( !handled )
					fZone.log.warn(
						"Received a SIF_Response message with MsgId " + rsp.getMsgId() + " (for SIF_Request with MsgId " + rsp.getSIF_RequestMsgId() + ") " +
						" containing an empty result set or a SIF_Error, but failed to obtain the SIF Data Object" +
						" type from the RequestCache due to an error.", cacheErr );

				return;
			}



			if( reqInfo == null )
			{
				reqInfo = new UnknownRequestInfo( rsp.getSIF_RequestMsgId(), objectType );
			}

			//  Decide where to send this response
			QueryResults target = getQueryResultsTarget(rsp,null,ADK.DTD().lookupElementDef( objectType ), null, fZone );
			if( target == null )
			{
				boolean handled = false;
				UndeliverableMessageHandler errHandler = fZone.getErrorHandler();
				if( errHandler != null )
				{
					SIFMessageInfo msginfo = new SIFMessageInfo(rsp,fZone);
					msginfo.setSIFRequestInfo( reqInfo );

					handled = errHandler.onDispatchError( rsp, fZone, msginfo );

					//	Notify MessagingListeners...
					for( MessagingListener ml : getMessagingListeners( fZone ) ){
						ml.onMessageProcessed( SIFMessagingListener.SIF_RESPONSE, msginfo );
					}
				}

				if( !handled )
					fZone.log.warn( "Received a SIF_Response message with MsgId " + rsp.getMsgId() + " (for SIF_Request with MsgId " + rsp.getSIF_RequestMsgId() + "), but no QueryResults object is registered to handle it or the request was issued by a TrackQueryResults that has timed out" );

				return;
			}

			//
			//  Dispatch the message...
			//
			ElementDef sifRequestObjectDef = ADK.DTD().lookupElementDef( objectType );
			DataObjectInputStreamImpl dataStr = DataObjectInputStreamImpl.newInstance();
			dataStr.fObjType = sifRequestObjectDef;

			if( error == null && elementList != null )
			{
				//  Convert to a SIFDataObject array
				SIFDataObject[] data = new SIFDataObject[ elementList.size() ];
				elementList.toArray( data );
				//  Let the QueryResults object process the message
				dataStr.setData( data );
			}

			SIFMessageInfo msgInf = new SIFMessageInfo(rsp,fZone);
			msgInf.setSIFRequestInfo( reqInfo );
			msgInf.setSIFRequestObjectType( sifRequestObjectDef );

			target.onQueryResults(dataStr,error,fZone,msgInf);

			//	Notify MessagingListeners...
			for( MessagingListener ml : getMessagingListeners( fZone ) ){
				ml.onMessageProcessed( SIFMessagingListener.SIF_RESPONSE, msgInf );
			}

		}
		catch( ADKException adkEx )
		{
			retry = adkEx.getRetry();
			throw adkEx;
		}
		finally
		{
			// If the reqInfo variable came from the cache, and retry is set to false,
			// remove it from the cache if this is the last packet
			if( !( reqInfo instanceof UnknownRequestInfo ) && !retry  )
			{
				String morePackets = rsp.getSIF_MorePackets();
				if( !( morePackets != null && morePackets.equalsIgnoreCase( "yes") ) )
				{
					// remove from the cache
					fRequestCache.getRequestInfo( rsp.getSIF_RequestMsgId(), fZone );
				}
			}
			if( BuildOptions.PROFILED ) {
				if( reqInfo != null ) {
					ProfilerUtils.profileStop();
				}
			}
		}
	}

	protected void dispatchServiceResponse( SIF_ServiceOutput rsp ) throws ADKException {
		SIFZoneServiceProxy target =  fZone.getServiceSubscriber(rsp.getSIF_Service());
		if (target != null) {
//			String xmlData = rsp.getSIF_Body();
			//SIFDataObject[] dataObjects = new SIFDataObject[rsp.getChildCount()];
			ArrayList<SIFDataObject> doList = new ArrayList<SIFDataObject>();
			for (SIFElement element : rsp.getChildList()) {
				if (element instanceof SIFDataObject) {
					SIFDataObject addThis = (SIFDataObject) element;
					doList.add(addThis);
				}
			}
			
			
			//SIFPullParser parser = new SIFPullParser();
			//SIFElement sifElement = parser.parse(xmlData);

			DataObjectInputStreamImpl data = DataObjectInputStreamImpl.newInstance();
			SIFDataObject[] wua = new SIFDataObject[doList.size()];
			for (int i = 0; i < doList.size(); ++i)
				wua[i] = doList.get(i);
			
			data.setData(wua);
			MessageInfo info = new SIFMessageInfo(rsp,fZone);
			target.onQueryResults(data, rsp.getSIF_Error(), fZone, info);
		}
	}
	
	/*
	 * Service
	 */
	protected void dispatchServiceRequest( SIF_ServiceInput req )
	throws ADKException
	{	
		SIFVersion renderAsVer = null;
		ElementDef typ = req.getElementDef();
		int maxBufSize = 0;
		boolean rethrow = false;
	
		try
		{
			//	block thread until Zone.query() has completed in case it is in the
			//	midst of a SIF_Request and the destination of that request is this
			//	agent (i.e. a request of self). This is done to ensure that we don't
			//	receive the SIF_Request from the zone before the ADK and agent have
			//	finished issuing it in Zone.query()
			fZone.waitForRequestsToComplete();
	
			//
			//  Check SIF_Version. If the version is not supported by the ADK,
			//  fail the SIF_Request with an error SIF_Ack. If the version is
			//  supported, continue on; the agent may not support this version,
			//  but that will be determined later and will result in a SIF_Response
			//  with a SIF_Error payload.
			//
	
/*			SIF_Version[] versions = req.getSIF_Versions();
			if( versions == null || versions.length == 0 ) {
				rethrow = true;
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Request/SIF_Version is a mandatory element",
					fZone );
			}
*/	
			//  SIF_Version specifies the version of SIF that will be used to render
			//  the SIF_Responses
			// TODO: SIF now allows multiple versions within a SIF_Request
			// The ADK needs to keep the list and write the response back in the
			// latest supported version
/*			renderAsVer = SIFVersion.parse( versions[0].getTextValue() );
			if( !ADK.isSIFVersionSupported( renderAsVer ) ) {
				rethrow = true;
				throw new SIFException(
					SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_UNSUPPORTED_SIFVERSION_7,
					"SIF_Version " + renderAsVer + " is not supported by this agent",
					fZone );
			}
*/	
			//  Check max buffer size
			Integer bufferSize = req.getSIF_MaxBufferSize();
			if( bufferSize == null ) {
				rethrow = true;
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Request/SIF_MaxBufferSize is a mandatory element",
					fZone );
			}
	
			maxBufSize = bufferSize;
	
			if( maxBufSize < 4096 || maxBufSize > Integer.MAX_VALUE ) {
				throw new SIFException(
					SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_UNSUPPORTED_MAXBUFFERSIZE_8,
					"Invalid SIF_MaxBufferSize value (" + maxBufSize + ")",
					"Acceptable range is 4096 to " + Integer.MAX_VALUE, fZone );
			}
	
	
	
			// Check to see if the Context is supported
			try {	
				req.getSIFContexts();
			}
			catch( ADKNotSupportedException contextNotSupported ){
				throw new SIFException(
						SIFErrorCategory.GENERIC,
						SIFErrorCodes.GENERIC_CONTEXT_NOT_SUPPORTED_4,
						contextNotSupported.getMessage(), fZone );
			}
	
		}
		catch( SIFException se )
		{
			if( BuildOptions.PROFILED ){
				ProfilerUtils.profileStop();
			}
	
			if( !rethrow ) {
//				sendErrorResponse( req, se, renderAsVer, maxBufSize );
			}
		
			//	rethrow all errors at this point
			throw se;
		}
	
		// For now, SIFContext is not repeatable in SIF Requests
		SIFContext requestContext = req.getSIFContexts().get( 0 );
	
		Object target = null;
		
		if( target == null  )
		{
			target = fZone.getServicePublisher(req.getSIF_Service());
	
			if( target == null )
			{
				UndeliverableMessageHandler errHandler = fZone.getErrorHandler();
				boolean handled = false;
				if( errHandler != null )
				{
					SIFMessageInfo msginfo = new SIFMessageInfo( req, fZone );

					handled = errHandler.onDispatchError( req, fZone, msginfo );

					//	Notify MessagingListeners...
					for( MessagingListener ml : getMessagingListeners( fZone ) ){
						ml.onMessageProcessed( SIFMessagingListener.SIF_REQUEST, msginfo );
					}
				}

				if( !handled )
				{
					SIFException sifEx = new SIFException(
						SIFErrorCategory.REQUEST_RESPONSE,
						SIFErrorCodes.REQRSP_INVALID_OBJ_3,
						"Agent does not support this object type",
	    				"SIF_ServiceInput", fZone );

//						sendErrorResponse( req,sifEx, renderAsVer, maxBufSize );
					throw sifEx;

				}
				else
				{
					if( BuildOptions.PROFILED )
						ProfilerUtils.profileStop();

					return;
				}
				
			}
		}
	
		boolean success = false;
		// DataObjectOutputStreamImpl out = null; JEN
		BaseObjectOutputStream out = null;
		SIFMessageInfo msgInfo = new SIFMessageInfo( req, fZone );
	
		try
		{
			//  Create a stream the Publisher can write results to
			out = ServiceOutputStreamImpl.newInstance();
			((SIFZoneService)target).onRequest( (ServiceOutputFileStream)out,req,fZone,msgInfo );
	
			//	Notify MessagingListeners...
			for( MessagingListener ml : getMessagingListeners( fZone ) ){
				ml.onMessageProcessed( SIFMessagingListener.SIF_REQUEST, msgInfo );
			}
	
		}
		catch( SIFException se )
		{
			//  For a SIF_Request, a SIFException (other than a Transport Error)
			//  does not mean to return an error ack but instead to return a
			//  valid SIF_Response with a SIF_Error payload (see the SIF
			//  Specification). Transport Errors must be returned to the ZIS so
			//  that the message will be retried later.
			//
			if( se.getRetry() || se.getSIFErrorCategory() == SIFErrorCategory.TRANSPORT ) {
				success = false;
				//retry was requested, so we have to tell the output stream to not send an empty response
				out.deferResponse();
				fZone.log.warn( "SIFException in " + "Publisher.onRequest" + ":  Retry was requested, so deferring response because of this error", se );
				throw se;
			}
	
			fZone.log.warn( "SIFException in " + ("Publisher.onRequest"), se );
			out.setError( se.getError() );
		}
		catch( ADKException adke )
		{
			//	If retry requested, throw a Transport Error back to the ZIS
			//	instead of returning a SIF_Error in the SIF_Response payload
			if( adke.getRetry() ) {
				success = false;
				// retry was requested, so we have to tell the output stream to not send an empty response
				out.deferResponse();
				fZone.log.warn( "ADKException in " + "Publisher.onRequest" + ":  Retry was requested, so deferring response because of this error", adke );
				throw adke;
			}
	
			fZone.log.error( "Exception in " + "Publisher.onRequest", adke );
	
			//	Return SIF_Error payload in SIF_Response
			SIF_Error err = new SIF_Error();
			err.setSIF_Category( SIFErrorCategory.GENERIC );
			err.setSIF_Code( SIFErrorCodes.GENERIC_GENERIC_ERROR_1 );			
	
//			if( BuildOptions.PROFILED )
//				ProfilerUtils.profileStart( String.valueOf( openadk.profiler.api.OIDs.ADK_SIFREQUEST_RESPONDER_MESSAGING ), typ, req.getMsgId() );
	

			err.setSIF_Desc( adke.getMessage() );
			err.setSIF_ExtendedDesc( ADKStringUtils.getStackTrace(adke) );
	
			out.setError( err );
		}
		catch( Throwable thr )
		{
			fZone.log.error( "Exception in " + "Publisher.onRequest", thr );
	
	        SIF_Error err = new SIF_Error();
			err.setSIF_Category( SIFErrorCategory.GENERIC );
			err.setSIF_Code( SIFErrorCodes.GENERIC_GENERIC_ERROR_1 );
			err.setSIF_Desc( "Agent could not process the SIF_Request at this time" );
			err.setSIF_ExtendedDesc( ADKStringUtils.getStackTrace(thr) );
	
			out.setError( err );
		}
		finally
		{
			try
			{
				out.close();
			}
			catch( Exception ignored )
			{
				fZone.log.warn( "Ignoring exception in out.close()", ignored );
			}
	
			try
			{
				out.commit();
			}
			catch( Exception ignored )
			{
				fZone.log.warn( "Ignoring exception in out.commit()", ignored );
			}
	
			if( BuildOptions.PROFILED )
				ProfilerUtils.profileStop();
		}
	}


	/**
	 *  Dispatch a SIF_Request.<p>
	 *
	 *  <b>When ALQ Disabled:</b> The SIF_Request is immediately dispatched to
	 *  the Publisher of the associated topic. Only after the Publisher has
	 *  returned a result does this method return, causing the SIF_Request to
	 *  be acknowledged. The result data returned by the Publisher is handed to
	 *  the zone's ResponseDelivery thread, which sends SIF_Response messages to
	 *  the ZIS until all of the result data has been sent, potentially with
	 *  multiple SIF_Response packets. Note without the ALQ, there is the
	 *  potential for the agent to terminate before all data has been sent,
	 *  causing some results to be lost. In this case the SIF_Request will have
	 *  never been ack'd and will be processed again the next time the agent
	 *  is started.
	 *  <p>
	 *
	 *  <b>When ALQ Enabled:</b> The SIF_Request is placed in the ALQ where it
	 *  will be consumed by the zone's ResponseDelivery thread at a later time.
	 *  This method returns immediately, causing the SIF_Request to be
	 *  acknowledged. The ResponseDelivery handles dispatching the request to
	 *  the Publisher of the associated topic, and also handles returning
	 *  SIF_Response packets to the ZIS. With the ALQ, the processing of the
	 *  SIF_Request and the returning of all SIF_Response data is guaranteed
	 *  because the original SIF_Request will not be removed from the ALQ until
	 *  both of these activities have completed successfully (even over multiple
	 *  agent sessions).
	 *  <p>
	 *
	 *  Note that any error that occurs during a SIF_Request should result in a
	 *  successful SIF_Ack (because the SIF_Request was received successfully),
	 *  and a single SIF_Response with a SIF_Error payload. The SIF Compliance
	 *  harness checks for this.
	 *  <p>
	 *
	 *  @param req The SIF_Request to process
	 */
	protected void dispatchRequest( SIF_Request req )
		throws ADKException
	{

		SIFVersion renderAsVer = null;
		SIF_Query q = null;
		SIF_QueryObject qo = null;
		ElementDef typ = null;
		int maxBufSize = 0;
		boolean rethrow = false;
		boolean isReportObject = false;

		try
		{
			//	block thread until Zone.query() has completed in case it is in the
			//	midst of a SIF_Request and the destination of that request is this
			//	agent (i.e. a request of self). This is done to ensure that we don't
			//	receive the SIF_Request from the zone before the ADK and agent have
			//	finished issuing it in Zone.query()
			fZone.waitForRequestsToComplete();

			//
			//  Check SIF_Version. If the version is not supported by the ADK,
			//  fail the SIF_Request with an error SIF_Ack. If the version is
			//  supported, continue on; the agent may not support this version,
			//  but that will be determined later and will result in a SIF_Response
			//  with a SIF_Error payload.
			//

			SIF_Version[] versions = req.getSIF_Versions();
			if( versions == null || versions.length == 0 ) {
				rethrow = true;
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Request/SIF_Version is a mandatory element",
					fZone );
			}

			//  SIF_Version specifies the version of SIF that will be used to render
			//  the SIF_Responses
			// TODO: SIF now allows multiple versions within a SIF_Request
			// The ADK needs to keep the list and write the response back in the
			// latest supported version
			renderAsVer = SIFVersion.parse( versions[0].getTextValue() );
			if( !ADK.isSIFVersionSupported( renderAsVer ) ) {
				rethrow = true;
				throw new SIFException(
					SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_UNSUPPORTED_SIFVERSION_7,
					"SIF_Version " + renderAsVer + " is not supported by this agent",
					fZone );
			}

			//  Check max buffer size
			Integer bufferSize = req.getSIF_MaxBufferSize();
			if( bufferSize == null ) {
				rethrow = true;
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Request/SIF_MaxBufferSize is a mandatory element",
					fZone );
			}

			maxBufSize = bufferSize;

			if( maxBufSize < 4096 || maxBufSize > Integer.MAX_VALUE ) {
				throw new SIFException(
					SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_UNSUPPORTED_MAXBUFFERSIZE_8,
					"Invalid SIF_MaxBufferSize value (" + maxBufSize + ")",
					"Acceptable range is 4096 to " + Integer.MAX_VALUE, fZone );
			}



			// Check to see if the Context is supported
			try
			{
				req.getSIFContexts();
			}
			catch( ADKNotSupportedException contextNotSupported ){
				throw new SIFException(
						SIFErrorCategory.GENERIC,
						SIFErrorCodes.GENERIC_CONTEXT_NOT_SUPPORTED_4,
						contextNotSupported.getMessage(), fZone );
			}


			//  Lookup the SIF_QueryObject
			q = req.getSIF_Query();
			if( q == null ) {
				// If it's a SIF_ExtendedQuery or SIF_Example, throw the appropriate error
				if( req.getSIF_ExtendedQuery() != null ){
					throw new SIFException(
							SIFErrorCategory.REQUEST_RESPONSE,
						SIFErrorCodes.REQRSP_NO_SUPPORT_FOR_SIF_EXT_QUERY,
						"SIF_ExtendedQuery is not supported",
						fZone );
				}
				else
				{
					throw new SIFException(
							SIFErrorCategory.XML_VALIDATION,
						SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
						"SIF_Request/SIF_Query is a mandatory element",
						fZone );
				}
			}

			qo = q.getSIF_QueryObject();
			if( qo == null ) {
				rethrow = true;
				throw new SIFException(
						SIFErrorCategory.XML_VALIDATION,
					SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
					"SIF_Query/SIF_QueryObject is a mandatory element",
					fZone );
			}

			//  Lookup the ElementDef for the requested object type
			typ = ADK.DTD().lookupElementDef( qo.getObjectName() );
			if( typ == null ) {
				throw new SIFException(
					SIFErrorCategory.REQUEST_RESPONSE,
					SIFErrorCodes.REQRSP_INVALID_OBJ_3,
					"Agent does not support this object type: " + qo.getObjectName(),
					fZone );
			}

//			if( BuildOptions.PROFILED )
//				ProfilerUtils.profileStart( String.valueOf( openadk.profiler.api.OIDs.ADK_SIFREQUEST_RESPONDER_MESSAGING ), typ, req.getMsgId() );

			isReportObject = ( ReportingDTD.SIF_REPORTOBJECT  != null && typ == ReportingDTD.SIF_REPORTOBJECT );
		}
		catch( SIFException se )
		{
			if( BuildOptions.PROFILED ){
				ProfilerUtils.profileStop();
			}

			if( !rethrow ){
					sendErrorResponse( req, se, renderAsVer, maxBufSize );
				}





			//	rethrow all errors at this point
			throw se;

//			//  Capture the SIFException so it can be written to the output stream
//			//  and thus returned as the payload of the SIF_Response message later
//			//  in this function.
//			error = se;
//			fZone.log.error( "Error in dispatchRequest that will be put into the SIF_Response", se );
		}

		// For now, SIFContext is not repeatable in SIF Requests
		SIFContext requestContext = req.getSIFContexts().get( 0 );

		Object target = null;

		//
		//  Lookup the Publisher for this object type using Topics,
		// but only if the context is the Default context
		//
		if( typ != null && SIFContext.DEFAULT.equals( requestContext ) ){
			Topic topic = null;
			topic = fZone.getAgent().getTopicFactory().lookupInstance( typ, requestContext );
			if( topic != null ) {
				if( isReportObject )
					target = topic.getReportPublisher();
				else
	    			target = topic.getPublisher();
			}
		}

		if( target == null  )
		{
			//  Next check the Zone
			if( isReportObject )
				target = fZone.getReportPublisher( requestContext );
			else
				target = fZone.getPublisher( requestContext, typ);

			if( target == null )
			{
				//  Finally, check the Agent
				if( isReportObject )
					target = fZone.getAgent().getReportPublisher( requestContext );
				else
					target = fZone.getAgent().getPublisher( requestContext, typ );

				if( target == null )
				{
					//
					//  No Publisher message handler found. Try calling the Undeliverable-
					//  MessageHandler for the zone or agent. If none is registered,
					//  return an error SIF_Ack indicating the object type is not
					//  supported.
					//
					boolean handled = false;
					UndeliverableMessageHandler errHandler = fZone.getErrorHandler();
					if( errHandler != null )
					{
						SIFMessageInfo msginfo = new SIFMessageInfo( req, fZone );

						handled = errHandler.onDispatchError( req, fZone, msginfo );

						//	Notify MessagingListeners...
						for( MessagingListener ml : getMessagingListeners( fZone ) ){
							ml.onMessageProcessed( SIFMessagingListener.SIF_REQUEST, msginfo );
						}
					}

					if( !handled )
					{
						if( isReportObject )
							fZone.log.warn( "Received a SIF_Request for " + qo.getObjectName() + " (MsgId=" + req.getMsgId() + "), but no ReportPublisher object is registered to handle it" );
						else
							fZone.log.warn( "Received a SIF_Request for " + qo.getObjectName() + " (MsgId=" + req.getMsgId() + "), but no Publisher object is registered to handle it" );

						SIFException sifEx = new SIFException(
							SIFErrorCategory.REQUEST_RESPONSE,
							SIFErrorCodes.REQRSP_INVALID_OBJ_3,
							"Agent does not support this object type",
		    				qo.getObjectName(), fZone );

						sendErrorResponse( req,sifEx, renderAsVer, maxBufSize );
						throw sifEx;

					}
					else
					{
						if( BuildOptions.PROFILED )
							ProfilerUtils.profileStop();

						return;
					}
				}
			}
		}

		boolean success = false;
		// DataObjectOutputStreamImpl out = null; JEN
		BaseObjectOutputStream out = null;
		SIFMessageInfo msgInfo = new SIFMessageInfo( req, fZone );
		Query query = null;

		try
		{
			//  Convert SIF_Request/SIF_Query into a Query object
			if( q != null ){
				query = new Query( q );
			}

			msgInfo.setSIFRequestObjectType( typ );
		}
		catch( Throwable thr )
		{
			fZone.log.error( "Could not parse SIF_Query element", thr );

			SIFException sifEx = new SIFException(
					SIFErrorCategory.XML_VALIDATION,
				SIFErrorCodes.XML_MALFORMED_2,
				"Could not parse SIF_Query element",
				ADKStringUtils.getStackTrace(thr),
				fZone );
			sendErrorResponse( req, sifEx, renderAsVer, maxBufSize );
			throw sifEx;

		}

		try
		{
			//  Create a stream the Publisher can write results to
			if( isReportObject )
				out = ReportObjectOutputStreamImpl.newInstance();
			else
				out = DataObjectOutputStreamImpl.newInstance();

			out.initialize( fZone, query , req.getSourceId(), req.getMsgId(), renderAsVer, maxBufSize );

			success = true;

			if( isReportObject )
			{
				Condition cnd = query.hasCondition( ReportingDTD.SIF_REPORTOBJECT_REFID );
				String refId = cnd == null ? null : cnd.getValue();
				((ReportPublisher)target).onReportRequest( refId,(ReportObjectOutputStream)out,query,fZone,msgInfo );
			}
			else
			{
				((Publisher)target).onRequest( (DataObjectOutputStream) out,query,fZone,msgInfo );
			}

			//	Notify MessagingListeners...
			for( MessagingListener ml : getMessagingListeners( fZone ) ){
				ml.onMessageProcessed( SIFMessagingListener.SIF_REQUEST, msgInfo );
			}

		}
		catch( SIFException se )
		{
			//  For a SIF_Request, a SIFException (other than a Transport Error)
			//  does not mean to return an error ack but instead to return a
			//  valid SIF_Response with a SIF_Error payload (see the SIF
			//  Specification). Transport Errors must be returned to the ZIS so
			//  that the message will be retried later.
			//
			if( se.getRetry() || se.getSIFErrorCategory() == SIFErrorCategory.TRANSPORT ) {
				
				String msgId = req.getMsgId();
				msgIdCache.remove(msgId);
				
				success = false;
				//retry was requested, so we have to tell the output stream to not send an empty response
				out.deferResponse();
				fZone.log.warn( "SIFException in " + ( isReportObject ? "ReportPublisher.onReportRequest":"Publisher.onRequest") + ":  Retry was requested, so deferring response because of this error", se );
				throw se;
			}

			fZone.log.warn( "SIFException in " + ( isReportObject ? "ReportPublisher.onReportRequest":"Publisher.onRequest"), se );
			out.setError( se.getError() );
		}
		catch( ADKException adke )
		{
			String msgId = req.getMsgId();
			msgIdCache.remove(msgId);
			
			//	If retry requested, throw a Transport Error back to the ZIS
			//	instead of returning a SIF_Error in the SIF_Response payload
			if( adke.getRetry() ) {
				success = false;
				// retry was requested, so we have to tell the output stream to not send an empty response
				out.deferResponse();
				fZone.log.warn( "ADKException in " + ( isReportObject ? "ReportPublisher.onReportRequest":"Publisher.onRequest") + ":  Retry was requested, so deferring response because of this error", adke );
				throw adke;
			}

			fZone.log.error( "Exception in " + ( isReportObject ? "ReportPublisher.onReportRequest":"Publisher.onRequest"), adke );

			//	Return SIF_Error payload in SIF_Response
			SIF_Error err = new SIF_Error();
			err.setSIF_Category( SIFErrorCategory.GENERIC );
			err.setSIF_Code( SIFErrorCodes.GENERIC_GENERIC_ERROR_1 );
			err.setSIF_Desc( adke.getMessage() );
			err.setSIF_ExtendedDesc( ADKStringUtils.getStackTrace(adke) );

			out.setError( err );
		}
		catch( Throwable thr )
		{
			fZone.log.error( "Exception in " + ( isReportObject ? "ReportPublisher.onReportRequest":"Publisher.onRequest"), thr );

            SIF_Error err = new SIF_Error();
			err.setSIF_Category( SIFErrorCategory.GENERIC );
			err.setSIF_Code( SIFErrorCodes.GENERIC_GENERIC_ERROR_1 );
			err.setSIF_Desc( "Agent could not process the SIF_Request at this time" );
			err.setSIF_ExtendedDesc( ADKStringUtils.getStackTrace(thr) );

			out.setError( err );
		}
		finally
		{
			try
			{
				out.close();
			}
			catch( Exception ignored )
			{
				fZone.log.warn( "Ignoring exception in out.close()", ignored );
			}

			try
			{
				out.commit();
			}
			catch( Exception ignored )
			{
				fZone.log.warn( "Ignoring exception in out.commit()", ignored );
			}

			if( BuildOptions.PROFILED )
				ProfilerUtils.profileStop();
		}
	}

	private void sendErrorResponse(SIF_Request req, SIFException se, SIFVersion renderAsVer, int maxBufSize) throws ADKException {

		BaseObjectOutputStream out = DataObjectOutputStreamImpl.newInstance();
		out.initialize( fZone, (ElementDef[])null, req.getSourceId(), req.getMsgId(), renderAsVer, maxBufSize );

		SIF_Error err = new SIF_Error(
			se.getSIFErrorCategory(),
			se.getErrorCode(),
			se.getErrorDesc(),
			se.getErrorExtDesc() );

		out.setError( err );
		try
		{
			out.close();
		}
		catch( Exception ignored )
		{
			fZone.log.warn( "Ignoring exception in out.close()", ignored );
		}

		try
		{
			out.commit();
		}
		catch( Exception ignored )
		{
			fZone.log.warn( "Ignoring exception in out.commit()", ignored );
		}
	}

	/**
	 *  Checks out an EvDisp thread for the dispatchEvent method
	 */
	private EvDisp checkoutEvDisp( Event forEvent )
	{
		EvDisp d = new EvDisp();

		if( fEvDispCache == null )
			fEvDispCache = new Hashtable();
		fEvDispCache.put(forEvent,d);

		return d;
	}

	/**
	 *  Checks in an EvDisp thread previously obtained with checkoutEvDisp
	 */
	private void checkinEvDisp( Event forEvent )
	{
		if( fEvDispCache != null )
			fEvDispCache.remove(forEvent);
	}

	/**
	 *  Sends a message.<p>
	 *
	 *  If the message is a SIF_Event or SIF_Response message it is persisted to
	 *  the Agent Local Queue (if enabled) to ensure reliable delivery. The
	 *  message is then sent. Upon successful delivery to the ZIS, the message
	 *  is removed from the queue. For all other message types the message is
	 *  sent immediately without being posted to the queue.
	 *  <p>
	 *
	 *  If an exception occurs and a message has been persisted to the queue,
	 *  it is removed from the queue if possible (the queue is operational) and
	 *  the send operation fails.
	 *  <p>
	 */
	public SIF_Ack send( SIFMessagePayload msg )
		throws ADKMessagingException,
			   ADKTransportException
	{

		return send(msg,false);
	}

	public SIF_Ack send( SIFMessagePayload msg, boolean isPullMessage )
		throws ADKMessagingException,
			   ADKTransportException
	{
		if( fZone.fProtocolHandler == null  ){
			throw new ADKTransportException( "Zone is not connected", fZone );
		}

		try
		{
			PolicyManager policyMan = PolicyManager.getInstance( fZone );
			if( policyMan != null ){
				policyMan.applyOutboundPolicy(msg, fZone );
			}
		}
		catch( ADKException adkex ){
			throw new ADKMessagingException( "Unable to apply outbound message policy: " + adkex, fZone, adkex );
		}

		SIF_Ack ack = null;
		StringWriter buf = null;
		SIFWriter out = null;
		byte stage = 1;
		boolean queued = false;
		byte pload = 0;
		boolean cancelled = false;

		try
		{
			//  Assign values to message header.
			SIF_Header hdr = msg.getHeader();
			hdr.setSIF_Timestamp( Calendar.getInstance() );

			hdr.setSIF_SourceId(fSourceId);
			hdr.setSIF_Security(secureChannel());
			//	NOTE: Feature Request #55 allows SIF_Request and SIF_Event messages to be
			//		  assigned a SIF_MsgId by the agent, so make sure to only assign a
			//		  GUID if there isn't already one.
			if( hdr.getSIF_MsgId() == null || hdr.getSIF_MsgId().trim().length() == 0 )
				hdr.setSIF_MsgId(GUIDGenerator.makeGUID());

	    	//	ADK 1.5+: SIF_LogEntry requires that we *duplicate* the
	    	//	header within the object payload. This is really the only
	    	//	place we can do that and ensure that the SIF_Header and
	    	//	the SIF_LogEntry/SIF_LogEntryHeader are identical.

	    	if( msg instanceof SIF_Event )
	    	{
	    		SIF_Event ev = ((SIF_Event)msg);
	    		SIF_ObjectData od = (SIF_ObjectData)ev.getChild( InfraDTD.SIF_EVENT_SIF_OBJECTDATA );
	    		SIF_EventObject eo = od == null ? null : od.getSIF_EventObject();
	    		if( eo != null && eo.getObjectName().equals( "SIF_LogEntry" ) && eo.getAction().equals( "Add" ) )
	    		{
	    			SIF_LogEntry logentry = (SIF_LogEntry)eo.getChild( InfraDTD.SIF_LOGENTRY );
	    			if( logentry != null )
	    			{
	    				// Once we implement cloneable, we should be able to call hdr.Clone(). SIF_LogEntyr
						// will use the real SIF_Header in ADK 2.0
						SIF_LogEntryHeader sleh = new SIF_LogEntryHeader();
						sleh.setSIF_Header( (SIF_Header)hdr.clone() );
						logentry.setSIF_LogEntryHeader( sleh );
	    			}
	    		}
	    	}

			if( !isPullMessage || ( ADK.debug & ADK.DBG_MESSAGING_PULL ) != 0 ) {
				msg.LogSend(fZone.log);
			}
		}
		catch( Throwable thr )
		{
			throw new ADKMessagingException("MessageDispatcher could not assign outgoing message header: "+thr,fZone);
		}

		try
		{
			// Test
/*			ByteArrayOutputStream byteStream = new ByteArrayOutputStream ();
			SIFWriter writer = new SIFWriter(byteStream);
			writer.write(msg);
			writer.flush();		
			System.out.println(byteStream);
*/			
			//  Convert message to a string
			buf = new StringWriter();
	    		out = new SIFWriter(buf,fZone);
		    out.write(msg);
		    out.flush();

		    stage = 2;

			//  SIF_Event and SIF_Response are posted to the agent local queue
			//  if enabled, otherwise sent immediately. All other message types
			//  are sent immediately even when the queue is enabled.
			//
			if( fQueue != null &&
				( msg instanceof SIF_Event || msg instanceof SIF_Response ) )
			{
				fQueue.postMessage(msg);
				queued = true;
			}

			// NOTE: Keeping StringBuffer for now
			StringBuffer buf2 = new StringBuffer();
			buf2.append( buf.toString() );

			List<MessagingListener> msgList = null;
			//	Notify MessagingListeners...
			pload = ADK.DTD().getElementType( msg.getElementDef().name() );
			if( pload != SIFDTD.MSGTYP_ACK )
			{
				msgList = getMessagingListeners( fZone );
				if( msgList.size() > 0 ){
					SIFMessageInfo msgInfo = new SIFMessageInfo( msg, fZone );
					for( MessagingListener ml : msgList ){
						try {
							if( !ml.onSendingMessage( pload, msgInfo, buf2 ) ){
								cancelled = true;
								break;
							}
						} catch( Throwable ignored ) {
							// Log the error
							fZone.log.warn( "Error from message listener " + ml.getClass().getName() +
									" in onSendingMessage.", ignored );
						}
					}
				}
			}

			if( !cancelled ) {
				
			    //  Send the message
				String ackStr = fZone.fProtocolHandler.send(buf2.toString());
				
				// RawMessageListener JEN
				msgList = getMessagingListeners( fZone );
				for ( MessagingListener listener : msgList ) {
					try {
						if (listener instanceof RawMessageListener) {
							((RawMessageListener) listener).onUnparsedMessageReceived(pload, new StringBuffer(ackStr));
						}
					} 
					catch( Throwable ignored ) {
						// Log the error
						fZone.log.warn( "Error from message listener " + listener.getClass().getName() +
								" in onUnparsedMessageReceived.", ignored );
					}
				}

				try {
					//  Parse the results into a SIF_Ack					
					ack = (SIF_Ack)fParser.parse(ackStr,fZone,isPullMessage ? SIFParser.FLG_EXPECT_INNER_ENVELOPE : 0 );
				}
			    catch (Exception parseEx) {
                    if (isPullMessage &&
                    		(parseEx instanceof ADKParsingException ||
                    				parseEx instanceof SIFException ||
                    				parseEx instanceof XMLStreamException )) {

						if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 ) {
							fZone.log.info("Parse Exception : " + parseEx.getMessage());
							fZone.log.info("Unparsed ACK received from ZIF:");
							fZone.log.info( ackStr );
						}
							// The SIFParse was unable to parse this message. Try to create an appropriate
							// SIF_Ack, if SIFMessageInfo is able to parse enough of the message
						throw new PullMessageParseException( parseEx, ackStr, fZone );

					}

					throw new ADKMessagingException( parseEx.getMessage(), fZone );
				}

				if( ack != null ) {
					ack.message = msg;
					if( !isPullMessage || ( ADK.debug & ADK.DBG_MESSAGING_PULL ) != 0 ) {
						ack.LogRecv(fZone.log);
					}
				}

				//	Notify MessagingListeners...
				if( msgList != null && msgList.size() > 0 )
				{
					SIFMessageInfo msgInfo = new SIFMessageInfo( msg, fZone );

					for( MessagingListener ml : msgList ) {
						try {
							ml.onMessageSent( pload, msgInfo, ack );
						} catch( Throwable ignored ) {
							fZone.log.warn( "Error received from MessagingListener " +
									ml.getClass().getName() + " in onMessageSent()", ignored );
						}
					}
				}
			}
			else
			{
				//	Prepare a success SIF_Ack to return
				ack = msg.ackImmediate();
			}
		}
		catch( ADKMessagingException me )
		{
			throw me;
		}
		catch( ADKTransportException te )
		{
			throw te;
		}
		catch( Throwable thr )
		{
			if( stage == 1 )
				throw new ADKMessagingException("MessageDispatcher could not convert outgoing infrastructure message to a string: "+thr,fZone);
			if( stage == 2 )
				throw new ADKMessagingException("MessageDispatcher could not convert SIF_Ack response to an object: "+thr,fZone);
		}
		finally
		{
			//  Removed queued message from queue
			if( queued ) {
				try {
					fQueue.removeMessage(msg.getMsgId());
				} catch( Throwable ignored ) {
				}
			}

			try
			{
				if( buf != null )
	    			buf.close();
		    	out.close();
			}
			catch( IOException ignored )
			{
			}
		}

		return ack;
	}

	/**
	 * Poll the ZIS for messages pending in the agent's queue.
	 * <p>
	 * 
	 * This method is typically called by a ProtocolHandler thread to perform a
	 * periodic pull when the agent is running in pull mode. It may also be
	 * called by the framework to force a pull if the framework requires an
	 * immediate response to a message it has sent to the ZIS, such as a request
	 * for SIF_ZoneStatus.
	 * <p>
	 * 
	 * If a message is retrieved from the ZIS, it is dispatched through the
	 * ADK's usual message routing mechanism just as pushed messages are. Thus,
	 * there is no difference between push and pull mode once a message has been
	 * obtained from the ZIS. Because message routing is asynchronous (i.e. the
	 * MessageDispatcher will forward the message to the appropriate framework
	 * or agent code), this method does not return a value. If an error is
	 * returned in the SIF_Ack, the agent's FaultListener will be notified if
	 * one is registered. If an exception occurs, it is thrown to the caller.
	 * <p>
	 * 
	 * Each time this method is invoked it sends a SIF_GetMessage to the ZIS.
	 * Calling this method repeatedly until 0 or -1 is returned effectively
	 * empties out the agent's queue.
	 * <p>
	 * 
	 * @param threadRunLimit
	 *            limits the number of milliseconds this method will execute.
	 *            Messages are repeatedly pulled until the time limit is
	 *            exceeded or there are no more messages available. 0 means no
	 *            time limit.
	 * 
	 * @return zero if no messages were waiting in the agent's queue; 1 if a
	 *         message was pulled from the agent's queue; -1 if the zone is
	 *         sleeping.
	 */
	public int pull()
		throws ADKException,
			   LifecycleException
	{
		int messageCount = 0;

		//  Wait for fPullCanStart to be set to true by the ZoneImpl class once
		//  the zone is connected

		if( ( ADK.debug & ADK.DBG_MESSAGING_PULL ) != 0 ){
			fZone.log.debug("Polling for next message...");
		}

		// TODO: This should be in SIF_Primitives?
		//  Send a SIF_GetMessage, get a SIF_Ack
		SIF_SystemControl sys = new SIF_SystemControl( fZone.getHighestEffectiveZISVersion() );
		SIF_SystemControlData cmd = new SIF_SystemControlData();
		cmd.addChild( new SIF_GetMessage() );
		sys.setSIF_SystemControlData(cmd);
		SIF_Ack ack = null;
		try
		{
			// ACK received from ZIF JEN
			ack = send(sys,true);				
		}
		catch( PullMessageParseException pmpe ){
			// Unable to parse the pulled message. Try sending the proper
			// Error SIF_Ack to remove the message from the queue
			if( pmpe.fSourceMessage != null ){
				fZone.getLog().debug( "Handling exception by creating a SIF_Error", pmpe.fParseException );
				// Try parsing out the SIF_OriginalMsgId so that we can remove the message
				// from the queue.
				// Ack either the SIF_Ack or the internal, embedded message, based on our setting
				int startIndex = fAckAckOnPull ? 0 : 10;
				int messageStart = pmpe.fSourceMessage.indexOf( "<SIF_Message", startIndex );
				SIFException sourceException = null;
				if( pmpe.fParseException instanceof SIFException ){
					sourceException = (SIFException)pmpe.fParseException;
				}else {
					sourceException = new SIFException(
							SIFErrorCategory.XML_VALIDATION,
							SIFErrorCodes.XML_GENERIC_ERROR_1,
							"Unable to parse pulled SIF_Message",
							pmpe.fParseException.getMessage(),
							fZone, pmpe.fParseException );
				}
				SIF_Ack errorAck = SIFPrimitives.ackError( pmpe.fSourceMessage.substring( messageStart ), sourceException, fZone );
				errorAck.setSIFVersion( sys.getSIFVersion() );

				// Sending Error ACK to ZIF JEN 
				send( errorAck );
			}
		}

		//
		//  Process the response. If status code 9 (no message), no
		//  action is taken. If status code 0 (success), the content of
		//  the SIF_Status / SIF_Data element is parsed and dispatched.
		//  If an error is reported in the ack, the agent's fault
		//  handler is called with a SIFException describing the error
		//  if a fault handler has been registered.
		//

		if( ack.hasStatusCode( SIFStatusCodes.NO_MESSAGES_9 ) )
		{
			if( ( ADK.debug & ADK.DBG_MESSAGING_PULL ) != 0 )
				fZone.log.debug("No messages waiting in agent queue");

			return 0;
		} 

		if( ack.hasError() )
		{
			SIFException se = new SIFException( ack, fZone );
			fZone.log.debug("Unable to pull the next message from the queue: " + se.toString() );
			ADKUtils._throw( se, fZone.log );
		}

		if( ack.hasStatusCode( SIFStatusCodes.SUCCESS_0 ) )
		{
			messageCount++;
			ADKException parseEx = null;
			SIFMessagePayload payload = getPullMessagePayload( ack );
			if( ( ADK.debug & ( ADK.DBG_MESSAGING | ADK.DBG_MESSAGING_PULL ) ) != 0 ) {
				fZone.log.debug("Pulled a "+payload.getElementDef().tag( payload.getSIFVersion() )+" message (SIF " + payload.getSIFVersion() + ")" );
			}

			//	Notify MessagingListeners...
			boolean cancelled = false;
			List<MessagingListener> msgList = MessageDispatcher.getMessagingListeners( fZone );
			if( msgList != null && msgList.size() > 0 )
			{
				StringWriter tmp = new StringWriter();
				SIFWriter sifwriter = new SIFWriter(tmp,fZone);
				sifwriter.write( payload );
				sifwriter.flush();
				tmp.flush();

				StringBuffer xml = new StringBuffer();
				xml.append( tmp.toString() );

				//	Determine message type before parsing
				for( MessagingListener ml : msgList  )
				{
					try
					{
						byte pload = ADK.DTD().getElementType( payload.getElementDef().name() );
						byte code = ml.onMessageReceived( pload, xml );
						switch( code )
						{
						case MessagingListener.RX_DISCARD:
							cancelled = true;
							break;

						case MessagingListener.RX_REPARSE:
						{
							try
							{
								//	Reparse the XML into a new message
								payload = (SIFMessagePayload)fParser.parse( xml.toString(), fZone );
							}
							catch( IOException ioe )
							{
								parseEx = new ADKException( "Failed to reparse message that was modified by MessagingListener: " + ioe, fZone );
							}
						}
						break;
						}
					}
					catch( ADKException adke )
					{
						parseEx = adke;
					}
				}
			}

			if( fQueue != null )
			{
				//  TODO: put message on agent local queue


			}
			else
			{
				if( parseEx != null )
					throw parseEx;

				int ackStatus = SIFStatusCodes.IMMEDIATE_ACK_1;
				SIFException err = null;
				boolean acknowledge = true;

				try
				{
					//  Dispatch the message
					if( !cancelled )
						ackStatus = dispatch(payload);
				}
				catch( LifecycleException le )
				{
					throw le;
				}
				catch( SIFException se )
				{
					err = se;
				}
				catch( ADKException adke )
				{
					//  TODO: This needs to generate proper category/code based on payload

					if( adke.hasSIFExceptions() )
					{
						//  Return the first exception
						err = adke.getSIFExceptions()[0];
					}
					else
					{
						//  Build a SIFException to describe this ADKException
						err = new SIFException(
								SIFErrorCategory.GENERIC,
								SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
								adke.getMessage(),
								ADKStringUtils.getStackTrace(adke),
								fZone );
					}
				}
				catch( Throwable thr )
				{
					//  Uncaught exception (probably an ADK internal error)
					String txt = "An unexpected error occurred while processing a pulled message: " + thr;
					fZone.log.debug( txt, thr );

					//  Build a SIFException to describe this Throwable
					err = new SIFException(
							SIFErrorCategory.SYSTEM,
							SIFErrorCodes.SYS_GENERIC_ERROR_1,
							thr.getMessage(),
							ADKStringUtils.getStackTrace(thr),
							fZone );
				}

				if( acknowledge )
				{
					sendPushAck(ack, payload, ackStatus, err);
				}
				else
					return 1;
			}
		}
		else {
			// We only get to here if there is no error and no success code
			if( ack.hasStatusCode( SIFStatusCodes.SLEEPING_8 ) )
			{
				//  Zone is sleeping
				return -1;
			} else {
				// Unknown condition
				ADKUtils._throw( new SIFException(ack, fZone ), fZone.getLog() );
			}
		}
		
		return messageCount>0 ? 1 : 0;
	}

	private boolean threadTimeExceeded (long start, int threadRunLimit) {
		boolean retval = false;
		
		if (threadRunLimit != 0) {
			long now = Calendar.getInstance().getTimeInMillis();
			if ( (now-start) >= threadRunLimit ) {
				retval = true;
//				System.out.println("Thread Timeout exceeded. Start-" + start + " Now- " + now + " Limit-" + threadRunLimit);
			}
		}
		
		return retval;
	}

	/**
	 * Sends a SIF_Ack in response to a pulled message
	 * @param sifGetMessageAck The original SIF_Ack from the SIF_GetMessage. This is sometimes null, when
	 * 						parsing fails
	 * @param pulledMEssage The message delivered inside of the above ack. NOTE that even if parsing fails,
	 *          the SIFParser tries to return what it can, and will return this message payload (in getParsed()),
	 *          instead of the above container message.
	 * @param ackStatus The status to ack (NOTE: This is ignored if the err property is set)
	 * @param err The error to set in the SIF_Ack
	 */
	private void sendPushAck(SIF_Ack sifGetMessageAck,
			SIFMessagePayload pulledMEssage, int ackStatus, SIFException err) {
		try
		{
			SIF_Ack ack2 = null;
			if( fAckAckOnPull && sifGetMessageAck != null){
				ack2 = sifGetMessageAck.ackStatus( ackStatus );
			} else {
				ack2 = pulledMEssage.ackStatus(ackStatus);
			}


			//  If an error occurred processing the message, return
			//  the details in the SIF_Ack
			if( err != null )
			{
				fZone.log.debug( "Handling exception by creating a SIF_Error", err );

				SIF_Error newErr = new SIF_Error();
				newErr.setSIF_Category( err.getSIFErrorCategory() );
				newErr.setSIF_Code( err.getErrorCode() );
				newErr.setSIF_Desc( err.getErrorDesc() );
				newErr.setSIF_ExtendedDesc( err.getErrorExtDesc() );
				ack2.setSIF_Error( newErr );

				//  Get rid of the <SIF_Status>
				SIF_Status status = ack2.getSIF_Status();
				if( status != null ) {
					ack2.removeChild( status );
				}
			}
			
			// ACK sent back to ZIF JEN 
			//  Send the ack
			send(ack2);
		}
		catch( Exception ackEx )
		{
			fZone.log.debug( "Failed to send acknowledgement to pulled message: " + ackEx, ackEx );
		}
	}

	private SIFMessagePayload getPullMessagePayload( SIF_Ack sifPullAck )
		throws SIFException
	{
		 try
         {
				//  Get the next message
				SIFElement msg = sifPullAck.getSIF_Status().getSIF_Data().getChildren()[0];
				return (SIFMessagePayload)msg.getChildren()[0];
         }
         catch( Exception ex )
         {
             // TT 139 Andy E
             //An Exception occurred while trying to read the contents of the SIF_Ack
             throw new SIFException(
             		SIFErrorCategory.XML_VALIDATION , SIFErrorCodes.XML_MISSING_MANDATORY_ELEMENT_6,
                 "Unable to parse SIF_Ack", fZone, ex );
         }
	}


	public SIF_Security secureChannel()
	{
		AgentProperties props = fZone.getProperties();
		SIF_SecureChannel sec = new SIF_SecureChannel(
			AuthenticationLevel.wrap( String.valueOf( props.getAuthenticationLevel() ) ),
			EncryptionLevel.wrap( String.valueOf( props.getEncryptionLevel() ) )
			);
		return new SIF_Security(sec);
	}

	public void shutdown()
		throws ADKException
	{
		fRunning = null;
	}

	public void run()
	{
		fRunning = new Object();

		SIFMessageInfo messages[] = null;

		while( fRunning != null )
		{
			try
			{
				//  Wait for the next incoming message from any zone and of any type
	    			messages = fQueue.nextMessage( IAgentQueue.MSG_ANY, IAgentQueue.INCOMING);

				//  Dispatch all incoming messages...
				for( int i = 0; i < messages.length; i++ )
				{
					try
					{
						SIFMessagePayload msg = null;
						dispatch(msg);
						fQueue.removeMessage(messages[i].getMsgId());
					}
					catch( Exception adke )
					{
						System.out.println("Messenger could not dispatch message from Agent Local Queue (agent: \""+fZone.getAgent().getId()+"\", zone: \""+fZone.getZoneId()+"\": ");
						adke.printStackTrace( System.out );
					}
				}
			}
			catch( Exception adke )
			{
				System.out.println("MessageDispatcher could not get next message from Agent Local Queue (agent: \""+fZone.getAgent().getId()+"\", zone: \""+fZone.getZoneId()+"\": "+adke);
				adke.printStackTrace( System.out );
			}
		}
	}

	public class MessageIdCache extends LinkedHashMap<String, String> {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Entry<String, String> eldest) {
            if (this.size() > 10) {
                return true;
            }
            return false;
        }        
	}
	
	/**
	 *  EvDisp thread handles a single dispatch request. Used when ALQ is disabled.
	 */
	public class EvDisp implements Runnable
	{
		EvState _state = new EvState();     // state of dispatch
		Object _idle = new Object();        // signaled when a request received
		boolean _alive = false;             // flag thread is alive
		Subscriber _target;
		Event _event;
		ZoneImpl _zone;
		Topic _topic;
		MessageInfo _msgInfo;
		SMBHelper _smb;

		/**
		 *  Constructs an EvDisp and starts its thread
		 */
		public EvDisp()
		{
			new Thread(this).start();

			synchronized(this) {
				try {
					while( !_alive )
						wait();
				} catch( Throwable ignored ) {
					if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
						fZone.log.debug("EvDisp interrupted waiting for thread to run: "+ignored);
				}
			}
		}

		/**
		 *  Signals the waitForAckCode method that the processing of the
		 *  dispatch request is ready for MessageDispatcher to return a
		 *  SIF_Ack with this code.
		 */
		public void notifyAckCode( int code )
		{
			synchronized( _state ) {
				_state._ack = code;
				_state._exception = null;
			}
			synchronized( _idle ) {
	    		_idle.notifyAll();
			}
		}

		/**
		 *  Signals the waitForAckCode method that an exception occurred
		 *  during the processing of the dispatch request. It should stop
		 *  waiting for a code and instead throw the exception.
		 */
		public void notifyException( Throwable thr )
		{
			synchronized( _state ) {
				_state._ack = -1;
				_state._exception = thr;
			}
			synchronized( _idle ) {
				_idle.notifyAll();
			}
		}

		/**
		 *  Waits for the EvDisp thread to either signal that an Intermediate
		 *  acknowledgement should be returned to the ZIS, signal that processing
		 *  has been completed and an Immediate acknowledgement should be
		 *  returned, or signal that an exception occurred. If an exception
		 *  occurred, it is rethrown by this method.
		 *
		 *  @return The SIF_Ack code (either 1 or 2) that should be returned to
		 *      the ZIS in response to the processing of the SIF_Event
		 */
		private int waitForAckCode()
			throws ADKMessagingException
		{
			int result = -1;

			try
			{
				synchronized( _idle )
				{
					//  Wait for the state to be set
					if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
						fZone.log.debug( "EvDisp waiting for acknowlegement code" );
					while( _state._exception == null && _state._ack == -1 )
						_idle.wait(1000);
					if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
						fZone.log.debug( "EvDisp done waiting for acknowlegement code" );
				}

				synchronized( _state )
				{
					//  Was it changed because of an exception? If so throw it
					if( _state._exception != null )
					{
						if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
	    					fZone.log.debug( "EvDisp received an exception instead of an acknowledgement code" );
						if( _state._exception instanceof ADKMessagingException ) {
							ADKUtils._throw( (ADKMessagingException)_state._exception,fZone.log );
						} else {
							ADKMessagingException adkme = new ADKMessagingException(
									"Dispatching SIF_Event: " + _state._exception,
									fZone, _state._exception );
							if( _state._exception instanceof ADKException )
							{
								// ensure that retry support is always enabled
								adkme.setRetry( ((ADKException) _state._exception).getRetry() );
							}
							ADKUtils._throw( adkme ,fZone.log );
						}
					}

				    //  Return the SIF_Ack code the caller is waiting for
					if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
						fZone.log.debug( "EvDisp received acknowledgement code "+_state._ack );

		    		result = _state._ack;
				}
			}
			catch( InterruptedException ie )
			{
				if( ( ADK.debug & ADK.DBG_MESSAGING_EVENT_DISPATCHING ) != 0 )
					fZone.log.debug( "EvDisp interrupted waiting for ack code" );
				ADKUtils._throw( new ADKMessagingException(ie.toString(),fZone), fZone.log );
			}

			return result;
		}

		/**
		 *  Wakes up the thread, dispatches the event to Subscriber.onEvent,
		 *  then returns the appropriate SIF_Ack code. If onEvent() completes
		 *  processing without invoking SMB, this method returns once onEvent()
		 *  is finished. If, however, onEvent() invokes SMB by instantiating a
		 *  TrackQueryResults object, this method returns at the time the
		 *  TrackQueryResults constructor is called.
		 */
		public int dispatch(
			Subscriber target,
			Event event,
			ZoneImpl zone,
			Topic topic,
			MessageInfo msgInfo )
				throws ADKMessagingException
		{
			_target=target;
			_event=event;
			_zone=zone;
			_topic=topic;
			_msgInfo=msgInfo;
			_smb=null;

			_state._ack = -1;
			_state._exception = null;

			synchronized( _idle ) {
				if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
					fZone.log.debug( "MessageDispatcher giving EvDisp a dispatch request" );
			    _idle.notifyAll();
			}

			return waitForAckCode();
		}

		public void run()
		{
			synchronized(this) {
				_alive = true;
				notifyAll();
			}

			//  Wait for this thread to get dispatch info
			synchronized( _idle ) {
				try {
					if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
						fZone.log.debug( "EvDisp waiting for dispatch request..." );
					while( _target == null )
						_idle.wait();
				} catch( InterruptedException ie ) {
					return;
				}
			}

			try
			{
				if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
					fZone.log.debug( "EvDisp received dispatch request" );

				//  Dispatch event to subscriber...
				_target.onEvent(_event,_zone,_msgInfo);

				//	Notify MessagingListeners...
				List<MessagingListener> msgList = getMessagingListeners( fZone );
				if( msgList != null && msgList.size() > 0 )
				{
					for( MessagingListener ml : msgList){
						ml.onMessageProcessed( SIFMessagingListener.SIF_EVENT, _msgInfo );
					}
				}

				//  The event was successfully processed, so either send an
				//  immediate SIF_Ack with status code 1, *or* if SMB was
				//  invoked by a TrackQueryResults object, ask the SMBHelper
				//  to send a final SIF_Ack with status code 3.
				//
				if( _smb == null ) {
					notifyAckCode(1); // send SIF_Ack(1)
				} else {
					_smb.endSMB(); // send SIF_Ack(3)
				}
			}
			catch( SIFException se )
			{
				Thread.currentThread().interrupt();
				notifyException(se);
			}
			catch( ADKException adke )
			{
				Thread.currentThread().interrupt();
				notifyException(adke);
			}
			catch( Throwable thr )
			{
				Thread.currentThread().interrupt();
				fZone.log.error( "Uncaught exception in onEvent: " + thr, thr );
				notifyException(thr);
			}
		}
	}

	class EvState
	{
		int _ack = -1;
		Throwable _exception = null;
	}


	static class PullMessageParseException extends ADKMessagingException
	{

		Exception fParseException;
		String fSourceMessage;

		public PullMessageParseException( Exception parseException, String sourceMessage, Zone zone ) {
			super( parseException.getMessage(), zone );
			fSourceMessage = sourceMessage;
			fParseException = parseException;

		}

	}


	/**
	 * Implements RequestInfo for cases where a cache lookup fails. The ADK
	 * always guarantees that SIFMessageInfo.getSIFRequestInfo() will be a non-null
	 * value in the onQueryResults and onQueryPending message handlers
	 *
	 * @author Andrew Elmhorst
	 *
	 */
	class UnknownRequestInfo implements RequestInfo
	{
		private String fMsgId;
		private String fObjectType;

		public UnknownRequestInfo( String msgId, String objectType )
		{
			fMsgId = msgId;
			fObjectType = objectType;
		}
		public String getObjectType() {
			return fObjectType;
		}

		public String getMessageId() {
			return fMsgId;
		}

		public Date getRequestTime() {
			return null;
		}

		public boolean isActive() {
			return false;
		}

		public Serializable getUserData() {
			return null;
		}

	}

	private void logAndThrowSIFException( String shortMessage, Throwable exception ) throws SIFException {
		if ( (ADK.debug & ADK.DBG_EXCEPTIONS) != 0 )
			fZone.log.error( shortMessage, exception );
		SIFException exToThrow = new SIFException(
			SIFErrorCategory.GENERIC,
			SIFErrorCodes.GENERIC_GENERIC_ERROR_1,
			shortMessage,
			ADKStringUtils.getStackTrace( exception ),
			fZone );
		if ( (ADK.debug & ADK.DBG_EXCEPTIONS) != 0 )
			fZone.log.error( "Translated to a SIFException", exToThrow );
		throw exToThrow;

	}

	private void logAndRethrow( String shortMessage, ADKException exception ) throws ADKException {
		if ( (ADK.debug & ADK.DBG_EXCEPTIONS) != 0 ) {
			fZone.log.error( shortMessage, exception );
		}
		throw exception;
	}

	private void logAndThrowRetry( String shortMessage, Throwable exception ) throws ADKException {
		if ( (ADK.debug & ADK.DBG_EXCEPTIONS) != 0 ) {
			fZone.log.error( shortMessage, exception );
		}
		SIFException exToThrow = new SIFException(
				SIFErrorCategory.TRANSPORT,
				SIFErrorCodes.WIRE_GENERIC_ERROR_1,
				shortMessage,
				ADKStringUtils.getStackTrace( exception ),
				fZone );
		if ( (ADK.debug & ADK.DBG_EXCEPTIONS) != 0 )
			fZone.log.error( "Translated to a SIFException that will force a retry", exToThrow );
		throw exToThrow;

	}
}
