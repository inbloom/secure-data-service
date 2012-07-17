//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;


/**
 * Contains policy information that prescribes how the ADK requests
 * data for a specific object type
 * @author Andrew Elmhorst
 * @version ADK 2.1
 *
 */
public class ObjectRequestPolicy extends ObjectPolicy {

	private String fRequestVersions;
	private String fSourceId;
	
	public ObjectRequestPolicy(String objectName) {
		super( objectName );
	}
	
	/**
	 * Gets the SIF version(s) that should be used for requesting objects
	 * of this type.
	 * @return The version to use when requesting this object e.g. "1.1", "2.*", 
	 * or a comma-delimitedset of versions e.g. "1.5r1, 2.0r1, 2.*"
	 */
	public String getRequestVersions(){
			return fRequestVersions;
	}
	
	/**
	 * Sets the SIF versions that should be used for requesting objects
	 * @param requestVersion The version to use when requesting this 
	 * object, e.g. "1.1" or "2.*", or a comma-delimitedset of versions 
	 * e.g. "1.5r1, 2.0r1, 2.*"
	 */
	public void setRequestVersions( String requestVersions ){
		fRequestVersions = requestVersions;
	}
	
	/**
	 * Gets the SourceId of the agent from whom data should be requested 
	 * for this object type
	 * @return The sourceId that will be used for the <code>SIF_DestinationId</code>
	 *  	in SIF_Requests for this object type
	 */
	public String getRequestSourceId()
	{
		return fSourceId;
	}
	
	/**
	 * Gets the SourceId of the agent from whom data should be requested 
	 * for this object type
	 * @param sourceId The sourceId that will be used for the 
	 * 	<code>SIF_DestinationId</code> in SIF_Requests for this object type
	 */
	public void setRequestSourceId( String sourceId )
	{
		fSourceId = sourceId;
	}


}
