//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  The ReportPublisher interface is implemented by message handler classes 
 * 	that respond to requests for SIF_ReportObject. Note the ReportPublisher 
 * 	interface must be used instead of the standard Publisher interface for
 * 	the SIF_ReportObject object type.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.5
 * 	@since ADK 1.5
 */
public interface ReportPublisher
{
	/**
	 *  Respond to a SIF_Request for SIF_ReportObject received from a zone.<p>
	 *
	 *  The implementation should evaluate the supplied ReportManifestRefId 
	 * 	and Query to build a set of SIFDataObjects that satisfy the conditions 
	 * 	and field restrictions placed on the query. If the agent cannot satisfy
	 * 	the query it should return without taking any action.<p>
	 * 
	 * 	Assign report information to the ReportObjectOutputStream object by
	 * 	constructing a ReportInfo instance (from the com.edustructures.sifworks.reporting
	 * 	package) and passing that instance to the <code>setReportInfo</code>
	 * 	method. Alternatively, you may call the following methods directly:<p>
	 * 
	 * 	<ul>
	 * 		<li><code>setReportManifestRefId</code></li>
	 * 		<li><code>setDescription</code></li>
	 * 		<li><code>setCalculationDate</code></li>
	 * 		<li><code>setSubmissionNumber</code></li>
	 * 		<li><code>setSubmissionReason</code></li>
	 * 	</ul>
	 * 
	 * 	To return the SIF Data Objects that comprise the report, repeatedly call the 
	 * 	<code>ReportObjectOutputStream.write( SIFDataObject )</code> method. To
	 * 	return an error with the response, call the <code>setError</code> method
	 * 	or throw a SIFException. The ADK will convert the exception into a SIF_Error 
	 * 	element and include it in the payload of the SIF_Response.<p>
	 *
	 *  The ADK returns the results of the request to the requestor by sending
	 *  one or more SIF_Response messages with packetized SIF_ReportObject payloads. 
	 * 	This is handled in a separate thread managed by the framework. If the number 
	 * 	of SIFDataObjects passed to the output stream is too large to fit into a 
	 * 	single SIF_ReportObject and SIF_Response message, the ADK handles breaking 
	 * 	it up into multiple packets. The result data may be temporarily stored on 
	 * 	disk until all packets can be returned to the ZIS.<p>
	 *
	 *	@param reportObjectRefId The RefId of the SIF_ReportObject being requested
	 *  @param out The output stream to send SIFDataObject results to
	 *  @param query The query conditions
	 *  @param zone The zone this SIF_Request was received on
	 *  @param info Provides protocol-specific information about the message.
	 */
	public void onReportRequest( String reportObjectRefId, ReportObjectOutputStream out, Query query, Zone zone, MessageInfo info )
		throws ADKException;
}
