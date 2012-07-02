//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.threadpool;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import openadk.library.ADK;
import openadk.library.Agent;


/**
 * <p>This class manages a pool of Pull mode  threads</p>
 * 
 * To use this class setup as follows.
 *<p><ul>
 *       // Thread Pooling <br>
 *  	 ThreadPoolManager threadPoolManager = new ThreadPoolManager(3);<br>
 *       threadPoolManager.setThreadRunLimit(300); // 5 minutes <br>
 *<br>
 *       // Now, connect to all zones to register and query<br>
 *       for( Zone zone : this.getZoneFactory().getAllZones() ) {<br>
 *       	&nbsp;&nbsp;// Thread Pooling<br>
 *        	&nbsp;&nbsp;zone.setThreadPoolManager(threadPoolManager);<br>
 *       	<br>
 *			&nbsp;&nbsp;zone.setSubscriber(schoolInfoHandler, StudentDTD.SCHOOLINFO);<br>
 *			&nbsp;&nbsp;zone.setQueryResults(schoolInfoHandler);<br>
 *			&nbsp;&nbsp;try {<br>
 *			&nbsp;&nbsp;&nbsp;&nbsp;	zone.connect(ADKFlags.PROV_REGISTER);<br>
 *			&nbsp;&nbsp;} catch (Exception e) {<br>
 *			&nbsp;&nbsp;&nbsp;&nbsp;	zone.connect(ADKFlags.PROV_NONE);<br>
 *			&nbsp;&nbsp;}<br>
 *			<br>
 *			&nbsp;&nbsp;//  Request all students<br>
 *			&nbsp;&nbsp;Query query = new Query(StudentDTD.SCHOOLINFO);<br>
 *			&nbsp;&nbsp;zone.query(query); <br>
 *       }<br>
 *<br>
 *       // Thread Pool start<br>
 *       threadPoolManager.execute(); </ul></p>
 */
public class ThreadPoolManager implements MessageDispatchThreadListener {
	private MessageDispatcherThreadPoolExecutor threadExecutor = null;
	
//	private ArrayList<MessageDispatcherTask> tasks = new ArrayList<MessageDispatcherTask>();
	private List<Runnable> tasks = Collections.synchronizedList(new LinkedList<Runnable>());

	/**
	 * Constructor for a thread pool with 1 to 5 threads.
	 */
	public ThreadPoolManager () {
		setupPoolManager(1, 5, 10);
	}
	
	/**
	 * Constructor for a thread pool with a fixed pool size.
	 * 
	 * @poolSize - Number of threads in pool
	 */
	public ThreadPoolManager (int poolSize) {
		setupPoolManager(poolSize, poolSize, 10);
	}
	
	/**
	 * Constructor for a thread pool with a fixed pool size.
	 * 
	 * @param minPoolSize
	 *            corePoolSize the number of threads to keep in the pool, even
	 *            if they are idle.
	 * @param maxPoolSize
	 *            the maximum number of threads to allow in the pool.
	 * @param keepAliveTime
	 *            when the number of threads is greater than the core, this is
	 *            the maximum time in milliseconds that excess idle threads will
	 *            wait for new tasks before terminating.
	 */
	public ThreadPoolManager (int minPoolSize, int maxPoolSize, long keepAliveTime) {
		setupPoolManager(minPoolSize, maxPoolSize, keepAliveTime);
	}

	private void setupPoolManager(int minPoolSize, int maxPoolSize, long keepAliveTime) {
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ) {
			Agent.getLog().info( "Starting thread pool with " + minPoolSize + "-" +maxPoolSize + " threads" );
		}
		threadExecutor = new MessageDispatcherThreadPoolExecutor(minPoolSize, maxPoolSize, keepAliveTime);
		threadExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
		threadExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		threadExecutor.addListener(this);
		
	    Runtime.getRuntime().addShutdownHook(new Thread("ThreadPoolManager shutdown") {
	        public void run() {
				// Shutdown
	        	int i = 0;
				shutdown();
				while (!threadExecutor.isTerminated()) {
					try {
						Thread.sleep(1000);
						i++;
						if (i == 60)	// give the threadExecutor 60 seconds to shutdown gracefully
							threadExecutor.shutdownNow();
					} catch (InterruptedException e) {
						Agent.getLog().error( "Problem shuting down thread pool: " + e.getMessage(), e );
					}
				}
	        }
	      });
	}

	/**
	 * Initiates an orderly shutdown in which previously submitted tasks are
	 * executed, but no new tasks will be accepted. Invocation has no additional
	 * effect if already shut down.
	 */
	public void shutdown() {
		if (threadExecutor.isTerminating()) {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
				Agent.getLog().info( "Thread pool manager is terminating" );
			}
		} else if (threadExecutor.isShutdown()) {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
				Agent.getLog().info( "Thread pool manager is shutdown" );
			}
		} else {
			if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
				Agent.getLog().info( "Thread pool manager will shutdown; " + threadExecutor.getLargestPoolSize() + " threads used" );
			}
			threadExecutor.setRejectedExecutionHandler(new ShutdownExecutionHandler ());
		}
		threadExecutor.shutdown();
		//threadExecutor.getQueue().clear();
	}

	/**
	 * Call this method to start all tasks that were previously queued through
	 * <code>addTask(Runnable)</code> method.
	 */
	public void execute() {
		// Start threads and place in runnable state 
		threadExecutor.prestartCoreThread();
		if ( tasks != null ) {
			synchronized(tasks) {
				for (Runnable task : tasks) {
					execute(task);
				}

				tasks.clear();
				tasks = null;
			}
		}
	}

	/**
	 * Execute task with no initial delay. The task can execute repeatedly with
	 * a variable delay if it implements {@link IThreadPoolDelayTask}.
	 * 
	 * @param task
	 *            the task to execute
	 */
	public void execute(Runnable task) {
		if (task instanceof IThreadPoolDelayTask) {
			IThreadPoolDelayTask sch = (IThreadPoolDelayTask)task;
			long delay = sch.getScheduleDelay();
			threadExecutor.scheduleWithFixedDelay(task, 0, delay <= 0 ? 60000 : delay, TimeUnit.MILLISECONDS);
		} else {
			threadExecutor.execute(task);
		}
	}

	/**
	 * Execute task after waiting the specified delay. The task can execute
	 * repeatedly with a variable delay if it implements
	 * {@link IThreadPoolDelayTask}.
	 * 
	 * @param task
	 *            the task to execute
	 * @param delay
	 *            the time in milliseconds to delay before execution
	 */
	public void execute(Runnable task, long delay) {
		if (task instanceof IThreadPoolDelayTask) {
			IThreadPoolDelayTask sch = (IThreadPoolDelayTask)task;
			long schdlDelay = sch.getScheduleDelay();
			threadExecutor.scheduleWithFixedDelay(task, delay, schdlDelay <= 0 ? 60000 : schdlDelay, TimeUnit.MILLISECONDS);
		} else {
			threadExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
		}
	}
	
	/**
	 * Creates and executes a periodic task that becomes enabled first after the
	 * given initial delay, and subsequently with the given delay between the
	 * termination of one execution and the commencement of the next.
	 * 
	 * @param task
	 *            the task to execute
	 * @param initialDelay
	 *            the time to delay first execution in milliseconds
	 * @param delay
	 *            the delay between the termination of one execution and the
	 *            commencement of the next in milliseconds
	 * @return a Future representing pending completion of the task, and whose
	 *         get() method will throw an exception upon cancellation.
	 */
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay) {
		return threadExecutor.scheduleWithFixedDelay(task, initialDelay, delay, TimeUnit.MILLISECONDS);
	}

	/**
	 * Creates and executes a periodic action that becomes enabled first after
	 * the given initial delay, and subsequently with the given period.
	 * 
	 * @param task
	 *            the task to execute
	 * @param initialDelay
	 *            the time to delay first execution in milliseconds
	 * @param period
	 *            the period between successive executions in milliseconds
	 * @return a Future representing pending completion of the task, and whose
	 *         get() method will throw an exception upon cancellation.
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
		return threadExecutor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
	}

	/* *
	 * Returns the core number of threads.
	 * @return the core number of threads
	 * /
	public int getPoolSize() {
		return threadExecutor.getCorePoolSize();
	}
	*/

	/**
	 * Returns the approximate number of threads that are actively executing
	 * tasks.
	 * 
	 * @return the number of threads
	 */
	public int getActiveCount() {
		return threadExecutor.getActiveCount();
	}

	/**
	 * Returns the core number of threads.
	 * @return Returns the core number of threads.
	 * @see #setCorePoolSize
	 */
	public int getCorePoolSize() {
		return threadExecutor.getCorePoolSize();
	}

	/**
	 * Returns the maximum allowed number of threads.
	 * @return the maximum allowed number of threads
	 * @see #setMaximumPoolSize
	 */
	public int getMaximumPoolSize() {
		return threadExecutor.getMaximumPoolSize();
	}

	/**
	 * Returns the thread keep-alive time, which is the amount of time which
	 * threads in excess of the core pool size may remain idle before being
	 * terminated.
	 * 
	 * @return the time limit in milliseconds
	 * @see #setKeepAliveTime
	 */
	public long getKeepAliveTime() {
		return threadExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS);
	}

	/* *
	 * Returns the internal {@link ScheduledThreadPoolExecutor} allowing direct
	 * access to the thread pool for advanced applications.
	 * <tt>ThreadPoolManager</tt>
	 * 
	 * @return the {@link ScheduledThreadPoolExecutor}
	 * /
	public ScheduledThreadPoolExecutor getThreadExecutor() {
		return threadExecutor;
	}
	*/

	/**
	 * Sets the core number of threads. This overrides any value set in the
	 * constructor. If the new value is smaller than the current value, excess
	 * existing threads will be terminated when they next become idle. If
	 * larger, new threads will, if needed, be started to execute any queued
	 * tasks.
	 * 
	 * @param corePoolSize the new core size
	 * @see #getCorePoolSize
	 */
	public void setCorePoolSize(int corePoolSize) {
		threadExecutor.setCorePoolSize(corePoolSize);

		if (corePoolSize > threadExecutor.getMaximumPoolSize())
			threadExecutor.setMaximumPoolSize(corePoolSize);
	}

	/**
	 * Sets the maximum allowed number of threads. This overrides any value set
	 * in the constructor. If the new value is smaller than the current value,
	 * excess existing threads will be terminated when they next become idle.
	 * 
	 * @param maxPoolSize the new maximum
	 * @see #getMaximumPoolSize
	 */
	public void setMaximumPoolSize(int maxPoolSize) {
		threadExecutor.setMaximumPoolSize(maxPoolSize);
	}

	/**
	 * Sets the time limit for which threads may remain idle before being
	 * terminated. If there are more than the core number of threads currently
	 * in the pool, after waiting this amount of time without processing a task,
	 * excess threads will be terminated. This overrides any value set in the
	 * constructor.
	 * 
	 * @param time
	 *            the time to wait in milliseconds. A time value of zero will cause excess
	 *            threads to terminate immediately after executing tasks.
	 * @see #getKeepAliveTime
	 */
   public void setKeepAliveTime(long time) {
    	threadExecutor.setKeepAliveTime(time, TimeUnit.MILLISECONDS);
    }

	/**
	 * Removes this task from the executor's internal queue if it is present,
	 * thus causing it not to be run if it has not already started.
	 * 
	 * @param task the task to remove
	 * @return true if the task was removed
	 */
	public boolean removeTask(Runnable task) {
		return threadExecutor.remove(task);
	}

	/**
	 * Adds a task to be run in a thread pool. Tasks will not start until the
	 * <code>execute()</code> method is called. The task can execute with a
	 * variable delay if it implements {@link IThreadPoolDelayTask}.
	 * 
	 * @param task - Runnable most likely HttpPullProtocolHandler
	 */
	public void addTask(Runnable task) {
		List<Runnable> t = tasks;
		if (t != null) {
			synchronized(t) {
				t.add(task);	// queue up tasks until execute() is called
			}
		} else {
			execute(task);
		}
	}

	/**
	 * Called when a task is leaving the active pool and is not scheduled to
	 * run again.
	 */
	public void threadCompleted(Runnable r, Throwable t) {
		//if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ) {
		//	Agent.getLog().debug( "Task Completed: " + r.toString() );
		//}
	}

}
