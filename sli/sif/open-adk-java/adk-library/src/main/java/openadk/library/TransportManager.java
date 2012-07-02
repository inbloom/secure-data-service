//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import openadk.library.impl.Transport;

/**
 * Manages the state of all open transports used by an Agent instance. 
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.1
 *
 */
public interface TransportManager {

	/**
	 * Gets the Transport instance that has been instantiated for the specified protocol
	 * @param protocol The protocol to retrieve the transport instance for (e.g. "http")
	 * @return The transport object for the specified protocol
	 * @throws ADKTransportException if the protocol is not supported by the ADK
	 */
	Transport getTransport(String protocol) throws ADKTransportException;

}
