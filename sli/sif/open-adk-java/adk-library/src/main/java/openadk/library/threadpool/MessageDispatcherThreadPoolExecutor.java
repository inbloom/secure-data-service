//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.threadpool;

import java.util.ArrayList;

import openadk.library.ADK;
import openadk.library.Agent;


/**
 * Implementation of {@link ScheduledThreadPoolExecutor} that overrides the
 * <tt>afterExecute()</tt> method to call the registered listeners when a task
 * completes and exits (task is not rescheduled to run again).
 * 
 */
public class MessageDispatcherThreadPoolExecutor extends ScheduledThreadPoolExecutor {
	
	ArrayList<MessageDispatchThreadListener> listeners = new ArrayList<MessageDispatchThreadListener>();
	
	/**
	 * Creates a new <tt>MessageDispatcherThreadPoolExecutor</tt> with the given core pool size.
	 * 
	 * @param corePoolSize
	 *            the number of threads to keep in the pool, even if they are
	 *            idle.
	 * @throws IllegalArgumentException
	 *             if corePoolSize less than or equal to zero
	 */
	public MessageDispatcherThreadPoolExecutor(int corePoolSize) {
		super(corePoolSize);
	}
	
	/**
	 * Creates a new <tt>ScheduledThreadPoolExecutor</tt> with the given core pool size.
	 * 
	 * @param minPoolSize
	 *            corePoolSize the number of threads to keep in the pool, even
	 *            if they are idle.
	 * @param maxPoolSize
	 *            the maximum number of threads to allow in the pool.
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time that excess idle threads will wait for new
	 *            tasks before terminating.
	 * @throws IllegalArgumentException
	 *             if corePoolSize, or keepAliveTime less than zero, or if
	 *             maxPoolSize less than or equal to zero, or if corePoolSize
	 *             greater than maxPoolSize.
	 */
	public MessageDispatcherThreadPoolExecutor(int minPoolSize, int maxPoolSize, long keepAliveTime) {
		super(minPoolSize, maxPoolSize, keepAliveTime);
	}

	protected void terminated() {
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
			Agent.getLog().info( "MessageDispatcherThreadPoolExecutor terminated" );
		}
	}
	
	protected void afterExecute(Runnable r, Throwable t) {
		Runnable task = r;
		super.afterExecute(r, t);

		//Agent.getLog().debug("thread pool stats (afterExecute);  " + 
		//		" TaskCount: " + (getTaskCount()-getCompletedTaskCount())+
		//		" getLargestPoolSize: " + getLargestPoolSize()+
		//		" Queue size: " + getQueue().size()
		//		);

		if (r instanceof ScheduledFutureTask<?>) {
			ScheduledFutureTask<?> s = (ScheduledFutureTask<?>)r;
			if ( t == null && s.isPeriodic() )
				return;
			task = s.getTask();
		}

		for (MessageDispatchThreadListener listener : listeners) {
			listener.threadCompleted(task, t);
		}
	}

	public void addListener(MessageDispatchThreadListener listener) {
		this.listeners.add(listener);
	}
}
