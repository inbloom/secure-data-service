//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Wraps a Java <c>String</c> in an immutable instance that can be used
 * to construct a {@link openadk.library.SIFSimpleType} to
 * represent properties in SIF data objects
 * @author Andrew Elmhorst
 * @version 2.0
 *
 */
public class SIFString extends SIFSimpleType<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 991376505680342099L;

	/**
	 * Constructs a SIFToken with a String value
	 * @param value
	 */
	public SIFString( String value) {
		super( value );
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFSimpleType#getTypeConverter()
	 */
	@Override
	public SIFTypeConverter<String> getTypeConverter() {
		return SIFTypeConverters.STRING;
	}
	
	/**
	 * This type does require encoding and returns false
	 * @see SIFSimpleType#isDoNotEncode()
	 */
	public boolean isDoNotEncode(){
		return false;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFSimpleType#toString()
	 */
	@Override
	public String toString()
	{
		return fValue;
	}
	
	/**
	 * Evaluates the string value to determine if it
	 * equals the string value of this object
	 * @param value the value to compare
	 * @return true if the specified string equals the 
	 * value of this object
	 */
	public boolean valueEquals(String value) {
		if( value == null ){
			return fValue == null;
		} else {
			return value.equals( fValue );
		}
	 }

}
