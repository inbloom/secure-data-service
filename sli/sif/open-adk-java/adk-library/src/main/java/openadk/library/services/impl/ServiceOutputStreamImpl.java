//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import openadk.library.ADKException;
import openadk.library.DataObjectOutputStream;
import openadk.library.ElementDef;
import openadk.library.Query;
import openadk.library.SIFDataObject;
import openadk.library.SIFElement;
import openadk.library.SIFVersion;
import openadk.library.SIFWriter;
import openadk.library.Zone;
import openadk.library.common.YesNo;
import openadk.library.impl.BaseObjectOutputStream;
import openadk.library.impl.ZoneImpl;
import openadk.library.infra.SIF_Error;
import openadk.library.infra.SIF_Header;
import openadk.library.infra.SIF_ObjectData;
import openadk.library.infra.SIF_Response;
import openadk.library.infra.SIF_ServiceOutput;


/*
 * Clone of 
 */
public abstract class ServiceOutputStreamImpl implements BaseObjectOutputStream {
	protected String fReqId;
	protected String fDestId;

	/**
	 *  Construct a new DataObjectOutputStream
	 *  @return A new DataObjectOutputStream object, which will always be a
	 *      an instanceof DataObjectOutputStreamImpl as defined by the
	 *      <code>adkglobal.factory.DataObjectOutputStream</code> system property.
	 *
	 */
	public static ServiceOutputStreamImpl newInstance()
		throws ADKException
	{
		String cls = System.getProperty("adkglobal.factory.ServiceOutputStream");
		if( cls == null )
			cls = "openadk.library.services.impl.ServiceOutputFileStream";

		try
		{
			return (ServiceOutputStreamImpl)Class.forName(cls).newInstance();
		}
		catch( Throwable thr )
		{
			throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
		}
	}

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
	public abstract void initialize(
		Zone zone,
		Query query,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFMessageVersion,
		int maxSize )
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
	public abstract void initialize(
		Zone zone,
		ElementDef[] queryRestrictions,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFMessageVersion,
		int maxSize )
		    throws ADKException;

	public abstract void close()
		throws IOException;

	/**
	 *  Called by the class framework when the Publisher.onQuery method has
	 *  returned successfully
	 */
	public abstract void commit()
		throws ADKException;

	/**
	 *  Called by the class framework when the Publisher.onQuery method has
	 *  thrown an exception other than SIFException
	 */
	public abstract void abort()
		throws ADKException;

	/**
	 *  Write a SIFDataObject to the stream
	 */
	public abstract void write( SIFElement data )
		throws ADKException;

	/**
	 *  Called when the Publisher.onQuery method has thrown a SIFException,
	 *  indicating an error should be returned in the SIF_Response body
	 */
	public abstract void setError( SIF_Error error )
		throws ADKException;

	/**
	 *  Calculate the size of a SIF_Response minus the SIF_ObjectData content.
	 */
	protected int calcEnvelopeSize( ZoneImpl zone )
	{
		int size = 400;

		try
		{
			SIF_ServiceOutput rsp = new SIF_ServiceOutput();
			rsp.setSIF_MorePackets( "Yes" );
			rsp.setSIF_ServiceMsgId(fReqId);
			rsp.setSIF_PacketNumber( 100000000 );
			rsp.setSIF_Body( " " );

			SIF_Header hdr = rsp.getHeader();
			hdr.setSIF_Timestamp( Calendar.getInstance() );
			hdr.setSIF_MsgId("012345678901234567890123456789012");
			hdr.setSIF_SourceId(zone.getAgent().getId());
			hdr.setSIF_Security(zone.getFDispatcher().secureChannel());
			hdr.setSIF_DestinationId(fDestId);

			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			SIFWriter out = new SIFWriter( tmp, zone );
			out.write( rsp );
			out.flush();
			size = tmp.size() + 20 /* just in case */;
			out.close();
			tmp.close();
		}
		catch( Exception ex )
		{
			zone.log.warn( "Error calculating packet size: " + ex, ex );
		}

		return size;
	}
	
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

}
