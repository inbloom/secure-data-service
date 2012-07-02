//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents information cached by the ADK about a specific SIF_Request
 * @author Andrew Elmhorst
 *
 */
public interface RequestInfo 
{
	/**
	 * @return The Object Type of the Request. e.g. "StudentPersonal"
	 */
	public String getObjectType();	
	
	/**
	 * @return The SIF_Request MessageId
	 */
	public String getMessageId();
	
	/**
	 * The Date and Time that that this request was initially made
	 * @return
	 */
	public Date getRequestTime();
	
	/**
	 *  Returns whether or not this Request is Active
	 * @return
	 */
	public boolean isActive();
	
	/**
	 * Returns the Serializable UserData state object that was placed in the 
	 * {@link openadk.library.Query} class at the time of the original request.
	 * @return
	 */
	public Serializable getUserData();}
