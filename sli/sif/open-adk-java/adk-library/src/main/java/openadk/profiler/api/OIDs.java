//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

/**
 * 	Defines constants for all standard metric IDs defined by the Profiling Harness.
 * 	The values here are assembled to form hierarchical OIDs where each element in the OID
 * 	is delimited with a period.<p> 
 */
public class OIDs
{
	/**
	 * 	"ADK: SIF_Request Messaging (Requestor)"
	 */
	public static final int ADK_SIFREQUEST_REQUESTOR_MESSAGING = 1;
	
	/**
	 * 	"ADK: SIF_Request Messaging (Responder)"
	 */
	public static final int ADK_SIFREQUEST_RESPONDER_MESSAGING = 2;
	
	/**
	 * 	"ADK: SIF_Response Messaging (Requestor)"
	 */
	public static final int ADK_SIFRESPONSE_REQUESTOR_MESSAGING = 3;
	
	/**
	 * 	"ADK: SIF_Response Messaging (Responder)"
	 */
	public static final int ADK_SIFRESPONSE_RESPONDER_MESSAGING = 4;
	
	/**
	 * 	"ADK: SIF_Event Messaging"
	 */
	public static final int ADK_SIFEVENT_MESSAGING = 5;
	
	/**
	 * 	"ADK: Outbound Transformations"
	 */
	public static final int ADK_OUTBOUND_TRANSFORMATIONS = 6;
	
	/**
	 * 	"ADK: Inbound Transformations"
	 */
	public static final int ADK_INBOUND_TRANSFORMATIONS = 7;
	
	/**
	 * 	"ADK: SIF_Response Packetizing"
	 */
	public static final int ADK_SIFRESPONSE_PACKETIZING = 8;
	
	/**
	 * 	"ADK: SIF_Response Delivery"
	 */
	public static final int ADK_SIFRESPONSE_DELIVERY = 9;
	

	/**
	 * 	"SIFAgentLib: SIF_Request Object Processing (Requestor)"
	 */
	public static final int SIFAgentLib_SIFREQUEST_REQUESTOR_OBJPROC = 50;
	
	/**
	 * 	"SIFAgentLib: SIF_Request Object Processing (Responder)"
	 */
	public static final int SIFAgentLib_SIFREQUEST_RESPONDER_OBJPROC = 51;
	
	/**
	 * 	"SIFAgentLib: SIF_Response Object Processing (Requestor)"
	 */
	public static final int SIFAgentLib_SIFRESPONSE_REQUESTOR_OBJPROC = 52;
	
	/**
	 * 	"SIFAgentLib: SIF_Response Object Processing (Responder)"
	 */
	public static final int SIFAgentLib_SIFRESPONSE_RESPONDER_OBJPROC = 53;
	
	/**
	 * 	"SIFAgentLib: SIF_Event Object Processing"
	 */
	public static final int SIFAgentLib_SIFEVENT_REPORTER_OBJPROC = 54;
	
	/**
	 * 	"SIFAgentLib: SIF_Event Object Processing"
	 */
	public static final int SIFAgentLib_SIFEVENT_SUBSCRIBER_OBJPROC = 55;
	
	/**
	 * 	"SIFAgentLib: Database Reads"
	 */
	public static final int SIFAgentLib_DB_READS = 56;
	
	/**
	 * 	"SIFAgentLib: Database Writes"
	 */
	public static final int SIFAgentLib_DB_WRITES = 57;
	
	/**
	 * 	"SIFAgentLib: Synchronizer Data Collection Phase"
	 */
	public static final int SIFAgentLib_SYNC_PHASE_1 = 58;
	
	/**
	 * 	"SIFAgentLib: Synchronizer Analysis Phase"
	 */
	public static final int SIFAgentLib_SYNC_PHASE_2 = 59;
	
	/**
	 * 	"SIFAgentLib: Synchronizer Commit Phase"
	 */
	public static final int SIFAgentLib_SYNC_PHASE_3 = 60;
	
	/**
	 * 	"SIFAgentLib: Synchronizer Application Processing"
	 */
	public static final int SIFAgentLib_SYNC_PHASE_4 = 61;
	
	/**
	 * 	"SIFAgentLib: Event Reporting"
	 */
	public static final int SIFAgentLib_EVENT_REPORTING = 62;
	
	/**
	 * 	"SIFAgentLib: Event Subscription"
	 */
	public static final int SIFAgentLib_EVENT_SUBSCRIPTION = 63;
	
	/**
	 * 	"SIFAgentLib: Application Business Logic"
	 */
	public static final int SIFAgentLib_APP_BUSINESS_LOGIC = 64;
	
	/**
	 * 	"SIFAgentLib: Inbound Transformations"
	 */
	public static final int SIFAgentLib_INBOUND_TRANSFORMATIONS = 65;
	
	/**
	 * 	"SIFAgentLib: Outbound Transformations"
	 */
	public static final int SIFAgentLib_OUTBOUND_TRANSFORMATIONS = 66;
}
