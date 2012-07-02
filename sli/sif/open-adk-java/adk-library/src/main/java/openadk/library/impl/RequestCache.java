//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.infra.*;
import openadk.library.services.ServiceRequestInfo;


/**
 *  Stores the message ID and SIF Data Object type of each pending SIF_Request
 *  message. The RequestCache is a global resource of the class framework. It is
 *  only necessary because of an inconsistency in the SIF 1.0r2 Specification in
 *  which a SIF_Response with a SIF_Error must have an empty SIF_ObjectData
 *  element, which prevents the framework from determining the associated object
 *  type. It needs to know the object type to dispatch the SIF_Response to the
 *  appropriate QueryResults message handler when Topics are being used.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public abstract class RequestCache
{
	protected static RequestCache sSingleton = null;

	/**
	 *  Protected constructor; clients must use getInstance
	 */
	protected RequestCache() { }

	/**
	 *  Get a RequestCache instance
	 */
	public static synchronized RequestCache getInstance( Agent agent )
		throws ADKException
	{
		if( sSingleton == null )
		{
			String cls = System.getProperty("adkglobal.factory.RequestCache");
			if( cls == null )
				cls = "openadk.library.impl.RequestCacheFile";

			try
			{
				sSingleton = (RequestCache)Class.forName(cls).newInstance();
				sSingleton.initialize( agent );
			}
			catch( Throwable thr )
			{
				sSingleton = null;
				throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
			}
		}

		return sSingleton;
	}

	/**
	 *  Initialize the RequestCache
	 */
	protected abstract void initialize( Agent agent )
		throws ADKException;

	/**
	 *  Closes the RequestCache
	 */
	public abstract void close()
		throws ADKException;

	/**
	 * Returns the number of requests that are current active
	 * 
	 * @return The number of active requests
	 */
	public abstract int getActiveRequestCount();
	/**
	 *  Store the request MsgId and associated SIF Data Object type in the cache
	 */
	public abstract RequestInfo storeRequestInfo( SIF_Request request, Query q, Zone zone )
		throws ADKException;
	/**
	 *  Store the request MsgId and associated SIF Data Object type in the cache
	 */
	public abstract RequestInfo storeServiceRequestInfo( SIF_ServiceInput request, Query q, Zone zone )
		throws ADKException;

	/**
	 *  Lookup the SIF Data Object type of a pending request given its MsgId
	 */
	public abstract RequestInfo getRequestInfo( String msgId, Zone zone )
		throws ADKException;

	/**
	 *  Lookup the SIF Data Object type of a pending request given its MsgId
	 */
	public abstract RequestInfo lookupRequestInfo( String msgId, Zone zone )
		throws ADKException;
}
