//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.sifquery;

import java.io.*;
import java.util.*;

import openadk.library.*;

/**
 *  A convenience class to parse the command-line of all ADK Example agents
 *  and to read a list of zones from a zones.properties file, if present in
 *  the current working directory.<p>
 *
 *  @version ADK 1.0
 */
public class ADKExamples
{
	/**
	 *  False if the /noreg option was specified, indicating the agent should
	 *  not send a SIF_Register message when connecting to zones
	 */
	public static boolean Reg = true;

	/**
	 *  True if the /unreg option was specified, indicating the agent should
	 *  send a SIF_Unregister when shutting down and disconnecting from zones
	 */
	public static boolean Unreg = false;

	/**
	 *  The SIFVersion specified on the command-line
	 */
	public static SIFVersion Version = SIFVersion.LATEST;

	/**
	 *  Parsed command-line arguments
	 */
	public static String[] args = null;

	/**
	 *  Parse the command-line. This method may be called repeatedly, usually
	 *  once from the sample agent's <code>main</code> function prior to
	 *  initializing the ADK and again from the <code>Agent.initialize</code>
	 *  method after initializing the Agent superclass. When called without an
	 *  Agent instance, only those options that do not rely on an AgentProperties
	 *  object are processed (e.g. the /D option).
	 *  <p>
	 *
	 *  If a file named 'agent.rsp' exists in the current directory, any command
	 *  line options specified will be appended to the command-line arguments
	 *  passed to this method. Each line of the agent.rsp text file may be
	 *  comprised of one or more arguments separated by spaces, so that the
	 *  entirely set of arguments can be on one line or broken up onto many
	 *  lines.<p>
	 *
	 *  @param agent An Agent instance that will be updated when certain
	 *      command-line options are parsed
	 *  @param args The string of arguments provided by the <code>main</code>
	 *      function
	 */
    public static Map<String,String> parseCL( Agent agent, String[] arguments )
	{
		if( args == null )
		{
			args = arguments;

			if( args.length > 0 && args[0].charAt(0) != '/' )
			{
				//  Look for an agent.rsp response file
				File rsp = new File( args[0] );
				if( rsp.exists() )
				{
					try
					{
						Vector<String> v = new Vector<String>();

						BufferedReader in = new BufferedReader( new FileReader( rsp ) );
						String line = in.readLine();
						while( line != null ) {
							StringTokenizer tok = new StringTokenizer( line, " " );
							while( tok.hasMoreTokens() )
								v.addElement( tok.nextToken() );
							line = in.readLine();
						}

						//  Append any arguments found to the args array
						if( v.size() > 0 ) {
							args = new String[ args.length + v.size() ];
							System.arraycopy( arguments, 0, args, 0, arguments.length );
							System.arraycopy( v.toArray(), 0, args, arguments.length, v.size() );
							System.out.print("Reading command-line arguments from " + args[0] + ": " );
							for( int i = 0; i < args.length; i++ )
								System.out.print( args[i] + " " );
							System.out.println();
							System.out.println();
						}
					}
					catch( Exception ex )
					{
						System.out.println("Error reading command-line arguments from agent.rsp file: " + ex );
					}
				}
			}
		}

		if( agent == null )
		{
			//  Look for options that do not affect the AgentProperties...
			for( int i = 0; i < args.length; i++ )
	    	{
				if( args[i].equalsIgnoreCase("/debug") ) {
					if( i < args.length-1 ) {
						try {
							ADK.debug = ADK.DBG_NONE;
							int k = Integer.parseInt(args[++i]);
							if( k == 1 ) ADK.debug = ADK.DBG_MINIMAL; else
							if( k == 2 ) ADK.debug = ADK.DBG_MODERATE; else
							if( k == 3 ) ADK.debug = ADK.DBG_DETAILED; else
							if( k == 4 ) ADK.debug = ADK.DBG_VERY_DETAILED; else
							if( k == 5 ) ADK.debug = ADK.DBG_ALL;
						} catch( Exception e ) {
							ADK.debug = ADK.DBG_ALL;
						}
					}
					else
						ADK.debug = ADK.DBG_ALL;
				}
				else
				if( args[i].startsWith("/D") )
				{
					String prop = args[i].substring(2);
					if( i != args.length-1 )
						System.getProperties().setProperty(prop,args[++i]);
					else
						System.out.println("Usage: /Dproperty value");
				}
				else
				if( args[i].equalsIgnoreCase("/log") && i != args.length -1 )
				{
					try
					{
		    			ADK.setLogFile( args[++i] );
					}
					catch( IOException ioe )
					{
						System.out.println("Could not redirect debug output to log file: " + ioe );
					}
		    	}
				else
				if( args[i].equalsIgnoreCase("/ver") && i != args.length -1 )
				{
					Version = SIFVersion.parse( args[++i] );
				}
				else
				if( args[i].equals( "/?" ) )
				{
					System.out.println();
					System.out.println("These options are common to all ADK Example agents. For help on the usage");
					System.out.println("of this agent in particular, run the agent without any parameters. Note that");
					System.out.println("most agents support multiple zones if a zones.properties file is found in");
					System.out.println("the current directory.");
					System.out.println();
					printHelp();

					System.exit(0);
				}
		    }

			return null;
		}

		//  Parse all other options...
		AgentProperties props = agent.getProperties();
		Map<String, String> misc = new HashMap<String,String>();

		int port = -1;
		String host = null;
		boolean useHttps = false;
		String keystore = null;
		String ksPwd = null;
		String truststore = null;
		String tsPwd = null;
		boolean clientAuth = false;

		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("/sourceId") && i != args.length -1 )
    			agent.setId( args[++i] );
			else
			if( args[i].equalsIgnoreCase("/noreg") )
				Reg = false;
			else
			if( args[i].equalsIgnoreCase("/unreg") )
				Unreg = true;
			else
			if( args[i].equalsIgnoreCase("/pull") )
				props.setMessagingMode( AgentMessagingMode.PULL );
			else
			if( args[i].equalsIgnoreCase("/push") )
				props.setMessagingMode( AgentMessagingMode.PUSH );
			else
			if( args[i].equalsIgnoreCase("/port") && i != args.length-1 ) {
				try {
					port = Integer.parseInt(args[++i]);
				} catch( NumberFormatException nfe ) {
					System.out.println("Invalid port: "+args[i-1]);
				}
			}
			else
			if( args[i].equalsIgnoreCase("/https") ) {
				useHttps = true;
			}
			else
			if( args[i].equalsIgnoreCase("/keystore") ) {
				keystore = args[++i];
			}
			else
			if( args[i].equalsIgnoreCase("/ksPwd") ) {
				ksPwd = args[++i];
			}
			else
			if( args[i].equalsIgnoreCase("/truststore") ) {
				truststore = args[++i];
			}
			else
			if( args[i].equalsIgnoreCase("/tsPwd") ) {
				tsPwd = args[++i];
			}
			else
			if( args[i].equalsIgnoreCase("/auth") ) {
				clientAuth = true;
			}
			else
			if( args[i].equalsIgnoreCase("/host") && i != args.length -1 )
    			host = args[++i];
			else
			if( args[i].equalsIgnoreCase("/timeout") && i != args.length -1 ) {
				try {
					props.setDefaultTimeout( Integer.parseInt(args[++i]) );
				} catch( NumberFormatException nfe ) {
					System.out.println("Invalid timeout: "+args[i-1]);
				}
		    }
			else
			if( args[i].equalsIgnoreCase("/freq") && i != args.length -1 ) {
				try {
					props.setPullFrequency( Integer.parseInt(args[++i]) );
				} catch( NumberFormatException nfe ) {
					System.out.println("Invalid pull frequency: "+args[i-1]);
				}
		    }
			else
			if( args[i].equalsIgnoreCase("/opensif") )
			{
				//  OpenSIF reports attempts to re-subscribe to objects as an
				//  error instead of a success status code. The ADK would therefore
				//  throw an exception if it encountered the error, so we can
				//  disable that behavior here.

				props.setIgnoreProvisioningErrors( true );
			}
			else
			if( args[i].charAt(0) == '/' )
		    {
				if( i == args.length - 1 || args[i+1].startsWith("/") )
					misc.put( args[i].substring(1), null );
				else
					misc.put( args[i].substring(1), args[++i] );
			}
		}

		if( useHttps )
		{
			//  Set transport properties (HTTPS)
			HttpsProperties https = agent.getDefaultHttpsProperties();
			if( keystore != null )
				https.setKeyStore(keystore);
			if( ksPwd != null )
				https.setKeyStorePassword(ksPwd);
			if( truststore != null )
				https.setTrustStore(truststore);
			if( tsPwd != null )
				https.setTrustStorePassword(tsPwd);

			https.setRequireClientAuth(clientAuth);

			if( port != -1 )
				https.setPort(port);
			https.setHost(host);

			props.setTransportProtocol("https");
		}
		else
		{
			//  Set transport properties (HTTP)
			HttpProperties http = agent.getDefaultHttpProperties();
			if( port != -1 )
				http.setPort(port);
		    http.setHost(host);

			props.setTransportProtocol("http");
		}

		return misc;
    }

	/**
	 *  Display help to System.out
	 */
	public static void printHelp()
	{
		System.out.println("    /sourceId name    The name of the agent");
		System.out.println("    /ver version      Default SIF Version to use (e.g. 10r1, 10r2, etc.)");
		System.out.println("    /debug level      Enable debugging to the console");
		System.out.println("                         1 - Minimal");
		System.out.println("                         2 - Moderate");
		System.out.println("                         3 - Detailed");
		System.out.println("                         4 - Very Detailed");
		System.out.println("                         5 - All");
		System.out.println("    /log file         Redirects logging to the specified file");
		System.out.println("    /pull             Use Pull mode");
		System.out.println("    /freq             Sets the Pull frequency (defaults to 15 seconds)");
		System.out.println("    /push             Use Push mode");
		System.out.println("    /port n           The local port for Push mode (defaults to 12000)");
		System.out.println("    /host addr        The local IP address for push mode (defaults to any)");
		System.out.println("    /noreg            Do not send a SIF_Register on startup (sent by default)");
		System.out.println("    /unreg            Send a SIF_Unregister on exit (not sent by default)");
		System.out.println("    /timeout ms       Sets the ADK timeout period (defaults to 30000)");
		System.out.println("    /opensif          Ignores provisioning errors from OpenSIF");
		System.out.println("    /Dproperty val    Sets a Java System property");
		System.out.println();
		System.out.println("  HTTPS Transport Options:");
		System.out.println("    /https            Use HTTPS instead of HTTP");
		System.out.println("    /auth             Require Client Authentication");
		System.out.println("    /keystore file    The keystore file to use");
		System.out.println("    /kspwd password   The keystore password");
		System.out.println("    /truststore file  The truststore file to use");
		System.out.println("    /tspwd password   The truststore password");
		System.out.println();
		System.out.println("  Response Files:");
		System.out.println("    To use a response file instead of typing arguments on the command-line," );
		System.out.println("    pass the name of the response file as the first argument. This text" );
		System.out.println("    file may contain any combination of arguments on one or more lines," );
		System.out.println("    which are appended to any arguments specified on the command-line." );
		System.out.println();
	}

	/**
	 *  Looks for a file named zones.properties and if found reads its contents
	 *  into a HashMap where the key of each entry is the Zone ID and the value
	 *  is the Zone URL. A valid HashMap is always returned by this method; the
	 *  called typically assigns the value of the /zone and /url command-line
	 *  options to that map (when applicable).
	 */
	@SuppressWarnings("unchecked")
	public static HashMap readZonesList()
		throws IOException
	{
		HashMap list = new HashMap();

		File f = new File( "zones.properties" );
		if( f.exists() )
		{
			System.out.println("Reading zone list from zones.properties file...");
			Properties props = new Properties();
	    	props.load( new FileInputStream(f) );
			list.putAll(props);
		}

		return list;
	}
}
