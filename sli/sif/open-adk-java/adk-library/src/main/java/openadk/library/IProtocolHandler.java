//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


import java.io.Reader;

import openadk.library.*;
import openadk.library.impl.ZoneImpl;
import openadk.library.infra.SIF_Protocol;

/**
 *  A class that interacts with the MessageDispatcher to perform protocol-specific
 *  messaging implements the IProtocolHandler interface. There must be a protocol
 *  handler for each communication protocol supported by the ADK. By default
 *  the HttpProtocolHandler exists to support HTTP and HTTPS.
 */
public interface IProtocolHandler
{
	/**
	 * Opens the protocol handler for this zone
	 * @param zone
	 * @throws ADKException
	 */
	public void open( ZoneImpl zone )
		throws ADKException;

	/**
	 * Closes the protocol handler for this zone
	 * @param zone
	 */
	public void close( ZoneImpl zone );
	
	/**
	 * Returns true if the protocol and underlying transport are currently active
	 * for this zone
	 * @param zone
	 * @return True if the protocol handler and transport are active
	 * @throws ADKTransportException 
	 */
	public boolean isActive( ZoneImpl zone ) throws ADKTransportException;

	/**
	 * The name of the protocol handler
	 * @return The name of the protocol handler
	 */
	public String getName();

	/**
	 * 
	 * @throws Exception
	 */
	public void start()
		throws Exception;

	/**
	 * @throws Exception
	 */
	public void shutdown()
		throws Exception;
	
	/**
	 *  Creates the SIF_Protocol object that will be included with a SIF_Register
	 *  message sent to the zone associated with this Transport.<p>
	 *
	 *  The base class implementation creates an empty SIF_Protocol with zero
	 *  or more SIF_Property elements according to the parameters that have been
	 *  defined by the client via setParameter. Derived classes should therefore
	 *  call the superclass implementation first, then add to the resulting
	 *  SIF_Protocol element as needed.
	 * @param zone
	 * @return A SIF_Protocol object
	 * @throws ADKTransportException
	 */
	public SIF_Protocol makeSIF_Protocol( Zone zone, SIFVersion version )
		throws ADKTransportException;
	

	/**
	 *  Send a SIF infrastructure message
	 * @param msg 
	 * @return A string representing the SIF_Ack received from the ZIS
	 * @throws ADKTransportException 
	 * @throws ADKMessagingException 
	 */
	public String send( String msg )
		throws ADKTransportException,
			   ADKMessagingException;

	/**
	 *  Send a SIF infrastructure message
	 * @param msg 
	 * @param length 
	 * @return A string representing the SIF_Ack received from the ZIS
	 * @throws ADKTransportException 
	 * @throws ADKMessagingException 
	 */
	public String send( Reader msg, int length )
		throws ADKTransportException,
			   ADKMessagingException;

	
}
