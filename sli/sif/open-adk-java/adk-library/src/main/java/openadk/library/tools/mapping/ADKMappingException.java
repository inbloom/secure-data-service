//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.mapping;

import openadk.library.ADKException;
import openadk.library.Zone;

/**
 *  Signals an exception in a field mapping rule definition or mapping operation.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKMappingException extends ADKException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3843866641653378385L;

	/**
	 *  Constructor
	 * @param msg A detailed message to associated with this exception
	 * @param zone The zone associated with this exception
	 */
    public ADKMappingException( String msg, Zone zone )
	{
		super(msg,zone);
    }
    
	/**
	 *  Constructor
	 *  @param msg A detailed message to associated with this exception
	 * @param zone The Zone associated with this exception
	 * @param src The underlying exception that was originally thrown and
	 * 	 is wrapped by this exception 
	 */
    public ADKMappingException( String msg, Zone zone, Throwable src )
	{
		super(msg, zone, src);
    }
}
