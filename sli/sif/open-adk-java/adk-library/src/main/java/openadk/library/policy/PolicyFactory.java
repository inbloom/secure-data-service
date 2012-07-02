//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;

import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.ElementDef;
import openadk.library.Zone;
import openadk.library.impl.ObjectFactory;
import openadk.library.impl.ObjectFactory.ADKFactoryType;


/**
 * Creates Policy objects for Zones. The factory pattern enables the default
 * ADK policy management to be overriden by specific agents, frameworks, or
 * implementations.<p>
 * 
 * Policy objects control behavior of the ADK as it is configured in a specific
 * zone or implementation. Examples of policy behaviors are requesting data in a 
 * specific version of SIF, requesting data in a specific SIF Context, or sending
 * events using a specific version of SIF in a specific zone.
 * 
 * Policy can usually be applied external to agent code. For example, request policy
 * is, for the most part, transparent from the agent's code. 
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.1
 *
 */
public abstract class PolicyFactory {

	private static PolicyFactory sInstance;
	
	/**
	 * Returns an implementation of the PolicyFactory class that returns
	 * an implementation-specific set of policies for managing the agent
	 * @param agent The Agent instance to retrieve policy information for
	 * @return an instance of PolicyFactory
	 * @throws ADKException If the PolicyFactory instance cannot be
	 * created.
	 */
	public synchronized static PolicyFactory getInstance( Agent agent)
		throws ADKException
	{
		if( sInstance == null ){
			sInstance = (PolicyFactory)ObjectFactory.getInstance().createInstance( ADKFactoryType.POLICY_FACTORY, agent );
		}
		return sInstance;
	}
	
	/**
	 * Returns the ObjectRequestyPolicy for the specified SIF Data Object
	 * @param zone The zone to get policy information for
	 * @param objectType The name of the SIF Data Object for
	 * which to return request policy
	 * @return An instance of ObjectRequestPolicy that has been initialized
	 * to prescribe policy for requesting data of that type or <code>null</code> if
	 * no policy is defined
	 */
	public abstract ObjectRequestPolicy getRequestPolicy( Zone zone, String objectType );
	
}
