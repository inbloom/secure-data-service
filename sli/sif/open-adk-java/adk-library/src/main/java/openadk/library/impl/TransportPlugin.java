//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADKTransportException;
import openadk.library.TransportProperties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public abstract class TransportPlugin
{
	/**
	 * The protocol suported by this transport plugin (e.g. "http" )
	 * @return the protocol supported by this transport plugin (e.g "http")
	 */
	public abstract String getProtocol();

	/**
	 * Creates a new instance of this transport
	 * @param props The transport properties to use for this transport
	 * @return a new instance of this transport
	 * @throws ADKTransportException
	 */
	public abstract Transport newInstance( TransportProperties props )
		throws ADKTransportException;

	/**
	 * Returns true if this protocol is internal to the ADK
	 * @return True if this protocol is internal to the ADK
	 */
	public abstract boolean isInternal();
	
    /**
     * CreateProperties method is used to explicitly instantiate
     * a new TransportProperties object 
     * @return a newly instantiated properties object for this transport
     */
    public abstract TransportProperties createProperties();
    
   }
