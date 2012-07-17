//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

/**
 * Represents the SifElementType of an ADK SDO object
 *
 */
public enum ADKElementType {
	
	/**
	 * The base class for most SDO objects
	 */
	SIFEELEMENT,
	/**
	 * An SDO object that has a defined primary key for it's children
	 */
	SIFKEYEDELEMENT,
	/**
	 * A repeatable element list
	 */
	SIFLIST,
	/**
	 * A repeatable element list that has a key and thus supports the SIF
	 * "Action" flag
	 */
	SIFACTIONLIST,
	
	/**
	 * This object inherits from SIFDataObject 
	 */
	SIFDATAOBJECT,
}
