//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  DataObjectOutputStream is supplied to the <code>Publisher.onRequest</code> message 
 * 	handler to allow agents to stream an arbitrarily large set of SIFDataObjects when 
 * 	responding to SIF_Request messages. The ADK handles packetizing the objects 
 * 	into SIF_Response packets, which are stored in a cache on the local file system 
 * 	until they can be delivered to the zone.<p>
 * 
 * 	ADK 1.5.1 introduces a mechanism to decouple SIF_Request processing from 
 * 	SIF_Response delivery, which is necessary when implementing the StudentLocator 
 * 	message choreography or to place inbound SIF_Request messages in a work queue
 * 	for processing at a later time. Call the {@link #deferResponse} method to inform 
 * 	the ADK that you will take responsibility for processing the SIF_Request message 
 * 	at a later time after the <code>Publisher.onRequest</code> method has completed. 
 * 	Note the ADK immediately acknowledges the SIF_Request when <code>onRequest</code>
 * 	returns, but because the <code>deferResponse</code> method has been called it 
 * 	does not attempt to send cached SIF_Response packets to the zone. When your agent 
 * 	is ready to process the request, it can use the {@link openadk.library.SIFResponseSender} 
 * 	class to stream, packetize, and deliver SIF_Responses. See the class comments
 * 	for more information.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface DataObjectOutputStream
{
	
	/**
	 * Tells the DataObjectOutputStream to automatically filter out any SIFDataObjects that do
	 * not match the conditions specified in the provided Query object.<p>
	 * 
	 * Any SIFDataObject that does not meet the conditions specified in the Query will not be
	 * written to the underlying data stream.
	 * @param filter The Query object to use when filtering data or <code>null</code> to remove the filter 
	 */
	public void setAutoFilter( Query filter );
	
	/**
	 *  Write a SIFDataObject to the stream
	 * 	@param data A SIFDataObject instance to write to the output stream
	 */
	public void write( SIFDataObject data )
		throws ADKException;
	
	/**
	 * 	Defer sending SIF_Response messages and ignore any objects written to this stream.<p>
	 * 
	 * 	See the {@link openadk.library.SIFResponseSender} class comments for
	 * 	more information about using this method.<p>
	 *
	 * 	@see openadk.library.SIFResponseSender
	 * 
	 * 	@since ADK 1.5.1
	 */
	public void deferResponse()
		throws ADKException;
}
