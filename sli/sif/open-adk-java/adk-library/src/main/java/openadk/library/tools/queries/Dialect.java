//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

/**
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public abstract class Dialect {
	
	
	
	private String fQuoteCharacters;
	
	protected Dialect( String quoteCharacters )
	{
		fQuoteCharacters = quoteCharacters;
	}
	
	
	/**
	 *  Render a field value as a string
	 */
	public String renderString( String value )
	{
		return fQuoteCharacters + value + fQuoteCharacters;
	}

	/**
	 *  Render a field value as a number
	 */
	public String renderNumeric( String value )
	{
		return value;
	}

	/**
	 *  Render a field value as a date
	 */
	public String renderDate( String value )
	{
		return value;
	}

	/**
	 *  Render a field value as a time
	 */
	public String renderTime( String value )
	{
		return value;
	}
	
	/**
	 *  Render a field value as a time
	 */
	public String renderTimeStamp( String value )
	{
		return value;
	}
	
	
	
	
}
