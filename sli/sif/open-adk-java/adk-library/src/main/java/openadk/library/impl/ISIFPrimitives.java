//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.infra.*;
import openadk.library.services.ServiceRequestInfo;


/**
 *  The ISIFPrimitives interface is implemented by internal ADK classes that
 *  implement primitive SIF messaging functionality such as SIF_Register, SIF_Event,
 *  and SIF_SystemControl. Internal ADK classes always delegate low-level SIF
 *  functions to the global ISIFPrimitives object provided by the static <code>
 *  ADK.getPrimitives</code> method.
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public interface ISIFPrimitives
{
	/**
	 *  SIF_Register
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifRegister( Zone zone )
		throws ADKException;

	/**
	 *  SIF_Unregister
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifUnregister( Zone zone )
		throws ADKException;

	/**
	 *  SIF_Subscribe
	 * @param zone 
	 * @param objectType 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifSubscribe( Zone zone, String[] objectType )
		throws ADKException;

	/**
	 *  SIF_Unsubscribe
	 * @param zone 
	 * @param objectType 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifUnsubscribe( Zone zone, String[] objectType )
		throws ADKException;

	/**
	 *  SIF_Provide
	 * @param zone 
	 * @param objectType 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifProvide( Zone zone, String[] objectType )
		throws ADKException;

	/**
	 *  SIF_Unprovide
	 * @param zone 
	 * @param objectType 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifUnprovide( Zone zone, String[] objectType )
		throws ADKException;

	/**
	 *  SIF_Ping
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifPing( Zone zone )
		throws ADKException;

	/**
	 *  SIF_ZoneStatus
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifZoneStatus( Zone zone )
		throws ADKException;
	
	/**
	 * SIF_GetAgentACL
	 * @param zone
	 * @return
	 * @throws ADKException
	 */
	public SIF_Ack sifGetAgentACL( Zone zone )
		throws ADKException;
	
	/**
	 *  SIF_Provision
	 * @param zone
	 * @param providedObjects 
	 * @param subscribeObjects 
	 * @param publishAddObjects 
	 * @param publishChangeObjects 
	 * @param publishDeleteObjects 
	 * @param requestObjects 
	 * @param respondObjects 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifProvision(
			Zone zone, 
			SIF_ProvideObjects providedObjects,
			SIF_SubscribeObjects subscribeObjects,
			SIF_PublishAddObjects publishAddObjects,
			SIF_PublishChangeObjects publishChangeObjects,
			SIF_PublishDeleteObjects publishDeleteObjects,
			SIF_RequestObjects requestObjects,
			SIF_RespondObjects respondObjects )
			 
		throws ADKException;

	/**
	 *  SIF_Sleep
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifSleep( Zone zone )
		throws ADKException;

	/**
	 *  SIF_Wakeup
	 * @param zone 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifWakeup( Zone zone )
		throws ADKException;

	/**
	 *  Sends a SIF_Event
	 * @param zone 
	 * @param event 
	 * @param destinationId 
	 * @param sifMsgId 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifEvent( Zone zone, Event event, String destinationId, String sifMsgId )
		throws ADKException;

	/**
	 *  SIF_Request
	 * @param zone 
	 * @param query 
	 * @param destinationId 
	 * @param sifMsgId 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifRequest( Zone zone, Query query, String destinationId, String sifMsgId )
		throws ADKException;
	
	/**
	 *  SIF_CancelRequests
	 * @param zone 
	 * @param destinationId 
	 * @param sifMsgIds
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifCancelRequests(ZoneImpl zoneImpl, String destinationId,
			String[] sif_MsgIds) throws ADKException;

	/**
	 *  SIF_Request
	 * @param zone 
	 * @param query 
	 * @param destinationId 
	 * @param sifMsgId 
	 * @return 
	 * @throws ADKException 
	 */
	public SIF_Ack sifServiceRequest( Zone zone, ServiceRequestInfo requestInfo, SIFElement payload )
		throws ADKException;
	
	public SIF_Ack sifServiceEvent(Zone zone, ServiceEvent event, String destinationId) throws ADKException;


}
