//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class GeneratorException extends Exception
{
	private static final long serialVersionUID = Main.DEFAULT_SERIAL_VERSION_UID;
	
	public GeneratorException( Exception source ){
		super( source.getMessage(), source );
	}
	
	public GeneratorException( String msg )
	{
		super(msg);
	}
}
