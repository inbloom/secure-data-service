//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Collection;

/**
 *  Creates Topic objects and provides access to the topics that have been
 *  created during this session of the agent.<p>
 *
 *  Agents exchange data with SIF zones via Zone and Topic objects, which can
 *  be obtained with the ZoneFactory and TopicFactory classes. These factories
 *  are available by calling the <code>Agent.getZoneFactory</code> and
 *  <code>Agent.getTopicFactory</code> methods.<p>
 *
 *  Topics are used to aggregate publish, subscribe, and query activity from
 *  multiple zones. An Agent may have only one Topic instance per SIF Data Object
 *  type (e.g. "StudentPersonal", "BusInfo", or "LibraryPatronStatus"), but a
 *  Topic can be joined with any number of Zones.<p>
 *
 *  Topic instances are returned by calling the getInstance method, which
 *  returns the same Topic object for a given object type. Topic instances are
 *  cached by the factory. getAllTopics returns all topics in the cache. A Topic
 *  remains in the cache for as long as the agent is running.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface TopicFactory
{
	
	/**
	 *  Gets a Topic instance within the default SIF Context for a SIF object type. 
	 *  If the Topic does not exist, it is created<p>
	 *
	 *  @param objectType The ElementDef identifying a top-level SIF Data Object
	 *      defined by the SIFDTD class (e.g. <code>ADK.DTD().STUDENTPERSONAL</code>)
	 *  @return A new or cached Topic instance
	 *  @exception IllegalArgumentException is thrown if the object type is not
	 *      a root-level object for the version of SIF in use by the agent
	 */
	public Topic getInstance( ElementDef objectType );
	
	/**
	 *  Gets a Topic instance for a SIF object type. If the Topic does not
	 *  exist, it is created<p>
	 *
	 *  @param objectType The ElementDef identifying a top-level SIF Data Object
	 *      defined by the SIFDTD class (e.g. <code>ADK.DTD().STUDENTPERSONAL</code>)
	 * @param context Lookup a Topic instance for the given SIF Context      
	 *  @return A new or cached Topic instance
	 *  @exception IllegalArgumentException is thrown if the object type is not
	 *      a root-level object for the version of SIF in use by the agent
	 */
	public Topic getInstance( ElementDef objectType, SIFContext context );
	
	
	/**
	 * Looks up an existing Topic withing the given SIFContext for the given object 
	 * type. If the topic does not exist, null is returned
	 * @param objectType The ElementDef identifying the top-level SIF Data Object
	 * @param context The SIF Context to look in
	 * @return A Topic instance for the specified object type and SIFContext
	 */
	public Topic lookupInstance( ElementDef objectType, SIFContext context );

	/**
	 *  Gets all Topic instances in the factory cache for the specified context
	 *  @param context A SIFContext to return all topics for
	 *  @return an array of Topics
	 */
	public Collection<Topic> getAllTopics(SIFContext context);
	
	/**
	 * Gets all SIF Contexts that have topics created for them
	 * @return A collection of SIFContext instances that have topics joined to them
	 */
	public Collection<SIFContext> getAllSupportedContexts();
}
