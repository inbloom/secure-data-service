//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.Serializable;


/**
 * Wraps a Java Simple Type in an immutable instance that can be used
 * to construct SimpleFields that represent properties in SIF data objects.
 * 
 * @author Andrew Elmhorst
 * @version 2.0
 * @param <T> The datatype that the SIFSimpleType wraps, such as
 * Integer, Calendar, String, etc.
 */
public abstract class SIFSimpleType<T> implements Serializable {

	/**
	 * The java runtime value that this SIFSimpleType wraps. This value must only
	 * be set from a constructor in order to keep the type immutable
	 */
	protected final T fValue;
	
	/**
	 * Creates an instance of this type
	 * @param value
	 */
	protected SIFSimpleType( T value )
	{
		fValue = value;
	}
	
	/**
	 * @return The runtime native type that this SIFSimpleType wraps
	 */
	public T getValue()
	{
		return fValue;
	}
	
	/**
	 * Create a SimpleField from the value that this type represents
	 * @param parent The Element that will be the parent of the field
	 * @param id The ElementDef representing the field
	 * @return A SimpleField instance initialized with the proper value
	 */
	public SimpleField<T> createField( SIFElement parent, ElementDef id )
	{
		return new SimpleField<T>( parent, id, this );
	}
	
	/**
	 * Returns the string representation of this data field, using the 
	 * specified formatter
	 * @param formatter
	 * @return The XML String representation of this value
	 */
	public String toString( SIFFormatter formatter )
	{
		return getTypeConverter().toString( formatter, this );
	}
	
	/**
	 * Returns a string in the format for the designated version
	 * @param version The SIF Version to render this value in
	 * @return a string in the proper format for the specified version of SIF
	 */
	public String toString( SIFVersion version ) {
		return toString( ADK.DTD().getFormatter( version ) );
	}
	
	/** 
	 * Returns the text representation of this value in 1.x format by default.
	 * To change the default formatter used for rendering text values, call
	 * {@link ADK#setTextFormatter(SIFFormatter)}
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return toString( ADK.getTextFormatter() );
	}
	
	/**
	 * Returns a type converter that can be used to create an instance of the 
	 * datatype from a string
	 * @return The TypeConverter that is used for this element
	 */
	public abstract SIFTypeConverter<T> getTypeConverter();
	
	
	/**
	 * @return A value from the {@link SIFDataType} enum
	 */
	public SIFDataType getDataType(){
		return getTypeConverter().getDataType();
	}
	
	/**
	 * Returns a value from the <c>java.sql.types</c> class
	 * @see java.sql.Types
	 * @return A value from the <c>java.sql.types</c> class
	 */
	public int getSQLType(){
		return getTypeConverter().getSQLType();
	}
	
	/**
	 * Should the XML writer encode the value that this datatype produces?
	 * Encoding is usually only necessary for string-based datatypes
	 * @return True if this datatype should not be encoded when writing to XML
	 */
	public boolean isDoNotEncode() {
		return true;
	}
	
	/**
	 * Evaluates the native wrapped value of this object to see if
	 * it equals the value of the compared object
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
	    if( this == o )
	    {
	        return true;
	    }
	    if ((o != null) && (o.getClass().equals(this.getClass())))
	    {
	        SIFSimpleType compared = (SIFSimpleType)o;
	        if( this.fValue == null )
	        {
	            return compared.fValue == null;
	        }
	        return this.fValue.equals( compared.fValue );
	    }
	    return false;
	 }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
	    if( fValue == null )
	    {
	        return -1;
	    }
	    else
	    {
	        return fValue.hashCode();
	    }
	 }


	
}
