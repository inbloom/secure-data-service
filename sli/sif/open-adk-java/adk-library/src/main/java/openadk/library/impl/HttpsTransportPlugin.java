//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADKTransportException;
import openadk.library.HttpsProperties;
import openadk.library.TransportProperties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class HttpsTransportPlugin extends TransportPlugin
{
	@Override
	public String getProtocol() {
		return "https";
	}

	@Override
	public Transport newInstance( TransportProperties props )
		throws ADKTransportException
	{
	    return new HttpTransport( (HttpsProperties)props );
	}

	@Override
	public boolean isInternal() {
		return false;
	}

	@Override
	public TransportProperties createProperties() {
		return new HttpsProperties();
	}
}
