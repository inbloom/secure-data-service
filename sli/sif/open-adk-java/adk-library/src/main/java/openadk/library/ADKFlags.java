//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Flags and constants used throughout the ADK.<p>
 *
 *  <b>Provisioning Flags</b><p>
 *
 *  The PROV_ flags are used to control the provisioning process when the agent
 *  registers with zones and topics. These flags are typically passed to methods
 *  such as <code>Zone.connect</code>, <code>Topic.setSubscriber</code>, <code>Topic.setPublisher</code>,
 *  and <code>Agent.shutdown</code>.
 *  <p>
 *
 *  SIF provisioning messages include:
 *
 *  <ul>
 *      <li><code>&lt;SIF_Register&gt;</code></li>
 *      <li><code>&lt;SIF_Unregister&gt;</code></li>
 *      <li><code>&lt;SIF_Provide&gt;</code></li>
 *      <li><code>&lt;SIF_Unsubscribe&gt;</code></li>
 *      <li><code>&lt;SIF_Subscribe&gt;</code></li>
 *      <li><code>&lt;SIF_Unsubscribe&gt;</code></li>
 *  </ul>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class ADKFlags
{
	/**
	 *  Identifies the Agent Local Queue (as opposed to the agent's queue on the ZIS)
	 */
	public static final int QUEUE_LOCAL =                       0x00000001;

	/**
	 *  Identifies the agent's queue on the ZIS (as opposed to the Agent Local Queue)
	 */
	public static final int QUEUE_SERVER =                      0x00000002;


	/**
	 *  Provisioning Option: No provisioning action should be taken
	 */
	public static final int PROV_NONE =                         0x00000000;

	/**
	 *  Provisioning Option: Send a SIF_Register message
	 */
	public static final int PROV_REGISTER =                     0x00000001;

	/**
	 *  Provisioning Option: Send a SIF_Unregister message
	 */
	public static final int PROV_UNREGISTER =                   0x00000002;

	/**
	 *  Provisioning Option: Send a SIF_Provide message
	 */
	public static final int PROV_PROVIDE =                      0x00000004;

	/**
	 *  Provisioning Option: Send a SIF_Unprovide message
	 */
	public static final int PROV_UNPROVIDE =                    0x00000008;

	/**
	 *  Provisioning Option: Send a SIF_Subscribe message
	 */
	public static final int PROV_SUBSCRIBE =                    0x00000010;

	/**
	 *  Provisioning Option: Send a SIF_Unsubscribe message
	 */
	public static final int PROV_UNSUBSCRIBE =                  0x00000020;

	/**
	 *  Instruct the ADK to put the agent to sleep upon successful connection
	 *  to the zone. The agent is responsible for waking up the agent when it
	 *  is ready to begin receiving messages. This flags should be passed to
	 *  the <code>Zone.connect</code> method to prevent the agent from receiving
	 *  messages upon a successful connection to the zone.
	 */
	public static final int SLEEP_ON_CONNECT =                  0x10000000;
}
