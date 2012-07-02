//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


/**
 * Encapsulates a set of provisioning options for a QueryResults handler
 * @author Andrew
 *
 */
public class QueryResultsOptions extends ProvisioningOptions {

	
	/**
	 * Flag the indicates whether or not this QueryResults instance supports SIF_ExtendedQueryResults<p>
	 * 
	 *  If <code>false</code>, the ADK will automatically send an error packet response
	 *  back for any SIF_ExtendedQueryResults received.<p>
	 *  
	 *  If <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	private boolean fSupportsExtendedQuery;
	
	/**
	 * Creates an instance of QueryResultsOptions that supports the
	 * default SIF Context
	 */
	public QueryResultsOptions()
	{
		super();
	}
	
	/**
	 * Creates an instance of QueryResultsOptions that only supports
	 * the given set of SIFContexts. If the set of contexts given does not
	 * include the default SIF context, the default context will not be supported
	 * by this QueryResults instance
	 * @param contexts
	 */
	public QueryResultsOptions( SIFContext... contexts )
	{
		super( contexts );
	}
	
	/**
	 * Sets a flag the indicates whether or not this publisher supports SIF_ExtendedQueries<p>
	 * 
	 * @param fSupportsExtendedQuery  If <code>false</code>, the ADK will automatically
	 * send an error packet response back for any SIF_ExtendedQueryResults received.If 
	 * <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	public void setSupportsExtendedQuery(boolean fSupportsExtendedQuery) {
		this.fSupportsExtendedQuery = fSupportsExtendedQuery;
	}

	/**
	 * Sets a flag the indicates whether or not this publisher supports SIF_ExtendedQueries<p>
	 * @return If <code>false</code>, the ADK will automatically
	 * send an error packet response back for any SIF_ExtendedQueryResults received.If 
	 * <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	public boolean getSupportsExtendedQuery() {
		return fSupportsExtendedQuery;
	}
}
