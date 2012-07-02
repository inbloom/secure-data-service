//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import openadk.library.*;
import openadk.util.LinkedListMap;


/**
 *  Default ZoneFactory implementation.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class TopicFactoryImpl implements TopicFactory
{
	/**
	 *  Cache of Topics keyed by object type (e.g. "StudentPersonal")
	 */
	protected Map<SIFContext, Map<ElementDef,Topic>> fContexts = 
			new LinkedListMap<SIFContext, Map<ElementDef,Topic>>();

	/**
	 *  The agent that owns this TopicFactory. By associating factories with
	 *  Agents (rather than having a static singleton) we can support multiple
	 *  Agents per virtual machine.
	 */
	protected Agent fAgent;

	/**
	 *  Constructs a TopicFactory
	 *  @param agent The Agent that owns this factory
	 */
	public TopicFactoryImpl( Agent agent ) {
		fAgent = agent;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.TopicFactory#getInstance(com.edustructures.sifworks.ElementDef)
	 */
	public synchronized Topic getInstance(  ElementDef objectType )
	{
		return getInstance( objectType, SIFContext.DEFAULT );
	}
	
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.TopicFactory#getInstance(com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.SIFContext)
	 */
	public synchronized Topic getInstance(  ElementDef objectType, SIFContext context )
	{
		if( objectType == null ){
			throw new IllegalArgumentException("The {objectType} parameter cannot be null" );
		}
		
		if( context == null ){
			throw new IllegalArgumentException("The {context} parameter cannot be null" );
		}
		
		Map<ElementDef,Topic> map = getTopicMap( context );
		Topic topic = map.get( objectType );
		if( topic == null  ) {
			topic = new TopicImpl( objectType, context );
			map.put(objectType,topic);
		}
		return topic;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.TopicFactory#lookupInstance(com.edustructures.sifworks.ElementDef, com.edustructures.sifworks.SIFContext)
	 */
	public synchronized Topic lookupInstance( ElementDef objectType, SIFContext context )
	{
	 	return getTopicMap( context ).get( objectType );
	}

	/**
	 *  Gets all Topic instances in the factory cache
	 *  @return an array of Topics
	 */
	public synchronized Collection<Topic> getAllTopics( SIFContext context )
	{
		return getTopicMap( context ).values();
	}
	
	/**
	 * Gets the map of topics for the specified context
	 * @param context
	 * @return
	 */
	private Map<ElementDef,Topic> getTopicMap( SIFContext context ){
		Map<ElementDef,Topic> contextMap = fContexts.get( context );
		if( contextMap == null ){
			contextMap = new HashMap<ElementDef,Topic>();
			fContexts.put( context, contextMap );
		}
		return contextMap;
	}
	
	public Collection<SIFContext> getAllSupportedContexts(){
		return fContexts.keySet();
	}
	
}
