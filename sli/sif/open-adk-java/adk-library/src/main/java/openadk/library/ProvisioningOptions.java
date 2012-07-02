//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents Provisioning settings that can be applied when declaring support for 
 * objects or services
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 *
 */
public class ProvisioningOptions {
	
	private List<SIFContext> fSupportedContexts = new ArrayList<SIFContext>(1);
	
	/**
	 * Creates a ProvisioningOptions instance that supports the default SIF Context
	 */
	protected ProvisioningOptions()
	{
		fSupportedContexts.add( SIFContext.DEFAULT );
	}
	
	protected ProvisioningOptions( SIFContext... contexts )
	{
		addSupportedContext( contexts );
	}
	
	/**
	 * Adds one or more SIFContexts to this ProvisioningOptions instance
	 * @param contexts One or more supported SIFContext instances
	 */
	public void addSupportedContext( SIFContext...contexts )
	{
		for( SIFContext ctxt : contexts ){
			if( !fSupportedContexts.contains( ctxt ) ){
				fSupportedContexts.add( ctxt );
			}
		}
	}
	
	/**
	 * Determines if this provisioning instance supports the specified
	 * SIF Context
	 * @param contextName
	 * @return True if the SIF Context is supported
	 */
	public boolean supportsContext( String contextName )
	{
		for( SIFContext ctxt : fSupportedContexts ){
			if( ctxt.equals( contextName )){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the list of SIF Contexts supported by this set of provisioining options
	 * @return the list of SIF Contexts supported by this set of provisioining options
	 */
	public List<SIFContext> getSupportedContexts()
	{
		return fSupportedContexts;
	}

}
