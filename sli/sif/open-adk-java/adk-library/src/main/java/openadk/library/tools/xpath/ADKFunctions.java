//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.xpath;

import openadk.library.ValueBuilder;

/**
 * Contains built-in functions that are automatically registered with
 * {@link openadk.library.tools.xpath.SIFXPathContext}, using the
 * namespace prefix "adk". See the class documentation for
 * {@link openadk.library.tools.xpath.SIFXPathContext} for an example
 * of using these functions.
 * 
 * @author Andrew Elmhorst
 * @version ADK 2.0
 */
public class ADKFunctions {
	/**
	 * Returns the trimmed string 
	 * 
	 * @param str
	 *            The string trim
	 * @return The trimmed string 
	 */
	public static String trim(String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}
	
	/**
	 * Returns the string converted to all Upper case
	 * 
	 * @param str
	 *            The string to convert to upper case, or null
	 * @return The string converted to upper case
	 */
	public static String toUpperCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toUpperCase();
	}

	/**
	 * Returns the string converted to all Lower case
	 * 
	 * @param str The string to convert to lower case, or null
	 * @return The string converted to lower case
	 */
	public static String toLowerCase(String str) {
		if (str == null) {
			return null;
		}
		return str.toLowerCase();
	}

	/**
	 * Returns true if the two strings are equal, ignoring any differences in
	 * case
	 * 
	 * @param str1
	 *            The first string to compare
	 * @param str2
	 *            The second string to compare
	 * @return true if both strings are equal, differing only by the case of the
	 *         letters
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		if (str1 == null) {
			return str2 == null;
		}
		return str1.equalsIgnoreCase(str2);
	}
	
	/**
	 *  padBegin( Source, PadChar, Width )
	 *
	 *  Pads the beginning of the Source string with the specified PadChar 
	 *  character so that the source string is at least Width characters in length. 
	 *  If the Source string is already equal to or greater than Width, 
	 *  no action is taken.
	 *  
	 *  If an exception is thrown while evaluating this method, it will be eaten
	 *  and the source string will be returned
	 * @param source The string to start with
	 * @param padding The string to use for padding (only the first char will be used)
	 * @param width The length the final string should be
	 * @return The padded string or 'Null' if the source is null
	 */
	public static String padBegin( String source, String padding, int width )
	{
		return pad( source, padding, width, true );
	}
	
	/**
	 *  padEnd( Source, PadChar, Width )
	 *
	 *  Pads the end of the Source string with the specified PadChar 
	 *  character so that the source string is at least Width characters in length. 
	 *  If the Source string is already equal to or greater than Width, 
	 *  no action is taken.
	 *  
	 *  If an exception is thrown while evaluating this method, it will be eaten
	 *  and the source string will be returned
	 * @param source The string to start with
	 * @param padding The string to use for padding (only the first char will be used)
	 * @param width The length the final string should be
	 * @return The padded string or 'Null' if the source is null
	 */
	public static String padEnd( String source, String padding, int width )
	{
		return pad( source, padding, width, false );
		
	}
	
	private static String pad( String source, String pad, int width, boolean padBeginning ){
		if( source == null ){
			return null;
		}
		if( source.length() >= width || pad == null ){
			return source;
		}
		
		StringBuilder str = new StringBuilder( width );
		char padChar = pad.charAt( 0 );
		int padLength = width - source.length();
		if( padBeginning ){
			// put the padding on the beginning
			for( int i = 0; i < padLength; i++ ){
				str.append( padChar );
			}
			str.append( source );
		} else { 
			// put the padding on the end
			str.append( source );
			for( int i = 0; i < padLength; i++ ){
				str.append( padChar );
			}
		}
		
		return str.toString();
	}
	
	/**
	 *  toProperCase( Source )
	 *
	 *  Converts the source string to 'Proper' case. In general, this means
	 *  capitalizing the first letter of each word. However, in some cases, such
	 *  as O'Reilly, letters within the word will be capitalized as well.
	 *  
	 *  This function trims the string as well.
	 * @param source The String to ProperCase 
	 * @return The source string, proper-cased, or null if the source isnull
	 */
	public static String toProperCase( String source )
	{
		if( source == null ){
			return null;
		}
		try
		{
			StringBuilder b = new StringBuilder( source.trim() );
			int indexIntoWord = 1;
			for( int i = 0; i < b.length(); i++ )
			{
				char c = b.charAt( i );
				switch( c ){
					case ' ':
					case '\t':
					case '\r':
					case '\n':
						indexIntoWord = 0;
						break;
					case '\'':
						if( indexIntoWord < 3 ){
							indexIntoWord = 0;
						}
						default:
							if( indexIntoWord == 1 ){
								b.setCharAt( i, Character.toUpperCase( c ) );
							} else {
								b.setCharAt( i, Character.toLowerCase( c ) );
							}
				}
				indexIntoWord++;
			}

			return b.toString();
		}
		catch( Throwable thr )
		{
			return source;
		}
	}
	
	/**
	 * Used internally by the ADK
	 * @return 
	 */
	public static boolean x()
	{
		return true;
	}
	

}
