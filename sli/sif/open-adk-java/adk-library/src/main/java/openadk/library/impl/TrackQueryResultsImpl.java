//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.*;

import openadk.library.*;
import openadk.library.infra.*;

/**
 *  Base class for TrackQueryResults, located in the impl package so that
 *  MessageDispatcher can have access to its protected members.
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class TrackQueryResultsImpl implements QueryResults
{
	/**
	 *  Maps SIF_Request message IDs to TrackQueryResults objects. The ADK
	 *  MessageDispatcher consults this map to decide whether to route a
	 *  SIF_Response to a TrackQueryResults object or to the normal message
	 *  routing chain.
	 */
	protected static HashMap sRequestMsgIds = new HashMap();

	/**
	 *  Maps Query objects to TrackQueryResults instances. The ADK MessageDispatcher
	 *  needs to know this so it can call QueryResults.onQueryPending.
	 *  MessageDispatcher is responsible for cleaning up this map after
	 *  using it.
	 */
	protected static HashMap sRequestQueries = new HashMap();

	/** Results received from queries issued by this object (TrackQueryResultData objects) */
	protected Vector fResults = new Vector();

	/** Queries tracked by this object (QueryWrapper objects) */
	protected Vector fQueries = new Vector();

	/** Reference to this ptr */
	private TrackQueryResultsImpl _self = null;

	/** The last query issued */
	protected QueryWrapper fLastQuery;

	/**
	 *  Constructor
	 */
    protected TrackQueryResultsImpl()
	{
		_self = this;
    }

	public void onQueryPending( MessageInfo info, Zone zone )
		throws ADKException
	{
		//  Keep track of which SIF_Request msgIds map to which
		//  TrackQueryResults objects - for use by MessageDispatcher
		String id = ((SIFMessageInfo)info).getSIFRequestMsgId();
		sRequestMsgIds.put( id, this );

		//  Keep track of which SIF_Request msgIds map to which
		//  QueryWrapper objects
		fLastQuery.fRequestMsgId = id;
	}

	public void onQueryResults( DataObjectInputStream data, SIF_Error error, Zone zone, MessageInfo info )
		throws ADKException
	{
		SIFMessageInfo inf = (SIFMessageInfo)info;
		TrackQueryResultsData tdata = error == null ?
			new TrackQueryResultsData( data,zone,inf ) :
			new TrackQueryResultsData( error,data.getObjectType(),zone,inf );

		//  If this is the last SIF_Response packet, remove the tracked
		//  SIF_Request MsgId from the TrackQueryResultsImpl
		if( inf.getMorePackets() == null || inf.getMorePackets().booleanValue() == false )
			sRequestMsgIds.remove( inf.getSIFRequestMsgId() );

		//  Check SIF_MorePackets
		if( error != null || ( inf.getMorePackets() != null && !inf.getMorePackets().booleanValue() ) )
		{
			synchronized( fQueries ) {
				for( int i = 0; i < fQueries.size(); i++ ) {
					QueryWrapper w = (QueryWrapper)fQueries.elementAt(i);
					if( w.fRequestMsgId != null && w.fRequestMsgId.equals( inf.getSIFRequestMsgId() ) ) {
						w.fCompleted = System.currentTimeMillis();
                        break;
					}
				}
			}
		}

		synchronized( fResults ) {
			fResults.addElement(tdata);
			synchronized( _self ) {
				_self.notifyAll();
			}
		}
	}

	public class QueryWrapper
	{
		public Query fQuery;
		public Object fZoneOrTopic;
		public String fDestinationId;
		public String fRequestMsgId;
		public long fCompleted=0;
		public QueryWrapper( Query q, Object zoneOrTopic, String destinationId ) {
			fZoneOrTopic = zoneOrTopic;
			fDestinationId = destinationId;
			fQuery = q;
		}
	}

}
