//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;

import openadk.library.common.YesNo;
import openadk.library.impl.DataObjectOutputStreamImpl;
import openadk.library.infra.*;


/**
 * 	Helper class to send SIF_Response messages when an agent defers automatic
 * 	sending of responses in its implementation of the Publisher.onRequest
 * 	message handler. This class is designed to be used when the {@link openadk.library.DataObjectOutputStream#deferResponse}
 *  	method is called during SIF_Request processing, and must be used outside of the
 * 	Publisher.onRequest message handler.<p>
 * 
 * 	By design, the ADK automatically sends one or more SIF_Response messages to
 * 	the zone when control is returned from the Publisher.onRequest  
 * 	message handler. SIF_Responses are sent in a background thread without any
 * 	further intervention on the agent's part. The ADK takes care of proper data
 * 	object rendering and packetizing based on the parameters of the SIF_Request
 * 	message, and includes a SIF_Error element in the payload if an exception is
 * 	thrown from the onRequest method.<p>
 * 
 *  Although this behavior is convenient and very appropriate for most agent 
 * 	implementations, there are cases when agents require greater control over SIF 
 * 	Request &amp; Response messaging. Specifically, agents sometimes need the 
 * 	ability to decouple SIF_Response processing from SIF_Request processing. 
 * 	For example, implementing the SIF 1.5 StudentLocator message choreography may 
 * 	require that SIF_Response messages are sent hours or days after the initial 
 * 	SIF_Request is received. Or, an agent might queue requests in a database
 * 	table and work on them later when it has available resources. Both of these 
 * 	scenarios can be achieved by calling the {@link openadk.library.DataObjectOutputStream#deferResponse}
 * 	method on the DataObjectOutputStream passed to the Publisher.onRequest 
 * 	message handler. This method signals the ADK to ignore any SIFDataObjects 
 * 	written to the stream and to defer the sending of SIF_Response messages. The 
 * 	agent must send its own SIF_Response messages at a later time by using this 
 * 	SIFResponseSender helper class<p>
 * 
 * 	To use this class,<p>
 * 
 * 	<ul>
 *		<li>
 *			When the agent is ready to send SIF_Response messages for a SIF_Request
 *			that was received at an earlier time, instantiate a SIFResponseSender.
 *		</li>
 *		<li>
 *			Call the {@link #open} method and pass it the Zone you wish to send 
 *			SIF_Response messages to. You must also pass the SIF_Version and 
 *			SIF_MaxBufferSize value from the original SIF_Request message. (Be sure
 *			to obtain these values from the <code>SIFMessageInfo</code> parameter 
 *			in your Publisher.onRequest implementation so you can pass them to 
 *			the {@link #open} method when using this class.)
 *		</li>
 *		<li>
 *			Repeatedly call the {@link #write(SIFDataObject)} method, once for each 
 *			SIFDataObject that should be included in the SIF_Response stream
 *		</li>
 *		<li>
 *			Call the {@link #write(SIF_Error)} method to include a SIF_Error element 
 *			in the SIF_Response stream
 *		</li>
 *		<li>
 *			When finished, call the {@link #close} method. The ADK will automatically 
 *			package the objects into one or more SIF_Response packets in the same way it 
 *			does for normal request/response processing via the Publisher.onRequest 
 *			method.
 *		</li>
 *	</ul>
 *
 *	@since ADK 1.5.1
 */
public class SIFResponseSender
{
	protected Zone fZone;
	protected DataObjectOutputStreamImpl fOut = null;
	
	/**
	 * 	Open the SIFResponseSender to send SIF_Response messages to a specific zone.
	 * 
	 * 	@param zone The zone to send messages to
	 * 
	 * 	@param sifRequestMsgId The SIF_MsgId from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getMsgId}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method. NOTE: Do not call {@link openadk.library.SIFMessageInfo#getSIFRequestMsgId()}
	 * 		as it will return a <code>null</code> value and is only intended to be called
	 * 		on SIF_Response messages.
	 * 
	 * 	@param sifRequestSourceId The SIF_SourceId from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getSourceId()}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param sifVersion The SIF_Version value from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getSIFRequestVersion}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param maxBufSize The SIF_MaxBufferSize value from the original SIF_Request 
	 * 		message. You can obtain this by calling {@link openadk.library.SIFMessageInfo#getMaxBufferSize}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param fieldRestrictions The Query object representing the SIF_Request that this SIF_Response
	 * is being generated for
	 * 
	 * 	@exception IllegalArgumentException is thrown if any of the parameter are invalid
	 * 
	 * 	@exception ADKException is thrown if an error occurs preparing the output stream
	 */
	public void open( 
		Zone zone, 
		String sifRequestMsgId, 
		String sifRequestSourceId, 
		SIFVersion sifVersion, 
		int maxBufferSize, 
		Query srcQuery )
			throws ADKException
	{
		fZone = zone;
		fOut = DataObjectOutputStreamImpl.newInstance();
		fOut.initialize( zone, srcQuery, sifRequestSourceId, sifRequestMsgId, sifVersion, maxBufferSize );
	}
	
	
	/**
	 * 	Open the SIFResponseSender to send SIF_Response messages to a specific zone.
	 * 
	 * 	@param zone The zone to send messages to
	 * 
	 * 	@param sifRequestMsgId The SIF_MsgId from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getMsgId}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method. NOTE: Do not call {@link openadk.library.SIFMessageInfo#getSIFRequestMsgId()}
	 * 		as it will return a <code>null</code> value and is only intended to be called
	 * 		on SIF_Response messages.
	 * 
	 * 	@param sifRequestSourceId The SIF_SourceId from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getSourceId()}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param sifVersion The SIF_Version value from the original SIF_Request message.
	 * 		You can obtain this by calling {@link openadk.library.SIFMessageInfo#getSIFRequestVersion}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param maxBufSize The SIF_MaxBufferSize value from the original SIF_Request 
	 * 		message. You can obtain this by calling {@link openadk.library.SIFMessageInfo#getMaxBufferSize}
	 * 		on the SIFMessageInfo parameter passed to the Publisher.onRequest
	 * 		method.
	 * 
	 * 	@param fieldRestrictions An optional array of ElementDef constants, obtained or 
	 * 		reconstructed from the {@link openadk.library.Query#getFieldRestrictions}
	 * 		method from the original SIF_Request, that identify the subset of SIF elements 
	 * 		to include in the data objects written to SIF_Response messages. If this array 
	 * 		is provided, data objects will only have those elements specified; otherwise 
	 * 		data objects contain all of their elements.
	 * 
	 * 	@exception IllegalArgumentException is thrown if any of the parameter are invalid
	 * 
	 * 	@exception ADKException is thrown if an error occurs preparing the output stream
	 */
	public void open( 
		Zone zone, 
		String sifRequestMsgId, 
		String sifRequestSourceId, 
		SIFVersion sifVersion, 
		int maxBufferSize, 
		ElementDef[] fieldRestrictions )
			throws ADKException
	{
		fZone = zone;
		fOut = DataObjectOutputStreamImpl.newInstance();
		fOut.initialize( zone, fieldRestrictions, sifRequestSourceId, sifRequestMsgId, sifVersion, maxBufferSize );
	}
	
	/**
	 * 	Write a SIFDataObject to the output stream
	 */
	public void write( SIFDataObject sdo )
		throws ADKException
	{
		_checkOpen();
		
		fOut.write( sdo );
	}
	
	/**
	 * 	Write a SIF_Error to the output stream
	 * 	@param error A SIF_Error instance
	 */
	public void write( SIF_Error error )
		throws ADKException
	{
		_checkOpen();
		
		fOut.setError( error );
	}
	
	/**
	 *	Close the stream and send one or more SIF_Response packets to the zone. 	
	 */
	public void close()
		throws ADKException
	{
		_checkOpen();
		
		try {
			fOut.close();
		} catch( IOException ioe ) {
			throw new ADKException( "Failed to close SIFResponseSender stream: " + ioe, fZone );
		}
		
		fOut.commit();
	}
	
	private void _checkOpen() {
		if( fOut == null )
			throw new IllegalStateException( "SIFResponseSender is not open" );
	}

	/**
	 *  Allows the starting packet number for SIF_Responses to be set. <p>
	 * 
	 *  By default, the SIFResponseSender class will automatically set the starting 
	 *  packet number to 1 and increment the number automatically for each packet. 
	 *  However, some agents may need to respond to SIF_Requests with multiple, asynchronous
	 *  responses. In that case, the agent developer is responsible for keeping track of the
	 *  packet numbers that were previously sent and setting the correct starting packet number
	 *  for the next set of SIF_Response packtets.
	 *  
	 *  @see #getSIF_PacketNumber()
	 *  @param packetNumber The SIF_PacketNumber value that should be set for the next SIF_Response
	 *  packet
	 *  
	 *  @exception IllegalStateException thrown if this property is set after objects have
	 * already been written
	 */
	public void setSIF_PacketNumber( int packetNumber) {
		_checkOpen();
		fOut.setSIF_PacketNumber( packetNumber );
	}

	/**
	 * Allows the SIF_MorePackets value for SIF_Responses to be set.<p>
	 * 
	 *  By default, the SIFResponseSender class will automatically close out the 
	 *  SIF_Response stream when SIFResponseSender is closed by setting the 
	 *  SIF_MorePackets value to "No" on the final packet. However, if the SIF_Response
	 *  stream should be kept open, this value will be used on the last SIF_Response
	 *  packet that is generated by the SIFResponseSender class.
	 * 
	 * @param morePacketsValue
	 */
	public void setSIF_MorePackets(YesNo morePacketsValue) {
		_checkOpen();
		fOut.setSIF_MorePackets( morePacketsValue );
	}

	/**
	 * Gets the SIF_PacketNumber of the current SIF_Response packet
	 * @return
	 */
	public int getSIF_PacketNumber() {
		_checkOpen();
		return fOut.getSIF_PacketNumber();
	}

	/**
	 * Gets the value that will be set on the final SIF_Response packet
	 * when the SIFResponseSender is closed.
	 * @return
	 */
	public YesNo getSIF_MorePackets() {
		_checkOpen();
		return fOut.getSIF_MorePackets();
	}
}
