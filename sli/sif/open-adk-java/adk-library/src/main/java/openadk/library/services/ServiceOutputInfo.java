//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import openadk.library.SIFVersion;

/**
 * Represents the information needed to open a SIFServiceResponseSender class to
 * send a deferred SIF Zone Service method response.
 *
 * @author Andrew Elmhorst
 * @version ADK 2.3
 *
 */
public interface ServiceOutputInfo {

	/**
	 * Returns the SIF Request Message ID
	 *  @return String
	 */
	String getSIFRequestMsgId();

	/**
	 * Returns SIF Version number used
	 *  @return SIFVersion
	 */
	SIFVersion getSIFVersion();

	/**
	 * Returns max buffer size
	 *  @return int
	 */
	int getSIFMaxBufferSize();

	/**
	 * Returns ID of mesage source
	 *  @return String
	 */
	String getSIFRequestSourceId();

	/**
	 * Returns Service Name
	 *  @return String
	 */
	String getService();

	/**
	 * Returns Service method name
	 *  @return String
	 */
	String getOperation();

}
