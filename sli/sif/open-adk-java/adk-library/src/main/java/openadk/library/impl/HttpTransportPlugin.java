//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADKTransportException;
import openadk.library.HttpProperties;
import openadk.library.TransportProperties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class HttpTransportPlugin extends TransportPlugin
{
	@Override
	public String getProtocol() {
		return "http";
	}

	@Override
	public Transport newInstance( TransportProperties props )
		throws ADKTransportException
	{
	    return new HttpTransport( (HttpProperties)props );
	}

	@Override
	public boolean isInternal() {
		return false;
	}

	@Override
	public TransportProperties createProperties() {
		return new HttpProperties();
	}

}
