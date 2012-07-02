//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.examples.tinysis;


import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;

import openadk.library.ADK;
import openadk.library.ADKException;
import openadk.library.ADKFlags;
import openadk.library.Agent;
import openadk.library.AgentProperties;
import openadk.library.PublishingOptions;
import openadk.library.SIFVersion;
import openadk.library.Zone;
import openadk.library.school.SchoolDTD;
import openadk.library.student.StudentDTD;
import openadk.library.tools.cfg.AgentConfig;
import openadk.library.tools.mapping.Mappings;

import org.apache.log4j.PropertyConfigurator;

/**
 *  The TinySIS agent demonstrates how to respond to requests for a few common
 *  SIS objects by reading a list of students from a Microsoft Access database 
 *  using JDBC, then using the ADK's Mappings class to convert the field data 
 *  into StudentPersonal objects. The mappings can be changed in the agent.cfg
 *  configuration file, which is read by the program at startup using the ADK's
 *  AgentConfig class.
 *  <p>
 *
 *  To register the agent as the provider of SIS objects in one or
 *  more zones, open the agent.cfg file and provide zone connection parameters.
 *  Then run the agent to register in each zone as the provider of StudentPersonal.
 *  Unlike the other ADK Example agents, this one reads all zone information and
 *  connection settings from the agent.cfg file to demonstrate the use of the
 *  AgentConfig class.
 *  <p>
 *
 */
public class TinySIS extends Agent 
{
	/** The configuration file */
	AgentConfig fCfg;

	/** The SQL connection */
	Connection fConn;

	/**
	 *  Constructor
	 */
    public TinySIS()
	{
		super( "TinySIS" );
		setName( "TinySIS Agent" );
    }

	/**
	 *  Initialize and start the agent
	 *  @param args Command-line arguments (run with no arguments to display help)
	 */
	public void startAgent( String[] args )
		throws Exception
	{
		System.out.println("Initializing agent...");

		configureLogging();
		
		//  Initialize the ADK. 
		ADK.initialize(  );
		
		
		//  Read the configuration file
		fCfg = new AgentConfig();
		System.out.println("Reading configuration file...");
		fCfg.read( "agent.cfg", false );
		
		ADK.debug = ADK.DBG_ALL;

		//  Override the SourceId passed to the constructor with the SourceId
		//  specified in the configuration file
		setId( fCfg.getSourceId() );

		//  Inform the ADK of the version of SIF specified in the sifVersion=
		//  attribute of the <agent> element
		SIFVersion version = fCfg.getVersion();
		ADK.setVersion( version );

		//  Now call the superclass initialize once the configuration file has been read
		super.initialize();

		//
		//  Ask the AgentConfig instance to "apply" all configuration settings
		//  to this Agent; for example, all <property> elements that are children
		//  of the root <agent> node are parsed and applied to this Agent's
		//  AgentProperties object; all <zone> elements are parsed and registered
		//  with the Agent's ZoneFactory, and so on.
		//
		fCfg.apply( this, true );

		//  Establish the ODBC connection to the Students.mdb database file.
		//  The JDBC driver and URL are specified in the agent.cfg configuration
		//  file and were automatically added to the AgentProperties when the
		//  apply method was called above.
		//
		System.out.println("Opening database...");

		AgentProperties props = getProperties();
		String driver = props.getProperty( "jdbc.driver" );
		String url = props.getProperty( "jdbc.url" );
		System.out.println("- Using driver: " + driver );
		System.out.println("- Connecting to URL: " + url );

		//  Load the JDBC driver
		Driver d = (Driver)Class.forName( driver ).newInstance();
		System.out.println( d.toString() );
		
		File databaseDirectory = new File( "TinySISDB" );
		boolean shouldCreateDatabase = !databaseDirectory.exists();
		
		//  Get a Connection
		fConn = DriverManager.getConnection(url);
		fConn.setAutoCommit( true );
		
		if ( shouldCreateDatabase ) {
			Util.createDatabase( fConn );
		}
		
		Mappings rootMappings = fCfg.getMappings().getMappings( "Default" );
		
		DataObjectProvider schoolProvider = new DataObjectProvider("SIF_SchoolInfo", rootMappings );
		DataObjectProvider studentProvider = new DataObjectProvider("SIF_StudentPersonal", rootMappings );
		DataObjectProvider enrollmentProvider = new StudentEnrollmentProvider("SIF_StudentSchoolEnrollment", rootMappings );
		
		//  Connect to each zone specified in the configuration file, registering
		//  this agent as the Provider of the SIS objects. 
		
		Zone[] allZones = getZoneFactory().getAllZones();
		for( int i = 0; i < allZones.length; i++ )
		{
			try
			{
				//  Connect to this zone
				System.out.println("- Connecting to zone \"" + allZones[i].getZoneId() + "\" at " + allZones[i].getZoneUrl() );
				allZones[i].setPublisher( schoolProvider, SchoolDTD.SCHOOLINFO, new PublishingOptions( true ) );
				allZones[i].setPublisher( studentProvider, StudentDTD.STUDENTPERSONAL, new PublishingOptions( true ) );
				allZones[i].setPublisher( enrollmentProvider, StudentDTD.STUDENTSCHOOLENROLLMENT, new PublishingOptions( true ) );
				allZones[i].connect( ADKFlags.PROV_REGISTER );
			}
			catch( ADKException ex )
			{
				System.out.println( "  " + ex.getMessage() );
			}
		}
	}


	private void configureLogging() {
	
		PropertyConfigurator.configure( "log4j.properties" );
		
	}

	/**
	 *  Runs the agent from the command-line.
	 */
	public static void main( String[] args )
	{
		TinySIS agent = null;

		try
		{
			ADK.debug = ADK.DBG_MODERATE;
			
		    //  Start agent...
			agent = new TinySIS();

			//  Install a shutdown hook to cleanup when Ctrl+C is pressed
			final Agent _agent = agent;
			Runtime.getRuntime().addShutdownHook(
				new Thread()
				{
					@Override
					public void run()
					{
						try
						{
							//  Shutdown the agent and send a SIF_Unregister to
							//  the zone if the /unreg command line option was
							//  specified
						    _agent.shutdown( ADKFlags.PROV_NONE );
						}
						catch( ADKException adke )
						{
							System.out.println("Unable to shutdown: " + adke );
						}
					}
				}
			);

			agent.startAgent(args);

			//  Wait for Ctrl-C to be pressed
			System.out.println();
			System.out.println("Agent is running (Press Ctrl-C to stop)");
			System.out.println();
			Object semaphore = new Object();
			synchronized( semaphore ) {
				semaphore.wait();
			}
		}
		catch( Exception e )
		{
			System.out.println(e);
		}
	}
}
