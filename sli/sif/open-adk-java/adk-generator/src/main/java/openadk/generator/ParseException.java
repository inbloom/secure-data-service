//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

/**
 *  Signals a parsing error.
 */
public class ParseException extends Exception
{

	private static final long serialVersionUID = Main.DEFAULT_SERIAL_VERSION_UID;
	public ParseException()
	{
		super();
    }
	public ParseException( String msg )
	{
		super(msg);
	}
}
