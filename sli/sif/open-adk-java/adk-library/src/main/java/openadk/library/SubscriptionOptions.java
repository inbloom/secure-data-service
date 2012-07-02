//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


/**
 * Encapsulates the set of Subscription options supported by a given subscriber
 * @author Andrew
 *
 */
public class SubscriptionOptions extends ProvisioningOptions {
	
	private boolean fSendSIFSubscribe = true;
	
	/**
	 * Creates an instance of SubscriptionOptions that supports the
	 * default SIF Context
	 */
	public SubscriptionOptions()
	{
		super();
	}
	
	/**
	 * Creates an instance of SubscriptionOptions that only supports
	 * the given set of SIFContextx. If the set of contexts given does not
	 * include the default SIF context, the default context will not be supported
	 * by this Subscriber
	 * @param contexts
	 */
	public SubscriptionOptions( SIFContext... contexts )
	{
		super( contexts );
	}
	
	/**
	 * If ADK managed provisioining is in effect, this flag controls whether
	 * a SIF_Subscribe message is sent when connecting to the ZIS in legacy mode.<p>
	 * 
	 * The default value of this property is  <code>True</code>
	 * 
	 * @see AgentProperties#getProvisionInLegacyMode()
	 * @return <code>True</code> if a SIF_Subscribe message should be sent
	 */
	public boolean getSendSIFSubscribe(){
		return fSendSIFSubscribe;
	}
	
	/**
	 * If ADK managed provisioining is in effect, this flag controls whether
	 * a SIF_Subscribe message is sent when connecting to the ZIS in legacy mode.<p>
	 * 
	 * 	The default value of this property is  <code>True</code>
	 * 
	 * @see AgentProperties#getProvisionInLegacyMode()
	 * @param flag
	 */
	public void setSendSIFSubscribe( boolean flag ){
		fSendSIFSubscribe = flag;
	}
}
