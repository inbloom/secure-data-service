//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Thrown when the ADK attempts to parse a SIF XML Value into a SIF SimpleType
 * @author Andrew Elmhorst
 *
 */
public class ADKTypeParseException extends ADKParsingException {

	/**
	 * @param msg
	 * @param zone
	 */
	public ADKTypeParseException(String msg, Zone zone) {
		super(msg, zone);
	}

	/**
	 * @param msg
	 * @param zone
	 * @param src
	 */
	public ADKTypeParseException(String msg, Zone zone, Exception src) {
		super(msg, zone, src);
	}

}
