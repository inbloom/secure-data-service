//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools;

import java.util.*;

/**
 *  A LoadBalancer manages a free pool of <i>Baton</i> objects representing the
 *  right of a thread to perform a resource-intensive task. For example, you
 *  could create a LoadBalancer that represents the task "query all students"
 *  and assign it an initial pool of 5 Batons, meaning at most 5 threads will be
 *  able to carry out this task at once. A thread must check out a Baton in
 *  order to perform the task, and must release it back to the LoadBalancer
 *  when finished.<p>
 *
 *  Refer to the Baton class for a description of how to use the LoadBalancer
 *  and Baton classes and the LoadBalancerListener interface. These classes can
 *  be used to introduce internal load balancing into an agent to significantly
 *  improve scalability when connecting to tens or hundreds of zones
 *  concurrently.<p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class LoadBalancer
{
	public static boolean TRACE = false;

	/**
	 *  LoadBalancer ID
	 */
	protected Object fID;

	/**
	 *  Pool of batons
	 */
	protected Stack fPool = new Stack();

	/**
	 *  Maximum number of Batons
	 */
	protected int fSize;

	/**
	 *  checkoutBaton timeout value
	 */
	protected long fTimeout;

	/**
	 *  Global dictionary of LoadBalancers
	 *  @see #define
	 */
	protected static HashMap sDict = new HashMap();

	/**
	 *  Flagged true when the pool is emptied
	 */
	protected boolean fEmptied = false;

	/**
	 *  Listeners
	 */
	protected Vector fListeners	= null;


	/**
	 *  Constructs a LoadBalancer to represent a specific logical task.<p>
	 *
	 *  @param id A unique arbitrary ID that the agent will use to request this
	 *      LoadBalancer (e.g. "Request_StudentPersonal")
	 *  @param batons The number of Batons that will be available to threads
	 *  @param timeout The timeout period (in milliseconds) applied to the
	 *      <code>checkoutBaton</code> method. The timeout period should be less
	 *      than the HTTP or other transport timeout period so that the connection
	 *      to the ZIS does not timeout before the load balancer does.
	 */
    public LoadBalancer( Object id, int batons, long timeout )
	{
		if( id == null )
			throw new IllegalArgumentException("id cannot be null");

	    fID = id;
		fTimeout = timeout;
		fSize = batons;

		//  Create pool of batons
		int _batons = Math.max(1,batons);
		for( int i = 0; i < _batons; i++ )
			fPool.push( new Baton() );
    }

	/**
	 *  Define a LoadBalancer that may be subsequently returned by the <code>lookup</code> method.<p>
	 *  @param balancer A LoadBalancer instance
	 */
	public static void define( LoadBalancer balancer )
	{
		synchronized( sDict ) {
			sDict.put( balancer.fID, balancer );
		}
	}

	/**
	 *  Lookup a LoadBalancer that was previously defined by the <code>define</code> method.<p>
	 *  @param id The ID of the LoadBalancer to obtain
	 *  @return The LoadBalancer or null if no LoadBalancer with this id has been
	 *      previously defined by the <code>define</code> method
	 */
	public static LoadBalancer lookup( Object id )
	{
		synchronized( sDict ) {
			return (LoadBalancer)sDict.get( id );
		}
	}

	/**
	 *  Check-out a Baton.
	 */
	public Baton checkoutBaton()
	{
		Baton b = null;

		synchronized( fPool )
		{
			if( fPool.size() == 0 )
			{
				try
				{
					//  Wait for an instance to become available
					if( TRACE )
						System.out.println("Waiting for baton to become available (" + Thread.currentThread().getName() + ")");
					fPool.wait( fTimeout );
					if( TRACE )
						System.out.println("Done waiting for baton to become available (" + Thread.currentThread().getName() + ")");
				}
				catch( InterruptedException ie )
				{
				}
			}

			if( fPool.size() > 0 )
				b = (Baton)fPool.pop();
			if( TRACE )
				System.out.println( b == null ? "No baton available" : "Got a baton (there are " + fPool.size() + " left)" );
			if( fPool.size() == 0 )
				fEmptied = true;
		}

		return b;
	}

	/**
	 *  Check-in a Baton.
	 */
	public void checkinBaton( Baton baton )
	{
		if( baton != null )
		{
			synchronized( fPool )
			{
				fPool.push( baton );
				if( TRACE )
					System.out.println("Baton checked in; there are now "+fPool.size());

				if( fEmptied && fPool.size() >= ( fSize == 1 ? 1 : 2 ) )
				{
					if( TRACE )
						System.out.println("Notifying listeners that batons are now available");

					//  Notify all listeners that Batons are once again available
					fEmptied = false;
					if( fListeners != null ) {
						while(fListeners.size() > 0) {
							LoadBalancerListener l = (LoadBalancerListener)fListeners.remove(0);
							l.onBatonsAvailable( this );
						}
					}
				}

				try
				{
					//  Notify all waiting threads that a Baton is available
					if( TRACE )
						System.out.println("Notifying threads that baton is returned to free pool");
					fPool.notifyAll();
				} catch( Throwable thr ) {
				}
			}
		}
	}

	/**
	 *  Gets the current load (the number of Batons in use)
	 */
	public int getLoad()
	{
		synchronized( fPool ) {
			return fSize - fPool.size();
		}
	}

	/**
	 *  Gets the total number of Batons
	 */
	public int getTotalBatons()
	{
		return fSize;
	}

	/**
	 *  Gets the number of Batons
	 */
	public int getFreeBatons()
	{
		synchronized( fPool ) {
			return fPool.size();
		}
	}

	/**
	 *  Register a LoadBalancerListener with this LoadBalancer. The listener will
	 *  be called when the free pool is empty and subsequently contains at least
	 *  two Batons (or one Baton if this LoadBalancer was defined to have a pool
	 *  size of one).
	 */
	public void addLoadBalancerListener( LoadBalancerListener listener )
	{
		if( fListeners == null )
			fListeners = new Vector();
		fListeners.addElement( listener );
	}
}
