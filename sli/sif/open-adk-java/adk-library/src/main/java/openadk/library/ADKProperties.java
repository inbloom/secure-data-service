//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Properties;

/**
 *  The abstract base class for agent and zone properties.<p>
 *
 *  ADKProperties adds convenience methods to the java.util.Properties class for
 *  getting and setting property values by data type. In addition, it overrides
 *  the getProperty method to inherit the property value from its parent if not
 *  defined locally. In the ADK, the properties of a zone are always inherited
 *  from the default properties of the agent.
 *  <p>
 *
 *  Note that property inheritance is only provided by the getProperty method;
 *  no other superclass methods support inheritance. Thus, calling enumerating
 *  property elements does not include the properties of the parent object.
 *  Similarly, saving an ADKProperties to disk with the store method only writes
 *  the properties that are defined locally. When the properties are
 *  subsequently read from disk, they will not include the inherited properties
 *  of the parent object, nor will the object's relationship with its parent be
 *  re-established.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class ADKProperties extends Properties
{
	/**
	 * 
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;
	
	protected ADKProperties fParent;

	/**
	 *  Protected constructor called by Agent to create root properties
	 *  inherited by all zones
	 */
	protected ADKProperties()
	{
		this((ADKProperties)null);

		defaults(null);
	}

	/**
	 *  Protected constructor called by Agent to create root properties
	 *  inherited by all zones
	 */
	protected ADKProperties( Agent agent )
	{
		this((ADKProperties)null);

		defaults(agent);
	}

	/**
	 *  Creates a properties object that inherits values from a parent
	 *  @param inherit The parent ADKProperties object
	 */
	public ADKProperties( ADKProperties inherit )
	{
		fParent = inherit;
	}

	/**
	 *  Called by the default constructor to set default property values.
	 *  Defaults are usually imported from the Java System properties.
	 */
	protected void defaults( Object owner )
	{

	}

	/**
	 *  Gets the parent object from which properties are inherited
	 * @return The parent properties object
	 */
	public ADKProperties getParent() {
		return fParent;
	}

	/**
	 *  Gets a property value as a <code>String</code>. The property is
	 *  inherited from this object's parent if not defined locally.<p>
	 *  @param prop The name of the property
	 *  @param defaultValue The default value to return when the proprety is undefined
	 *  @return The value of the property, or <i>defaultValue</i> if undefined
	 */
	@Override
	public String getProperty( String prop, String defaultValue )
	{
		String s = getProperty(prop);
		return s == null ? defaultValue : s;
	}

	/**
	 *  Gets a property value as an <code>int</code>. The property is
	 *  inherited from this object's parent if not defined locally.<p>
	 *  @param prop The name of the property
	 *  @param defaultValue The default value to return when the proprety is
	 *      undefined or is not an integer
	 *  @return The value of the property, or <i>defaultValue</i> if
	 *      undefined or not an integer
	 */
	public int getProperty( String prop, int defaultValue )
	{
		String s = getProperty(prop);
		if( s != null ) {
			try {
	    		return Integer.parseInt(s);
		    } catch( Exception e ) {
			}
		}

		return defaultValue;
	}
	
	/**
	 *  Gets a property value as a <code>long</code>. The property is
	 *  inherited from this object's parent if not defined locally.<p>
	 *  @param prop The name of the property
	 *  @param defaultValue The default value to return when the property is
	 *      undefined or is not a long
	 *  @return The value of the property, or <i>defaultValue</i> if
	 *      undefined or not a long
	 */
	public long getProperty( String prop, long defaultValue )
	{
		String s = getProperty(prop);
		if( s != null ) {
			try {
	    		return Long.parseLong(s);
		    } catch( Exception e ) {
			}
		}

		return defaultValue;
	}

	/**
	 *  Gets a property value as a <code>boolean</code>. The property is
	 *  inherited from this object's parent if not defined.<p>
	 *  @param prop The name of the property
	 *  @param defaultValue The default value to return when the proprety is
	 *      undefined or is not set to "true"
	 *  @return The value of the property, or <i>defaultValue</i> if undefined
	 *      or is not set to "true"
	 */
	public boolean getProperty( String prop, boolean defaultValue )
	{
		String s = getProperty(prop);

		return s == null ? defaultValue : ( s.equalsIgnoreCase("true") );
	}

	/**
	 *  Sets an <code>int</code> property<p>
	 *  @param prop The name of the property
	 *  @param value The property value
	 */
	public void setProperty( String prop, int value )
	{
		setProperty( prop, String.valueOf(value) );
	}
	
	/**
	 * Sets a <code>long</code> property
	 * @param prop The name of the property
	 * @param value The property value
	 */
	public void setProperty(String prop, long value )
	{
		setProperty( prop, String.valueOf(value) );
	}

	/**
	 *  Sets a <code>boolean</code> property<p>
	 *  @param prop The name of the property
	 *  @param value The property value
	 */
	public void setProperty( String prop, boolean value )
	{
		setProperty( prop, value ? "true":"false" );
	}

	/**
	 *  Sets the value of a string property<p>
	 *  @param name The name of the property
	 *  @param value The property value
	 */
	@Override
	public Object setProperty( String name, String value )
	{
		if( value != null )
			return super.setProperty(name,value);

		return getProperty(name);
	}

	/**
	 *  Overridden to inherit properties from parent
	 *  @param name The property name
	 *  @return The value of the property if defined by this object or by its parent
	 */
	@Override
	public String getProperty( String name )
	{
		String s = super.getProperty(name);
		if( s == null && fParent != null )
			return fParent.getProperty(name);

		return s;
	}
}
