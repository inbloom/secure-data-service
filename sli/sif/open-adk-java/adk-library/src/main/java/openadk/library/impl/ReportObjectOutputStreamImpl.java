//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.io.*;
import java.util.Calendar;

import openadk.library.*;
import openadk.library.infra.*;
import openadk.library.reporting.*;

/**
 *  The abstract base class for SIF_ReportObject OutputStreams to which 
 * 	ReportPublishers write the results of a query.<p>
 * 
 *  This class extends DataObjectOutputStreamImpl to handle packetizing logic that 
 * 	is specific to SIF_Responses that contain SIF_ReportObject payloads.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.5
 * 	@since ADK 1.5
 */
public abstract class ReportObjectOutputStreamImpl extends DataObjectOutputStreamImpl implements ReportObjectOutputStream
{
	protected ReportInfo fRptInfo;
	protected String fReportObjectRefId;

	/**
	 *  Construct a new ReportObjectOutputStream.<p>
	 *  @return A new ReportObjectOutputStream object, which will always be a
	 *      concrete class of DataObjectOutputStreamImpl as defined by the
	 *      <code>adkglobal.factory.DataObjectOutputStream</code> system property.
	 *
	 */
	public static DataObjectOutputStreamImpl newInstance()
		throws ADKException
	{
		String cls = System.getProperty("adkglobal.factory.ReportObjectOutputStream");
		if( cls == null )
			cls = "openadk.library.impl.ReportObjectOutputFileStream";

		try
		{
			return (DataObjectOutputStreamImpl)Class.forName(cls).newInstance();
		}
		catch( Throwable thr )
		{
			throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
		}
	}

	/**
	 *  Calculate the size of a SIF_Response minus the SIF_ReportObject content.
	 */
	protected int calcEnvelopeSize( ZoneImpl zone )
	{
		int size = 400;

		try
		{
			//	Calc the size of a SIF_Response with an empty SIF_ObjectData...
			SIF_Response rsp = new SIF_Response();
			rsp.setSIF_MorePackets( "Yes" );
			rsp.setSIF_RequestMsgId( fReqId );
			rsp.setSIF_PacketNumber( 100000000 );
			SIF_ObjectData data = new SIF_ObjectData();
			data.setTextValue(" ");
			rsp.setSIF_ObjectData( data );

			//	...plus the size of its SIF_Header
			SIF_Header hdr = rsp.getHeader();
			hdr.setSIF_Timestamp( Calendar.getInstance() );
			hdr.setSIF_MsgId("012345678901234567890123456789012");
			hdr.setSIF_SourceId(zone.getAgent().getId());
			hdr.setSIF_Security(zone.getFDispatcher().secureChannel());
			hdr.setSIF_DestinationId(fDestId);
			
			//	...plus the size of its SIF_ReportObject/ReportInfo
			ReportInfo copy;
			if( fRptInfo != null ) {
				copy = cloneReportInfo( fRptInfo ); 
			} else {
				copy = new ReportInfo();
			}
			SIF_ReportObject roInfo = new SIF_ReportObject();
			roInfo.setRefId("012345678901234567890123456789012");
			roInfo.setReportInfo( copy );
						
			//	...plus the size of an empty SIF_ReportObject/ReportData
			SIF_ReportObject roData = new SIF_ReportObject();
			ReportData rd = new ReportData();
//			rd.setTextValue( " " );
			
			//	Write out everything to a buffer to get the size in bytes
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			SIFWriter out = new SIFWriter( tmp, zone );
			out.write( rsp );
			// JEN Packet buffer size fix add - xmlns="http://www.sifinfo.org/infrastructure/2.x" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			out.suppressNamespace(false);
			// JEN
			out.write( roInfo );
			out.write( roData );
			out.flush();
			size = tmp.size() + 20 /* just in case - make it 20 */;
			out.close();
			tmp.close();
		}
		catch( Exception ex )
		{
			System.out.println( "Error calculating packet size: " + ex );
			ex.printStackTrace();
		}

		return size;
	}

	protected ReportInfo cloneReportInfo(ReportInfo info) {
		ReportInfo copy;
		try{
			copy =(ReportInfo) info.clone();
		} catch( CloneNotSupportedException cnse ){
			throw new IllegalStateException( "Unable to clone ReportInfo object: " + cnse, cnse );
		}
		return copy;
	}
}
