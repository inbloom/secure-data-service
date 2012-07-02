//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library;

/**
 *  Classes that implement the ValueBuilder interface evaluate an arbitrary
 *  expression to produce a string value. The string value is then assigned as
 *  the value to a SIF element or attribute.<p>
 *
 *  The ValueBuilder interface is used by the SIFDTD, SIFDataObject, and Mappings
 *  classes when evaluating XPath-like query strings. It enables developers to
 *  customize the way the ADK evaluates value expressions in these query strings
 *  to produce a value for a SIF element or attribute. The DefaultValueBuilder
 *  implementation supports <code>$(variable)</code> token replacement as well as
 *  <code>@com.class.method</code> style calls to static Java methods.
 *  <p>
 *
 *  @author Eric Petersen
 *  @version ADK 1.0
 */
public interface ValueBuilder
{
	/**
	 *  Evaluate an expression to return a String value.<p>
	 *
	 *  @param expression The expression to evaluate
	 *  @param map A Map comprised of name/value pairs that may be referenced by
	 *      the expression for token replacement
	 *  @return The value that results from the expression
	 */
	public String evaluate( String expression );
}
