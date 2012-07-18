//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Exception signaling that a method was called on a Zone instance but the zone
 *  is not in a connected state.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKZoneNotConnectedException extends ADKException
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;
	
	
	public ADKZoneNotConnectedException( String msg, Zone zone )
	{
		super(msg,zone);
	}
}
