//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import openadk.library.impl.ADKUtils;
import openadk.library.infra.*;

import org.apache.log4j.Category;

/**
 *  A specialization of SIFElement for SIF infrastructure messages such as
 *  SIF_Register, SIF_Request, and SIF_Event.<p>
 *
 *  SIFMessagePayload provides methods specific to infrastructure messages, such
 *  as retrieving the SIF_Header or its individual fields. When an instance of
 *  this class is constructed, it is done so without its SIF_Header element. The
 *  <code>getHeader</code> method adds a SIF_Header child if one does not exist,
 *  and the class framework takes care of assigning values to the header prior to
 *  sending messages.
 *  <p>
 *
 *  For consistency the ADK employs the same SIFElement class hierarchy and
 *  conventions for SIF Infrastructure messages as it does for SIF Data Objects.
 *  Some inherited methods of SIFElement, such as setChanged and setEmpty, have
 *  no effect for infrastructure messages.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public abstract class SIFMessagePayload extends SIFElement
{
	/**
	 *  The namespace (e.g. "http://www.sifinfo.org/v1.0r1/message",
	 *  "http://www.sifinfo.org/infrastructure/1.x", etc)
	 */
	protected String fXmlns;

	/**
	 *  The version attribute. SIF 1.1 uses a separate version attribute to indicate
	 *  the version of SIF to which a message conforms; SIF 1.0 encodes the version
	 *  in the namespace. If SIF 1.1 or later is in use and this value is empty, it
	 *  is assumed to be the latest version of the SIF Specification.
	 */
	protected String fVersionAttr;


	/**
	 * Constructor
	 * @param metadata The ElementDef representing this object in the ADK metadata
	 */
    public SIFMessagePayload( ElementDef metadata )
	{
		super(metadata);
		setSIFVersion( ADK.getSIFVersion() );
	}

	/**
	 * Constructor
	 * @param version The SIFVersion to render this message in
	 * @param metadata The ElementDef representing this object in the ADK metadata
	 */
    public SIFMessagePayload( SIFVersion version, ElementDef metadata )
	{
		super(metadata);
		setSIFVersion( version );
	}

	/**
	 *  Returns the SIF_Header element. A new SIF_Header is created for this
	 *  object if one does not already exist.<p>
	 *  @return The infrastructure message header
	 */
	public SIF_Header getHeader()
	{
		SIF_Header hdr = (SIF_Header)getChild( InfraDTD.SIF_HEADER );
		if( hdr == null ) {
		    hdr = new SIF_Header();
			addChild(hdr);
		}
		return hdr;
	}

	/**
	 * Returns the XML namespace of this message
	 * @return The value that was set in the "xmlns" attribute of the message
	 */
	public String getXmlns()
	{
		return fXmlns;
	}

	/**
	 *  Sets the XML namespace of this message
	 * @param xmlns The value to set for the "xmlns" attribute.
	 */
	public void setXmlns( String xmlns )
	{
		fXmlns = xmlns;
	}

	/**
	 *  Returns the Version attribute of this SIF_Message.<p>
	 *
	 *  The Version attribute was introduced in SIF 1.1, which uses the attribute
	 *  to identify the version of SIF to which a message conforms. Prior
	 *  versions of SIF encode the version in the namespace.
	 *
	 *  @return The value of the Version attribute. If a null value is returned,
	 *      the caller should assume "1.1"
	 *
	 *  @since SIF 1.1
	 */
	public String getVersionAttribute()
	{
		return fVersionAttr;
	}

	/**
	 *  Sets the Version attribute of this SIF_Message.<p>
	 *
	 *  The Version attribute was introduced in SIF 1.1<p>
	 *
	 *  @param version The text for the Version attribute
	 *  @since SIF 1.1
	 */
	public void setVersionAttribute( String version )
	{
		fVersionAttr = version;
	}

	/**
	 *  Gets the SIF Version to which this message conforms.<p>
	 *
	 *  The SIF Version is determined by first inspecting the Version attribute
	 *  of the SIF_Message. If present, that attribute identifies the version of
	 *  SIF to which the version conforms. If not present, the namespace is
	 *  inspected; if it is in the form "http://www.sifinfo.org/v1.0r1/messages",
	 *  it is parsed to obtain the version of SIF. If it is in the form
	 *  "http://www.sifinfo.org/infrastructure/1.x" and no Version attribute is
	 *  present, the version of SIF is assumed to be "1.1"
	 *
	 *  @return A SIFVersion encapsulating the version of SIF to which the
	 *      message conforms, or null if the message does not provide a valid
	 *      xmlns or Version attribute from which to determine the SIF Version
	 */
	public SIFVersion getSIFVersion()
	{
		if( fVersionAttr != null )
		{
			//  SIF 1.1 or later
			return SIFVersion.parse( fVersionAttr );
		}
		else
		if( fXmlns != null )
		{
			return SIFVersion.parseXmlns( fXmlns );
		}

		return null;
	}

	/**
	 *  Sets the SIF Version to which this SIF_Message conforms.<p>
	 *
	 *  If the supplied version if SIF 1.1 or later, the Version attribute is
	 *  assigned a value and the xmlns attribute is set to "http://www.sifinfo.org/infrastructure/<i>major</i>.x".
	 *  Otherwise, the Version attribute is not set and the xmlns attribute is set
	 *  to "http://www.sifinfo.org/v<i>version</i>/messages"
	 *
	 *  @param A SIFVersion instance encapsulating the version of the SIF
	 *      Specification to which this message conforms
	 */
	public void setSIFVersion( SIFVersion ver )
	{
		fXmlns = ver.getXmlns();
		if( ver.compareTo( SIFVersion.SIF11 ) >= 0 ){
			fVersionAttr = ver.toString();
		}
		else {
			fVersionAttr = null;
		}

	}

	/**
	 *  Gets the SIF_MsgId value from this message's header.<p>
	 *
	 *  If the message does not have a SIF_Header element, one is created.<p>
	 *
	 *  @return The SIF_MsgId value
	 */
	public String getMsgId() {
		SIF_Header h = getHeader();
		return ( h != null ? h.getSIF_MsgId() : null );
	}

	/**
	 *  Gets the SIF_SourceId value from this message's header.<p>
	 *
	 *  If the message does not have a SIF_Header element, one is created.<p>
	 *
	 *  @return The SIF_SourceId value
	 */
	public String getSourceId()
	{
		SIF_Header h = getHeader();
		return ( h != null ? h.getSIF_SourceId() : null );
	}

	/**
	 *  Gets the SIF_DestinationId value from this message's header.<p>
	 *
	 *  If the message does not have a SIF_Header element, one is created.<p>
	 *
	 *  @return The SIF_DestinationId value
	 */
	public String getDestinationId()
	{
		SIF_Header h = getHeader();
		return ( h != null ? h.getSIF_DestinationId() : null );
	}

	/**
	 *  Gets the timestamp of this message from the SIF_Timestamp element in the header.<p>
	 *
	 *  If the message does not have a SIF_Header element, one is created with
	 *  a timestamp equal to the current time.<p>
	 *
	 *  @return The message timestamp
	 */
	public Calendar getTimestamp()
	{
		SIF_Header h = getHeader();
		Calendar c = null;
		if( h != null )
		{
			c = h.getSIF_Timestamp();
		}
		if( c == null ){
			c = Calendar.getInstance();
		}
		return c;
	}

	/**
	 * Gets the list of SIFContexts associated with this message.<p>
	 *
	 * @return The list of SIFContexts that are associated with this message
	 * @throws ADKException If one of the contexts in the message is not defined
	 * by this agent instance
	 */
	public List<SIFContext> getSIFContexts() throws ADKNotSupportedException
	{
		List<SIFContext> contexts = new ArrayList<SIFContext>();
		SIF_Contexts allContexts = getHeader().getSIF_Contexts();
		if( allContexts == null || allContexts.getChildCount() == 0 ){
			contexts.add( SIFContext.DEFAULT );
		} else {
			for( SIF_Context context : allContexts.getSIF_Contexts() ){
				SIFContext definedContext = SIFContext.isDefined( context.getValue() );
				if( definedContext == null ){
					throw new ADKNotSupportedException(
							"SIFContext \"" + context.getValue() +
							"\" is not supported"  );
				}
				contexts.add( definedContext );
			}
		}
		return contexts;

	}

	/**
	 *  Create an Immediate SIF_Ack for this message.<p>
	 *  @return A new SIF_Ack instance where the SIF_Status/SIF_Code value is
	 *      set to a value of "1" and SIF_Ack header values are derived from this
	 *      message's header values
	 */
	public SIF_Ack ackImmediate()
	{
		return ackStatus(1);
	}

	/**
	 *  Create an Intermediate SIF_Ack for this message.<p>
	 *  @return A new SIF_Ack instance where the SIF_Status/SIF_Code value is
	 *      set to a value of "2" and SIF_Ack header values are derived from this
	 *      message's header values
	 */
	public SIF_Ack ackIntermediate()
	{
		return ackStatus(2);
	}

	/**
	 *  Create a Final SIF_Ack for this message.<p>
	 *  @return A new SIF_Ack instance where the SIF_Status/SIF_Code value is
	 *      set to a value of "3" and SIF_Ack header values are derived from this
	 *      message's header values
	 */
	public SIF_Ack ackFinal()
	{
		return ackStatus(3);
	}

	/**
	 *  Create a SIF_Ack for this message.<p>
	 *  @param code The SIF_Status/SIF_Code value
	 *  @return A new SIF_Ack instance where the SIF_Status/SIF_Code value is
	 *      set to the specified value and SIF_Ack header values are derived
	 *      from this message's header values
	 */
	public SIF_Ack ackStatus( int code )
	{
	    SIF_Ack ack = new SIF_Ack();
		SIF_Status status = new SIF_Status( code );
		ack.setSIF_Status(status);
		ack.setSIF_OriginalMsgId(getMsgId());
		ack.setSIF_OriginalSourceId(getSourceId());

		SIFVersion msgVersion = getSIFVersion();

		if( code == 8 /* Receiver is sleeping */ ) {
			if( msgVersion.getMajor() == 1 ){
				// SIF 1.x used SIF_Data for text
				SIF_Data d = new SIF_Data();
				d.setTextValue("Receiver is sleeping");
				status.setSIF_Data( d );
			} else {
				status.setSIF_Desc( "Receiver is sleeping" );
			}
		}

		ack.message = this;

		//  Ack using the same version of SIF as this message
		ack.setSIFVersion( msgVersion );

		return ack;
	}

	/**
	 *  Create an error SIF_Ack for this message.<p>
	 *  @deprecated Please use the overload of this method that accepts a
	 *  	SIFErrorCategory as the first parameter
	 *  @param category The value of the SIF_Error/SIF_Category element
	 *  @param code The value of the SIF_Error/SIF_Code element
	 *  @param desc The value of the SIF_Error/SIF_Desc element
	 *  @return A new SIF_Ack instance with a SIF_Error element and SIF_Ack
	 *      header values derived from this message's header values
	 */
	public SIF_Ack ackError( int category, int code, String desc )
	{
		return ackError(category , code, desc, null );
	}

	/**
	 * Create an error SIF_Ack for this message.
	 * @param sifEx The SIFException that is the cause of the error
	 * @return
	 */
	public SIF_Ack ackError( SIFException sifEx ){
		SIF_Ack ack = new SIF_Ack();
		ack.message = this;

		ack.setSIF_OriginalMsgId(getMsgId());
		ack.setSIF_OriginalSourceId(getSourceId());

		SIF_Error error = new SIF_Error(
			sifEx.getSIFErrorCategory(),
			sifEx.getErrorCode(),
			sifEx.getErrorDesc(),
			sifEx.getErrorExtDesc() );

		ack.setSIF_Error( error );

		//  Ack using the same version of SIF as this message
		ack.setSIFVersion( getSIFVersion() );

		return ack;
	}



	/**
	 *  Create an error SIF_Ack for this message.<p>
	 *  @param category The value of the SIF_Error/SIF_Category element
	 *  @param code The value of the SIF_Error/SIF_Code element
	 *  @param desc The value of the SIF_Error/SIF_Desc element
	 *  @return A new SIF_Ack instance with a SIF_Error element and SIF_Ack
	 *      header values derived from this message's header values
	 */
	public SIF_Ack ackError( SIFErrorCategory category, int code, String desc )
	{
		return ackError(category,code,desc,null);
	}

	/**
	 *  Create an error SIF_Ack for this message.<p>
	 *  @deprecated Please use the overload of this method that accepts a SIFErrorCategory
	 *  		as the first parameter
	 *  @param category The value of the SIF_Error/SIF_Category element
	 *  @param code The value of the SIF_Error/SIF_Code element
	 *  @param desc The value of the SIF_Error/SIF_Desc element
	 *  @param extDesc The value of the SIF_Error/SIF_ExtendedDesc element
	 *  @return A new SIF_Ack instance with a SIF_Error element and SIF_Ack
	 *      header values derived from this message's header values
	 */
	public SIF_Ack ackError( int category, int code, String desc, String extDesc )
	{
		return ackError( SIFErrorCategory.lookup( category ), code, desc, extDesc );
	}

	/**
	 *  Create an error SIF_Ack for this message.<p>
	 *  @param category The value of the SIF_Error/SIF_Category element
	 *  @param code The value of the SIF_Error/SIF_Code element
	 *  @param desc The value of the SIF_Error/SIF_Desc element
	 *  @param extDesc The value of the SIF_Error/SIF_ExtendedDesc element
	 *  @return A new SIF_Ack instance with a SIF_Error element and SIF_Ack
	 *      header values derived from this message's header values
	 */
	public SIF_Ack ackError( SIFErrorCategory category, int code, String desc, String extDesc )
	{

		 SIF_Ack ack = new SIF_Ack();
			ack.setSIF_OriginalMsgId(getMsgId());
			ack.setSIF_OriginalSourceId(getSourceId());

			SIF_Error error = new SIF_Error(
					category,
					code,
					desc == null ? "" : desc, extDesc );

			ack.setSIF_Error( error );

			//  Ack using the same version of SIF as this message
			ack.setSIFVersion( getSIFVersion() );

			return ack;
	}

	/**
	 *  Utility method called by the class framework to log this SIF_Message
	 *  prior to sending it to a zone.<p>
	 *  @param log The Log4j Category instance representing the destination Zone
	 */
	public void LogSend( Category log )
	{
		if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
			log.debug("Send "+fElementDef.name());
		if( ( ADK.debug & ADK.DBG_MESSAGING_DETAILED ) != 0 ) {
			String id = getMsgId();
			log.debug("  MsgId: " + ( id == null ? "<none>" : id ) );
		}
	}

	/**
	 *  Utility method called by the class framework to log this SIF_Message
	 *  upon receipt from a zone.<p>
	 *  @param log The Log4j Category instance representing the source Zone
	 */
	public void LogRecv( Category log )
	{
		if( ( ADK.debug & ADK.DBG_MESSAGING ) != 0 )
			log.debug("Receive "+fElementDef.name());
		if( ( ADK.debug & ADK.DBG_MESSAGING_DETAILED ) != 0 ) {
			String id = getMsgId();
			log.debug("  MsgId: " + ( id == null ? "<none>" : id ) );
		}
	}
}
