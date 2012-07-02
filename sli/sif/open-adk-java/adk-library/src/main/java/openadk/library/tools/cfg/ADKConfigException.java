//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.cfg;

import openadk.library.ADKException;

/**
 *  Signals an error in an AgentConfig configuration file<p>
 *
 *  @author Edustructures
 *  @version ADK 1.0
 */
public class ADKConfigException extends ADKException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2112802785950638047L;

	/**
	 *  Constructor
	 *  @param msg A detailed message
	 */
    public ADKConfigException( String msg )
	{
		super( msg, null );
    }
    
    /**
     * Constructor
     * @param msg A detailed message
     * @param thr The source exception
     */
    public ADKConfigException( String msg, Throwable thr )
	{
		super( msg, null, thr );
    }
    
}
