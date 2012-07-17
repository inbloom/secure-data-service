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
 *	The base interface for all listener interfaces related to the exchange of
 *	messages over the underlying messaging infrastructure. For example, 
 *	SIFMessagingListener extends this interface for the Schools Interoperability
 *	Framework.<p>
 */
public interface MessagingListener
{
	/**
	 * 	onMessageReceived return value indicating the message should be processed
	 */
	public static final byte RX_PROCESS = 1;	

	/**
	 * 	onMessageReceived return value indicating the message should be discarded
	 */
	public static final byte RX_DISCARD = 0;	
	
	/**
	 * 	onMessageReceived return value indicating the message should be reparsed
	 * 	and processed
	 */
	public static final byte RX_REPARSE = 2;	
	
	/**
	 * 	Called when a message has been received by the framework, before it is
	 * 	dispatched to the ADK's message handlers.<p> 
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
	 * 	Called when a message has been received by the framework and successfully
	 * 	dispatched to the ADK's message handlers. If a message is received but 
	 * 	not processed by a message handler -- either because of an error or
	 * 	because there is no <i>Publisher</i>, <i>Subscriber</i>, or <i>QueryResults</i>
	 * 	message handler to handle it -- this method is never called.<p>
	 * 	
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param info The MessageInfo instance associated with the message
	 * 
	 * 	@exception ADKException may be thrown by this method to return an error 
	 * 		acknowledgement to the server
	 * 
	 * 	@exception ADKException An ADKException may be thrown by this method 
	 * 		to return an error acknowledgement to the server
	 */
	public void onMessageProcessed( byte messageType, MessageInfo info )
		throws ADKException;
		
	/**
	 * 	Called when a message is about to be sent by the framework.<p>
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
	 * 	Called when a message has been sent by the framework.<p>
	 * 
	 * 	An agent can implement this method to count the number of messages 
	 * 	sent or to signal the user interface or other interested parties that a 
	 * 	message has been sent.<p>
	 * 	
	 * 	@param messageType A message type constant defined by a subclass of	this interface
	 * 	@param info The MessageInfo instance associated with the message
	 * 	@param receipt The acknowledgement or receipt returned by the server
	 */
	public void onMessageSent( byte messageType, MessageInfo info, Object receipt );
}
