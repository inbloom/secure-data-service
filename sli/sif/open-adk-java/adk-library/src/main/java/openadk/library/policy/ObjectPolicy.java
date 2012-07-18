//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.policy;


/**
 * Represents policy around a specific type of object
 * @author Andrew Elmhorst
 * @version ADK 2.1
 *
 */
public abstract class ObjectPolicy {

	private String fObjectName;
	
	/**
	 * Creates an instance of ObjectPolicy
	 * @param objectType The name of the SIF data object
	 * that this ObjectPolicy applies to
	 */
	public ObjectPolicy( String objectType ){
		fObjectName = objectType;
	}
	
	/**
	 * Returns the name of the SIF data object that this
	 * ObjectPolicy applies to
	 * @return The name of the SIF data object that this 
	 * ObjectPolicy applies to
	 */
	public String getObjectName(){
		return fObjectName;
	}
	
}
