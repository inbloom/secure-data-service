package org.sli.ingestion;

import org.sli.ingestion.processors.ContextManager;



/**
 * Interchange domain association interface.
 * 
 * Requires Interchange domain representations to allow for initializing the extended SLI domain association instances
 * using the appropriate DAL repositories.
 * 
 */

public interface InterchangeAssociation {
	
	/**
	 * Initialize the SLI Ingestion association instance using lookups on the appropriate SLI repositories.
	 * 
	 */
	public void init(ContextManager contextManager);
	
}
