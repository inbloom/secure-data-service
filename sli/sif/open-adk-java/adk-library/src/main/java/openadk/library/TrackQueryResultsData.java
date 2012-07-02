//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.infra.SIF_Error;

/**
 *  Encapsulates result data returned by the TrackQueryResults class. For each
 *  SIF_Response message received by TrackQueryResults, an instance of this
 *  class is returned by the <code>next</code> method.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class TrackQueryResultsData
{
	protected DataObjectInputStream fData;
	protected ElementDef fObjectType;
	protected SIF_Error fError;
	protected Zone fZone;
	protected SIFMessageInfo fInfo;

	/**
	 *  Constructor
	 *  @param data The array of SIFDataObjects received from the SIF_Response message
	 *  @param zone The zone from which the SIF Response data was received
	 *  @param info The SIFMessageInfo object describing additional information
	 *      about the SIF_Response message, including SIF header fields and
	 *      optionally the XML content of the message.
	 *
	 */
    public TrackQueryResultsData( DataObjectInputStream data, Zone zone, SIFMessageInfo info )
	{
		fData = data;
		fObjectType = data.getObjectType();
		fError = null;
		fZone = zone;
		fInfo = info;
    }

	/**
	 *  Constructor for a response with errors
	 *  @param error The SIF_Error object contained in the SIF_Response message
	 *  @param objectType A constant from the SIFDTD class identifying the
	 *      SIF Data Object type associated with this response
	 *  @param zone The zone from which the SIF Response data was received
	 *  @param info The SIFMessageInfo object describing additional information
	 *      about the SIF_Response message, including SIF header fields and
	 *      optionally the XML content of the message.
	 *
	 */
    public TrackQueryResultsData( SIF_Error error, ElementDef objectType, Zone zone, SIFMessageInfo info )
	{
		fData = null;
		fObjectType = objectType;
		fError = error;
		fZone = zone;
		fInfo = info;
    }

	/**
	 *  Gets the zone from which the SIF Response data was received
	 */
	public Zone getZone() {
		return fZone;
	}

	/**
	 *  Gets the SIFMessageInfo object describing additional information about
	 *  the SIF Response message, including SIF header fields and optionally the
	 *  XML content of the message.
	 */
	public SIFMessageInfo getMessageInfo() {
		return fInfo;
	}

	/**
	 *  Gets the array of SIFDataObjects received from the SIF_Response message.
	 *  If the query resulted in zero results, or if an error occurred, the
	 *  stream may return a null value. However, this method will always return
	 *  a valid stream from which to read.
	 *
	 *  @see #getError
	 */
	public DataObjectInputStream getData() {
		return fData;
	}

	/**
	 *  Gets the SIF_Error element received from the SIF_Response message, if
	 *  applicable. If no error was returned with the SIF_Response, this method
	 *  returns a null value. Agents should always check for the presence of an
	 *  error, in which case no SIF Data Objects should be expected.
	 *
	 *  @see #getData
	 */
	public SIF_Error getError() {
		return fError;
	}
}
