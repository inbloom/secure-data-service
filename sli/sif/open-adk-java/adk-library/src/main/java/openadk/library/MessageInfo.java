//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *	Provides information about an XML message, such as envelope and header attributes,
 *	the 	source of the message, and optionally the raw XML document that comprises
 *	the message. A concrete implementation of this interface is available for each 
 *	messaging protocol supported by the ADK. For instance, the {@link openadk.library.SIFMessageInfo}
 * 	class provides access to specific information regarding Schools Interoperability 
 * 	Framework <code>SIF_Message</code> envelopes and <code>SIF_Header</code> headers.<p>
 * 
 * 	<b>Using MessageInfo in Message Handlers</b><p>
 * 
 * 	A MessageInfo instance is passed as a parameter to message handlers such as 
 * 	<code>Publisher.onRequest</code> and <code>Subscriber.onEvent</code>.
 * 	Cast the parameter to a concrete type to obtain protocol-specific information
 * 	about the message. For example, the following shows how to obtain the value of 
 * 	the <code>SIF_SourceId</code> and <code>SIF_MsgId</code> elements from the
 * 	<code>SIF_Header</code>, as well as the version of SIF declared by the 
 * 	<code>SIF_Message</code> envelope:<p>
 * 
 *  <code>
 *  &nbsp;&nbsp;&nbsp;&nbsp;public void onEvent( Event event, Zone zone, MessageInfo info )<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Cast to SIFMessageInfo to get SIF-specific header info<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFMessageInfo inf = (SIFMessageInfo)info;<br/><br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Obtain SIF-specific header values<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String sourceId = inf.getSourceId();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String msgId = inf.getMsgId();<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFVersion version = inf.getSIFVersion();<br/><br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Display some information about this SIF_Event...<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println( "SIF_Event message with ID " + msgId + <br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" received from agent " + sourceId + <br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" in zone " + zone.getZoneId() + "."<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" This is a SIF " + version.toString() + " message." );<br/>
 *  <br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
 *  </code>
 *  </p>
 *
 * 	<b>Raw XML Message Content</b><p> 
 * 	When the <code>adk.keepMessageContent</code> agent property is enabled, the
 * 	{@link #getMessage} method will return the XML document that was received as a
 * 	single String. When this option is disabled (the default), the ADK does not keep 
 * 	a copy of raw message content in memory and will return a <code>null</code> value.<p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public interface MessageInfo
{
	/**
	 *  Gets the payload message type
	 *  @return A <code>MSGTYP_</code> constant (e.g. <code>SIFDTD.MSGTYPE_REQUEST</code>)
	 */
	public byte getPayloadType();

	/**
	 *  Gets the payload message element tag
	 *  @return The element tag of the message (e.g. "SIF_Request")
	 */
	public String getPayloadTag();

	/**
	 *  Gets the zone from which the message originated
	 */
	public Zone getZone();

	/**
	 *  Gets the value of an attribute. Implementations of this interface may
	 *  define attributes to hold values specific to the messaging protocol.<p>
	 * 	@param attr The name of the attribute
	 * 	@return The value of the attribute, or <code>null</code> if not defined
	 * 	@see #setAttribute
	 * 	@see #getAttributeNames()
	 */
	public String getAttribute( String attr );

	/**
	 *  Sets the value of an attribute. Implementations of this interface may
	 *  define attributes to hold values specific to the messaging protocol.<p>
	 * 	@param attr The attribute name
	 * 	@param value The attribute value
	 * 	@see #getAttributeNames
	 * 	@see #getAttribute
	 */
	public void setAttribute( String attr, String value );

	/**
	 *  Gets the names of all attributes. Implementations of this interface may
	 *  define attributes to hold values specific to the messaging protocol.<p>
	 * 	@return An array of the attribute names that can be individually queried
	 * 		by the <code>getAttribute</code> method
	 * 	@see #getAttribute
	 * 	@see #setAttribute
	 */
	public String[] getAttributeNames();

	/**
	 *  Gets the content of the raw XML message<p>
	 *  @return The raw XML message content as it was received by the ADK. If
	 *      the <code>adk.keepMessageContent</code> agent property is disabled (the 
	 * 		default), <code>null</code> is returned.
	 */
	public String getMessage();
}
