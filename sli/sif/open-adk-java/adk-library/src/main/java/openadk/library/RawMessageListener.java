//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

public interface RawMessageListener extends MessagingListener {
	
	/**
	 * 	Called when a SIF_Ack has been received by the framework, before it 
	 * 	parsed by the framework.<p> 
	 * 
	 * 	@param messageType A message type constant (e.g. <code>SIFMessagingListener.SIF_EVENT</code>)	
	 * 	@param message A StringBuffer containing the message to be sent. The
	 * 		contents of the buffer may be modified by this method.
	 *
	 * 	@return void
	 * 
	 * 	@exception ADKException may be thrown by this method to return an error 
	 * 		Acknowledgment to the server
	 */
	public void onUnparsedMessageReceived( byte messageType, StringBuffer message )
		throws ADKException;

}
