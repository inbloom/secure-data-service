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
 *	Interface of a ServerLog module to which log information will be posted
 *	whenever any of the logging methods of the ServerLog class are called.<p>
 */
public interface ServerLogModule
{
	/**
	 * 	Gets the ID of this logger
	 * 	@return The ID of this ServerLogModule instance
	 */
	public String getID();
	
	/**
	 * 	Post a string message to the server log.<p>
	 * 	@param zone The zone on the server to post the message to
	 * 	@param message The message text
	 */
	public void log( Zone zone, String message );	
	/**
	 * 	Post information encapsulated by a SIF <code>SIF_LogEntry</code> object to the server log.<p>
	 * 	@param zone The zone on the server to post the message to
	 * 	@param data The SIF_LogEntry object 
	 */
	public void log( Zone zone, SIF_LogEntry data );
}
