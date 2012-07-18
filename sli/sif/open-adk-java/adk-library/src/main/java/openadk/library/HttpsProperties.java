//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Properties for the HTTPS transport protocol.<p>
 *
 *  To set default HTTPS properties, call the Agent.getDefaultHttpsProperties
 *  method to obtain the agent's default properties for this transport protocol.
 *  The defaults are used by all zones that do not explicitly set their own
 *  transport properties. Alternatively, you may set the default value of a
 *  property by calling the System.setProperty method, or by using the -D option
 *  on the Java command-line. Property names follow the naming convention
 *  <code>adk.transport.https.<i>property</i></code> (e.g.
 *  <code>adk.transport.https.port</code>).
 *  <p>
 *
 *  No default HTTP or HTTPS port is
 *  assigned to push mode agents by the class framework. It is the developer's
 *  responsibility to assign a default port. To do so, use one of the following
 *  methods:<p>
 *
 *  <ul>
 *      <li>
 *          Set the <code>adk.transport.https.port</code> system property prior
 *          to creating your agent's Zones and/or Topics. This property can be
 *          set programmatically by calling the System.setProperty method, or
 *          via the -D option on the Java command-line.<br/><br/>
 *      </li>
 *      <li>
 *          Call the setPort method on the default HttpProperties and/or
 *          HttpsProperties objects prior to creating and your agent's Zones
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
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public class HttpsProperties extends HttpProperties
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8977063312750602786L;

	/**
	 *  Constructor
	 */
	public HttpsProperties()
	{
		super();
	}

	/**
	 *  Constructs an HttpsProperties object that inherits values from a parent
	 *  @param parent The Http or HttpsProperties object from which properties
	 *      will be inherited if not explicitly defined by this object
	 */
	public HttpsProperties( HttpProperties parent )
	{
		super(parent);
	}

	/**
	 *  Gets the name of the transport protocol associated with these properties
	 *  @return The protocol name ("https")
	 */
	@Override
	public String getProtocol()
	{
		return "https";
	}

	/**
	 *  Gets the path to the keystore where the agent's certificate is stored.
	 *  @return The path to the agent keystore, or null if the default Java
	 *      system keystore is used. (The location of that keystore is OS-dependent
	 *      but in most cases is in the user's home directory in a file named
	 *      ".keystore")
	 */
	public String getKeyStore()
	{
		return getProperty( "keyStore", null );
	}

	/**
	 *  Sets the path to the keystore where the agent's certificate is stored.
	 *  @param keyStore The fully-qualified path to a Java keystore file in JKS
	 *      format.
	 */
	public void setKeyStore( String keyStore )
	{
		setProperty( "keyStore", keyStore );
	}

	/**
	 *  Gets the passphrase used to open the keystore
	 *  @return The passphrase used to open the keystore file (the default is
	 *      "changeit")
	 */
	public String getKeyStorePassword()
	{
		return getProperty( "keyStorePassword", "changeit" );
	}

	/**
	 *  Sets the passphrase used to open the keystore
	 *  @param keyStorePass The passphrase used to open the keystore file
	 */
	public void setKeyStorePassword( String keyStorePass )
	{
		setProperty( "keyStorePassword", keyStorePass );
	}

	/**
	 * The path to the Trust Store
	 * @return the path to the Trust Store
	 */
	public String getTrustStore()
	{
		return getProperty( "trustStore", null );
	}

	/**
	 * Sets the path to the Trust Store
	 * @param trustStore the path to the trust store
	 */
	public void setTrustStore( String trustStore )
	{
		setProperty( "trustStore", trustStore );
	}

	/**
	 * Gets the password being used to open the Trust Store
	 * @return the password used to open the Trust Store
	 */
	public String getTrustStorePassword()
	{
		return getProperty( "trustStorePassword", "changeit" );
	}

	/**
	 * Sets the password being used to open the Trust Store
	 * @param pwd the password to use when opening the Trust Store
	 */
	public void setTrustStorePassword( String pwd )
	{
		setProperty( "trustStorePassword", pwd );
	}

	/**
	 *  Gets the key password this transport protocol will use when setting
	 *  up HTTPS. When a null value is returned, the caller should use the same
	 *  password as the keystore password.
	 * @return the key password
	 */
	public String getPassword()
	{
		return getProperty( "password", null );
	}

	/**
	 *  Sets the key password this transport protocol will use when setting
	 *  up HTTPS. When a null value is set, the ADK will use the same
	 *  password as the keystore password.
	 * @param pass the key password
	 */
	public void setPassword( String pass )
	{
		setProperty( "password", pass );
	}

	/**
	 *  Determines if Client Authentication is required when the ZIS establishes
	 *  a connection with this agent in Push mode. When Client Authentication is
	 *  enabled, the certificate presented by the ZIS must include a CN=
	 *  attribute that evaluates to the IP address of the ZIS.<p>
	 *
	 *  Client Authentication is only used when the agent is operating in a server
	 *  role (i.e. push mode). It does not apply to outbound HTTPS connections or
	 *  to plain HTTP connections.<p>
	 *
	 *  @return The Client Authentication value returned by the HttpProperties
	 *      object passed to the constructor
	 */
	public boolean getRequireClientAuth()
	{
		return getProperty( "requireClientAuth", false );
	}

	/**
	 *  Determines if Client Authentication is required when the ZIS establishes
	 *  a connection with this agent in Push mode. When Client Authentication is
	 *  required, the certificate presented by the ZIS must include a CN= attribute
	 *  that evaluates to the IP address of the ZIS. The agent will refuse to
	 *  accept the connection if this is not the case. When disabled, the ZIS
	 *  may still present a certificate but it is not required.<p>
	 *
	 *  Client Authentication is only used when the agent is operating in a
	 *  server role (that is, in Push mode). It does not apply to outbound HTTPS
	 *  connections or to plain HTTP connections.<p>
	 *
	 *  @param auth true to require Client Authentication, false to disable it.
	 */
	public void setRequireClientAuth( boolean auth )
	{
		setProperty( "requireClientAuth", auth );
	}

	/**
	 *  Sets the Hostname Verifier used by the class framework when establishing
	 *  an HTTPS connection to the ZIS.<p>
	 *
	 *  @param verifier To disable hostname verification (the default), pass a
	 *      value of <code>null</code> to this method. To enable the default
	 *      hostname verification employed by Java's Secure Socket Extensions,
	 *      pass a value of "JSSE". To provide your own hostname verifier
	 *      implementation, pass the fully-qualified name of a class that
	 *      implements the <code>com.sun.net.ssl.HostnameVerifier</code> interface.
	 */
	public void setHostnameVerifier( String verifier )
	{
		if( verifier == null )
			remove( "hostnameVerifier" );
		else
			setProperty( "hostnameVerifier", verifier );
	}

	/**
	 *  Gets the Hostname Verifier used by the class framework when establishing
	 *  a connection to the ZIS.<p>
	 *
	 *  @return The value of the <i>hostnameVerifier</i> property. If hostname
	 *      verification should be disabled (the default), a value of <code>null</code>
	 *      is returned. If the default hostname verification offered by the Java
	 *      Secure Socket Extensions (JSSE) should be used, a value of "JSSE"
	 *      is returned. If the agent uses its own implementation of the
	 *      <code>com.sun.net.ssl.HostnameVerifier</code> interface, the
	 *      fully-qualified classname of that implementation is returned.
	 */
	public String getHostnameVerifier()
	{
		return getProperty( "hostnameVerifier", null );
	}
}
