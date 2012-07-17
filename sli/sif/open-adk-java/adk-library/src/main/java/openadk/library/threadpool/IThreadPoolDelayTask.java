//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.threadpool;

/**
 * Interface used in conjunction with {@link ScheduledThreadPoolExecutor} tasks
 * that are scheduled with a fixed delay. This allows individual tasks to
 * dynamically change the delay time before the next scheduled run of the task.
 * 
 */
public interface IThreadPoolDelayTask {

	/**
	 * Gets the number of milliseconds to delay the next run of the task. The
	 * {@link ScheduledThreadPoolExecutor} calls this method immediately after
	 * the tasks run() method returns to set the delay period for the next run
	 * of the task. A return value of 0 will queue the task to run on the next
	 * available thread. A negative value will remove the task from the thread
	 * pool.
	 * 
	 * @return Number of milliseconds to delay before next run of the task.
	 */
	public int getScheduleDelay();
}
