//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

/**
 *  Indicates an internal error has occurred
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class InternalError extends Error
{
	/**
	 *  Constructs an error with a message
	 */
	public InternalError( String msg )
	{
		super(msg);
	}
}
