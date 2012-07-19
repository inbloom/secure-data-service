//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (c)2001-2007 Edustructures LLC
//  All rights reserved.
//
//  This software is the confidential and proprietary information of
//  Edustructures LLC ("Confidential Information").  You shall not disclose
//  such Confidential Information and shall use it only in accordance with the
//  terms of the license agreement you entered into with Edustructures.
//

/**
 *	A MessagingListener that can be implemented to receive notification when 
 *	when SIF-related messaging activity takes place. An agent can register one
 *	or more SIFMessagingListeners with Zone and Topic classes. In some cases a
 *	listener instance can be directly passed to a method of those classes, such 
 *	as <code>Zone.query</code> and <code>Topic.query</code>, so that an 
 *	anonymous implementation of the listener can be used.<p>
 *
 *	For all types of messages represented by this interface, the framework 
 *	calls the appropriate function both before and after a message is received
 *	or sent to a zone. The <i>messageType</i> parameter will be a value defined
 *	by the constants of this interface (e.g. <code>SIF_EVENT</code>).
 *	<p>
 */
public interface SIFMessagingListener extends MessagingListener
{
	/**
	 * 	MessageType constant identifying a SIF_Event
	 */
	public static final byte SIF_EVENT = SIFDTD.MSGTYP_EVENT;
	
	/**
	 * 	MessageType constant identifying a SIF_Request
	 */	
	public static final byte SIF_REQUEST = SIFDTD.MSGTYP_REQUEST;
	
	/**
	 * 	MessageType constant identifying a SIF_Response
	 */	
	public static final byte SIF_RESPONSE = SIFDTD.MSGTYP_RESPONSE;
	
	/**
	 * 	MessageType constant identifying a SIF_SystemControl
	 */	
	public static final byte SIF_SYSTEM_CONTROL = SIFDTD.MSGTYP_SYSTEMCONTROL;

	/**
	 * 	MessageType constant identifying a SIF_Register
	 */
	public static final byte SIF_REGISTER = SIFDTD.MSGTYP_REGISTER;

	/**
	 * 	MessageType constant identifying a SIF_Unregister
	 */
	public static final byte SIF_UNREGISTER = SIFDTD.MSGTYP_UNREGISTER;

	/**
	 * 	MessageType constant identifying a SIF_Subscribe
	 */
	public static final byte SIF_SUBSCRIBE = SIFDTD.MSGTYP_SUBSCRIBE;

	/**
	 * 	MessageType constant identifying a SIF_Unsubscribe
	 */
	public static final byte SIF_UNSUBSCRIBE = SIFDTD.MSGTYP_UNSUBSCRIBE;

	/**
	 * 	MessageType constant identifying a SIF_Provide
	 */
	public static final byte SIF_PROVIDE = SIFDTD.MSGTYP_PROVIDE;

	/**
	 * 	MessageType constant identifying a SIF_Unprovide
	 */
	public static final byte SIF_UNPROVIDE = SIFDTD.MSGTYP_UNPROVIDE;
		
	
	/**
	 * 	Called when a SIF_Message has been received by the framework, before it 
	 * 	is dispatched to the ADK's message handlers.<p> 
	 * 
	 * 	An agent can implement this method to count the number of messages 
	 * 	received, to signal the user interface or other interested parties that 
	 * 	a message has arrived, to examine or change the raw content of incoming
	 * 	messages before they're dispatched to the framework, or to prevent the 
	 * 	framework from dispatching messages to message handlers. When filtering 
	 * 	messages, return <code>true</code> to allow the framework to process 
	 * 	this message, <code>false</code> to silently discard the message, or 
	 * 	throw an ADKException to return an error to the server.
	 * 	<p>
	 * 	
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param message A StringBuffer containing the message to be sent. The
	 * 		contents of the buffer may be modified by this method.
	 *
	 * 	@return One of the following codes: <code>RX_PROCESS</code> to allow 
	 * 		the framework to process this message; <code>RX_DISCARD</code> to 
	 * 		silently discard the message; or <code>RX_REPARSE</code> if this
	 * 		method has changed the content of the <i>message</i> parameter
	 * 		StringBuffer.
	 * 
	 * 	@exception ADKException may be thrown by this method to return an error 
	 * 		acknowledgement to the server
	 */
	public byte onMessageReceived( byte messageType, StringBuffer message )
		throws ADKException;
		
	/**
	 * 	Called when a SIF_Message has been received by the framework and 
	 * 	successfully dispatched to the ADK's message handlers. If a message is 
	 * 	received but not processed by a message handler, this method is never 
	 * 	called.<p>
	 * 	
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param info The MessageInfo instance associated with the message
	 * 
	 * 	@exception ADKException may be thrown by this method to return an error 
	 * 		acknowledgement to the server
	 */
	public void onMessageProcessed( byte messageType, MessageInfo info )
		throws ADKException;
		
	/**
	 * 	Called when a SIF_Message is about to be sent by the framework.<p>
	 * 	
	 * 	An agent can implement this method to filter outbound messages in order
	 * 	to change the message content or prevent the framework from sending a
	 * 	message to the server. When filtering messages, return <code>true</code> 
	 * 	to allow the framework to send this message or <code>false</code> to 
	 * 	silently discard the message.
	 * 	<p>
	 * 
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param info The MessageInfo instance associated with the message
	 * 	@param message A StringBuffer containing the message to be sent. The
	 * 		contents of the buffer may be modified by this method.
	 */
	public boolean onSendingMessage( byte messageType, MessageInfo info, StringBuffer message );
		
	/**
	 * 	Called when a SIF_Message has been sent by the framework and a SIF_Ack
	 * 	received from the server.<p>
	 * 
	 * 	An agent can implement this method to count the number of messages 
	 * 	sent or to signal the user interface or other interested parties that a 
	 * 	message has been sent.<p>
	 * 	
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param info The MessageInfo instance associated with the message
	 * 	@param ack The SIF_Ack returned by the server
	 */
	public void onMessageSent( byte messageType, MessageInfo info, Object ack );
}
