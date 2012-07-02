//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  The Publisher interface is implemented by classes that respond to queries (SIF_Requests).<p>
 *
 *  Implement the Publisher interface on a class that is capable of responding
 *  to SIF_Requests for one or more SIF Data Objects, then register your class
 *  with a Topic or Zone by calling the <code>Topic.setPublisher</code> or
 *  <code>Zone.setPublisher</code> methods. SIF_Request messages received by
 *  the ADK are then dispatched to your Publisher for processing.<p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public interface Publisher
{
	/**
	 *  Respond to a SIF_Request received from a zone.<p>
	 *
	 *  The implementation should evaluate the supplied Query to build a set
	 *  of SIFDataObjects that satisfy the conditions and field restrictions
	 *  placed on the query. To return these objects to the ZIS, repeatedly
	 *  call the <code>DataObjectOutputStream.write( SIFDataObject )</code>
	 *  method.<p>
	 *
	 *  The ADK returns the results of the request to the requestor by sending
	 *  one or more SIF_Response messages. This is handled in a separate thread
	 *  managed by the framework. If the number of SIFDataObjects passed to the
	 *  output stream is too large to fit into a single SIF_Response message,
	 *  the ADK handles breaking it up into multiple packets. The result data
	 *  may be temporarily stored on disk until all packets can be returned to
	 *  the ZIS.<p>
	 *
	 *  @param out The output stream to send SIFDataObject results to
	 *  @param query The query conditions
	 *  @param zone The zone this SIF_Request was received on
	 *  @param info Provides protocol-specific information about the message.
	 * @throws ADKException 
	 */
	public void onRequest( DataObjectOutputStream out, Query query, Zone zone, MessageInfo info )
		throws ADKException;
}
