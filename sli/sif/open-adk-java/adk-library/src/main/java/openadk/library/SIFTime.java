//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Calendar;

public class SIFTime extends SIFSimpleType<Calendar> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 991376505680342099L;
	/**
	 * Creates an instance of SIFTime that wraps the specified Calendar value
	 * @param value The Calendar value to wrap in this instance
	 */
	public SIFTime( Calendar value) {
		super( value );
	}

	@Override
	public SIFTypeConverter<Calendar> getTypeConverter() {
		return SIFTypeConverters.TIME;
	}
}
