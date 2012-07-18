//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import java.net.InetAddress;
import java.util.Random;
import java.util.UUID;
import java.net.*;
import java.security.*;

/**
 *  Generates Globally Unique Identifiers (GUIDs) in accordance with the SIF
 *  Specifications.<p>
 *
 *  Agents should call the makeGUID method to create nww GUIDs for SIF Data
 *  Objects provided by the agent. GUIDs are used by SIF to identify objects
 *  in a SIF Zone independent of any one application. The originator of a data
 *  object is responsible for creating a GUID for it and maintaining the mapping
 *  between the local application and the SIF object via its GUID.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class GUIDGenerator
{
  /**
	 *  Issue a Globally Unique Identifier (GUID)
	 *  @return A GUID
	 */
	public static synchronized String makeGUID()
	{
		UUID guid = UUID.randomUUID();
		StringBuilder sb = new StringBuilder( guid.toString().toUpperCase() );
		sb.deleteCharAt( 8 );
		sb.deleteCharAt( 12 );
		sb.deleteCharAt( 16 );
		sb.deleteCharAt( 20 );
		return sb.toString();
	}

}
