//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.profiler.api;

import java.sql.*;

/**
 */
public class MetricCalc
{
	/**
	 * 	Calculate the Overhead components for a benchmark
	 * 
	 * 	@param session A ProfilerSession
	 */
	public Times analyze( ProfilerSession session, String benchmarkCode, short objType )
		throws SQLException
	{
		if( session == null )
			throw new IllegalArgumentException( "Session cannot be null" );
		if( benchmarkCode == null || benchmarkCode.length() < 4 )
			throw new IllegalArgumentException( "Invalid benchmark code (must be at least 4 characters)" );
		
		short _objType = objType >= 1000 ? objType : ( (short)( objType + 1000 ) );
		
		Times results = new Times();

		//	First character of benchmark code to determine the kinds of Metrics that are 
		//	involved in the calculation. A "Q" is for synchronize/query benchmarks and a
		//	"V" is for SIF_Event benchmarks.
		//
		boolean isSync = benchmarkCode.charAt(0) == 'Q';
		
		//	Calculate application database reads & writes, and application business logic
		MetricQuery query = new MetricQuery( OIDs.SIFAgentLib_DB_READS + ".9999." + _objType );
		results.Component[ Times.APP_DB_READS ] = session.queryElapsedTime( query );
		results.CompCount[ Times.APP_DB_READS ] = session.queryMetricsCount( query ); 
		query = new MetricQuery( OIDs.SIFAgentLib_DB_WRITES + ".9999." + _objType );
		results.Component[ Times.APP_DB_WRITES ] = session.queryElapsedTime( query );
		results.CompCount[ Times.APP_DB_WRITES ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_APP_BUSINESS_LOGIC + _objType );
		results.Component[ Times.APP_BUSINESS_LOGIC ] = session.queryElapsedTime( query );
		results.CompCount[ Times.APP_BUSINESS_LOGIC ] = session.queryMetricsCount(query);
		
		//	Calculate all other database reads & writes
		query = new MetricQuery( OIDs.SIFAgentLib_DB_READS + ".*." + _objType );
		results.Component[ Times.DB_READS ] = session.queryElapsedTime( query );
		results.CompCount[ Times.DB_READS ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_DB_WRITES + ".*." + _objType );
		results.Component[ Times.DB_WRITES ] = session.queryElapsedTime( query );
		results.CompCount[ Times.DB_WRITES ] = session.queryMetricsCount( query );
		
		//	Calculate SIF_Request messaging, SIF_Request processing by the requestor (e.g.
		//	recording the fact that it issued a query), and SIF_Request processing by the 
		//	responder (e.g. evaluating the query conditions)
		query = new MetricQuery( OIDs.ADK_SIFREQUEST_REQUESTOR_MESSAGING + "." + _objType );
		results.Component[ Times.SIF_REQUEST_REQUESTOR_MESSAGING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_REQUEST_REQUESTOR_MESSAGING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.ADK_SIFREQUEST_RESPONDER_MESSAGING + "." + _objType );
		results.Component[ Times.SIF_REQUEST_RESPONDER_MESSAGING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_REQUEST_RESPONDER_MESSAGING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_SIFREQUEST_REQUESTOR_OBJPROC + "." + _objType );
		results.Component[ Times.SIF_REQUEST_REQUESTOR_PROCESSING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_REQUEST_REQUESTOR_PROCESSING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_SIFREQUEST_RESPONDER_OBJPROC + "." + _objType );
		results.Component[ Times.SIF_REQUEST_RESPONDER_PROCESSING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_REQUEST_RESPONDER_PROCESSING ] = session.queryMetricsCount( query );
		
		//	Calculate SIF_Response messaging, SIF_Response processing by the requestor (e.g.
		//	processing and importing results), SIF_Response processing by the responder, and
		//	packetizing and delivery of SIF_Responses by the responder
		query = new MetricQuery( OIDs.ADK_SIFRESPONSE_REQUESTOR_MESSAGING + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_REQUESTOR_MESSAGING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_REQUESTOR_MESSAGING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.ADK_SIFRESPONSE_RESPONDER_MESSAGING + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_RESPONDER_MESSAGING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_RESPONDER_MESSAGING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_SIFRESPONSE_REQUESTOR_OBJPROC + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_REQUESTOR_PROCESSING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_REQUESTOR_PROCESSING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_SIFRESPONSE_RESPONDER_OBJPROC + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_RESPONDER_PROCESSING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_RESPONDER_PROCESSING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.ADK_SIFRESPONSE_PACKETIZING + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_PACKETIZING ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_PACKETIZING ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.ADK_SIFRESPONSE_DELIVERY + "." + _objType );
		results.Component[ Times.SIF_RESPONSE_DELIVERY ] = session.queryElapsedTime( query );
		results.CompCount[ Times.SIF_RESPONSE_DELIVERY ] = session.queryMetricsCount( query );
		
		//	Calculate inbound transformations/mappings by both the ADK and SIFAgentLib
		query = new MetricQuery( OIDs.ADK_INBOUND_TRANSFORMATIONS + "." + _objType );
		results.Component[ Times.INBOUND_TRANSFORMATIONS_LOW ] = session.queryElapsedTime( query );
		results.CompCount[ Times.INBOUND_TRANSFORMATIONS_LOW ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_INBOUND_TRANSFORMATIONS + "." + _objType );
		results.Component[ Times.INBOUND_TRANSFORMATIONS_HIGH ] = session.queryElapsedTime( query );
		results.CompCount[ Times.INBOUND_TRANSFORMATIONS_HIGH ] = session.queryMetricsCount( query );
		
		//	Calculate outbound transformations/mappings by both the ADK and SIFAgentLib
		query = new MetricQuery( OIDs.ADK_OUTBOUND_TRANSFORMATIONS + "." + _objType );
		results.Component[ Times.OUTBOUND_TRANSFORMATIONS_LOW ] = session.queryElapsedTime( query );
		results.CompCount[ Times.OUTBOUND_TRANSFORMATIONS_LOW ] = session.queryMetricsCount( query );
		query = new MetricQuery( OIDs.SIFAgentLib_OUTBOUND_TRANSFORMATIONS + "." + _objType );
		results.Component[ Times.OUTBOUND_TRANSFORMATIONS_HIGH ] = session.queryElapsedTime( query );
		results.CompCount[ Times.OUTBOUND_TRANSFORMATIONS_HIGH ] = session.queryMetricsCount( query );
		
		//	(A) First get application-level overhead
		//	- 55.1.50.objType.53.2999 and .54.2999 represents all application-level database reads and writes
		//	- 55.1.50.objType.61 represents all application-level business logic 
		long A =	results.Component[ Times.APP_DB_READS ] +
				results.Component[ Times.APP_DB_WRITES ] +
				results.Component[ Times.APP_BUSINESS_LOGIC ];
		System.out.println("(A)=" + A);

		//	(B) Next get total of ADK components
		long B =	results.Component[ Times.SIF_REQUEST_REQUESTOR_MESSAGING ] +
				results.Component[ Times.SIF_REQUEST_RESPONDER_MESSAGING ] +
				results.Component[ Times.SIF_RESPONSE_REQUESTOR_MESSAGING ] +
				results.Component[ Times.SIF_RESPONSE_RESPONDER_MESSAGING ] +
				results.Component[ Times.SIF_RESPONSE_DELIVERY ] +
				results.Component[ Times.SIF_EVENT_REPORTER_MESSAGING ] +
				results.Component[ Times.SIF_EVENT_SUBSCRIBER_MESSAGING ];
		System.out.println("(B)="+B);

		//	(C) Get total of of SIFAgentLib components
		long C =	results.Component[ Times.SIF_REQUEST_REQUESTOR_PROCESSING ] +
				results.Component[ Times.SIF_RESPONSE_REQUESTOR_PROCESSING ] +
				results.Component[ Times.SIF_REQUEST_RESPONDER_PROCESSING ] +
				results.Component[ Times.SIF_RESPONSE_RESPONDER_PROCESSING ] +
				results.Component[ Times.SIF_EVENT_REPORTER_PROCESSING ] +
				results.Component[ Times.SIF_EVENT_SUBSCRIBER_PROCESSING ];
		System.out.println("(C)="+C);

		results.ADKOverhead = B-C;
		results.SIFAgentOverhead = C-A-results.ADKOverhead;
		results.AppOverhead = A;

		
		results.Component[ Times.SIF_REQUEST_REQUESTOR_MESSAGING ] =
			results.Component[ Times.SIF_REQUEST_REQUESTOR_MESSAGING ] - results.Component[ Times.SIF_REQUEST_REQUESTOR_PROCESSING ];
		results.Component[ Times.SIF_REQUEST_RESPONDER_MESSAGING ] =
			results.Component[ Times.SIF_REQUEST_RESPONDER_MESSAGING ] - results.Component[ Times.SIF_REQUEST_RESPONDER_PROCESSING ];
		results.Component[ Times.SIF_RESPONSE_REQUESTOR_MESSAGING ] =
			results.Component[ Times.SIF_RESPONSE_REQUESTOR_MESSAGING ] - results.Component[ Times.SIF_RESPONSE_REQUESTOR_PROCESSING ];
		results.Component[ Times.SIF_RESPONSE_RESPONDER_MESSAGING ] =
			results.Component[ Times.SIF_RESPONSE_RESPONDER_MESSAGING ] - results.Component[ Times.SIF_RESPONSE_RESPONDER_PROCESSING ];		
		
		results.Total = 
			( results.Component[ Times.SIF_REQUEST_REQUESTOR_PROCESSING ] ) +
			( results.Component[ Times.SIF_REQUEST_RESPONDER_PROCESSING ] ) +
			( results.Component[ Times.SIF_RESPONSE_REQUESTOR_PROCESSING ] ) +
			( results.Component[ Times.SIF_RESPONSE_RESPONDER_PROCESSING ] ) +
			( results.Component[ Times.SIF_RESPONSE_DELIVERY ] ) +
			( results.Component[ Times.SIF_EVENT_REPORTER_PROCESSING ] ) +
			( results.Component[ Times.SIF_EVENT_SUBSCRIBER_PROCESSING ] );

		results.dump();
		
		return results;
	}
	
	/**
	 * 	Add together the relative times of all Metrics
	 */
	public long sum( Metric[] metrics )
	{
		long result = 0;
	
		if( metrics != null )
		{
			for( int i = 0; i < metrics.length; i++ )
				result += metrics[i].fElapsed;
		}
		
		System.out.println("sum("+metrics.length+")="+result);
		
		return result;
	}
}
