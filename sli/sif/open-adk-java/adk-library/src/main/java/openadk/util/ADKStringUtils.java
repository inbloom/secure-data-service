//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  Various static helper routines for manipulating text strings.
 *
 */
public class ADKStringUtils
{
	/**
	 *  Replaces characters that are illegal in filenames to underscores ("_").<p>
	 *  @param path A string to be used in a file path or file name
	 *  @return The input string with illegal characters converted to an underscore.
	 *      The following characters are replaced: : % / \ ; $ &gt; &lt; * . ? " | ! @
	 */
    public static String safePathString( String path )
	{
		if( path == null )
			return "null";

		StringBuilder b = new StringBuilder();

		int cnt = path.length();
		for( int i = 0; i < cnt; i++ ) {
			char c = path.charAt(i);
			if( c == ':' ||
				c == '%' ||
				c == '/' ||
				c == '\\' ||
				c == ';' ||
				c == '$' ||
				c == '>' ||
				c == '<' ||
				c == '*' ||
				c == '.' ||
				c == '?' ||
				c == '"' ||
				c == '|' ||
				c == '!' ||
				c == '@' )
			{
				b.append('_');
			}
			else
			{
				b.append((char)c);
			}
		}

		return b.toString();
    }
    
	/**
	 *  Escapes an XML string by replacing the characters shown below with their
	 *  equivalent entity references as defined by the XML specification.<p>
	 *
	 *  <table>
	 *      <tr><td>Character</td><td>Entity Reference</td></tr>
	 *      <tr><td>&lt;</td><td>&amp;lt;</td></tr>
	 *      <tr><td>&gt;</td><td>&amp;gt;</td></tr>
	 *      <tr><td>&amp;</td><td>&amp;amp;</td></tr>
	 *      <tr><td>&apos;</td><td>&amp;apos;</td></tr>
	 *      <tr><td>&quot;</td><td>&amp;quot;</td></tr>
	 *  </table>
	 *
	 *  @param str The source string
	 *  @return The escaped string
	 */
	public static String encodeXML( String str )
	{
		if( str == null )
			return null;
	
		StringBuilder b = new StringBuilder();
	
		int cnt = str.length();
		for( int i = 0; i < cnt; i++ )
		{
			char c = str.charAt(i);
	
			switch( c )
			{
				case '<':
					b.append("&lt;"); break;
				case '>':
					b.append("&gt;"); break;
				case '\'':
					b.append("&apos;"); break;
				case '"':
					b.append("&quot;"); break;
				case '&':
					b.append("&amp;"); break;
				default:
					b.append( (char)c );
			}
		}
	
		return b.toString();
	}

	/**
	 *  Unescapes an XML string by replacing the entity references shown below
	 *  with their equivalent characters as defined by the XML specification.<p>
	 *
	 *  <table>
	 *      <tr><td>Character</td><td>Entity Reference</td></tr>
	 *      <tr><td>&lt;</td><td>&amp;lt;</td></tr>
	 *      <tr><td>&gt;</td><td>&amp;gt;</td></tr>
	 *      <tr><td>&amp;</td><td>&amp;amp;</td></tr>
	 *      <tr><td>&apos;</td><td>&amp;apos;</td></tr>
	 *      <tr><td>&quot;</td><td>&amp;quot;</td></tr>
	 *  </table>
	 *
	 *  @param str The source string
	 *  @return The escaped string
	 */
	public static String unencodeXML( String str )
	{
		if( str == null )
			return null;
	
		StringBuilder b = new StringBuilder();
	
		int c = 0;
		while( c < str.length() )
		{
			if( str.charAt(c) == '&' )
			{
				String entity = ADKStringUtils._entity( str, c );
				if( entity == null )
					b.append( '&' );
				else
				{
					if( entity.equals("lt") )
						b.append('<');
					else
					if( entity.equals("gt") )
						b.append('>');
					else
					if( entity.equals("amp") )
						b.append('&');
					else
					if( entity.equals("apos") )
						b.append("'");
					else
					if( entity.equals("quot") )
						b.append('"');
					else {
						b.append( "&" );
						b.append( entity );
						b.append( ";" );
					}
	
					c += entity.length() + 1;
				}
			}
			else
				b.append( str.charAt(c) );
	
			c++;
		}
	
		return b.toString();
	}

	private static String _entity( String src, int ch )
	{
		int c = ch+1;
	
		while( c < src.length() ) {
			if( src.charAt(c) == '&' )
				return null;
			if( src.charAt(c) == ';' )
				return src.substring(ch+1,c);
			c++;
		}
	
		return null;
	}
	
	public static String getStackTrace( Throwable th ) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter( sw );
		th.printStackTrace( pw );
		pw.close();
		return sw.toString();
	}
}
