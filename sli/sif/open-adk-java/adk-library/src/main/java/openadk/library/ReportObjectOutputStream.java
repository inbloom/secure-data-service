//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.reporting.*;

/**
 *  The ReportObjectOutputStream is implemented by the ADK to supply agents with
 *  a means of streaming arbitrarily large SIF Data Objects responses for SIF_ReportObject
 * 	queries in SIF 1.5 and later. The ADK handles packetizing and storing SIF_Responses 
 * 	for SIF_ReportObject in a local cache for reliable delivery to the ZIS.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.5
 * 	@since ADK 1.5
 */
public interface ReportObjectOutputStream
{
	/**
	 * 	Assign a ReportInfo instance to the stream.<p>
	 * 
	 * 	The ReportInfo object identifies the report manifest to which the SIF_ReportObject
	 * 	relates, as well as information about this particular submission. The requesting
	 * 	agent uses this information to associate the report data with a report manifest. 
	 * 	The ReportInfo will be included in each SIF_ReportObject packet sent with the 
	 * 	response.<p>  
	 * 
	 *  This method must be called before writing any SIFDataObject data to the stream 
	 * 	via the <code>write</code> method. Once called, it must not be called again for 
	 * 	the duration of the message processing or an IllegalStateException is thrown.<p>
	 * 
	 * 	@param reportObjectRefId The RefId of the SIF_ReportObject being streamed. The
	 * 		agent is responsible for assigning a RefId to the SIF_ReportObject and using
	 * 		the same RefId to identify all SIF_ReportObject instances that comprise the
	 * 		report data.
	 * 
	 * 	@param info A ReportInfo instance from the <code>com.edustructures.sifworks.reporting</code> package
	 * 
	 * 	@param zone The zone that is responding to the SIF_Request for SIF_ReportObjects
	 * 	
	 * 	@exception IllegalStateException is thrown if this method is called more than once
	 */
	public void setReportInfo( String reportObjectRefId, ReportInfo info, Zone zone )
		throws ADKException;
	
	/**
	 *  Write a SIFDataObject to the stream
	 */
	public void write( SIFDataObject data )
		throws ADKException;
}
