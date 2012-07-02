//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import org.xml.sax.SAXException;


/**
 *  Generates SIFDataObject classes for the SIFWorks ADK.<p>
 *
 *  This program is for internal use only. It generates the ADK's Data Object
 *  classes by processing metadata definition files (*.xml) that specify how to
 *  build DataObject classes for all versions of SIF supported by the ADK. The
 *  ADKGen program generates a complete set of DataObject class source code and
 *  a SIFDTD class for each version of SIF.<p>
 *
 *  ADKGen handles multiple versions of SIF by producing a single version-
 *  <i>independent</i> SIFDTD interface class that defines all objects and
 *  fields for all versions of SIF. Version-specific SIFDTD classes are then
 *  created to implement this interface. These version-specific classes are
 *  named the same as the SIF version (e.g. SIF10r1.class, SIF10r2.class, etc.)
 *  ADK developers always interact with the version-independent SIFDTD interface;
 *  internally, the ADK instantiates the appropriate version-specific class
 *  at initialization time. In this way, versioning is mostly transparent to the
 *  developer.<p>
 *
 *  Each version-specific SIFDTD class (e.g. "com.edustructures.sifworks.impl.SIF10r2")
 *  extends the SIFDTD class of the prior version. Thus, SIF10r2.class would
 *  extend SIF10r1, SIF15r0.class would extend SIF10r2, and so on. These classes
 *  then add ElementDef definitions for objects and fields that were newly
 *  introduced in that version of SIF.<p>
 *
 *  SIFDataObject implementation classes handle SIF versioning by aggregation.
 *  Any new member functions that are introduced as a result of new or modified
 *  object and fields in the SIF schema are simply added to the existing class.
 *  Enumerated type classes work in the same way. For example, suppose in
 *  SIF 1.0r2 the <Name> element were given a new child element <NickName>, and
 *  the <PreferredName> element were changed to <PrefName>. The
 *  <code>com.edustructures.sifworks.common.Name class</code> would now include the
 *  new member functions setNickName and setPrefName. However, the
 *  setPreferredName method that previously existed for SIF 1.0r1 would remain
 *  intact since ADKGen builds classes by aggregating all versions of SIF into
 *  a single implementation class. Developers could call the setPreferredName()
 *  method if the ADK were initialized for SIF 1.0r1, and could call setPrefName()
 *  if the ADK were initialized for 1.0r2. However, calling setPrefName() for
 *  1.0r1 would throw a SIFVersionError runtime exception since the <PrefName>
 *  element did not exist in 1.0r1.<p>
 *
 *  Usage: <code>adkgen [dir | @files] o=output p=package</code><p>
 *
 *  Where:<p>
 *
 *  <ul>
 *      <li>
 *          <b>dir</b> is the directory or directories that contain the .xml
 *          definition files imported into ADKGEN. All files ending in *.xml will
 *          be processed. More than one directory can be specified by separating
 *          directories with a semi-colon (e.g. "..\\sif10r1;..\\sif10r2")
 *      </li>
 *      <li>
 *          <b>@files</b> is an optional text file that lists the definition
 *          files to read, one per line. If this file is provided then only the
 *          definition files listed in it will be processed and <i>dir</i>
 *          is ignored.
 *      </li>
 *      <li>
 *          <b>o=output</b> is the root directory where classes are to be generated
 *      </li>
 *      <li>
 *          <b>p=package</b> is the package prefix to use for Data Object
 *          classes that are generated (defaults to "com.edustructures.sifworks")
 *      </li>
 *  </ul>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public class Main implements FilenameFilter
{
	
	public static final long DEFAULT_SERIAL_VERSION_UID = 2;
	
	/** The list of definition files to process */
	protected Vector<DefinitionFile> fFileList;
	
	/** The language to generate output in ( Java or CSharp ) **/
	protected String fLanguage = "java";

	/** The output directory */
	protected String fOutput;

	/** The package prefix to use for all Data Object classes we generate */
	protected String fPackage = "openadk.library";

	/** Map of DB objects keyed by SIF version string */
	protected Map<String, DB> fDBs = new TreeMap<String, DB>();

	/** Static singleton */
	public static Main self = null;
	
	protected String fFilespec;
	protected static String fLocale;
	

	/**
	 *  Constructor
	 *  @param args The command-line arguments
	 */
    public Main( String[] args )
		throws ParseException,SAXException,IOException
	{
		self = this;

		fFilespec = System.getProperty("user.dir");
		fLanguage = "java";
		fLocale = "us";

	    for( int i = 0; i < args.length; i++ )
		{
			if( args[i].startsWith("@") )
				readFileList(args[i].substring(1));
			else
			if( args[i].startsWith("o=") )
				fOutput = args[i].substring(2);
			else
			if( args[i].startsWith("l=") )
				fLanguage = args[i].substring(2).toLowerCase();
			else
			if( args[i].startsWith("p=") )
				fPackage = args[i].substring(2);
			else
			if (args[i].startsWith("locale="))
				fLocale = args[i].substring(7).toLowerCase();
			else
				fFilespec = args[i];
		}
	    
	    if( fOutput == null ) {
			System.out.println("No o=output parameter specified");
			printHelp();
			System.exit(-1);
		}
		
		if( !( fLanguage.equals("java") || 
				fLanguage.equals("cs") ||
				fLanguage.equals("zis") ||
				fLanguage.equals("mappings") ||
				fLanguage.equals("stats") ||
				fLanguage.equals("sifdbms") ||
				fLanguage.equals( "jh" ) ) ){
			System.out.println("Expected l=java or l=cs or l=jh");
			printHelp();
			System.exit(-1);
		}
		
		if( !( fLocale.equals("au") || 
				fLocale.equals("uk") ||
				fLocale.equals("us") ||
				fLocale.equals("core")) ){
			System.out.println("Expected locale=au or locale=uk or locale=us or locale=core");
			printHelp();
			System.exit(-1);
		}
		
		if( fFileList == null )
		{
			fFileList = new Vector<DefinitionFile>();

			//  The input filespec may contain multiple entries separated by
			//  a semi-colon (e.g. "../datadef/sif10r1;../datadef/sif10r2")
			StringTokenizer tok = new StringTokenizer( fFilespec, ";" );
	    	while( tok.hasMoreTokens() ) {
		    	String fs = tok.nextToken();
	    		getFileList(fs);
			}
		}

		//  All files created by this tool are placed in one directory. (When
		//  multiple versions of SIF are processed, the results are combined
		//  into one set of source code.)
		File f = new File(fOutput);
		System.out.println("Output directory: "+f.getAbsolutePath());
		System.out.println("Package: "+fPackage);

		//  Parse all of the .xml files to build the DB[] databases
		int cnt = fFileList.size();
		System.out.println("Processing "+cnt+" files...");
		for( int i = 0; i < cnt; i++ ) {
		    (fFileList.elementAt(i)).parse();
		}
	}
    
	public static String getLocale() {
		return fLocale;
	}

	public void go()
	{
		if( fDBs.size() == 0 )
		{
			System.out.println( "No input files found (or none specified on command-line)" );
			System.exit(0);
		}
		
		//  Create a single list of DB[] objects to work with
		int i = 0;
		DB[] dbs = new DB[fDBs.size()];
		fDBs.values().toArray( dbs );
		
		try {
			//  Generate code from each DB
			Generator g;
			if( fLanguage.equalsIgnoreCase( "cs" ) ) {
				g = new CSGenerator(fFilespec,fOutput, fLocale);
			} else if( fLanguage.equalsIgnoreCase( "zis" ) ) {
				g = new ZISDefinitionGenerator( fFilespec, fOutput );
			} else if( fLanguage.equalsIgnoreCase( "mappings" ) ) {
				g = new MappingsGenerator( fFilespec, fOutput );
			} else if( fLanguage.equalsIgnoreCase( "stats" ) ) {
				g = new StatsGenerator( fFilespec, fOutput );
			}
			else {
				// The default behavior is to output java code files
				g = new JavaGenerator( fFilespec, fOutput );
			}
			g.generate(dbs);
			
		} catch( Exception e ) {
			System.out.println(e);
		}
	}

	/**
	 *  Populates the list of definition files by searching a directory for all
	 *  files ending with a .xml extension
	 *
	 *  @param dir The directory to search
	 *  @return The number of files found in the directory
	 */
	protected int getFileList( String dir )
		throws IOException
	{
		File cwd = new File(dir);
		StringBuffer fs = new StringBuffer(dir);
		if( !dir.endsWith( File.separator ) )
			fs.append( File.separator );
		fs.append("*.xml");

		System.out.println("Reading file list: "+fs.toString());
		File[] files = cwd.listFiles(this);
		if( files != null ) {
			for( int i = 0; i < files.length; i++ )
				fFileList.addElement( new DefinitionFile(files[i]) );
		}
		
		System.out.println("Returned : " + files.length + " files...." );
		
		return fFileList.size();
	}

	public boolean accept( File f, String n )
	{
		return n.endsWith(".xml");
	}

	/**
	 *  Utility method to populates the list of input .xml files by reading
	 *  filenames from a text file. This method is only called when the user
	 *  specifies the "@file.txt" option on the command-line.
	 *
	 *  @param file The file that lists .xml files to process
	 *  @return The number of files listed in the text file
	 */
	protected int readFileList( String file )
	{
		BufferedReader in = null;

		try
		{
			System.out.println("Reading file list: "+file);

			fFileList = new Vector<DefinitionFile>();
			in = new BufferedReader( new FileReader(file) );
			while( in.ready() )
				fFileList.addElement( new DefinitionFile(in.readLine()) );

			return fFileList.size();
		}
		catch( Exception e ) {
			System.out.println("Error reading file '"+file+"': "+e);
			System.exit(-1);
		}
		finally {
			if( in != null ) {
				try {
					in.close();
				} catch( Exception ignored ) {
				}
			}
		}

		return 0;
	}

	public static String versionStr( SIFVersion v )
	{
		StringBuffer b = new StringBuffer("SIF");

		b.append( String.valueOf( v.getMajor() ) );
		b.append( String.valueOf( v.getMinor() ) );
		if( v.getRevision() != 0 ) {
			b.append( 'r' );
			b.append( String.valueOf( v.getRevision() ) );
		}

		return b.toString();
	}

	/**
	 *  Gets the base package all other packages are relative to
	 *  @return A package name (defaults to "com.edustructures.sifworks")
	 */
	public static String getPackage()
	{
		return self.fPackage;
	}

	/**
	 *  Gets the DB object associated with the specified version of SIF (or
	 *  creates a new object if none currently exists).
	 *  @param version The SIF version (e.g. "1.0r1")
	 *  @param namespace The namespace defined for that version of SIF (e.g.
	 *      "http://www.sifinfo.org/v1.0r2/messages")
	 */
	public static DB getDB( SIFVersion version, String namespace )
	{
		DB db = self.fDBs.get(version.toString());
		if( db == null ) {
			db = new DB(version,namespace);
			self.fDBs.put(version.toString(),db);
		}
		return db;
	}

	/**
	 *  Displays command-line help
	 */
	protected void printHelp()
	{
		System.out.println("\r\nUsage: adkgen [dir1;dir2;etc. | @files] o=output p=package l=[java|cs|jh|zis] locale=[au|uk|us]");
		System.out.println("Example: adkgen ..\\datadef\\sif10r1 o=.. p=com.edustructures.sifworks");
		System.out.println("Example: adkgen ..\\datadef\\sif10r1 o=.. p=com.edustructures.sifworks l=cs");
		System.out.println("Example: adkgen ..\\datadef\\sif10r1;..\\datadef\\sif10r2");
		System.out.println("Example: adkgen @MyFiles.txt o=c:\\adk\\src");
	}

	/**
	 *  Main
	 */
	public static void main( String[] args )
	{
		System.out.println();
		System.out.println("Edustructures SIFWorks ADK");
		System.out.println("Data Object Class Generator");
		System.out.println("Copyright (c) 2002-2007 Edustructures LLC. All Rights Reserved.");
		System.out.println("Version 1.1");
		System.out.println();
		System.out.println("*** For Internal Use Only ***");
		System.out.println();

		try {
			Main gen = new Main(args);
			gen.go();
		} catch( Exception e ) {
			System.out.println(e);
		}
	}
}
