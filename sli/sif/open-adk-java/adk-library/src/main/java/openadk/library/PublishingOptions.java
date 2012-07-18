//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


/**
 * 
 * Encapsulates all of the optional behavior that a publisher is able to support. 
 * Flags in this class are used to control behavior of the agent during provisioning
 *  
 * @author Andrew
 *
 */
public class PublishingOptions extends ProvisioningOptions {

	
	/**
	 * Flag that indicates whether or not the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 */
	private boolean fSendSIFProvide;
	
	/**
	 * Flag the indicates whether or not this publisher supports SIF_ExtendedQueries<p>
	 * 
	 *  If <code>false</code>, the ADK will automatically send an error packet response
	 *  back for any SIF_ExtendedQueries received.<p>
	 *  
	 *  If <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	private boolean fSupportsExtendedQuery;

	/**
	 * Creates an instance of PublishingOptions that supports the
	 * default SIF Context.
	 */
	public PublishingOptions( )
	{
		this( true );
	}
	
	/**
	 * Creates an instance of PublishingOptions that supports the
	 * default SIF Context.
	 *  @param sendSIFProvide <code>True</code> if the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 */
	public PublishingOptions( boolean sendSIFProvide )
	{
		super();
		fSendSIFProvide = sendSIFProvide;
	}
	
	
	/**
	 *  Creates an instance of PublishingOptions that only supports
	 * the given set of SIFContexts. 
	 * @param contexts
	 */
	public PublishingOptions( SIFContext... contexts)
	{
		super( contexts );
	}
	
	
	/**
	 * Creates an instance of PublishingOptions that only supports
	 * the given set of SIFContext. If the set of contexts given does not
	 * include the default SIF context, the default context will not be supported
	 * by this ReportPublisher<p>
	 * 
	 * @param sendSIFProvide <code>True</code> if the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 *
	 * @param contexts the explicit list of contexts to support
	 */
	public PublishingOptions( boolean sendSIFProvide, SIFContext... contexts )
	{
		super( contexts );
		fSendSIFProvide = sendSIFProvide;
	}
	

	/**
	 * Sets a flag that indicates whether or not the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 * 
	 * @param fSendSIFProvide
	 */
	public void setSendSIFProvide(boolean fSendSIFProvide) {
		this.fSendSIFProvide = fSendSIFProvide;
	}

	/**
	 * Gets the flag that indicates whether or not the ADK should provision this 
	 * agent as the default provider in the zone for the object.
	 * @return True if the ADK should send a SIF_Provide message
	 */
	public boolean getSendSIFProvide() {
		return fSendSIFProvide;
	}

	/**
	 * Sets a flag the indicates whether or not this publisher supports SIF_ExtendedQueries<p>
	 * 
	 * @param fSupportsExtendedQuery  If <code>false</code>, the ADK will automatically
	 * send an error packet response back for any SIF_ExtendedQueries received.If 
	 * <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	public void setSupportsExtendedQuery(boolean fSupportsExtendedQuery) {
		this.fSupportsExtendedQuery = fSupportsExtendedQuery;
	}

	/**
	 * Sets a flag the indicates whether or not this publisher supports SIF_ExtendedQueries<p>
	 * @return If <code>false</code>, the ADK will automatically
	 * send an error packet response back for any SIF_ExtendedQueries received.If 
	 * <code>true</code>, the ADK will notify the zone of SIF_ExtendedQuery support during
	 *  agent provisioning.
	 */
	public boolean getSupportsExtendedQuery() {
		return fSupportsExtendedQuery;
	}

}
