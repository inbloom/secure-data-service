//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.ElementDef;
import openadk.library.ProvisioningOptions;

/**
 * Information about a specific handler for a specific message type
 * @author Andrew
 *
 * @param <T> The type of handler
 */
public class ProvisionedObject<T, V extends ProvisioningOptions> {
	
	private T fHandler;
	private V fOptions; 
	private ElementDef fObjectType;
	
	/**
	 * Creates a ProvisionedObject instance
	 * @param objectType The ElementDef describing the SIFDataObject represented by this instance
	 * @param handler The handler for this instance, such as a Subscriber or Publisher
	 * @param options The Provisioning options in effect for this instance
	 */
	public ProvisionedObject( ElementDef objectType, T handler, V options ){
		fObjectType = objectType;
		fHandler = handler;
		fOptions = options;
	}
	
	/**
	 * Returns the ElementDef describing the SIFDataObject represented by this instance
	 * @return the ElementDef describing the SIFDataObject represented by this instance
	 */
	public ElementDef getObjectType()
	{
		return fObjectType;
	}
	
	/**
	 * Returns the handler for this message type, such as a Subscriber, Publisher, ReportPublisher, etc.
	 * @return the handler for this message type, such as a Subscriber, Publisher, ReportPublisher, etc.
	 */
	public T getHandler(){
		return fHandler;
	}
	/**
	 * Returns the provisioining options in effect for this instance
	 * @return The provisioning options in effect for this instance
	 */
	public V getProvisioningOptions(){
		return fOptions;
	}
}
