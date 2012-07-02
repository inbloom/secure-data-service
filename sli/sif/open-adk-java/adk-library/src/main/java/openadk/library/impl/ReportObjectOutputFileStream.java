//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.io.*;

import openadk.library.*;
import openadk.library.common.YesNo;
import openadk.library.infra.SIF_Error;
import openadk.library.reporting.*;
import openadk.util.ADKStringUtils;

/**
 *  An implementation of the DataObjectOutputStream interface that packetizes
 *  SIF_Response packets to the agent work directory.
 *
 *  @author Eric Petersen
 *  @version ADK 1.5
 * 	@since ADK 1.5
 */
public class ReportObjectOutputFileStream extends ReportObjectOutputStreamImpl
{
	protected File fFile;
	protected Query fQuery;
	protected ElementDef[] fQueryRestrictions;
	protected String fWorkDir;
	protected int fMaxSize;
	protected int fCurSize;
	protected int fEnvSize;
	protected int fCurPacket = 0;
	protected SIF_Error fError;
	protected OutputStream fOutputStream;
	protected Zone fZone;
	protected SIFVersion fRenderAsVersion;
	protected boolean fDeferResponses;

	/**
	 * This field is set to true in certain cases by SIFResposeSender. If it is set to true,
	 * the final packet that is written by this class will have the SIF_MorePackets flag set to 'Yes'
	 */
	private boolean fMorePackets = false;


	/**
	 * The Query filter used to filter data. If set, each call to write() makes an evaluation
	 * of the data based on this filter. It the data does not meet the conditions of the query,
	 * the object is not written to the output stream.
	 */
	private Query fFilter;


	/**
	 *  Initialize the output stream. This method must be called after creating
	 *  a new instance of this class and before writing any SIFDataObjects to
	 *  the stream.
	 *
	 *  @param zone The Zone associated with messages that will be written to the stream
	 *  @param query The Query restrictions that were specified in the SIF_Request message
	 *  @param requestSourceId The SourceId of the associated SIF_Request message
	 *  @param requestMsgId The MsgId of the associated SIF_Request message
	 *
	 *  @param requestSIFVersion The version of the SIF_Message envelope of the
	 *      SIF_Request message (if specified and different than the SIF_Message
	 *      version, the SIF_Request/SIF_Version element takes precedence).
	 *      SIF_Responses will be encapsulated in a message envelope matching
	 *      this version and SIFDataObject contents will be rendered in this
	 *      version
	 *
	 *  @param maxSize The maximum size of rendered SIFDataObject that will be
	 *      accepted by this stream. If a SIFDataObject is written to the stream
	 *      and its size exceeds this value after rendering the object to an XML
	 *      stream, an ObjectTooLargeException will be thrown by the <i>write</i>
	 *      method
	 */
	@Override
	public void initialize(
		Zone zone,
		Query query,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFVersion,
		int maxSize )
		    throws ADKException
	    {
			fQuery = query;
			initialize( zone, query == null ? null : query.getFieldRestrictions(), requestSourceId, requestMsgId, requestSIFVersion, maxSize );
	    }


	/**
	 *  Initialize the output stream. This method must be called after creating
	 *  a new instance of this class and before writing any SIFDataObjects to
	 *  the stream.<p>
	 *
	 *  @param zone The Zone associated with messages that will be written to the stream
	 *
	 *  @param query The Query that was specified in the SIF_Request message
	 *
	 *  @param requestSourceId The SourceId of the associated SIF_Request message
	 *
	 *  @param requestMsgId The MsgId of the associated SIF_Request message
	 *
	 *  @param requestSIFVersion The version of the SIF_Message envelope of the
	 *      SIF_Request message (if specified and different than the SIF_Message
	 *      version, the SIF_Request/SIF_Version element takes precedence).
	 *      SIF_Responses will be encapsulated in a message envelope matching
	 *      this version and SIFDataObject contents will be rendered in this
	 *      version
	 *
	 *  @param maxSize The maximum size of rendered SIFDataObject that will be
	 *      accepted by this stream. If a SIFDataObject is written to the stream
	 *      and its size exceeds this value after rendering the object to an XML
	 *      stream, an ObjectTooLargeException will be thrown by the <i>write</i>
	 *      method
	 */
	public void initialize(
		Zone zone,
		ElementDef[] fieldRestrictions,
		String requestSourceId,
		String requestMsgId,
		SIFVersion requestSIFVersion,
		int maxSize )
			throws ADKException
	{
		fZone = zone;
		fQueryRestrictions = fieldRestrictions;
		fReqId = requestMsgId;
		fDestId = requestSourceId;
		fMaxSize = maxSize;
		fCurPacket = 0;
		fRenderAsVersion = requestSIFVersion;

		//
		//  Messages written to this stream are stored in the directory
		//  "%adk.home%/work/%zoneId%_%zoneHost%/responses/reports". One or more files
		//  are written to this directory, where each file has the name
		//  "destId.requestId.{packet}.pkt". As messages are written to the
		//  stream, the maxSize property is checked to determine if the size of
		//  the current file will be larger than the maxSize. If so, the file is
		//  closed and the packet number incremented. A new file is then created
		//  for the message and all subsequent messages until maxSize is again
		//  exceeded.
		//
		StringBuffer workDir = new StringBuffer();
		workDir.append( zone.getAgent().getHomeDir() );
		if( workDir.charAt( workDir.length() - 1 ) != File.separatorChar )
			workDir.append( File.separatorChar );
		workDir.append( "work" );
		workDir.append( File.separator );
		workDir.append( ADKStringUtils.safePathString( zone.getZoneId() + "_" + zone.getZoneUrl().getHost() ) );
		workDir.append( File.separator );
		workDir.append( "responses" );
		workDir.append( File.separator );
		workDir.append( "reports" );
		fWorkDir = workDir.toString();

		//  Ensure work directory exists
		File dir = new File(fWorkDir);
		dir.mkdirs();

		//  Get the size of the SIF_Message envelope to determine the actual
		//  packet size we're producing
		fEnvSize = this.calcEnvelopeSize((ZoneImpl)fZone);
	}

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
	 * 	@param reportObjectRefId The RefId of the SIF_ReportObject being streamed
	 *
	 * 	@param info A ReportInfo instance from the <code>com.edustructures.sifworks.reporting</code> package
	 *
	 * 	@exception IllegalStateException is thrown if this method is called more than once
	 */
	public void setReportInfo( String reportObjectRefId, ReportInfo info, Zone zone )
		throws ADKException
	{
		if( fDeferResponses )
			return;

		if( reportObjectRefId == null )
			throw new IllegalArgumentException( "reportObjectRefId parameter cannot be null" );
		if( info == null )
			throw new IllegalArgumentException( "ReportInfo parameter cannot be null" );
		if( fRptInfo != null )
			throw new IllegalStateException( "setReportInfo cannot be called more than once per response stream");


		//	Make a copy of the ReportInfo
		ReportInfo copy = cloneReportInfo(info);
		fRptInfo = copy;
		fReportObjectRefId = reportObjectRefId;

		//  Update the envelope size calculation
		fEnvSize = this.calcEnvelopeSize((ZoneImpl)fZone);

		try
		{
			//	Write the ReportInfo to a ".rpt" file. Each SIF_Response
			//	packet will have a complete ReportInfo as its first
			//	SIF_ReportObject/ReportData element
			BufferedWriter out = SIFIOFormatter.createOutputWriter( new FileOutputStream( fWorkDir + File.separator + fDestId + '.' + fReqId + ".rpt" ));
			SIF_ReportObject ro = new SIF_ReportObject();
			ro.setRefId( reportObjectRefId );
			ro.setReportInfo( fRptInfo );
			SIFWriter writer = new SIFWriter( out,fZone );
			writer.write( ro );
			writer.flush();
			writer.close();
			out.close();
		}
		catch( IOException ioe )
		{
			throw new ADKException( "Failed to persist ReportInfo: " + ioe, zone );
		}
	}

	/**
	 *  Start writing messages to a new packet file. The current packet file
	 *  stream is closed, the packet number incremented by one, and a new packet
	 *  file created. All subsequent calls to the <i>write</i> method will render
	 *  messages to the newly-created packet file.
	 */
	protected void newPacket()
		throws IOException
	{
		close();

		fCurPacket++;
		fCurSize = fEnvSize;

		//  Create output file and stream
		fFile = createOutputFile();
		if( fOutputStream != null ){
			fOutputStream.close();
		}

		fOutputStream = new FileOutputStream( fFile );
	}

	/**
	 *  Create a File descriptor of the current output file
	 */
	protected File createOutputFile()
		throws IOException
	{
		StringBuilder builder = new StringBuilder();

		builder.append( fWorkDir );
		builder.append( File.separator );

		ResponseDelivery.serializeResponsePacketFileName( builder, fDestId, fReqId, fCurPacket, fRenderAsVersion, (fError != null ) );
		return new File( builder.toString() );
	}


	/**
	 *  Write a SIFDataObject to the stream
	 */
	public void write( SIFDataObject data )
		throws ADKException
	{


		// Check to see if the data object is null or if the
		// deferResponses() property has been set
		if( data == null || fDeferResponses ){
			return;
		}

		// Check to see if a SIF_Error has already been written
		if( fError != null ){
			throw new ADKException("A SIF_Error has already been written to the stream",fZone);
		}


		//	ReportObjectOutputStream requires that the user call the setReportInfo
		//	method before writing any SIFDataObjects to the output stream
		if( fRptInfo == null ){
			throw new ADKException("ReportObjectOutputStream.setReportInfo must be called before writing data to the stream",fZone);
		}

		// If the autoFilter property has been set, determine if this object meets the
		// conditions of the filter
		if( fFilter != null ){
			if( !fFilter.evaluate( data ) ){
				// TODO: Perhaps this feature should log any objects not written to the output
				// stream if extended logging is enabled
				return;
			}
		}


		ByteArrayOutputStream buffer = null;

		try
		{
			if( fOutputStream == null || fZone.getProperties().getOneObjectPerResponse() ){
				newPacket();
			}

			//	Temp save of SIFDataObject's original parent
			Element saveParent = data.getParent();
			data.setParent( null );

			//	Encapsulate in a SIF_ReportObject
			SIF_ReportObject ro = new SIF_ReportObject();
			ro.setRefId( fReportObjectRefId );
			ReportData rptData = new ReportData();
			rptData.addChild( data );
			ro.addChild( rptData );

			//  Write to memory stream first so we can determine if the resulting
			//  message will fit in the current packet
			// TODO: The mechanism below is not properly calculating packet size
			// for Unicode characters. The length of the CharArray in the code below
			// may be different than the number of characters written out to the UTF8
			// stream by fWriter.
			buffer = new ByteArrayOutputStream();
			SIFWriter out = new SIFWriter( SIFIOFormatter.createOutputWriter( buffer ),fZone );
			data.setSIFVersion( fRenderAsVersion );
			ro.setSIFVersion( fRenderAsVersion );
		    // TODO: Fix up
		    if( fQuery != null ){
		    	fQuery.setRenderingRestrictionsTo( data );
		    } else if ( fQueryRestrictions != null ){
		    	out.setFilter( fQueryRestrictions );
		    }
			out.write( ro );
			out.flush();
			out.close();

			//	Restore SIFDataObject's original parent
			data.setParent( saveParent );

			if( ( buffer.size() + fCurSize ) > fMaxSize )
			{
				// If the current packet size is equal to the envelope size (e.g. no objects
				// have been written), we have exceeded the size of the buffer and need to abort
				if( fCurSize == fEnvSize ) {
					String errorMessage = "Publisher result data in packet " + fCurPacket + " too large (" +
						buffer.size()+" [Data] + "+fEnvSize+" [Sif Envelope] > " + fMaxSize + ")";
					if( fZone.getProperties().getOneObjectPerResponse() ){
						errorMessage +=  " [1 Object per Response Packet]";
					}
					throw new ADKException( errorMessage, fZone );
				}

				//  Create new packet for this object
				newPacket();
			}

			if( ( ADK.debug & ADK.DBG_MESSAGE_CONTENT ) != 0 ){
				// Convert the binary data to a string for logging purposes
				((ZoneImpl)fZone).log.debug( "Writing SIF_ReportObject to SIF_Response packet #" + fCurPacket + ":\r\n" + buffer.toString( SIFIOFormatter.CHARSET.name() )  );
			}

			//  Write to current packet
			buffer.writeTo( fOutputStream );
			fCurSize += buffer.size();
		}
		catch( IOException ioe )
		{
			throw new ADKException("Failed to write ReportPublisher result data (packet "+fCurPacket+") to "+fFile.getAbsolutePath()+": " + ioe, fZone );
		}
		finally
		{
			if( buffer != null ){
				try {
					buffer.close();
				} catch (IOException unexpectedError ) {
					fZone.getLog().warn( "Unexpected Error closing output stream: " + unexpectedError.getMessage(), unexpectedError );

				}
			}
		}
	}

	/**
	 *  Called when the Publisher.onQuery method has thrown a SIFException,
	 *  indicating an error should be returned in the SIF_Response body
	 */
	public void setError( SIF_Error error )
		throws ADKException
	{
		fError = error;

		//
		//  Write a SIF_Response packet that contains only this SIF_Error
		//
		ByteArrayOutputStream buffer = null;

		try
		{
			newPacket();

			//  Write to memory stream first
			buffer = new ByteArrayOutputStream();
			SIFWriter out = new SIFWriter( buffer,fZone );
			out.suppressNamespace( true );
			out.write( fRenderAsVersion, error );
			out.flush();
			out.close();

			//  Write to current packet
			buffer.writeTo( fOutputStream );
			fCurSize += buffer.size();
		}
		catch( IOException ioe )
		{
			throw new ADKException("Failed to write ReportPublisher SIF_Error data (packet "+fCurPacket+") to "+fFile.getAbsolutePath()+": " + ioe, fZone );
		}
		finally
		{
			if( buffer != null )
				try {
					buffer.close();
				} catch (IOException unexpectedError ) {
					fZone.getLog().warn( "Unexpected Error closing output stream: " + unexpectedError.getMessage(), unexpectedError );

				}
		}
	}

	public void commit()
		throws ADKException
	{
		try
		{
			if( fDeferResponses )
			{
				abort();
			}
			else
			{
				//  If no objects or SIF_Errors have been written to the stream, we still
				//  need to return an empty SIF_Response to the ZIS.
				if( fOutputStream == null ) {
					try {
						newPacket();
						close();
					} catch( IOException ioe ) {
						throw new ADKException( "Could not commit the stream because of an IO error writing an empty SIF_Response packet: " + ioe, fZone );
					}
				}

				String responseFileName = ResponseDelivery.serializeResponseHeaderFileName( fDestId, fReqId, fMorePackets );

				//  Write out "destId.requestId." file to signal the Publisher has finished
				//  writing all responses successfully. This file will hang around until
				//  all "requestId.{packet}.pkt" files have been sent to the ZIS by the ADK,
				//  a process that could occur over several agent sessions if the agent
				//  is abruptly terminated.
				//
				String fileName = fWorkDir + File.separator + responseFileName;
				try {
					new File( fileName ).createNewFile();
				} catch( IOException ioe ) {
					fZone.getLog().warn( "Unable to create SIF_Response header file: " + fileName + ". " + ioe.getMessage(), ioe );
				}

				//  Process response packets
				((ZoneImpl)fZone).getReportResponseDelivery().process();
			}
		}
		finally
		{
			fZone = null;
		}
	}

	public void abort()
		throws ADKException
	{
		final String _filter = fDestId + "." + fReqId;

		//  Delete "destId.requestId.*" files
		File dir = new File( fWorkDir );
		File[] toDelete = dir.listFiles(
			new FilenameFilter() {
				public boolean accept( File file, String name ) {
					return name.startsWith(_filter);
				}
			}
		);

		if( toDelete != null ) {
			for( int i = 0; i < toDelete.length; i++ )
				toDelete[i].delete();
		}

		fZone = null;
	}

	public void close()
		throws IOException
	{
		if( fOutputStream != null ) {
			fOutputStream.flush();
			fOutputStream.close();
		}
	}


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
		throws ADKException
	{
		fDeferResponses = true;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.DataObjectOutputStream#setAutoFilter(com.edustructures.sifworks.Query)
	 */
	public void setAutoFilter(Query filter) {
		fFilter = filter;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.DataObjectOutputStreamImpl#getSIF_MorePackets()
	 */
	@Override
	public YesNo getSIF_MorePackets() {
		return fMorePackets ? YesNo.YES : YesNo.NO;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.DataObjectOutputStreamImpl#getSIF_PacketNumber()
	 */
	@Override
	public int getSIF_PacketNumber() {
		// Special case: If newPacket() has not been called yet,
		// fWriter will be null, in which case, we need to add one to fCurPacket
		// to get the actual value of the packet
		if( fOutputStream == null ){
			return fCurPacket + 1;
		} else {
			return fCurPacket;
		}
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.DataObjectOutputStreamImpl#setSIF_MorePackets(com.edustructures.sifworks.common.YesNo)
	 */
	@Override
	public void setSIF_MorePackets(YesNo morePacketsValue) {
		fMorePackets =  morePacketsValue.equals( YesNo.YES );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.DataObjectOutputStreamImpl#setSIF_PacketNumber(int)
	 */
	@Override
	public void setSIF_PacketNumber(int packetNumber) {
		// If fWriter is not initialized, set the fCurPacket value to
		// 1 value less (allows it to be properly incremented in newPacket())
		if( fOutputStream == null ){
			fCurPacket = packetNumber - 1;
		} else {
			throw new IllegalStateException("Cannot set the packet number after objects have already been written" );
		}
	}


}
