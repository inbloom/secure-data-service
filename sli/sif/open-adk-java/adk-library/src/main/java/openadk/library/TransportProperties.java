//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Enumeration;

import openadk.library.impl.ZoneImpl;

/**
 *  Properties describing operational settings of a transport protocol.<p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public abstract class TransportProperties extends ADKProperties
{
	
	/**
	 * Returns whether this Transport  is enabled 
	 */
	private boolean fEnabled = true;
	
	/**
	 *  Constructor
	 */
	public TransportProperties()
	{
		this((TransportProperties)null);
	}

	/**
	 *  Constructs a TransportProperties object that inherits its properties
	 *  from a parent. Call the Agent.getDefaultTransportProperties method to
	 *  obtain the default TransportProperties object for a given transport
	 *  protocol.
	 *
	 *  @param inherit The parent TransportProperties object, usually obtained
	 *      by calling Agent.getDefaultTransportProperties
	 */
	public TransportProperties( TransportProperties parent )
	{
		fParent = parent;
	}

	/**
	 *  Gets the name of the transport protocol associated with these properties
	 *  @return A protocol name such as <i>http</i> or <i>https</i>
	 */
	public abstract String getProtocol();

	/**
	 *  Initialize the TransportProperties with default values
	 *  @param owner The zone that these TransportProperties are for, or null
	 */
	@Override
	public void defaults( Object owner )
	{
		String key = "adk.transport."+getProtocol();
		if( owner != null && owner instanceof ZoneImpl ){
			getZoneProperties( (ZoneImpl) owner, key );
		}
		else{
			getSystemProperties(owner, key );
		}
	}

	private void getZoneProperties(ZoneImpl impl, String key) {
		int keyLen = key.length();
		for( Object obj : impl.getProperties().keySet() ){
			String k = obj.toString();
			if( k.startsWith( key ) ){
				String name = k.substring( keyLen + 1 );
				String val = impl.getProperties().getProperty( k );
				impl.getLog().debug( createPropertyDebugStatement( name, val ) );
				put(name,val);
			}
		}
	}

	private void getSystemProperties(Object owner, String key ) {
		int keyLen = key.length();
		for( Enumeration e = System.getProperties().keys(); e.hasMoreElements(); )
		{
			String k = (String)e.nextElement();
			if( k.startsWith(key) )
			{
				String name = k.substring( keyLen + 1 );
				String val = System.getProperty(k);

				if( ( ADK.debug & ADK.DBG_PROPERTIES ) != 0 )
				{
					if( owner == null )
						ADK.log.debug( createPropertyDebugStatement(k, val) );
					else
					if( owner instanceof Agent )
						Agent.log.debug( createPropertyDebugStatement(k, val) );
				}

				put(name,val);
			}
		}
	}

	private String createPropertyDebugStatement(String k, String val) {
		return getProtocol().toUpperCase() + ": Using System property " + k + " = " + val;
	}

	/**
	 * Sets whether this particular transport is enabled
	 * @param isEnabled
	 */
	public void setEnabled(boolean isEnabled) {
		fEnabled = isEnabled;
	}
	/**
	 * Returns whether this particular transport is enabled
	 * @return true if this transport is enabled
	 */
	public boolean isEnabled() {
		return fEnabled;
	}
}
