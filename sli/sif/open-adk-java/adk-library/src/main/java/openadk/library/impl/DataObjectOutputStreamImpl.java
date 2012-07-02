//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.*;
import java.util.Calendar;

import openadk.library.*;
import openadk.library.common.YesNo;
import openadk.library.infra.*;

/**
 *  The abstract base class for OutputStreams to which Publishers write the
 *  results of a query. Although this class has a generic name that might imply
 *  it is a generic output stream for SIFDataObjects, it is really very specific
 *  to SIF_Request processing and is not intended to be used generically.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public abstract class DataObjectOutputStreamImpl implements DataObjectOutputStream, BaseObjectOutputStream
{
	protected String fReqId;
	protected String fDestId;

	/**
	 *  Construct a new DataObjectOutputStream
	 *  @return A new DataObjectOutputStream object, which will always be a
	 *      an instanceof DataObjectOutputStreamImpl as defined by the
	 *      <code>adkglobal.factory.DataObjectOutputStream</code> system property.
	 *
	 */
	public static DataObjectOutputStreamImpl newInstance()
		throws ADKException
	{
		String cls = System.getProperty("adkglobal.factory.DataObjectOutputStream");
		if( cls == null )
			cls = "openadk.library.impl.DataObjectOutputFileStream";

		try
		{
			return (DataObjectOutputStreamImpl)Class.forName(cls).newInstance();
		}
		catch( Throwable thr )
		{
			throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
		}
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#initialize(com.edustructures.sifworks.Zone, com.edustructures.sifworks.Query, java.lang.String, java.lang.String, com.edustructures.sifworks.SIFVersion, int)
	 */
	public abstract void initialize(
		Zone zone,
		Query query,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFMessageVersion,
		int maxSize )
		    throws ADKException;
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#initialize(com.edustructures.sifworks.Zone, com.edustructures.sifworks.ElementDef[], java.lang.String, java.lang.String, com.edustructures.sifworks.SIFVersion, int)
	 */
	public abstract void initialize(
		Zone zone,
		ElementDef[] queryRestrictions,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFMessageVersion,
		int maxSize )
		    throws ADKException;

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#close()
	 */
	public abstract void close()
		throws IOException;

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#commit()
	 */
	public abstract void commit()
		throws ADKException;

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#abort()
	 */
	public abstract void abort()
		throws ADKException;

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#write(com.edustructures.sifworks.SIFDataObject)
	 */
	public abstract void write( SIFDataObject data )
		throws ADKException;

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#setError(com.edustructures.sifworks.infra.SIF_Error)
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
			SIF_Response rsp = new SIF_Response();
			rsp.setSIF_MorePackets( "Yes" );
			rsp.setSIF_RequestMsgId( fReqId );
			rsp.setSIF_PacketNumber( 100000000 );
			SIF_ObjectData data = new SIF_ObjectData();
			data.setTextValue(" ");
			rsp.setSIF_ObjectData( data );

			SIF_Header hdr = rsp.getHeader();
			hdr.setSIF_Timestamp( Calendar.getInstance() );
			hdr.setSIF_MsgId("012345678901234567890123456789012");
			hdr.setSIF_SourceId(zone.getAgent().getId());
			hdr.setSIF_Security(zone.getFDispatcher().secureChannel());
			hdr.setSIF_DestinationId(fDestId);

			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			SIFWriter out = new SIFWriter( tmp, zone );
			// JEN Packet buffer size fix add - xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			out.suppressNamespace(false);
			// JEN
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
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#getSIF_MorePackets()
	 */
	public abstract YesNo getSIF_MorePackets();

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#getSIF_PacketNumber()
	 */
	public abstract int getSIF_PacketNumber();

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#setSIF_MorePackets(com.edustructures.sifworks.common.YesNo)
	 */
	public abstract void setSIF_MorePackets(YesNo morePacketsValue);

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseObjectOutputStream#setSIF_PacketNumber(int)
	 */
	public abstract void setSIF_PacketNumber(int packetNumber);
	
}
