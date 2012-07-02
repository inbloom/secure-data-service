//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;


import openadk.library.infra.*;

import org.xml.sax.InputSource;
import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 *  Encapsulates information about a <code>SIF_Message</code>, including its
 *  payload type, header fields, and raw XML message content.<p>
 *
 *  An instance of this class is passed to the <i>MessageInfo</i> parameter of
 *  all ADK message handlers like Subscriber.onEvent, Publisher.onQuery, and
 *  QueryResults.onQueryResults so that implementations of those methods can
 *  access header fields or XML content associated with an incoming message.
 *  Callers should cast the <i>MessageInfo</i> object to a SIFMessageInfo type
 *  in order to call the methods of this class that are specific to the Schools
 *  Interoperability Framework.
 *  <p>
 *
 *  Note that raw XML content is only retained if the "<code>adk.messaging.keepMessageContent</code>"
 *  agent property is enabled. Otherwise, the <code>getMessage</code> method
 *  returns a <code>null</code> value. Refer to the AgentProperties class for a
 *  description of all agent and zone properties.
 *  <p>
 *
 *  To use SIFMessageInfo, cast the <i>MessageInfo</i> parameter as shown below.
 *  <p>
 *
 *  <code>
 *  &nbsp;&nbsp;&nbsp;&nbsp;public void onEvent( Event event, Zone zone, MessageInfo info )<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFMessageInfo inf = (SIFMessageInfo)info;<br/>
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
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SIFMessageInfo implements MessageInfo
{
	// TODO: Determine if we should use a single HashMap or not.... For now,
	// splitting them out for clarity
	private HashMap<String, String> fAttr = new HashMap<String, String>();
	private HashMap<String, Object> fObjects = new HashMap<String, Object>();
	private SIFContext[] fContexts;
	protected Zone fZone;
	protected byte fPayload;
	protected String fMessage;
	// TODO: fHeader should be removed from this class for consistency in the APIs. All
	// header values should be stored in the fAttr Map
	protected SIF_Header fHeader;
	protected SIFVersion fPayloadVersion;

	/**
	 *  Called by the ADK to construct a SIFMessageInfo instance
	 */
    public SIFMessageInfo()
	{
		super();
	}

	/**
	 *  Called by the ADK to construct a SIFMessageInfo
	 *  @param msg The SIF_Message
	 *  @param zone The associated zone
	 */
	public SIFMessageInfo( SIFMessagePayload msg, Zone zone )
	{
		super();

		fZone = zone;
		fPayload = ADK.DTD().getElementType( msg.fElementDef.name() );
		if( zone.getProperties().getKeepMessageContent() )
		{
			try
			{
				StringWriter sw = new StringWriter();
	    		SIFWriter out = new SIFWriter( sw, zone );
		    	out.write( msg );
				out.flush();
				out.close();
				sw.close();

				fMessage = sw.getBuffer().toString();
			}
			catch( Exception ex ) {
			}
		}

		//  Set SIF_Header values
		fHeader = msg.getHeader();

		// Get the list of contexts for this message
		try
		{
			List<SIFContext> contexts = msg.getSIFContexts();
			fContexts = new SIFContext[ contexts.size() ];
			contexts.toArray( fContexts );
		}
		catch (ADKNotSupportedException contextNotSupported ){
			// TODO: Determine if we should be throwing here or what...
			throw new IllegalArgumentException(
					"One or more SIF Contexts are not supported: " + contextNotSupported.getMessage(), contextNotSupported );
		}

		//  Set information about the message payload
		fPayloadVersion = msg.getSIFVersion();
		// JEN SIF_ServiceInput
		if (msg instanceof SIF_ServiceInput) {
			SIF_ServiceInput req = (SIF_ServiceInput)msg;
			fObjects.put( "SIF_MaxBufferSize", req.getSIF_MaxBufferSize() );
			fObjects.put( "SIF_Service", req.getSIF_Service() );
			fObjects.put( "SIF_Operation", req.getSIF_Operation() );
			fObjects.put( "SIF_ServiceMsgId", req.getSIF_ServiceMsgId() );
			fObjects.put( "SIF_PacketNumber", req.getSIF_PacketNumber() );
			setAttribute( "SIF_MorePackets", req.getSIF_MorePackets() );
		}
		else if (msg instanceof SIF_ServiceOutput) {
			SIF_ServiceOutput req = (SIF_ServiceOutput)msg;
//			fObjects.put( "SIF_MaxBufferSize", req.getSIF_MaxBufferSize() );
			fObjects.put( "SIF_Service", req.getSIF_Service() );
			fObjects.put( "SIF_Operation", req.getSIF_Operation() );
			fObjects.put( "SIF_ServiceMsgId", req.getSIF_ServiceMsgId() );
			fObjects.put( "SIF_PacketNumber", req.getSIF_PacketNumber() );
			setAttribute( "SIF_MorePackets", req.getSIF_MorePackets() );
		}
		else if (msg instanceof SIF_ServiceNotify) {
			SIF_ServiceNotify req = (SIF_ServiceNotify)msg;
//			fObjects.put( "SIF_MaxBufferSize", req.getSIF_MaxBufferSize() );
			fObjects.put( "SIF_Service", req.getSIF_Service() );
			fObjects.put( "SIF_Operation", req.getSIF_Operation() );
			fObjects.put( "SIF_ServiceMsgId", req.getSIF_ServiceMsgId() );
			fObjects.put( "SIF_PacketNumber", req.getSIF_PacketNumber() );
			setAttribute( "SIF_MorePackets", req.getSIF_MorePackets() );
		}
		switch( fPayload )
		{
			case SIFDTD.MSGTYP_REQUEST:
			{
				SIF_Request req = (SIF_Request)msg;
				fObjects.put( "SIF_MaxBufferSize", req.getSIF_MaxBufferSize() );
				fObjects.put( "SIF_RequestVersions", req.parseRequestVersions( fZone.getLog() ) );
			}
			break;

			case SIFDTD.MSGTYP_RESPONSE:
			{
				SIF_Response rsp = (SIF_Response)msg;
				setSIFRequestMsgId( rsp.getSIF_RequestMsgId() );
				fObjects.put( "SIF_PacketNumber", rsp.getSIF_PacketNumber() );
				setAttribute( "SIF_MorePackets", rsp.getSIF_MorePackets() );
			}
			break;
		}
	}

	/**
	 * 	Assigns a SIF_Header to this message info. The SIF_Header element is usually
	 * 	extracted from the message by the constructor. When the default constructor
	 * 	is called, SIF_Header can be assigned separately by calling this method. Note
	 * 	this method is intended to be called internally by the class framework.<p>
	 *
	 * 	@param header a SIF_Header instance
	 *
	 * 	@see #getSIFHeader
	 */
	public void setSIFHeader( SIF_Header header )
	{
		// TODO: fHeader should be removed from this class for consistency in the APIs. All
		// header values should be stored in the fAttr Map
		fHeader = header;
	}

	/**
	 * 	Gets the SIF_Header encapsulated by this object.<p>
	 * 	@return The SIF_Header instance extracted from the message passed to
	 * 		the constructor or assigned via the <code>setSIFHeader</code> method.
	 * 	@see #setSIFHeader
	 */
	public SIF_Header getSIFHeader()
	{
		return fHeader;
	}

	/**
	 *  Gets the zone from which the message originated.<p>
	 * 	@return The Zone instance from which the message originated
	 */
	public Zone getZone() {
		return fZone;
	}

	/**
	 *  Gets the SIF payload message type.<p>
	 *  @return A <code>MSGTYP_</code> constant from the SIFDTD class
	 *      (e.g. <code>SIFDTD.MSGTYPE_REQUEST</code>)
	 */
	public byte getPayloadType() {
		return fPayload;
	}

	/**
	 *  Gets the SIF payload message element tag.<p>
	 *  @return The element tag of the message (e.g. "SIF_Request")
	 */
	public String getPayloadTag() {
		return ADK.DTD().getElementTag(fPayload);
	}

	/**
	 *  Gets the SIF_Message header timestamp.<p>
	 * 	@return The <code>SIF_Header/SIF_Date</code> and <code>SIF_Header/SIF_Time</code>
	 * 		element values as a Date instance, identifying the time and date the
	 * 		message was sent
	 */
	public Calendar getTimestamp() {
		return fHeader.getSIF_Timestamp();
	}


	/**
	 *  Gets the value of the <code>SIF_MsgId</code> header element
	 * 	@return The value of the <code>SIF_Header/SIF_MsgId</code> element, the
	 * 		unique GUID assigned to the message by its sender
	 */
	public String getMsgId() {
		return fHeader.getSIF_MsgId();
	}

	/**
	 *  Gets the value of the <code>SIF_SourceId</code> header element<p>
	 * 	@return The value of the <code>SIF_Header/SIF_SourceId</code> element,
	 * 		which identifies the agent that originated the message
	 */
	public String getSourceId() {
		return fHeader.getSIF_SourceId();
	}

	/**
	 *  Gets the value of the <code>SIF_DestinationId</code> header element<p>
	 * 	@return The value of the optional <code>SIF_Header/SIF_SourceId</code> element.
	 * 		When present, it identifies the agent to which the message should be routed
	 * 		by the zone integration server.
	 */
	public String getDestinationId() {
		return fHeader.getSIF_DestinationId();
	}

	/**
	 *  Gets the value of the optional <code>SIF_Security/SIF_SecureChannel/SIF_AuthenticationLevel</code> header element<p>
	 *  @return The authentication level or zero if not specified
	 */
	public int getAuthenticationLevel() {
		try {
			return Integer.parseInt( fHeader.getSIF_Security().getSIF_SecureChannel().getSIF_AuthenticationLevel() );
		} catch( Throwable thr ) {
			return 0;
		}
	}

	/**
	 *  Gets the value of the optional <code>SIF_Security/SIF_SecureChannel/SIF_EncryptionLevel</code> header element<p>
	 *  @return The encryption level or zero if not specified
	 */
	public int getEncryptionLevel() {
		try {
			return Integer.parseInt( fHeader.getSIF_Security().getSIF_SecureChannel().getSIF_EncryptionLevel() );
		} catch( Throwable thr ) {
			return 0;
		}
	}

	/**
	 *  Gets the content of the raw XML message<p>
	 *  @return The raw XML message content as it was received by the ADK. If
	 *      the <code>adk.keepMessageContent</code> agent or zone property has
	 *      a value of "false" (the default), null is returned.
	 */
	public String getMessage() {
		return fMessage;
	}

	/**
	 *  Gets the version of SIF associated with the message. The version is
	 *  determined by inspecting the <i>xmlns</i> attribute of the SIF_Message
	 *  envelope.<p>
	 *
	 *  @return A SIFVersion object identifying the version of SIF associated
	 *      with the message
	 *
	 *  @see #getLatestSIFRequestVersion()
	 */
	public SIFVersion getSIFVersion() {
	    return fPayloadVersion;
	}

	/**
	 *  For SIF_Response messages, sets the SIF_MsgId of the associated SIF_Request
	 * 	message. This method is called internally by the class framework.<p>
	 *  @param msgId The value to assign to the <code>SIF_Header/SIF_RequestMsgId</code>
	 * 		element
	 */
	public void setSIFRequestMsgId( String msgId ) {
		setAttribute( "SIF_RequestMsgId", msgId );
	}

	/**
	 *  For SIF_Response messages, gets the SIF_MsgId of the associated SIF_Request.<p>
	 *  @return The value of the <code>SIF_Header/SIF_RequestMsgId</code> element, or
	 *      null if the message encapsulated by this SIFMessageInfo instance is not a
	 * 		SIF_Response message
	 */
	public String getSIFRequestMsgId() {
		return (String)fAttr.get("SIF_RequestMsgId");
	}

	/**
	 *  For SIF_Request messages, sets the SIF version that responses should conform to.
	 * 	This method is called internally by the class framework.<p>
	 *  @param versions One or more SIFVersion instances identifying the version of the specification
	 * 		that SIF_Responses should conform to.
	 *  @see #getSIFVersion
	 */
	public void setSIFRequestVersion( SIFVersion... versions ) {
	    fObjects.put("SIF_RequestVersions", versions );
	}

	/**
	 *  For SIF_Request messages, gets the SIF versions responses should conform to.<p>
	 *  @return The value of the <code>SIF_Request/SIF_Version</code> element or
	 * 		null if the message is not a SIF_Request message
	 */
	public SIFVersion[] getSIFRequestVersions()
	{
		return (SIFVersion[]) fObjects.get("SIF_RequestVersions");
	}

	/**
	 *  For SIF_Request messages, gets the latest SIF version
	 *  that was requested by the requestor and is supported by the ADK
	 *  @return The value of the <code>SIF_Request/SIF_Version</code> element or
	 * 		null if the message is not a SIF_Request message
	 */
	public SIFVersion getLatestSIFRequestVersion()
	{
		return ADK.getLatestSupportedVersion( getSIFRequestVersions() );
	}



	/**
	 *  For SIF_Responze messages, returns information about the original SIF_Request,
	 *  including any custom user data that was assigned to the
	 *  {@link Query#setUserData(Serializable)} member.<p>
	 *  @param requestInfo a RequestInfo object representing the SIF_Request
	 *  @see #getSIFRequestInfo()
	 */
	public void setSIFRequestInfo( RequestInfo requestInfo ) {
	    fObjects.put("SIFRequestInfo", requestInfo );
	}

	/**
	 *  For SIF_Responze messages, returns information about the original SIF_Request,
	 *  including any custom user data that was assigned to the
	 *  {@link Query#setUserData(Serializable)} member.<p>
	 *  @return a RequestInfo object representing the request.
	 *  @see #setSIFRequestInfo(RequestInfo)
	 */
	public RequestInfo getSIFRequestInfo()
	{
		return (RequestInfo)fObjects.get( "SIFRequestInfo" );
	}


	/**
	 *  For SIF_Request messages, identifies the type of object requested<p>
	 *  @param objType An ElementDef constant from the SIFDTD class
	 */
	public void setSIFRequestObjectType( ElementDef objType ) {
		fObjects.put( "SIF_RequestObjectType", objType );
	}

	/**
	 *  For SIF_Request messages, identifies the type of object requested<p>
	 *  @return An ElementDef constant from the SIFDTD class
	 */
	public ElementDef getSIFRequestObjectType() {
		return (ElementDef)fObjects.get( "SIF_RequestObjectType" );
	}


	/**
	 *  For SIF_Response messages, gets the packet number<p>
	 *  @return The Integer value of the <code>SIF_Response/SIF_PacketNumber</code>
	 *      element or null if the message is not a SIF_Response message
	 */
	public Integer getPacketNumber() {
		return (Integer)fObjects.get("SIF_PacketNumber");
	}

	/**
	 *  For SIF_Response messages, determines if more packets are to be expected<p>
	 *  @return The string value of the <code>SIF_Response/SIF_MorePackets</code>
	 *      element or null if the message is not a SIF_Response message or the
	 *      element is missing.
	 */
	public Boolean getMorePackets() {
		String s = (String)fAttr.get("SIF_MorePackets");
		return s == null ? null : new Boolean( s.equalsIgnoreCase("yes") );
	}

	/**
	 *  For SIF_Request messages, gets the maximum packet size of result packets
	 *  @return The value of the <code>SIF_Request/SIF_MaxBufferSize</code>
	 *      element or zero if the message is not a SIF_Request message or the
	 *      buffer size could not be converted to an integer
	 */
	public int getMaxBufferSize()
	{
		Integer s = (Integer)fObjects.get("SIF_MaxBufferSize");
		try {
			return s.intValue();
		} catch( Throwable thr ) {
			return 0;
		}
	}

	public String getAttribute( String attr ) {
		return (String)fAttr.get(attr);
	}
	public void setAttribute( String attr, String value ) {
		fAttr.put(attr,value);
	}
	public String[] getAttributeNames() {
		return null;
	}


	/**
	 *  Preparses a raw SIF_Message to extract its header information and payload
	 *  element type. Applications that do not wish to fully parse a message but
	 *  need to know its header information can call this method and defer full
	 *  parsing to a later stage. When <code>keepContent</code> is true, the
	 *  message content is preserved as-is in the returned SIFMessageInfo object
	 *  and can be retrieved by calling getMessage on that object.
	 *  <p>
	 *
	 *  For SIF_Ack messages, the value of <code>&lt;SIF_OriginalMsgId&gt;</code>
	 *  and <code>&lt;SIF_OriginalSourceId&gt;</code> are parsed and stored in
	 *  the SIFMessageInfo object. These are the only non-header elements that
	 *  are parsed.
	 *  <p>
	 *
	 * @param is The source of the SIF_Message to preparse
	 * @param keepMessage If true, the string representation of the message is kept in memory
	 * @param zone The zone from which this message was received
	 * @return A SIFMessageInfo object containing information about the message
	 * @throws IOException
	 * @throws ADKMessagingException
	 */
	public static SIFMessageInfo parse( InputSource is, boolean keepMessage, Zone zone )
		throws IOException, ADKMessagingException
	{
		BufferedReader in = null;
		try
		{
			String file = is.getSystemId();
			if( file == null ){
				throw new ADKMessagingException("InputSource has no data to parse",zone);
			}

			in = new BufferedReader( new FileReader(file) );
			return parse( in, keepMessage, zone );
		}
		finally
		{
			if( in != null )
				in.close();
		}
	}


	/**
	 *  Preparses a raw SIF_Message to extract its header information and payload
	 *  element type. Applications that do not wish to fully parse a message but
	 *  need to know its header information can call this method and defer full
	 *  parsing to a later stage. When <code>keepContent</code> is true, the
	 *  message content is preserved as-is in the returned SIFMessageInfo object
	 *  and can be retrieved by calling getMessage on that object.
	 *  <p>
	 *
	 *  For SIF_Ack messages, the value of <code>&lt;SIF_OriginalMsgId&gt;</code>
	 *  and <code>&lt;SIF_OriginalSourceId&gt;</code> are parsed and stored in
	 *  the SIFMessageInfo object. These are the only non-header elements that
	 *  are parsed.
	 *  <p>
	 *
	 * @param in The source of the SIF_Message to preparse. The caller is responsible for closing the Reader
	 * @param keepMessage If true, the string representation of the message is kept in memory
	 * @param zone The zone from which this message was received
	 * @return A SIFMessageInfo object containing information about the message
	 * @throws IOException
	 * @throws ADKMessagingException
	 */
	public static SIFMessageInfo parse( Reader in, boolean keepMessage, Zone zone )
		throws IOException, ADKMessagingException
	{
		StringWriter out = null;

		try
		{	if( keepMessage )
				out = new StringWriter();

		    //  Header info, payload type, and full message if desired
			SIFMessageInfo inf = new SIFMessageInfo();
			inf.fZone = zone;

			int ch = -1;
			int elements = 0;
			int bytes = 0;
			boolean inTag = false;
			boolean inHeader = false;
			boolean storValue = false;
			boolean ack = false, response = false;

			//  Buffer for retaining message content
			char[] buf = keepMessage ? new char[1024] : null;

			//  Tag buffer size is 16*2, enough for largest SIF10r1 element name
			StringBuffer tag = new StringBuffer(16*2);
			//  Value buffer size is 16*3, enough for GUID
			StringBuffer value = new StringBuffer(16*3);

			//  SIF 1.0r1 Optimization: as soon as we parse the SIF_Header and
			//  optionally the SIF_OriginalMsgId and SIF_OriginalSourceId elements
			//  of a SIF_Ack we're done. So continue for as long as
			//  required_elements != 3.
			//
			int required_elements = 0;

			while( in.ready() && required_elements != 3 )
			{
				ch = in.read();

				if( keepMessage ) {
					buf[bytes++] = (char)ch;
					if( bytes == buf.length-1 ) {
						out.write(buf,0,bytes);
						bytes=0;
					}
				}

				if( ch == '<' ) {
					inTag = true;
					storValue = false;
				}
				else
				if( ch == ' ' && inTag ) {
					inTag = false;
					storValue = true; // attributes follow
				}
				else
				if( ch == '>' )
				{
					if( storValue )
						System.out.println("Attributes: "+value.toString());

					//  We now have text of next element
					switch( elements )
					{
						case 0:
							//  Ensure first element is <SIF_Message>
							if( !tag.toString().equals("SIF_Message") ){
								throw new ADKMessagingException("Message does not begin with SIF_Message",zone);
							}
							break;

						case 1:
						    //
							//  Payload element (e.g. "SIF_Ack", "SIF_Register", etc.)
							//  Ask the DTD object for a type code for this message, store
							//  it as the payload type in SIFMessageInfo. If zero, it means
							//  the element is not recognized as a valid payload type for
							//  this version of SIF.
							//
							inf.fPayload = ADK.DTD().getElementType(tag.toString());
							if( inf.fPayload == 0 ){
								throw new ADKMessagingException("<"+tag.toString()+"> is not a valid payload message",zone);
							}

							//  Is this a SIF_Ack or SIF_Response?
							ack = ( inf.fPayload == SIFDTD.MSGTYP_ACK );
							response = ack ? false : ( inf.fPayload == SIFDTD.MSGTYP_RESPONSE );
							if( !ack && !response )
								required_elements += 2;
							else
							if( response )
								required_elements += 1;

							break;

						default:
							String s = tag.toString();
							if( inHeader )
							{
								//  End of a header element, or </SIF_Header>...
								if( s.charAt(0) == '/' )
								{
	    							if( s.equals("/SIF_Header") ) {
		    							inHeader = false;
										required_elements++;
				    				} else
									if( !( s.startsWith("/SIF_Sec") ) ) {
					    			    inf.setAttribute(s.substring(1),value.toString());
									}
								}
								else
									storValue=true;
							}
							else
							if( !inHeader )
							{
								if( s.equals("SIF_Header") )
								{
	    							//  Begin <SIF_Header>
									// TODO: This class maintains SIF_Header information in the fHeader
									// variable. This particular parsing mechanism doesn't re-create a SIF_Header
									// element. Therefore if the parse method is used, the only way to get these
									// properties back out is to use the getAttribute() call.
		    						inHeader = true;
			    				}
				    			else
					    		if( ack )
								{
	    							//  SIF_Ack / SIF_OriginalSourceId or SIF_OriginalMsgId
		    						if( s.startsWith("SIF_Orig") )
			    						storValue = true;
				    				else
					    			if( s.startsWith("/SIF_Orig") ) {
						    			required_elements++;
							    		inf.setAttribute(s.substring(1),value.toString());
								    }
								}
								else
								if( response )
								{
	    							//  SIF_Response / SIF_RequestMsgId
		    						if( s.startsWith("SIF_Req") )
			    						storValue = true;
				    				else
					    			if( s.startsWith("/SIF_Req") ) {
						    			required_elements++;
							    		inf.setAttribute(s.substring(1),value.toString());
								    }
								}
							}

							value.setLength(0);
							break;
					}

					inTag=false;
				    tag.setLength(0);
					elements++;
				}
				else
				{
					if( inTag )
	    				tag.append((char)ch);
					else
					if( storValue )
						value.append((char)ch);
				}
			}

			if( out != null )
			{
				//  Read the remainder of the input stream and copy it to the
				//  output buffer
				if( bytes > 0 )
					out.write(buf,0,bytes);
				while( in.ready() ) {
					bytes = in.read(buf,0,buf.length-1);
					out.write(buf,0,bytes);
				}

				//  Store message content
				out.flush();
				inf.fMessage = out.getBuffer().toString();
		    }

			return inf;
		}
		finally
		{
			if( out != null ) {
				out.flush();
				out.close();
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fAttr.toString();
	}


	/**
	 * Gets the SIF Contexts that this message applies to
	 * @return the SIF Contexts that this message applies to
	 */
	public SIFContext[] getSIFContexts() {
		return fContexts;
	}

	
/*
 * TODO:
 * These added by Eric P on 3/2/09 in the initial 2.3.0 branch so that 
 * ETF will continue to work. The use of these methods needs to be evaluated; 
 * for example, some of these values should probably be assigned in the 
 * constructor like they are for other SIF message types, but because the 
 * SIF_ServiceXxx messages are not yet part of the ADK, that's not possible 
 * at the moment. So, functions like setSIFServiceMsgId are being called 
 * from elsewhere in the ADK code (search for that method to find where)...
 */	
	public String getSIFServiceMsgId() {
		return getAttribute("SIF_ServiceMsgId");
	}
	public void setSIFServiceMsgId( String msgId ) {
		setAttribute( "SIF_ServiceMsgId", msgId );
	}
	public String getSIFServiceName() {
		return getAttribute("SIF_ServiceName");
	}
	public String getSIFServiceMethod() {
		return getAttribute("SIF_ServiceMethod");
	}

	public HashMap<String, Object> getObjects() {
		return fObjects;
	}
}

