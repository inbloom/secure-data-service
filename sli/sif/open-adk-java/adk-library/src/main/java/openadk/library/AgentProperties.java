//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Properties;
import java.util.Enumeration;

import java.io.IOException;

import openadk.library.impl.ZoneImpl;
import openadk.library.policy.ADKDefaultPolicy;
import openadk.library.policy.PolicyFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *  Properties describing the operational settings of the agent or a zone.<p>
 *
 *  The constructor will initialize each property to its default value if the
 *  property is defined in the Java System properties (more precisely, any
 *  System property that begins with the string "adk.")  Otherwise the ADK's
 *  factory defaults are used. Because of this it is possible to adjust an
 *  agent's properties at runtime with the -D java command-line option.
 *  <p>
 *
 *  The properties below are currently defined. Most properties can be set in both the
 *  agent or zone scope so that configurable options can be defined on a zone-by-zone
 *  basis. Properties set in the agent scope are global to the agent and inherited
 *  by all zones that have not explicitly overridden the property. To set a
 *  property in the agent scope, call <code>Agent.getProperties</code> method to
 *  obtain the agent's properties object, then call its setter methods. To set a
 *  property in the zone scope, call a zone's <code>Zone.getProperties</code>
 *  method to obtain the zone's AgentProperties object, then call its setter
 *  methods.
 *  <p>
 *
 *  <table border="1" cellpadding="2" cellspacing="3">
 *
 *      <tr>
 *          <td><center><b>Property / Description</b></center></td>
 *          <td><center><b>Default</b></center></td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.mode</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates whether the agent should operate in Push or Pull mode.
 *              Possible values include <code>AgentMessagingMode.PUSH</code> and
 *              <code>AgentMessagingMode.PULL</code>. When Push mode is selected,
 *              the agent establishes a local socket to listen for incoming
 *              messages sent by the ZIS. (The Transport object associated with
 *              a zone dictates the type and parameters of the local socket.
 *              Transport properties are set independently in Transport objects.)
 *              When Pull mode is selected, the agent periodically polls the ZIS
 *              to check its queue for new messages. The default polling frequency
 *              can adjusted with the <code>adk.messaging.pullFrequency</code>
 *              property. Per SIF specification, an agent must use the same
 *              messaging mode from the time it is registered on the ZIS until
 *              it is unregistered.
 *              @see #getMessagingMode()
 *              @see #setMessagingMode(AgentMessagingMode)
 *          </td>
 *          <td valign="top">
 *              <code>AgentProperties.PULL</code>
 *          </td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.transport</code></b></td></tr>
 *      <tr>
 *          <td>The transport protocol to use (e.g. "http", "https")
 *          @see #getTransportProtocol()
 *          @see #setTransportProtocol(String)
 *          </td>
 *          <td valign="top">"http"</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.pullFrequency</code></b></td></tr>
 *      <tr>
 *          <td>For pull agents: the polling frequency in milliseconds
 *          @see #getPullFrequency()
 *          @see #setPullFrequency(int)
 *          </td>
 *          <td valign="top">30 seconds</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.pullDelayOnError</code></b></td></tr>
 *      <tr>
 *          <td>
 *              For pull agents, gets the amount of time in milliseconds the agent
 *              will delay when it encounters a transport error or disconnected
 *              zone when attempting to pull the next message from its queue.
 *              @see #getPullDelayOnError()
 *              @see #setPullDelayOnError(int)
 *          </td>
 *          <td valign="top">3 minutes</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.maxBufferSize</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The maximum size of messages (in bytes) that can be processed by
 *              this agent. This setting is used whenever the SIF_MaxBufferSize
 *              element is required in a SIF_Register or SIF_Request message.
 *              @see #getMaxBufferSize()
 *              @see #setMaxBufferSize(int)
 *          </td>
 *          <td valign="top">393216</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.effectiveBufferSize</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The maximum size of messages (in bytes) that will be processed
 *              in-memory before the ADK off-loads the message to the local file
 *              system for processing. This setting is used internally to
 *              influence memory management but is not used in any SIF messages.
 *              @see #getEffectiveBufferSize()
 *              @see #setEffectiveBufferSize(int)
 *          </td>
 *          <td valign="top">32000</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.keepMessageContent</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Determines if the ADK will retain SIF_Message XML content after
 *              it processes a message. When enabled, the SIFMessageInfo.getMessage
 *              method returns a non-null value (otherwise it returns null). A
 *              SIFMessageInfo object is passed to all message handlers such as
 *              <code>Subscriber.onEvent</code>, <code>Publisher.onQuery</code>, and
 *              <code>QueryResults.onQueryResults</code>
 *              @see #getKeepMessageContent()
 *              @see #setKeepMessageContent(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.strictVersioning</b></code></td></tr>
 *      <tr>
 *          <td>
 *              When set to <code>true</code>, the agent will only parse messages
 *              received from agents that are using the same version of SIF as
 *              this agent. An agent declares the version of SIF it will use
 *              when initializing the ADK's class framework. When <code>false</code>,
 *              the agent parses messages from all versions of SIF supported by
 *              the ADK's SIF Data Objects (SDO) library.
 *              @see #getStrictVersioning()
 *              @see #setStrictVersioning(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *        <tr><td colspan="2"><b><code>adk.messaging.strictTypeParsing</b></code></td></tr>
 *      <tr>
 *          <td>
 *              When set to <code>true</code>, the agent will throw an exception while
 *              parsing a SIF_Message if it cannot parse the value of a strongly-typed
 *              element or attribute. When <code>false</code>,
 *              the agent ignores the values it cannot parse.
 *              @see #getStrictVersioning()
 *              @see #setStrictVersioning(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.oneObjectPerResponse</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Instructs the ADK to return one SIF Data Object per SIF_Response
 *              packet. When disabled (the default), the ADK fits as many SIF
 *              Data Objects in a single response packet as allowed by the
 *              requestor's SIF_MaxBufferSize.
 *              @see #getOneObjectPerResponse()
 *              @see #setOneObjectPerResponse(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.processEventsFromSelf</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Instructs the ADK to process SIF_Event messages that were reported
 *              by this agent (i.e. the SourceId of the SIF_Event matches the
 *              SourceId of this agent). By default, such events are ignored by
 *              the class framework and automatically acknowledged as successfully
 *              received.
 *              @see #getProcessEventsFromSelf()
 *              @see #setProcessEventsFromSelf(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.noRequestIndividualElements</code></b></td></tr>
 *      <tr>
 *          <td>
 *              A compatibility option that instructs the ADK to render SIF_Request
 *              messages without SIF_Element field restrictions. When this property
 *              is enabled, the class framework will not include SIF_Element elements
 *              in SIF_Request messages even if you have called the
 *              Query.addFieldRestriction method. This property can be used when
 *              requesting data from agents that do not work well if SIF_Elements
 *              are present in the SIF_Request.
 *              @see #getNoRequestIndividualElements()
 *              @see #setNoRequestIndividualElements(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.messaging.disableDispatcher</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Advanced - Disables the ADK's message dispatcher, causing all
 *              messages received by the agent to be ignored and disposed of
 *              immediately without dispatching to the agent's message handlers.
 *              This property is only useful in rare situations when an agent
 *              sends messages to the zone integration server but does not want
 *              to process any messages in its queue.
 *              @see #getDisableMessageDispatcher()
 *              @see #setDisableMessageDispatcher(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.compatibility.useZoneStatusSystemControl</code></b></td></tr>
 *      <tr>
 *          <td>
 *              When this property is <code>true</code>
 *              and the ADK is initialized for SIF 1.5 or later, it will issue a
 *				synchronous SIF_SystemControl/SIF_GetZoneStatus message when the
 *				<code>Zone.getZoneStatus</code> method is called instead of
 *				using asynchronous SIF_Request messages. This mechanism of obtaining
 *				SIF_ZoneStatus is preferred over the traditional SIF_Request method,
 *				but is not officially supported in SIF as of 1.5. It is considered
 *				experimental. When this property is <code>false</code> (the default),
 *				the ADK issues SIF_Requests to obtain the SIF_ZoneStatus object.
 *				@see #getUseZoneStatusSystemControl()
 *				@see #setUseZoneStatusSystemControl(boolean)
 *          </td>
 *          <td valign="top">true if the ADK is initialize to SIF 2.0 or later, otherwise false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.zisVersion</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Defines the latest version of the SIF Specification supported by
 *              the Zone Integration Server to which the agent is connecting. This
 *              property defaults to the latest version of the SIF Specification
 *              supported by the ADK. Currently, it affects how SIF_Register
 *              messages are sent: if the ZIS supports SIF 1.1 or later, the ADK
 *              will send a SIF_Register with multiple SIF_Version elements, one
 *              for each version of the SIF Specification supported by the ADK.
 *              The first SIF_Version will be the version passed to the <code>ADK.initialize</code>
 *              method. If the ZIS does not support SIF 1.1 or later, the ADK will send
 *              a SIF_Register with a single SIF_Version element where the value
 *              is equal to the version passed to the <code>ADK.initialize</code> method.
 *              @see #getZisVersion()
 *              @see #setZisVersion(String)
 *          </td>
 *          <td valign="top">1.1</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.overrideSifVersions</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Overrides the way the ADK prepares SIF_Register/SIF_Version elements
 *              to include only the list of versions specified in the comma-delimited
 *              list. When connecting to a SIF 1.1 or later zone integration server,
 *              the class framework will include a SIF_Version element for the
 *              version of SIF used to initialize the ADK, followed by one additional
 *              SIF_Version element for each version specified by this property.
 *              @see #getOverrideSifVersions()
 *              @see #setOverrideSifVersions(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>adk.provisioning.overrideSifMessageVersionForSifRequests</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Overrides the way the ADK prepares the SIF_Message/Version element
 *              for SIF_Requests to be the version specified by this property. This
 *              override applies only to an Adk initialized to SIF Versions greater 
 *              than 2.0.  Normally, the Adk causes this version to default to 2.0r1.
 *              @see #getOverrideSifMessageVersionForSifRequests()
 *              @see #setOverrideSifMessageVersionForSifRequests(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>  
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.batch</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Controls how the ADK prepares SIF_Provide and SIF_Subscribe
 *              provisioning messages. When this property is set to false (the
 *              default), the ADK sends an individual SIF_Provide and SIF_Subscribe
 *              message to the zone for each SIF Data Object. If any of the
 *              messages fail with an Access Control error (Category 4), the
 *              error is recorded as a warning and subsequently returned by the
 *              <code>Zone.getConnectWarnings</code> method. All other SIF Errors
 *              result in an exception thrown by the <code>Zone.connect</code>
 *              method. When this property is set to true, SIF Data Objects are
 *              batched into a single message that will be accepted or rejected
 *              as a group by the zone integration server.
 *              @see #isBatchProvisioning()
 *              @see #setBatchProvisioning(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.mode</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The provisioning mode in effect for this zone. Possible values:
 *              "adk" for ADK-managed provisioning; "zis" for ZIS-managed
 *              provisioning; or "agent" for Agent-managed provisioning. Refer
 *              to the ADK Developer Guide for an explanation of the three
 *              provisioning modes.
 *              @see #getProvisioningMode()
 *              @see #setProvisioningMode(AgentProvisioningMode)
 *          </td>
 *          <td valign="top">"adk"</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.legacy</code></b></td></tr>
 *      <tr>
 *          <td>
 *             Whether the ADK will provision the agent using the legacy SIF_Subscribe and
 *             SIF_Provide messages even if it is running in SIF 2.0. By default, the ADK
 *             will elect to use the new SIF_Provision method in SIF 2.0. However, this property
 *             may need to be set if the agent is running in SIF 2.0 against a ZIS that has not
 *             properly implemented the SIF_Provision message.
 *             @see #getProvisionInLegacyMode()
 *             @see #setProvisionInLegacyMode(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.ignoreErrors</code></b></td></tr>
 *      <tr>
 *          <td>
 *              A compatibility option that determines if the ADK will throw
 *              exceptions when a SIF_Error is received by the ZIS during
 *              ADK-managed provisioning. This property should be enabled when
 *              connecting to the OpenSIF ZIS (0.9.x) because that it incorrectly
 *              treats attempt to re-provide or re-subscribe as errors instead of
 *              successful statuses.
 *              @see #getIgnoreProvisioningErrors()
 *              @see #setIgnoreProvisioningErrors(boolean)
 *          </td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.icon</code></b></td></tr>
 *      <tr>
 *          <td>
 *             A valid URL to an icon for the agent that matches the specification for SIF_Icon.
 *             If this property is specified, it will be sent to the ZIS during agent registration.
 *             @see #getAgentIconURL()
 *             @see #setAgentIconURL(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.agentVendor</code></b></td></tr>
 *      <tr>
 *          <td>
 *             The name of the vendor who developed this SIF agent. This information is available
 *             in SIF_Register and SIFZoneSTatus
 *             @see #getAgentVendor()
 *             @see #setAgentVendor(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.agentVersion</code></b></td></tr>
 *      <tr>
 *          <td>
 *             The version number of this agent, e.g. "2.0.1.11"
 *             @see #getAgentVersion()
 *             @see #setAgentVersion(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.applicationName</code></b></td></tr>
 *      <tr>
 *          <td>
 *             The name of the application that this agent services.
 *             This information is available in SIF_Register and SIFZoneSTatus
 *             @see #getApplicationName()
 *             @see #setApplicationName(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.applicationVendor</code></b></td></tr>
 *      <tr>
 *          <td>
 *             The name of the vendor who developed the application that this agent services.
 *             This information is available in SIF_Register and SIFZoneSTatus
 *             @see #getApplicationVendor()
 *             @see #setApplicationVendor(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.provisioning.applicationVersion</code></b></td></tr>
 *      <tr>
 *          <td>
 *             The version number of the application that this agent services, e.g. "2.0.1.11"
 *             @see #getApplicationVersion()
 *             @see #setApplicationVersion(String)
 *          </td>
 *          <td valign="top">null</td>
 *      </tr>
 *
 *
 *      <tr><td colspan="2"><b><code>adk.queue.disable</code></b></td></tr>
 *      <tr>
 *          <td>Disables the Agent Local Queue</td>
 *          <td valign="top">false</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.security.authenticationLevel</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Authentication level to use for all communications with this zone.
 *              This value is specified in the header of all SIF messages to
 *              direct the Zone Integration Server to protect sending of the
 *              agent's messages to another agent with a lower authentication level.
 *              @see #getAuthenticationLevel()
 *              @see #setAuthenticationLevel(int)
 *          </td>
 *          <td valign="top">0</td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>adk.security.encryptionLevel</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Encryption level to use for all communications with this zone.
 *              This value is specified in the header of all SIF messages to
 *              direct the Zone Integration Server to protect sending of the
 *              agent's messages to another agent with a lower encryption level.
 *              @see #getEncryptionLevel()
 *              @see #getEncryptionLevel()
 *          </td>
 *          <td valign="top">0</td>
 *      </tr>
 *
 *       <tr><td colspan="2"><b><code>adk.encryption.algorithm</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The default algorithm used for writing passwords
 *              @see #getDefaultEncryptionAlgorithm()
 *              @see #setDefaultEncryptionAlgorithm(String)
 *          </td>
 *          <td valign="top">&nbsp;</td>
 *      </tr>
 *
 *   <tr><td colspan="2"><b><code>adk.encryption.key</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The default key used for writing passwords.
 *              @see #getDefaultEncryptionKeyName()
 *              @see #setDefaultEncryptionKeyName(String)
 *          </td>
 *          <td valign="top">&nbsp;</td>
 *      </tr>
 *
 *   <tr><td colspan="2"><b><code>adk.encryption.keys.[KeyName]</code></b></td></tr>
 *      <tr>
 *          <td>
 *              The actual key to use for encryption or decryption where
 * 				"keyname" matches the @KeyName attribute of the Password
 * 				object
 * 				@see ADKProperties#getProperty(String)
 * 				@see ADKProperties#setProperty(String, String)
 *          </td>
 *          <td valign="top">&nbsp;</td>
 *      </tr>
 *  </table>
 *  <p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
@SuppressWarnings("restriction")
public class AgentProperties extends ADKProperties
{

	/**
	 *
	 */
	private static final long serialVersionUID = Element.CURRENT_SERIALIZE_VERSION;

	/**
	 *  <code>adk.defaultTimeout</code>
	 */
	public static final String PROP_DEFAULT_TIMEOUT = "adk.defaultTimeout";


	/**
	 *  The <code>adk.messaging.mode</code> property
	 */
	public static final String PROP_MESSAGING_MODE = "adk.messaging.mode";

	/**
	 *  The <code>adk.messaging.sleepOnDisconnect</code> property
	 */
	public static final String PROP_SLEEP_ON_DISC = "adk.messaging.sleepOnDisconnect";

	/**
	 *  The <code>adk.messaging.disableDispatcher</code> property
	 */
	public static final String PROP_DISABLE_DISPATCHER = "adk.messaging.disableDispatcher";

	/**
	 *  The <code>adk.messaging.transport</code> property
	 */
	public static final String PROP_MESSAGING_TRANSPORT = "adk.messaging.transport";

	/**
	 *  The <code>adk.messaging.maxBufferSize</code> property
	 */
	public static final String PROP_MAX_BUFFER_SIZE = "adk.messaging.maxBufferSize";

	/**
	 *  The <code>adk.messaging.effectiveBufferSize</code> property
	 */
	public static final String PROP_EFFECTIVE_BUFFER_SIZE = "adk.messaging.effectiveBufferSize";

	/**
	 *  <code>adk.messaging.pullFrequency</code>
	 */
	public static final String PROP_PULL_FREQUENCY = "adk.messaging.pullFrequency";

	/**
	 *  <code>adk.messaging.pullFrequency</code>
	 */
	public static final String PROP_PULL_DELAY_ON_ERROR = "adk.messaging.pullDelayOnError";

	/**
	 *  <code>adk.messaging.pullAckAck</code>
	 */
	public static final String PROP_PULL_ACKACK = "adk.messaging.pullAckAck";

	/**
	 *  <code>adk.messaging.strictVersioning</code>
	 */
	public static final String PROP_STRICT_VERSIONING = "adk.messaging.strictVersioning";

	/**
	 *  <code>adk.messaging.strictParsing</code>
	 */
	public static final String PROP_STRICT_TYPEPARSING = "adk.messaging.strictTypeParsing";

	/**
	 *  <code>adk.messaging.oneResponsePerPacket</code>
	 */
	public static final String PROP_ONEOBJECTPERRESPONSE = "adk.messaging.oneObjectPerResponse";

	/**
	 *  <code>adk.messaging.processEventsFromSelf</code>
	 */
	public static final String PROP_PROCESSEVENTSFROMSELF = "adk.messaging.processEventsFromSelf";

	/**
	 *  <code>adk.messaging.noRequestIndividualElements</code>
	 */
	public static final String PROP_NOREQUESTINDIVIDUALELEMENTS = "adk.messaging.noRequestIndividualElements";
	
	/**
	 * <code>adk.messaging.acceptEncoding</code>
	 */
	public static final String PROP_ACCEPTENCODING = "adk.messaging.acceptEncoding";
	
	/**
	 * <code>adk.messaging.compressionThreshold</code>
	 */
	public static final String PROP_COMPRESSIONTHRESHOLD = "adk.messaging.compressionThreshold";
	
	/**
	 * <code>adk.threading.corePoolSize</code>
	 */
	public static final String PROP_THREADINGCOREPOOLSIZE = "adk.threading.corePoolSize";
	
	/**
	 * <code>adk.threading.maximumPoolSize</code>
	 */
	public static final String PROP_THREADINGMAXIMUMPOOLSIZE = "adk.threading.maximumPoolSize";
	
	/**
	 * <code>adk.threading.keepAliveTime</code>
	 */
	public static final String PROP_THREADINGKEEPALIVETIME = "adk.threading.keepAliveTime";

	/**
	 * 	<code>adk.compatibility.useZoneStatusSystemControl</code>
	 */
	public static final String PROP_USE_ZONE_STATUS_SYSTEM_CONTROL = "adk.compatibility.useZoneStatusSystemControl";


	/**
	 *  <code>adk.provisioning.zisVersion</code>
	 */
	public static final String PROP_PROVISIONING_ZISVERSION = "adk.provisioning.zisVersion";

	/**
	 *  <code>adk.provisioning.overrideSifVersions</code>
	 */
	public static final String PROP_PROVISIONING_OVERRIDESIFVERSIONS = "adk.provisioning.overrideSifVersions";

	
	/**
	 * <code>adk.provisioning.overrideSifMessageVersionForSifRequests</code>
	 */
	public static final String PROP_PROVISIONING_OVERRIDE_REQUEST_VERSION = "adk.provisioning.overrideSifMessageVersionForSifRequests";
	
	
	/**
	 *  <code>adk.provisioning.batch</code>
	 */
	public static final String PROP_PROVISIONING_BATCH = "adk.provisioning.batch";

	/**
	 *  <code>adk.provisioning.legacy</code>
	 */
	public static final String PROP_PROVISIONING_LEGACY = "adk.provisioning.legacy";

	/**
	 *  <code>adk.provisioning.mode</code>
	 */
	public static final String PROP_PROVISIONING_MODE = "adk.provisioning.mode";

	/**
	 * <code>adk.provisioning.icon</code>
	 */
	public static final String PROP_PROVISIONING_ICON = "adk.provisioning.icon";

	/**
	 * <code>adk.provisioning.agentVendor</code>
	 */
	public static final String PROP_PROVISIONING_AGENT_VENDOR = "adk.provisioning.agentVendor";

	/**
	 * <code>adk.provisioning.agentVersion</code>
	 */
	public static final String PROP_PROVISIONING_AGENT_VERSION = "adk.provisioning.agentVersion";

	/**
	 * <code>adk.provisioning.applicationName</code>
	 */
	public static final String PROP_PROVISIONING_APP_NAME = "adk.provisioning.applicationName";

	/**
	 * <code>adk.provisioning.applicationVendor</code>
	 */
	public static final String PROP_PROVISIONING_APP_VENDOR = "adk.provisioning.applicationVendor";

	/**
	 * <code>adk.provisioning.applicationVersion</code>
	 */
	public static final String PROP_PROVISIONING_APP_VERSION = "adk.provisioning.applicationVersion";

	/**
	 * <code>adk.fileServerDirectory</code>
	 */
	public static final String PROP_FILESERVER_DIRECTORY = "adk.fileServerDirectory";

	/**
	 *  <code>adk.provisioning.ignoreErrors</code>
	 */
	public static final String PROP_IGNORE_PROVISIONING_ERRORS = "adk.provisioning.ignoreErrors";

	/**
	 *  <code>adk.keepMessageContent</code>
	 */
	public static final String PROP_KEEP_MESSAGE_CONTENT = "adk.keepMessageContent";

	/**
	 *  <code>pull-read-timeout</code>
	 */
	public static final String PROP_PULL_MODE_READ_TIMEOUT = "pull-read-timeout";
	
	/**
	 *  <code>pull-connect-timeout</code>
	 */
	public static final String PROP_PULL_MODE_CONNECT_TIMEOUT = "pull-connect-timeout";

	/**
	 * Default connect timeout for pull mode
	 */
	public static final int DEFAULT_PULL_CONNECT_TIMEOUT = 90 * 1000;
	
	/**
	 * Default connect timeout for pull mode
	 */	
	public static final int DEFAULT_PULL_READ_TIMEOUT = 60 * 1000;		
	
	/**
	 *  <code>adk.queue.disable</code>
	 */
	protected static final String PROP_QUEUE_DISABLE = "adk.queue.disable";

	/**
	 *  <code>adk.security.authenticationLevel</code>
	 */
	protected static final String PROP_AUTH_LEVEL = "adk.security.authenticationLevel";

	/**
	 *  <code>adk.security.encryptionLevel</code>
	 */
	protected static final String PROP_ENCRYPT_LEVEL = "adk.security.encryptionLevel";

	/**
	 *  <code>adk.encryption.algorithm</code>
	 */
	protected static final String PROP_ENCRYPT_ALGORITHM = "adk.encryption.algorithm";

	/**
	 *  <code>adk.encryption.key</code>
	 */
	protected static final String PROP_ENCRYPT_KEY = "adk.encryption.key";

	/**
	 *  <code>adk.encryption.keys.</code>
	 */
	protected static final String PROP_ENCRYPT_KEYS_BASE = "adk.encryption.keys.";



	/**
	 *  Constructor
	 */
	protected AgentProperties( Agent agent )
	{
		super(agent);
	}

	/**
	 *  Constructor
	 *  @param inherit The parent AgentProperties from which properties will be
	 *      inherited when not explicitly set on this object
	 */
	public AgentProperties( AgentProperties inherit )
	{
		super( inherit );
	}

	/**
	 *  Assigns default property values. Called by the constructor to import
	 *  the value of all System properties beginning with the
	 *  prefix <code>adk.</code>
	 */
	@Override
	protected void defaults( Object owner )
	{
		Properties sysprops = System.getProperties();

		//  Get all System properties that begin with "adk."
		for( Enumeration<Object> e = sysprops.keys(); e.hasMoreElements(); )
		{
			String k = (String)e.nextElement();

			if( k.startsWith("adk.") && !k.startsWith("adk.transport") )
			{
				String val = (String)sysprops.get(k);

//				if( ( ADK.debug & ADK.DBG_PROPERTIES ) != 0 )
				{
					if( owner == null )
						ADK.log.debug( "Using System property " + k + " = " + val );
					else
					if( owner instanceof ZoneImpl )
						((ZoneImpl)owner).log.debug( "Using System property " + k + " = " + val );
					else
					if( owner instanceof Agent )
						Agent.log.debug( "Using System property " + k + " = " + val );
				}

				put( k,val );
			}
		}
	}

	/**
	 *  Gets the preferred agent mode of operation (push or pull). The default
	 *  value is <code>PULL_MODE</code>.
	 *  @return Either <code>PUSH_MODE</code> or <code>PULL_MODE</code>
	 */
	public AgentMessagingMode getMessagingMode()
	{
		String s = getProperty( PROP_MESSAGING_MODE, "Pull" );

		if( s.equalsIgnoreCase("Push") ){
			return AgentMessagingMode.PUSH;
		}

		return AgentMessagingMode.PULL;
	}

	/**
	 *  Sets the preferred agent mode of operation (push or pull).
	 *  @param mode Either <code>PUSH_MODE</code> or <code>PULL_MODE</code>
	 */
	public void setMessagingMode( AgentMessagingMode mode )
	{
		setProperty( PROP_MESSAGING_MODE, mode == AgentMessagingMode.PUSH ? "Push":"Pull" );
	}

	/**
	 *  Determines if the ADK should send a SIF_Sleep message to a zone when
	 *  disconnecting. The default is true.<p>
	 *  @param sleep true if the ADK should place the agent's ZIS queue in sleep
	 *      mode when disconnecting.
	 */
	public void setSleepOnDisconnect( boolean sleep )
	{
		setProperty( PROP_SLEEP_ON_DISC, sleep );
	}

	/**
	 *  Determines if the ADK should send a SIF_Sleep message to a zone when
	 *  disconnecting. The default is true.<p>
	 *  @return true if the ADK should place the agent's ZIS queue in sleep
	 *      mode when disconnecting.
	 */
	public boolean getSleepOnDisconnect()
	{
		return getProperty( PROP_SLEEP_ON_DISC, true );
	}

	/**
	 *  Disables the ADK's message dispatcher. When disabled, all messages
	 *  received by the ADK are disposed of immediately without dispatching to
	 *  the agent's message handlers. This property should only be set true in
	 *  rare cases when the agent should send but not receive messages. The
	 *  default is false.<p>
	 *
	 *  @param disable true if the ADK should disable its message dispatcher
	 */
	public void setDisableMessageDispatcher( boolean disable )
	{
		setProperty( PROP_DISABLE_DISPATCHER, disable );
	}

	/**
	 *  Determines if the ADK's message dispatcher is disabled. When disabled,
	 *  all messages received by the ADK are disposed of immediately without
	 *  dispatching to the agent's message handlers. This property should only
	 *  be set true in rare cases when the agent should send but not receive
	 *  messages. The default is false.<p>
	 *
	 *  @return true if the ADK's has disabled its message dispatcher
	 */
	public boolean getDisableMessageDispatcher()
	{
		return getProperty( PROP_DISABLE_DISPATCHER, false );
	}

	/**
	 *  Determines if the ADK renders SIF_Response packets with one SIF Data
	 *  Object per packet regardless of the requestor's SIF_MaxBufferSize value.
	 *  By default, this property is disabled, causing the ADK to fit as many
	 *  SIF Data Objects per SIF_Response packet as possible.<p>
	 *
	 *  @return true if the ADK should render SIF_Response packets with one object per packet
	 */
	public boolean getOneObjectPerResponse()
	{
		return getProperty( PROP_ONEOBJECTPERRESPONSE, false );
	}

	/**
	 *  Determines if the ADK renders SIF_Response packets with one SIF Data
	 *  Object per packet regardless of the requestor's SIF_MaxBufferSize value.
	 *  By default, this property is disabled, causing the ADK to fit as many
	 *  SIF Data Objects per SIF_Response packet as possible.<p>
	 *
	 *  @param enable true if the ADK should render SIF_Response packets with
	 *      one object per packet
	 */
	public void setOneObjectPerResponse( boolean enable )
	{
		setProperty( PROP_ONEOBJECTPERRESPONSE, enable );
	}

	/**
	 *  A compatibility option that determines if the ADK renders SIF_Request
	 *  messages with SIF_Element field restrictions. When this property is
	 *  enabled, the class framework will not include SIF_Element elements in
	 *  SIF_Request messages even if you have called the Query.addFieldRestriction
	 *  method. This property can be used when requesting data from agents that
	 *  do not work well if SIF_Elements are present in the SIF_Request.<p>
	 *
	 *  @return true if the ADK should not include SIF_Element elements in
	 *      SIF_Request messages
	 */
	public boolean getNoRequestIndividualElements()
	{
		return getProperty( PROP_NOREQUESTINDIVIDUALELEMENTS, false );
	}

	/**
	 *  A compatibility option that determines if the ADK renders SIF_Request
	 *  messages with SIF_Element field restrictions. When this property is
	 *  enabled, the class framework will not include SIF_Element elements in
	 *  SIF_Request messages even if you have called the Query.addFieldRestriction
	 *  method. This property can be used when requesting data from agents that
	 *  do not work well if SIF_Elements are present in the SIF_Request.<p>
	 *
	 *  @param enable true if the ADK should not include SIF_Element elements in
	 *      SIF_Request messages
	 */
	public void setNoRequestIndividualElements( boolean enable )
	{
		setProperty( PROP_NOREQUESTINDIVIDUALELEMENTS, enable );
	}

	/**
	 *  Gets the maximum size of SIF messages that can be processed by this agent
	 *  when sending and receiving. The default value is 32K.
	 *  @return The maximum packet size in bytes
	 */
	public int getMaxBufferSize()
	{
		return getProperty( PROP_MAX_BUFFER_SIZE, 393216 );
	}

	/**
	 *  Sets the maximum size of packets that can be processed by this agent
	 *  when sending and receiving.
	 *  @param bytes The maximum packet size in bytes
	 */
	public void setMaxBufferSize( int bytes )
	{
		setProperty( PROP_MAX_BUFFER_SIZE,bytes );
	}

	/**
	 *  Gets the maximum size of SIF messages that can be processed by this agent
	 *  in-memory before off-loading the message to the local file system. Used
	 *  internally to influence memory management.
	 *  @return The effective buffer size (in bytes)
	 */
	public int getEffectiveBufferSize()
	{
		return getProperty( PROP_EFFECTIVE_BUFFER_SIZE, 32000 );
	}

	/**
	 *  Sets the maximum size of SIF messages that can be processed by this agent
	 *  in-memory before off-loading the message to the local file system. Used
	 *  internally to influence memory management.
	 *  @param bytes The maximum packet size in bytes
	 */
	public void setEffectiveBufferSize( int bytes )
	{
		setProperty( PROP_EFFECTIVE_BUFFER_SIZE,bytes );
	}

	/**
	 *  Gets the Pull frequency when the agent is registered in Pull mode.
	 *  By default, a Pull agent will query the ZIS for new messages every 30
	 *  seconds.
	 *
	 *  @return The number of milliseconds between Pull requests
	 */
	public int getPullFrequency()
	{
		return getProperty( PROP_PULL_FREQUENCY, 30000 );
	}

	/**
	 *  Sets the Pull frequency when the agent is registered in Pull mode.
	 *  @param ms the number of milliseconds between Pull requests
	 */
	public void setPullFrequency( int ms )
	{
		setProperty( PROP_PULL_FREQUENCY, ms );
	}

	/**
     *  For pull agents, gets the amount of time in milliseconds the agent will
	 *  delay when it encounters a transport error or disconnected zone when
	 *  attempting to pull the next message from its queue.
	 *
	 *  @return The pull delay (in milliseconds)
	 */
	public int getPullDelayOnError()
	{
		return getProperty( PROP_PULL_DELAY_ON_ERROR, 180000 );
	}

	/**
     *  For pull agents, sets the amount of time in milliseconds the agent will
	 *  delay when it encounters a transport error or disconnected zone when
	 *  attempting to pull the next message from its queue.
	 *
	 *  @param ms The pull delay (in milliseconds)
	 */
	public void setPullDelayOnError( int ms )
	{
		setProperty( PROP_PULL_DELAY_ON_ERROR, ms );
	}

	/**
	 * Used entirely for compatibility with non SIF compliant
	 * zone integration servers. If set to true, the ADK will
	 * send a SIF_Ack back to the ZIS when it has received
	 * a SIF_Ack from a Pull message
	 * @return True or false
	 */
	public boolean getPullAckAck() {
		return getProperty( PROP_PULL_ACKACK, false );
	}
	/**
	 * Used entirely for compatibility with non SIF compliant
	 * zone integration servers. If set to true, the ADK will
	 * send a SIF_Ack back to the ZIS when it has received
	 * a SIF_Ack from a Pull message
	 * @param enabled True if the non SIF compliant behavior
	 * should be used
	 */
	public void setPullAckAck( boolean enabled ) {
		setProperty( PROP_PULL_ACKACK, enabled );
	}
	
	/**
	 * Determines the actual value of the HTTP Accept-Encoding header used when 
	 * communicating with a zone integration server. By default, gzip is 
	 * specified as the preferred encoding, with identity at half priority,
	 * and all other encodings explicitly refused.
	 * @return a String appropriate for use as the value of the Accept-Encoding header in HTTP requests
	 */
	public String getAcceptEncoding() {
		return getProperty( PROP_ACCEPTENCODING, "gzip;q=1.0, identity;q=0.5, *;q=0" );
	}
	
	/**
	 * Determines the actual value of the HTTP Accept-Encoding header used when 
	 * communicating with a zone integration server. By default, gzip is 
	 * specified as the preferred encoding, with identity at half priority,
	 * and all other encodings explicitly refused.
	 * @param acceptEncoding a String appropriate for use as the value of the Accept-Encoding header in HTTP requests
	 */
	public void setAcceptEncoding(String acceptEncoding) {
		setProperty( PROP_ACCEPTENCODING, acceptEncoding );
	}
	
	/**
	 * Used to decide whether GZIP compression should be used when it's not mandatory.
	 * The default value is 64 kilobytes, and is based on thresholds observed when
	 * analyzing a variety of benchmarks of the GZIP implementation in Java.
	 * @return the size in bytes that a message must exceed to be compressed when compression is optional
	 */
	public int getCompressionThreshold() {
		return getProperty( PROP_COMPRESSIONTHRESHOLD, 65535);
	}
	
	/**
	 * Used to decide whether GZIP compression should be used when it's not mandatory.
	 * The default value is 64 kilobytes, and is based on thresholds observed when
	 * analyzing a variety of benchmarks of the GZIP implementation in Java.
	 * @param threshold the size in bytes that a message must exceed to be compressed when compression is optional
	 */
	public void setCompressionThreshold(int threshold) {
		setProperty( PROP_COMPRESSIONTHRESHOLD, threshold );
	}

	/**
	 * Determines whether the ADK returns an error message
	 * if it parses a SIF Version attribute representing a
	 * SIF Version that is not supported by the ADK
	 * @return True if an error should be returned for non-supported
	 * versions
	 */
	public boolean getStrictVersioning() {
		return getProperty( PROP_STRICT_VERSIONING, false );
	}
	/**
	 * Determines whether the ADK returns an error message
	 * if it parses a SIF Version attribute representing a
	 * SIF Version that is not supported by the ADK
	 * @param enabled True if an error should be returned for non-supported
	 * versions
	 */
	public void setStrictVersioning( boolean enabled ) {
		setProperty( PROP_STRICT_VERSIONING, enabled );
	}


	/**
	 * Determines whether the ADK returns an error message
	 * if it is unable to parse the value of a strongly-typed element
	 * or attribute (one that is defined as a numeric or date type)
	 * @return True if an error should be returned for unparseable
	 * elements or attributes
	 */
	public boolean getStrictTypeParsing() {
		return getProperty( PROP_STRICT_TYPEPARSING, false );
	}
	/**
	 * Determines whether the ADK returns an error message
	 * if it is unable to parse the value of a strongly-typed element
	 * or attribute (one that is defined as a numeric or date type)
	 * @param enabled True if an error should be returned for unparseable
	 * elements or attributes
	 */
	public void setStrictTypeParsing( boolean enabled ) {
		setProperty( PROP_STRICT_TYPEPARSING, enabled );
	}


	/**
	 *  Gets the latest version of the SIF Specification supported by the Zone
	 *  Integration Server to which the agent is connecting. This property
	 *  defaults to the latest version of the SIF Specification supported by the
	 *  ADK.
	 *  <p>
	 *
	 *  This property is used only for messages that are meant to be sent directly
	 *  to the ZIS, which include the provisioning messages (SIF_Register, SIF_Subscribe,
	 *  SIF_Provide, etc.), and the system messages (SIF_SystemControl).<p>
	 *
	 *  If the ZIS
	 *  supports SIF 1.1 or later, the ADK will send a SIF_Register with multiple
	 *  SIF_Version elements, one for each version of the SIF Specification
	 *  supported by the ADK. The first SIF_Version will be the version passed
	 *  to the <code>ADK.initialize</code> method. If the ZIS does not support
	 *  SIF 1.1 or later, the ADK will send a SIF_Register with a single
	 *  SIF_Version element where the value is equal to the version passed to
	 *  the <code>ADK.initialize</code> method.
	 *  <p>
	 *
	 *  To override behavior of messages that are sent to another agent (SIF_Event, SIF_Request),
	 *  the ADK supports a more granular approach for dealing with versions down to the object
	 *  level. To accomplish this, use ADK object versioning policy (see {@link PolicyFactory} )
	 *
	 *  @see ADKDefaultPolicy
	 *  @see PolicyFactory
	 *
	 *
	 *  @param version The latest version of the SIF Specification supported by
	 *      the Zone Integration Server (e.g. "1.1", "1.0r1", etc.)
	 */
	public void setZisVersion( String version )
	{
		setProperty( PROP_PROVISIONING_ZISVERSION, version );
	}

	/**
	 *  Gets the latest version of the SIF Specification supported by the Zone
	 *  Integration Server to which the agent is connecting.<p>
	 *
	 *  This property is used only for messages that are meant to be sent directly
	 *  to the ZIS, which include the provisioning messages (SIF_Register, SIF_Subscribe,
	 *  SIF_Provide, etc.), and the system messages (SIF_SystemControl).<p>
	 *
	 *  To override behavior of messages that are sent to another agent (SIF_Event, SIF_Request),
	 *  the ADK supports a more granular approach for dealing with versions down to the object
	 *  level. To accomplish this, use ADK object versioning policy (see {@link PolicyFactory} )
	 *
	 *  @see ADKDefaultPolicy
	 *  @see PolicyFactory
	 *
	 *  @return A SIF Version number (e.g. "1.1", "1.5r1", etc.)
	 */
	public String getZisVersion()
	{
		return getProperty( PROP_PROVISIONING_ZISVERSION, SIFVersion.LATEST.toString() );
	}
	
	
	
	
	
	/**
	 *  Overrides the way the ADK prepares the SIF_Message/Version element
	 *	for SIF_Requests to be the version specified by this property. This
	 *  override applies only to an Adk initialized to SIF Versions greater 
	 *  than 2.0.  Normally, the Adk causes this version to default to 2.0r1.
	 *  <p>
	 *
	 *  @param version A String representing the SIF Version to use for the 
	 *  SIF_Message/@Version property when that SIF_Message is a parent of 
	 *  a SIF_Request.
	 */
	public void setOverrideSifMessageVersionForSifRequests( String version )
	{
		setProperty( PROP_PROVISIONING_OVERRIDE_REQUEST_VERSION, version );
	}

	/**
	 *  Gets the value passed to the setOverrideSifMessageVersionForSifRequests
	 *  method, or null if that method has not been called.
	 * @return The SIFVersion that should be used in SIF_Message/@Version when
	 * it is a parent of a SIF_Request
	 */
	public String getOverrideSifMessageVersionForSifRequests()
	{
		return getProperty( PROP_PROVISIONING_OVERRIDE_REQUEST_VERSION, null );
	}
	

	/**
	 *  Overrides the versions of SIF supported by the agent when the class
	 *  framework sends a SIF_Register message to the zone.<p>
	 *
	 *  @param versions A comma-delimited list of SIF version or wildcard tags.
	 *      When connecting to a SIF 1.1 or later zone integration server, the
	 *      class framework will include a SIF_Version element for the version
	 *      of SIF used to initialize the ADK, followed by one additional
	 *      SIF_Version element for each version specified by this value.
	 */
	public void setOverrideSifVersions( String versions )
	{
		setProperty( PROP_PROVISIONING_OVERRIDESIFVERSIONS, versions );
	}

	/**
	 *  Gets the value passed to the setSifVersions method, or null if that
	 *  method has not been called.
	 * @return The list of SIF Versions that should be sent in a
	 * SIF_Register message (e.g. 2.1;2.0r1)
	 */
	public String getOverrideSifVersions()
	{
		return getProperty( PROP_PROVISIONING_OVERRIDESIFVERSIONS, null );
	}

	/**
	 *  Gets the provisioning mode for this zone
	 *  @return An <code>AgentProvisioningMode</code> value
	 */
	public AgentProvisioningMode getProvisioningMode()
	{
		String s = getProperty( PROP_PROVISIONING_MODE, "adk" );

		if( s.equalsIgnoreCase( "zis" ) )
			return AgentProvisioningMode.ZIS;
		if( s.equalsIgnoreCase( "agent" ) )
			return AgentProvisioningMode.AGENT;

		return AgentProvisioningMode.ADK;
	}

	/**
	 *  Sets the provisioning mode for this zone
	 *  @param mode The provisioning mode to use
	 */
	public void setProvisioningMode( AgentProvisioningMode mode )
	{
		switch( mode ) {
			case AGENT:
				setProperty( PROP_PROVISIONING_MODE, "agent" );
				break;
			case ADK:
				setProperty( PROP_PROVISIONING_MODE, "adk" );
				break;
			case ZIS:
				setProperty( PROP_PROVISIONING_MODE, "zis" );
				break;
			default:
				throw new IllegalArgumentException("Invalid provisioning mode ("+mode+")");
		}
	}


	/**
	 * Gets whether the ADK will provision the agent using the legacy SIF_Subscribe and
	 * SIF_Provide messages even if it is running in SIF 2.0.
	 *
	 * NOTE: The ADK will ALWAYs provision the agent in legacy mode if the ADK is initialized
	 * to SIF 1.5r1 or less or the {@link #getZisVersion()} property is set to SIF 1.5r1 or less.
	 *
	 * @return True if legacy provisioning should be used
	 */
	public boolean getProvisionInLegacyMode(){
		return getProperty( PROP_PROVISIONING_LEGACY, false );
	}

	/**
	 * Sets whether the ADK will use the legacy SIF_Subscribe and SIF_Provide messages
	 * to provision the agent, even if running in SIF 2.0.
	 * @param provLegacy True if legacy provisioining should be used
	 */
	public void setProvisionInLegacyMode( boolean provLegacy ){
		setProperty( PROP_PROVISIONING_LEGACY, provLegacy );
	}


	/**
	 *  Gets the name of the Transport Protocol to use for this agent or zone.<p>
	 *  @return The name of the Transport Protocol (defaults to <i>http</i>)
	 */
	public String getTransportProtocol()
	{
		return getProperty( PROP_MESSAGING_TRANSPORT, "http");
	}

	/**
	 *  Sets the name of the Transport Protocol used by this agent or zone.
	 *  @param protocol The name of a Transport Protocol supported by the ADK (currently
	 *      <i>http</i> or <i>https</i>)
	 */
	public void setTransportProtocol( String protocol )
	{
		String[] supported = ADK.getTransportProtocols();
		for( int i = 0; i < supported.length; i++ ) {
			if( supported[i].equalsIgnoreCase(protocol) ) {
				setProperty( PROP_MESSAGING_TRANSPORT,protocol );
				return;
			}
		}

		throw new IllegalArgumentException(protocol+" is not a supported protocol");
	}


	/**
	 * Determines whether the agent local queue is enabled
	 * @return True if the local queue is disabled
	 */
	public boolean getDisableQueue() {
		return getProperty( PROP_QUEUE_DISABLE, true );
	}
	/**
	 * Sets whether the agent local queue
	 * @param disable
	 */
	public void setDisableQueue( boolean disable ) {
		setProperty( PROP_QUEUE_DISABLE,disable );
	}
	/**
	 * Determines whether the ADK keeps the SIF XML message content in memory
	 * as a string during message processing
	 * @return True if the message should be kept in memory as a string
	 */
	public boolean getKeepMessageContent() {
		return getProperty( PROP_KEEP_MESSAGE_CONTENT,false );
	}
	/**
	 * Determines whether the ADK keeps the SIF XML message content in memory
	 * as a string during message processing
	 * @param keep True if the message should be kept in memory as a string
	 */
	public void setKeepMessageContent( boolean keep ) {
		setProperty( PROP_KEEP_MESSAGE_CONTENT, keep );
	}

	/**
	 * A compatibility option that determines if the ADK will throw
	 * exceptions when a SIF_Error is received by the ZIS during
	 * ADK-managed provisioning. This property should be enabled when
	 * connecting to the OpenSIF ZIS (0.9.x) because that it incorrectly
	 * treats attempt to re-provide or re-subscribe as errors instead of
	 * successful statuses.
	 * @return true if the ADK should ignore provisioning errors
	 */
	public boolean getIgnoreProvisioningErrors() {
		return getProperty( PROP_IGNORE_PROVISIONING_ERRORS, false );
	}
	/**
	 * A compatibility option that determines if the ADK will throw
	 * exceptions when a SIF_Error is received by the ZIS during
	 * ADK-managed provisioning. This property should be enabled when
	 * connecting to the OpenSIF ZIS (0.9.x) because that it incorrectly
	 * treats attempt to re-provide or re-subscribe as errors instead of
	 * successful statuses.
	 * @param ignore
	 */
	public void setIgnoreProvisioningErrors( boolean ignore ) {
		setProperty( PROP_IGNORE_PROVISIONING_ERRORS, ignore );
	}

	/**
	 * When this property is <code>true</code>
	 * and the ADK is initialized for SIF 1.5 or later, it will issue a
	 * synchronous SIF_SystemControl/SIF_GetZoneStatus message when the
	 * <code>Zone.getZoneStatus</code> method is called instead of
	 * using asynchronous SIF_Request messages. This mechanism of obtaining
	 * SIF_ZoneStatus is preferred over the traditional SIF_Request method,
	 * but is not officially supported in SIF as of 1.5. It is considered
	 * experimental. When this property is <code>false</code> (the default),
	 * the ADK issues SIF_Requests to obtain the SIF_ZoneStatus object.
	 * @param enabled
	 */
	public void setUseZoneStatusSystemControl( boolean enabled ) {
		setProperty( PROP_USE_ZONE_STATUS_SYSTEM_CONTROL, enabled );
	}
	/**
	 * When this property is <code>true</code>
	 * and the ADK is initialized for SIF 1.5 or later, it will issue a
	 *	synchronous SIF_SystemControl/SIF_GetZoneStatus message when the
	 *	<code>Zone.getZoneStatus</code> method is called instead of
	 *	using asynchronous SIF_Request messages. This mechanism of obtaining
	 *	SIF_ZoneStatus is preferred over the traditional SIF_Request method,
	 *	but is not officially supported in SIF versions prior 2.0.<p>
	 * 	When this property is <code>false</code> the ADK issues SIF_Requests
	 * to obtain the SIF_ZoneStatus object.
	 * @return True if the ADK should use a SIF_SystemControl message to
	 * retrieve SIF_ZoneStatus
	 */
	public boolean getUseZoneStatusSystemControl() {
		String overrideZISVersion = getProperty( PROP_PROVISIONING_ZISVERSION );
		SIFVersion calculatedVersion = ADK.getSIFVersion();
		if( overrideZISVersion != null )
		{
			try
			{
			calculatedVersion = SIFVersion.parse( overrideZISVersion );
			}
			catch( IllegalArgumentException iae ){
				ADK.log.warn( "Unable to parse property 'adk.provisioning.zisVersion'", iae );
				calculatedVersion = ADK.getSIFVersion();
			}
		}
		boolean useSystemControlDefault = calculatedVersion.compareTo( SIFVersion.SIF15r1 ) >= 0;
		return getProperty( PROP_USE_ZONE_STATUS_SYSTEM_CONTROL, useSystemControlDefault );
	}

	/**
	 * Gets the SIF_AuthenticationLevel the ADK should use for all
	 * outgoing messages
	 * @return The SIF_Authentication level value
	 */
	public int getAuthenticationLevel() {
		return getProperty( PROP_AUTH_LEVEL, 0 );
	}

	/**
	 * Sets the SIF_AuthenticationLevel the ADK should use for all
	 * outgoing messages
	 * @param level  The SIF_Authentication level value
	 */
	public void setAuthenticationLevel( int level ) {
		setProperty( PROP_AUTH_LEVEL, String.valueOf(level) );
	}

	/**
	 * Gets the SIF_EncryptionLevel the ADK should use for all
	 * outgoing messages
	 * @return The SIF_Encryption level value
	 */
	public int getEncryptionLevel() {
		return getProperty( PROP_ENCRYPT_LEVEL, 0 );
	}

	/**
	 * Sets the SIF_AuthenticationLevel the ADK should use for all
	 * outgoing messages
	 * @param level  The SIF_Authentication level value
	 */
	public void setEncryptionLevel( int level ) {
		setProperty( PROP_ENCRYPT_LEVEL, String.valueOf(level) );
	}

	/**
	 *  Gets the default timeout value used by the ADK for functions that
	 *  accept a timeout as a parameter.
	 *  @return A timeout value in milliseconds (defaults to 30000)
	 */
	public int getDefaultTimeout() {
		return getProperty( PROP_DEFAULT_TIMEOUT, 30000 );
	}

	/**
	 *  Sets the default timeout value used by the ADK for functions that
	 *  accept a timeout as a parameter.
	 * @param ms The timeout value in milliseconds
	 *
	 */
	public void setDefaultTimeout( int ms ) {
		setProperty( PROP_DEFAULT_TIMEOUT, ms );
	}

	/**
	 *  Determines how the ADK prepares SIF_Provide and SIF_Subscribe messages.
	 *
	 *  @return false if the ADK should send an individual SIF_Provide and
	 *      SIF_Subscribe message to the zone for each SIF Data Object. If any
	 *      of the messages fail with an Access Control error (Category 4),
	 *      the error is recorded as a warning and subsequently returned by the
	 *      <code>Zone.getConnectWarnings</code> method. All other SIF Errors
	 *      result in an exception thrown by the <code>Zone.connect</code> method.
	 *      When this property is set to true, SIF Data Objects are batched into
	 *      a single message that will be accepted or rejected as a group by the
	 *      zone integration server.
	 */
	public boolean isBatchProvisioning()
	{
		return getProperty( PROP_PROVISIONING_BATCH, false );
	}

	/**
	 *  Controls how the ADK prepares SIF_Provide and SIF_Subscribe messages.
	 *
	 *  @param batch false if the ADK should send an individual SIF_Provide and
	 *      SIF_Subscribe message to the zone for each SIF Data Object. If any
	 *      of the messages fail with an Access Control error (Category 4),
	 *      the error is recorded as a warning and subsequently returned by the
	 *      <code>Zone.getConnectWarnings</code> method. All other SIF Errors
	 *      result in an exception thrown by the <code>Zone.connect</code> method.
	 *      When this property is set to true, SIF Data Objects are batched into
	 *      a single message that will be accepted or rejected as a group by the
	 *      zone integration server.
	 */
	public void setBatchProvisioning( boolean batch )
	{
		setProperty( PROP_PROVISIONING_BATCH, batch );
	}

	/**
	 *  Determines if the ADK processes or ignores SIF_Event messages that were
	 *  reported by this agent (i.e. the SourceId of the SIF_Event matches the
	 *  SourceId of this agent). By default, such events are ignored by the
	 *  class framework and automatically acknowledged as successfully received.
     *
	 *  @return true if the class framework will process SIF_Event messages that
	 *      were reported by this agent; false if it will ignore them
	 */
	public boolean getProcessEventsFromSelf()
	{
		return getProperty( PROP_PROCESSEVENTSFROMSELF, false );
	}

	/**
	 *  Instructs the ADK to process or ignore SIF_Event messages that were reported
     *  by this agent (i.e. the SourceId of the SIF_Event matches the SourceId
	 *  of this agent). By default, such events are ignored by the class framework
	 *  and automatically acknowledged as successfully received.
     *
	 *  @param process true to process SIF_Event messages that were reported
	 *      by this agent; false to ignore them
	 */
	public void setProcessEventsFromSelf( boolean process )
	{
		setProperty( PROP_PROCESSEVENTSFROMSELF, process );
	}


	/**
	 * Determines the default encryption algorithm used for writing passwords
	 *
	 * @return Returns the default encryption algorithm
	 */
	public String getDefaultEncryptionAlgorithm()
	{
		return getProperty( PROP_ENCRYPT_ALGORITHM );
	}


	/**
	 * Instructs the ADK to use the specified password algorithm when writing
	 * passwords.
	 * @param algorithm The algorithm to use as the default from the
	 * PasswordAlgorithm enum ( e.g. "RC2" )
	 */
	public void setDefaultEncryptionAlgorithm( String algorithm )
	{
		setProperty( PROP_ENCRYPT_ALGORITHM, algorithm );
	}

	/**
	 * Retrieves the default encryption key name used for writing passwords.
	 * @return the encryption key name to use
	 */
	public String getDefaultEncryptionKeyName()
	{
			return getProperty( PROP_ENCRYPT_KEY );
	}

	/**
	 * Sets the default encryption key name used for writing passwords.
	 * @param keyName
	 */
	public void setDefaultEncryptionKeyName( String keyName )
	{
		setProperty( PROP_ENCRYPT_KEY, keyName );
	}


	/**
	 * Returns the encryption key with the specified name.
	 * @param keyName The name that the key is saved under in the properties
	 * @return The encryption key or null if not found
	 * @throws IOException
	 */
	public byte[] getEncryptionKey( String keyName )
		throws IOException
	{
		String key = getProperty( PROP_ENCRYPT_KEYS_BASE + keyName );
		if( key != null )
		{
		    BASE64Decoder decoder = new BASE64Decoder();
		    return decoder.decodeBuffer( key );

		}
		else
		{
			return null;
		}
	}

	/**
	 * Adds the encryption key with the specified name to the properties
	 * @param keyName The name to save the key under
	 * @param key The encryption key
	 * @throws IOException
	 */
	public void setEncryptionKey( String keyName, byte[] key )throws IOException
	{
	    BASE64Encoder encoder = new BASE64Encoder();
	    String encoded = encoder.encode( key );
		setProperty( PROP_ENCRYPT_KEYS_BASE + keyName, encoded );
	}

	/**
	 * Gets the URL to the agent's icon. The icon must meet the requirements for the SIF_Icon
	 * element. If this property is set, the agent will send a &lt;SIF_Icon&gt; element during
	 * agent registration
	 * @return the URL to the agent's ICON
	 */
	public String getAgentIconURL()
	{
		return getProperty( PROP_PROVISIONING_ICON, null );
	}


	/**
	 * Sets the URL to the agent's icon. The icon must meet the requirements for the SIF_Icon
	 * element. If this property is set, the agent will send a &lt;SIF_Icon&gt; element during
	 * agent registration
	 * @param URL The URL to the agent's icon
	 */
	public void setAgentIconURL(String URL)
	{
		setProperty( PROP_PROVISIONING_ICON, URL );
	}


	/**
	 * The name of the vendor who developed this SIF agent. If set, this information will
	 * sent to the ZIS during agent registration.
	 * @return the name of the vendor that developed this agent
	 */
	public String getAgentVendor()
	{
		return getProperty( PROP_PROVISIONING_AGENT_VENDOR, null );
	}


	/**
	 * The name of the vendor who developed this SIF agent. If set, this information will
	 * sent to the ZIS during agent registration.
	 * @param name The URL to the agent's icon
	 */
	public void setAgentVendor(String name)
	{
		setProperty( PROP_PROVISIONING_AGENT_VENDOR, name );
	}


	/**
	 * The version of this SIF Agent, e.g. "2.0.0.11"
	 * @return the version of this SIF Agent
	 */
	public String getAgentVersion()
	{
		return getProperty( PROP_PROVISIONING_AGENT_VERSION, null );
	}


	/**
	 * The version of this SIF Agent, e.g. "2.0.0.11"
	 * @param version the version of this SIF Agent
	 */
	public void setAgentVersion(String version)
	{
		setProperty( PROP_PROVISIONING_AGENT_VERSION, version );
	}

	/**
	 * The name of the application that this agent services.
	 * This information is available in SIF_Register and SIFZoneStatus
	 * @return the name of the application serviced by this agent
	 */
	public String getApplicationName()
	{
		return getProperty( PROP_PROVISIONING_APP_NAME, null );
	}


	/**
	 * The name of the application that this agent services.
	 * This information is available in SIF_Register and SIFZoneStatus
	 * @param name The name of the application
	 */
	public void setApplicationName(String name)
	{
		setProperty( PROP_PROVISIONING_APP_NAME, name );
	}


	/**
	 * The name of the vendor who developed the application serviced by this agent.
	 * If set, this information will sent to the ZIS during agent registration.
	 * @return the name of the vendor that developed the application serviced by this agent
	 */
	public String getApplicationVendor()
	{
		return getProperty( PROP_PROVISIONING_APP_VENDOR, null );
	}


	/**
	 * The name of the vendor who developed the application serviced by this agent.
	 * If set, this information will sent to the ZIS during agent registration.
	 * @param name The name of the vendor of the application serviced by this agent
	 */
	public void setApplicationVendor(String name)
	{
		setProperty( PROP_PROVISIONING_APP_VENDOR, name );
	}


	/**
	 * The version of the application serviced by this agent, e.g. "2.0.0.11"
	 * @return the version of the application serviced by this agent
	 */
	public String getApplicationVersion()
	{
		return getProperty( PROP_PROVISIONING_APP_VERSION, null );
	}


	/**
	 * The version of this the Application serviced by this agent, e.g. "2.0.0.11"
	 * @param version the version of the application serviced by this agent
	 */
	public void setApplicationVersion(String version)
	{
		setProperty( PROP_PROVISIONING_APP_VERSION, version );
	}
	
	/**
	 * Set the minimum number of threads to keep allocated in the ThreadPoolManager
	 * @param corePoolSize minimum number of threads to keep allocated in the ThreadPoolManager
	 */
	public void setThreadingCorePoolSize(int corePoolSize) {
		setProperty( PROP_THREADINGCOREPOOLSIZE, corePoolSize );
	}
	
	/**
	 * Get the current minimum number of thread to keep allocated in the ThreadPoolManager
	 * @return minimum number of thread to keep allocated in the ThreadPoolManager
	 */
	public int getThreadingCorePoolSize() {
		return getProperty( PROP_THREADINGCOREPOOLSIZE, 4);
	}
	
	/**
	 * Set the maximum number of threads that may be allocated by the ThreadPoolManager
	 * @param maximumPoolSize the maximum number of threads that may be allocated by the ThreadPoolManager
	 */
	public void setThreadingMaximumPoolSize(int maximumPoolSize) {
		setProperty(PROP_THREADINGMAXIMUMPOOLSIZE, maximumPoolSize);
	}
	
	/**
	 * Get the current maximum number of threads that may be allocated by the ThreadPoolManager
	 * @return the maximum number of threads that may be allocated by the ThreadPoolManager
	 */
	public int getThreadingMaximumPoolSize() {
		return getProperty( PROP_THREADINGMAXIMUMPOOLSIZE, Runtime.getRuntime().availableProcessors() * 4);
	}
	
	/**
	 * Set the time to wait before allowing threads allocated beyond the core pool in the TheadPoolManager to be released
	 * @param keepAliveTime the time to wait before allowing threads allocated beyond the core pool in the TheadPoolManager to be released
	 */
	public void setThreadingKeepAliveTime(long keepAliveTime) {
		setProperty(PROP_THREADINGKEEPALIVETIME, keepAliveTime);
	}
	
	/**
	 * Get the time to wait before allowing threads allocated beyond the core pool in the TheadPoolManager to be released
	 * @return the time to wait before allowing threads allocated beyond the core pool in the TheadPoolManager to be released
	 */
	public long getThreadingKeepAliveTime() {
		return getProperty( PROP_THREADINGKEEPALIVETIME, 1000L );
	}
}
