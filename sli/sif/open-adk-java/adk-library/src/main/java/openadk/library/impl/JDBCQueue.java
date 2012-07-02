//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import java.util.Properties;

import openadk.library.*;

/**
 *  The JDBCQueue class is a JDBC-based implementation of an Agent Local Queue.
 */
public class JDBCQueue implements IAgentQueue
{
	private Zone fZone;

    public JDBCQueue()
	{
    }

	/**
	 *  Initialize the queue
	 *  @param agent The agent that owns the queue. Each agent has one queue
	 *      per zone to which it is connected.
	 *  @param zone The zone that owns the queue. Each zone to which an agent
	 *      is connected is represented by its own queue.
	 *  @param props Implementation-specific initialization properties (e.g.
	 *      location of the queue, user authentication parameters, etc.)
	 */
	public void initialize( Zone zone, Properties props )
		throws ADKQueueException
	{
		fZone = zone;
	}

	/**
	 *  Is the queue ready? A queue is ready when it has been successfully
	 *  initialized and shutdown has not been called.
	 */
	public boolean isReady()
	{
		return false;
	}

	/**
	 *  Close the queue
	 */
	public void shutdown()
		throws ADKQueueException
	{
	}

	/**
	 *  Gets the Zone that owns this queue
	 */
	public Zone getZone()
	{
		return fZone;
	}

	/**
	 *  Posts an unparsed incoming SIF message to the queue
	 */
	public void postMessage( SIFMessageInfo msgInfo )
		throws ADKQueueException
	{
	}

	/**
	 *  Posts a parsed outgoing SIF message to the queue
	 */
	public void postMessage( SIFMessagePayload msg )
		throws ADKQueueException
	{
	}

	/**
	 *  Removes a message from the queue
	 */
	public void removeMessage( String msgId )
		throws ADKQueueException
	{
	}

	/**
	 *  Gets the next available group of messages<p>
	 *
	 *  @param msgType The message type, or <code>MSG_ANY</code> to return the
	 *      next available groups of messages regardless of type. Message type
	 *      codes are defined by <code>MSG_</code> constants defined by the
	 *      IAgentQueue interface.
	 *  @param direction Specifies whether the message is incoming or outgoing;
	 *      one of the following: <code>IAgentQueue.INCOMING</code>, <code>
	 *      IAgentQueue.OUTGOING</code>, or <code>IAgentQueue.ALL</code>
	 */
	public SIFMessageInfo[] nextMessage( byte msgType, byte direction )
		throws ADKQueueException
	{
		return null;
	}

	/**
	 *  Determines if a message is in the queue
	 *  @param msgId The SIF_MsgId identifier
	 */
	public boolean hasMessage( String msgId )
		throws ADKQueueException
	{
		return false;
	}

	/**
	 *  Gets a message by ID
	 *
	 *  @param msgId The message identifier
	 */
	public String getMessage( String msgId )
		throws ADKQueueException
	{
		return null;
	}

	/**
	 *  Counts the total number of messages in the queue<p>
	 *
	 *  @param msgType The message type, or <code>MSG_ANY</code> to return the
	 *      number of all messages regardless of type. Message type codes are
	 *      defined by <code>MSG_</code> constants defined by the IAgentQueue
	 *      interface.
	 *  @param direction Specifies whether the message is incoming or outgoing;
	 *      one of the following: <code>IAgentQueue.INCOMING</code>, <code>
	 *      IAgentQueue.OUTGOING</code>, or <code>IAgentQueue.ALL</code>
	 */
	public int getCount( byte msgType, byte direction )
		throws ADKQueueException
	{
		return 0;
	}

	/**
	 *  Gets a queue statistic<p>
	 *
	 *  @param id Identifies the statistic to return. Identifiers are enumerated
	 *      by STAT_ constants defined by the IAgentQueue interface.
	 *  @return A statistic object
	 */
	public IStatistic getStatistic( byte statId )
	{
		return null;
	}
}
