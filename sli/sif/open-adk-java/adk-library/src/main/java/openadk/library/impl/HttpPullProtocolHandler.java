//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;

import openadk.library.*;
import openadk.library.infra.SIF_Protocol;
import openadk.library.threadpool.IThreadPoolDelayTask;

import org.mortbay.http.HttpServer;


/**
 * A protocol handler implementation for HTTP. Each zone that is registered with
 * a ZIS using the HTTP or HTTPS protocol has an instance of this class as its
 * protocol handler. It implements the HttpHandler interface to process SIF
 * messages received by the agent's internal Jetty HTTP Server. When a message
 * is received via that interface it is delegated to the zone's
 * MessageDispatcher. HttpProtocolHandler also implements the IProtocolHandler
 * interface so it can send outgoing messages received by the MessageDispatcher.
 *
 * An instance of this class runs in a separate thread only when the agent is
 * registered with the ZIS in Pull mode. In this case it does not accept
 * ,essages from the HttpHandler interface but instead periodically queries the
 * ZIS for new messages waiting in the agent's queue. Messages are delegated to
 * the MessageDispatcher for processing.
 *
 * @author Andrew Elmhorst
 * @version 2.1
 *
 */
public class HttpPullProtocolHandler extends BaseHttpProtocolHandler implements
		Runnable, IThreadPoolDelayTask {

	/**
	 * Creates an instance of HttpPullProtocolHandler running on an HTTP or
	 * HTTPS transport
	 *
	 * @param transport
	 */
	HttpPullProtocolHandler(HttpTransport transport) {
		super(transport);
	}

	private int fDelay;
	
	private boolean fStarted = false;

	@Override
	public synchronized void start() {
		// Polling thread already running?
		if (fStarted) {
			fZone.log
					.debug("Polling thread (HTTP/HTTPS) already running for zone");
			return;
		}

		if ((ADK.debug & ADK.DBG_TRANSPORT) != 0) {
			fZone.log
					.debug("Starting polling thread (HTTP/HTTPS), zone connected in Pull mode...");
		}

		fTransport.fThreadPoolManager.execute(this);
		
		fStarted = true;
	}

	@Override
	public synchronized void shutdown() {
		fStarted = false;
		fDelay = -1;
	}

	@Override
	public synchronized void close(ZoneImpl zone) {
		fTransport.fThreadPoolManager.removeTask(this);
		fDelay = -1;
	}


	/* (non-Javadoc)
	 * @see com.edustructures.sifworks.impl.BaseHttpProtocolHandler#open(com.edustructures.sifworks.impl.ZoneImpl)
	 */
	@Override
	public void open(ZoneImpl zone) throws ADKException {
		super.open(zone);
		fDelay = fZone.getProperties().getPullFrequency();
	}

	/**
	 * Thread periodically sends a SIF_GetMessage request to the ZIS to get the
	 * next message waiting in the agent queue. When running in a thread pool
	 * this method returns after a single pull, otherwise runs until shutdown.
	 */
	public void run() {

		if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0) {
			fZone.log
					.debug("Polling task (HTTP/HTTPS) started");
		}

		
		int freq = fZone.getProperties().getPullFrequency();
		int delay = fZone.getProperties().getPullDelayOnError();
		try {
			if (fZone.isShutdown()) {
				if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0)
					fZone.log
							.debug("Polling thread (HTTP/HTTPS) will stop, zone has shut down");
			} else if (fZone.isSleeping( ADKFlags.QUEUE_LOCAL ) ) {
				if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0){
					fZone.log.debug("Polling thread (HTTP/HTTPS) will delay "
									+ delay
									/ 1000
									+ " seconds, zone is sleeping.");
				}
				delayProcessing(delay);
			} else if (!fZone.isConnected()) {
				if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0){
					fZone.log.debug("Polling thread (HTTP/HTTPS) will delay "
									+ delay
									/ 1000
									+ " seconds, zone is no longer connected");
				}
				delayProcessing(delay);
			} else {
				int i = fZone.getFDispatcher().pull();
				if (i == -1) {
					// The zone is sleeping
					if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0)
						fZone.log.debug("Polling thread (HTTP/HTTPS) will delay "
										+ delay
										/ 1000
										+ " seconds, zone is sleeping");

					delayProcessing(delay);
				} else if (i == 1) {
					// Pulled message from ZIS queue
					delayProcessing(0);	// Reschedule immediately
				} else {
					// No messages in ZIS queue
					delayProcessing(freq);
				}
			}

		} catch (LifecycleException le) {
			// Agent was shutdown - exit gracefully
		} catch (InterruptedException ie) {
			if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0)
				fZone.log.debug("Polling thread (HTTP/HTTPS) interrupted");
			fDelay = -1;	// negative delay removes handler from thread pool
		} catch (Exception adke) {
			// TT139 Andy E. Catch all exceptions, not just ADK Exceptions
			if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0)
				fZone.log
						.debug("Polling thread (HTTP/HTTPS) failed to retrieve message: "
								+ adke);

			//
			// Special Check: If registered in Push mode and we're still
			// pulling, stop the thread - there is no sense in continuing.
			// This should not happen, but just in case...
			//
			if (adke instanceof ADKException
					&& ((ADKException) adke).hasSIFError(
							SIFErrorCategory.REGISTRATION,
							SIFErrorCodes.REG_PUSH_EXPECTED_9)) {
				if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0)
					fZone.log
							.debug("Polling thread (HTTP/HTTPS) will now stop because agent is registered in Push mode");
				
				fDelay = -1;	// negative delay removes handler from thread pool 
				return;
			}

			try {
				delayProcessing(freq); 
			} catch (InterruptedException ie) {
				//We do not hold up the thread here so there is nothing to do
			}
		}

		if ((ADK.debug & ADK.DBG_MESSAGING_PULL) != 0) {
			fZone.log
					.debug("Polling task (HTTP/HTTPS) will delay for "
							+ getScheduleDelay() + "ms");
		}
	}

	/**
	 * Causes the currently executing thread to sleep when not running in a
	 * thread pool. Otherwise notifies the thread pool to reschedule after
	 * delay.
	 * 
	 * @param delay the length of time to sleep in milliseconds
	 * @throws InterruptedException
	 */
	private void delayProcessing(int delay) throws InterruptedException {
		fDelay = delay;	// notify thread scheduler of delay time
	}

	public synchronized boolean isActive(ZoneImpl zone) {
		return fStarted;
	}

	public SIF_Protocol makeSIF_Protocol(Zone zone, SIFVersion version)
			throws ADKTransportException {
		// No SIF_Protocol element is necessary in pull mode
		return null;
	}

	public int getScheduleDelay() {
		return fDelay;
	}

}
