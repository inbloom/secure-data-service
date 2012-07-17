//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 * Wraps a Java <c>Boolean<c> Type in an immutable instance that can be used
 * to construct a {@link openadk.library.SIFSimpleType} to represent 
 * properties in SIF data objects.
 * 
 * @author Andrew Elmhorst
 * @version 2.0
 */
public class SIFBoolean extends SIFSimpleType<Boolean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4500143680373235993L;

	/**
	 * Creates in instance of SIFBoolean
	 * @param value
	 */
	public SIFBoolean(Boolean value) {
		super(value);
	}
	
	@Override
	public SIFTypeConverter<Boolean> getTypeConverter() {
		return SIFTypeConverters.BOOLEAN;
	}

}
