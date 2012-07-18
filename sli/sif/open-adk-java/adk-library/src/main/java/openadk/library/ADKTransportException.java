//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Exception signaling that an error has occurred in a transport protocol.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKTransportException extends ADKException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;
	
	public ADKTransportException( String msg, Zone zone )
	{
		super(msg,zone);
	}
	
	public ADKTransportException( String msg, Zone zone, Throwable innerException )
	{
		super( msg, zone, innerException );
	}
}
