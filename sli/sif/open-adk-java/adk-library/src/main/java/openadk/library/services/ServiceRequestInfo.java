//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.services;

import java.io.Serializable;

/**
 * Represents a data structure used by the ADK services subsystem to track
 * information needed for calling methods on services and handling responses.
 *
 * @author Andrew Elmhorst
 * @version ADK 2.3
 *
 */
public class ServiceRequestInfo implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -8931584403370686890L;

	private String serviceName;
	private String fMethodName;

	private String fDestinationId;
	
	// JEN added to match ETF
	private String fServiceMsgId;
	private String fMsgId;

	/**
	 * Creates in instance of ServiceRequestInfo
	 *
	 */
	public ServiceRequestInfo() {

	}

	/**
	 * Gets the name of the service method that has been invoked
	 *
	 * @return the name of the service method that has been invoked
	 */
	public String getMethodName() {
		return fMethodName;
	}


	/**
	 * Sets the name of the service method that will be invoked
	 * @param methodName
	 */
	public void setMethodName( String methodName ){
		fMethodName = methodName;
	}


	/**
	 * Sets the SIF_DestinationID value that will be used if this is a directed
	 * SIF Zone Service request
	 *
	 * @param destinationID
	 */
	public void setDestinationId(String destinationID) {
		fDestinationId = destinationID;
	}

	/**
	 * Gets the SIF_DestinationID value that will be used if this is a directed
	 * SIF Zone Service request
	 *
	 * @return destinationID
	 */
	public String getDestinationId() {
		return fDestinationId;
	}

	/**
	 * Sets the SIF Service Message ID
	 *
	 * @param msgId
	 */
	public void setSIFServiceMsgId( String msgId ) {
		fServiceMsgId=msgId;
	}
	
	/**
	 * Gets theSIF Service Message ID
	 *
	 * @return serviceMsgID
	 */
	public String getSIFServiceMsgId() {
		return fServiceMsgId;
	}
	
	/**
	 * Sets the SIF Message ID
	 *
	 * @param msgId
	 */
	public void setSIFMsgId( String msgId ) {
		fMsgId=msgId;
	}
	
	/**
	 * Gets the SIF Message ID
	 *
	 * @return msgID
	 */
	public String getSIFMsgId() {
		return fMsgId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}
