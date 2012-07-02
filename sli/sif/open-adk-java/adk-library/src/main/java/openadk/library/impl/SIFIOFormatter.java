//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.impl;


import java.io.*;
import java.nio.charset.Charset;

import openadk.library.SIFFormatter;

/**
 * Contains functions helpful for converting data to and from native datatypes to SIF datatypes.
 *
 * This class is internal in this release. It may be helpful to make it public and move to the parent
 * package in a future release. At that point, we might define it differently, due to enhanced support in
 * java 1.4 for getting access to encoders
 * @author Andy Elmhorst
 * @version 1.5
 */
public class SIFIOFormatter {

	/**
	 *  The name of the UTF8 Java Charset used by this formatter
	 */
	public static final String CHARSET_UTF8 = "UTF8";

	/**
	 * The HTTP ContentType supported by SIF ( <code>application/xml;charset="utf-8"</code> )
	 */
	public static final String CONTENT_TYPE = "application/xml;charset=\"utf-8\"";
	public static final String CONTENT_TYPE_BASE = "application/xml";
	public static final String CONTENT_TYPE_UTF8 = "utf-8";
	

	/*
	 * Creates a writer using the charset decoder that will encode characters
	 * as per the SIF Specification ( UTF-8 ).
	 * @param	out	An output stream that will be wrapped by a writer with the proper encoder set
	 * @return		a BufferedWriter with the proper encoding set
	 */
	public static BufferedWriter createOutputWriter( OutputStream out )
	{
		return new BufferedWriter( new OutputStreamWriter( out, CHARSET ) );
	}

	/*
	 * Creates a writer using the charset decoder that will decode characters
	 * as per the SIF Specification ( UTF-8 ).
	 * @param 	in	An InputStream that will be wrapped by a reader with the proper encoding set
	 * @return		A BufferedReader with the proper encoding set
	 */
	public static BufferedReader createInputReader( InputStream in )
	{
		return new BufferedReader( new InputStreamReader( in, CHARSET ));
	}

	/**
	 * The Charset to use for encoding SIF XML ("UTF8")
	 */
	public static Charset CHARSET = Charset.forName( CHARSET_UTF8 );



}
