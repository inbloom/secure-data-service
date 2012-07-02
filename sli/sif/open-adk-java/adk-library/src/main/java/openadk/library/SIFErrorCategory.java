//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * The list of codes defined by SIF for SIF_Category element in SIF_Error
 *
 */
public enum SIFErrorCategory {
	
	/**
	 * Unknown (This SHOULD NOT be used, if possible) (0)
	 */
	UNKNOWN,
	/**
	 * XML Validation (1)
	 */
	XML_VALIDATION,
	/**
	 * Encryption (2)
	 */
	ENCRYPTION,
	/**
	 * Authentication (3)
	 */
	AUTHENTICATION,
	/**
	 * Access and Permissions (4)
	 */
	ACCESS_PERMISSIONS,
	/**
	 * Agent Registration (5)
	 */
	REGISTRATION,
	/**
	 * Agent Provisioning (6)
	 */
	PROVISION,
	/**
	 * Subscription (7)
	 */
	SUBSCRIPTION,
	/**
	 * Request and Response (8)
	 */
	REQUEST_RESPONSE,
	/**
	 * Reporting and Processing events (9)
	 */
	EVENTS,
	/**
	 * Transport (10)
	 */
	TRANSPORT,
	/**
	 * System (OS, Database, Vendor localized, etc.) (11) 
	 */
	SYSTEM,
	/**
	 * Generic Message Handling (12)
	 */
	GENERIC;
	
	/**
	 * The integer value of the code
	 * @return An integer value (e.g. 2 )
	 */
	public int getValue(){
		return ordinal();
	}
	
	/**
	 * Returns a SIFErrorCategory enum value for the given int value
	 * @param value The int value to use for searching the enum class
	 * @return The appropriate matching SIFErrorCategory instance or SIFErrorCategory.UNKNOWN
	 */
	public static SIFErrorCategory lookup( Integer value )
	{
		if( value != null ){
			for( SIFErrorCategory cat : values() ){
				if( cat.getValue() == value.intValue() ){
					return cat;
				}
			}
		}
		return SIFErrorCategory.UNKNOWN;
	}
	
}
