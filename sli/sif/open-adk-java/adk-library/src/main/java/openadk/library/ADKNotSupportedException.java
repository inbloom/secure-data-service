//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Signals an operation is not supported.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKNotSupportedException extends Error
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;
	
	protected Zone fZone;

	/**
	 *  Constructs an exception with a detailed message
	 *  @param msg The detailed message
	 */
    public ADKNotSupportedException( String msg )
	{
		super(msg);
	}

	/**
	 *  Constructs an exception with a detailed message
	 *  @param msg The detailed message
	 *  @param zone The Zone associated with this exception
	 */
    public ADKNotSupportedException( String msg, Zone zone )
	{
		super(msg);

		fZone = zone;
    }

	/**
	 *  Gets the Zone associated with this exception
	 */
	public Zone getZone() {
		return fZone;
	}
}
