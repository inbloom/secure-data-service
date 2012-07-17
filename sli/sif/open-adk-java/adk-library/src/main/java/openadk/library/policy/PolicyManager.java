//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;

import openadk.library.ADKException;
import openadk.library.SIFMessagePayload;
import openadk.library.Zone;
import openadk.library.impl.ObjectFactory;
import openadk.library.impl.ObjectFactory.ADKFactoryType;


/**
 * @author Andy
 *
 */
public abstract class PolicyManager {

	private static PolicyManager sInstance;
	public static synchronized PolicyManager getInstance( Zone zone ) 
		throws ADKException{
		if( sInstance == null ){
			sInstance = (PolicyManager)ObjectFactory.getInstance().createInstance( ADKFactoryType.POLICY_MANAGER, zone.getAgent() );
		}
		return sInstance;
	}
	
	/**
	 * Unloads the singleton instance of the PolicyManager
	 */
	public static void unloadInstance(){
		sInstance = null;
	}
	
	
	/**
	 * Applies ADK policy to the outbound message
	 * @param payload The message being sent from the ADK
	 * @param zone The zone that the message is being sent to
	 * @throws ADKException
	 */
	public abstract void applyOutboundPolicy( SIFMessagePayload payload, Zone zone ) throws ADKException;
		
}
