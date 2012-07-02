//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.threadpool;

import openadk.library.ADK;
import openadk.library.Agent;


public class ShutdownExecutionHandler implements RejectedExecutionHandler {

	public ShutdownExecutionHandler() {
		// TODO Auto-generated constructor stub
	}

	public void rejectedExecution(Runnable r, ScheduledThreadPoolExecutor executor) {
		if( ( ADK.debug & ADK.DBG_LIFECYCLE ) != 0 ){
			Agent.getLog().warn( "Task can not be executed because Thread pool is shuting down" );
		}
	}

}
