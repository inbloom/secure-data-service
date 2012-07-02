//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.infra.*;

import org.apache.log4j.*;

/**
 *  Utility routines used internally by the ADK
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ADKUtils
{

	/**
	 *  Throws an ADKMessagingException, optionally logging its message first
	 */
	public static void _throw( ADKMessagingException thr, Category log )
		throws ADKMessagingException
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			thr.log(log);
		throw thr;
	}

	/**
	 *  Throws an ADKTransportException, optionally logging its message first
	 */
	public static void _throw( ADKTransportException thr, Category log )
		throws ADKTransportException
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			thr.log(log);
		throw thr;
	}

	/**
	 *  Throws a SIFException, optionally logging its message first
	 */
	public static void _throw( SIFException thr, Category log )
		throws SIFException
	{
		SIFException exc = thr;

		//  If exception has a non-success status code and no errors, substitute a
		//  more descriptive exception
		if( thr.getAck() != null &&	( !thr.getAck().hasStatusCode(0) && !thr.getAck().hasError() ) )
		{
			StringBuffer b = new StringBuffer();
			b.append("Received non-success status code (");

			SIF_Status s = thr.getAck().getSIF_Status();
			if( s == null )
				b.append("or the SIF_Status element does not exist");
			else
			{
				b.append( s.getSIF_Code() );
			}

			b.append(") but no error information");

			exc = new SIFException( b.toString(), thr.getAck(),thr.getZone() );
		}

		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			exc.log(log);

		throw exc;
	}

	/**
	 *  Throws an ADKQueueException, optionally logging its message first
	 */
	public static void _throw( ADKQueueException thr, Category log )
		throws ADKQueueException
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			thr.log(log);
		throw thr;
	}

	/**
	 *  Throws an ADKException, optionally logging its message first
	 */
	public static void _throw( ADKException thr, Category log )
		throws ADKException
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			thr.log(log);
		throw thr;
	}

	/**
	 *  Throws an exception, optionally logging its message first
	 */
	public static void _throw( RuntimeException thr, Category log )
		throws RuntimeException
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			log.error(thr.toString(), thr);
		throw thr;
	}

	/**
	 *  Throws an exception, optionally logging its message first
	 */
	public static void _throw( Throwable thr, Category log )
		throws Throwable
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			log.error(thr.toString(), thr );
		throw thr;
	}

	/**
	 *  Throws an Error, optionally logging its message first
	 */
	public static void _throw( Error thr, Category log )
		throws Error
	{
		if( ( ADK.debug & ADK.DBG_EXCEPTIONS ) != 0 )
			log.error(thr.toString(), thr );
		throw thr;
	}
}
