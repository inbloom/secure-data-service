//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.net.*;

/**
 *  Properties for the HTTP transport protocol.<p>
 *
 *  To set default HTTPS properties, call the Agent.getDefaultHttpsProperties
 *  method to obtain the agent's default properties for this transport protocol.
 *  The defaults are used by all zones that do not explicitly set their own
 *  transport properties. Alternatively, you may set the default value of a
 *  property by calling the System.setProperty method, or by using the -D option
 *  on the Java command-line. Property names follow the naming convention
 *  <code>adk.transport.http.<i>property</i></code> (e.g.
 *  <code>adk.transport.http.port</code>).
 *
 *  No default HTTP or HTTPS port is
 *  assigned to push mode agents by the class framework. It is the developer's
 *  responsibility to assign a default port. To do so, use one of the following
 *  methods:<p>
 *
 *  <ul>
 *      <li>
 *          Set the <code>adk.transport.http.port</code> system property prior
 *          to creating your agent's Zones and/or Topics. This property can be
 *          set programmatically by calling the System.setProperty method, or
 *          via the -D option on the Java command-line.<br/><br/>
 *      </li>
 *      <li>
 *          Call the setPort method on the default HttpProperties and/or
 *          HttpsProperties objects prior to creating your agent's Zone
 *          instances. The following block of code demonstrates:<br/>
 *          <br/>
 *          <code>
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
 *          </code>
 *      </li>
 *  </ul>
 *  <p>
 *  Http properties can also be set in the agent's configuration file if the ADK's XML
 *  configuration framework is in use. (See {@link openadk.library.tools.cfg.AgentConfig} ).
 *  Here is an example:
 *  
 *  <code>
 *  <pre>
 *  &lt;transport enabled="true" protocol="http"&gt;
 *       &lt;property name="port" value="5580"/&gt;
 *  &lt;/transport&gt;
 *	</pre>
 *  </code>
 *
 *  The properties below are currently defined and can be set in the agent's configuration file
 *  <p>
 *
 *  <table border="1" cellpadding="2" cellspacing="3">
 *
 *      <tr>
 *          <td><center><b>Property / Description</b></center></td>
 *          <td><center><b>Default</b></center></td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>port</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the port that push-mode agents will use when establishing a local socket.
 *          </td>
 *          <td valign="top">
 *              <code>&nbsp;</code>
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>pushPort</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the port that push-mode agents will use when sending a SIF_Register
 * 				message to the zone integration server, if different than the port the
 * 				agent binds on when establishing the local socket. If set this value
 * 				will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
 * 				set, the ADK will use the Port property to prepare this element.
 *          </td>
 *          <td valign="top">
 *              <code>&nbsp;</code>
 *          </td>
 *      </tr>
 *
 *      <tr><td colspan="2"><b><code>host</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the local hostname this transport protocol will use when listening
 *              for incoming traffic from the ZIS.
 *          </td>
 *          <td valign="top">
 *              <code>localhost</code>
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>pushHost</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the hostname that push-mode agents will use when sending a SIF_Register
 * 				message to the zone integration server, if different than the hostname the
 * 				agent binds on when establishing the local socket. If set this value
 * 				will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
 * 				set, the ADK will use the Host property to prepare this element.
 *          </td>
 *          <td valign="top">
 *              (same as 'host' property)
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>servletEnabled</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates whether the ADK is configured to support running a web application.
 *          </td>
 *          <td valign="top">
 *              <code>false</code>
 *          </td>
 *      </tr>
 *
  *      
 *      <tr><td colspan="2"><b><code>maxConnections</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the maximum number of HTTP connections that will be allowed by the ADK
 *          </td>
 *          <td valign="top">
 *              <code>0</code>
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>minConnections</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the minimum number of unused threads used by the HTTP listener thread pool
 *          </td>
 *          <td valign="top">
 *              <code>-1</code>
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>maxIdleTimeMs</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates the maximum time in milliseconds that a thread can be allocated 
 * 				to a connection without a request being received. This limits the 
 * 				duration of idle persistent connections.
 *          </td>
 *          <td valign="top">
 *              <code>10000</code>
 *          </td>
 *      </tr>
 *      
 *      <tr><td colspan="2"><b><code>lowResourcesPersistTimeMs</code></b></td></tr>
 *      <tr>
 *          <td>
 *              Indicates An alternative value for maxIdleTimeMs to be used when the listener is 
 *              low on resources
 *          </td>
 *          <td valign="top">
 *              <code>1000</code>
 *          </td>
 *      </tr>

 *    </table>
 *  <p>
 *
 *
 * 
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class HttpProperties extends TransportProperties
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1536559953518697543L;

	/**
	 *  Constructor
	 */
	public HttpProperties()
	{
		super();
	}

	/**
	 *  Constructs an HttpProperties object that inherits values from a parent
	 *  @param parent The HttpProperties object from which properties
	 *      will be inherited if not explicitly defined by this object
	 */
	public HttpProperties( HttpProperties parent )
	{
		super(parent);
	}

	/**
	 *  Gets the name of the transport protocol associated with these properties
	 *  @return The string "http"
	 */
	public String getProtocol()
	{
		return "http";
	}

	/**
	 *  Sets the port that push-mode agents will use when establishing a local socket.<p>
	 *  @param port A valid port number (some platforms restrict port numbers
	 *      in the range 0-1024)
	 */
	public void setPort( int port )
	{
		setProperty( "port", port );
	}

	/**
	 *  Gets the port that push-mode agents will use when establishing a local socket.<p>
	 *  @return The port number or -1 if uninitialized
	 */
	public int getPort()
	{
		return getProperty( "port", -1 );
	}

	/**
	 *  Sets the port that push-mode agents will use when sending a SIF_Register
	 * 	message to the zone integration server, if different than the port the
	 * 	agent binds on when establishing the local socket. If set this value
	 * 	will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
	 * 	set, the ADK will use the Port property to prepare this element.<p>
	 * 	
	 * 	Most agents do not call this method. It is only needed when the network
	 * 	configuration demands the hostname and port the ZIS uses to contact the
	 * 	agent is different than the local socket the agent binds on.<p>
	 *
	 *  @param port The port to be used in the SIF_Register/SIF_Protocol/SIF_URL element
	 * 
	 * 	@since ADK 1.5
	 */
	public void setPushPort( int port )
	{
		setProperty( "pushPort", port );
	}

	/**
	 *  Gets the port that push-mode agents will use when sending a SIF_Register
	 * 	message to the zone integration server, if different than the port the
	 * 	agent binds on when establishing the local socket. If set this value
	 * 	will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
	 * 	set, the ADK will use the Port property to prepare this element.<p>
	 *
	 *  @return The port to be used in the SIF_Register/SIF_Protocol/SIF_URL element
	 * 		or -1 if not initialized
	 * 
	 * 	@since ADK 1.5
	 */
	public int getPushPort()
	{
		return getProperty( "pushPort", -1 );
	}
	
	/**
	 *  Sets the local host name or IP address this transport protocol will use
	 *  when establishing a local socket. By default, <i>localhost</i> is assumed.
	 *  This property is useful when more than one network interface is available
	 *  on the machine, or when the agent wishes to exercise more control over
	 *  the exact hostname or IP address that is registered with the ZIS (e.g.
	 *  "localhost" versus "127.0.0.1" versus the IP address or DNS host name of
	 *  the local machine.)
	 *  <p>
	 *
	 *  @param host The IP address of a network interface on the local machine,
	 *      or a domain name that resolves to a local IP address. (No attempt is
	 *      made to check that the address is valid.)
	 */
	public void setHost( String host )
	{
		setProperty( "host", host );
	}

	/**
	 *  Gets the local hostname this transport protocol will use when listening
	 *  for incoming traffic from the ZIS.<p>
	 *  @return The host name or IP address passed to the setHost method.
	 *      Defaults to "localhost"
	 * @throws ADKTransportException 
	 */
	public String getHost()
		throws ADKTransportException
	{
		String def = null;

		try {
			def = InetAddress.getLocalHost().getHostAddress();
		} catch( Exception e ) {
			throw new ADKTransportException("HttpProperties.getHost() could not resolve localhost to an IP address",null);
		}

		return getProperty( "host", def );
	}
	
	/**
	 *  Sets the hostname that push-mode agents will use when sending a SIF_Register
	 * 	message to the zone integration server, if different than the hostname the
	 * 	agent binds on when establishing the local socket. If set this value
	 * 	will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
	 * 	set, the ADK will use the Host property to prepare this element.<p>
	 * 	
	 * 	Most agents do not call this method. It is only needed when the network
	 * 	configuration demands the hostname and port the ZIS uses to contact the
	 * 	agent is different than the local socket the agent binds on.<p>
	 * 
	 *  @param host The hostname to be used in the SIF_Register/SIF_Protocol/SIF_URL element
	 * 
	 * 	@since ADK 1.5
	 */
	public void setPushHost( String host )
	{
		setProperty( "pushHost", host );
	}

	/**
	 *  Gets the hostname that push-mode agents will use when sending a SIF_Register
	 * 	message to the zone integration server, if different than the hostname the
	 * 	agent binds on when establishing the local socket. If set this value
	 * 	will be used in preparing the SIF_Register/SIF_Protocol/SIF_URL. If not
	 * 	set, the ADK will use the Host property to prepare this element.<p>
	 * 	
	 * 	Most agents do not call this method. It is only needed when the network
	 * 	configuration demands the hostname and port the ZIS uses to contact the
	 * 	agent is different than the local socket the agent binds on.<p>
	 *
	 * @return The hostname to be used in the SIF_Register/SIF_Protocol/SIF_URL element
	 * 		or null if not initialized
	 * @throws ADKTransportException 
	 * 
	 * 	@since ADK 1.5
	 */
	public String getPushHost()
		throws ADKTransportException
	{
		return getProperty( "pushHost", null );
	}
	
	/**
	 * Gets whether the ADK is configured to support running a web application.
	 * @return True if the ADK is configured to support running a web application
	 */
	public boolean getServletEnabled()
	{
		return getProperty( "servletEnabled", false );
	}
	
	/**
	 * Sets whether the ADK is configured to support running a web application.
	 * @param enabled True if ADK should configure Jetty to support running a web application
	 */
	public void setServletEnabled( boolean enabled )
	{
		setProperty( "servletEnabled", enabled );
	}

	/**
	 * Gets the maximum number of HTTP connections that will be allowed by the ADK
	 * @return the maximum number of HTTP connections that will be allowed by the ADK
	 */
	public int getMaxConnections() {
		return getProperty( "maxConnections", 0 );
	}
	
	/**
	 * Sets the maximum number of HTTP connections that will be allowed by the ADK
	 * @param maxConnections the maximum number of HTTP connections that will be allowed by the ADK
	 */
	public void setMaxConnections( int maxConnections ) {
		setProperty( "maxConnections", maxConnections );
	}

	/**
	 * Gets the minimum number of unused threads used by the HTTP listener thread pool
	 * @return the minimum number of unused threads used by the HTTP listener thread pool
	 */
	public int getMinConnections() {
		return getProperty( "minConnections", -1 );
	}
	
	/**
	 * Sets the minimum number of unused threads used by the HTTP listener thread pool
	 * @param minConnections the minimum number of unused threads used by the HTTP listener thread pool
	 */
	public void setMinConnections( int minConnections ) {
		setProperty( "minConnections", minConnections );
	}

	/**
	 * Gets the maximum time in milliseconds that a thread can be allocated 
	 * to a connection without a request being received. This limits the 
	 * duration of idle persistent connections.
	 * @return the maximum time in milliseconds that a thread can be allocated 
	 * to a connection
	 */
	public int getMaxIdleTimeMs() {
		return getProperty( "maxIdleTimeMs", 10000);
	}
	
	/**
	 * Sets the maximum time in milliseconds that a thread can be allocated 
	 * to a connection without a request being received. This limits the 
	 * duration of idle persistent connections.
	 * @param idleTime the maximum time in milliseconds that a thread can be allocated 
	 * to a connection
	 */
	public void setMaxIdleTimeMs( int idleTime ) {
		setProperty( "maxIdleTimeMs", idleTime );
	}


	/**
	 * An alternative value for maxIdleTimeMs to be used when the listener is 
	 * low on resources
	 * @return the low resources limit
	 */
	public int getLowResourcesPersistTimeMs() {
		return getProperty( "lowResourcesPersistTimeMs", 1000 );
	}
	
	/**
	 * Sets an alternative value for maxIdleTimeMs to be used when the listener is 
	 * low on resources
	 * @param persistTimeMs the low resources limit (milliseconds)
	 */
	public void setLowResourcesPersistTimeMs( int persistTimeMs ) {
		setProperty( "lowResourcesPersistTimeMs", persistTimeMs );
	}
	
	
}
