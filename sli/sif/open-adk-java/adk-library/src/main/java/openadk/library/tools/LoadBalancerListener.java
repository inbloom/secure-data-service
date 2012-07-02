//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools;

/**
 *  This interface is implemented by classes that wish to be notified when a
 *  LoadBalancer's free pool increases from zero to greater than one, signaling
 *  Batons are once again available to the thread.<p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public interface LoadBalancerListener
{
	/**
	 *  Called when a LoadBalancer's free pool increases from zero to greater
	 *  than one, signaling Batons are once again available to this thread.
	 */
	public void onBatonsAvailable( LoadBalancer balancer );
}
