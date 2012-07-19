//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import openadk.library.*;


/**
 * Manages the state of all open transports used by an agent instance.
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class TransportManagerImpl implements TransportManager
{

	private ArrayList<Transport> fTransports = new ArrayList<Transport>();
	private ArrayList<TransportProperties> fDefaultTransportProps;
	
	/**
	 * Activate the transport for this zone
	 * @param zone the zone that is being activated
	 * @return The transport object that was used to activate the zone
	 * @throws ADKTransportException
	 */
	public synchronized IProtocolHandler activate( ZoneImpl zone )
		throws ADKTransportException
	{
		//  Activate the transport used by the zone
		Transport trans = getTransport( zone.getProperties().getTransportProtocol() );
		trans.activate( zone );
		return trans.createProtocolHandler( zone.getProperties().getMessagingMode() );
	}
	
	/**
	 * Gets the Transport instance that has been instantiated for the specified protocol
	 * @param protocol The protocol to retrieve the transport instance for (e.g. "http")
	 * @return The transport object for the specified protocol
	 * @throws ADKTransportException if the protocol is not supported by the ADK
	 */
	public synchronized Transport getTransport( String protocol )
		throws ADKTransportException
	{
		if( protocol == null ){
			throw new IllegalArgumentException( "Protocol cannot be null" );
		}
		protocol = protocol.toLowerCase();
		
		for( Transport trans : fTransports ){
			if( trans.getProtocol().equals( protocol ) ){
				return trans;
			}
		}

		// No transport has been created for this protocol yet. Create
		// new one using the TransportPlugin
		TransportPlugin tp = ADK.getTransportProtocol( protocol );
		TransportProperties defs = getDefaultTransportProperties( protocol );
		
		Transport trans = tp.newInstance( defs );
		fTransports.add( trans );
		return trans;
	}
	
	/**
	 *  Gets the default properties for a transport protocol.<p>
	 *
	 *  Each transport protocol supported by the ADK is represented by a class
	 *  that implements the Transport interface. Transports are identified by
	 *  a string such as "http" or "https". Like Zones, each Transport instance
	 *  is associated with a set of properties specific to the transport
	 *  protocol. Such properties may include IP address, port, SSL security
	 *  attributes, and so on. The default properties for a given transport
	 *  protocol may be obtained by calling this method.<p>
	 * @param protocol 
	 *
	 *  @return The default properties for the specified protocol
	 *
	 *  @exception ADKTransportException is thrown if the protocol is not supported
	 *      by the ADK
	 */
	public TransportProperties getDefaultTransportProperties( String protocol )
		throws ADKTransportException
	{
		//  Already initialized?
		if( fDefaultTransportProps == null ){
			fDefaultTransportProps = new ArrayList<TransportProperties>();
		} else {
			for( TransportProperties props : fDefaultTransportProps ){
				if( props.getProtocol().equals( protocol ) ){
					return props;
				}
			}
		}
		
		// Didn't find the transport properties above. Create a new one and return it
		TransportPlugin tp = ADK.getTransportProtocol( protocol );
		if( tp == null ){
			throw new ADKTransportException( "The requested transport protocol: '" + protocol +
						"'  is not supported by this instance of the ADK", null );
		}
		
		TransportProperties props = tp.createProperties();
		props.defaults( null );
		fDefaultTransportProps.add( props );
		return props;
	}
	
	
	/**
	 * Shuts down all transports managed by this TransportManager instance 
	 * @throws ADKTransportException 
	 */
	public synchronized void shutdown()
		throws ADKTransportException
	{
		for( Transport t : fTransports ){
			t.shutdown();
		}
		fTransports.clear();
	}


	/**
	 * Initializes the enabled agent transports
	 * @param agent
	 * @throws ADKTransportException
	 */
	public void activate( Agent agent )
		throws ADKTransportException
	{
			
		// Initialize each transport supported by the ADK
		for( String protocol : ADK.getTransportProtocols() ){
			TransportProperties tp = getDefaultTransportProperties( protocol );
			if( tp.isEnabled() ){
				Transport transport = getTransport( protocol );
				transport.activate( agent );
			}
		}
		
	}

}
