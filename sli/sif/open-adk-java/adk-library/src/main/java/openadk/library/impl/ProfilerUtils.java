//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import org.apache.commons.lang.NotImplementedException;

import openadk.library.ADK;
import openadk.library.ElementDef;
import openadk.profiler.api.ObjectTypeCodes;


/**
 * 	Utilities for interacting with the SIFProfilerClient when support for the Edustructures
 * 	SIF Profiling Harness is enabled.<p>
 */
public class ProfilerUtils
{
	/**
	 * 	Profiler name assigned by the Agent class upon initialization
	 */
	private static String fProfName;
	
	/**
	 * 	Profiler session ID
	 */
	private static int fSessionId;

	/**
	 * 	Called by the agent when it is ready to initialize the SIFProfilerClient instance
	 * 	of the ADK. This must be done after the agent has learned the Session ID to associate
	 * 	with all recorded metrics.
	 */
	public static void startProfiling( int sessionId, ObjectTypeCodes otcImpl )
	{
	    throw new NotImplementedException();
//		ADK.getLog().debug( "SIFProfilerClient instance name: " + fProfName );
//		ProfilerUtils.setProfilerSessionId( sessionId );
//		ADK.getLog().debug( "SIFProfilerClient session ID: " + fSessionId );
//
//		openadk.profiler.SIFProfilerClient prof = 
//			openadk.profiler.SIFProfilerClient.getInstance( fProfName );
//		if( prof != null ) {
//			try {
//				prof.setObjectTypeCodesImpl( otcImpl );
//				prof.open( new openadk.profiler.api.ProfilerSession( ProfilerUtils.getProfilerSessionId() ) );
//			} catch( Exception ex ) {
//				System.out.println( "Failed to open SIFProfilerClient instance: " + ex );
//				System.exit(-1);
//			}
//		}
	}	
	
	/**
	 * 	Sets the profiler name.<p>
	 * 
	 * 	This method is called by the Agent class upon initialization.<p>
	 * 
	 * 	@param name The string that identifies the SIFProfilerClient instance for the ADK,
	 * 		which may differ from other components being profiled. The name must be unique
	 * 		among all SIFProfilerClient instances.
	 */
	public static void setProfilerName( String name ) {
		fProfName = name;
	}

	/**
	 * 	Gets the profiler name.<p>
	 * 
	 * 	@return The string that identifies the SIFProfilerClient instance for the ADK,
	 * 		which may differ from other components being profiled. The name must be unique
	 * 		among all SIFProfilerClient instances.
	 */
	public static String getProfilerName() {
		return fProfName;
	}
	
	
	/**
	 * 	Sets the profiler name.<p>
	 * 
	 * 	This method is called by the Agent class upon initialization.<p>
	 * 
	 * 	@param name The string that identifies the SIFProfilerClient instance for the ADK,
	 * 		which may differ from other components being profiled. The name must be unique
	 * 		among all SIFProfilerClient instances.
	 */
	public static void setProfilerSessionId( int id ) {
		fSessionId = id;
	}

	/**
	 * 	Gets the profiler session ID.<p>
	 */
	public static int getProfilerSessionId() {
		return fSessionId;
	}
	
	/**
	 *	Start recording metric data 
	 */
	public static void profileStart( String oid, short objType, String msgId ) {
        throw new NotImplementedException();
//		openadk.profiler.SIFProfilerClient c = openadk.profiler.SIFProfilerClient.getInstance( fProfName );
//		if( c != null ) {
//			c.metricStart( oid + "." + objType, msgId, objType );
//		}
	}

	/**
	 *	Start recording metric data 
	 */
	public static void profileStart( String oid, ElementDef objType, String msgId ) {
	    throw new NotImplementedException();
//
//		openadk.profiler.SIFProfilerClient c = openadk.profiler.SIFProfilerClient.getInstance( fProfName );
//		if( c != null ) {
//			short objTypeCode = c.getObjectTypeCode( objType );
//			c.metricStart( oid + "." + objTypeCode, msgId, objTypeCode );
//		}
	}

	/**
	 *	Stop recording metric data 
	 */
	public static void profileStop() {
        throw new NotImplementedException();
//		openadk.profiler.SIFProfilerClient c = openadk.profiler.SIFProfilerClient.getInstance( fProfName );
//		if( c != null ) {
//			c.metricStop();
//		}
	}
}
