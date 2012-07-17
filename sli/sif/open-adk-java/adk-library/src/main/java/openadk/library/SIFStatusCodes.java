//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *	Defines SIF 1.0r1 status code constants.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SIFStatusCodes
{
	/**
	 *  Success. SIF_Status / SIF_Data may contain additional data (ZIS only) ("0")
	 */
	public static final int SUCCESS_0 = 0;

	/**
	 *  Receiver is sleeping ("8")
	 */
	public static final int SLEEPING_8 = 8;

	/**
	 *  Already registered using this protocol ("4")
	 */
	public static final int ALREADY_REGISTERED_4 = 4;

	/**
	 *  Final SIF_Ack. Processing of a previously message acknowledged with INTERMEDIATE_ACK is now complete. Discard the referenced message (Agent only) ("3")
	 */
	public static final int FINAL_ACK_3 = 3;

	/**
	 *  Already registered as a provider of this object ("6")
	 */
	public static final int ALREADY_PROVIDER_6 = 6;

	/**
	 *  Immediate SIF_Ack. Message is persisted or processing is complete. Discard the referenced message (Agent only) ("1")
	 */
	public static final int IMMEDIATE_ACK_1 = 1;

	/**
	 *  Already subscribed to this object ("5")
	 */
	public static final int ALREADY_SUBSCRIBED_5 = 5;

	/**
	 *  Already have a message with this MsgId from sender ("7")
	 */
	public static final int DUPLICATE_MESSAGE_7 = 7;

	/**
	 *  No messages available. This is returned when an agent is trying to pull messages from a ZIS and there are no messages available ("9")
	 */
	public static final int NO_MESSAGES_9 = 9;

	/**
	 *  Intermediate SIF_Ack. Message processing will take time. The message referenced must still be persisted. Expect a FINAL_ACK at a later time (Agent only) ("2")
	 */
	public static final int INTERMEDIATE_ACK_2 = 2;
}
