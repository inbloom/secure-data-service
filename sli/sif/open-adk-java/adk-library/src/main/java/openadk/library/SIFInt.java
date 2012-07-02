//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Wraps a Java <c>Integer<c> Type in an immutable instance that can be used
 * to construct a {@link openadk.library.SIFSimpleType} to represent 
 * properties in SIF data objects.
 * 
 * @author Andrew Elmhorst
 * @version 2.0
 */
public class SIFInt extends SIFSimpleType<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2741327258184937701L;

	/**
	 * Creates an instance of SIFInt
	 * @param value
	 */
	public SIFInt( Integer value )
	{
		super( value );
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFSimpleType#getTypeConverter()
	 */
	@Override
	public SIFTypeConverter<Integer> getTypeConverter() {
		return SIFTypeConverters.INT;
	}

}
