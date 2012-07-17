//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADK;

import org.mortbay.http.*;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class AnonymousHttpHandler implements HttpHandler
{
	HttpContext ctx;
	boolean started = false;

	public void handle( String pathInContext, String pathParams, HttpRequest request, HttpResponse response )
		throws HttpException
	{
		System.out.println("Warning: Anonymous message received from ZIS");
		ADK.getLog().warn( "Warning: Anonymous message received from ZIS" + request.getURI() );
		response.setStatus( 403 );
	}

	public void initialize( HttpContext context ) {
		ctx = context;
		started = true;
	}
	public HttpContext getHttpContext() {
		return ctx;
	}
	public String getName() {
		return "AnonymousHttpHandler";
	}

	public synchronized boolean isStarted()
	{
		return started;
	}
	public void start()
	{
		started = true;
	}
	public void stop()
	{
		started = false;
	}
}
