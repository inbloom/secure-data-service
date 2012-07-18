//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *	Defines SIF 1.0r1 error category and code constants.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class SIFErrorCodes
{
	/**
	 *  Unknown (Error Category Code; never use if possible) (Deprecated)
	 *  @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_UNKNOWN_0 = 0;

	/**
	 *  XML Validation category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_XML_1 = 1;

	/**
	 *  Encryption category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_ENCRYPT_2 = 2;

	/**
	 *  Authentication category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_AUTH_3 = 3;

	/**
	 *  Access and Permissions category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_ACCESS_4 = 4;

	/**
	 *  Registration category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_REG_5 = 5;

	/**
	 *  Provision category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_PROVISION_6 = 6;

	/**
	 *  Subscription category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_SUBSCR_7 = 7;

	/**
	 *  Request and Response category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_REQRSP_8 = 8;

	/**
	 *  Event Reporting and Processing category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_EVENT_9 = 9;

	/**
	 *  Transport category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_TRANSPORT_10 = 10;

	/**
	 *  System category (Deprecated)
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_SYS_11 = 11;


	/**
	 *  Generic Message Handling
	 *   @deprecated Use {@link SIFErrorCategory} instead
	 */
	public static final int CAT_GENERIC_12 = 12;


	/**
	 *  Generic error
	 */
	public static final int XML_GENERIC_ERROR_1 = 1;

	/**
	 *  Message is not well formed
	 */
	public static final int XML_MALFORMED_2 = 2;

	/**
	 *  Generic validation error
	 */
	public static final int XML_GENERIC_VALIDATION_3 = 3;

	/**
	 *  Invalid value for element
	 */
	public static final int XML_INVALID_VALUE_4 = 4;

	/**
	 *  Reserved
	 */
	public static final int XML_RESERVED_5 = 5;

	/**
	 *  Missing mandatory element
	 */
	public static final int XML_MISSING_MANDATORY_ELEMENT_6 = 6;



	/**
	 *  Generic error
	 */
	public static final int AUTH_GENERIC_ERROR_1 = 1;

	/**
	 *  Generic authentication error (with signature)
	 */
	public static final int AUTH_GENERIC_AUTH_ERROR_2 = 2;

	/**
	 *  Missing sender's certificate
	 */
	public static final int AUTH_NO_SENDER_CERT_3 = 3;

	/**
	 *  Invalid certificate
	 */
	public static final int AUTH_INVALID_CERT_4 = 4;

	/**
	 *  Sender's certificate is not trusted
	 */
	public static final int AUTH_SENDER_NOT_TRUSTED_5 = 5;

	/**
	 *  Expired certificate
	 */
	public static final int AUTH_EXPIRED_CERT_6 = 6;

	/**
	 *  Invalid signature
	 */
	public static final int AUTH_INVALID_SIG_7 = 7;

	/**
	 *  Invalid encryption algorithm (only accepts MD4)
	 */
	public static final int AUTH_INVALID_ENCRYPT_ALGO_8 = 8;

	/**
	 *  Missing public key of the receiver (when decrypting message)
	 */
	public static final int AUTH_NO_PUBLIC_KEY_9 = 9;

	/**
	 *  Missing private key of the receiver (when decrypting message)
	 */
	public static final int AUTH_NO_PRIVATE_KEY_10 = 10;


	/**
	 *  Generic error
	 */
	public static final int ACCESS_GENERIC_ERROR_1 = 1;

	/**
	 *  No permission to Register
	 */
	public static final int ACCESS_REGISTER_DENIED_2 = 2;

	/**
	 *  No permission to Provide this object
	 */
	public static final int ACCESS_PROVIDE_DENIED_3 = 3;

	/**
	 *  No permission to Subscribe to this SIF_Event
	 */
	public static final int ACCESS_SUBSCRIBE_DENIED_4 = 4;

	/**
	 *  No permission to Request this object
	 */
	public static final int ACCESS_REQUEST_DENIED_5 = 5;

	/**
	 *  No permission to Respond to this object request
	 */
	public static final int ACCESS_RESPOND_DENIED_6 = 6;

	/**
	 *  No permission to Report SIF_Events
	 */
	public static final int ACCESS_REPORT_DENIED_7 = 7;

	/**
	 *  No permission to administer policies
	 */
	public static final int ACCESS_ADMIN_DENIED_8 = 8;

	/**
	 *  SIF_SourceId is not registered
	 */
	public static final int ACCESS_UNKNOWN_SOURCEID_9 = 9;

	/**
	 *  No permission to report SIF_Event Add
	 */
	public static final int ACCESS_SIFEVENT_ADD_DENIED_10 = 10;

	/**
	 *  No permission to report SIF_Event Change
	 */
	public static final int ACCESS_SIFEVENT_CHANGE_DENIED_11 = 11;

	/**
	 *  No permission to report SIF_Event Delete
	 */
	public static final int ACCESS_SIFEVENT_DELETE_DENIED_12 = 12;


	/**
	 *  Generic error
	 */
	public static final int REG_GENERIC_ERROR_1 = 1;

	/**
	 *  The SIF_SourceId is invalid
	 */
	public static final int REG_INVALID_SOURCEID_2 = 2;

	/**
	 *  Requested transport protocol is unsupported
	 */
	public static final int REG_UNSUPPORTED_WIRE_PROTO_3 = 3;

	/**
	 *  Requested SIF Version(s) not supported
	 */
	public static final int REG_UNSUPPORTED_SIFVERSION_4 = 4;

	/**
	 *  Requested Maximum Packet Size is too small
	 */
	public static final int REG_SMALL_MAXPACKETSIZE_6 = 6;

	/**
	 *  ZIS requires an encrypted transport
	 */
	public static final int REG_SECURE_TRANSPORT_REQUIRED_7 = 7;

	/**
	 *  Reserved
	 */
	public static final int REG_RESERVED_8 = 8;

	/**
	 *  Agent is registered for Push mode
	 */
	public static final int REG_PUSH_EXPECTED_9 = 9;

	/**
	 *  ZIS does not support the requested Accept-Encoding value.
	 */
	public static final int REG_ENCODING_NOT_SUPPORTED_10 = 10;

	

	/**
	 *  Generic error
	 */
	public static final int PROVISION_GENERIC_ERROR_1 = 1;

	/**
	 *  Reserved
	 */
	public static final int PROVISION_RESERVED_2 = 2;

	/**
	 *  Invalid object
	 */
	public static final int PROVISION_INVALID_OBJ_3 = 3;

	/**
	 *  Object already has a provider (SIF_Provide message)
	 */
	public static final int PROVISION_ALREADY_HAS_PROVIDER_4 = 4;

	/**
	 *  Not the provider of the object (SIF_Unprovide message)
	 */
	public static final int PROVISION_NOT_REGISTERED_PROVIDER_5 = 5;



	/**
	 *  Generic error
	 */
	public static final int SUBSCR_GENERIC_ERROR_1 = 1;

	/**
	 *  Reserved
	 */
	public static final int SUBSCR_RESERVED_2 = 2;

	/**
	 *  Invalid object
	 */
	public static final int SUBSCR_INVALID_OBJ_3 = 3;

	/**
	 *  Not a subscriber of the object (SIF_Unsubscribe message)
	 */
	public static final int SUBSCR_NOT_A_SUBSCRIBER_4 = 4;



	/**
	 *  Generic error
	 */
	public static final int REQRSP_GENERIC_ERROR_1 = 1;

	/**
	 *  Reserved
	 */
	public static final int REQRSP_RESERVED_2 = 2;

	/**
	 *  Invalid object
	 */
	public static final int REQRSP_INVALID_OBJ_3 = 3;

	/**
	 *  No Provider
	 */
	public static final int REQRSP_NO_PROVIDER_4 = 4;

	/**
	 *  Reserved
	 */
	public static final int REQRSP_RESERVED_5 = 5;

	/**
	 *  Reserved
	 */
	public static final int REQRSP_RESERVED_6 = 6;

	/**
	 *  Responder does not support requested SIF_Version
	 */
	public static final int REQRSP_UNSUPPORTED_SIFVERSION_7 = 7;

	/**
	 *  Responder does not support requested SIF_MaxBufferSize
	 */
	public static final int REQRSP_UNSUPPORTED_MAXBUFFERSIZE_8 = 8;

	/**
	 *  Responder does not support the query
	 */
	public static final int REQRSP_UNSUPPORTED_QUERY_9 = 9;
	
	/**
	 * Invalid SIF_RequestMsgId specified in SIF_Response
	 */
	public static final int REQRSP_INVALID_SIFREQ_MSGID = 10;
	/**
	 * SIF_Response is larger than requested SIF_MaxBufferSize
	 */
	public static final int REQRSP_RESP_LARGER_MAXBUFFERSIZE= 11;
	/**
	 * SIF_PacketNumber is invalid in SIF_Response
	 */
	public static final int REQRSP_PACKETNUMBER_INVALID = 12;
	/**
	 * SIF_Response does not match any SIF_Version from SIF_Request
	 */
	public static final int REQRSP_INVALID_VERSION = 13;
	/**
	 * SIF_DestinationId does not match SIF_SourceId from SIF_Request
	 */
	public static final int REQRSP_DESTINATION_ID_DOES_NOT_MATCH = 14;
	/**
	 * No support for SIF_ExtendedQuery
	 */
	public static final int REQRSP_NO_SUPPORT_FOR_SIF_EXT_QUERY = 15;
	/**
	 * SIF_RequestMsgId deleted from cache due to timeout
	 */
	public static final int REQRSP_REQUEST_DELETED_CACHE_TIMEOUT = 16;
	/**
	 * SIF_RequestMsgId deleted from cache by administrator
	 */
	public static final int REQRSP_REQUEST_DELETED_ADMIN= 17;

	/**
	 * SIF_Request cancelled by requesting agent
	 */
	public static final int REQRSP_REQUEST_DELETED_AGENT= 18;

	/**
	 *  Generic error
	 */
	public static final int EVENT_GENERIC_ERROR_1 = 1;

	/**
	 *  Reserved
	 */
	public static final int EVENT_RESERVED_2 = 2;

	/**
	 *  Invalid event
	 */
	public static final int EVENT_INVALID_EVENT_3 = 3;


	/**
	 *  Generic error
	 */
	public static final int WIRE_GENERIC_ERROR_1 = 1;

	/**
	 *  Requested protocol is not supported
	 */
	public static final int WIRE_PROTO_NOT_SUPPORTED_2 = 2;

	/**
	 *  Secure channel requested and no secure path exists
	 */
	public static final int WIRE_NO_SECURITY_AVAIL_3 = 3;

	/**
	 *  Unable to establish connection
	 */
	public static final int WIRE_NO_CONNECTION_4 = 4;


	/**
	 *  Generic system error
	 */
	public static final int SYS_GENERIC_ERROR_1 = 1;


	/**
	 *  Generic Agent Message Handling error
	 *  @deprecated Use GENERIC_GENERIC_ERROR_1 instead
	 */
	public static final int AGENT_GENERIC_ERROR_1 = 1;

	/**
	 *  Generic Message Handling error
	 */
	public static final int GENERIC_GENERIC_ERROR_1 = 1;

	/**
	 *  Message not supported
	 *  @deprecated Use GENERIC_MESSAGE_NOT_SUPPORTED_2 instead
	 */
	public static final int AGENT_MESSAGE_NOT_SUPPORTED_2 = 2;

	/**
	 *  Message not supported
	 */
	public static final int GENERIC_MESSAGE_NOT_SUPPORTED_2 = 2;

	/**
	 *  Version not supported
	 */
	public static final int GENERIC_VERSION_NOT_SUPPORTED_3 = 3;
	
	/**
	 *  Version not supported
	 */
	public static final int GENERIC_CONTEXT_NOT_SUPPORTED_4 = 4;
}
