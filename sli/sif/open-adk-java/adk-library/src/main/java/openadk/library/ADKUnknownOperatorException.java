//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

public class ADKUnknownOperatorException extends Exception
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;
	
    public ADKUnknownOperatorException( String op )
	{
		super(op);
    }
}
