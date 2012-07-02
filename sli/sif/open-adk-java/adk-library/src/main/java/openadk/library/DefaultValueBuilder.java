//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

import java.lang.reflect.*;
import java.util.*;

import openadk.library.tools.mapping.FieldAdaptor;


/**
 *  The default ValueBuilder implementation evaluates an expression to produce
 *  a string value.<p>
 *
 *  The ValueBuilder interface is used by the SIFDTD, SIFDataObject, and Mappings
 *  classes when evaluating XPath-like query strings. It enables developers to
 *  customize the way the ADK evaluates value expressions in these query strings
 *  to produce a value for a SIF element or attribute. The DefaultValueBuilder
 *  implementation supports <code>$(variable)</code> token replacement as well as
 *  <code>@com.class.method</code> style calls to static Java methods.
 *  <p>
 *
 *  <b>Token Replacement</b><p>
 *
 *  When a <code>$(variable)</code> token is found in an expression, it is
 *  replaced with a value from the Map passed to <code>evaluate</code>. For
 *  example, if the Map constains the entry "color=blue", calling the <code>evaluate</code>
 *  method with the expression "The color is $(color)" would produce the
 *  string "The color is blue".
 *  <p>
 *
 *  <b>Java Method Calls</b><p>
 *
 *  When a <code>@method( arg1, arg2, ... )</code> call is found in an expression,
 *  the static Java method is called and its return value inserted into the value
 *  string. Token replacement is performed before calling the method.
 *  If <code>method</code> is not fully-qualified, it is assumed to be a method
 *  declared by this DefaultValueBuilder class. The default class can be changed
 *  by calling the <code>setDefaultClass</code> method. When writing your own
 *  static method, the first parameter must be of type ValueBuilder; zero or
 *  more String parameters may follow. The function must return a String:
 *  <code>String method( ValueBuilder vb, String p1, String p2, ... )</code>.
 *  <p>
 *
 *  In the following example, the toUpperCase static method is called to convert
 *  the $(color) variable to uppercase. This expression would yield the result
 *  "The color is BLUE":
 *
 *  <code>The color is @com.edustructures.sifworks.DefaultValueBuilder.toUpperCase( $(color) )</code>
 *
 *  The following static methods are defined by this class:
 *
 *  <table>
 *      <tr>
 *          <td><b>Method</b></td>
 *          <td><b>Description</b></td>
 *      </tr>
 *      <tr>
 *          <td><code>pad( source, padding, width )</code></td>
 *          <td>
 *              Pads the <i>source</i> string with <i>padding</i> such that the
 *              resulting string is <i>width</i> characters in length.
 *          </td>
 *      </tr>
 *      <tr>
 *          <td><code>toUpperCase( source )</code></td>
 *          <td>
 *              Converts the <i>source</i> string to uppercase
 *          </td>
 *      </tr>
 *      <tr>
 *          <td><code>toLowerCase( source )</code></td>
 *          <td>
 *              Converts the <i>source</i> string to lowercase
 *          </td>
 *      </tr>
 *      <tr>
 *          <td><code>toMixedCase( source )</code></td>
 *          <td>
 *              Converts the <i>source</i> string to mixed case
 *          </td>
 *      </tr>
 *  </table>
 *
 *
 */
public class DefaultValueBuilder implements ValueBuilder
{
	private static String sDefClass = "openadk.library.DefaultValueBuilder";
	protected static Hashtable sAliases = new Hashtable();
	protected FieldAdaptor fVars;
	protected SIFFormatter fFormatter;

	/**
	 * Creates an instance of DefaultValueBuilder that builds values based
	 * on the SIFDataMap, using the ADK's default text formatter 
	 * @param data
	 */
	public DefaultValueBuilder( FieldAdaptor data )
	{
		this( data, ADK.getTextFormatter() );
	}
	
	/**
	 * Creates an instance of DefaultValueBuilder that builds values based
	 * on the SIFDataMap, using the specified <code>SIFFormatter</code> instance
	 * @param data
	 * @param formatter
	 */
	public DefaultValueBuilder( FieldAdaptor data, SIFFormatter formatter )
	{
		fVars = data;
		fFormatter = formatter;
	}

	/**
	 *  Returns the MappingsAdaptor
	 *  @return The MappingsAdaptor passed to the constructor
	 */
	public FieldAdaptor getData()
	{
		return fVars;
	}

	/**
	 *  Evaluate an expression that the implementation of this interface
	 *  understands to return a String value.<p>
	 *
	 *  @param expression The expression to evaluate
	 *  @return The value built from the expression
	 */
	public String evaluate( String expression )
	{
		return java( replaceTokens( expression, fVars, fFormatter ) );
	}

	/**
	 *  Calls all Java methods referenced in the source string to replace the
	 *  method reference with the string representation of the method's return
	 *  value
	 */
	public String java( String src )
	{
		if( src == null )
			return null;

		StringBuffer b = new StringBuffer();

		int len = src.length();
		int at = 0;
		int mark = 0;
		int x = 0;

		do
		{
			at = src.indexOf("@",at);
			if( at == -1 ) {
				b.append( src.substring(mark) );
	    		at = len;
			}

			if( at < len )
			{
				ParseResults mm = ParseResults.parse( src, at );
				if( mm != null )
				{
					b.append( src.substring(mark,at) );
					
					mark = mm.Position;
						
					String method = mm.MethodName;
					MyStringTokenizer params = mm.Parameters;
					int paramCount = params.countTokens();
					int methodParamCount = 0;

					Class targetClass = null;

					try
					{
    					x = method.lastIndexOf('.');
	    				if( x != -1 )
						{
							//  Use the fully-qualified Java method
		    				targetClass = Class.forName( method.substring(0,x) );
							method = method.substring(x+1);
						}
						else
						{
							//  Was an alias registered?
							String aliasClass = (String)sAliases.get( method );
							if( aliasClass != null )
								targetClass = Class.forName( aliasClass );
						}

						if( targetClass == null )
						{
							//  Use the default class
							targetClass = Class.forName( sDefClass );
						}
					}
					catch( ClassNotFoundException cnfe ) {
						throw new RuntimeException( "Class not found: " + method );
					}

					Method targetMethod = null;
					Method[] methods = targetClass.getMethods();
					for( int m = 0; m < methods.length; m++ ) {
						if( methods[m].getName().equals(method) ) {
							targetMethod = methods[m];
							methodParamCount = methods[m].getParameterTypes().length;
							break;
						}
					}

					if( targetMethod == null )
						throw new RuntimeException( "Java method not found: " + method );

					x = 1;
					Object[] args = new Object[ methodParamCount ];
					args[0] = this;
					
					//	Assign parameters
					while( x < params.countTokens() + 1 && x < methodParamCount ) {
						args[x] = params.getToken( x-1 );
						x++;
					}
						
					//	Fill in any remaining parameters with a blank string value
					while( x < methodParamCount ) {
						args[x++] = "";
					}

					at = mark;

					try {
						Object result = targetMethod.invoke( null, args );
						if( result != null )
							b.append( result.toString() );
					} catch( Exception ex ) {
						throw new RuntimeException( "Failed to call Java method '" + method + "': " + ex.toString() );
					}

					continue;
				}

				b.append( src.substring(mark,len) );
				at = len;
			}
		}
		while( at < len );

		return b.toString();
	}

	/**
	 *  Replaces all <code>$(variable)</code> tokens in the source string with
	 *  the corresponding entry in the supplied Map<p>
	 *  @param src The source string
	 *  @param adaptor A set of data values
	 *  @param formatter The <code>SIFFormatter</code> to use for creating String representations
	 *  of SIF data.
	 */
	public static String replaceTokens( String src, FieldAdaptor adaptor, SIFFormatter formatter )
	{
		if( src == null )
			return null;

		StringBuffer b = new StringBuffer();

		int len = src.length();
		int at = 0;
		int mark = 0;

		do
		{
			at = src.indexOf("$(",at);
			if( at == -1 )
	    		at = len;
			b.append( src.substring(mark,at) );

			if( at < len )
			{
				int i = src.indexOf(")",at+2);
				if( i != -1 )
				{
					mark = i+1;
					String key = src.substring(at+2,i);
					at = mark;
					
					Object val = adaptor.getValue( key );
					if( val != null ){
						b.append( val );
					}
					
				}
				else
				{
					b.append( src.substring(mark,len) );
					at = len;
				}
			}
		}
		while( at < len );

		return b.toString();
	}

////////////////////////////////////////////////////////////////////////////////

	/**
	 *  "@pad( Source, PadChar, Width )"
	 *
	 *  Pads the Source string with the specified PadChar character so that the
	 *  source string is at least Width characters in length. If the Source
	 *  string is already equal to or greater than Width, no action is taken.
	 */
	public static String pad( ValueBuilder vb, String source, String padding, String width )
	{
		try
		{
			String _source = source.trim();
			int _width = Integer.parseInt( width.toString().trim() );
			if( _source.length() >= _width )
				return _source;

			String _padding = padding.toString().trim();

			StringBuffer b = new StringBuffer();
			for( int i = _source.length(); i < _width; i++ )
				b.append( _padding );
			b.append( _source );

			return b.toString();
		}
		catch( Throwable thr )
		{
			return source.trim();
		}
	}

	/**
	 *  "@toUpperCase( Source )"
	 *
	 *  Converts the source string to uppercase
	 */
	public static String toUpperCase( ValueBuilder vb, String source )
	{
		try
		{
			return source.trim().toUpperCase();
		}
		catch( Throwable thr )
		{
			return source;
		}
	}

	/**
	 *  "@toLowerCase( Source )"
	 *
	 *  Converts the source string to lowercase
	 */
	public static String toLowerCase( ValueBuilder vb, String source )
	{
		try
		{
			return source.trim().toLowerCase();
		}
		catch( Throwable thr )
		{
			return source;
		}
	}

	/**
	 *  "@toMixedCase( Source )"
	 *
	 *  Converts the source string to mixed case
	 */
	public static String toMixedCase( ValueBuilder vb, String source )
	{
		try
		{
			StringBuffer b = new StringBuffer();
		    String _source = source.trim();
			for( int i = 0; i < _source.length(); i++ )
			{
				if( i == 0 || _source.charAt(i-1) == ' ' )
					b.append( Character.toUpperCase( _source.charAt(i) ) );
				else
					b.append( Character.toLowerCase( _source.charAt(i) ) );
			}

			return b.toString();
		}
		catch( Throwable thr )
		{
			return source;
		}
	}

	/**
	 *  Specifies the default class for Java method calls that do not reference
	 *  a fully-qualified class name. <code>com.edustructures.sifworks.DefaultValueBuilder</code>
	 *  is used as the default unless this method is called to change it.
	 *  <p>
	 *
	 *  @param clazz The name of a class (e.g. "openadk.library.DefaultValueBuilder")
	 */
	public static void setDefaultClass( String clazz )
	{
		sDefClass = clazz;
	}

	/**
	 *  Registers an alias to a static Java method.<p>
	 *  @param alias The alias name (e.g. "doSomething")
	 *  @param method The fully-qualified Java method name (e.g. "com.mycompany.MyValueBuilder.doSomething")
	 */
	public static void addAlias( String alias, String method )
	{
		int i = method.lastIndexOf(".");
		if( i != -1 ) {
			sAliases.put( alias, method.substring(0,i) );
		} else {
			sAliases.put( alias, method );
		}
	}
	
	/**
	 * 	A java.util.StringTokenizer replacement. We need a replacement for two
	 * 	reasons: first, Java's default does not consider empty tokens to be 
	 * 	tokens. For example, ",,,blue" is considered to have one token, not 4.
	 * 	Second, we need to ignore commas in literal strings so that a parameter
	 * 	to a method can itself be comprised of delimiters. If a double-quote is
	 * 	found, all commas until the next double-quote are considered literal.
	 * 	The commas are not included in the resulting tokens.
	 */
	static class MyStringTokenizer 
	{
		private String[] fTokens;
		
		public MyStringTokenizer( String src, char delimiter )
		{
			//	Parse the source string into an array of tokens
			Vector v = new Vector();
			if( src != null ) 
			{
				int i = 0;
				boolean inQuote = false;
				StringBuffer token = new StringBuffer();
				
				while( i < src.length() ) 
				{
					if( src.charAt(i) == '"' )
					{
						inQuote = !inQuote;
					}
					else	
					if( src.charAt(i) == delimiter ) 
					{
						if( inQuote )
							token.append( delimiter );
						else {
							v.add( token.toString() );
							token.setLength(0);
						}
					}
					else
						token.append( src.charAt(i) );
						
					i++;
				}
				
				v.add( token.toString() );
			}
			
			fTokens = new String[ v.size() ];
			v.copyInto( fTokens );
		}	
		
		public int countTokens()
		{
			return fTokens.length;
		}
		
		public String getToken( int i )
		{
			return fTokens[i];
		}
	}
	
	/**
	 * 	A helper class to parse "@method( parameterlist )" into Java method name
	 * 	and parameter list, and to return the position in the source string where
	 * 	the caller should continue processing.  
	 */
	static class ParseResults
	{
		// Position in source string immediately after closing parenthesis
		public int Position; 
		
		// The name of the Java method
		public String MethodName;
		
		// The parameters to the Java method
		public MyStringTokenizer Parameters;
		
		/**
		 * 	Given a source string and an index into that string where a Java
		 * 	method begins (i.e. the location of the @ character), parse the name
		 * 	of the Java method and the list of parameters. Return a new ParseResults
		 * 	instance of both components were found, otherwise return null.<p>
		 * 
		 * 	For example, if this string were passed: '@random() @strip("(801) 323-1131")',
		 * 	and the <i>position</i> parameter were 11, this function would return
		 * 	a new ParseResults with Position set to 32, MethodName set to 'strip',
		 * 	and Parameters having a single parameter of "(801) 323-1131".
		 */
		public static ParseResults parse( String src, int position )
		{
			ParseResults results = new ParseResults();
			
			int i = position + 1;
			boolean inQuote = false;
			StringBuffer buf = new StringBuffer();
				
			while( i < src.length() ) 
			{
				if( src.charAt(i) == '"' )
				{
					inQuote = !inQuote;
				}
				else	
				if( src.charAt(i) == '(' ) 
				{
					if( !inQuote ) {
						results.MethodName = src.substring(position+1,i);
						buf.setLength(0);
					} else {
						buf.append( '(' );
					}
				}
				else
				if( src.charAt(i) == ')' )
				{
					if( !inQuote ) {
						results.Parameters = new MyStringTokenizer( buf.toString(), ',' );
						results.Position = i + 1;
						return results;
					} else {
						buf.append( ')' );
					}
				}
				else
					buf.append( src.charAt(i) );
						
				i++;
			}
				
			return null;
		}		
			
	}
		
}
