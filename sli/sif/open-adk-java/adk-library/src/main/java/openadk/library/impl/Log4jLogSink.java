//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADK;
import openadk.library.Agent;

import org.mortbay.log.Frame;
import org.mortbay.log.LogSink;





/**
 *  A Jetty LogSink implementation that writes messages to the global Log4j
 *  Category for the Agent
 */
public class Log4jLogSink implements LogSink
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3851732954939651965L;
	private boolean fStarted;

    public Log4jLogSink()
    {
    }

	public synchronized void log( String str )
	{
		if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 ){
			Agent.getLog().debug( str );
		}
	}

    public synchronized void log( String tag, Object msg, Frame frame, long time )
	{
		if( ( ADK.debug & ADK.DBG_TRANSPORT ) != 0 ){
			Agent.getLog().debug( msg );
		}
	}

	public String getOptions()
	{
		return "";
	}

	public void setOptions( String options )
	{
	}

	public boolean isStarted()
	{
		return fStarted;
	}

	public void start()
	{
		fStarted = true;
	}

	public void stop()
	{
		fStarted = false;
	}

	public void setLogImpl(org.mortbay.log.LogImpl impl ) 
	{
		log( "Log4jLogSink.setLogImpl called with " + impl.toString() );
	}

}
