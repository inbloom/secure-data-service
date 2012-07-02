//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Creates Zone objects and provides access to the Zones created by an agent.<p>
 *
 *  Agents exchange data with SIF zones via Zone and Topic objects, which can
 *  be obtained with the ZoneFactory and TopicFactory classes. These factories
 *  are available by calling the Agent.getZoneFactory and Agent.getTopicFactory
 *  methods.<p>
 *
 *  An Agent may have one Zone object per SIF zone to which it is connected.<p>
 *
 *  Zone instances are returned by calling the getInstance method. When no
 *  properties are specified for a zone, the agent's defaults are used. Default
 *  properties are returned by the Agent.getProperties method. For a explanation
 *  of the various properties that affect zones, refer to the AgentProperties
 *  class description.<p>
 *
 *  The getInstance method returns the same Zone object for a given zone
 *  identifier for as long as it remains in the factory's cache. getAllZones
 *  returns all zones in the cache. A Zone remains in the cache until its
 *  disconnect method is called, at which point it is removed and can no longer
 *  be used by agent. (Calling the Zone.connect method on a disconnected zone
 *  throws an exception.) To reconnect to a zone after calling its disconnect
 *  method, obtain a fresh Zone instance from the ZoneFactory.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface ZoneFactory
{
	/**
	 *  Gets a Zone instance with default properties<p>
	 *  @param zoneId The zone identifier
	 *  @param zoneUrl The URL of the Zone Integration Server that manages this
	 *      zone. The format of the URL is specific to the ZIS product and the
	 *      transport protocol used to communicate with the ZIS
	 *  @return A new or cached Zone instance
	 */
	public Zone getInstance( String zoneId, String zoneUrl ) throws ADKException;

	/**
	 *  Gets a Zone instance with custom properties<p>
	 *  @param zoneId The zone identifier
	 *  @param zoneUrl The URL of the Zone Integration Server that manages this
	 *      zone. The format of the URL is specific to the ZIS product and the
	 *      transport protocol used to communicate with the ZIS
	 *  @param props Properties for the factory to use when creating a new Zone
	 *      instance. If the zone already exists, these properties are ignored
	 *      and the cached Zone instance is returned with the properties it
	 *      currently has.
	 *  @return A new or cached Zone instance
	 */
	public Zone getInstance( String zoneId, String zoneUrl, AgentProperties props ) throws ADKException;

	/**
	 *  Gets a Zone previously created by a call to getInstance
	 *  @param zoneId The zone identifier
	 *  @return The Zone instance
	 */
	public Zone getZone( String zoneId );

	/**
	 *  Gets all zones in the factory cache<p>
	 *  @return An array of Zones
	 */
	public Zone[] getAllZones();

	/**
	 *  Remove a zone from the ZoneFactory<p>
	 *  This method can only be called on a zone that is in the disconnected state.<p>
	 *  @param zone The zone to remove
	 */
	public void removeZone( Zone zone );
}
