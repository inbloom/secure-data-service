//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ADKException;
import openadk.library.Agent;
import openadk.library.policy.ADKDefaultPolicy;
import openadk.library.policy.PolicyManagerImpl;


/**
 * The default Object Factory used by the ADK
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class ObjectFactoryImpl extends ObjectFactory {

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.ObjectFactory#createInstance(com.edustructures.sifworks.impl.ObjectFactory.ADKFactoryType, com.edustructures.sifworks.Agent)
	 */
	@Override
	public Object createInstance(ADKFactoryType factoryType, Agent agentInstance )
		throws ADKException
	{
		if( factoryType == null ){
			return null;
		}
		switch( factoryType ){
		case ZONE:
			return new ZoneFactoryImpl( agentInstance );
		case TOPIC:
			return new TopicFactoryImpl( agentInstance );
		case POLICY_FACTORY:
			return new ADKDefaultPolicy( agentInstance );
		case POLICY_MANAGER:
			return new PolicyManagerImpl( agentInstance );
		}
		return null;
	}

}
