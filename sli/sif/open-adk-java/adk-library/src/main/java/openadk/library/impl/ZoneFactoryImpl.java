//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.Vector;
import java.util.Hashtable;

import openadk.library.*;

/**
 *  Default ZoneFactory implementation.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ZoneFactoryImpl implements ZoneFactory
{
	/**
	 *  Cache of Zones keyed by zoneId
	 */
	protected Hashtable<String,Zone> fZones = new Hashtable<String,Zone>();

	/**
	 *  Zone vector in the order created by the agent
	 */
	protected Vector<Zone> fZoneList = new Vector<Zone>();

	/**
	 *  The agent that owns this ZoneFactory. By associating factories with
	 *  Agents (rather than having a static singleton) we can support multiple
	 *  Agents per virtual machine.
	 */
	protected Agent fAgent;

	/**
	 *  Constructs a ZoneFactory for an Agent
	 *  @param agent The Agent that owns this factory
	 */
	public ZoneFactoryImpl( Agent agent )
	{
		fAgent = agent;
	}

	public synchronized Zone getInstance( String zoneId, String zoneUrl )
		throws ADKTransportException
	{
		return getInstance(zoneId,zoneUrl,null);
	}

	public synchronized Zone getInstance( String zoneId, String zoneUrl, AgentProperties props )
		throws ADKTransportException
	{
		if( zoneId == null )
			throw new IllegalArgumentException( "Zone ID cannot be null" );
		if( zoneUrl == null )
			throw new IllegalArgumentException( "Zone URL cannot be null" );

		Zone zone = null;

		synchronized( fZones )
		{
			//  Lookup zone by zoneId
			zone = fZones.get(zoneId);

			if( zone == null )
			{
				//  Not found so create new instance
				zone = createZone(zoneId,zoneUrl,props);
				fZones.put(zoneId,zone);
				fZoneList.addElement(zone);
			}
			
			/* TT3770 EP 3/30/2009. 
			 * See TT comments for explanation of why this block removed.
			else
			{
				//  Reassign properties in case they're different
				zone.setProperties( props );
			}
			*/
		}

		return zone;
	}
	
	protected Zone createZone( String zoneId, String zoneUrl, AgentProperties props )
		throws ADKTransportException
	{
		return new ZoneImpl(zoneId,zoneUrl,fAgent,props);
	}

	/**
	 *  Gets a Zone previously created by a call to getInstance
	 *  @param zoneId The zone identifier
	 *  @return The Zone instance
	 */
	public Zone getZone( String zoneId )
	{
		if( zoneId == null )
			throw new IllegalArgumentException( "Zone ID cannot be null" );

		return (Zone)fZones.get(zoneId);
	}

	public synchronized Zone[] getAllZones()
	{
		Zone[] arr = null;
		arr = new Zone[ fZoneList.size() ];
		fZoneList.copyInto(arr);
		return arr;
	}

	/**
	 *  Remove a zone from the cache
	 *  @param zone The Zone
	 */
	public synchronized void removeZone( Zone zone )
	{
		if( zone == null )
			throw new IllegalArgumentException( "Zone cannot be null" );
		if( zone.isConnected() )
			throw new IllegalStateException( "Zone is connected" );

		fZones.remove(zone.getZoneId());
		fZoneList.remove(zone);
	}
}
