//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.util.List;
import java.util.Vector;

import openadk.library.impl.ADKUtils;
import openadk.library.impl.SMBHelper;
import openadk.library.impl.TrackQueryResultsImpl;

/**
 *  Synchronously issues one or more SIF Requests to a zone or topic and returns
 *  the results to the calling thread as they are received.<p>
 *
 *  TrackQueryResults differs from normal (asynchonous) SIF Request and Response
 *  message handling in that responses are not dispatched to the usual QueryResults
 *  objects in the message dispatching chain. Rather, they are delvered directly
 *  to this class and returned to the calling thread by the <code>next</code>
 *  method. The calling thread is blocked when <code>waitForResults</code> is
 *  called for <i>synchronous</i> SIF Request processing.<p>
 *
 *  <b>When to Use</b>
 *  <p>
 *  TrackQueryResults should be used when:<p>
 *
 *  <ul>
 *      <li>
 *          Your agent needs to request one or more objects from a zone before
 *          proceeding with the current operation. In this case, a synchronous
 *          method of SIF Request and Response processing is ideal because the
 *          current thread can block until the query has completed, avoiding
 *          complex asynchronous interactions in your code. If results are not
 *          received in the specified period of time, the agent can timeout and
 *          abandon the operation in progress.<br/><br/>
 *      </li>
 *      <li>
 *          Your agent needs to query a zone for objects while handling a SIF
 *          Event message. In this case, Selective Message Blocking is
 *          automatically invoked by the TrackQueryResults constructor. (Refer
 *          to the SIF Specification for more information on Selective Message
 *          Blocking.) Note: When the Agent Local Queue (ALQ) is enabled,
 *          Selective Message Blocking is not necessary. The ADK will dispatch
 *          messages from the local queue regardless. However, to ensure your
 *          code works properly even when the local queue is disabled, we suggest
 *          always using the TrackQueryResults class when sending SIF Requests
 *          during SIF Event handling.<br/><br/>
 *      </li>
 *  </ul>
 *  <p>
 *
 *  <b>How to Use</b>
 *  <p>
 *
 *  Follow these steps to use TrackQueryResults:<p>
 *
 *  <ul>
 *      <li>
 *          Create an instance of the TrackQueryResults class. If your code
 *          is processing a SIF Event message from the Subscriber.onEvent
 *          method, pass the <code>Event</code> object to the constructor. This
 *          will invoke Selective Message Blocking on the event to prevent a
 *          deadlock condition at the Zone Integration Server.<br/><br/>
 *
 *          <code>
 *          TrackQueryResult tqr = new TrackQueryResults();<br/>
 *          </code>
 *          <br/>
 *      </li>
 *      <li>
 *          Call the <code>addQuery</code> method one or more times to specify
 *          the queries the TrackQueryResults object will issue. If you add more
 *          than one query, keep in mind the results may not be received in the
 *          same order as the queries were issued.<br/><br/>
 *
 *          <code>
 *          //  Query for a SchoolInfo object by RefId<br/>
 *          SIFDTD dtd = ADK.DTD();<br/>
 *          Query query = new Query( dtd.SCHOOLINFO );<br/>
 *          query.addCondition( dtd.SCHOOLINFO_REFID, Condition.EQ, refId );<br/>
 *          Topic schoolInfo = getTopicFactory().getInstance( dtd.SCHOOLINFO );<br/>
 *          <br/>
 *          //  Add the query to TrackQueryResults<br/>
 *          tqr.addQuery( schoolInfoTopic, query );<br/>
 *          </code>
 *          <br/>
 *      </li>
 *      <li>
 *          Create a loop that calls the <code>waitForResults</code>,
 *          <code>available</code>, and <code>next</code> methods as illustrated
 *          below to wait for and retrieve the results of all queries. The
 *          outermost <code>waitForResults</code> loop blocks until at least
 *          <i>one</i> result is available, all queries have completed (i.e. the
 *          last packet of each SIF_Response message has been received), or the
 *          timeout period has expired. The inner <code>available</code> method
 *          does not block.<br/><br/>
 *
 *          <code>
 *          //  Timeout if results are not received in 30 seconds...<br/>
 *          while( tqr.waitForResults( 30000 ) )<br/>
 *          {<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;//  Retrieve all messages so far<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;while( tqr.available() )<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SIFDataObject[] data = tqr.next();<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;for( int i = 0; i < data.length; i++ )<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//  Process the SIF Data Objects received...<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 *          &nbsp;&nbsp;&nbsp;&nbsp;}<br/>
 *          }<br/>
 *          </code>
 *          <br/><br/>
 *      </li>
 *  </ul>
 *
 *  <b>TrackQueryResultsData</b>
 *  <p>
 *  When TrackQueryResults receives a SIF Response message, it captures not only
 *  the SIF Data Objects provided by that message, but also the zone from which
 *  the message was received and the associated SIFMessageInfo object, which
 *  includes header fields and optionally the XML content of the message.
 *  TrackQueryResults packages this data to the caller by returning an instance
 *  of the TrackQueryResultsData class.<p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class TrackQueryResults extends TrackQueryResultsImpl
{
	/** Handles calling back into MessageDispatcher's EvDisp thread to invoke SMB */
	protected SMBHelper fSmb;

	/** The last timeout value passed to waitForResults */
	protected int fTimeout = 0;

	/** Query options (not currently used) */
	protected int fQueryOpts = 0;

	/** True if all queries have completed */
	protected boolean fCompleted = false;

	/** Query errors */
	protected Vector<ADKException> fErrors = new Vector<ADKException>();


	/**
	 *  Constructs a TrackQueryResults object to be used when the agent is not
	 *  processing a SIF_Event message. If the agent is processing a SIF_Event
	 *  in the Subscriber.onEvent method, the other form of constructor must be
	 *  used to invoke Selective Message Blocking.
	 */
    public TrackQueryResults()
		throws ADKException
	{
		this( null,null );
	}

	/**
	 *  Constructs a TrackQueryResults object to be used when the agent needs
	 *  to query for additional data while processing a SIF_Event message.
	 *  The Event object passed to Subscriber.onEvent must be passed to this
	 *  form of constructor. Selective Message Blocking is invoked when the
	 *  Agent Local Queue is not being used.
	 *
	 *  @param event The Event object passed to Subscriber.onEvent
	 *  @param msgInfo The MessageInfo object passed to Subscriber.onEvent
	 */
	public TrackQueryResults( Event event, MessageInfo msgInfo )
		throws ADKException
	{
		super();

		if( event != null && event.getZone() == null )
			ADKUtils._throw( new IllegalStateException("TrackQueryResults( Event ) can only be called during the processing of an incoming SIF_Event"), Agent.getLog() );

		if( event != null ) {
			fSmb = new SMBHelper(this,event,msgInfo);
	    	fSmb.invokeSMB();
		}
	}

	/**
	 *  Sets optional query options
	 */
	public void setQueryOptions( int queryOptions )
	{
		fQueryOpts = queryOptions;
	}

	/**
	 *  Add a query to be executed when the waitForResults method is called
	 */
	public synchronized void addQuery( Zone zone, Query query )
	{
		_addQuery( zone,query,null );
	}

	/**
	 *  Add a query to be executed when the waitForResults method is called
	 */
	public synchronized void addQuery( Zone zone, Query query, String destinationId )
	{
		_addQuery( zone,query,destinationId );
	}

	/**
	 *  Add a query to be executed when the waitForResults method is called.
	 */
	public synchronized void addQuery( Topic topic, Query query  )
	{
		_addQuery( topic,query,null );
	}

	/**
	 *  Add a query to be executed when the waitForResults method is called.
	 */
	public synchronized void addQuery( Topic topic, Query query, String destinationId )
	{
		_addQuery( topic,query,destinationId );
	}

	private void _addQuery( Object zoneOrTopic, Query query, String destinationId )
	{
		//  Let the ADK MessageDispatcher know which object to call onQueryPending on
		sRequestQueries.put( query, this );

		fQueries.addElement( new QueryWrapper( query, zoneOrTopic, destinationId ) );
	}

	/**
	 *  Sends a SIF_Request message for each Query passed to the <code>addQuery</code>
	 *  method and resets the timeout period to the given value. Blocks until at least
	 *  one pending query returns data or the timeout period expires.<p>
	 *
	 *  This method should be called repeatedly in a <b>while</b> loop as
	 *  SIF_Response packets may be received by the agent in multiple packets.
	 *  If not enclosed in a <b>while</b> loop, your agent will only process
	 *  the first packet when a multi-packet SIF_Response is received. (If the
	 *  caller only expects one packet - for example, when querying for a single
	 *  SIF Data Object during SIF_Event processing - an outer <b>while</b>
	 *  loop is not necessary.) Each time this method is called, the timeout
	 *  period is reset and any new queries that have been added are executed.
	 *  If called and all queries tracked by this object have returned data, no
	 *  action is taken.<p>
	 *
	 *  If a SIF_Request messages fails with an error acknowledgement from the
	 *  ZIS or otherwise results in an exception, the error condition can be
	 *  learned by calling the <code>getErrors</code> method. It is recommended
	 *  that you always check for an error condition by calling the <code>hasErrors</code>
	 *  or <code>getErrors</code> methods.
	 *
	 *  @return true if result data is available, otherwise false
	 *
	 *  @see #getErrors
	 *  @see #hasErrors
	 */
	public synchronized boolean waitForResults( int timeout )
	{
		fTimeout = timeout;

		int sent = 0;
		int completed = 0;

		synchronized( fQueries )
		{
			for( int i = 0; i < fQueries.size(); i++ )
			{
				QueryWrapper w = (QueryWrapper)fQueries.elementAt(i);

				if( w.fRequestMsgId == null )
				{
					fLastQuery = w;

					try
					{
						if( w.fZoneOrTopic instanceof Zone )
	    					((Zone)w.fZoneOrTopic).query( w.fQuery, w.fDestinationId, fQueryOpts );
		    			else
			    		if( w.fZoneOrTopic instanceof Topic )
				    		((Topic)w.fZoneOrTopic).query( w.fQuery, w.fDestinationId, fQueryOpts );
					}
					catch( ADKException ex )
					{
						fErrors.addElement( ex );
						completed++;
					}

					sent++;
				}
				else
				if( w.fCompleted != 0 )
					completed++;
			}
		}

		//  Any pending queries?
		fCompleted = ( completed == fQueries.size() );
		if( fCompleted ) {
			return false;
		}

		//  Wait for results to come in
		try {
			if( !fCompleted && fResults.size() == 0 ) {
				synchronized( this ) {
				wait( timeout );
                
				}
			}
		} catch( InterruptedException ie ) {
			return false;
		}

		return fResults.size() > 0;
	}

	/**
	 *  Returns the number of results currently available
	 *  @return The number of
	 */
	public int available()
	{
		synchronized( fResults )
		{
			return fResults.size();
		}
	}

	/**
	 *  Determines if any results will be returned by the <code>next</code> method
	 */
	public boolean hasNext()
	{
		synchronized( fResults )
		{
			return fResults.size() > 0;
		}
	}

	/**
	 *  Have all requests been received?
	 */
	public synchronized boolean isComplete()
	{
		return fCompleted;
	}

	/**
	 *  Returns the next group of result data, or null if no data is available.
	 *  This method must be called repeatedly to retrieve all results because
	 *  TrackQueryResults may have received multiple SIF_Response messages in
	 *  response to its queries.<p>
	 */
	public synchronized TrackQueryResultsData next()
	{
		synchronized( fResults )
		{
			for( int i = 0; i < fResults.size(); i++ ) {
				TrackQueryResultsData data = (TrackQueryResultsData)fResults.elementAt(i);
				fResults.removeElementAt(i);
				return data;
			}
		}

		return null;
	}

	/**
	 *  Gets any SIF_Request errors received when the queries were executed.<p>
	 *
	 *  When the <code>waitForResults</code> method is called, it executes all
	 *  queries previously added to the TrackQueryResults object. If an error
	 *  acknowledgement is received for a SIF_Request message, it is returned
	 *  in this array. Call the <code>ADKException.getZone</code> method to
	 *  learn the zone associated with each exception.
	 *
	 *  @return An array of ADKExceptions representing all error acknowledgements
	 *      received by TrackQueryResults when the queries were issued.
	 *
	 *  @see #hasErrors
	 *  @see #waitForResults
	 */
	public synchronized ADKException[] getErrors()
	{
		ADKException[] arr = new ADKException[ fErrors.size() ];
		fErrors.copyInto( arr );
		return arr;
	}

	/**
	 *  Determines if any SIF_Request errors occurred when the queries were executed.<p>
	 *
	 *  @see #getErrors
	 *  @see #waitForResults
	 */
	public synchronized boolean hasErrors()
	{
		return fErrors.size() > 0;
	}
}
