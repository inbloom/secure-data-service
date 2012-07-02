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


import java.io.StringWriter;
import java.util.*;

import openadk.library.*;
import openadk.library.infra.*;

/**
 * 	Provides access to the Zone Integration Server log.<p>
 * 
 * 	ServerLog functionality can be customized and extended by adding one or 
 * 	more <i>ServerLogModule</i> implementations to the chain of loggers. The 
 * 	logging chain is hierarchical, comprised of ServerLog instances at the ADK,
 * 	Agent, and Zone levels. Whenever a server-side logging operation is 
 * 	performed on a zone, it is delegated to the <i>ServerLogModule</i> instances 
 * 	at each level in the hierarchy, beginning with the Zone.<p>
 * 
 * 	To customize server-side logging on a global basis, call the 
 * 	<code>ADK.getServerLog</code> static function and use the methods below to
 * 	manipulate <i>ServerLogModule</i> instances at the root level of the 
 * 	hierarchy:
 * 
 * 	<ul>
 * 		<li><code>addLogger</code></li>
 * 		<li><code>removeLogger</code></li>
 * 		<li><code>clearLoggers</code></li>
 * 		<li><code>getLoggers</code></li>
 * 	</ul> * 
 * 
 * 	Similarly, to customize logging on an agent-global basis or per-zone basis, 
 * 	call the <code>Agent.getServerLog</code> or <code>Agent.getServerLog( Zone )</code>
 * 	methods to obtain the ServerLog for the Agent or a Zone, respectively.<p>
 * 
 * 
 */
public class ServerLog
{
	/**
	 * 	Global registry of ServerLog instances keyed by ID
	 */	
	private static HashMap<String, ServerLog> sInstances = new HashMap<String, ServerLog>();
	
	/**
	 * 	The parent ServerLog
	 */
	private ServerLog fParent;
	
	/**
	 * 	The ID
	 */
	private String fID;	
	
	/**
	 * 	The zone to which log entries will be reported
	 */
	private Zone fZone;
	
	/**
	 * 	ServerLogModule objects to which log information will be posted
	 */
	private List<ServerLogModule> fLoggers = new Vector<ServerLogModule>();

	/**
	 * 	Protected constructor; clients must call <code>getInstance</code>
	 */
	private ServerLog( String id, Zone zone )
	{
		fID = id;
		fZone = zone;
		
		//	Determine the parent
		if( id.equals("ADK") )
			fParent = null;
		else
		if( id.equals("ADK.Agent") )
			fParent = getInstance( "ADK", null );
		else
			fParent = getInstance( "ADK.Agent", null );
	}
	
	/**
	 * Returns the ID of this logger instance
	 * @return the ID of this logger instance
	 */
	public String getID()
	{
		return fID;
	}
	
	/**
	 * 	Get a ServerLog instance with the specified ID.<p>
	 * 
	 * 	This method is intended to be called internally by the ADK. You
	 * 	should call the <code>ADK.getServerLog</code>, <code>Agent.getServerLog</code>, 
	 * 	or <code>Zone.getServerLog</code> methods to obtain a ServerLog instance
	 * 	rather than directly calling this method.<p>
	 * 
	 * 	@param id The ID identifying the ServerLog to return
	 * @param zone The zone that the ServerLog is attached to
	 * 
	 * 	@return A ServerLog instance 
	 */	
	public static ServerLog getInstance( String id, Zone zone )
	{
		if( id == null )
			throw new IllegalArgumentException( "ID cannot be null" );
			
		ServerLog log = sInstances.get( id );
		if( log == null ) {
			log = new ServerLog( id, zone );
			sInstances.put( id, log );
		}
		
		return log;
	}
		
	/**
	 * 	Adda a ServerLogModule to the chain of loggers.<p>
	 * 
	 * 	@param logger A <i>ServerLogModule</i> implementation
	 * 
	 * 	@see #removeLogger
	 * 	@see #clearLoggers
	 * 	@see #getLoggers
	 * 
	 * 	@since ADK 1.5
	 */
	public void addLogger( ServerLogModule logger )
	{
		synchronized( fLoggers ) {
			if( !fLoggers.contains( logger ) )
				fLoggers.add( logger );
		}	
	}	
		
	/**
	 * 	Remove a ServerLogModule from the chain of loggers.<p>
	 * 
	 * 	@param logger A <i>ServerLogModule</i> implementation
	 * 
	 * 	@see #addLogger
	 * 	@see #clearLoggers
	 * 	@see #getLoggers
	 * 
	 * 	@since ADK 1.5
	 */
	public void removeLogger( ServerLogModule logger )
	{
		synchronized( fLoggers ) {
			fLoggers.remove( logger );
		}
	}	
		
	/**
	 * 	Clear all ServerLogModule modules from the chain of loggers.<p>
	 * 
	 * 	@see #addLogger
	 * 	@see #removeLogger
	 * 	@see #getLoggers
	 * 
	 * 	@since ADK 1.5
	 */
	public void clearLoggers()
	{
		synchronized( fLoggers ) {
			fLoggers.clear();
		}
	}	
		
	/**
	 * 	Get a list of all registered ServerLogModule modules that comprise the
	 * 	chain of loggers.<p>
	 * @return An array of ServerLogModules
	 * 
	 * 	@see #addLogger
	 * 	@see #removeLogger
	 * 	@see #clearLoggers
	 * 
	 * 	@since ADK 1.5
	 */
	public ServerLogModule[] getLoggers()
	{
		synchronized( fLoggers ) {
			ServerLogModule[] arr = new ServerLogModule[ fLoggers.size() ];
			fLoggers.toArray( arr );
			return arr;
		}
	}
		
	/**
	 * 	Copies all registered ServerLogModules for this ServerLog into the
	 * 	supplied Vector.<p>
	 * 	@since ADK 1.5
	 */
	protected void getLoggersInto( List<ServerLogModule> target )
	{
		synchronized( fLoggers ) {
			for( ServerLogModule logger : fLoggers ) {
				target.add( logger );
			}
		}
	}
	
	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post a simple
	 * 	informative message to the server.<p>
	 * 	
	 * 	@param message A textual description of the error 
	 */
	public void log( String message )
	{
		log( LogLevel.INFO, message, null, null, -1, -1, null, (SIFDataObject[])null );
	}
	
	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post an
	 * 	error, warning, or informative message to the server with an
	 * 	description, extended description, and optional application-defined
	 * 	error code.<p>
	 * 
	 * 	@param level The LogLevel to assign to this log entry
	 * 	@param desc A textual description of the error
	 * 	@param extDesc Extended error description, or <code>null</code> if no 
	 * 		value is to be assigned to the SIF_LogEntry/SIF_ExtDesc element
	 * 	@param appCode Error code specific to the application posting the log 
	 * 		entry, or <code>null</code> if no value is to be assigned to the 
	 * 		SIF_LogEntry/SIF_ApplicationCode element
	 */
	public void log(
		LogLevel level,
		String desc,
		String extDesc,
		String appCode )
	{
		log( level, desc, extDesc, appCode, -1, -1, null, (SIFDataObject[])null );
	}
	
	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post an
	 * 	error, warning, or informative message to the server with an category
	 * 	and code enumerated by the SIF Specification.<p>
	 * 
	 * 	@param level The LogLevel to assign to this log entry
	 * 	@param desc A textual description of the error
	 * 	@param extDesc Extended error description, or <code>null</code> if no 
	 * 		value is to be assigned to the SIF_LogEntry/SIF_ExtDesc element
	 * 	@param appCode Error code specific to the application posting the log 
	 * 		entry, or <code>null</code> if no value is to be assigned to the 
	 * 		SIF_LogEntry/SIF_ApplicationCode element
	 * 	@param category The SIF_Category value to assign to this log entry, as
	 * 		defined by the SIF Specification
	 *  @param code The SIF_Code value to assign to this log entry, as defined
	 *  	by the SIF Specification
	 */
	public void log( 
		LogLevel level,
		String desc,
		String extDesc,
		String appCode,
		int category,
		int code )
	{
		log( level, desc, extDesc, appCode, category, code, null, (SIFDataObject[])null );
	}

	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post a simple
	 * 	error, warning, or informative message to the server that references a
	 * 	SIF Message and optionally a set of SIF Data Objects previously received
	 * 	by the agent.<p>
	 * 
	 * 	@param level The LogLevel to assign to this log entry
	 * 	@param message A textual description of the error
	 * 	@param info The <i>SIFMessageInfo</i> instance from the ADK message
	 * 		handler implementation identifying a SIF Message received by the agent  
	 * 	@param objects One or more SIFDataObject instances received in the message
	 * 		identified by the <i>info</i> parameter
	 */
	public void log( 
		LogLevel level,
		String message,
		SIFMessageInfo info,
		SIFDataObject[] objects )
	{
		log( level, message, null, null, -1, -1, info, objects );
	}
	
	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post an
	 * 	error, warning, or informative message to the server that references a
	 * 	SIF Message and optionally a set of SIF Data Objects previously received
	 * 	by the agent. The log entry can also have an extended error description 
	 * 	and application-defined error code.<p>
	 * 
	 * 	@param level The LogLevel to assign to this log entry
	 * 	@param desc A textual description of the error
	 * 	@param extDesc Extended error description, or <code>null</code> if no 
	 * 		value is to be assigned to the SIF_LogEntry/SIF_ExtDesc element
	 * 	@param appCode Error code specific to the application posting the log 
	 * 		entry, or <code>null</code> if no value is to be assigned to the 
	 * 		SIF_LogEntry/SIF_ApplicationCode element
	 * 	@param info The <i>SIFMessageInfo</i> instance from the ADK message
	 * 		handler implementation identifying a SIF Message received by the agent  
	 * 	@param objects One or more SIFDataObject instances received in the message
	 * 		identified by the <i>info</i> parameter
	 */
	public void log( 
		LogLevel level,
		String desc,
		String extDesc,
		String appCode,
		SIFMessageInfo info,
		SIFDataObject[] objects )
	{
		log( level, desc, extDesc, appCode, -1, -1, info, objects );
	}

	/**
	 * 	Post a SIF_LogEntry to the server.<p>
	 * 
	 * 	Use this form of the <code>log</code> method to post an error, warning, 
	 * 	or informative message to the server that references a
	 * 	SIF Message and optionally a set of SIF Data Objects previously received
	 * 	by the agent. The log entry is assigned a category and code defined by
	 * 	the SIF Specification, and may have an extended error description and 
	 * 	optional application-defined error code.<p>
	 * 
	 * 	@param level The LogLevel to assign to this log entry
	 * 	@param desc A textual description of the error
	 * 	@param extDesc Extended error description, or <code>null</code> if no 
	 * 		value is to be assigned to the SIF_LogEntry/SIF_ExtDesc element
	 * 	@param appCode Error code specific to the application posting the log 
	 * 		entry, or <code>null</code> if no value is to be assigned to the 
	 * 		SIF_LogEntry/SIF_ApplicationCode element
	 * 	@param category The SIF_Category value to assign to this log entry, as
	 * 		defined by the SIF Specification
	 * @param code The SIF_Code value to assign to this log entry, as
	 * 		defined by the SIF Specification
	 * 	@param info The <i>SIFMessageInfo</i> instance from the ADK message
	 * 		handler implementation identifying a SIF Message received by the agent  
	 * 	@param objects One or more SIFDataObject instances received in the message
	 * 		identified by the <i>info</i> parameter. This argument may be specified
	 * 		as an array or sequence of SIFDataObjects
	 */
	public void log( 
		LogLevel level,
		String desc,
		String extDesc,
		String appCode,
		int category,
		int code,
		SIFMessageInfo info,
		SIFDataObject... objects )
	{
		if( fZone == null ){
			throw new IllegalStateException( "ServerLog.log can only be called on a zone's ServerLog instance" );
		}

		String msg = null;			
		SIF_LogEntry le = null;
		if( ADK.getSIFVersion().compareTo( SIFVersion.SIF15r1 ) >= 0 )
		{
			//	Create a SIF_LogEntry
			le = new SIF_LogEntry(); 
			le.setSource( LogSource.AGENT );
			le.setLogLevel( LogLevel.wrap( level == null ? "Unknown" : level.toString() ) );
			if( desc != null )
				le.setSIF_Desc( desc );
			if( extDesc != null )
				le.setSIF_ExtendedDesc( extDesc );
			if( appCode != null )
				le.setSIF_ApplicationCode( appCode );
			if( category != -1 )
				le.setSIF_Category( String.valueOf( category ) );
			if( code != -1 )
				le.setSIF_Code( code );
				
			//	Reference a SIF_Message?
			if( info != null )
			{
				try
				{
					SIF_Header headerCopy = (SIF_Header)info.getSIFHeader().clone();
					SIF_LogEntryHeader sleh = new SIF_LogEntryHeader();
					sleh.setSIF_Header( headerCopy );
					le.setSIF_OriginalHeader( sleh );
				} catch( CloneNotSupportedException cnse ){
					fZone.getLog().warn( "Unable to clone SIF_Header for SIF_LogEntry event:" + cnse.getMessage(), cnse );
				}
			}			
			
			if( objects != null )
			{
				SIF_LogObjects slos = new SIF_LogObjects();
				le.setSIF_LogObjects( slos );
				for( int i = 0; i < objects.length; i++ )
				{
					if( objects[i] == null )
						continue;
					//	Package into a SIF_LogObject and add to the repeatable list
					//	of SIF_LogEntry/SIF_LogObjects
					// TODO: This code needs to change so that it will work properly
					// Also, it needs to be tested 
					SIF_LogObject lo = new SIF_LogObject();
					lo.setObjectName( objects[i].getObjectType().tag( info.getSIFVersion() ) );
					try{
						lo.addChild( (SIFElement)objects[i].clone() );
					} catch( CloneNotSupportedException cnse ){
						le.setSIF_ExtendedDesc( le.getSIF_ExtendedDesc() + " ERROR cloning object\r\n" + cnse  );
					}
					slos.add( lo );
					
				}
			}
		}
		else
		{
			// 	When running in SIF 1.1 or earlier, there is no
			//	SIF_LogEntry support. Build a string that can be
			//	written to the local zone log, including as much
			//	information from the would-be SIF_LogEntry as 
			//	possible.
			
			StringBuffer b = new StringBuffer();
			
			b.append( "Server Log [Level=" );
			b.append( level == null ? "Unknown" : level.toString() );
			
			if( category != -1 && code != -1 )
			{
				b.append( ", Category=" );
				b.append( category );
				b.append( ", Code=" );
				b.append( code );
			}
			if( appCode != null ) {
				b.append( ", AppCode=" );
				b.append( appCode );
			}
			
			b.append( "] " );

			if( desc != null )
				b.append( desc );
			if( extDesc != null )
				b.append( ". " + extDesc );

			msg = b.toString();
		}
			
		//	Post the the server		
		ServerLogModule[] chain = _getLogChain( fZone );
		for( int i = 0; i < chain.length; i++ ) {
			if( le != null )
				chain[i].log( fZone, le );
			else
				chain[i].log( fZone, msg );
		}
	}

	private ServerLogModule[] _getLogChain( Zone zone )
	{
		Vector<ServerLogModule> v = new Vector<ServerLogModule>();
		
		ServerLog parent = this;
		while( parent != null ) {
			parent.getLoggersInto( v );
			parent = parent.fParent;
		}
			
		ServerLogModule[] arr = new ServerLogModule[ v.size() ];
		v.copyInto( arr );
		return arr;
	} 
}
