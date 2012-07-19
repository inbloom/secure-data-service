//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Represents a set of type converters that can use SIFFormatter instances to convert
 * Strings into SIFSimpleType instances
 *
 * @author Andrew Elmhorst
 * @version 2.0
 */
/**
 * @author Andrew
 *
 * @param <T> The datatype that the SIFSimpleType wraps, such as Integer, Calendar, String, etc.
 */
public abstract class SIFTypeConverter<T> {

	//public final ElementDef TEXT_VALUE = new ElementDefImpl(null, "TEXT_VALUE", "", 9999, "", ElementDefImpl.FD_ELEMENT_VALUE, SIFVersion.SIF11, this );

	/**
	 * Parses the given XML string value to into a SIFSimpleType instance
	 * @param formatter The formatter to use for the specific version of SIF
	 * being parsed
	 * @param xmlValue The XML string value
	 * @return A SIFSimpleType instance
	 * @throws ADKTypeParseException TODO
	 */
	public abstract SIFSimpleType<T> parse( SIFFormatter formatter, String xmlValue ) throws ADKTypeParseException;

	/**
	 * Returns a SIFSimpleType that wraps the specified native value
	 * @param nativeType The native Java type for this datatype, such as a String
	 * or Calendar. If <code>null</code> is passed in, a SIFSimpleType with a <code>null</code>
	 * value is returned.
	 * @return The appropriate SIFSimpleType for this datatype
	 */
	public abstract SIFSimpleType<T> getSIFSimpleType( Object nativeType );

	/**
	 * Converts the given SIFSimpleType instance to an XML string value
	 * @param formatter The formatter to use for the specific version of SIF being written to
	 * @param value a SIFSimpleType instance
	 * @return a String representing the XML payload used for this version of SIF. All types
	 * are nullable in SIF, so the resulting value could be null
	 */
	public abstract String toString( SIFFormatter formatter, SIFSimpleType<T> value );

	/**
	 * @return A value from the {@link SIFDataType} enum
	 */
	public abstract SIFDataType getDataType();

	/**
	 * Returns a value from the <c>java.sql.types</c> class
	 * @see java.sql.Types
	 * @return A value from the <c>java.sql.types</c> class
	 */
	public abstract int getSQLType();

	/**
	 * Parses the XML String value and returns the proper SimpleField subclass to hold the
	 * element value
	 * @param parent The parent SIFElement of the field
	 * @param id The metadata definition of the field
	 * @param formatter the formatter to use for the specific version of SIF in use
	 * @param xmlValue a String representing the XML payload used for this version of SIF
	 * @return a SimpleField initialized with the proper value
	 * @throws ADKTypeParseException TODO
	 */
	public SimpleField parseField(
			SIFElement parent,
			ElementDef id,
			SIFFormatter formatter,
			String xmlValue)
	throws ADKTypeParseException
	{
		return parse( formatter, xmlValue ).createField( parent, id );
	}






}
