//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  * Encapsulates all of the optional behavior that a publisher is able to support. 
 * Flags in this class are used to control behavior of the agent during provisioning
 * @author Andrew
 *
 */
public class ReportPublishingOptions extends PublishingOptions {

	/**
	 * Creates an instance of PublishingOptions that supports the
	 * default SIF Context.
	 *  @param sendSIFProvide <code>True</code> if the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 */
	public ReportPublishingOptions( )
	{
		this( true );
	}
	
	/**
	 * Creates an instance of PublishingOptions that supports the
	 * default SIF Context.
	 *  @param sendSIFProvide <code>True</code> if the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 */
	public ReportPublishingOptions( boolean sendSIFProvide )
	{
		super( sendSIFProvide );
	}
	
	
	/**
	 *  Creates an instance of PublishingOptions that only supports
	 * the given set of SIFContexts. 
	 * @param contexts
	 */
	public ReportPublishingOptions( SIFContext... contexts)
	{
		super( contexts );
	}
	
	
}
