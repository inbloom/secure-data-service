//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import javax.xml.datatype.Duration;

public class SIFDuration extends SIFSimpleType<Duration> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6588431543483622604L;

	/**
	 * Creates a new SIFDuration to wrapte the Duration value
	 * @param value
	 */
	public SIFDuration(Duration value) {
		super(value);
	}
	
	/**
	 * Creates a new SIFDuration to represent the given amount of time
	 * @param isPositive True if this represents a positive amount of time
	 * @param days The number of days
	 * @param hours The number of hours
	 * @param minutes The number of minutes
	 * @param seconds The number of seconds
	 */
	public SIFDuration(boolean isPositive, int days, int hours, int minutes, int seconds) 
	{
		super( SIFFormatter.getDataTypeFactory().newDurationDayTime( isPositive, days, hours, minutes, seconds ) );
	}
	
	/**
	 * Creates a new SIFDuration to represent the given amount of time
	 * @param hours The number of hours
	 * @param minutes The number of minutes
	 * @param seconds The number of seconds
	 */
	public SIFDuration( int hours, int minutes, int seconds ) 
	{
		super( SIFFormatter.getDataTypeFactory().newDurationDayTime( true, 0, hours, minutes, seconds ) );
	}

	
	@Override
	public SIFTypeConverter<Duration> getTypeConverter() {
		// TODO Auto-generated method stub
		return SIFTypeConverters.DURATION;
	}

}
