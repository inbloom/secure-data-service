//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Refer to the Developer Guide for more information on the ADK's
 * provisioning modes.
 * @author Andrew Elmhorst
 *
 */
public enum AgentProvisioningMode {
	/**
	 *  The ADK uses the ZIS-Managed Provisioning mode for this zone. ADK
	 *  methods such as <code>Agent.connect</code> and <code>Topic.subscribe</code>
	 *  that would normally cause the ADK to send a provisioning message will
	 *  not send the message when this mode is enabled. Provisioning messages
	 *  include: SIF_Register and SIF_Unregister, SIF_Subscribe and SIF_Unsubscribe,
	 *  and SIF_Provide and SIF_Unprovide.<p>
	 *
	 *  Refer to the Developer Guide for more information on the ADK's
	 *  provisioning modes.
	 */
	ZIS,

	/**
	 *  The ADK uses the ADK-Managed Provisioning mode for this zone. When
	 *  enabled, the ADK sends provisioning messages at the appropriate times
	 *  when methods such as <code>Agent.connect</code> and <code>Topic.subscribe</code>
	 *  are called.  Refer to the Developer Guide for more information on the
	 *  ADK's provisioning modes.
	 */
	ADK,

	/**
	 *  The ADK uses the Agent-Managed Provisioning mode for this zone. When
	 *  enabled, the ADK does not send any provisioning messages. Agents must
	 *  explicitly call the following methods of the Zone class to perform all
	 *  provisioning tasks:<p>
	 *
	 *  <ul>
	 *      <li><code>Zone.sifRegister</code> and <code>Zone.sifUnregister</code></li>
	 *      <li><code>Zone.sifSubscribe</code> and <code>Zone.sifUnsubscribe</code></li>
	 *      <li><code>Zone.sifProvide</code> and <code>Zone.sifUnprovide</code></li>
	 *  </ul><p>
	 *
	 *  Refer to the Developer Guide for more information on the ADK's
	 *  provisioning modes.
	 */
	AGENT

}
