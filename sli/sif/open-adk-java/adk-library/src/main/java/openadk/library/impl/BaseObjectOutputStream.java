//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.IOException;

import openadk.library.ADKException;
import openadk.library.ElementDef;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.common.YesNo;
import openadk.library.infra.SIF_Error;


public interface BaseObjectOutputStream {

	/**
	 *  Initialize the output stream. This method must be called after creating
	 *  a new instance of this class and before writing any SIFDataObjects to
	 *  the stream.
	 *
	 *  @param zone The Zone associated with messages that will be written
	 *      to the stream
	 *  @param query The Query object to use as a source for any SIF_Element
	 *  	 filter restrictions that might be placed on this SIF_Response
	 *  @param requestSourceId The SourceId of the associated SIF_Request message
	 *  @param requestMsgId The MsgId of the associated SIF_Request message
	 *  @param requestSIFMessageVersion The version of the SIF_Message envelope
	 *      of the SIF_Request message
	 *  @param maxSize The maximum size of rendered SIFDataObject that will be
	 *      accepted by this stream. If a SIFDataObject is written to the stream
	 *      and its size exceeds this value after rendering the object to an XML
	 *      stream, an ObjectTooLargeException will be thrown by the <i>write</i>
	 *      method
	 */
	public abstract void initialize(Zone zone, Query query,
			String requestSourceId, String requestMsgId,
			SIFVersion requestSIFMessageVersion, int maxSize)
			throws ADKException;

	/**
	 *  Initialize the output stream. This method must be called after creating
	 *  a new instance of this class and before writing any SIFDataObjects to
	 *  the stream.
	 *
	 *  @param zone The Zone associated with messages that will be written
	 *      to the stream
	 *  @param queryRestrictions Any field restrictions that were specified in the SIF_Request message.
	 * 		Call {@link openadk.library.Query#getFieldRestrictions} to obtain
	 * 		this array.
	 *  @param requestSourceId The SourceId of the associated SIF_Request message
	 *  @param requestMsgId The MsgId of the associated SIF_Request message
	 *  @param requestSIFMessageVersion The version of the SIF_Message envelope
	 *      of the SIF_Request message
	 *  @param maxSize The maximum size of rendered SIFDataObject that will be
	 *      accepted by this stream. If a SIFDataObject is written to the stream
	 *      and its size exceeds this value after rendering the object to an XML
	 *      stream, an ObjectTooLargeException will be thrown by the <i>write</i>
	 *      method
	 */
	public abstract void initialize(Zone zone, ElementDef[] queryRestrictions,
			String requestSourceId, String requestMsgId,
			SIFVersion requestSIFMessageVersion, int maxSize)
			throws ADKException;

	public abstract void close() throws IOException;

	/**
	 *  Called by the class framework when the Publisher.onQuery method has
	 *  returned successfully
	 */
	public abstract void commit() throws ADKException;

	/**
	 *  Called by the class framework when the Publisher.onQuery method has
	 *  thrown an exception other than SIFException
	 */
	public abstract void abort() throws ADKException;

	/**
	 *  Write a SIFDataObject to the stream
	 */
	public abstract void write(SIFDataObject data) throws ADKException;

	/**
	 *  Called when the Publisher.onQuery method has thrown a SIFException,
	 *  indicating an error should be returned in the SIF_Response body
	 */
	public abstract void setError(SIF_Error error) throws ADKException;

	/**
	 * Returns the value that will be set to the SIF_MorePackets element in the message
	 * after this DataObjectOutputStream is closed
	 * @return
	 */
	public abstract YesNo getSIF_MorePackets();

	/**
	 * Returns the current SIF_PacketNumber
	 * @return
	 */
	public abstract int getSIF_PacketNumber();

	/**
	 * Sets the value that will be set to the SIF_MorePackets element in the message
	 * after this DataObjectOutputStream is closed
	 * @param morePacketsValue
	 * @exception IllegalStateException thrown if an object has already been written to the output stream
	 */
	public abstract void setSIF_MorePackets(YesNo morePacketsValue);

	/**
	 * Sets the value that will be used for the number of the first packet created by the output stream
	 * 
	 * @param packetNumber
	 * @exception IllegalStateException thrown if an object has already been written to the output stream
	 */
	public abstract void setSIF_PacketNumber(int packetNumber);

	public abstract void deferResponse() throws ADKException;

}
