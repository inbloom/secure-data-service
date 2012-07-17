//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Calendar;

public class SIFDateTime extends SIFSimpleType<Calendar> {

	public SIFDateTime(Calendar value) {
		super(value);
	}

	@Override
	public SIFTypeConverter<Calendar> getTypeConverter() {
		return SIFTypeConverters.DATETIME;
	}


}
