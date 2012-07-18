//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.log;

////////////////////////////////////////////////////////////////////////////////
//
//  Copyright (c)2001-2007 Edustructures LLC
//  All rights reserved.
//
//  This software is the confidential and proprietary information of
//  Edustructures LLC ("Confidential Information").  You shall not disclose
//  such Confidential Information and shall use it only in accordance with the
//  terms of the license agreement you entered into with Edustructures.
//

import openadk.library.*;
import openadk.library.infra.*;

/**
 * 	The default <i>ServerLogModule</i> implementation for server logging. 
 * 	An instance of this class is installed by default when the ADK is 
 * 	initialized.<p>
 * 
 * 	DefaultServerLogModule writes to the server log by constructing and
 * 	reporting a SIF_LogEntry object as an Add SIF_Event when the <code>log</code>
 * 	method is called. Because SIF_LogEntry was introduced in SIF 1.5, an 
 * 	event is only reported when the agent's default SIF Version is 1.5 or 
 * 	later. No action is taken for earlier versions.<p>
 * 
 * 	By default, DefaultServerLogModule echos all messages to the zone's 
 * 	local Log4j Category. Call the <code>setEcho</code> method to disable 
 * 	this functionality. Follow these steps to obtain the DefaultServerLogModule 
 * 	that is installed by the ADK when it is initialized:<p>
 * 
 * 	<ul>
 * 		<li>
 * 			Call <code>ADK.getServerLog</code> to obtain the ServerLog
 * 			instance at the root of the logging chain
 * 		</li>
 * 		<li>
 * 			Call the <code>ServerLog.getLoggers</code> method to obtain
 * 			an array of all <i>ServerLogModule</i>s
 *		</li>
 * 		<li>
 * 			Call the <code>getID</code> method on each element in the
 * 			array until you encounter one that returns "DefaultServerLogModule".
 * 			Alternatively, you can use <code>instanceof</code> to test each
 * 			element to determine if it is an instance of this class.
 * 		</li>
 * 		<li>
 * 			Call <code>setEcho</code> to enable/disable echoing of
 * 			SIF_LogEntry information to the local zone log. (SIF_LogEntry
 * 			is always echoed when the ADK is initialized to use a version
 * 			of SIF prior to 1.5)
 * 		</li>
 * 		<li>
 * 			Call <code>setReportEvents</code> to enable/disable the reporting
 * 			of SIF_LogEntry events to the zone. This method can be used to
 * 			disable SIF_LogEntry reporting to zones while continuing to allow 
 * 			log messages to be echoed to the local zone log.
 * 		</li>
 * 	</ul>
 */
public class DefaultServerLogModule implements ServerLogModule
{
	protected boolean fEcho = true;
	protected boolean fReportEvents = true;
	
	/**
	 * 	Gets the ID of this logger
	 * 	@return The ID of this ServerLogModule instance
	 */
	public String getID()
	{
		return "DefaultServerLogModule";
	}
	
	/**
	 * 	Determines if SIF_LogEntry objects should be echoed to the local client-side zone log
	 * 	@param echo <code>true</code> to echo SIF_LogEntry information to the client-size
	 * 		zone log; <code>false</code> to disable echoing. Note that SIF_LogEntry is always
	 * 		echoed to the local zone log when the ADK is initialized with a version of SIF
	 * 		prior to 1.5.
	 */ 
	public void setEcho( boolean echo )
	{
		fEcho = echo;
	}	
	
	/**
	 * 	Determines if SIF_LogEntry objects should be echoed to the local client-side zone log
	 * 	@return <code>true</code> to echo SIF_LogEntry information to the client-size
	 * 		zone log; <code>false</code> to disable echoing. Note that SIF_LogEntry is always
	 * 		echoed to the local zone log when the ADK is initialized with a version of SIF
	 * 		prior to 1.5.
	 */ 
	public boolean getEcho()
	{
		return fEcho;
	}	
	
	/**
	 * 	Determines if SIF_LogEntry objects are reported to the zone. This method can 
	 * 	be called to disable SIF_LogEntry reporting to zones while continuing to allow 
	 * 	log messages to be echoed to the local zone log.<p>
	 * 
	 * 	@param report <code>true</code> to report SIF_LogEntry events, <code>false</code>
	 * 		to disable reporting of events
	 */
	public void setReportEvents( boolean report )
	{
		fReportEvents = report;
	}	
	
	/**
	 * 	Determines if SIF_LogEntry objects are reported to the zone. This method can 
	 * 	be called to disable SIF_LogEntry reporting to zones while continuing to allow 
	 * 	log messages to be echoed to the local zone log.<p>
	 * 
	 * 	@return <code>true</code> to report SIF_LogEntry events, <code>false</code>
	 * 		to disable reporting of events
	 */
	public boolean getReportEvents()
	{
		return fReportEvents;
	}	
	
	/**
	 * 	Post a string message to the server log.<p>
	 * 
	 * 	The implementation of this method constructs a SIF_LogEntry object 
	 * 	with the <code>LogLevel</code> attribute set to "Info" and the 
	 * 	<code>SIF_Desc</code> element set to the text message passed to
	 * 	the <i>message</i> parameter. The SIF_LogEntry object is then 
	 * 	reported to the zone as a SIF_Event by delegating to the <code>log</code>
	 * 	method that accepts a SIF_LogEntry parameter.<p>
	 * 
	 * 	@param zone The zone on the server to post the message to
	 * 	@param message The message text
	 */
	public void log( Zone zone, String message )
	{
		//	If SIF 1.5 or later, encapsulate in a SIF_LogEntry and
		//	report it to the zone. Otherwise just write the message
		//	to the local zone log.
		if( ADK.getSIFVersion().compareTo( SIFVersion.SIF15r1 ) >= 0 ) {
			SIF_LogEntry le = new SIF_LogEntry();
			le.setLogLevel( LogLevel.INFO );
			le.setSIF_Desc( message );
			log( zone, le );
		}
		else
		{
			zone.getLog().debug( message );
		}
	}
	
	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	@param zone The Zone to post log information to
	 * 	@param data The SIF_LogEntry object to post as an Add Event 
	 */
	public void log( Zone zone, SIF_LogEntry data )
	{
		if( data == null )
			return;
			
		if( fEcho )
		{
			StringBuffer b = new StringBuffer();
			b.append( "Server Log [Level=" );
			b.append( data.getLogLevel() );
			
			String category = data.getSIF_Category();
			Integer code = data.getSIF_Code();
			if( category != null && code != null )
			{
				b.append( ", Category=" );
				b.append( category );
				b.append( ", Code=" );
				b.append( code.toString() );
			}
			String appCode = data.getSIF_ApplicationCode();
			if( appCode != null ) {
				b.append( ", AppCode=" );
				b.append( appCode );
			}
			
			b.append( "] " );

			String desc = data.getSIF_Desc();
			if( desc != null )
				b.append( desc );
			desc = data.getSIF_ExtendedDesc();
			if( desc != null ) {
				b.append( ". " + desc );
			}

			zone.getLog().debug( b.toString() );
		}

		if( fReportEvents && ADK.getSIFVersion().compareTo( SIFVersion.SIF15r1 ) >= 0 )
		{
			try {		
				zone.reportEvent( data, EventAction.ADD );
			} catch( Exception ex ) {
				zone.getLog().debug( "Error reporting SIF_LogEntry event to zone: " + ex );
			}
		}
	}
}
