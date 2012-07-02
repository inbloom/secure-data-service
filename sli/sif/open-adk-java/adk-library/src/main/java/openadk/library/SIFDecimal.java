//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.math.BigDecimal;

public class SIFDecimal extends SIFSimpleType<BigDecimal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6588431543483622604L;

	public SIFDecimal(BigDecimal value) {
		super(value);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SIFTypeConverter<BigDecimal> getTypeConverter() {
		return SIFTypeConverters.DECIMAL;
	}
}
