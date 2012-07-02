//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import openadk.library.impl.HttpTransportPlugin;
import openadk.library.impl.HttpsTransportPlugin;
import openadk.library.impl.ISIFPrimitives;
import openadk.library.impl.TransportPlugin;
import openadk.library.log.DefaultServerLogModule;
import openadk.library.log.ServerLog;
import openadk.library.tools.xpath.SIFXPathContext;
import openadk.util.GUIDGenerator;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


/**
 * A global class representing the SIFWorks&reg; ADK class framework.
 * <p>
 * 
 * Prior to calling any ADK methods or referencing any static constants, agents
 * must first initialize the ADK by calling the static
 * <code>ADK.initialize</code> method. The default <code>initialize</code>
 * method loads all SIF Data Object (SDO) modules and selects the latest version
 * of SIF as the default version used to render SIF Messages. If you use this
 * method, ensure the sdoall.jar file is on the Java classpath. Other forms of
 * the <code>initialize</code> method allow you to choose the specific SDO
 * modules that are loaded as well as the version of SIF that will be the
 * default for this agent session.
 * <p>
 * 
 * @author Eric Petersen
 * @version 1.0
 */
public class ADK {

	/**
	 * The default pattern used for writing to the logging framework
	 */
	public static String DEFAULT_LOG4J_PATTERN = "%d %-5p [%c] %m%n";

	/**
	 * The Identifier that is used to identify the ADK itself for logging
	 * operations ("ADK")
	 */
	public static final String LOG_IDENTIFIER = "ADK";

	/**
	 * ADK debugging flag indicates whether Transport setup is traced
	 */
	public static final int DBG_TRANSPORT = 0x00000004;

	/**
	 * ADK debugging flag indicates whether Message Dispatching actions are
	 * traced
	 */
	public static final int DBG_MESSAGING = 0x00000008;

	/**
	 * ADK debugging flag indicates whether SIF_Event Message Dispatching
	 * actions are traced
	 */
	public static final int DBG_MESSAGING_EVENT_DISPATCHING = 0x00000010;

	/**
	 * ADK debugging flag indicates whether SIF_Response processing is traced
	 */
	public static final int DBG_MESSAGING_RESPONSE_PROCESSING = 0x00000020;

	/**
	 * ADK debugging flag indicates whether SIF_SystemControl/SIF_GetMessage
	 * messages are traced
	 */
	public static final int DBG_MESSAGING_PULL = 0x00000040;

	/**
	 * ADK debugging flag indicates whether message headers are traced
	 */
	public static final int DBG_MESSAGING_DETAILED = 0x00000080;

	/**
	 * ADK debugging flag indicates whether message content is traced
	 */
	public static final int DBG_MESSAGE_CONTENT = 0x00000100;

	/**
	 * ADK debugging flag indicates whether provisioning activity is traced.
	 * Note other debugging flags may cause provisioning messages to be logged
	 * even when this flag is not set.
	 */
	public static final int DBG_PROVISIONING = 0x00000200;

	/**
	 * ADK debugging flag indicates whether ADK policy is traced.
	 */
	public static final int DBG_POLICY = 0x00000400;

	/**
	 * ADK debugging flag indicates whether Agent Runtime activity is traced.
	 */
	public static final int DBG_RUNTIME = 0x00001000;

	/**
	 * ADK debugging flag indicates whether agent startup/shutdown and
	 * initializaion activity is logged
	 */
	public static final int DBG_LIFECYCLE = 0x10000000;

	/**
	 * ADK debugging flag indicates whether exceptions are logged
	 */
	public static final int DBG_EXCEPTIONS = 0x20000000;

	/**
	 * ADK debugging flag indicates whether agent and zone properties are logged
	 */
	public static final int DBG_PROPERTIES = 0x40000000;

	/**
	 * ADK debugging flag to enable all debugging output
	 */
	public static final int DBG_ALL = 0xFFFFFFFF;

	/**
	 * ADK debugging flag to disable debugging output
	 */
	public static final int DBG_NONE = 0x0;

	/**
	 * Minimal debugging flags (exceptions, provisioning)
	 */
	public static int DBG_MINIMAL = DBG_EXCEPTIONS | DBG_PROVISIONING;

	/**
	 * Moderate debugging flags (exceptions, provisioning, messaging, lifecycle)
	 */
	public static int DBG_MODERATE = DBG_MINIMAL | DBG_MESSAGING | DBG_POLICY | DBG_LIFECYCLE;

	/**
	 * Moderate debugging flags, with DBG_MESSAGING_PULL
	 */
	public static int DBG_MODERATE_WITH_PULL = DBG_MODERATE | DBG_MESSAGING_PULL;

	/**
	 * Detailed debugging flags (exceptions, provisioning, messaging, detailed
	 * messaging, transport)
	 */
	public static int DBG_DETAILED = DBG_MODERATE_WITH_PULL | DBG_TRANSPORT | DBG_MESSAGING_DETAILED;

	/**
	 * Very detailed debugging flags (exceptions, provisioning, messaging,
	 * detailed messaging, transport, event dispatching, properties)
	 */
	public static int DBG_VERY_DETAILED = DBG_DETAILED | DBG_MESSAGING_EVENT_DISPATCHING | DBG_MESSAGING_RESPONSE_PROCESSING | DBG_PROPERTIES;

	/**
	 * The ADK debugging level determines which types of messages will be
	 * submitted to the Log4j environment by the class framework. To eliminate
	 * all ADK-generated log messages set this value to 0. The default is
	 * DBG_VERY_DETAILED, which includes all debug flags except
	 * DBG_MESSAGE_CONTENT.
	 */
	public static int debug = DBG_VERY_DETAILED;

	// This array must be in order from earliest to latest version
	private static final SIFVersion[] sSupportedVersionsArray = { SIFVersion.SIF11, SIFVersion.SIF15r1, SIFVersion.SIF20, SIFVersion.SIF20r1, SIFVersion.SIF21, SIFVersion.SIF22, SIFVersion.SIF23, SIFVersion.SIF24, SIFVersion.SIF25 };

	/**
	 * The root log Category. Subcategories exist for the Agent and each zone,
	 * where the hierarchy is "ADK.Agent$<i>zoneId</i>". The ADK uses the root
	 * Category when writing log events prior to the initialization of the Agent
	 * class.
	 */
	protected static Logger log = Logger.getLogger(LOG_IDENTIFIER);

	/**
	 * The root ServerLog instance. Subcategories exist for the Agent and each
	 * zone, where the hierarchy is "ADK.Agent$<i>zoneId</i>". The ADK uses the
	 * root ServerLog instance only to establish the global chain of loggers; no
	 * actual logging is performed outside the context of a zone.
	 */
	protected static ServerLog serverLog = ServerLog.getInstance("ADK", null);

	/** Static single instance of the ADK object for this virtual machine */
	private static ADK sSingleton;

	/** Global primitives object */
	private ISIFPrimitives fImpl;

	/** The version of SIF used by the agent */
	private SIFVersion fVersion;

	/**
	 * The default SIFFormatter used by the agent if no formatter is specified.
	 * By default, the formatter is selected based on the SIFVersion that the
	 * ADK is currently running as default.
	 */
	private SIFFormatter fDefaultFormatter;

	/** The SIF version-dependent DTD object used by the SIFDataObject classes */
	private static SIFDTD DTD = new SIFDTD();

	/** The ADK product version */
	private String fLibVersion;

	/** The ADK build revision */
	private String fLibRevision;

	/** Installed Transport Protocol Plug-ins */
	private HashMap<String, TransportPlugin> fTransports = new HashMap<String, TransportPlugin>();

	// Protected constructor
	private ADK() {
		sSingleton = this;
	}

	/**
	 * Initialize the ADK to use the latest version of SIF and all SIF Data
	 * Object (SDO) libraries.
	 * <p>
	 * 
	 * Calling this method when the ADK has already been initialized has no
	 * effect.
	 * 
	 * @throws ADKException
	 *             If the ADK cannot be initialized
	 */
	public static void initialize() throws ADKException {
		initialize(SIFVersion.LATEST, SIFDTD.SDO_ALL);
	}

	/**
	 * Initialize the ADK to use the specified version of SIF.
	 * <p>
	 * 
	 * Calling this method when the ADK has already been initialized has no
	 * effect.
	 * <p>
	 * 
	 * This method must be called at agent startup to initialize various
	 * resources of the ADK, establish global settings for the class framework,
	 * and set the default version of SIF to which all messages originating from
	 * the agent will conform.
	 * <p>
	 * 
	 * Beginning with ADK 1.5.0, this method also configures the global ADK
	 * <code>ServerLog</code> instance with a logging module that will be
	 * inherited by the ServerLog of all zones. It installs a single logging
	 * module implementation: <code>DefaultServerLogModule</code>. The behavior
	 * of this module is to report SIF_LogEntry objects to the zone integration
	 * server via an Add SIF_Event message whenever
	 * <code>ServerLog.reportLogEntry</code> is called on a zone and the agent
	 * is running in SIF 1.5 or later. DefaultServerLogModule also echos server
	 * log messages to the zone's local Log4j Category so that agents do not
	 * need to duplicate logging to both the server and local agent log. If you
	 * want to install a custom <i>ServerLogModule</i> implementation -- or need
	 * to adjust the settings of the default module installed when the ADK is
	 * initialized -- call the <code>ADK.getServerLog</code> method to obtain a
	 * reference to the root of the <code>ServerLog</code> chain, then call its
	 * methods to add and remove modules. Refer to the ServerLog class for more
	 * information on server logging.
	 * <p>
	 * 
	 * @param version
	 *            The version of SIF that will be used by the agent this
	 *            session. Supported versions are enumerated by constants of the
	 *            <code>com.edustructures.SIFVersion</code> class. Once
	 *            initialized, the version cannot be changed.
	 * @param sdoLibraries
	 *            One or more of the constants defined by the SDOLibrary class,
	 *            identifying the SIF Data Object libraries to be loaded into
	 *            memory (e.g. SDOLibrary.STUDENT | SDOLibrary.HR )
	 * @throws ADKException
	 *             If the ADK cannot be initialized
	 * 
	 * @exception ADKNotSupportedException
	 *                is thrown if the specified SIF version is not supported by
	 *                the ADK, or if the <i>sdoLibraries</i> parameter is
	 *                invalid
	 */
	public static void initialize(SIFVersion version, int sdoLibraries) throws ADKException {
		if (sSingleton != null)
			return;

		if (version == null)
			throw new IllegalArgumentException("SIFVersion cannot be null");

		sSingleton = new ADK();

		try {
			initLogging();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// The default formatter for String APIs in the ADK is the SIF 1.x
		// formatter
		// for backwards compatibility
		sSingleton.fDefaultFormatter = SIFDTD.SIF_1X_FORMATTER;

		// Initialize the SIFXPathContext Class
		// TODO: Would like a more elegant way to initialize the JXPathContext
		// Subsystem, but this seems to be the only way that works all the time
		// What needs to be set is the default JXpathContextFactory, which the
		// ADK
		// overrides to create instances of SIFXPathContextFactory.
		SIFXPathContext.initialize();

		//
		// Set up the ServerLog
		//
		ServerLog sl = getServerLog();
		if (sl != null) {
			sl.addLogger(new DefaultServerLogModule());
		}

		//
		// For some reason we sometimes get this error message in the SASIxp
		// agent:
		// "MessageDispatcher could not convert SIF_Ack response to an object: java.lang.ClassNotFoundException: org/apache/xerces/parsers/SAXParser"
		// Not sure why, but try forcing the SIFParser static blocks here to
		// clear
		// up this problem.
		//
		SIFParser.newInstance();

		// HTTP and HTTPS transports always available
		TransportPlugin tp = new HttpTransportPlugin();
		sSingleton.fTransports.put(tp.getProtocol(), tp);
		tp = new HttpsTransportPlugin();
		sSingleton.fTransports.put(tp.getProtocol(), tp);

		ResourceBundle rb = ResourceBundle.getBundle("openadk.library.Library");
		sSingleton.fLibVersion = rb.getString("Version");
		sSingleton.fLibRevision = rb.getString("Revision");

		if (debug != DBG_NONE)
			log.debug("Using ADK " + ADK.getADKVersion());

		setVersion(version);

		DTD.loadLibraries(sdoLibraries);

	}

	private static boolean _initLogging = false;

	private static void initLogging() throws ADKException {
		if (!_initLogging) {
			_initLogging = true;
			String logFile = System.getProperty("adk.log.file");
			if (logFile != null) {
				try {
					setLogFile(logFile);
				} catch (IOException ioe) {
					throw new ADKException("Could not initialize log file (\"+logFile+\"): " + ioe, null);
				}
			} else {
				// No logging to file is configured. Add a console appender, but
				// only if there are no
				// appenders currently configured in the logging framework and
				// no appenders configured in
				// The ADK's logger. The appender is added to the ADK's logger.
				// If there are other classes
				// in the application logging to log4net outside of the ADK's
				// log hierarchy, the appenders
				// for those logs will need to be added by the application

				// NOTE: If someone wishes to use their own custom appenders
				// completely for the ADK, they can
				// initialize log4net before calling Adk.Initialize(). This
				// method must make sure that it
				// does no initialization of log4net if it has already been
				// done.
				Enumeration rootAppenders = LogManager.getRootLogger().getAllAppenders();
				if (!rootAppenders.hasMoreElements()) {
					if (!log.getAllAppenders().hasMoreElements()) {
						log.removeAllAppenders();
						ConsoleAppender consoleApp = new ConsoleAppender();
						consoleApp.setLayout(new PatternLayout(DEFAULT_LOG4J_PATTERN));
						consoleApp.activateOptions();
						log.addAppender(consoleApp);
						log.getLoggerRepository().setThreshold(Level.DEBUG);
					}
				}
			}
		}
	}

	/**
	 * Returns the root Log4j Category for the ADK.
	 * <p>
	 * 
	 * Agents that wish to customize ADK logging may call this method to obtain
	 * the root Log4j Category.
	 * 
	 * @return The Logger used by the ADK
	 */
	public static Logger getLog() {
		return log;
	}

	/**
	 * Gets the root ServerLog instance for the ADK.
	 * <p>
	 * 
	 * Agents that wish to customize ADK server-side logging may call this
	 * method to obtain the class framework's root ServerLog instance. Call any
	 * of the following methods to set up the chain of loggers that will be
	 * inherited by the Agent and all Zones:
	 * <p>
	 * 
	 * <ul>
	 * <li><code>addLogger</code></li>
	 * <li><code>removeLogger</code></li>
	 * <li><code>clearLoggers</code></li>
	 * <li><code>getLoggers</code></li>
	 * </ul>
	 * 
	 * Unlike client-side logging, server logging requires a connection to a
	 * Zone Integration Server. Because the current SIF 1.x infrastructure does
	 * not allow connections to servers independent of a zone, the logging
	 * methods of ServerLog are useful only when called within the context of a
	 * zone. Therefore, calling any of the logging methods on the ServerLog
	 * instance returned by this method will result in an IllegalStateException.
	 * This method is provided only to set up the ServerLog logger chain at the
	 * global ADK level.
	 * <p>
	 * 
	 * @return The ADK's root ServerLog instance
	 * 
	 * @since ADK 1.5
	 */
	public static ServerLog getServerLog() {
		return serverLog;
	}

	/**
	 * Redirects all log output to the specified file.
	 * <p>
	 * 
	 * @param file
	 *            The log file
	 * @throws IOException
	 *             If the File cannot be created or written to
	 */
	public static void setLogFile(String file) throws IOException {
		log.removeAllAppenders();
		log.addAppender(new org.apache.log4j.FileAppender(new PatternLayout(DEFAULT_LOG4J_PATTERN), file));
	}

	/**
	 * Has the ADK been initialized?
	 * 
	 * @return True if {@link #initialize()} has already been called
	 */
	public static boolean isInitialized() {
		return sSingleton != null;
	}

	/**
	 * Sets the version of SIF the ADK will use. The version setting is global
	 * and therefore applies to all SIF messaging activity for all Agents
	 * instantiated in the current Java virtual machine.
	 * <p>
	 * 
	 * Calling this method after agent initialization is not recommended.
	 * <p>
	 * 
	 * @param version
	 *            The version of SIF that will be used by the agent. Supported
	 *            versions are enumerated by constants of the <code>
	 *      com.edustructures.SIFVersion</code> class
	 * 
	 * @throws ADKNotSupportedException
	 *             is thrown if the SIF version is not supported
	 */
	public static void setVersion(SIFVersion version) throws ADKNotSupportedException {
		if (!isSIFVersionSupported(version))
			throw new ADKNotSupportedException("SIF " + sSingleton.fVersion.toString() + " is not supported by the ADK", null);

		sSingleton.fVersion = (version == null ? SIFVersion.LATEST : version);

		if (debug != DBG_NONE) {
			log.debug("Using SIF " + sSingleton.fVersion.toString());
		}
	}

	/**
	 * Gets the versions of SIF supported by the ADK
	 * 
	 * @return An array of SIFVersion objects
	 */
	public static SIFVersion[] getSupportedSIFVersions() {
		return sSupportedVersionsArray;
	}

	/**
	 * Returns the highest SIF_Version supported by the current instance of the
	 * ADK from the list of candidate versions. This method is helpful to agents
	 * during SIF_Request processing. A SIF_Request can contain multiple
	 * SIF_Versions in it.
	 * 
	 * @param candidates
	 *            If null, or zero-length, the ADK.getSIFVersion() value will be
	 *            returned
	 * @return The latest version supported by the ADK from the list of
	 *         candidate versions
	 */
	public static SIFVersion getLatestSupportedVersion(SIFVersion[] candidates) {
		checkInit();
		if (candidates == null || candidates.length == 0) {
			return ADK.getSIFVersion();
		}

		SIFVersion returnVal = null;
		for (SIFVersion candidate : candidates) {
			if (returnVal == null || candidate.compareTo(returnVal) > 0) {
				returnVal = candidate;
			}
		}
		if (returnVal == null) {
			returnVal = ADK.getSIFVersion();
		}
		return returnVal;
	}

	/**
	 * Gets the transport protocols available to agents.
	 * 
	 * @return An array of transport protocol strings (e.g. "http")
	 */
	public static String[] getTransportProtocols() {
		checkInit();

		int i = 0;
		String[] arr = new String[sSingleton.fTransports.size()];
		for (Iterator it = sSingleton.fTransports.values().iterator(); it.hasNext();) {
			TransportPlugin pi = (TransportPlugin) it.next();
			if (!pi.isInternal())
				arr[i++] = pi.getProtocol();
		}

		return arr;
	}

	/**
	 * Gets an installed transport protocol
	 * 
	 * @param protocol
	 *            The transport protocol name (e.g. "http", "https", etc.)
	 * @return The plugin class that represents this protocol, for internal use
	 *         by the class framework
	 */
	public static TransportPlugin getTransportProtocol(String protocol) {
		if (protocol == null) {
			throw new IllegalArgumentException("Protocol cannot be null");
		}
		checkInit();
		return sSingleton.fTransports.get(protocol.toLowerCase());
	}

	/**
	 * Installs a transport protocol
	 * 
	 * @param tp
	 *            The Transport plugin to install
	 */
	public static void install(TransportPlugin tp) {
		if (tp == null) {
			throw new IllegalArgumentException("TransportPlugin cannot be null");
		}
		checkInit();
		sSingleton.fTransports.put(tp.getProtocol().toLowerCase(), tp);
	}

	/**
	 * Utility method to generate a GUID for SIF Data Objects and messages.
	 * 
	 * @return A GUID
	 * @see openadk.util.GUIDGenerator
	 */
	public static String makeGUID() {
		return GUIDGenerator.makeGUID();
	}

	/**
	 * Gets the ADK build version
	 * 
	 * @return The ADK build version string (e.g. "1.0.4")
	 */
	public static String getADKVersion() {
		if (sSingleton == null)
			return "Unknown";
		return sSingleton.fLibVersion;
	}

	/**
	 * Gets the version of SIF used by the agent
	 * <p>
	 * 
	 * @return The SIFVersion that the ADK is initialized to use
	 */
	public static SIFVersion getSIFVersion() {
		checkInit();
		return sSingleton.fVersion;
	}

	/**
	 * The SIFFormatter used by default for backwards-compatible non-typed APIs
	 * in the ADK, such as {@link Element#getTextValue()}. The default formatter
	 * used by the ADK is the SIF 1.x formatter for backwards compatibility. If
	 * you are* using the strongly-typed APIs, such as
	 * {@link Element#getSIFValue()}, this setting has no effect.
	 * 
	 * @return the SIFFormatter used by default if no SIFVersion is provided
	 */
	public static SIFFormatter getTextFormatter() {
		checkInit();
		return sSingleton.fDefaultFormatter;
	}

	/**
	 * Sets the SIFFormatter used by default for rendering Text values of SIF
	 * Elements when mapping SIF Data directly to a string value.
	 * <p>
	 * 
	 * The default SIFFormatter used by the ADK is the formatter for SIF 1.5.
	 * This means that agents that were based on the 1.x version of the ADK will
	 * continue to get the SIF 1.x string version of data fields.
	 * <p>
	 * 
	 * APIs that are affected by this setting include:<br>
	 * {@link Element#getTextValue()} (@link
	 * com.edustructures.sifworks.tools.mapping.StringMapAdaptor}
	 * 
	 * 
	 * @param formatter
	 *            The default formatter to be used that translates native data
	 *            types supported by SIF to their textual representation
	 */
	public static void setTextFormatter(SIFFormatter formatter) {
		sSingleton.fDefaultFormatter = formatter;
	}

	/**
	 * Determines if the specified version of SIF is supported by the ADK
	 * 
	 * @param version
	 *            The version of SIF
	 * @return True if the specified version is supported by the ADK
	 */
	public static boolean isSIFVersionSupported(SIFVersion version) {
		return Arrays.binarySearch(sSupportedVersionsArray, version) > -1;
	}

	/**
	 * Gets the default SIFDTD object for the version of SIF selected when the
	 * ADK was initialized.
	 * <p>
	 * 
	 * @return The SIFDTD object for the version of SIF selected when the ADK
	 *         was initialized.
	 */
	public static SIFDTD DTD() {
		return ADK.DTD;
	}

	/**
	 * Gets a reference to the global ISIFPrimitives object used for SIF
	 * messaging
	 * <p>
	 * 
	 * @return The ISIFPrimitives implementation used by the ADK to send SIF
	 *         Messages
	 */
	public static ISIFPrimitives getPrimitives() {
		checkInit();
		if (sSingleton.fImpl == null)
			sSingleton.fImpl = new openadk.library.impl.SIFPrimitives();
		return sSingleton.fImpl;
	}

	/**
	 * Utility to check that the ADK has been initialized.
	 * 
	 * @throws InternalError
	 *             if the <code>initialize</code> function has not been
	 *             successfully called
	 */
	private static void checkInit() {
		if (sSingleton == null)
			throw new InternalError("The ADK is not initialized. Please call ADK.initialize()");
	}

	/**
	 * Displays the ADK Version
	 * 
	 * @param args
	 *            The command line arguments are ignored
	 */
	public static void main(String[] args) {
		try {
			initialize();
			System.out.println(ADK.getADKVersion());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
