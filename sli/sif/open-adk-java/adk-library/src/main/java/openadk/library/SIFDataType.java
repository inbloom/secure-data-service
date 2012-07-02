//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * An enumeration of XSD datatypes supported by the ADK
 *
 */
public enum SIFDataType {
	/**
	 * Represents the XSD <c>boolean</c> datatype
	 */
	BOOLEAN( SIFTypeConverters.BOOLEAN ),
	/**
	 * Represents the XSD <c>int</c> and <c>uint</c> datatypes
	 */
	INT( SIFTypeConverters.INT ),
	/**
	 * Represents the XSD <c>long</c> datatype
	 */
	LONG( SIFTypeConverters.LONG ),
	/**
	 * Represents the XSD <c>string</c>, <c>token</c>, and <c>normalizedString</c> datatypes
	 */
	STRING( SIFTypeConverters.STRING ),
	/**
	 * Represents the XSD <c>date</c> datatype
	 */
	DATE( SIFTypeConverters.DATE ),
	/**
	 * Represents the XSD <c>datetime</c> datatype
	 */
	DATETIME( SIFTypeConverters.DATETIME ),

	/**
	 * Represents the XSD <c>float</c> datatype
	 */
	FLOAT( SIFTypeConverters.FLOAT ),

	/**
	 * Represents the XSD <c>time</c> datatype
	 */
	TIME( SIFTypeConverters.TIME ),

	/**
	 * Represents the XSD <c>decimal</c> datatype
	 */
	DECIMAL( SIFTypeConverters.DECIMAL ),

	/**
	 * Represents the XSD <c>duration</c> datatype
	 */
	DURATION( SIFTypeConverters.DURATION );

	private SIFTypeConverter fConverter;

	private SIFDataType( SIFTypeConverter converter){
		fConverter = converter;
	}

	public SIFTypeConverter getConverter(){
		return fConverter;
	}
}
