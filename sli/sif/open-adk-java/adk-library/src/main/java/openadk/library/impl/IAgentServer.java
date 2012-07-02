//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.rmi.*;

import openadk.library.*;

/**
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public interface IAgentServer extends Remote
{
	/**
	 *  Connect this agent to the Agent Server
	 */
	public void connect()
		throws ADKException;

	/**
	 *  Disconnect this agent from the Agent Server
	 */
	public void disconnect()
		throws ADKException;
}
