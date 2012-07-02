//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * The set of allowable actions in a SIF Event
 * @author Andrew Elmhorst
 * @version ADK 2.0
 *
 */
public enum EventAction {

	/**
	 *  Action code indicating the data was <i>added</i> by the event publisher
	 */
	ADD ((byte)1),
	
	/**
	 *  Action code indicating the data was <i>changed</i> by the event publisher
	 */
	CHANGE ((byte)2),
	
	/**
	 * Action code indicating the data was <i>deleted</i> by the event publisher
	 */
	DELETE ((byte)3),
	
	/**
	 * Action code is undefined
	 */
	UNDEFINED ((byte)0);
	
	private byte fCode;
	private EventAction( byte byteCode ){
		fCode = byteCode;
	}
	
	/**
	 * Returns an integral value for each EventAction type
	 * @return ADD = 1, CHANGE = 2, DELETE=3, UNDEFINED=0
	 */
	public byte value()
	{
		return fCode;
	}
	
}
