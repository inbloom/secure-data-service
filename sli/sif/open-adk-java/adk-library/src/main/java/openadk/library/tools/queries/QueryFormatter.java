//
// Copyright (c)1998-2011 Pearson Education, Inc. or its affiliate(s). 
// All rights reserved.
//

package openadk.library.tools.queries;

import java.util.*;

import openadk.library.*;

/**
 *  The abstract base class for query formatters, which format SIF_Query queries
 *  in another form such as an SQL WHERE clause. The way in which a query is
 *  formatted is determined by the subclass implementation. A subclass must
 *  implement these methods:
 *  <p>
 *
 *  <ul>
 *      <li>getOpenBrace</li>
 *      <li>getCloseBrace</li>
 *      <li>getOperator</li>
 *      <li>renderField</li>
 *      <li>renderValue</li>
 *  </ul>
 *  <p>
 *
 *  @author Edustructures LLC
 *  @version ADK 1.0
 */
public abstract class QueryFormatter
{
	/**
	 *  Constructs a QueryFormatter
	 */
    public QueryFormatter()
	{
    }

	/**
	 *  Builds a query string given a dictionary of field definitions and a Query
	 *  instance. This method evaluates the conditions of that Query to produce
	 *  a textual query string in the format determined by the implementation.<p>
	 *
	 *  The dictionary should contain application-defined field values that map
	 *  to <code>ElementDef</code> key elements. Whenever a SIF element or attribute
	 *  is found in the Query, the corresponding application-defined field is
	 *  used in its place.<p>
	 *
	 *  A special convention allows agents to define an in-line translation table for
	 * 	replacing SIF element/attribute values with values defined in the table. If 
	 * 	a field is expressed in the form "field-name{value1=cons1;value2=cons2;..}",
	 *  the comma-delimited list of values within the curly braces is applied to the
	 *  value of the SIF_Value element in the SIF_Query, such that "value1" is
	 *	represented as "cons1". For example, the acceptable values for 
	 *	LibraryPatronStatus/@SifRefIdType attribute are "StudentPersonal" and 
	 *	"StaffPersonal". If in your application you represent these values as numeric 
	 *	types - say, 1 and 2, respectively - you could create the following in-line
	 *	translation table to instruct the QueryFormatter to substitute 
	 *	"StudentPersonal" with "1" and "StaffPersonal" with "2":
	 *
	 *      "CircRecord.PatronType{StudentPersonal=1;StaffPersonal=2}"
	 *
	 *  @param query An ADK Query object, usually obtained during the processing
	 *      of a SIF_Request by a <i>Publisher</i> message handler
	 *  @param table A dictionary that maps <code>SIFDTD</code> ElementDef constants to
	 *      application-defined field values
	 */
    public String format( Query query, Map table )
	throws QueryFormatterException
	{
    	return format( query, table, true );
	}
    
	public String format( Query query, Map table, boolean explicit )
		throws QueryFormatterException
	{
		StringBuffer str = new StringBuffer();

		ConditionGroup[] grp = query.getConditions();
		for( int c = 0; c < grp.length; c++ )
		{
			str.append( getOpenBrace() );
			evaluateConditionGroup( query, grp[c], str, table, explicit );
			str.append( getCloseBrace() );
			
			if( c != grp.length - 1 )
				str.append( getOperator( query.getRootConditionGroup().getOperator() ) );
		}

		return str.toString();
	}

	protected void evaluateConditionGroup( Query query, ConditionGroup grp, StringBuffer str, Map table, boolean explicit )
		throws QueryFormatterException
	{
		Condition[] conds = grp.getConditions();

		if( conds.length != 0 )
		{
			for( int i = 0; i < conds.length; i++ )
			{
				evaluateCondition( query, conds[i], str, table, explicit );

				if( i != conds.length - 1 )
					str.append( getOperator( grp.getOperator() ) );
			}
		}
		else
		{

			ConditionGroup[] groups = grp.getGroups();

			for( int i = 0; i < groups.length; i++ )
			{
				str.append( getOpenBrace() );
				evaluateConditionGroup( query, groups[i], str, table, explicit );
				str.append( getCloseBrace() );

				if( i != groups.length - 1 )
					str.append( getOperator( grp.getOperator() ) );
			}
		}
	}

	protected void evaluateCondition( Query query, Condition cond, StringBuffer str, Map table, boolean explicit )
		throws QueryFormatterException
	{
		String path = cond.getXPath();
		Object o = table.get( path );
		if( o == null ){
			ElementDef field = cond.getField();
			if( field != null ){
				o = table.get( field );
			}
		}
		
		if( o == null ){
			if( explicit ){
			throw new QueryFormatterException(
					"QueryFormatter was not provided with an application-defined field value for " +
					query.getObjectTag() + "/" + cond.getXPath() +
					"; cannot format the SIF_Query" );
			} else {
				// Render a default operation that will always return true
				// as a placeholder
				str.append( "1=1" );
				return;
			}
		}
			
        if ( o instanceof QueryField ) {
            str.append( ((QueryField) o).render( this, query, cond ) );
        }
        else {
			str.append( renderField( cond.getField(), o ) );
			str.append( getOperator( cond.getOperator() ) );
			str.append( renderValue( cond.getValue(), o ) );
        }
	}

	/**
	 *  Return the text that should be inserted for an opening brace
	 */
	public abstract String getOpenBrace();

	/**
	 *  Return the text that should be inserted for a closing brace
	 */
	public abstract String getCloseBrace();

	/**
	 *  Return the text that should be inserted for the particular comparison operator, such as "Equals"
	 */
	public abstract String getOperator( ComparisonOperators op );

	/**
	 *  Return the text that should be inserted for the particular grouping operator, such as "AND"
	 */
	public abstract String getOperator( GroupOperators op );
	
	/**
	 *  Return the text for a field name
	 *  @param field The field name
	 *  @param def The corresponding field definition from the Map passed to
	 *      the <code>format</code> method
	 *  @return The implementation returns the field name in whatever form is
	 *      appropriate to the implementation, using the supplied <i>def</i>
	 *      Object if necessary to obtain additional field information.
	 */
	public abstract String renderField( ElementDef field, Object def )
		throws QueryFormatterException;

	/**
	 *  Return the text for a field value
	 *  @param field The field value
	 *  @param def The corresponding field definition from the Map passed to
	 *      the <code>format</code> method
	 *  @return The implementation returns the field value in whatever form is
	 *      appropriate to the implementation, using the supplied <i>def</i>
	 *      Object if necessary to obtain additional field information
	 */
	public abstract String renderValue( String value, Object def )
		throws QueryFormatterException;

	/**
	 *  Extracts a field name from a string in the form "field-name{...}"
	 */
	protected String extractFieldName( String def )
	{
		int i = def.indexOf('{');
		if( i == -1 )
			return def;

		return def.substring(0,i);
	}

	/**
	 *  Applies the value substitutions defined for a field as described in the
	 *  class comments. For example, if the source string passed to this method
	 *  has the value "Blue" and the field mapping definition has the value
	 *  "Color{Red=0;Green=1;Blue=2}", a value of "2" will be returned.
	 *  <p>
	 *
	 *  @param src The value to process
	 *  @param def The application-defined field mapping to apply
	 */
	protected String doValueSubstitution( String src, String def )
	{
		int i = def.indexOf( '{' );
		if( i == -1 )
			return src;

		int end = def.indexOf( '}', i+1 );
		if( end == -1 )
			return src;

		String trans = def.substring(i+1,end);
		StringTokenizer tok = new StringTokenizer( trans,";" );
		while( tok.hasMoreTokens() ) {
			String s = tok.nextToken();
			i = s.indexOf('=');
			if( i != -1 ) {
				if( i == 0 )
					return s.substring(1);
				String cmp = s.substring(0,i);
				if( cmp.equals( src ) )
				    return s.substring(i+1);
			}
		}

		return src;
	}
}
