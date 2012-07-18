//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


/**
 *  Thrown when an element or attribute is referenced but does not exist.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ADKSchemaException extends ADKException
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2264652911593903940L;

	public ADKSchemaException( String msg )
	{
		super( msg, null );
    }

	public ADKSchemaException( String msg, Zone zone )
	{
		super( msg, zone );
	}
	
	public ADKSchemaException( String msg, Zone zone, Throwable src )
	{
		super( msg, zone, src );
	}
}
