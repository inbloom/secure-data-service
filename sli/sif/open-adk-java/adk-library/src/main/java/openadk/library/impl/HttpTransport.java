//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import openadk.library.*;
import openadk.library.common.*;
import openadk.library.infra.*;
import openadk.library.threadpool.ThreadPoolManager;

import org.mortbay.http.*;
import org.mortbay.util.*;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

import javax.net.ssl.SSLServerSocket;

/**
 * Transport class for the HTTP and HTTPS protocols.
 * <p>
 * 
 * One instance of HttpTransport is shared among all zones that use the HTTP or
 * HTTPS protocols. When activated, it creates an internal Jetty HTTP/HTTPS web
 * server for use by all zones the agent is connected to using these protocols.
 * Each zone then instantiates its own HttpProtocolHandler, which is responsible
 * for sending and receiving messages. (The HttpTransport class does not send or
 * receive messages.)
 * <p>
 * 
 * Because many SIF Agents may use the ADK, no default HTTP or HTTPS port is
 * assigned to push mode agents by the class framework. It is the developer's
 * responsibility to assign a default port (pull-mode agents do not require a
 * port be assigned.) To do so, use one of the following methods:
 * <p>
 * 
 * <ul>
 * <li> Set the <code>adk.transport.http.port</code> system property prior to
 * creating your agent's Zones and/or Topics. This property can be set
 * programmatically by calling the System.setProperty method, or via the -D
 * option on the Java command-line. </li>
 * <li> Call the setPort method on the default HttpProperties and/or
 * HttpsProperties objects prior to creating your agent's Zones and/or Topics.
 * The following block of code demonstrates:<br/> <br/> <code>
 *
 *			//  Set transport properties for HTTP<br/>
 *			Agent myAgent = ...<br/>
 *			HttpProperties http = agent.getDefaultHttpProperties();<br/>
 *          http.setPort( 7081 );<br/>
 *          <br/>
 *          //  Set transport properties for HTTPS<br/>
 *			HttpsProperties https = agent.getDefaultHttpsProperties();<br/>
 *          https.setPort( 7082 );<br/>
 *          https.setKeystorePassword( "changeit" );<br/>
 *          ...<br/>
 *          <br/>
 *      </li>
 *  </ul>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class HttpTransport extends TransportImpl {
	/** DO NOT ENABLE IN PRODUCTION CODE * */
	private static final boolean USE_CDATA_IN_PUSHURL = false;

	/**
	 * The root log Category. Subcategories exist for each zone, where the
	 * subcategory name is "Agent.<i>zoneId</i>". The ADK uses the root
	 * Category when writing log events that are not associated with a specific
	 * zone. Your agent may also use this Category to post log events.
	 */
	protected Category log = null;

	/**
	 * The internal Jetty http/https server
	 */
	protected static HttpServer sServer = null;
	
	/**
	 * a reference to the ThreadPoolManager instance to execute pull tasks in
	 */
	protected ThreadPoolManager fThreadPoolManager = null;

	/**
	 * Constructs an HttpTransport for HTTP or HTTPS
	 * 
	 * @param props
	 *            Transport properties (usually an instance of HttpProperties or
	 *            HttpsProperties)
	 */
	protected HttpTransport(HttpProperties props) throws ADKTransportException {
		super(props);

		log = Logger.getLogger("ADK.Agent.transport$" + fProps.getProtocol());
	}
	
	/**
	 * Clone this HttpTransport.
	 * <p>
	 * 
	 * Cloning a transport results in a new HttpTransport instance with
	 * HttpProperties that inherit from this object's properties. However, the
	 * Jetty web server owned by this transport will be shared with the cloned
	 * instance.
	 */
	public Transport cloneTransport() throws ADKTransportException {
		HttpProperties props = null;
		if (fProps instanceof HttpProperties)
			props = new HttpProperties((HttpProperties) fProps.getParent());
		else if (fProps instanceof HttpsProperties)
			props = new HttpsProperties((HttpsProperties) fProps.getParent());

		HttpTransport t = new HttpTransport(props);

		// This object and the clone share the Jetty server
		t.sServer = sServer;

		return t;
	}

	/**
	 * Activate this Transport for an agent. This method is called when the
	 * agent is initialized
	 * 
	 * @param agent
	 *            The zone
	 */
	public synchronized void activate(Agent agent) throws ADKTransportException {
		activateServer(agent, agent.getProperties(), false );
	}

	/**
	 * Activates the embedded HTTP server if the agent is configured to run in
	 * push more or support for servlets is enabled
	 * 
	 * @param agentProps
	 * @return True if the server is activated
	 * @throws ADKTransportException
	 */
	private synchronized boolean activateServer(Agent agent, AgentProperties agentProps, boolean isPushModeZone ) 
		throws ADKTransportException 
	{
		HttpProperties props = (HttpProperties) fProps;
		fThreadPoolManager = agent.getThreadPoolManager();
		boolean webAppEnabled = props.getServletEnabled();
		// Create the http server if it doesn't exist. All HttpTransport
		// objects share once instance of the server.
		if ( (sServer == null || !sServer.isStarted() )
				&& ( webAppEnabled || isPushModeZone ) ) {
			if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isInfoEnabled()) {
				log.info("Activating " + fProps.getProtocol().toUpperCase()
						+ " transport...");
			}
			if (sServer == null) {
				try {
					if (webAppEnabled) {
						sServer = new org.mortbay.jetty.Server();
					} else {
						sServer = new org.mortbay.http.HttpServer();
					}
				} catch (NoClassDefFoundError ncdfe) {
					String message = null;
					if (ncdfe.getMessage().endsWith("LogFactory")) {
						message = "Unable to start Jetty Server. Please add commons-logging.jar to the classpath."
								+ ncdfe.getMessage();
					} else {
						message = "Unable to start Jetty Server. "
								+ ncdfe.getMessage();
					}
					ADKTransportException adkte = new ADKTransportException(
							message, null, ncdfe);
					throw adkte;
				}

				// The following is an undocumented feature of the ADK
				if (agentProps.getProperty("adk.transport.NCSARequestLog",
						false)) {
					// The following code turns on standard weblogging, but may
					// not turn
					// out to be all that useful
					NCSARequestLog serverRequestLog = new NCSARequestLog();
					File f = new File(agent.getHomeDir() + "/weblogs");
					f.mkdirs();
					serverRequestLog.setFilename(f.getAbsolutePath()
							+ "/yyyy_mm_dd.request.log");
					serverRequestLog.setRetainDays(30);
					serverRequestLog.setAppend(true);
					serverRequestLog.setExtended(true);

					sServer.setRequestLog(serverRequestLog);
				}

			}
		}

		try {
			// Start the server if not already started
			if (sServer != null && !sServer.isStarted()) {
				if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isInfoEnabled()) {
					log.info("Starting HTTP/HTTPS server...");
				}
				sServer.start();
			}
		} catch (Exception ex) {
			ADKTransportException te = new ADKTransportException(
					"Failed to activate " + fProps.getProtocol().toUpperCase()
							+ " transport on " + getHost() + ":" + getPort(),
					null, ex);
			if (ex instanceof MultiException) {
				MultiException me = (MultiException) ex;
				List inners = me.getExceptions();
				for (Iterator it = inners.iterator(); it.hasNext();) {
					Throwable th = (Throwable) it.next();
					te.add(th);
				}
			} else {
				te.add(ex);
			}
			ADKUtils._throw(te, log);
		}

		return sServer != null;

	}

	/**
	 * Activate this Transport for a zone. This method is called for every zone
	 * when it is being connected
	 * 
	 * @param zone
	 *            The zone
	 */
	public synchronized void activate(Zone zone) throws ADKTransportException {

		boolean isPushMode = zone.getProperties().getMessagingMode() == AgentMessagingMode.PUSH;
		if ( activateServer( zone.getAgent(), zone.getProperties(), isPushMode ) && isPushMode ) {
			// Configure the server specifically for this Transport
			configureServer( zone );
		}
	}

	/**
	 * Is this Transport activated?
	 */
	public boolean isActive(Zone zone) throws ADKTransportException {
		if (zone.getProperties().getMessagingMode() == AgentMessagingMode.PUSH) {
			if (sServer != null && sServer.isStarted()) {
				HttpListener[] listeners = sServer.getListeners();
				for (int i = 0; i < listeners.length; i++) {
					if (!listeners[i].isStarted())
						return false;
				}
			} else
				return false;
		}

		return true;
	}

	/**
	 * Shutdown this Transport
	 */
	public synchronized void shutdown() throws ADKTransportException {
		if (sServer != null) {
			try {
				// Remove listener
				int port = getPort();
				HttpListener[] listeners = sServer.getListeners();
				for (int i = 0; i < listeners.length; i++) {
					if (listeners[i] instanceof SocketListener
							&& listeners[i].getPort() == port) {
						sServer.removeListener(listeners[i]);
						break;
					}
				}
				sServer.stop();
			} catch (InterruptedException ie) {
				log.warn("Error shutting down Jetty Server: " + ie, ie);
			}
		}
	}

	/**
	 * Configure the Jetty server for HTTP as needed based on the settings of
	 * this Transport object. If the server does not have a SocketListener on
	 * the port specified for this transport, one is created. Jetty
	 * configuration is performed dynamically as HttpTransport objects are
	 * created, so listeners are added to the server the first time they are
	 * needed.
	 */
	protected void configureServer(Zone zone) throws ADKTransportException {
		
		SocketListener newListener = null;
		if (fProps.getProtocol().equalsIgnoreCase("http")){
			newListener = configureHttp(zone);
		} else if (fProps.getProtocol().equalsIgnoreCase("https")){
			newListener = configureHttps(zone);
		} else {
			throw new InternalError(
					"HttpTransport object configured with properties for another protocol: "
							+ fProps.getProtocol());
		}
		
		if( newListener != null ){
			sServer.addListener( newListener );
			try
			{
				newListener.start();
			}
			catch( Exception le ){
				ADKUtils._throw( new ADKTransportException( "Error starting SocketListener: " + le.getMessage(), zone, le ), log );
			}
			
			if( fProps.getProtocol().equalsIgnoreCase("https") ){
				
				String allowedCiphers = fProps.getProperty( "ciphers" );
				if( allowedCiphers != null && allowedCiphers.length() > 0 ){
					
					log.debug( "Setting the set of allowed ciphers to " + allowedCiphers );
					String[] allowed = allowedCiphers.split( "," );
					
					SunJsseListener jsse = (SunJsseListener)newListener;
					SSLServerSocket socket = (SSLServerSocket)jsse.getServerSocket();
					
					List<String> ciphers = new ArrayList<String>();
					for( String cipher : socket.getEnabledCipherSuites() ){
						if( Arrays.binarySearch( allowed, cipher ) < 0 ) {
							log.debug( "Disabling cipher: " + cipher );
						}else {
							log.debug( "Enabling cipher: " + cipher );
							ciphers.add( cipher );
						}
					}
					
					String[] enabled = new String[ ciphers.size() ];
					ciphers.toArray( enabled );
					socket.setEnabledCipherSuites( enabled );
					
	//				for( String pro : socket.getEnabledProtocols() ){
	//					System.out.println( pro );
	//				}
	//				
					for( String cipher : socket.getEnabledCipherSuites() ){
						log.debug( cipher + " is enabled for this session." );
					}
				
				}
				
				
			}
			
		}
	}

	/**
	 * Configures an HTTP listener
	 * @param zone The zone to use for configuring properties
	 * @return The SocketListener that was configured, or null
	 * @throws ADKTransportException
	 */
	protected SocketListener configureHttp(Zone zone) throws ADKTransportException {
		int port = getPort();
		if (port == -1)
			throw new ADKTransportException(
					"The agent is not configured with a default HTTP port",
					zone);

		String optHost = getHost();

		// If there is no SocketListener on this port, create one
		HttpListener listener = null;
		HttpListener[] listeners = sServer.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] instanceof SocketListener
					&& listeners[i].getPort() == port) {
				if (optHost != null
						&& listeners[i].getHost().equalsIgnoreCase(optHost))
					listener = listeners[i];
			}
		}

		if (listener == null) {
			if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isInfoEnabled()) {
				if (optHost != null) {
					log.info("Creating HTTP listener for push mode on "
							+ optHost + ":" + port);
				} else {
					log.info("Creating HTTP listener for push mode on port "
							+ port);
				}
			}

			SocketListener http = new SocketListener();
			configureSocketListener(http, zone, port, optHost);
			return http;
		} else {
			if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isDebugEnabled()) {
				if (optHost != null) {
					log.debug("Already a HTTP listener on " + optHost + ":"
							+ port);
				} else {
					log.debug("Already a HTTP listener on port " + port);
				}
			}
		}
		return null;
	}

	/**
	 * Configure the Jetty server for HTTPS as needed based on the settings of
	 * this Transport object. If the server does not have a JSSEListener on the
	 * port specified for this transport, one is created. Jetty configuration is
	 * performed dynamically as HttpTransport and HttpsTransport objects are
	 * created, so listeners are added to the server the first time they are
	 * needed.
	 * @return A SocketListener if a new one was created, or null
	 */
	protected SocketListener configureHttps(Zone zone) throws ADKTransportException {
		int port = getPort();
		if (port == -1) {
			throw new ADKTransportException(
					"The agent is not configured with a default HTTPS port",
					zone);
		}

		String optHost = getHost();

		// If there is no SunJsseListener on this port, create one
		HttpListener listener = null;
		HttpListener[] listeners = sServer.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] instanceof SocketListener
					&& listeners[i].getPort() == port) {
				if (optHost != null	&& listeners[i].getHost().equalsIgnoreCase(optHost))
					listener = listeners[i];
			}
		}

		if (listener == null) {
			try {
				String ks = getKeyStore();
				String ksPwd = getKeyStorePassword();

				if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isInfoEnabled()) {
					if (optHost == null) {
						log
								.info("Creating HTTPS listener for push mode on port "
										+ port);
					} else {
						log.info("Creating HTTPS listener for push mode on "
								+ optHost + ":" + port);
					}

					if (ks == null) {
						log.info("Using default Java keystore");
					} else {
						log.info("Using keystore: " + ks);
					}

					if (ksPwd.equals("changeit"))
						log
								.info("Using default Java keystore password 'changeit'");

					if (fProps instanceof HttpsProperties)
						log
								.info("Requiring client authentication: "
										+ (((HttpsProperties) fProps)
												.getRequireClientAuth() ? "yes"
												: "no"));
				}

				SunJsseListener https = new SunJsseListener();
				
				configureSocketListener(https, zone, port, optHost);

				if (ks != null)
					https.setKeystore(ks);
				https.setKeyPassword(ksPwd);
				String pwd = getPassword();
				if (pwd == null) {
					https.setPassword(ksPwd);
				} else {
					https.setPassword(pwd);
				}

				HttpsProperties httpsProps = (HttpsProperties) fProps;
				String ts = httpsProps.getTrustStore();
				String tsPwd = httpsProps.getTrustStorePassword();
				if (tsPwd == null)
					tsPwd = "changeit";

				if (ts != null) {
					File tsFile = new File(ts);
					if (!tsFile.exists())
						throw new ADKTransportException(
								"Truststore file not found: "
										+ tsFile.getAbsolutePath(), zone);
					log.info("(HttpTransport) Using truststore: "
							+ tsFile.getAbsolutePath());
					System.setProperty("javax.net.ssl.trustStore", ts);
					System.setProperty("javax.net.ssl.trustStorePassword",
							tsPwd);
				} else {
					log.info("Using default Java truststore");
				}
				// The following property tells the SunJsseListener to 
				// use a seperate truststore from the keystore. Note that if
				// the truststore file is set above, it will be used, rather
				// than the 'default' java truststore
				https.setUseDefaultTrustStore(true);

				if (fProps instanceof HttpsProperties) {
					https.setNeedClientAuth(((HttpsProperties) fProps)
							.getRequireClientAuth());
				}
				return https;
				
			} catch (Exception ioe) {
				throw new ADKTransportException(
						"Error configuring HTTPS transport: " + ioe, zone);
			}
		} else {
			if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isDebugEnabled()) {
				if (optHost != null) {
					log.debug("Already a HTTPS listener on " + optHost + ":"
							+ port);
				} else {
					log.debug("Already a HTTPS listener on port " + port);
				}
			}
		}
		return null;
	}

	/**
	 * Configures common settings for the Http Listener
	 * 
	 * @param listener
	 * @param zone
	 * @param port
	 * @param hostName
	 */
	private void configureSocketListener(SocketListener listener, Zone zone,
			int port, String hostName) {

		listener.setPort(port);
		if (hostName != null) {
			try {
				listener.setHost(hostName);
			} catch (Exception uhe) {
				log.warn("Could not change the local socket address to '"
						+ hostName + "': " + uhe, uhe);
			}
		}

		//
		// TT 1440 Add support for a "Max-Connections" feature in the ADK
		// This is currently an experimental, undocumented and untested feature.
		// See http://jetty.mortbay.org/jetty5/doc/optimization.html for more
		// information on optimization with Jetty
		HttpProperties httpProps = (HttpProperties) fProps;
		int maxRequestThreads = httpProps.getMaxConnections();
		if (maxRequestThreads > 0) {
			listener.setMaxThreads(maxRequestThreads);

			int minRequestThreads = httpProps.getMinConnections();
			if (minRequestThreads < 0) {
				minRequestThreads = (int) Math.ceil(maxRequestThreads / 5);
			}
			listener.setMinThreads(minRequestThreads);

			int maxIdleTimeMs = httpProps.getMaxIdleTimeMs();
			if (maxIdleTimeMs > 0) {
				listener.setMaxIdleTimeMs(maxIdleTimeMs);
			}
			int lowResourcesPersistTimeMs = httpProps
					.getLowResourcesPersistTimeMs();
			if (lowResourcesPersistTimeMs > 0) {
				listener.setLowResourcePersistTimeMs(lowResourcesPersistTimeMs);
			}

			if ((ADK.debug & ADK.DBG_TRANSPORT) != 0 && log.isDebugEnabled()) {
				log.debug("Set HttpListener.maxThreads to "
						+ String.valueOf(maxRequestThreads));
				if (minRequestThreads > 0) {
					log.debug("Set HttpListener.minThreads to "
							+ String.valueOf(minRequestThreads));
				}
				if (maxIdleTimeMs > 0) {
					log.debug("Set HttpListener.maxIdleTimeMs to "
							+ String.valueOf(maxIdleTimeMs));
				}
				if (lowResourcesPersistTimeMs > 0) {
					log.debug("Set HttpListener.lowResourcesPersistTimeMs to "
							+ String.valueOf(lowResourcesPersistTimeMs));
				}
			}
		}
	}

	/**
	 * Create the IProtocolHandler for this transport
	 * <p>
	 * 
	 * @return An instance of HttpProtocolHandler
	 */
	@Override
	public IProtocolHandler createProtocolHandler(AgentMessagingMode mode)
			throws ADKTransportException {
		if (mode == AgentMessagingMode.PULL) {
			return new HttpPullProtocolHandler(this);
		} else {
			if (sServer == null) {
				throw new ADKTransportException(
						"HttpTransport is not Activated", null);
			}
			return new HttpPushProtocolHandler(this, sServer);
		}
	}

	/**
	 * Gets the name of this transport protocol
	 * 
	 * @return The unique name of the transport protocol, used to identify this
	 *         transport among all transports that may be active in the agent
	 * @see #getProtocol
	 */
	public String getName() {
		return fProps.getProtocol();
	}

	/**
	 * Gets the protocol name
	 * 
	 * @return The protocol name used in constructing URLs (e.g. "http",
	 *         "https", etc.)
	 */
	public String getProtocol() {
		return fProps.getProtocol();
	}

	/**
	 * Determines if this transport is secure or not
	 */
	public boolean isSecure() {
		return getProtocol().equalsIgnoreCase( "https");
	}

	/**
	 * Gets the internal Jetty HTTP/HTTPS server. The server is used by all
	 * instances of the HttpTransport for both HTTP and HTTPS communications
	 * with the ZIS.
	 * 
	 * @return The Jetty HttpServer object
	 */
	public HttpServer getServer() {
		return sServer;
	}

	/**
	 * Sets the local port this transport protocol will use when listening for
	 * incoming traffic from the ZIS. The port is assigned to the
	 * TransportProperties object passed to the constructor.
	 * 
	 * @param port
	 *            The port number
	 */
	public void setPort(int port) {
		((HttpProperties) fProps).setPort(port);
	}

	/**
	 * Gets the local port this transport protocol will use when listening for
	 * incoming traffic from the ZIS.
	 * 
	 * @return The port number returned by the TransportProperties object passed
	 *         to the constructor
	 */
	public int getPort() {
		return ((HttpProperties) fProps).getPort();
	}

	/**
	 * Sets the local hostname this transport protocol will use when listening
	 * for incoming traffic from the ZIS. By default, localhost is assumed. The
	 * hostname is assigned to the TransportProperties object passed to the
	 * constructor.
	 * 
	 * @param host
	 *            The host name used for this protocol
	 */
	public void setHost(String host) {
		((HttpProperties) fProps).setHost(host);
	}

	/**
	 * Gets the local hostname this transport protocol will use when listening
	 * for incoming traffic from the ZIS. By default, localhost is assumed.
	 * 
	 * @return The hostname returned by the TransportProperties object passed to
	 *         the constructor
	 * @throws ADKTransportException
	 */
	public String getHost() throws ADKTransportException {
		return ((HttpProperties) fProps).getHost();
	}

	/**
	 * Gets the keystore file this transport protocol will use for HTTPS.
	 * <p>
	 * 
	 * This method has no effect if HttpsProperties were not passed to the
	 * constructor.
	 * <p>
	 * 
	 * @return The keystore file returned by the HttpProperties passed to the
	 *         constructor
	 */
	public String getKeyStore() {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			return ((HttpsProperties) fProps).getKeyStore();

		return null;
	}

	/**
	 * Sets the keystore file this transport protocol will use for HTTPS.
	 * 
	 * This method has no effect if HttpsProperties were not passed to the
	 * constructor.
	 * <p>
	 * 
	 * @param keyStore
	 *            The path to a keystore file. The keystore value is assigned to
	 *            the HttpsProperties passed to the constructor.
	 */
	public void setKeyStore(String keyStore) {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			((HttpsProperties) fProps).setKeyStore(keyStore);
	}

	/**
	 * Gets the keystore password this transport protocol will use when setting
	 * up HTTPS.
	 * <p>
	 * 
	 * This method has no effect if HttpsProperties were not passed to the
	 * constructor.
	 * <p>
	 * 
	 * @return The keystore password returned by the HttpsProperties passed to
	 *         the constructor
	 */
	public String getKeyStorePassword() {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			return ((HttpsProperties) fProps).getKeyStorePassword();

		return null;
	}

	/**
	 * Sets the keystore password this transport protocol will use when setting
	 * up HTTPS. This value does not apply to HTTP connections. The keystore
	 * password value is assigned to the HttpProperties passed to the
	 * constructor.
	 * 
	 * @param keyStorePass
	 *            the keystore password
	 */
	public void setKeyStorePassword(String keyStorePass) {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			((HttpsProperties) fProps).setKeyStorePassword(keyStorePass);
	}

	/**
	 * Gets the key password this transport protocol will use when setting up
	 * HTTPS. When a null value is returned, the caller should use the same
	 * password as the keystore password. This value does not apply to HTTP
	 * connections.
	 * 
	 * @return The key password returned by the HttpProperties passed to the
	 *         constructor
	 */
	public String getPassword() {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			return ((HttpsProperties) fProps).getPassword();

		return null;
	}

	/**
	 * Sets the key password this transport protocol will use when setting up
	 * HTTPS. It is typically the same as the keystore password. This value does
	 * not apply to HTTP connections. The key password is assigned to the
	 * HttpProperties passed to the constructor.
	 * 
	 * @param pass
	 *            the key password
	 */
	public void setPassword(String pass) {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			((HttpsProperties) fProps).setPassword(pass);
	}

	/**
	 * Enable or disable Client Authentication
	 * 
	 * @param required
	 *            True if client authentication is required
	 */
	public void setRequireClientAuth(boolean required) {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			((HttpsProperties) fProps).setRequireClientAuth(required);
	}

	/**
	 * Determines if Client Authentication is required
	 * 
	 * @return true if Client Authentication if required
	 */
	public boolean getRequireClientAuth() {
		if (fProps.getProtocol().equalsIgnoreCase("https"))
			return ((HttpsProperties) fProps).getRequireClientAuth();

		return false;
	}

	/**
	 * Modifies the SIF_Protocol element to add Http specific properties
	 * @param proto
	 * @param zone
	 * @throws ADKTransportException
	 */
	public void configureSIF_Protocol( SIF_Protocol proto, Zone zone, SIFVersion versionOfMessage)
		throws ADKTransportException
	{
		proto.setType( getProtocol().toUpperCase() );
		proto.setSecure( isSecure() ? YesNo.YES : YesNo.NO);
		
		String host = ((HttpProperties) fProps).getPushHost();
		if (host == null || host.trim().length() == 0)
			host = getHost();

		int port = ((HttpProperties) fProps).getPushPort();
		if (port == -1)
			port = getPort();
		
		
		if (SIFVersion.SIF21.compareTo(versionOfMessage) <= 0) {
			SIF_Property acceptEncoding = null;
			for (SIF_Property sifProperty : proto.getSIF_Propertys()) {
				if ("Accept-Encoding".equals(sifProperty.getSIF_Name())) {
					acceptEncoding = sifProperty;
					break;
				}
			}
			if (acceptEncoding == null) {
				acceptEncoding = new SIF_Property("Accept-Encoding", "gzip;q=1.0, identity;q=0.5, *;q=0");
				proto.addSIF_Property(acceptEncoding);
			}
		}

		proto.setSIF_URL(getProtocol() + "://" + host + ":" + port
				+ "/zone/" + zone.getZoneId() + "/");
		
	}
}
