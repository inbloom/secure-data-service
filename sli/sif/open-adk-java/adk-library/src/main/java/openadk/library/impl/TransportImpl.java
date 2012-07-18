//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.threadpool.ThreadPoolManager;


/**
 *  Abstract base class for all Transport implementations.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public abstract class TransportImpl implements Transport, Cloneable
{
	protected TransportProperties fProps;

	protected TransportImpl( TransportProperties props )
	{
		fProps = props;
	}

	/**
	 *  Creates an IProtocolHandler for the zone associated with this Transport instance.
	 */
	public abstract IProtocolHandler createProtocolHandler( AgentMessagingMode mode )
		throws ADKTransportException;


	public TransportProperties getProperties() {
		return fProps;
	}
}
