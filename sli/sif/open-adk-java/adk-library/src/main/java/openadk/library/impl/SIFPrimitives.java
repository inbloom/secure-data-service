//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;

import openadk.library.*;
import openadk.library.common.XMLData;
import openadk.library.common.YesNo;
import openadk.library.impl.ISIFPrimitives;
import openadk.library.infra.*;
import openadk.library.services.ServiceRequestInfo;
import openadk.util.ADKStringUtils;

import org.xml.sax.SAXException;

/**
 *  Default implementation of the ISIFPrimitives interface.
 *
 *
 */
public class SIFPrimitives implements ISIFPrimitives
{
	/**
	 *  SIF_Register
	 */

	public SIF_Ack sifRegister( Zone zone )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		AgentProperties props = zone.getProperties();
		SIFVersion effectiveZISVersion = adkZone.getHighestEffectiveZISVersion();

		SIF_Register msg = new SIF_Register( effectiveZISVersion );
		msg.setSIF_MaxBufferSize( props.getMaxBufferSize() );
		msg.setSIF_Mode( props.getMessagingMode() == AgentMessagingMode.PULL ? "Pull" : "Push" );

		// Set the agent's name and version
		Agent agent = zone.getAgent();
		msg.setSIF_Name( agent.getName() );

		String vendor = props.getAgentVendor();
		if( vendor != null ){
			msg.setSIF_NodeVendor( vendor );
		}

		String version = props.getAgentVersion();
		if( version != null ){
			msg.setSIF_NodeVersion( version );
		}


		SIF_Application applicationInfo = new SIF_Application();
		String appName = props.getApplicationName();
		if( appName != null ){
			applicationInfo.setSIF_Product( appName );
		}
		String appVersion = props.getApplicationVersion();
		if( appVersion != null ){
			applicationInfo.setSIF_Version( appVersion );
		}
		String appVendor = props.getApplicationVendor();
		if( appVendor != null ){
			applicationInfo.setSIF_Vendor( appVendor );
		}

		if( applicationInfo.getFieldCount() > 0 ){
			// All three fields under SIF_Application are required by the
			// SIF_Specification. Determine if any are missing. If so,
			// create the field with an empty value
			if( applicationInfo.getSIF_Product() == null ){
				applicationInfo.setSIF_Product( "" );
			}
			if( applicationInfo.getSIF_Version() == null ){
				applicationInfo.setSIF_Version( "" );
			}
			if( applicationInfo.getSIF_Vendor() == null ){
				applicationInfo.setSIF_Vendor( "" );
			}
			msg.setSIF_Application( applicationInfo );
		}


		String propVal = props.getAgentIconURL();
		if( propVal != null ){
			msg.setSIF_Icon( propVal );
		}

		//
		//  SIF_Version handling:
		//
		//  * If the "adk.provisioning.zisVersion" property is set to > SIF 1.1
		//    (the default), use SIF 1.1+ registration where multiple SIF_Version
		//    elements are included in the SIF_Register message. Otherwise use
		//	  SIF 1.0 registration where only a single SIF_Version is included in
		//	  the SIF_Register message.
		//
		//  For SIF 1.1 registrations:
		//
		//  * If the "adk.sifRegister.sifVersions" System property is set,
		//    enumerate its comma-delimited list of SIF_Version values and use
		//    those instead of building a list. This is primarily used for
		//    testing wildcards (which the ADK doesn't normally use) or when an
		//    agent wants to connect to a ZIS where wildcarding works better for
		//    some reason.
		//
		//  * Otherwise, build a list of SIF_Versions: Set the first SIF_Version
		//    element to the version initialized by the ADK, then add a SIF_Version
		//    element for each additional version of SIF supported by the ADK
		//

			String forced = zone.getProperties().getOverrideSifVersions();
			if( forced != null )
			{
				((ZoneImpl)zone).log.debug("Using custom SIF_Register/SIF_Version: " + forced );
				StringTokenizer tok = new StringTokenizer( forced, "," );
				while( tok.hasMoreTokens() )
				{
					msg.addSIF_Version( new SIF_Version( tok.nextToken().trim() ) );
				}
			}
			else
			{
				SIFVersion zisVer = SIFVersion.parse( zone.getProperties().getZisVersion() );

				if( zisVer.compareTo( SIFVersion.SIF11 ) >= 0 )
				{
					// Add the ADK version first. This is the "default"
					// agent version, which has special meaning to the
					// ZIS
					msg.addSIF_Version( new SIF_Version( effectiveZISVersion ) );
					if( true )
					{
						// TT 2007
						// If the ADK Version is set to 1.1 or 1.5r1, only send those two
						// versions in the SIF_Register message. The downside to this is
						// that we can't connect to a 2.0 ZIS using SIF 1.5r1 and still
						// receive 2.0 events. However, this seems to be the best approach
						// because it ensures greater compatibility with older ZIS's that will
						// otherwise fail if they get a 2.0 version in the SIF_Register message
						SIFVersion[] supported = ADK.getSupportedSIFVersions();
						for( int i = 0; i < supported.length; i++ ) {
							// Exclude the version added above
							if( supported[i].compareTo( effectiveZISVersion ) < 0 ) {
									msg.addSIF_Version( new SIF_Version( supported[i] ) );
							}
						}
					}
					else
					{
						//  NOTE: Decided not to do this because the ADK does not currently
						// support ".*" XML parsing.
						if( ADK.getSIFVersion().compareTo( SIFVersion.SIF20 ) >= 0 )
						{
							msg.addSIF_Version( new SIF_Version( "2.*" ) );
						}
						msg.addSIF_Version( new SIF_Version( "1.*" ) );
					}
				}
				else
				{
					msg.addSIF_Version( new SIF_Version( ADK.getSIFVersion() ) );
				}
		}


		//
		//  Set the SIF_Protocol object as supplied by the Transport. Depending
		//  on the transport protocol and the messaging mode employed by the
		//  zone we may or may not get a SIF_Protocol object back
		//
		SIF_Protocol po = ((ZoneImpl)zone).getProtocolHandler().makeSIF_Protocol(zone, effectiveZISVersion );
		if( po != null ) {
/*
// TODO: This not currently supported by the 1.0r1 or 1.0r2 DTDs; message won't
//       pass validation if this is included. Investigate passing parameters on
//       the URL instead of using the <SIF_Property> element to notify SIFWorks
//       that an ADK-based agent is connecting.

			po.setSIF_Property( new SIF_Property( "SIFWorks ADK", ADK.getADKVersion() ) );
*/
			msg.setSIF_Protocol(po);
		}

		return adkZone.getFDispatcher().send(msg);
	}


	/**
	 *  SIF_Unregister
	 */
	public SIF_Ack sifUnregister( Zone zone )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		SIFVersion effectiveZISVersion = adkZone.getHighestEffectiveZISVersion();
		SIFMessagePayload message = new SIF_Unregister( effectiveZISVersion );
		return adkZone.getFDispatcher().send( message );
	}

	/**
	 *  SIF_Subscribe
	 */
	public SIF_Ack sifSubscribe( Zone zone, String[] objectType )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		SIF_Subscribe msg = new SIF_Subscribe( adkZone.getHighestEffectiveZISVersion() );
		for( int i = 0; i < objectType.length; i++ ) {
			SIF_Object obj = new SIF_Object();
			obj.setObjectName( objectType[i] );
			msg.addSIF_Object(obj);
		}
		return adkZone.getFDispatcher().send(msg);
	}

	/**
	 *  SIF_Unsubscribe
	 */
	public SIF_Ack sifUnsubscribe( Zone zone, String[] objectType )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		SIF_Unsubscribe msg = new SIF_Unsubscribe( adkZone.getHighestEffectiveZISVersion() );
		for( int i = 0; i < objectType.length; i++ ) {
			SIF_Object obj = new SIF_Object();
			obj.setObjectName( objectType[i] );
			msg.addSIF_Object(obj);
		}
		return adkZone.getFDispatcher().send(msg);
	}

	/**
	 *  SIF_Provide
	 */
	public SIF_Ack sifProvide( Zone zone, String[] objectType )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		SIF_Provide msg = new SIF_Provide( adkZone.getHighestEffectiveZISVersion() );
		for( int i = 0; i < objectType.length; i++ ) {
			SIF_Object obj = new SIF_Object();
			obj.setObjectName( objectType[i] );
			msg.addSIF_Object(obj);
		}

		return adkZone.getFDispatcher().send(msg);
	}

	/**
	 *  SIF_Unprovide
	 */
	public SIF_Ack sifUnprovide( Zone zone, String[] objectType )
		throws ADKException
	{
		ZoneImpl adkZone = ( ZoneImpl )zone;
		SIF_Unprovide msg = new SIF_Unprovide( adkZone.getHighestEffectiveZISVersion() );
		for( int i = 0; i < objectType.length; i++ ) {
			SIF_Object obj = new SIF_Object();
			obj.setObjectName( objectType[i] );
			msg.addSIF_Object(obj);
		}

		return adkZone.getFDispatcher().send(msg);
	}

	/**
	 *  SIF_Ping
	 */
	public SIF_Ack sifPing( Zone zone )
		throws ADKException
	{

		return sifSystemControl( new SIF_Ping(), (ZoneImpl)zone ); 	}

	/**
	 *  SIF_ZoneStatus
	 */
	public SIF_Ack sifZoneStatus( Zone zone )
		throws ADKException
	{
		return sifSystemControl(  new SIF_GetZoneStatus(), (ZoneImpl)zone );
	}

	/**
	 *  SIF_Sleep
	 */
	public SIF_Ack sifSleep( Zone zone )
		throws ADKException
	{
		return sifSystemControl(  new SIF_Sleep(), (ZoneImpl)zone );
	}

	/**
	 *  SIF_GetAgentACL
	 */
	public SIF_Ack sifGetAgentACL(Zone zone) throws ADKException {
		return sifSystemControl(  new SIF_GetAgentACL(), (ZoneImpl)zone );
	}


	/**
	 *  SIF_Wakeup
	 */
	public SIF_Ack sifWakeup( Zone zone )
		throws ADKException
	{
		return sifSystemControl( new SIF_Wakeup(), (ZoneImpl)zone );
	}

	private SIF_Ack sifSystemControl( SIFElement command, ZoneImpl zone )
		throws ADKException
	{
		SIF_SystemControl msg = new SIF_SystemControl( zone.getHighestEffectiveZISVersion() );
		SIF_SystemControlData cmd = new SIF_SystemControlData();
		cmd.addChild( command );
		msg.setSIF_SystemControlData(cmd);
		
		return zone.getFDispatcher().send(msg);
	}

	/**
	 *  Sends a SIF_Event
	 *  @param zone The zone to send the event to
	 */
	public SIF_Ack sifEvent( Zone zone, Event event, String destinationId, String sifMsgId )
		throws ADKException
	{

		if( event.getData() == null || event.getData().available() == false )
			throw new ADKException( "The event has no SIFDataObjects", zone );

		SIF_ObjectData od = new SIF_ObjectData();

		//  Fill out the SIF_ObjectData
		DataObjectInputStream inStr = event.getData();
		SIFDataObject data = inStr.readDataObject();

		SIFVersion msgVersion = data.effectiveSIFVersion();

		SIF_EventObject eo = new SIF_EventObject();
		od.setSIF_EventObject( eo );
		eo.setAction( event.getActionString() );
		eo.setObjectName( data.getElementDef().tag( msgVersion ) );

		// Create the SIF_Event object
		SIF_Event msg = new SIF_Event( msgVersion );
		msg.setSIF_ObjectData(od);

		SIF_Header msgHdr = msg.getHeader();

		//	Assign SIF_DestinationId if applicable
		if( destinationId != null ) {
			msgHdr.setSIF_DestinationId(destinationId);
		}

		while( data != null ) {
			eo.attach( data );
			data = inStr.readDataObject();
		}

		if( sifMsgId != null ) {
			msgHdr.setSIF_MsgId(sifMsgId);
		}

		SIFContext[] contexts = event.getContexts();
		if( contexts == null ){
			contexts = new SIFContext[] { SIFContext.DEFAULT };
		}

		SIF_Contexts msgContexts = new SIF_Contexts();
		for( SIFContext context : contexts ){
			msgContexts.addSIF_Context( context.getName() );
		}
		msgHdr.setSIF_Contexts( msgContexts );
		return ((ZoneImpl)zone).getFDispatcher().send(msg);
	}

	/**
	 *  SIF_Request
	 */
	public SIF_Ack sifRequest( Zone zone, Query query, String destinationId, String sifMsgId )
		throws ADKException
	{
		//  Send SIF_Request...
		SIF_Request msg = new SIF_Request();
		// Find the maxmimum requested version and set the version of the message to lower
		// if the version is currently higher than the highest requested version.
		// In other words, if the ADK is initialized to 2.0, but the highest requested version
		// is 1.5r1, set the message version to 1.5r1
		SIFVersion highestRequestVersion = SIFVersion.SIF11;
		if( query.getObjectType() == InfraDTD.SIF_ZONESTATUS ){
			// This query will be satisfied by the ZIS. Use the ZIS compatibility
			// version, which returns the highest version supported by the ZIS
			// (Default to ADK.getSIFVersion() if not specified in the config)
			highestRequestVersion = ( (ZoneImpl)zone).getHighestEffectiveZISVersion();
			msg.addSIF_Version(  new SIF_Version( highestRequestVersion ) );
		} else {
			
			SIFVersion[] requestVersions = query.getSIFVersions();
			if( requestVersions.length > 0 ){
				// If the Query has one or more SIFVersions set, use them,
				// and also add [major].*
				for( SIFVersion version : requestVersions ){
					msg.addSIF_Version(  new SIF_Version( version ) );
					if( version.compareTo( highestRequestVersion ) > 0 ){
						highestRequestVersion = version;
					}
				}
				
			} else {
				highestRequestVersion = ADK.getSIFVersion();
				if( highestRequestVersion.getMajor() == 1 ){
					msg.addSIF_Version(  new SIF_Version( highestRequestVersion ) );
				} else {
					// 2.0 and greater, request all data using
					// [major].*, with 2.0r1 as the message version
					// This allows for maximum compatibility will all 2.x providers
					msg.addSIF_Version( new SIF_Version( String.valueOf( highestRequestVersion.getMajor() ) + ".*" ));
					// Version change also made when Message read in
				
					SIFVersion earliest = SIFVersion.getEarliest( highestRequestVersion.getMajor() );
					
					if( earliest.compareTo( query.getObjectType().getEarliestVersion())  < 0  ){
						// This object type is new since the release of SIF. Use the minimum version
						// The object appeared in as the message version.
						msg.setSIFVersion( query.getObjectType().getEarliestVersion() );
					} else {
						msg.setSIFVersion( earliest );
					}
				}
			}
		}
		
		AgentProperties zoneProperties = zone.getProperties();		

		String overrideRequestVersion = zoneProperties.getOverrideSifMessageVersionForSifRequests();
		if (overrideRequestVersion != null && overrideRequestVersion.length() > 0) {
			//There is a property in Agent.cfg that can be used to override the message version from the 
			//default of 2.0r1.  This is needed to pass the test harness for 2.3.
			msg.setSIFVersion(SIFVersion.parse(overrideRequestVersion));
		} else if( msg.getSIFVersion().compareTo( highestRequestVersion ) > 0 ){
			// The current version of the SIF_Message is higher than the highest
			// requested version. Back the version number of message down to match
			msg.setSIFVersion( highestRequestVersion );
		}

		msg.setSIF_MaxBufferSize( zone.getProperties().getMaxBufferSize() );

		SIF_Query sifQ = createSIF_Query( query, highestRequestVersion, zone );
		msg.setSIF_Query( sifQ );

		SIF_Header msgHeader = msg.getHeader();

		if( destinationId != null ) {
			msgHeader.setSIF_DestinationId(destinationId);
		}
		if( sifMsgId != null ) {
			msgHeader.setSIF_MsgId(sifMsgId);
		}

		// Set the SIF_Context
		msgHeader.setSIF_Contexts(
				new SIF_Contexts(
						new SIF_Context( query.getSIFContext().getName() ) ) );

		return ((ZoneImpl)zone).getFDispatcher().send(msg);
	}
	

	public SIF_Ack sifCancelRequests(ZoneImpl zone, String destinationId,
			String[] sif_MsgIds) throws ADKException {
		
		//  Send CancelRequests...
		SIF_CancelRequests msg = new SIF_CancelRequests();
		
		msg.setSIF_NotificationType(SIF_NotificationType.STANDARD);
		SIF_RequestMsgIds sif_RequestMsgIds = new SIF_RequestMsgIds();
		
		for (int i=0; i< sif_MsgIds.length; ++i) {
			SIF_RequestMsgId sif_RequestMsgId = new SIF_RequestMsgId();
			sif_RequestMsgId.setValue(sif_MsgIds[i]);
			sif_RequestMsgIds.add(sif_RequestMsgId);
		}
		msg.setSIF_RequestMsgIds(sif_RequestMsgIds);
				
		return sifSystemControl( msg, (ZoneImpl)zone ); 	
	}


	/**
	 * Creates a SIF_Query element from the specified ADK query object using
	 * zone-specific querying rules
	 * @param query The Query to convert to a SIF_Query
	 * @param zone The Zone to retrieve query settings from, or null
	 * @return a SIF_Query instance
	 */
	public static SIF_Query createSIF_Query( Query query, Zone zone )
	{
		boolean allowFieldRestrictions = query.hasFieldRestrictions();
		if( allowFieldRestrictions && zone !=null )
		{
			 allowFieldRestrictions =  !zone.getProperties().getNoRequestIndividualElements();
		}
		return createSIF_Query( query, query.getEffectiveVersion(), allowFieldRestrictions );
	}

	/**
	 * Creates a SIF_Query element from the specified ADK query object using
	 * zone-specific querying rules
	 * @param query The Query to convert to a SIF_Query
	 * @param version The version of SIF to render the SIF_Query xml in
	 * @param zone The Zone to retrieve query settings from, or null
	 * @return a SIF_Query instance
	 */
	public static SIF_Query createSIF_Query( Query query, SIFVersion version, Zone zone )
	{
		boolean allowFieldRestrictions = query.hasFieldRestrictions();
		if( allowFieldRestrictions && zone !=null )
		{
			 allowFieldRestrictions =  !zone.getProperties().getNoRequestIndividualElements();
		}
		return createSIF_Query( query, version, allowFieldRestrictions );
	}

	/**
	 * Creates a SIF_Query element from the specified ADK query object using
	 * the specified version of SIF
	 * @param query The Query to convert to a SIF_Query
	 * @param version The version of SIF to render the SIF_Query xml in
	 * @param allowFieldRestrictions True if the field restrictions in the query should be rendered
	 * @return a SIF_Query object
	 */
	public static SIF_Query createSIF_Query( Query query, SIFVersion version, boolean allowFieldRestrictions )
	{

		SIF_QueryObject sqo = new SIF_QueryObject(query.getObjectType().tag( version ));
		SIF_Query sifQ = new SIF_Query( sqo );
		if( query.hasConditions() )
		{
			sifQ.setSIF_ConditionGroup( createConditionGroup( query, version ) );
		}

		if( allowFieldRestrictions && query.hasFieldRestrictions() ) {
			for( ElementRef elementRef : query.getFieldRestrictionRefs()){
				 String path = null;
				 ElementDef field = elementRef.getField();
                 if( field != null )
                 {
                     if( !field.isSupported( version ) )
                     {
                         continue;
                     }
                     path = field.getSQPPath( version );
                 }
                 if( path == null )
                 {
                     path = elementRef.getXPath();
                 }
                 if( path != null )
                 {
                     path = ADK.DTD().translateSQP( query.getObjectType(), path, version );
                     sqo.addSIF_Element(new SIF_Element( path ));
                 }
			}
		}

		return sifQ;

	}

	private static SIF_ConditionGroup createConditionGroup( Query query, SIFVersion effectiveVersion )
	{

		// Create the hierarchy SIF_ConditionGroup
		//                              >    SIF_Conditons
		//                                        > SIF_Condition

		// From
		//                       ConditionGroup
		//								> [ConditionGroup (Optional)]
		//										> Condition

		SIF_ConditionGroup returnGroup = new SIF_ConditionGroup();
		returnGroup.setType( ConditionType.NONE );
		ConditionGroup cg = query.getRootConditionGroup();
		ConditionGroup[] groups = cg.getGroups();
		if( groups != null && groups.length > 0 )
		{
			//
			//	There's one or more ConditionGroups...
			// 	These get translated to SIF_Conditions elements
			//
			if( cg.getOperator() == GroupOperators.OR ){
				returnGroup.setType( ConditionType.OR );
			}
			else if( cg.getOperator() == GroupOperators.AND ){
				returnGroup.setType( ConditionType.AND );
			}

			for( ConditionGroup group : groups )
			{
				returnGroup.addSIF_Conditions( createConditions( query, group, effectiveVersion ) );
			}
		}
		else
		{
			//
			//	There are no SIF_Conditions groups, so build one...
			//
			returnGroup.addSIF_Conditions( createConditions( query, cg, effectiveVersion ) );
		}
		return returnGroup;
	}

	private static SIF_Conditions createConditions( Query query, ConditionGroup group, SIFVersion effectiveVersion )
	{
		ConditionType typ = ConditionType.NONE;
		if( group.getOperator() == GroupOperators.AND ){
			typ = ConditionType.AND;
		}
		else if( group.getOperator() == GroupOperators.OR ){
			typ = ConditionType.OR;
		}
		Condition[] conditions = group.getConditions();
		SIF_Conditions conds = new SIF_Conditions( conditions.length > 1 ? typ : ConditionType.NONE );
		for( Condition c : conditions ) {
			conds.addSIF_Condition(
				c.getXPath( query, effectiveVersion ),
				c.getOperator().getSIFValue(),
				c.getValue() );
		}
		return conds;

	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.ISIFPrimitives#sifProvision(com.edustructures.sifworks.Zone, com.edustructures.sifworks.infra.SIF_Provision)
	 */
	public SIF_Ack sifProvision( Zone zone,
			SIF_ProvideObjects providedObjects,
			SIF_SubscribeObjects subscribeObjects,
			SIF_PublishAddObjects publishAddObjects,
			SIF_PublishChangeObjects publishChangeObjects,
			SIF_PublishDeleteObjects publishDeleteObjects,
			SIF_RequestObjects requestObjects,
			SIF_RespondObjects respondObjects  ) throws ADKException {

		SIF_Provision msg = new SIF_Provision( ((ZoneImpl)zone).getHighestEffectiveZISVersion() );
		if( providedObjects != null ){
			msg.setSIF_ProvideObjects( providedObjects );
		}
		if( publishAddObjects != null ){
			msg.setSIF_PublishAddObjects( publishAddObjects );
		}
		if( publishChangeObjects != null ){
			msg.setSIF_PublishChangeObjects( publishChangeObjects );
		}
		if( publishDeleteObjects != null ){
			msg.setSIF_PublishDeleteObjects( publishDeleteObjects );
		}
			if( subscribeObjects != null ){
			msg.setSIF_SubscribeObjects( subscribeObjects );
		}
		if( requestObjects != null ){
			msg.setSIF_RequestObjects( requestObjects );
		}
		if( respondObjects != null ){
			msg.setSIF_RespondObjects( respondObjects );
		}

		return ((ZoneImpl)zone).getFDispatcher().send( msg );
	}


	/**
	 * Attempts to parse attributes out of the source message enough to make a valid
	 * SIF_Ack with a SIF_Error. This is useful in conditions where the source message cannot
	 * be parsed by the ADK
	 *
	 * @param sourceMessage The original message as a string
	 * @param error The error to place in the SIF_Ack/SIF_Error
	 * @param zone The zone associated with this message
	 * @return
	 * @throws ADKMessagingException
	 */
	public static SIF_Ack ackError(String sourceMessage, SIFException error, ZoneImpl zone )
	{
		SIFMessageInfo parsed =  null;
		try
		{
			StringReader reader = new StringReader( sourceMessage );
			parsed = SIFMessageInfo.parse(reader, false, zone );
			reader.close();
		} catch (IOException ioe ){
			zone.getLog().error( ioe, ioe );
		} catch (ADKMessagingException e) {
			zone.getLog().error( e, e );
		}

		SIF_Ack errorAck = new SIF_Ack( zone.getHighestEffectiveZISVersion() );

		if( parsed != null ){
			// Set SIFVersion, OriginalSourceId, and OriginalMsgId;
			if( parsed.getSIFVersion() != null ){
				errorAck.setSIFVersion( parsed.getSIFVersion() );
			}
			errorAck.setSIF_OriginalMsgId( parsed.getAttribute( "SIF_MsgId" ) );
			errorAck.setSIF_OriginalSourceId( parsed.getAttribute( "SIF_SourceId" ) );
		}
		SetRequiredAckValues( errorAck );

		SIF_Error newErr = new SIF_Error();
		newErr.setSIF_Category( error.getSIFErrorCategory() );
		newErr.setSIF_Code( error.getErrorCode() );
		newErr.setSIF_Desc( error.getErrorDesc() );
		newErr.setSIF_ExtendedDesc( error.getErrorExtDesc() );
		errorAck.setSIF_Error( newErr );

		return errorAck;
	}

	/**
	 * If the SIF_OriginalMsgID or SIF_OriginalSourceId are not set,
	 * process according to Infrastructure resolution #157
	 * @param errorAck
	 */
	public static void SetRequiredAckValues( SIF_Ack errorAck )
	{
		//  Return a SIF_Ack with a blank SIF_OriginalSourceId and
		//  SIF_OriginalMsgId per SIFInfra resolution #157
		// Also See 4.1.2.1 SIF_Message processing
		if( errorAck.getField( InfraDTD.SIF_ACK_SIF_ORIGINALMSGID ) == null ){
			// Set SIF_OriginalMsgId to xsi:nill
			errorAck.setField( InfraDTD.SIF_ACK_SIF_ORIGINALMSGID, new SIFString( null ) );
		}
		if( errorAck.getField( InfraDTD.SIF_ACK_SIF_ORIGINALSOURCEID ) == null ){
			// Set SIF_OriginalSource to an empty string
			errorAck.setSIF_OriginalSourceId( "" );
		}
	}


	/**
	 * JEN - new Service Request
	 * @see openadk.library.impl.ISIFPrimitives#sifServiceRequest(openadk.library.Zone, openadk.library.services.ServiceRequestInfo, openadk.library.SIFElement)
	 */
	public SIF_Ack sifServiceRequest(Zone zone, ServiceRequestInfo requestInfo, SIFElement payload) throws ADKException {
		//  Send SIF_ServiceInput...
		SIF_ServiceInput msg = new SIF_ServiceInput();
		msg.setSIF_Service(requestInfo.getServiceName());
		msg.setSIF_Operation(requestInfo.getMethodName());
		msg.setSIF_ServiceMsgId(requestInfo.getSIFServiceMsgId());
		msg.setSIFVersion(SIFVersion.LATEST); // todo fix
		
		// todo implement packetization
		msg.setSIF_PacketNumber(1);
		msg.setSIF_MorePackets(YesNo.NO);
		
		// Find the maxmimum requested version and set the version of the message to lower
		// if the version is currently higher than the highest requested version.
		// In other words, if the ADK is initialized to 2.0, but the highest requested version
		// is 1.5r1, set the message version to 1.5r1
//		SIFVersion highestRequestVersion = SIFVersion.SIF11;
		SIFVersion highestRequestVersion = SIFVersion.LATEST;
/*		if( query.getObjectType() == InfraDTD.SIF_ZONESTATUS ){
			// This query will be satisfied by the ZIS. Use the ZIS compatibility
			// version, which returns the highest version supported by the ZIS
			// (Default to ADK.getSIFVersion() if not specified in the config)
			highestRequestVersion = ( (ZoneImpl)zone).getHighestEffectiveZISVersion();
			msg.addSIF_Version(  new SIF_Version( highestRequestVersion ) );
		} 
		else {
			SIFVersion[] requestVersions = query.getSIFVersions();
			if( requestVersions.length > 0 ){
				// If the Query has one or more SIFVersions set, use them,
				// and also add [major].*
				for( SIFVersion version : requestVersions ){
					msg.addSIF_Version(  new SIF_Version( version ) );
					if( version.compareTo( highestRequestVersion ) > 0 ){
						highestRequestVersion = version;
					}
				}
			} else {
				highestRequestVersion = ADK.getSIFVersion();
				if( highestRequestVersion.getMajor() == 1 ){
					msg.addSIF_Version(  new SIF_Version( highestRequestVersion ) );
				} else {
					// 2.0 and greater, request all data using
					// [major].*, with 2.0r1 as the message version
					// This allows for maximum compatibility will all 2.x providers
					msg.addSIF_Version( new SIF_Version( String.valueOf( highestRequestVersion.getMajor() ) + ".*" ));
					msg.setSIFVersion( SIFVersion.getEarliest( highestRequestVersion.getMajor() ) );
				}
			}
		}
*/
		if( msg.getSIFVersion().compareTo( highestRequestVersion ) > 0 ){
			// The current version of the SIF_Message is higher than the highest
			// requested version. Back the version number of message down to match
			msg.setSIFVersion( highestRequestVersion );
		}

		msg.setSIF_MaxBufferSize( zone.getProperties().getMaxBufferSize() );
		
/*		StringWriter sw = new StringWriter();
		SIFWriter writer = new SIFWriter(sw);
		writer.suppressNamespace(true);
		writer.write(payload);
		writer.flush();
		writer.close();
		String xml = sw.toString();

		XMLData xmlData = new XMLData();
		try {
			xmlData.setXML(loadXMLFrom(xml));
		} catch (Exception e) {
		} 
*/		
//		msg.setSIF_Body(xmlData);	
		SIF_Body sifBody = new SIF_Body();
		sifBody.addChild(payload);		
		msg.setSIF_Body(sifBody);

		SIF_Header msgHeader = msg.getHeader();

		if( requestInfo.getDestinationId() != null ) {
			msgHeader.setSIF_DestinationId(requestInfo.getDestinationId());
		}

		// Set the SIF_Context
/*		msgHeader.setSIF_Contexts(
				new SIF_Contexts(
						new SIF_Context( query.getSIFContext().getName() ) ) );
*/
		return ((ZoneImpl)zone).getFDispatcher().send(msg);
	}
	
	private static org.w3c.dom.Document loadXMLFrom(String xml)
    	throws org.xml.sax.SAXException, java.io.IOException {
	    return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
	}

	private static org.w3c.dom.Document loadXMLFrom(java.io.InputStream is) 
    	throws org.xml.sax.SAXException, java.io.IOException {
	    javax.xml.parsers.DocumentBuilderFactory factory =
	        javax.xml.parsers.DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    javax.xml.parsers.DocumentBuilder builder = null;
	    try {
	        builder = factory.newDocumentBuilder();
	    }
	    catch (javax.xml.parsers.ParserConfigurationException ex) {
	    }  
	    org.w3c.dom.Document doc = builder.parse(is);
	    is.close();
	    return doc;
	}


	public SIF_Ack sifServiceEvent(Zone zone, ServiceEvent event, String destinationId) throws ADKException {
		
		if( event.getData() == null || event.getData().available() == false )
			throw new ADKException( "The event has no SIFDataObjects", zone );
	
	
		// Create the SIF_ServiceNotify object
		DataObjectInputStream inStr = event.getData();
		SIFDataObject data = inStr.readDataObject();
		SIFVersion msgVersion = data.effectiveSIFVersion();
		SIF_ServiceNotify msg = new SIF_ServiceNotify( msgVersion );
		msg.setSIFVersion(SIFVersion.LATEST); // todo fix

		msg.setSIF_Service(event.getService());
		msg.setSIF_Operation(event.getOperation());
		msg.setSIF_ServiceMsgId(event.getServiceMsgId());
		msg.setSIF_PacketNumber(1);  // todo fix
		msg.setSIF_MorePackets(YesNo.NO);
		
		// Body
		SIF_Body sifBody = new SIF_Body();
		while( data != null ) {
			sifBody.addChild(data);		
			data = inStr.readDataObject();
		}
		msg.setSIF_Body(sifBody);
	
		// Header
		SIF_Header msgHdr = msg.getHeader();
	
		//	Assign SIF_DestinationId if applicable
		if( destinationId != null ) {
			msgHdr.setSIF_DestinationId(destinationId);
		}
	
	
/*		if( sifMsgId != null ) {
			msgHdr.setSIF_MsgId(sifMsgId);
		}
*/	
		SIFContext[] contexts = event.getContexts();
		if( contexts == null ){
			contexts = new SIFContext[] { SIFContext.DEFAULT };
		}
	
		SIF_Contexts msgContexts = new SIF_Contexts();
		for (SIFContext context : contexts) {
			msgContexts.addSIF_Context( context.getName() );
		}
		msgHdr.setSIF_Contexts( msgContexts );
		
		return ((ZoneImpl)zone).getFDispatcher().send(msg);	
	}


}
