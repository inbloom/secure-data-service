//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.math.BigDecimal;

/**
 * @author Andrew Elmhorst
 *
 */
public class SIFFloat extends SIFSimpleType<Float> {

	public SIFFloat(Float value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SIFTypeConverter<Float> getTypeConverter() {
		return SIFTypeConverters.FLOAT;
	}
}
