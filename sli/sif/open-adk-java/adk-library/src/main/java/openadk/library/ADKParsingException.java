//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Signals an error parsing SIF message or datatype content
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ADKParsingException extends ADKException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6725315491031025687L;

	/**
	 *  Constructs an exception with a detailed message
	 *  @param msg A detailed eror message
	 *  @param zone The zone that is in scope, or null

	 */
	public ADKParsingException( String msg, Zone zone )
	{
		super(msg,zone);
	}
	
	/**
	 * Constructs an exception with a message, and the source exception
	 * @param msg A detailed error message
	 * @param zone The zone that is in scope, or null
	 * @param src The exception that is the cause of this re-thrown exception
	 */
	public ADKParsingException( String msg, Zone zone, Exception src ){
		super( msg, zone, src );
	}
	
}
