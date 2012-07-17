//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import openadk.library.ADKException;
import openadk.library.MessageInfo;
import openadk.library.SIFElement;
import openadk.library.common.YesNo;
import openadk.library.infra.SIF_Error;


/**
*  This interface defines the methods to read a Services Input Stream.<p>
*
*  @author Andrew Elmhorst
*  @version ADK 2.3
*/
public interface ServiceObjectOutputStream {

	/**
	 * Write a SIFElement to the stream
	 *
	 * @param data
	 *            A SIFElement instance to write to the output stream
	 */
	public void write(SIFElement data) throws ADKException;

	/**
	 * Defer sending SIF_Response messages and ignore any objects written to
	 * this stream.
	 * <p>
	 *
	 * See the
	 * {@link com.edustructures.sifworks.Services.SIFServiceOutputSender} class
	 * comments for more information about using this method.
	 * <p>
	 *
	 * @param info
	 *
	 * @see com.edustructures.sifworks.SIFServiceOutputSender
	 *
	 * @since ADK 1.5.1
	 */
	public ServiceOutputInfo deferResponse(MessageInfo info)
			throws ADKException;

	/**
	 * Set an error condition
	 * @param SIF_Error
	 * @exception ADKException
	 */
	public void setError(SIF_Error error) throws ADKException;

	/**
	 * Close stream
	 * @exception IOException
	 */
	public void close() throws IOException;

	/**
	 * Commit stream 
	 * @exception ADKException
	 */
	public void commit() throws ADKException;

	/**
	 * Set the packet number. A stream can send more than one packet
	 * as part of a response
	 * @param packetNumber
	 */
	public void setSIF_PacketNumber(int packetNumber);

	/**
	 * Sets Indicator whether stream has more to send
	 * @param YesNo
	 */
	public void setSIF_MorePackets(YesNo morePacketsValue);

	/**
	 * Indicates whether stream has more to send
	 *  @return YesNo
	 */
	public YesNo getSIF_MorePackets();

	/**
	 * Gets the packet number the when stream sends one than one packet
	 * as part of a response
	 *  @return packet number
	 */
	public int getSIF_PacketNumber();
	
	
	public void writeBuffer(ByteArrayOutputStream buffer) throws IOException;
	


}
