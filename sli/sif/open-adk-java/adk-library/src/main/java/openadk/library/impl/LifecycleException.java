//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

/**
 *  Thrown to signal an object has not been initialized or started, or that it
 *  has been closed or shut down
 */
public class LifecycleException extends RuntimeException
{
    public LifecycleException( String msg )
	{
		super( msg );
    }
}
