//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import openadk.library.impl.SIFPullParser;


/**
 *  Parses a SIF message or Data Object into a SIFElement object graph.<p>
 *
 *  A single instance of SIFParser can be reused by a thread. However, the parse
 *  method is synchronized for serial access. If multiple threads wish to parse
 *  messages concurrently a unique SIFParser instance should be allocated to
 *  each.<p>
 *
 *  @author Eric Petersen
 *  @version 1.0
 */
public abstract class SIFParser
{
	
	/**
	 *  Flag that indicates that SIFParser should expect a nested SIF_Message 
	 */
	public static final int FLG_EXPECT_INNER_ENVELOPE = 0x00000001;
	
		
	/**
	 *  Factory method for creatin a new instance of a SIFParser
	 * @return A SIFParser that can be used for parsing SIF XML
	 * @throws ADKException If a SIFParser class cannot be instantiated
	 */
	public static SIFParser newInstance()
		throws ADKException
	{
		String cls = System.getProperty("adkglobal.factory.SIFParser");
		if( cls == null || cls.length() == 0 ){
			return new SIFPullParser();
		}
		
		try
		{
			return (SIFParser)Class.forName(cls).newInstance();
		}
		catch( Throwable thr )
		{
			throw new ADKException("ADK could not create an instance of the class "+cls+": "+thr,null);
		}
	}

	/**
	 * Parses Xml text into a SIFElement
	 * @param msg The content to parse
	 * @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( String msg )
		throws ADKParsingException,
			SIFException,
			IOException;
	

	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( String msg, Zone zone )
		throws ADKParsingException,
		       SIFException,
		       IOException;
	

	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *  @param flags One or more <code>FLG_</code> constants, or zero if no
	 *      flags are applicable
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( String msg, Zone zone, int flags )
		throws ADKParsingException,
		       SIFException,
		       IOException;
	

	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *  @param flags One or more <code>FLG_</code> constants, or zero if no
	 *      flags are applicable
	 *  @param version The version of SIF that will be associated with the
	 *      returned object. By default, SIFParser uses the default version of
	 *      SIF in effect for the agent when parsing messages that do not have
	 *      a SIF_Message envelope. By specifying a value to this parameter, you
	 *      can change the version of SIF associated with the returned object in
	 *      the event there is no SIF_Message envelope present in the XML
	 *      content. Note that when parsing XML content with a SIF_Message
	 *      envelope, SIFParser ignores this parameter and instead uses the
	 *      version indicated by the <i>Version</i> and <i>xmlns</i> attributes
	 *
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( String msg, Zone zone, int flags, SIFVersion version )
		throws ADKParsingException,
		       SIFException,
		       IOException;
	

	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( Reader msg, Zone zone )
		throws ADKParsingException,
			   SIFException,
		       IOException;
	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *  @param flags One or more <code>FLG_</code> constants, or zero if no
	 *      flags are applicable
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( Reader msg, Zone zone, int flags )
		throws ADKParsingException,
			   SIFException,
		       IOException;
	/**
	 *  Parses a SIF data element into a <code>SIFElement</code>.
	 *
	 *  @param msg The content to parse
	 *  @param zone The Zone from which the message was received, or null if
	 *      not applicable or not known
	 *  @param flags One or more <code>FLG_</code> constants, or zero if no
	 *      flags are applicable
	 *  @param version The version of SIF that will be associated with the
	 *      returned object. By default, SIFParser uses the default version of
	 *      SIF in effect for the agent when parsing messages that do not have
	 *      a SIF_Message envelope. By specifying a value to this parameter, you
	 *      can change the version of SIF associated with the returned object in
	 *      the event there is no SIF_Message envelope present in the XML
	 *      content. Note that when parsing XML content with a SIF_Message
	 *      envelope, SIFParser ignores this parameter and instead uses the
	 *      version indicated by the <i>Version</i> and <i>xmlns</i> attributes
	 *
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object)
	 *
	 * @throws ADKParsingException is thrown if unable to parse the message
	 * @throws SIFException is thrown if unable to parse the message
	 * @throws IOException is thrown if an error is reported while reading the message content
	 */
	public abstract SIFElement parse( Reader msg, Zone zone, int flags, SIFVersion version  )
		throws ADKParsingException,
			   SIFException,
		       IOException;
	/**
	 *  Gets the last SIFElement parsed
	 *  @return A SIFElement object encapsulating the message payload (e.g.
	 *      a com.edustructures.sifworks.student.StudentPersonal object). Note
	 *      if the parse method was unsuccessful, the element may be incomplete.
	 */
	public abstract SIFElement getParsed();

	
	/**
	 * 	Run SIFParser as a command-line program. This may be useful to Customer Support
	 * 	staff in determining if an arbitrary SIF XML document is invalid from the ADK's
	 * 	perspective.<p>
	 * 
	 * 	Usage: SIFParser [/v n.n] file<p>
	 * 
	 * 	Use the <code>/v</code> option to specify a version of SIF (e.g. "/v 1.5r1").
	 * 	Defaults to the latest version of SIF supported by the ADK. 
	 * @param args Command-Line arguments ([/v n.n] file)
	 */
	public static void main( String[] args )
	{
		try
		{
			SIFVersion ver = SIFVersion.LATEST;
			String file = null;
			
			for( int i = 0; i < args.length; i++ ) {
				if( args[i].charAt(0) == '-' && args[i].length() > 1 ) {
					switch( Character.toLowerCase(args[i].charAt(1)) ) {
						case 'v':
						case 'V': {
							if( i+1 != args.length ) {
								ver = SIFVersion.parse(args[++i]);
							}
						}
						break;
					
						default: {
							file = args[i];
						}
					}
				} else {
					file = args[i];
				}
			}
			
			if( file == null ) {
				System.out.println("Usage: SIFParser [/v n.n] file" );
				System.out.println("       /v - The version of SIF to use to parse the file (defaults to " + SIFVersion.LATEST + ")" );
				System.out.println("       file - The file to parse");
				return;
			}

			ADK.initialize( ver, SIFDTD.SDO_ALL );
			System.out.println("Using ADK: " + ADK.getADKVersion() );
			System.out.println("Using SIF: " + ADK.getSIFVersion() );
			System.out.println("Parsing: " + file + "\r\n" );
			
			SIFParser p = SIFParser.newInstance();
			BufferedReader in = new BufferedReader( new FileReader(file) );
			p.parse(in,null);
			in.close();
		}
		catch( Exception ex )
		{
			System.out.println(ex);
		}
	}
}
