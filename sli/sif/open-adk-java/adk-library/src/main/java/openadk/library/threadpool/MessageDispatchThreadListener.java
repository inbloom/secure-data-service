//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.threadpool;

public interface MessageDispatchThreadListener {

	public void threadCompleted(Runnable r, Throwable t);
}
