//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.Calendar;
import java.util.Date;

/**
 * Wraps a Java <c>Calendar</c> in an immutable instance that can be used
 * to construct a {@link openadk.library.SIFSimpleType} to
 * represent properties in SIF data objects
 * 
 * @author Eric Petersen
 * @version 1.0
 */
public class SIFDate extends SIFSimpleType<Calendar> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7011547686908878340L;

	/**
	 * Creates an instance of SIFDate that wraps the specified Calendar value
	 * @param value The Calendar value to wrap in this instance
	 */
	public SIFDate( Calendar value) {
		super( value );
	}
	
	/**
	 * @param value The Date value to wrap. This value will be converted
	 * and stored as a calendar
	 */
	public SIFDate( Date value ){
		super( getCalendar( value ) );
	}
	
	/**
	 * This constructor is deprecated and will eventually be removed from the ADK.
	 * It is kept here for compatibility with the 1.5 version of the ADK.
	 * If a ParseException occurs, it will be converted to a NumberFormatException so 
	 * that the String constructor doesn't change its semantics.
	 * @param value The SIF 1.5 formatted date to wrap in the format "yyyyMMdd"
	 * @deprecated Since dates can be represented as strings in different formats
	 * across SIF Versions, please use SIFFormatter to create dates from strings and
	 * use the {@link #SIFDate(Calendar)} constructor.
 	 * @throws NumberFormatException if the value being set cannot be parsed into
	 * the datatype being stored for this field
	 */
	public SIFDate( String value )  {
		super( parseSIF1xDate( value ) );
	}
	
	/**
	 * Creates a Calendar from a Date
	 * @param value
	 * @return
	 */
	private static Calendar getCalendar( Date value )
	{
		Calendar c = Calendar.getInstance();
        c.clear();
        c.setTime( value );
        return c;
	}
	
	/**
	 * Gets the date as a <code>java.util.Date</code> object
	 * @return
	 */
	public Date getDate(){
		return getValue().getTime();
	}
	
	
	/**
	 * Parse a SIFDate string into a Calendar instance 
	 * 
	 * @param yyyyMMdd The string representation of the date in the format "yyyyMMdd"
	 * @return a calendar instance
	 * @throws NumberFormatException If the date cannot be parsed
	 */
	public static Calendar parseSIF1xDate( String yyyyMMdd )
	{
		return SIFDTD.SIF_1X_FORMATTER.toDate( yyyyMMdd );
	}
	
	/**
	 * Parse a SIFDate string, using the appropriate format for the specified version of SIF 
	 * @param value The string value to parse into a date
	 * @param version The SIFVersion to use for parsing the string
	 * @return a Calendar instance
	 * @throws NumberFormatException If the date cannot be parsed
	 */
	public static Calendar parseSIFDateString( String value, SIFVersion version ) 
	{
		if( version.compareTo( SIFVersion.SIF20 ) >= 0 ){
			return SIFDTD.SIF_2X_FORMATTER.toDate( value );
		} else {
			return SIFDTD.SIF_1X_FORMATTER.toDate( value );
		}
	}
	
	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFSimpleType#getTypeConverter()
	 */
	@Override
	public SIFTypeConverter<Calendar> getTypeConverter() {
		return SIFTypeConverters.DATE;
	}


}
