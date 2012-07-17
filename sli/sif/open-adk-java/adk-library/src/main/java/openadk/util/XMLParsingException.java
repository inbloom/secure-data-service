//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

/**
 *	Encapsulates an exception thrown during the parsing of an XML doc.
 *	Simply wraps the actual exception that was thrown.
 */
public class XMLParsingException extends Exception
{
	protected Throwable fException;

	public XMLParsingException( Throwable exception ) {
		fException = exception;
	}
	public XMLParsingException( String msg ) {
	    super(msg);
	}
	public Throwable getException() {
		return fException;
	}
}
