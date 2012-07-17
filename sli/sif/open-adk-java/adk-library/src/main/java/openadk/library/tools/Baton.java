//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools;

/**
 *  A load balancing <i>baton</i> is an object issued by a LoadBalancer to grant
 *  the calling thread the right to perform the logical task associated with
 *  the baton. When the caller is finished performing the task, it must return
 *  the baton to the LoadBalancer so another thread can use it.<p>
 *
 *  Load Balancing Batons provide a conceptually simple way to implement internal
 *  load balancing for arbitrary tasks. A LoadBalancer is configured with a pool
 *  of batons that may be checked out by threads. When a baton is checked out,
 *  the calling thread has the right to perform the operation associated with the
 *  baton. When finished, it returns the baton to the LoadBalancer's pool of
 *  batons so that other threads may now perform the task.<p>
 *
 *  For example, if one of the resource-intensive tasks your agent performs is
 *  to respond to SIF_Requests for "all students", create a LoadBalancer instance
 *  to represent this task. Whenever a SIF_Request is received for "all students",
 *  obtain a Baton from the LoadBalancer. If the baton is received, you may carry
 *  out the task at hand by querying the database for all students and returning
 *  those students as StudentPersonal objects. If the baton is not received in
 *  the timeout period, throw a SIFException with category 10 ("Transport Error"),
 *  which instructs the ZIS to keep the message in the agent's queue for later
 *  retry.<p>
 *
 *  When an agent is unable to obtain a Baton in response to a SIF_Request, it
 *  should put the agent to sleep until the load has relaxed. The LoadBalancer
 *  class offers a simple way to achieve this. When requesting a Baton times out,
 *  call the Zone.sleep method to put the agent to sleep. Next, register a
 *  LoadBalancerListener with the LoadBalancer. When the LoadBalancer has a
 *  free pool of available batons greater than 1, it will call your listener's
 *  onBatonsAvailable method. You can then call Zone.wakeup to wakeup the agent,
 *  signaling the ZIS that it may continue sending messages.<p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public class Baton
{
    public Baton()
	{
    }
}
