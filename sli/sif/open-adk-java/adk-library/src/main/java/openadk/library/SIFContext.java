//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.Serializable;
import java.util.Map;

import openadk.util.LinkedListMap;


/**
 * Contains behavior and implementation details for each SIF Context officially
 * supported by the ADK
 * @author Andrew
 *
 */
public class SIFContext implements Serializable 
{
	private static final String SIF_DEFAULT = "SIF_Default"; 
	
	static
	{
		DEFAULT = new SIFContext( SIF_DEFAULT );
		sDefinedContexts = new LinkedListMap<String,SIFContext>();
	}
	
	/**
	 * A list of SIF Contexts that have been defined by this agent instance 
	 */
	private static final transient Map<String,SIFContext> sDefinedContexts; 	
	
	/**
	 * The name of this context e.g. "SIF_Default"
	 */
	private final String fContextName;
	
	/**
	 * The default SIF Context defined by SIF ("SIF_Default")
	 */
	public static final SIFContext DEFAULT;

	private SIFContext( String contextName ){
		fContextName = contextName;
	}
	

	/**
	 * Creates a SIFContext object with the given name. If the name
	 * matches a context name already defined by the ADK, the existing context will
	 * be returned.
	 * 
	 * @param contextName
	 * @return a SIFContext
	 */
	public static SIFContext create( String contextName )
	{
		if(	contextName == null || 
			contextName.length() == 0 ||
			contextName.equalsIgnoreCase( SIF_DEFAULT ) ){
			return DEFAULT;
		}
		
		// Determine if the context is already defined
		SIFContext returnValue = sDefinedContexts.get( contextName );
		if( returnValue == null ){
			returnValue = new SIFContext( contextName );
			sDefinedContexts.put( contextName, returnValue );
		}

		return returnValue; 
	}
	
	/**
	 * Returns a SIFContext that has been defined by the agent, <code>null</code>
	 * if the context has not been defined
	 * 
	 * @param contextName
	 * @return the matching SIFContext instance or <code>null</code> if it
	 * has not been defined
	 */
	public static SIFContext isDefined( String contextName )
	{
		if(	contextName == null || 
				contextName.length() == 0 ||
				contextName.equalsIgnoreCase( SIF_DEFAULT ) ){
				return DEFAULT;
			}
		return sDefinedContexts.get( contextName );
	}
	
	
	/**
	 * Returns the name of this context (e.g. SIF_Default)
	 * @return the name of this context (e.g. SIF_Default)
	 */
	public String getName()
	{
		return fContextName;
	}
	
	/**
	 * Evaluates the native wrapped value of this object to see if
	 * it equals the value of the compared object, using a case-insensitive comparison
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
	    if( this == o )
	    {
	        return true;
	    }
	    if ((o != null) && (o.getClass().equals(this.getClass())))
	    {
	        SIFContext compared = (SIFContext)o;
	        if( this.fContextName == null )
	        {
	            return compared.fContextName == null;
	        }
	        return this.fContextName.equalsIgnoreCase( compared.fContextName );
	    }
	    return false;
	 }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    if( fContextName == null )
	    {
	        return -1;
	    }
	    else
	    {
	        return fContextName.toLowerCase().hashCode();
	    }
	 }
	

}
