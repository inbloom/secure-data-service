//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 	
 */
public class Times
{
	//
	//	Indices to the Components array, which stores the times for each component
	//	of the total time elapsed for these stats.
	//
	public static final byte SIF_REQUEST_REQUESTOR_MESSAGING = 		0;
	public static final byte SIF_REQUEST_RESPONDER_MESSAGING = 		1;
	public static final byte SIF_RESPONSE_REQUESTOR_MESSAGING =		2;
	public static final byte SIF_RESPONSE_RESPONDER_MESSAGING = 	3;
	public static final byte SIF_REQUEST_REQUESTOR_PROCESSING = 	4;
	public static final byte SIF_REQUEST_RESPONDER_PROCESSING = 	5;
	public static final byte SIF_RESPONSE_REQUESTOR_PROCESSING = 	6;
	public static final byte SIF_RESPONSE_RESPONDER_PROCESSING = 	7;
	public static final byte SIF_EVENT_REPORTER_MESSAGING = 		8;
	public static final byte SIF_EVENT_SUBSCRIBER_MESSAGING = 		9;
	public static final byte SIF_EVENT_REPORTER_PROCESSING = 		10;
	public static final byte SIF_EVENT_SUBSCRIBER_PROCESSING = 		11;
	public static final byte INBOUND_TRANSFORMATIONS_LOW = 			12;
	public static final byte INBOUND_TRANSFORMATIONS_HIGH =			13;
	public static final byte OUTBOUND_TRANSFORMATIONS_LOW = 		14;
	public static final byte OUTBOUND_TRANSFORMATIONS_HIGH= 		15;
	public static final byte DB_READS = 							16;
	public static final byte DB_WRITES = 							17;
	public static final byte SIF_RESPONSE_PACKETIZING = 			18;
	public static final byte SIF_RESPONSE_DELIVERY = 				19;
	public static final byte APP_BUSINESS_LOGIC = 					20;
	public static final byte APP_DB_READS = 						21;
	public static final byte APP_DB_WRITES = 						22;
	private static final byte COMPONENTS = 						23;

	private static final String[] sIndexNames = {
		"SIF_REQUEST_REQUESTOR_MESSAGING" ,
		"SIF_REQUEST_RESPONDER_MESSAGING" ,
		"SIF_RESPONSE_REQUESTOR_MESSAGING" ,
		"SIF_RESPONSE_RESPONDER_MESSAGING" ,
		"SIF_REQUEST_REQUESTOR_PROCESSING" ,
		"SIF_REQUEST_RESPONDER_PROCESSING" ,
		"SIF_RESPONSE_REQUESTOR_PROCESSING" ,
		"SIF_RESPONSE_RESPONDER_PROCESSING" ,
		"SIF_EVENT_REPORTER_MESSAGING" ,
		"SIF_EVENT_SUBSCRIBER_MESSAGING" ,
		"SIF_EVENT_REPORTER_PROCESSING" ,
		"SIF_EVENT_SUBSCRIBER_PROCESSING" ,
		"INBOUND_TRANSFORMATIONS_LOW" ,
		"INBOUND_TRANSFORMATIONS_HIGH" ,
		"OUTBOUND_TRANSFORMATIONS_LOW",
		"OUTBOUND_TRANSFORMATIONS_HIGH",
		"DB_READS",
		"DB_WRITES",
		"SIF_RESPONSE_PACKETIZING",
		"SIF_RESPONSE_DELIVERY",
		"APP_BUSINESS_LOGIC",
		"APP_DB_READS",
		"APP_DB_WRITES",
	};
	
	/**
	 * 	Total elapsed time
	 */
	public long Total;
	
	/**
	 * 	Amount of total time allocated by the ADK
	 */
	public long ADKOverhead;
	
	/**
	 * 	Amount of total time allocated by the SIFAgent Library
	 */
	public long SIFAgentOverhead;
	
	/**
	 * 	Amount of total time consumed by agent-specific business logic and database access
	 */
	public long AppOverhead;
	
	/**
	 * 	Amount of total time consumed by the ZIS
	 */
	public long ZISOverhead;
	
	/**
	 * 	Amount of total time consumed by the ADK, SIFAgent Library, and SIF
	 */
	public long SIFOverhead;
	
	/**
	 * 	Times broken down by component
	 */
	public long[] Component = new long[ COMPONENTS ];
	
	/**
	 * 	Number of metrics used in the calculation of each Component
	 */
	public int[] CompCount = new int[ COMPONENTS ];
	
	private static SimpleDateFormat sFmt = (SimpleDateFormat)DateFormat.getTimeInstance();
	static {
		sFmt.applyPattern( "HH:mm:ss.SSS" );
	}
	
	/**
	 * 	Gets the percentage of total time represented by ADK Overhead
	 */
	public double getADKOverheadPct() {
		return ( (double)ADKOverhead / (double)Total );
	}
	
	/**
	 * 	Gets the ADK Overhead value represented as a time
	 */
	public  String getADKOverheadStr() {
		return format( ADKOverhead );
	}
	
	/**
	 * 	Gets the percentage of total time represented by SIFAgentLib Overhead
	 */
	public double getSIFAgentOverheadPct() {
		return ( (double)SIFAgentOverhead / (double)Total );
	}

	/**
	 * 	Gets the SIFAgentLib Overhead value represented as a time
	 */
	public  String getSIFAgentOverheadStr() {
		return format( SIFAgentOverhead );
	}
	
	/**
	 * 	Gets the percentage of total time represented by SIFAgentLib Overhead
	 */
	public double getAppOverheadPct() {
		return ( (double)AppOverhead / (double)Total );
	}

	/**
	 * 	Gets the SIFAgentLib Overhead value represented as a time
	 */
	public  String getAppOverheadStr() {
		return format( AppOverhead );
	}

	/**
	 * 	Gets the percentage of total time represented by ZIS Overhead
	 */
	public double getZISOverheadPct() {
		return ( (double)ZISOverhead / (double)Total );
	}

	/**
	 * 	Gets the ZIS Overhead value represented as a time
	 */
	public  String getZISOverheadStr() {
		return format( ZISOverhead );
	}
	
	/**
	 * 	Gets the percentage of total time represented by SIF Overhead
	 */
	public double getSIFOverheadPct() {
		return ( (double)SIFOverhead / (double)Total );
	}

	/**
	 * 	Gets the SIF Overhead value represented as a time
	 */
	public  String getSIFOverheadStr() {
		return format( SIFOverhead );
	}
	
	/**
	 * 	Gets the total value represented as a time
	 */
	public  String getTotalStr() {
		return format( Total );
	}

	/**
	 * 	Gets the average elapsed time of the specified component
	 */
	public long getComponentAvg( byte component ) {
		return Component[ component ] == 0 ? 0 : Component[ component ] / CompCount[ component ];
	}
	
	public String getCompAvgStr( byte component ) {
		return format( getComponentAvg( component ) );
	}
	
	public int getTotalCompCount() {
		int l = 0;
		for( int i = 0; i < CompCount.length; i++ )
			l += CompCount[i];
		return l;
	}
	
	public String getTotalCompCountStr() {
		return String.valueOf( getTotalCompCount() );
	}
	
	public int getComponentsSum() {
		int l = 0;
		for( int i = 0; i < Component.length; i++ )
			l += Component[i];
		return l;
	}
	
	/**
	 * 	Gets the percentage of total time represented by the specified component
	 */
	public double getComponentPct( byte component ) {
		return ( (double)Component[ component ] / (double)Total );
	}

	/**
	 * 	Gets the percentage of a sub-total represented by the specified component
	 */
	public double getComponentPct( byte component, long subtotal ) {
		return ( (double)Component[ component ] / (double)subtotal );
	}
	
	/**
	 * 	Gets the percentage of the total
	 */
	public double getTotalPercentage( long total ) {
		return ( (double)Total / (double)total );
	}

	/**
	 * 	Gets the specified component value represented as a time
	 */
	public  String getComponentStr( byte component ) {
		return format( Component[ component ] );
	}
	
	/**
	 * 	Gets the specified component count as a string
	 */
	public String getCompCountStr( byte component ) {
		return String.valueOf( CompCount[ component ] );
	}
	
	/**
	 * 	Format a relative millisecond time value as "hh:mm:ss.ms [+n days]". For example,
	 * 	a value of 2500 is returned as "00:00:02.0500", and a value of 172799999 is returned
	 * 	as "23:59:59.0999 +1 days"<p>
	 * 
	 * 	@param value Number of millseconds
	 * 
	 * 	@return A relative time in the form "hh:mm:ss.ms [+n days]
	 */
	public synchronized static String format( long value ) 
	{
		if( value < 1000 )
			return "00:00:00." + value;
		
		int days = (int)( value / 86400000 );
		
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, (int)value );

		return sFmt.format( cal.getTime() ) + ( days == 0 ? "" : ( " +" + days + " days" ) );
	}
	
	/**
	 * 	Add the specified times to the values in this Times object
	 */
	public void combineWith( Times source )
	{
		this.Total += source.Total;
		this.ADKOverhead += source.ADKOverhead;
		this.AppOverhead += source.AppOverhead;
		this.SIFAgentOverhead += source.SIFAgentOverhead;
		this.SIFOverhead += source.SIFAgentOverhead;
		this.ZISOverhead += source.ZISOverhead;
		
		for( int i = 0; i < Component.length; i++ ) 
			this.Component[i] += source.Component[i];
		for( int i = 0; i < CompCount.length; i++ ) 
			this.CompCount[i] += source.CompCount[i];
	}
	
	public void dump() {
		for( int i = 0; i < Component.length; i++ ) {
			System.out.println( "[" + i + "] " + sIndexNames[i] + ": " + Component[i] );
		}
	}
	public static void main( String[] args ) {
		System.out.println( format( Long.parseLong( args[0] ) ) );
	}
}
