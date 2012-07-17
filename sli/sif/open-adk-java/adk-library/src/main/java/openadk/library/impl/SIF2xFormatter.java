//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import openadk.library.SIFFormatter;

/**
 * Represents a SIFFormatter than can be used to format data to and from
 * SIF 2.x datatypes
 * @author Andrew Elmhorst
 * @version 2.0
 *
 */
public class SIF2xFormatter extends SIFFormatter {

	// TODO: Keep an instance of XMLDataTypeFactory.newInstance().
	// Use it to create new XMLGregorianCalendars... use toXMLFormat() to return the
	// lexical representation

	private DatatypeFactory fXmlDataTypeFactory = null;

	  /**
     * SimpleDateFormat is not a thread-safe object, so we store one copy in each thread.
     */
    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>();
    private static final ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>();
    //private static final ThreadLocal<SimpleDateFormat> tzFormat = new ThreadLocal<SimpleDateFormat>();

    public SIF2xFormatter()
    {
    	fXmlDataTypeFactory = SIFFormatter.getDataTypeFactory();
    }


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toDateString(java.util.Calendar)
	 */
    //FIXME Stephen Miller changed this for the same reason toTimeString was broken
	public String toDateString( Calendar date ) {
		if( date == null ){
			return null;
		}

//		// TODO: This is still experimental
//		XMLGregorianCalendar calendar = fXmlDataTypeFactory.newXMLGregorianCalendar( (GregorianCalendar) date );

		//OLD METHOD
//		Date rawDate = date.getTime();
//		return getDateFormat().format( rawDate );
		
		//NEW METHOD
		//"yyyy-MM-dd"
		DecimalFormat f2 = new DecimalFormat("00");
		DecimalFormat f4 = new DecimalFormat("0000");
		
		String year = f4.format( date.get(Calendar.YEAR));
		String month = f2.format( date.get(Calendar.MONTH) + 1 );
		String day = f2.format( date.get( Calendar.DAY_OF_MONTH) );
		
		return year + "-" + month + "-" + day;
	}


	public String toDateTimeString( Calendar date ) {
		if( date == null ){
			return null;
		}

		StringBuilder buf = new StringBuilder();
		buf.append( toDateString( date ) );
		buf.append( "T" );
		buf.append( toTimeString( date ) );


		TimeZone zone = date.getTimeZone();
		int offset = zone.getRawOffset() + zone.getDSTSavings();
		int hourOffset = offset / 3600000;
		int minuteOffset = (offset - (hourOffset * 3600000)) / 60000;

		
		if ( offset == 0 ) {
			buf.append("Z");
		} else {
			if( offset > 0 ){
				buf.append( "+" );
			} else if ( offset < 0) {
				buf.append( "-" );
			}
			
			DecimalFormat f = new DecimalFormat("00");
			buf.append( f.format( Math.abs( hourOffset ) ) );
			buf.append( ':' );
			buf.append( f.format( Math.abs( minuteOffset ) ) );			
		}

		return buf.toString();
	}


	//FIXME I changed this to work properly across time zones.  This should show up in the unit tests...
	public String toTimeString( Calendar date ) {
//		OLD METHOD
//		if( date == null ){
//			return null;
//		}
//		Date rawDate = date.getTime();
//		return getTimeFormat().format( rawDate );
		
		//NEW METHOD
		if ( date == null ) {
			return null;
		}
		
		DecimalFormat f = new DecimalFormat("00");
		String hours = f.format(date.get(Calendar.HOUR_OF_DAY));
		String minutes = f.format(date.get(Calendar.MINUTE));
		String seconds = f.format(date.get(Calendar.SECOND));
		
		return hours + ":" + minutes + ":" + seconds;
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toString(java.lang.Boolean)
	 */
	public String toString(Boolean boolValue) {
		if( boolValue == null ){
			return null;
		}
		return String.valueOf( boolValue );
	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toDate(java.lang.String)
	 */
	public Calendar toDate(String value) {
		if( value == null || value.length() == 0 ){
			return null;
		}

		// TODO: This is still experimental
//		XMLGregorianCalendar xgc = fXmlDataTypeFactory.newXMLGregorianCalendar( value );
//		return xgc.toGregorianCalendar();


		try {
			Date parsedDate = getDateFormat().parse( value );
			Calendar retValue = Calendar.getInstance();
	        retValue.clear();
	        retValue.setTime( parsedDate );
	        return retValue;
		} catch( ParseException parseEx ){
			throw new NumberFormatException( "Error parsing SIF 2.x formatted Date:'" + value + "'. " + parseEx.toString() );
		}

	}

	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toBoolean(java.lang.String)
	 */
	public Boolean toBoolean(String value ) {
		if( value == null ){
			return null;
		}
		if( value.length() == 1 ){
			return value.equals( "1" );
		}

		if( value.equalsIgnoreCase( "true" ) ){
			return true;
		} else if( value.equalsIgnoreCase( "false" ) ){
			return false;
		} else if( value.equalsIgnoreCase( "yes" ) ){
			return true;
		} else if( value.equalsIgnoreCase( "no" ) ){
			return false;
		}

		throw new IllegalArgumentException( "Value '" + value + "' cannot be parsed into a Boolean" );

	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toDuration(java.lang.String)
	 */
	@Override
	public Duration toDuration(String xmlValue) {
		return fXmlDataTypeFactory.newDuration( xmlValue );
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.SIFFormatter#toString(javax.xml.datatype.Duration)
	 */
	@Override
	public String toString(Duration d) {
		if( d == null ){
			return null;
		}
		return d.toString();
	}

	/**
	 * Returns true if the formatter supports writing the xsi:nil attribute.
	 * If the return value is false, SIFWriter will write an empty string
	 * @return true if the formatter supports writing the xsi:nil attribute
	 */
	public boolean supportsNamespaces(){
		return true;
	}

	 /**
     * SimpleDateFormat is not thread-safe, so we use a ThreadLocal to store one copy
     * on each thread.  Call this method to get a copy of the SimpleDateFormat used
     * to turn java.util.Date into a String formatted for SIF.
     *
     * @return A SimpleDateFormat that can turn a java.util.Date into an 8 character date in the SIF format.
     */
    private synchronized SimpleDateFormat getDateFormat() {
        SimpleDateFormat retval = dateFormat.get();
        if (retval == null) {
            retval = new SimpleDateFormat("yyyy-MM-dd");
            retval.setLenient( false );
            dateFormat.set(retval);
        }
        return retval;
    }

    /**
     * Returns a Time format for SIF 2.x
     * @return
     */
    private synchronized SimpleDateFormat getTimeFormat() {
        SimpleDateFormat retval = timeFormat.get();
        if (retval == null) {
            retval = new SimpleDateFormat("HH:mm:ss");
            retval.setLenient( false );
            timeFormat.set(retval);
        }
        return retval;
    }

    @Override
	public String toString(BigDecimal decimalValue) {
		if( decimalValue == null ){
			return null;
		}
		return decimalValue.toPlainString();
	}

	@Override
	public BigDecimal toDecimal(String decimalValue) {
		if( decimalValue == null || decimalValue.length() == 0 ){
			return null;
		}
		return new BigDecimal(decimalValue);
	}

	@Override
	public Calendar toTime(String xmlValue) {

		// TODO: This is still experimental
//		XMLGregorianCalendar xgc = fXmlDataTypeFactory.newXMLGregorianCalendar( xmlValue );
//		return xgc.toGregorianCalendar();

		try{
			String timePortion = xmlValue.substring( 0, 8 );
			Date time = getTimeFormat().parse( timePortion );

			Calendar returnValue = Calendar.getInstance();
			returnValue.clear();
			returnValue.setTime( time );

			TimeZone timeZone = extractTimeZone( xmlValue, 8 );
			if( timeZone != null ) {
				int offset = TimeZone.getDefault().getRawOffset() - timeZone.getRawOffset();
				returnValue.add( Calendar.MILLISECOND, offset );
			}

			return returnValue;

		} catch( ParseException parseEx ){
			throw new NumberFormatException( "Error parsing SIF 2.x formatted Date:'" + xmlValue + "'. " + parseEx.toString() );
		} catch( StringIndexOutOfBoundsException parseEx ){
			throw new NumberFormatException( "Error parsing SIF 2.x formatted Date:'" + xmlValue + "'. " + parseEx.toString() );
		}


	}

	@Override
	public Calendar toDateTime(String xmlValue) {
		XMLGregorianCalendar xgc = fXmlDataTypeFactory.newXMLGregorianCalendar( xmlValue );
		Calendar cal = xgc.toGregorianCalendar();
		cal.get(Calendar.HOUR_OF_DAY);
		cal.setTimeZone(TimeZone.getDefault());
		cal.get(Calendar.HOUR_OF_DAY);
		return cal;
	}


	private TimeZone extractTimeZone(String timeZoneString, int startPos) {
		TimeZone timeZone = null;
		if( timeZoneString.endsWith( "Z" ) ){
			timeZone = TimeZone.getTimeZone( "GMT" );
		} else if ( timeZoneString.length() == startPos + 6 ) {

			String tz = "GMT" + timeZoneString.substring( startPos );
			timeZone = TimeZone.getTimeZone( tz );
//				int hoursOffset =Integer.parseInt( xmlValue.substring( 20, 22 ) );
//				int minutesOffset = Integer.parseInt( xmlValue.substring( 23, 25 ) );
//				int millisecondsOffset = hoursOffset * 60 * 60 * 1000 + minutesOffset * 60 * 1000;
//				if( xmlValue.substring( 19, 20 ).equals( "-" ) )
//				{
//					millisecondsOffset = 0 - millisecondsOffset;
//				}
//
//				TimeZone test = TimeZone.getDefault();
//				if( test.getRawOffset() == millisecondsOffset ){
//					timeZone = test;
//				} else {
//					String[] availableIds = TimeZone.getAvailableIDs( millisecondsOffset );
//					if( availableIds != null && availableIds.length > 0 ){
//						timeZone = TimeZone.getTimeZone( availableIds[0] );
//					}
//				}
		}
		if( timeZone == null ) {
			timeZone = TimeZone.getDefault();
		}
		return timeZone;
	}


	/**
	 * Converts a Java <c>int</c> value to a SIF int value
	 *
	 * @param intValue
	 * @return The int formatted as a string, using SIF formatting requirements
	 */
	public String toString(Integer intValue) {
		if( intValue == null ){
			return null;
		}
		return String.valueOf( intValue );
	}


}

